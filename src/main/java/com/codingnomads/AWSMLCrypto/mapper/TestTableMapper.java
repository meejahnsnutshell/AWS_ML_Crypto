package com.codingnomads.AWSMLCrypto.mapper;

import com.codingnomads.AWSMLCrypto.model.Coin;
import com.codingnomads.AWSMLCrypto.model.Data;
import com.codingnomads.AWSMLCrypto.model.PredictCustomPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TestTableMapper {

    public final String GET_TIME = "select unixtime from data where unixtime = #{unixtime}";
    public final String INSERT_DATA = "insert into data (closevalue, highvalue, lowvalue, openvalue, volumefrom, volumeto, unixtime)" +
            "values (#{closevalue}, #{highvalue}, #{lowvalue}, #{openvalue},#{volumeFrom}, #{volumeTo}, #{unixtime})";
    public final String SELECT_LATEST_TIME = "select unixtime from data where unixtime = (select max(unixtime) from data)";
    public final String SELECT_CLOSEVALUE_FROM_LATEST_ENTRY = "select closevalue from data where unixtime = (select max(unixtime) from data)";
    public final String INSERT_PREDICT_DATA = "insert into predictions (requestdate, amznrequestid, modeltype, " +
            "highValuepredict, coinid, unixtime) values (#{requestDate}, #{amznRequestId}, #{modelType}, " +
            "#{highValuePredict}, #{coinId}, #{unixtime})";
    public final String GET_COINID = "select coinid from data where unixtime = #{unixtime}";
    public final String GET_DATA_BY_COINID = "select * from datatable where coinid = #{coinid}";
    public final String INSERT_COIN_INFO = "insert into coininfo (name, symbol, coinname, fullname) values (#{name}, #{symbol}, #{coinName}, #{fullName})";
    public final String GET_COINID_BY_STRING = "select id from coininfo where name = #{fsym}";
    public final String GET_ALL_COINS = "select * from coininfo";
    public final String GET_DATA_BETWEEN_TWO_TIMES_FOR_COINID = "select * from datatable where unixtime >= #{arg0} "
            +"and unixtime <= #{arg1} and coinid = #{arg2} order by unixtime asc";


    @Select(GET_TIME)
    public Integer getTime(Integer unixtime);

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
    public int getCoinId(int unixtime);

    @Select(GET_DATA_BETWEEN_TWO_TIMES_FOR_COINID)
    public ArrayList<Data> getDataBetweenTwoTimesForCoinID (Long timeFrom, Long timeTo, Integer coinID);

}
