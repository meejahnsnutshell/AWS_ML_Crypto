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
}
