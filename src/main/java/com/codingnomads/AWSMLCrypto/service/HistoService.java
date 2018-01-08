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
     * @return Histopojo object from API call
     */
    public HistoPojo getHistoData(String type, String fsym, String tsym, String e, String extraParams, Boolean sign,
                                  Boolean tryConversion, Integer aggregate, Integer limit, Timestamp toTs, Boolean allData){

        //creates a new Arraylist of type Data to store new Data for insertion into database
        ArrayList<Data> newData = new ArrayList<Data>();
        //create a generic HistoDataCall based on parameters passed by API call
        GenericHistoCall genericHistoCall = new GenericHistoCall(type, fsym, tsym, e, extraParams, sign, tryConversion,
                aggregate, limit, toTs, allData);
        //domainParams creates a string url based on parameters passed in through API call and passes it to resttemplate call
        HistoPojo histoPojo = restTemplate.getForObject(genericHistoCall.domainParams(), HistoPojo.class);
        //checks data and separates out non existing data compared to DB into new data array
        newData = checkData(histoPojo.getData(), genericHistoCall.getFsym(), histoPojo.getTimeFrom(), histoPojo.getTimeTo());
        //inserts new data into database
        insertHistoData(newData, genericHistoCall.getFsym());
        return histoPojo;
    }

    /**
     * Method to insert HistoPojo Data into database and assign coinID based on query parameters
     * @param data  Arraylist of data
     * @param fsym  Query parameter passed from API call
     */
    public void insertHistoData (ArrayList<Data> data, String fsym){
        //assigns coinID to database coinID for given String fsym parameter
        int coinID = cryptoCurrencySelect(fsym);
        //for each object in arraylist, sets coinID corresponding to given cryptocurrency and inserts into database
        for(Data item: data){
            item.setCoinid(coinID);
            mapper.insertData(item);
        }
    }

    /**
     * Method to check data from API call against existing database for the given time frame of data according
     * to the cryptocurrency being queried
     *
     * @param data      Array of data returned from  API call
     * @param fsym      Query parameter passed from API call
     * @param timeFrom  TimeFrom variable returned by API call indicating start time of data set
     * @param timeTo    TimeTo variable returned by API call indicating end time of data set
     * @return          Returns an arraylist of Data corresponding to new data that does not exist in database
     */
    public ArrayList<Data> checkData (Data[]data, String fsym, Long timeFrom, Long timeTo){

        //assigns coinID of database to coinID that matches String fsym
        int coinID = cryptoCurrencySelect(fsym);
        ArrayList<Data> inDB = new ArrayList();
        ArrayList<Data> notInDB = new ArrayList();
        //query database for all data between timeFrom and timeTo for coinID and stores into inDB arraylist
        inDB = mapper.getDataBetweenTwoTimesForCoinID(timeFrom,timeTo,coinID);
        //if inDB contains no data, adds data into notInDB arraylist and returns arraylist
        //if inDB contains data, checks all items in data against inDB and adds non-existing data into notInDB arraylist
        if (inDB.size() == 0){
            for (Data item: data){
                notInDB.add(item);
            }
            System.out.println(notInDB.toString());
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
     * Method to select the corresponding coinID for fsym parameter passed in by API call
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
        ArrayList<Data> data = new ArrayList<Data>();
        data = checkData(histoPojo.getData(), genericHistoCall.getFsym(), histoPojo.getTimeFrom(), histoPojo.getTimeTo());
        insertHistoData(data, genericHistoCall.getFsym());

//        // call the API w/ limit = timeDiff (this specifies # of time increments to go back and get)
//        HistoPojo histoPojo = restTemplate.getForObject(
//                domain + "histohour?fsym=BTC&tsym=USD&limit=" + timeDiff +"&aggregate=1&e=CCCAGG",
//                HistoPojo.class);
//        insertHistoData(histoPojo.getData());
        return histoPojo;
    }

    /**
     * Method to retrieve all coins from api and add new coins to database
     * @return  returns CoinOuput from API call
     */
    public CoinOutput getCoin () {
        ArrayList<Coin> newData = new ArrayList<Coin>();
        CoinOutput coin = restTemplate.getForObject("https://www.cryptocompare.com/api/data/coinlist/", CoinOutput.class);
        newData = checkCoin(coin.getData());
        insertCoin(newData);

        return coin;
    }

    /**
     * Method that checks list of data against existing database and adds non existing coins to new arraylist
     * @param data      Hashmap of list of objects returned by API call
     * @return          Arraylist of Coin pojos that do not exist in database
     */
    public ArrayList<Coin> checkCoin(HashMap<String, Coin> data){
        ArrayList<Coin> inDB = new ArrayList<>();
        ArrayList<Coin> notInDB = new ArrayList<>();
        inDB = mapper.getAllCoins();

        for (Coin item : data.values()) {
            Boolean existInDB = false;
            for (Coin checkDB : inDB) {
                if (checkDB.getName()==item.getName()) {
                    existInDB = true;
                    break;
                }
            }
            if (!existInDB){
                notInDB.add(item);
            }
        }
        return notInDB;
    }

    /**
     * Method to insert coin into database
     * @param data  Arraylist of coins that does not exist in database
     */
    public void insertCoin (ArrayList<Coin> data){
        for (Coin item : data){
            mapper.insertCoinInfo(item);
        }
    }
}
