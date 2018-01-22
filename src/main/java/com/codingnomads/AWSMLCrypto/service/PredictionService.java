package com.codingnomads.AWSMLCrypto.service;

import com.amazonaws.services.machinelearning.AbstractAmazonMachineLearning;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.*;
import com.codingnomads.AWSMLCrypto.mapper.AnalyzeMapper;
import com.codingnomads.AWSMLCrypto.mapper.TableMapper;
import com.codingnomads.AWSMLCrypto.model.AnalyzeResult;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.abs;

/**
 * created by Meghan Boyce on 12/22/17
 * This class contains service methods to make real time predictions using AWS machine learning API.
 */

@Service
public class PredictionService extends AbstractAmazonMachineLearning {

    @Autowired
    TableMapper mapper;

    @Autowired
    AnalyzeMapper analyzeMapper;

    private String modelId = null;
    private String modelName = null;
    private String endptUrl = null;
    private AmazonMachineLearningClient awsMlClient = new AmazonMachineLearningClient();



    /**
     * Method that calls the AWS Real Time Prediction API. Params are optional.
     * @param createModel User defines as true to create a new model, false to use existing. Default = false.
     * @param modelId   User defined or auto-generated if not provided
     * @param modelName User defined or auto-generated if not provided
     * @return PredictCustomPojo Custom prediction result pojo
     */
    public PredictCustomPojo getRealTimePrediction(boolean createModel, String modelId, String modelName) {
        // Create a new AWS prediction request
        PredictRequest predictRequest = new PredictRequest();

        if (createModel == true) {
            // Programmatically create a new model. Optional: provide id and name.
            // todo make it possible to pass specific model parameters
            CreateMLModelResult createMLModelResult = createAwsMlModel(modelId, modelName);   // returns only modelId, not entire model object
            modelId = createMLModelResult.getMLModelId();
        }

        // Build a model request
        GetMLModelRequest modelRequest = new GetMLModelRequest();
        modelRequest.setVerbose(false);
        // If using existing model, modelId must have been passed as query param    // todo add a check that says if createModel == false, then modelId must not be null.
        modelRequest.setMLModelId(modelId);

        // Get model object
        GetMLModelResult model = getModel(modelRequest);
        // Get model status
        String modelStatus = getModelStatus(model);

        // Perform actions based on model status
        if ((modelStatus.equalsIgnoreCase("DELETED")) || (modelStatus.equalsIgnoreCase("FAILED"))) {
            System.out.println("Model status is " + modelStatus + ". It can't be used.");
        }

        if ((modelStatus.equalsIgnoreCase("PENDING")) || (modelStatus.equalsIgnoreCase("INPROGRESS"))) {
            // Check model status every 30 seconds until it is completed. This takes a few minutes.
            while (!modelStatus.equalsIgnoreCase("COMPLETED")) {
                try {
                    System.out.println("Sleeping thread for 30 seconds while AWS creates the model...");
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.toString();
                }
                // Refresh model status
                model = getModel(modelRequest);
                modelStatus = getModelStatus(model);
                if (modelStatus.equalsIgnoreCase("COMPLETED")){
                    System.out.println("Model status is " + modelStatus);
                } else {
                    System.out.println("Model status is " + modelStatus + ". Checking again...");
                }
            }
        }

        if (modelStatus.equalsIgnoreCase("COMPLETED")) {
            // Set the model Id for real time prediction request
            predictRequest.setMLModelId(modelId);
            // Get the real time endpoint status
            String endptStatus = getEndpointStatus(model);

            // Case where endpoint does not exist
            if (endptStatus.equalsIgnoreCase("none")) {
                System.out.println("Creating a real time prediction endpoint.. ");
                // Build request to create endpoint
                CreateRealtimeEndpointRequest createEndptRequest = new CreateRealtimeEndpointRequest();
                createEndptRequest.setMLModelId(modelId);
                // Create endpoint via client
                CreateRealtimeEndpointResult endptResult = awsMlClient.createRealtimeEndpoint(createEndptRequest);
                // Check endpoint status from endptResult to ensure it was created
                endptStatus = endptResult.getRealtimeEndpointInfo().getEndpointStatus();
                // Case where endpoint was not created successfully
                if (endptStatus.equalsIgnoreCase("none")) {
                    System.out.println("Something went wrong, endpoint was not created for model " + modelId + ".");
                }
                // Case where endpoint was created successfully
                else {
                    // Refresh model
                    model = getModel(modelRequest);
                    // Get the new endpoint URL
                    endptUrl = model.getEndpointInfo().getEndpointUrl();
                    // Set endpoint url for real time prediction request
                    predictRequest.setPredictEndpoint(endptUrl);
                }
            }
            // Case where endpoint exists but is not ready for use.
            if (endptStatus.equalsIgnoreCase("updating")) {
                // Continuously check endpoint status every 10 seconds until it is ready
                while (!endptStatus.equalsIgnoreCase("ready")) {
                    try {
                        System.out.println("Sleeping thread for 10 seconds...");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.toString();
                    }
                    // Refresh model
                    model = getModel(modelRequest);
                    // Get endpoint status
                    endptStatus = getEndpointStatus(model);
                    if (endptStatus.equalsIgnoreCase("ready")){
                        System.out.println("Endpoint status is " + endptStatus + "! You can now make real time predictions");
                        // One more 5 sec sleep for safety
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.toString();
                        }
                    } else {
                        System.out.println("Endpoint status is " + endptStatus + ". Checking again...");
                    }
                }
            }
            // Case where endpoint exists and is ready
            if (endptStatus.equalsIgnoreCase("ready")){
                // Get existing endpoint url
                endptUrl = model.getEndpointInfo().getEndpointUrl();
                // Set existing endpoint url for real time prediction request
                predictRequest.setPredictEndpoint(endptUrl);
            }
        }

        // Get time for most recent data point
        int latestTime = mapper.selectLatestTime();
        // Add 1 hour for prediction time - commenting out while we are 1 hr behind
        int predictionTimeInt = mapper.selectLatestTime() + 3600;
        // Select previous hour's closevalue
        String recordOpenValue = String.valueOf(mapper.selectCloseValueFromLatestEntry());
        // Set record entries for real time prediction request
        predictRequest
                // Time is one hour after the latest time with actual value available
                .addRecordEntry("time", String.valueOf(predictionTimeInt))
                // Open value is the previous hour's close value
                .addRecordEntry("openvalue", recordOpenValue);

        // Send prediction request via client & get the results.
        PredictResult predictResult = awsMlClient.predict(predictRequest);

        // Use custom results POJO & extract desired data points from predictResult
        PredictCustomPojo predictPojo = new PredictCustomPojo();
            // Date & time the prediction request was made
            predictPojo.setRequestDate(predictResult.getSdkHttpMetadata().getHttpHeaders().get("Date"));
            // AWS provided request id
            predictPojo.setAmznRequestId(predictResult.getSdkResponseMetadata().getRequestId());
            // Numeric prediction (high value) result by the AWS ML API
            predictPojo.setHighValuePredict(predictResult.getPrediction().getPredictedValue());
            // Cryptocurrency coin id
            predictPojo.setCoinId(mapper.getCoinId(latestTime));
            // Prediction time (future)
            predictPojo.setTime(predictionTimeInt);
            // Type of prediction model used (ex: REGRESSION, BINARY, MULTICLASS)
            predictPojo.setModelTypeId(mapper.getModelTypeId(predictResult.getPrediction().getDetails().get("PredictiveModelType")));
            // Id of the user's AWS Model being used to make the prediction
            predictPojo.setAwsMLModelId(predictRequest.getMLModelId());

        // Insert results in table
        insertPredictResult(predictPojo);
        return predictPojo;
    }

    /**
     * Method that inserts prediction data in db
     *
     * @param result
     */
    public void insertPredictResult(PredictCustomPojo result) {
        mapper.insertPredictData(result);
    }


    /**
     * Method to determine the accuracy of real time predictions for hourly "highvalue" by comparing the predicted
     * highvalue to the actual highvalue once it is available.
     * @return double - percent error (accuracy) of hourly prediction
     */
    public AnalyzeResult analyzePrediction() {
        // Get time for most recent prediction
        Integer predictHour = mapper.selectLatestPredictionTime();

        // Get actual high value for this time from datatable
        double actualValue = mapper.selectHighValueActual(predictHour);

        // Update/set actual value in prediction table for record w/currentHour
        mapper.updateHighValueActual(actualValue, predictHour);

        // Select highvaluepredict for currentHour
        double predictValue = mapper.selectHighValuePredict(predictHour);

        // Calculate (absolute value) %error between highvalue predict & actual
        double pctError;
        if (actualValue == 0.0) {
            // To avoid divide by zero, get the absolute error if actualValue happens to be 0.
            pctError = abs(actualValue - predictValue);
        } else {
            pctError = (abs(actualValue - predictValue) / actualValue) * 100.0;
        }
        // Insert pctError into predictions table
        mapper.updatePcterror(pctError, predictHour);

        // Build AnalyzeResult object for nice JSON response display
        AnalyzeResult analyzeResult = new AnalyzeResult();
        analyzeResult.setId(analyzeMapper.getId(predictHour));
        analyzeResult.setRequestDate(analyzeMapper.getRequestDate(predictHour));
        analyzeResult.setAmznRequestId(analyzeMapper.getAmazonRequestId(predictHour));
        analyzeResult.setHighValuePredict(predictValue);
        analyzeResult.setCoinId(analyzeMapper.getCoinId(predictHour));
        analyzeResult.setUnixTime(predictHour);
        analyzeResult.setHighValueActual(actualValue);
        analyzeResult.setPctError(pctError);
        analyzeResult.setModelTypeId(analyzeMapper.getModelTypeId(predictHour));
        analyzeResult.setAwsMLModelId(analyzeMapper.getAwsMlModelId(predictHour));

        return analyzeResult;
    }

    /**
     * Method to programmatically create a new AWS ML Model.
     * @param mlModelId - unique identifier, auto-generated if not provided
     * @param mlModelName - unique identifier, auto-generated if not provided
     * @return CreateMLModelResult - the newly created model object
     */
    public CreateMLModelResult createAwsMlModel(String mlModelId, String mlModelName) {
        // Build request to create a new model
        CreateMLModelRequest mLModelRequest = new CreateMLModelRequest();

        // Check if modelId is provided
        if (null == mlModelId) {
            // If not provided, generate id
            mlModelId = modelIdGenerator();
        }

        // Check that modelId does not already exist & generate a new name if it does
        while (null != mapper.checkModelIdExists(mlModelId)) {
            mlModelId = modelIdGenerator();
        }

        // Check if modelName is provided
        if (mlModelName == null) {
            // If not provided, generate name using modelId
            mlModelName = "Model " + mlModelId;
        }
        // Assign values to the new model request
        mLModelRequest.setMLModelId(mlModelId);
        mLModelRequest.setMLModelName(mlModelName);
        mLModelRequest.setMLModelType(MLModelType.REGRESSION);

        // Build hash map of parameters
        // todo make these query params too
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("sgd.maxMLModelSizeInBytes", "13107200");   // = 100 MB
        parameters.put("sgd.maxPasses", "50");
        parameters.put("sgd.shuffleType", "auto");                  // default = none
        //parameters.put("sgd.l1RegularizationAmount", );
        //parameters.put("sgd.l2RegularizationAmount", );

        mLModelRequest.setParameters(parameters);
        //mLModelRequest.setRecipe(); // AWS creates default if not provided
        mLModelRequest.setTrainingDataSourceId("ds-wW8vfMKT0qv");

        // Model creation result object
        CreateMLModelResult createModelResult = new CreateMLModelResult();
        try {
            createModelResult = awsMlClient.createMLModel(mLModelRequest);
            // Handle possible error due to the time involved in creating models (2-3 mins)
        } catch (Exception e) {
            System.out.println("Model creation takes a few minutes. Model ID:" + createModelResult.getMLModelId() + "will be ready for use soon.");
        }
        return createModelResult;
    }

    /**
     * Simple method to get model status from AWS.
     * @param model - GetMLModelResult
     * @return String - Model status
     */
    public String getModelStatus(GetMLModelResult model){
        String modelStatus = model.getStatus();
        return modelStatus;
    }

    /**
     * Simple method to get endpoint status.
     * @param model - GetMLModelResult
     * @return String - Endpoint Status
     */
    public String getEndpointStatus(GetMLModelResult model){
        String endptStatus = model.getEndpointInfo().getEndpointStatus();
        return endptStatus;
    }

    /**
     * Simple method to get the model via AWS client.
     * @param getMLModelRequest
     * @return
     */
    public GetMLModelResult getModel(GetMLModelRequest getMLModelRequest){
        GetMLModelResult model = awsMlClient.getMLModel(getMLModelRequest);
        return model;
    }

    /**
     * Generates a random modelId String
     * @return String mlModelId
     */
    public String modelIdGenerator() {
        String mlModelId = "ml-" + UUID.randomUUID().toString();
        return mlModelId;
    }
}
