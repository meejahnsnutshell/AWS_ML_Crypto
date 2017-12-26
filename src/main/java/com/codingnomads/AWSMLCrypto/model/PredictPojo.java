package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictPojo {

    private String requestDate;
    private String amznRequestId;
    private String modelType;
    private double highValuePredict;
    private int coinId;
    private int time;

    public PredictPojo(@JsonProperty("Date")String requestDate,
                       @JsonProperty("x-amzn-RequestId") String amznRequestId,
                       @JsonProperty("PredictiveModelType") String modelType,
                       @JsonProperty("predictedValue") double highValuePredict,
                       @JsonProperty("coinId") int coinId) {
        this.requestDate = requestDate;
        this.amznRequestId = amznRequestId;
        this.modelType = modelType;
        this.highValuePredict = highValuePredict;
        this.coinId = coinId;
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

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
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
}
