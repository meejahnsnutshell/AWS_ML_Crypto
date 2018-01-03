package com.codingnomads.AWSMLCrypto.mapper;

import com.codingnomads.AWSMLCrypto.model.Coin;
import com.codingnomads.AWSMLCrypto.model.Data;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface TestTableMapper {

    public final String GET_TIME = "select unixtime from data where unixtime = #{time}";
    public final String INSERT_DATA = "insert into data (closevalue, highvalue, lowvalue, openvalue, volumefrom, volumeto, unixtime)" +
            "values (#{close}, #{high}, #{low}, #{open},#{volumeFrom}, #{volumeTo}, #{time})";
    public final String SELECT_LATEST_TIME = "select unixtime from data where unixtime = (select max(unixtime) from data)";
    public final String SELECT_CLOSEVALUE_FROM_LATEST_ENTRY = "select closevalue from data where unixtime = (select max(unixtime) from data)";
    public final String INSERT_PREDICT_DATA = "insert into predictions (requestdate, amznrequestid, modeltype, " +
            "highValuepredict, coinid, unixtime) values (#{requestDate}, #{amznRequestId}, #{modelType}, " +
            "#{highValuePredict}, #{coinId}, #{time})";
    public final String GET_COINID = "select coinid from data where unixtime = #{time}";
    public final String GET_DATA_BY_COINID = "select * from datatable where coinid = #{coinid}";
    public final String INSERT_COIN_INFO = "insert into coininfo (name, symbol, coinname, fullname) values (#{name}, #{symbol}, #{coinName}, #{fullName})";
    public final String GET_COINID_BY_STRING = "select id from coininfo where name = #{fsym}";
    public final String GET_ALL_COINS = "select * from coininfo";
    public final String INSERT_HIGHVALUEACTUAL = "insert into predictions (highvalueactual) values (#{})"; // TODO: finish this
    public final String SELECT_HIGHVALUEPREDICT = "select highvaluepredict from predictions where unixtime = (select max(unixtime) from predictions)";

    @Select(GET_TIME)
    public Integer getTime(Integer time);

    @Select(INSERT_DATA)
    public void insertData(Data data);

    @Select(SELECT_LATEST_TIME)
    public Integer selectLatestTime();

    @Select(GET_DATA_BY_COINID)
    public ArrayList<Data> getDataByCoinID (Integer coinID);

    @Select(INSERT_COIN_INFO)
    public void insertCoinInfo (Coin coin);

    @Select(GET_COINID_BY_STRING)
    public Integer getCoinIdByString (String fsym);

    @Select(GET_ALL_COINS)
    public ArrayList<Coin> getAllCoins();

    @Select(SELECT_CLOSEVALUE_FROM_LATEST_ENTRY)
    public double selectCloseValueFromLatestEntry();

    @Select(INSERT_PREDICT_DATA)
    public void insertPredictData(PredictCustomPojo predictResult);

    @Select(GET_COINID)
    public int getCoinId(int time);

    @Select(INSERT_HIGHVALUEACTUAL)
    public void insertHighValueActual();

    @Select(SELECT_HIGHVALUEPREDICT)
    public double selectHighValuePredict();

}
