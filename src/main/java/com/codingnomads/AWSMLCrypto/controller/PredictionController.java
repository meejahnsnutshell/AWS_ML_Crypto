package com.codingnomads.AWSMLCrypto.controller;

import com.amazonaws.services.machinelearning.model.PredictResult;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import com.codingnomads.AWSMLCrypto.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by Meghan Boyce on 12/22/17
 */

@RequestMapping("/predict")
@RestController
public class PredictionController {

    @Autowired
    PredictionService predictionService;

    @RequestMapping("/realtime")
    public PredictCustomPojo realTimePrediction(){
        return predictionService.getRealTimePrediction();
    }
}
