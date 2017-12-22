package com.codingnomads.AWSMLCrypto.service;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.machinelearning.AbstractAmazonMachineLearning;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.internal.PredictEndpointHandler;
import com.amazonaws.services.machinelearning.model.*;
import com.amazonaws.services.machinelearning.model.transform.CreateRealtimeEndpointRequestProtocolMarshaller;
import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * created by Meghan Boyce on 12/22/17
 *
 * will need these classes to get results of creating an endpoint: CreateRealtimeEndpointResult/Request
 */

@Service
public class PredictionService extends AbstractAmazonMachineLearning {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TestTableMapper mapper;

    Prediction prediction;
    PredictResult predictResult;

    /**
     * first iteration of the real time prediction method.. creating model and endpoint in aws console
     * @param
     */
    public PredictResult getRealTimePrediction(){
        // get latest time plus 1 hour
        String predictionTime = String.valueOf(mapper.selectLatestTime() + 3600);
        // select time (next hour) & openvalue (previous hour's closevalue)
        Map<String,String> record = mapper.selectCloseValueTimeFromMostRecentEntry();

        //try manually adding items
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
        // predictRequest.setMLModelId("ml-74ZaWAEUNCm");
        // predictRequest.setPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com");
        // predictRequest.setRecord(record);

        AmazonMachineLearningClient client = new AmazonMachineLearningClient();
        predictResult = client.predict(predictRequest);
        //float result = prediction.getPredictedValue();
        //predictResult = predictResult.getPrediction();

        return predictResult;
    }
}
