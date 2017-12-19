package com.codingnomads.AWSMLCrypto.model;

/**
 * created by Jialor Cheung on 12/19/17
 */

public class ConversionType {

    private String type;
    private String conversionSymbol;

    public ConversionType() {
    }

    public ConversionType(String type, String conversionSymbol) {
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
