package com.codingnomads.AWSMLCrypto.controller;

import com.codingnomads.AWSMLCrypto.model.AnalyzeResult;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import com.codingnomads.AWSMLCrypto.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by Meghan Boyce on 12/22/17
 */

@RequestMapping("/predict")
@RestController
public class PredictionController {

    @Autowired
    PredictionService predictionService;
    PredictCustomPojo predictCustomPojo;

    @RequestMapping("/realtime")
    public PredictCustomPojo realTimePrediction(
            @RequestParam(value = "createmodel", defaultValue = "false") boolean createModel,
            @RequestParam(value = "modelId", required = false) String modelId,
            @RequestParam(value = "modelName", required = false) String modelName){
        return predictionService.getRealTimePrediction(createModel, modelId, modelName);
    }

    @RequestMapping("/analyze")
    public AnalyzeResult analyzeRealTimePrediction(){
        return predictionService.analyzePrediction();
    }
}
