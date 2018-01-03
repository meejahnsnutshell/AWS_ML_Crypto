package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * created by Jialor Cheung on 12/19/17
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

    private Integer id;
    private Integer time;
    private Double close;
    private Double high;
    private Double low;
    private Double open;
    private Double volumeFrom;
    private Double volumeTo;
    private Integer coinid;

    public Data() {
    }

    public Data(@JsonProperty("id") Integer id, @JsonProperty("time")Integer time,@JsonProperty("close") Double close,@JsonProperty("high") Double high,
                @JsonProperty("low") Double low,@JsonProperty("open") Double open,@JsonProperty("volumefrom") Double volumeFrom,
                @JsonProperty("volumeto") Double volumeTo) {
        this.id = id;
        this.time = time;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volumeFrom = volumeFrom;
        this.volumeTo = volumeTo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getVolumeFrom() {
        return volumeFrom;
    }

    public void setVolumeFrom(Double volumeFrom) {
        this.volumeFrom = volumeFrom;
    }

    public Double getVolumeTo() {
        return volumeTo;
    }

    public void setVolumeTo(Double volumeTo) {
        this.volumeTo = volumeTo;
    }

    public Integer getCoinid() {
        return coinid;
    }

    public void setCoinid(Integer coinid) {
        this.coinid = coinid;
    }
}
