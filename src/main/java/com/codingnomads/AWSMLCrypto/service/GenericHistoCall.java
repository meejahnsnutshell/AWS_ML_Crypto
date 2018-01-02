package com.codingnomads.AWSMLCrypto.service;

import java.sql.Timestamp;

/**
 * created by Jialor Cheung on 12/29/17
 */

public class GenericHistoCall {

    String domain = "https://min-api.cryptocompare.com/data/";
    String type = "histohour";
    String fsym = "BTC";
    String tsym = "USD";
    String e;
    String extraParams;
    Boolean sign;
    Boolean tryConversion;
    Integer aggregrate;
    Integer limit;
    Timestamp toTs;
    Boolean allData;

//    public GenericHistoCall() {
//    }

    public GenericHistoCall(String type, String fsym, String tsym) {
        this.type = type;
        this.fsym = fsym;
        this.tsym = tsym;
    }

    public GenericHistoCall(String e, String extraParams, Boolean sign, Boolean tryConversion, Integer aggregrate,
                            Integer limit, Timestamp toTs, Boolean allData) {
        this.e = e;
        this.extraParams = extraParams;
        this.sign = sign;
        this.tryConversion = tryConversion;
        this.aggregrate = aggregrate;
        this.limit = limit;
        this.toTs = toTs;
        this.allData = allData;
    }

    public GenericHistoCall(String type, String fsym, String tsym, String e, String extraParams,
                            Boolean sign, Boolean tryConversion, Integer aggregrate, Integer limit, Timestamp toTs,
                            Boolean allData) {
        this.type = type;
        this.fsym = fsym;
        this.tsym = tsym;
        this.e = e;
        this.extraParams = extraParams;
        this.sign = sign;
        this.tryConversion = tryConversion;
        this.aggregrate = aggregrate;
        this.limit = limit;
        this.toTs = toTs;
        this.allData = allData;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFsym() {
        return fsym;
    }

    public void setFsym(String fsym) {
        this.fsym = fsym;
    }

    public String getTsym() {
        return tsym;
    }

    public void setTsym(String tsym) {
        this.tsym = tsym;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public Boolean getTryConversion() {
        return tryConversion;
    }

    public void setTryConversion(Boolean tryConversion) {
        this.tryConversion = tryConversion;
    }

    public Integer getAggregrate() {
        return aggregrate;
    }

    public void setAggregrate(Integer aggregrate) {
        this.aggregrate = aggregrate;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Timestamp getToTs() {
        return toTs;
    }

    public void setToTs(Timestamp toTs) {
        this.toTs = toTs;
    }

    public Boolean getAllData() {
        return allData;
    }

    public void setAllData(Boolean allData) {
        this.allData = allData;
    }

    public String domainParams() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDomain()).append(getType()).append("?fsym=").append(getFsym())
        .append("&tsym=").append(getTsym());
        if (null != getE()) {
            sb.append("&").append("e").append("=").append(getE());
        }
        if (null != getExtraParams()) {
            sb.append("&").append("extraParams").append("=").append(getExtraParams());
        }
        if (null != getSign()) {
            sb.append("&").append("sign").append("=").append(getSign());
        }
        if (null != getTryConversion()) {
            sb.append("&").append("tryConversion").append("=").append(getTryConversion());
        }
        if (null != getAggregrate()) {
            sb.append("&").append("aggregate").append("=").append(getAggregrate());
        }
        if (null != getLimit()) {
            sb.append("&").append("limit").append("=").append(getLimit());
        }
        if (null != getToTs()) {
            sb.append("&").append("toTs").append("=").append(getToTs());
        }
        if (null != getAllData()) {
            sb.append("&").append("allData").append("=").append(getAllData());
        }
        return sb.toString();
    }
}
