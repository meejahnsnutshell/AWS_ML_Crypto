package com.codingnomads.AWSMLCrypto.service;

import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.services.machinelearning.AbstractAmazonMachineLearning;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.*;
import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.abs;

/**
 * created by Meghan Boyce on 12/22/17
 */

@Service
public class PredictionService extends AbstractAmazonMachineLearning {

    @Autowired
    TestTableMapper mapper;
    //public boolean createModel = false;
    public String modelId = null;
    public String modelName = null;
    AmazonMachineLearningClient awsMlClient = new AmazonMachineLearningClient();
    MLModel model;

    /**
     * third iteration of the real time prediction method.. creating model & endpoint in program
     * saving results to redshift db (predictions table)
     *
     * @param createModel, modelId status is defined by the user via url query param, existing modelid must be provided to use
     */
    public PredictCustomPojo getRealTimePrediction(boolean createModel, String modelId, String modelName) {
        // get latest time (used to query for coinid)
        int latestTime = mapper.selectLatestTime();
        // add 1 hour (used to insert into prediction db)
        int predictionTimeInt = mapper.selectLatestTime() + 3600;
        // select previous hour's closevalue & give to AWS as next hour's openvalue
        String recordOpenValue = String.valueOf(mapper.selectCloseValueFromLatestEntry());

        if (createModel == true) {
            // programmatically create a new model
            // todo make it possible to pass specific model parameters
            CreateMLModelResult model = createAwsMlModel(modelId, modelName);
            modelId = model.getMLModelId();
        }

        // check that model exists (whether new or existing)

        GetMLModelRequest modelRequest = new GetMLModelRequest();
        modelRequest.setVerbose(false);
        modelRequest.setMLModelId(modelId);

        GetMLModelResult model = awsMlClient.getMLModel(modelRequest);
        String modelStatus = model.getStatus();

        PredictRequest predictRequest = new PredictRequest();

        if((modelStatus =="DELETED")||(modelStatus =="FAILED"))
        {
            System.out.println("Model status is " + modelStatus + ". It can't be used.");
        } else if((modelStatus =="PENDING")||(modelStatus =="INPROGRESS"))
        {
            while (modelStatus != "COMPLETED") {
                System.out.println("Model status is " + modelStatus + ". Checking again..");
                modelStatus = model.getStatus();
            }
        } else if(modelStatus =="COMPLETED")
        {
            predictRequest
                    .addRecordEntry("time", String.valueOf(predictionTimeInt))
                    .addRecordEntry("openvalue", recordOpenValue);
            predictRequest.setMLModelId(modelId);
            // specify endpoint (must be activated for real time predictions)
            // todo get endpoint based on provided model id. note - it's based on region, doesn't actually change unless models are in diff aws regions
            predictRequest.setPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com");
            predictRequest.setMLModelId(modelId);
        }

        // Use AWS client to get the results of the prediction
        PredictResult predictResult = awsMlClient.predict(predictRequest);

        // use custom results POJO to extract desired data points from predictResult
        PredictCustomPojo predictPojo = new PredictCustomPojo();
        predictPojo.setRequestDate(predictResult.getSdkHttpMetadata().getHttpHeaders().get("Date"));
        predictPojo.setAmznRequestId(predictResult.getSdkResponseMetadata().getRequestId());
        predictPojo.setHighValuePredict(predictResult.getPrediction().getPredictedValue());
        predictPojo.setCoinId(mapper.getCoinId(latestTime));
        predictPojo.setTime(predictionTimeInt);
        predictPojo.setModelTypeId(mapper.getModelTypeId(predictResult.getPrediction().getDetails().get("PredictiveModelType")));
        predictPojo.setAwsMLModelId(predictRequest.getMLModelId());

        // insert results in db
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
     * Method to determine accuracy of real-time predictions
     */
    public double analyzePrediction() {
        // thinking this will be part of the cronjob that will be called every hour,
        // must run after getting the data for the latest hour

        // get time for most recent prediction
        Integer predictHour = mapper.selectLatestTime();

        // get actual high value for this time from datatable
        double actualValue = mapper.selectHighValueActual(predictHour);

        // update/set actual value in prediction table for record w/currentHour
        mapper.updateHighValueActual(actualValue, predictHour);

        // select highvaluepredict for currentHour
        double predictValue = mapper.selectHighValuePredict(predictHour);

        // calculate (absolute value) %error between highvalue predict & actual
        double pctError;
        if (actualValue == 0) {
            // to avoid divide by zero, get the absolute error
            pctError = abs(actualValue - predictValue);
        } else {
            pctError = (abs(actualValue - predictValue) / actualValue) * 100.0;
        }
        // insert %error into predictions table
        mapper.updatePcterror(pctError, predictHour);

        return pctError;
        // alternatively can I get the existing prediction object in one sql statement,
        // set the actual and percent error and then return the object so we see the whole record in json?
    }

    /**
     * Method to programatically create a new AWS ML Model
     *
     * @param mlModelId
     * @return
     */
    public CreateMLModelResult createAwsMlModel(String mlModelId, String mlModelName) {
        CreateMLModelRequest mLModelRequest = new CreateMLModelRequest();

        // check if modelId is provided
        if (null == mlModelId) {
            // generate id
            mlModelId = modelIdGenerator();
        }

        // check that modelid name does not already exist & generate a new name if it does exist
        while (null != mapper.checkModelIdExists(mlModelId)) {
            mlModelId = modelIdGenerator();
        }

        // check if modelName is provided
        if (mlModelName == null) {
            // generates unique name if model name is not provided
            mlModelName = mlModelId + " Model"; // todo check that name doesn't already exist - maybe not necessary since we already check for a unique modelid
        }

        mLModelRequest.setMLModelId(mlModelId);
        mLModelRequest.setMLModelName(mlModelName);
        mLModelRequest.setMLModelType(MLModelType.REGRESSION);  // todo not necessary for us, but this could also be a param

        // hash map of parameters
        // todo potentially make these query params too, so you can tweak models on the fly
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("sgd.maxMLModelSizeInBytes", "13107200");   // = 100 MB
        parameters.put("sgd.maxPasses", "50");
        parameters.put("sgd.shuffleType", "auto");                  // default = none
        //parameters.put("sgd.l1RegularizationAmount", );
        //parameters.put("sgd.l2RegularizationAmount", );

        mLModelRequest.setParameters(parameters);
        //mLModelRequest.setRecipe(); // AWS creates default if not provided
        mLModelRequest.setTrainingDataSourceId("ds-wW8vfMKT0qv");

        // Expect an error due to the time involved in creating models (2-3 mins)
        CreateMLModelResult result = new CreateMLModelResult();
        try {
            result = awsMlClient.createMLModel(mLModelRequest);
        } catch (Exception e) {
            System.out.println("Model creation takes a few minutes. Model ID:" + result.getMLModelId() + "will be ready for use soon.");
        }
        return result;
    }

    public String getEndpoint(String modelId) {
        GetMLModelRequest modelRequest = new GetMLModelRequest();
        modelRequest.setMLModelId(modelId);
        modelRequest.setVerbose(false); // returns recipe

        GetMLModelResult model = awsMlClient.getMLModel(modelRequest);
        String status = model.getEndpointInfo().getEndpointStatus();

        if (status == "READY") {

        }
        return status;
    }

    public String modelIdGenerator() {
        String mlModelId = "ml-" + UUID.randomUUID().toString();
        return mlModelId;
    }
}
