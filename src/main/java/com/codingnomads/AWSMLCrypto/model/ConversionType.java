package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * created by Jialor Cheung on 12/19/17
 *
 * ConversionType in JSON response for HistoPojo
 */

public class ConversionType {

    private String type;
    private String conversionSymbol;

    public ConversionType() {
    }

    public ConversionType(@JsonProperty("type")String type,@JsonProperty("conversionSymbol") String conversionSymbol) {
        this.type = type;
        this.conversionSymbol = conversionSymbol;
    }

    public String getType() {
        return type;
    }

    public String getConversionSymbol() {
        return conversionSymbol;
    }
}
