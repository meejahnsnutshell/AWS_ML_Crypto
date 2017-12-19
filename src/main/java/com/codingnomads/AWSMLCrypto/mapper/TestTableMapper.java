package com.codingnomads.AWSMLCrypto.mapper;

import com.codingnomads.AWSMLCrypto.model.Data;
import com.codingnomads.AWSMLCrypto.model.Test;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface TestTableMapper {

    public final String SELECT_ALL_TEST = "SELECT * FROM testtable";
    public final String INSERT_DATA = "insert into public.data ( time, closevalue, highvalue, lowvalue, openvalue, volumefrom, volumeto)" +
            "values (#{time}, #{close}, #{high}, #{low}, #{open},#{volumefrom}, #{volumeto})";


    @Select(SELECT_ALL_TEST)
    public ArrayList<Test> selectAllTest();

    @Select(INSERT_DATA)
    public int insertData(Data[] data);

}
