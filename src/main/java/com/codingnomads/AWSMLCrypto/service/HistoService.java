package com.codingnomads.AWSMLCrypto.service;

import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import com.codingnomads.AWSMLCrypto.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.serializer.Deserializer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service methods for cryptocompare api calls.
 */

@Service
public class HistoService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TestTableMapper mapper;

    String domain = "https://min-api.cryptocompare.com/data/";

    /**
     * Creates a generic historical data api call from cryptocompare.com depending on the parameters included
     * By default histohour is set as default historical data type, BTC is set as default fsym, USD is set as default tsym
     *
     * @return histopojo object from restfull api call
     */
    public HistoPojo getHistoData(String type, String fsym, String tsym, String e, String extraParams, Boolean sign,
                                  Boolean tryConversion, Integer aggregate, Integer limit, Timestamp toTs, Boolean allData){

        ArrayList<Data> newData;
        //create a generic HistoDataCall
        GenericHistoCall genericHistoCall = new GenericHistoCall(type, fsym, tsym, e, extraParams, sign, tryConversion,
                aggregate, limit, toTs, allData);
        HistoPojo histoPojo = restTemplate.getForObject(genericHistoCall.domainParams(), HistoPojo.class);
        //checks data and separates out non existing data compared to DB into new data array
        newData = checkData(histoPojo.getData(), genericHistoCall.getFsym());
        //inserts new data into db
        insertHistoData(newData, genericHistoCall.getFsym());

        return histoPojo;
    }

    //method to insert historical data into DB
    public void insertHistoData (ArrayList<Data> data, String fsym){

        int coinID = cryptoCurrencySelect(fsym);

        for(Data item: data){
            item.setCoinid(coinID);
            mapper.insertData(item);

        }
    }

    //checking database for existing time entry, if it doesnt exist adds and returns a new arraylist
    public ArrayList<Data> checkData (Data[]data, String fsym){

        Integer time;
        int coinID = cryptoCurrencySelect(fsym);
        ArrayList<Data> inDB;
        ArrayList<Data> notInDB = null;

        inDB = mapper.getDataByCoinID(coinID);

        for (Data item: data) {
            for (Data checkDB : inDB) {
                if (item.getTime() != checkDB.getTime()) {
                    time = null;
                    if (time == null) {
                        notInDB.add(item);
                        break;
                    }
                }
            }
        }

        return notInDB;
    }

    /**
     * Method to select the correct coinID for database insertion or check based off of fsym variable
     * @param fsym  parameter of API call to select the type of crypto currency data to search for
     * @return  integer corresponding to crypto currency ID in DB
     */
    public int cryptoCurrencySelect(String fsym){
        int coinID = mapper.getCoinIdByString(fsym);

        return coinID;
    }

    /**
     * This method is used after the initial getHisto call is made, to fill in missing data between the last
     * historical data point and the first data point of the real-time call.
     * @return
     */
    public HistoPojo getBackload() {
        // get the most recent time that data was captured(in unixtime-seconds, convert to hours)
        long latestTime = (mapper.selectLatestTime()) / 3600;

        // get current time (in ms, convert to secs)
        long currentTimeSec =(long) (System.currentTimeMillis() * .001);
        // convert to hours
        long currentTimeHrs = currentTimeSec / 3600;

        // # of time increments between latest and current time (here we're using hrs)
        long timeDiff = (currentTimeHrs - latestTime);

        // call the API w/ limit = timeDiff (this specifies # of time increments we want to go back and get)
        HistoPojo histoPojo = restTemplate.getForObject(
                domain + "histominute?fsym=BTC&tsym=USD&limit=" + timeDiff +"&aggregate=1&e=CCCAGG",
                HistoPojo.class);
//      insertHistoData(histoPojo.getData());
        return histoPojo;
    }

    //Checks API call of coins with DB and inserts new coins into DB
    public CoinOutput getCoin () {
        ArrayList<Coin> newCoin;
        CoinOutput coin = restTemplate.getForObject("https://www.cryptocompare.com/api/data/coinlist/", CoinOutput.class);
        newCoin = checkCoin(coin.getData());
        insertCoin(newCoin);
        return coin;
    }

    //Checks DB for existing coin
    public ArrayList<Coin> checkCoin(HashMap<String, Coin> data){
        ArrayList<Coin> inDB;
        ArrayList<Coin> notInDB=null;
        inDB = mapper.getAllCoins();
        String exist;

        for (Coin item : data.values()){
            for (Coin checkDB : inDB){
                if (item.getName() != checkDB.getName()) {
                    exist = null;
                    if (exist == null) {
                        notInDB.add(item);
                        break;
                    }
                }
            }
        }
        return notInDB;
    }

    //method to insert coin information from api call
    public void insertCoin (ArrayList<Coin> data){
        for (Coin item : data){
            mapper.insertCoinInfo(item);
        }
    }
}
