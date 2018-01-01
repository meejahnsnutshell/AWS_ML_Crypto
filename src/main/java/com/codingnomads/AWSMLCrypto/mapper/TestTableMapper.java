package com.codingnomads.AWSMLCrypto.mapper;

import com.amazonaws.services.machinelearning.model.PredictResult;
import com.codingnomads.AWSMLCrypto.model.Data;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface TestTableMapper {

    public final String GET_TIME = "select time from data where time = #{time}";
    public final String INSERT_DATA = "insert into data (closevalue, highvalue, lowvalue, openvalue, volumefrom, volumeto, time)" +
            "values (#{close}, #{high}, #{low}, #{open},#{volumeFrom}, #{volumeTo}, #{time})";
    public final String SELECT_LATEST_TIME = "select time from data where time = (select max(time) from data)";
    public final String SELECT_CLOSEVALUE_FROM_LATEST_ENTRY = "select closevalue from data where time = (select max(time) from data)";
    public final String INSERT_PREDICT_DATA = "insert into predictions (requestdate, amznrequestid, modeltype, " +
            "highValuepredict, coinid, time) values (#{requestDate}, #{amznRequestId}, #{modelType}, " +
            "#{highValuePredict}, #{coinId}, #{time})";
    public final String GET_COINID = "select coinid from data where time = #{time}";

    @Select(GET_TIME)
    public int getTime(int time);

    @Select(INSERT_DATA)
    public void insertData(Data data);

    @Select(SELECT_LATEST_TIME)
    public int selectLatestTime();

    @Select(SELECT_CLOSEVALUE_FROM_LATEST_ENTRY)
    public double selectCloseValueFromLatestEntry();

    @Select(INSERT_PREDICT_DATA)
    public void insertPredictData(PredictCustomPojo predictResult);

    @Select(GET_COINID)
    public int getCoinId(int time);

}
