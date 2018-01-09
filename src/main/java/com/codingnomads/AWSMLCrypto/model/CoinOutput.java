package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * created by Jialor Cheung on 1/2/18
 *
 * Pojo for coinlist response from <url>https://www.cryptocompare.com/api/data/coinlist/</url>
 */

public class CoinOutput {
    String response;
    String message;
    String baseImageUrl;
    String baseLinkUrl;
    List defaultWatchList;
    CoinData Data;
    Integer type;

    public CoinOutput(@JsonProperty("Response")String response,@JsonProperty("Message") String message,
                      @JsonProperty("BaseImageUrl") String baseImageUrl,@JsonProperty("BaseLinkUrl") String baseLinkUrl,
                      @JsonProperty("DefaultWatchList") List defaultWatchList,@JsonProperty("Data") CoinData data,
                      @JsonProperty("Type")Integer type) {
        this.response = response;
        this.message = message;
        this.baseImageUrl = baseImageUrl;
        this.baseLinkUrl = baseLinkUrl;
        this.defaultWatchList = defaultWatchList;
        Data = data;
        this.type = type;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBaseImageUrl() {
        return baseImageUrl;
    }

    public void setBaseImageUrl(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    public String getBaseLinkUrl() {
        return baseLinkUrl;
    }

    public void setBaseLinkUrl(String baseLinkUrl) {
        this.baseLinkUrl = baseLinkUrl;
    }

    public List getDefaultWatchList() {
        return defaultWatchList;
    }

    public void setDefaultWatchList(List defaultWatchList) {
        this.defaultWatchList = defaultWatchList;
    }

    public CoinData getData() {
        return Data;
    }

    public void setData(CoinData data) {
        Data = data;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
