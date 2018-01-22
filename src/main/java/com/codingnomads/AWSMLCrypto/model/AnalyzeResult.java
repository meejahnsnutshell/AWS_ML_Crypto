package com.codingnomads.AWSMLCrypto.model;

/**
 * Created by Meghan Boyce on 1/22/18
 *
 * POJO for a complete row in the Predictions Table.
 */

public class AnalyzeResult {

    private int id;
    private String requestDate;
    private String amznRequestId;
    private double highValuePredict;
    private int coinId;
    private int unixTime;
    private double highValueActual;
    private double pctError;
    private int modelTypeId;
    private String awsMLModelId;

    public AnalyzeResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(int unixTime) {
        this.unixTime = unixTime;
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
