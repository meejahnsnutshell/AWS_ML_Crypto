package com.codingnomads.AWSMLCrypto.service;

import com.codingnomads.AWSMLCrypto.model.HistoPojo;
import org.springframework.http.HttpMethod;

/**
 * created by Jialor Cheung on 1/2/18
 */

public enum GenericRequestEnum {

    GETDAY("histoday", HttpMethod.GET, HistoPojo.class),
    GETHOUR("histohour", HttpMethod.GET, HistoPojo.class),
    GETMIN("histominute", HttpMethod.GET, HistoPojo.class);

    private String endPoint;
    private final HttpMethod httpMethod;
    private final Class outputClass;
    private String fullURL;
    private final String domain = "https://min-api.cryptocompare.com/data/";

    public String getEndPoint() {
        return endPoint;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getDomain() {
        return domain;
    }

    public String getFullURL() {
        return fullURL;
    }

    public Class getOutputClass() {
        return outputClass;
    }

    public void updateEndpoint(String queryParams){
        this.endPoint = this.endPoint + queryParams;
        this.fullURL = this.fullURL + queryParams;
    }

    GenericRequestEnum(String endPoint, HttpMethod httpMethod, Class outputClass) {
        this.endPoint = endPoint;
        this.httpMethod = httpMethod;
        this.outputClass = outputClass;
        this.fullURL = domain + endPoint;
    }

    GenericRequestEnum(String endPoint, HttpMethod httpMethod, Class outputClass, String pair) {
        this.endPoint = endPoint + pair;
        this.httpMethod = httpMethod;
        this.outputClass = outputClass;
        this.fullURL = domain + endPoint + pair;
    }
}
