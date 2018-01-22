package com.codingnomads.AWSMLCrypto.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Created by Meghan Boyce on 1/22/18
 *
 * SQL query mapper. Used in the analyze method of PredictionService class to get all values per entry in
 * the predictions method for nice displaying of results.
 */

@Mapper
public interface AnalyzeMapper {
    String GET_ID = "select id from predictions where unixtime = #{unixtime}";
    String GET_REQUEST_DATE = "select requestdate from predictions where unixtime = #{unixtime}";
    String GET_AMAZON_REQUEST_ID = " select amznrequestid from predictions where unixtime = #{unixtime}";
    String GET_COIN_ID = "select coinid from predictions where unixtime = #{unixtime}";
    String GET_MODEL_TYPE_ID = "select modeltypeid from predictions where unixtime = #{unixtime}";
    String GET_AWS_ML_MODEL_ID = "select awsmlmodelid from predictions where unixtime = #{unixtime}";

    @Select(GET_ID)
    public Integer getId(Integer unixtime);

    @Select(GET_REQUEST_DATE)
    public String getRequestDate(Integer unixtime);

    @Select(GET_AMAZON_REQUEST_ID)
    public String getAmazonRequestId(Integer unixtime);

    @Select(GET_COIN_ID)
    public Integer getCoinId(Integer unixtime);

    @Select(GET_MODEL_TYPE_ID)
    public Integer getModelTypeId(Integer unixtime);

    @Select(GET_AWS_ML_MODEL_ID)
    public String getAwsMlModelId(Integer unixtime);
}
