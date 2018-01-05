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
    public boolean createModel = false;
    public String modelId = null;
    public String mlModelName = null;

    /**
     * third iteration of the real time prediction method.. creating model & endpoint in program
     * saving results to redshift db (predictions table)
     * @param createModel, modelId status is defined by the user via url query param, existing modelid must be provided to use
     */
    // todo add model name as query param
    public PredictCustomPojo getRealTimePrediction(boolean createModel, String modelId) {
        // get latest time (used to query for coinid)
        int latestTime = mapper.selectLatestTime();
        // add 1 hour (used to insert into prediction db)
        int predictionTimeInt = mapper.selectLatestTime() + 3600;
        // select previous hour's closevalue & give to AWS as next hour's openvalue
        String recordOpenValue = String.valueOf(mapper.selectCloseValueFromLatestEntry());

        PredictRequest predictRequest = new PredictRequest();
        predictRequest
                .addRecordEntry("time", String.valueOf(predictionTimeInt))
                .addRecordEntry("openvalue", recordOpenValue);
        if (createModel == false) {
            // use an existing model, specified by modelId, which must exist in your aws account
            // todo figure out a way to check that the model exists?
            predictRequest.setMLModelId(modelId);
            // specify endpoint (must be activated for real time predictions)
            // todo get endpoint based on provided model id. could be param but it's a long string
            predictRequest.setPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com");
        } else {
            // programmatically create a new model
            // todo make it possible to pass specific model parameters
            CreateMLModelResult result = createAwsMlModel(modelId, mlModelName);
            predictRequest.setMLModelId(result.getMLModelId());

            // todo figure out how to make endpoint available programatically for a newly created model
            predictRequest.setPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com");
        }
        // status will likely be pending for a few minutes, if pending or ready then move on to get prediction
        GetMLModelResult model = new GetMLModelResult();
        // todo delete after testing:
        model.getMLModelId();
        // not sure if this can be done for an existing model. may need to put inside the above else block

        PredictCustomPojo predictPojo = new PredictCustomPojo();
        String modelStatus = model.getStatus();

        if ((modelStatus == "DELETED") || (modelStatus == "FAILED")){
            System.out.println("Model status is " + modelStatus + ". It can't be used");
        } else if ((modelStatus == "PENDING") || (modelStatus == "INPROGRESS")){
            while (modelStatus != "COMPLETED"){
                System.out.println("Model status is " + modelStatus + ". Checking again..");
                modelStatus = model.getStatus();
            }
        } else if (modelStatus == "COMPLETED"){
            // create AWS ML client
            AmazonMachineLearningClient client = new AmazonMachineLearningClient();
            // Use client to get the results of the prediction
            PredictResult predictResult = client.predict(predictRequest);
            // use custom results POJO to extract desired data points from predictResult
            predictPojo.setRequestDate(predictResult.getSdkHttpMetadata().getHttpHeaders().get("Date"));
            predictPojo.setAmznRequestId(predictResult.getSdkResponseMetadata().getRequestId());
            predictPojo.setHighValuePredict(predictResult.getPrediction().getPredictedValue());
            predictPojo.setCoinId(mapper.getCoinId(latestTime));
            predictPojo.setTime(predictionTimeInt);
            predictPojo.setModelTypeId(mapper.getModelTypeId(predictResult.getPrediction().getDetails().get("PredictiveModelType")));
            predictPojo.setAwsMLModelId(predictRequest.getMLModelId());

            // insert results in db
            insertPredictResult(predictPojo);
        }
        // todo create an endpoint/ make it real time ready -- should have a check for existing endpoint first
        return predictPojo;
    }

    /**
     * Method that inserts prediction data in db
     * @param result
     */
    public void insertPredictResult(PredictCustomPojo result) {
        mapper.insertPredictData(result);
    }

    /**
     * Method to determine accuracy of real-time predictions
     */
    public double analyzePrediction(){
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
        if (actualValue == 0){
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
     * @param mlModelId
     * @return
     */
    public CreateMLModelResult createAwsMlModel(String mlModelId, String mlModelName){
        CreateMLModelRequest mLModelRequest = new CreateMLModelRequest();

        if (mlModelId == null){
            // generates unique identifier if model id is not provided
            mlModelId = "ml-" + UUID.randomUUID().toString();   // todo could check that id doesn't already exist
        }

        if (mlModelName == null){
            // generates unique name if model name is not provided
            mlModelName = mlModelId + " Model";     // todo could check that name doesn't already exist
        }

        // user provides id & name
        mLModelRequest.setMLModelId(mlModelId);
        mLModelRequest.setMLModelName(mlModelName);   // todo make this a parameter (query param?)
        mLModelRequest.setMLModelType(MLModelType.REGRESSION);  // todo not necessary but this could also be a param

        // hash map of parameters
        // todo potentially make these query params too, so you can tweak models on the fly
        Map<String, String> parameters = new HashMap<String,String>();
        parameters.put("sgd.maxMLModelSizeInBytes", "13107200" );   // = 100 MB
        parameters.put("sgd.maxPasses", "50");
        parameters.put("sgd.shuffleType", "auto");                  // default = none
        //parameters.put("sgd.l1RegularizationAmount", );
        //parameters.put("sgd.l2RegularizationAmount", );

        mLModelRequest.setParameters(parameters);
        //mLModelRequest.setRecipe(); // AWS creates default if not provided
        mLModelRequest.setTrainingDataSourceId("ds-wW8vfMKT0qv");

        AmazonMachineLearningClient client = new AmazonMachineLearningClient();
        // I'm expecting an error due to the time involved in creating models (2-3 mins)
        CreateMLModelResult result =  new CreateMLModelResult();
        try {
            result = client.createMLModel(mLModelRequest);
        } catch (Exception e){
            System.out.println("Model creation takes a few minutes. Model ID:" + result.getMLModelId() + "will be ready for use soon.");
        }
        return result;
    }
}
