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
//        //checks data and separates out non existing data compared to DB into new data array
        newData = checkData(histoPojo.getData(), genericHistoCall.getFsym(), histoPojo.getTimeFrom(), histoPojo.getTimeTo());
        //inserts new data into db
//        insertHistoData(histoPojo.getData(), genericHistoCall.getFsym());
        return histoPojo;
    }

    //method to insert historical data into DB
    public void insertHistoData (Data[] data, String fsym){

        int coinID = cryptoCurrencySelect(fsym);

        for(Data item: data){
            item.setCoinid(coinID);
            mapper.insertData(item);

        }
    }

    //checking database for existing time entry, if it doesnt exist adds and returns a new arraylist
    public ArrayList<Data> checkData (Data[]data, String fsym, Long timeFrom, Long timeTo){

        Integer time;
        int coinID = cryptoCurrencySelect(fsym);
        ArrayList<Data> inDB = null;
        ArrayList<Data> notInDB = null;

        inDB = mapper.getDataBetweenTwoTimesForCoinID(timeFrom,timeTo,coinID);
        //test line to see what inDB is
        System.out.println(inDB.toString());

        if (inDB.size() == 0){
            for (Data item : data){
                notInDB.add(item);
            }
            return notInDB;
        } else {
            for (Data item: data) {
                boolean existInDB = false;
                for (Data checkDB : inDB) {
                    if (item.getUnixTime() == checkDB.getUnixTime()) {
                        existInDB = true;
                        break;
                    }
                }
                if (! existInDB){
                    notInDB.add(item);
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
    public HistoPojo getBackload(String type, String fsym, String tsym, String e, String extraParams, Boolean sign,
                                 Boolean tryConversion, Integer aggregate, Integer limit, Timestamp toTs, Boolean allData) {
        // get the most recent time that data was captured(in unixtime-seconds, convert to hours)
        long latestTime = (mapper.selectLatestTime()) / 3600;
        // get current time (in ms, convert to secs)
        long currentTimeSec =(long) (System.currentTimeMillis() * .001);
        // convert to hours
        long currentTimeHrs = currentTimeSec / 3600;
        // # of time increments between latest and current time (here we're using hrs)
        long timeDiff = (currentTimeHrs - latestTime);

        GenericHistoCall genericHistoCall = new GenericHistoCall(type, fsym, tsym, e, extraParams, sign, tryConversion,
                aggregate, limit, toTs, allData);
        genericHistoCall.setLimit((int)timeDiff);
        HistoPojo histoPojo = restTemplate.getForObject(genericHistoCall.domainParams(), HistoPojo.class);
        insertHistoData(histoPojo.getData(), genericHistoCall.getFsym());

//        // call the API w/ limit = timeDiff (this specifies # of time increments to go back and get)
//        HistoPojo histoPojo = restTemplate.getForObject(
//                domain + "histohour?fsym=BTC&tsym=USD&limit=" + timeDiff +"&aggregate=1&e=CCCAGG",
//                HistoPojo.class);
//        insertHistoData(histoPojo.getData());
        return histoPojo;
    }

    //Checks API call of coins with DB and inserts new coins into DB
    public CoinOutput getCoin () {
        CoinOutput coin = restTemplate.getForObject("https://www.cryptocompare.com/api/data/coinlist/", CoinOutput.class);
        insertCoin(coin.getData());

        return coin;
    }

//    //Checks DB for existing coin
//    public ArrayList<Coin> checkCoin(HashMap<String, Coin> data) throws NullPointerException{
//        ArrayList<Coin> inDB;
//        ArrayList<Coin> notInDB = null;
//        inDB = mapper.getAllCoins();
//        Boolean exist = false;
//
//        for (Coin item : data.values()) {
//            for (Coin checkDB : inDB) {
//                if (checkDB.getName().equals(item.getName())) {
//                    exist = true;
//                    break;
//                }
//                notInDB.add(item);
//            }
//        }
//        return notInDB;
//    }

    //method to insert coin information from api call
    public void insertCoin (CoinData data){
        for (Coin item : data.values()){
            mapper.insertCoinInfo(item);
        }
    }
}
