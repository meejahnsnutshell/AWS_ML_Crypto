package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Meghan Boyce on 12/26/17
 */

public class PredictCustomPojo {

    private int id;
    private String requestDate;
    private String amznRequestId;
    private double highValuePredict;
    private int coinId;
    private int time;
    private double highValueActual;
    private double pctError;
    private int modelTypeId;
    private String awsMLModelId;

//    public PredictCustomPojo(@JsonProperty("Date")String requestDate,
//                             @JsonProperty("x-amzn-RequestId") String amznRequestId,
//                             //@JsonProperty("PredictiveModelType") String modelType,
//                             @JsonProperty("predictedValue") double highValuePredict,
//                             @JsonProperty("coinId") int coinId) {
//        this.requestDate = requestDate;
//        this.amznRequestId = amznRequestId;
//        // this.modelType = modelType;
//        this.highValuePredict = highValuePredict;
//        this.coinId = coinId;
//    }

    public PredictCustomPojo() {
    }

    public int getId() {
        return id;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getAmznRequestId() {
        return amznRequestId;
    }

    public void setAmznRequestId(String amznRequestId) {
        this.amznRequestId = amznRequestId;
    }

    public double getHighValuePredict() {
        return highValuePredict;
    }

    public void setHighValuePredict(double highValuePredict) {
        this.highValuePredict = highValuePredict;
    }

    public int getCoinId() {
        return coinId;
    }

    public void setCoinId(int coinId) {
        this.coinId = coinId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getHighValueActual() {
        return highValueActual;
    }

    public void setHighValueActual(double highValueActual) {
        this.highValueActual = highValueActual;
    }

    public double getPctError() {
        return pctError;
    }

    public void setPctError(double pctError) {
        this.pctError = pctError;
    }

    public int getModelTypeId() {
        return modelTypeId;
    }

    public void setModelTypeId(int modelTypeId) {
        this.modelTypeId = modelTypeId;
    }

    public String getAwsMLModelId() {
        return awsMLModelId;
    }

    public void setAwsMLModelId(String awsMLModelId) {
        this.awsMLModelId = awsMLModelId;
    }
}
