package com.codingnomads.AWSMLCrypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * created by Jialor Cheung on 1/2/18
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class Coin {

    Integer id;
    String name;
    String symbol;
    String coinName;
    String fullName;

    public Coin(@JsonProperty("id")Integer id,@JsonProperty("Name")String name,@JsonProperty("Symbol") String symbol,@JsonProperty("CoinName") String coinName,
                @JsonProperty("FullName")String fullName) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.coinName = coinName;
        this.fullName = fullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
