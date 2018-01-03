package com.codingnomads.AWSMLCrypto.mapper;

import com.codingnomads.AWSMLCrypto.model.Coin;
import com.codingnomads.AWSMLCrypto.model.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TestTableMapper {

    public final String SELECT_ALL_TEST = "SELECT * FROM datatable";
    public final String GET_TIME = "select unixtime from datatable where unixtime = #{time}";
    public final String INSERT_DATA = "insert into datatable (closevalue, highvalue, lowvalue, openvalue, volumefrom, volumeto, unixtime, coinid)" +
            "values (#{close}, #{high}, #{low}, #{open},#{volumeFrom}, #{volumeTo}, #{time}, #{coinid})";
    public final String SELECT_LATEST_TIME = "select unixtime from datatable where unixtime = (select max(unixtime) from datatable)";
    public final String GET_DATA_BY_COINID = "select * from datatable where coinid = #{coinid}";
    public final String INSERT_COIN_INFO = "insert into coininfo (name, symbol, coinname, fullname) values (#{name}, #{symbol}, #{coinName}, #{fullName})";
    public final String GET_COINID_BY_STRING = "select id from coininfo where name = #{fsym}";
    public final String GET_ALL_COINS = "select * from coininfo";


    @Select(SELECT_ALL_TEST)
    public ArrayList<Data> selectAllTest();

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

}
