package com.codingnomads.AWSMLCrypto.service;

import com.amazonaws.services.machinelearning.AbstractAmazonMachineLearning;
import com.amazonaws.services.machinelearning.internal.PredictEndpointHandler;
import com.amazonaws.services.machinelearning.model.*;
import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * created by Meghan Boyce on 12/22/17
 */

@Service
public class PredictionService extends AbstractAmazonMachineLearning {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TestTableMapper mapper;

    PredictRequest predictRequest;
    //Prediction predictResult;
    PredictResult predictResult;

    /**
     * first iteration of the real time prediction method.. creating model and endpoint in aws console
     * @param
     */
    public PredictResult getRealTimePrediction(){
        // identify model to use (this model was created in the AWS console)
        predictRequest.setMLModelId("ml-74ZaWAEUNCm");

        // identify endpoint to send prediction results to (also created in the AWS console)
        predictRequest.setPredictEndpoint("https://realtime.machinelearning.us-east-1.amazonaws.com");

        // get latest time and and 1 hour
        String predictionTime = String.valueOf(mapper.selectLatestTime() + 3600);
        System.out.println("predictionTime = " + predictionTime);

        // todo change this, only want to send predictiontime, not whole latest entry
        predictRequest.addRecordEntry(""

        ).setRecord("time", predictionTime);

        predictResult.getPrediction();

        return predictResult;
    }
}
