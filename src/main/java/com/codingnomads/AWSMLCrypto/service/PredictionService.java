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
 *
 */

@Service
public class PredictionService extends AbstractAmazonMachineLearning {

    @Autowired
    TestTableMapper mapper;

    PredictResult predictResult;

    PredictCustomPojo predictPojo;

    /**
     * second iteration of the real time prediction method.. creating model and endpoint in aws console
     * saving results to redshift db (predictions)
     * @param
     */
    public PredictCustomPojo getRealTimePrediction(){
        // get latest time plus 1 hour
        int predictionTimeInt = mapper.selectLatestTime() + 3600;
        String predictionTimeStr = String.valueOf(predictionTimeInt);
        //String predictionTime = String.valueOf(mapper.selectLatestTime() + 3600);
        // select time (next hour) & openvalue (previous hour's closevalue)
        Map<String,String> record = mapper.selectCloseValueTimeFromMostRecentEntry(); // not using this but like this way much better
        String recordOpenValue = String.valueOf(mapper.selectCloseValueFromLatestEntry());

        PredictRequest predictRequest = new PredictRequest()
                // identify model to use (created in the AWS console)
                .withMLModelId("ml-74ZaWAEUNCm")
                // identify prediction endpoint (also created in the AWS console)
                .withPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com")
                // identify record values
                //.withRecord(record);
                .addRecordEntry("time", predictionTimeStr)
                .addRecordEntry("openvalue", recordOpenValue);

        AmazonMachineLearningClient client = new AmazonMachineLearningClient();
        predictResult = client.predict(predictRequest);

        // use custom results POJO to extract desired data points from AWS response
        PredictCustomPojo predictPojo = new PredictCustomPojo();
            predictPojo.setRequestDate(predictResult.getSdkHttpMetadata().getHttpHeaders().get("Date"));
            predictPojo.setAmznRequestId(predictResult.getSdkResponseMetadata().getRequestId());
            predictPojo.setModelType(predictResult.getPrediction().getDetails().get("PredictiveModelType"));
            predictPojo.setHighValuePredict(predictResult.getPrediction().getPredictedValue());
//          predictPojo.setCoinId();  // todo get coin id - still needs to be added to db
            predictPojo.setTime(predictionTimeInt);

        // insert results in db
        insertPredictResult(predictPojo);
        return predictPojo;
    }

    //method to insert prediction data in db
    public void insertPredictResult (PredictCustomPojo result){
        mapper.insertPredictData(result);
    }
}
