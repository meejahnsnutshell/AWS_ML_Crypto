package com.codingnomads.AWSMLCrypto.service;

import com.amazonaws.services.machinelearning.AbstractAmazonMachineLearning;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.*;
import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import com.codingnomads.AWSMLCrypto.model.PredictPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * created by Meghan Boyce on 12/22/17
 *
 */

@Service
public class PredictionService extends AbstractAmazonMachineLearning {

    @Autowired
    TestTableMapper mapper;

    PredictResult predictResult;

    PredictPojo predictPojo;

    /**
     * second iteration of the real time prediction method.. creating model and endpoint in aws console
     * saving results to redshift db (predictions)
     * @param
     */
    public PredictResult getRealTimePrediction(){
        // get latest time plus 1 hour
        String predictionTime = String.valueOf(mapper.selectLatestTime() + 3600);
        // select time (next hour) & openvalue (previous hour's closevalue)
        Map<String,String> record = mapper.selectCloseValueTimeFromMostRecentEntry();
        String recordOpenValue = String.valueOf(mapper.selectCloseValueFromLatestEntry());

        PredictRequest predictRequest = new PredictRequest()
                // identify model to use (created in the AWS console)
                .withMLModelId("ml-74ZaWAEUNCm")
                // identify prediction endpoint (also created in the AWS console)
                .withPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com")
                // identify record values
                //.withRecord(record);
                .addRecordEntry("time", predictionTime)
                .addRecordEntry("openvalue", recordOpenValue);

        AmazonMachineLearningClient client = new AmazonMachineLearningClient();
        // predict method returns PredictResult object -- how to use my pojo when inserting into db?
        predictResult = client.predict(predictRequest);

        //mapper.insertTime(predictionTime);
        //String amznrequestId = predictResult.getSdkResponseMetadata().getRequestId();

        // save results to db
        insertPredictResult(predictResult);
        return predictResult;
    }

    //method to insert prediction data in db
    public void insertPredictResult (PredictResult result){
        mapper.insertPredictData(result);
    }
}
