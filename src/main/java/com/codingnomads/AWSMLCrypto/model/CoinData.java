package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * created by Jialor Cheung on 1/2/18
 *
 * Hashmap corresponding to Data from API call for coinlist from <url>https://www.cryptocompare.com/api/data/coinlist/</url>
 * Reference <url>https://www.cryptocompare.com/api/#-api-data-coinlist-</url>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinData extends HashMap<String, Coin> {

    CoinData(@JsonProperty ("Data")Map<String, Coin> Data){

    }

}
