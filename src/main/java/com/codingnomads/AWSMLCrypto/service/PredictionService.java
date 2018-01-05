package com.codingnomads.AWSMLCrypto.service;

import com.amazonaws.services.machinelearning.AbstractAmazonMachineLearning;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.*;
import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * created by Meghan Boyce on 12/22/17
 */

@Service
public class PredictionService extends AbstractAmazonMachineLearning {

    @Autowired
    TestTableMapper mapper;

    /**
     * second iteration of the real time prediction method.. creating model and endpoint in aws console
     * saving results to redshift db (predictions table)
     *
     * @param
     */
    public PredictCustomPojo getRealTimePrediction() {
        // get latest time (used to query for coinid)
        int latestTime = mapper.selectLatestTime();
        // add 1 hour (used to insert into prediction db)
        int predictionTimeInt = mapper.selectLatestTime() + 3600;
        // convert to String (used in prediction request)
        //String predictionTimeStr = String.valueOf(predictionTimeInt);
        // select previous hour's closevalue & give to AWS as next hour's openvalue
        String recordOpenValue = String.valueOf(mapper.selectCloseValueFromLatestEntry());

        // create AWS prediction request
        PredictRequest predictRequest = new PredictRequest()
                // identify model to use (created programatically
                .withMLModelId(createAwsMlModel().toString())   //("ml-AWCa7W6ltxQ") <-(created in the AWS console)
                // identify prediction endpoint (also created in the AWS console)
                .withPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com")
                // give AWS known data for prediction entry
                .addRecordEntry("time", String.valueOf(predictionTimeInt))
                .addRecordEntry("openvalue", recordOpenValue);

        // create AWS ML client
        AmazonMachineLearningClient client = new AmazonMachineLearningClient();
        // Use client to get the results of the prediction
        PredictResult predictResult = client.predict(predictRequest);

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
     * Method to insert prediction data in db
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

    public CreateMLModelResult createAwsMlModel(){

        // Programatically create an ML Model, using existing training datasource
        CreateMLModelRequest mLModelRequest = new CreateMLModelRequest();
        // user provided id & name
        mLModelRequest.setMLModelId("ml-test-1");
        mLModelRequest.setMLModelName("ML Model: Java Model Creation Test");
        mLModelRequest.setMLModelType(MLModelType.REGRESSION);
        // hash map of parameters
        Map<String, String> parameters = new HashMap<String,String>();
        parameters.put("sgd.maxMLModelSizeInBytes", "13107200" );   // = 100 MB
        parameters.put("sgd.maxPasses", "50");
        parameters.put("sgd.shuffleType", "auto");                  // default = none
        //parameters.put("sgd.l1RegularizationAmount", );
        //parameters.put("sgd.l2RegularizationAmount", );

        mLModelRequest.setParameters(parameters);
        //mLModelRequest.setRecipe(); // AWS creates default if not provided
        mLModelRequest.setTrainingDataSourceId("ds-wW8vfMKT0qv");

        // Create prediction request
        AmazonMachineLearningClient client = new AmazonMachineLearningClient();
        // I'm expecting an error due to the time involved in creating models (15 mins)
        CreateMLModelResult result =  new CreateMLModelResult();
        try {
            result = client.createMLModel(mLModelRequest);
        } catch (Exception e){
            System.out.println("Models take a few mins before you can use them. Check again in 10.");
        }

//        CreateDataSourceFromRedshiftRequest dataSourceRequest = new CreateDataSourceFromRedshiftRequest();
//        dataSourceRequest.setComputeStatistics(true);
//        dataSourceRequest.setDataSourceId("ds-000001");
//        dataSourceRequest.setDataSourceName();
        return result;
    }

}
