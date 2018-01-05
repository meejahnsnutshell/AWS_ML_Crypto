package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * created by Jialor Cheung on 12/19/17
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

    private Integer id;
    private Integer unixtime;
    private Double closevalue;
    private Double highvalue;
    private Double lowvalue;
    private Double openvalue;
    private Double volumeFrom;
    private Double volumeTo;
    private Integer coinid;

    public Data() {
    }

    public Data(@JsonProperty("id") Integer id, @JsonProperty("time")Integer unixtime, @JsonProperty("close") Double closevalue, @JsonProperty("high") Double highvalue,
                @JsonProperty("low") Double lowvalue, @JsonProperty("open") Double openvalue, @JsonProperty("volumefrom") Double volumeFrom,
                @JsonProperty("volumeto") Double volumeTo) {
        this.id = id;
        this.unixtime = unixtime;
        this.closevalue = closevalue;
        this.highvalue = highvalue;
        this.lowvalue = lowvalue;
        this.openvalue = openvalue;
        this.volumeFrom = volumeFrom;
        this.volumeTo = volumeTo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnixTime() {
        return unixtime;
    }

    public void setTime(Integer time) {
        this.unixtime = unixtime;
    }

    public Double getClosevalue() {
        return closevalue;
    }

    public void setClosevalue(Double closevalue) {
        this.closevalue = closevalue;
    }

    public Double getHighvalue() {
        return highvalue;
    }

    public void setHighvalue(Double highvalue) {
        this.highvalue = highvalue;
    }

    public Double getLowvalue() {
        return lowvalue;
    }

    public void setLowvalue(Double lowvalue) {
        this.lowvalue = lowvalue;
    }

    public Double getOpenvalue() {
        return openvalue;
    }

    public void setOpenvalue(Double openvalue) {
        this.openvalue = openvalue;
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
