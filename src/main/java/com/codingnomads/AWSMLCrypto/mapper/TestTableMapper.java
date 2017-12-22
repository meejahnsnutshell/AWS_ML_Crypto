package com.codingnomads.AWSMLCrypto.mapper;

import com.codingnomads.AWSMLCrypto.model.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface TestTableMapper {

    public final String SELECT_ALL_TEST = "SELECT * FROM data";
    public final String GET_TIME = "select time from data where time = #{time}";
    public final String INSERT_DATA = "insert into data (closevalue, highvalue, lowvalue, openvalue, volumefrom, volumeto, time)" +
            "values (#{close}, #{high}, #{low}, #{open},#{volumeFrom}, #{volumeTo}, #{time})";
    public final String SELECT_LATEST_TIME = "select time from data where time = (select max(time) from data)";
    public final String GET_MOST_RECENT_ENTRY = "select * from data where time = (select max(time) from data)";



    @Select(SELECT_ALL_TEST)
    public ArrayList<Data> selectAllTest();

    @Select(GET_TIME)
    public int getTime(int time);

    @Select(INSERT_DATA)
    public void insertData(Data data);

    @Select(SELECT_LATEST_TIME)
    public int selectLatestTime();

    @Select(GET_MOST_RECENT_ENTRY)  // return object as map of strings to meet aws requirements
    public Map<String, String> getMostRecentEntry();

}
