package com.codingnomads.AWSMLCrypto.service;

import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.services.machinelearning.AbstractAmazonMachineLearning;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.*;
import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        String predictionTimeStr = String.valueOf(predictionTimeInt);
        // select previous hour's closevalue & give to AWS as next hour's openvalue
        String recordOpenValue = String.valueOf(mapper.selectCloseValueFromLatestEntry());

        // create AWS prediction request
        PredictRequest predictRequest = new PredictRequest()
                // identify model to use (created in the AWS console)
                .withMLModelId("ml-74ZaWAEUNCm")
                // identify prediction endpoint (also created in the AWS console)
                .withPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com")
                // give AWS known data for prediction entry
                .addRecordEntry("time", predictionTimeStr)
                .addRecordEntry("openvalue", recordOpenValue);

        // create AWS ML client
        AmazonMachineLearningClient client = new AmazonMachineLearningClient();
        // Use client to get the results of the prediction
        PredictResult predictResult = client.predict(predictRequest);

        // use custom results POJO to extract desired data points from predictResult
        PredictCustomPojo predictPojo = new PredictCustomPojo();

        predictPojo.setRequestDate(predictResult.getSdkHttpMetadata().getHttpHeaders().get("Date"));
        predictPojo.setAmznRequestId(predictResult.getSdkResponseMetadata().getRequestId());
        predictPojo.setModelType(predictResult.getPrediction().getDetails().get("PredictiveModelType"));
        predictPojo.setHighValuePredict(predictResult.getPrediction().getPredictedValue());
        predictPojo.setCoinId(mapper.getCoinId(latestTime));  // todo test after coinid column added to data table
        predictPojo.setTime(predictionTimeInt);

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
     * Method to analyze prediction results
     * When last predicted hour has passed, call histohour to get the actual value
     * But we'll be doing this for the real time data gatherer anyway.. so maybe it can work double duty and insert
     * the value into both data and predictions tables..
     * Compare the two values and give them a score of some sort
     */
    public void analyzePrediction(){
        // thinking this will be part of the cronjob that will be called every hour
        // need the hour (time) in question
        // get the actual high value for the previous hour now that it is available (via cryptocompare histohour response)
        double actualValue;
        // use mapper (SQL) method to insert actual value into prediction table - started this
        // get highvaluepredict using current hour (time) in question (mapper - sql)
        double predictValue = mapper.selectHighValuePredict();
        // calculate %error between highvalue predict & actual
//        double pctError = ((actualvalue - predictValue) / actualvalue) * 100;
        /**
         * how to get high value.. do we query the sql database for the last prediction? or do we pull the high
         * value from the response at the time that we get the response.. no we'll have to get it from the db because
         * this method won't be happening until an hour later..
         */

        // insert %error into predictions table

    }
}
