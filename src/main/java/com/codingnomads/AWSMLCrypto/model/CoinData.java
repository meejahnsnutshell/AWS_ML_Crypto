package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * created by Jialor Cheung on 1/2/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinData extends HashMap<String, Coin> {

    CoinData(@JsonProperty ("Data")Map<String, Coin> Data){

    }

}
