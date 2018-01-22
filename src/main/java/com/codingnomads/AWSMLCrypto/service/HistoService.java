package com.codingnomads.AWSMLCrypto.service;

import com.codingnomads.AWSMLCrypto.mapper.TableMapper;
import com.codingnomads.AWSMLCrypto.model.*;
//import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Service methods for cryptocompare api calls.
 */

@Service
public class HistoService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TableMapper mapper;

    String domain = "https://min-api.cryptocompare.com/data/";

    /**
     * Creates a generic historical data api call from cryptocompare.com depending on the parameters included
     * By default histohour is set as default historical data type, BTC is set as default fsym, USD is set as default tsym
     *
     * @param type          Type of  histodata call being made; histoday, histominute, histohour, default value histohour - required
     * @param fsym          Name of From Symbol, default value BTC - required
     * @param tsym          Name of To Symbol, default value USD -required
     * @param e             Name of exchange, default value CCCAGG - required
     * @param extraParams   Name of your application - optional
     * @param sign          If set to true, the server will sign the requests. default value false - optional
     * @param tryConversion If set to false, it will try to get values without using any conversion at all, default value true - optional
     * @param aggregate    Number of aggregate to query by, default value 1 - optional
     * @param limit         Limit of results to return - optional
     * @param toTs          toTimestamp - optional
     * @return              Histopojo object from API call
     */
    public HistoPojo getHistoData(String type, String fsym, String tsym, String e, String extraParams, Boolean sign,
                                  Boolean tryConversion, Integer aggregate, Integer limit, long toTs){

        //creates a new Arraylist of type Data to store new Data for insertion into database
        ArrayList<Data> newData = new ArrayList<Data>();
        //create a generic HistoDataCall based on parameters passed by API call
        GenericHistoCall genericHistoCall = new GenericHistoCall(type, fsym, tsym, e, extraParams, sign, tryConversion,
                aggregate, limit, toTs);
        //domainParams creates a string url based on parameters passed in through API call and passes it to resttemplate call
        HistoPojo histoPojo = restTemplate.getForObject(genericHistoCall.domainParams(), HistoPojo.class);
        //checks data and separates out non existing data compared to DB into new data array
        newData = checkData(histoPojo.getData(), genericHistoCall.getFsym(), histoPojo.getTimeFrom(), histoPojo.getTimeTo());
//        inserts new data into database
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
        if (inDB.size() == 0){
            for (Data item: data){
                notInDB.add(item);
            }
            return notInDB;
        }
        //if inDB contains data, checks all items in data against inDB for existing time stamps and changes boolean existInDB,
        else {
            for (Data item: data) {
                boolean existInDB = false;
                long itemTime = item.getUnixTime();
                for (Data checkDB : inDB) {
                    long checkDBTime = checkDB.getUnixTime();
                    if (itemTime == checkDBTime) {
                        existInDB = true;
                        break;
                    }
                }
                // if existInDB is false, adds non-existing data into notInDB arraylist
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
     * @param type          Type of  histodata call being made; histoday, histominute, histohour, default value histohour - required
     * @param fsym          Name of From Symbol, default value BTC - required
     * @param tsym          Name of To Symbol, default value USD -required
     * @param e             Name of exchange, default value CCCAGG - required
     * @param extraParams   Name of your application - optional
     * @param sign          If set to true, the server will sign the requests. default value false - optional
     * @param tryConversion If set to false, it will try to get values without using any conversion at all, default value true - optional
     * @param aggregate    Number of aggregate to query by, default value 1 - optional
     * @param limit         Limit of results to return - optional
     * @param toTs          toTimestamp - optional
     * @return              Histopojo object from API call
     */
    public HistoPojo getBackload(String type, String fsym, String tsym, String e, String extraParams, Boolean sign,
                                 Boolean tryConversion, Integer aggregate, Integer limit, long toTs) {
        // get the most recent time that data was captured(in unixtime-seconds, convert to hours)
        long latestTime = (mapper.selectLatestTime()) / 3600;
        // get current time (in ms, convert to secs)
        long currentTimeSec =(long) (System.currentTimeMillis() * .001);
        // convert to hours
        long currentTimeHrs = currentTimeSec / 3600;
        // # of time increments between latest and current time (here we're using hrs)
        long timeDiff = (currentTimeHrs - latestTime);
        // makes API call with requested parameters and sets limit to the timeDiff in hours
        getHistoData(type, fsym, tsym, e, extraParams, sign, tryConversion, aggregate,(int)timeDiff, toTs);
        return getHistoData(type, fsym, tsym, e, extraParams, sign, tryConversion, aggregate,(int)timeDiff, toTs);
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
        //create inDB arraylist to store database coinlist
        ArrayList<Coin> inDB = new ArrayList<>();
        //create notInDB arraylist to store new coin data
        ArrayList<Coin> notInDB = new ArrayList<>();
        //query database to get all coins and store into inDB
        inDB = mapper.getAllCoins();
        //checks each value in hashmap data for coin name and compares to inDB arraylist
        for (Coin item : data.values()) {
            Boolean existInDB = false;
            String itemName = item.getName();
            for (Coin checkDB : inDB) {
                String checkDBName = checkDB.getName();
                //if name exists in database sets existInDB true and breaks out of for loop
                if (checkDBName == itemName) {
                    existInDB = true;
                    break;
                }
            }
            //if existInDB false, adds Coin data to notInDB arraylist
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

    /**
     *  This method is used to backload at least a year's worth of data for the requested cryptocurrency.  Extra data will be
     *  aggregated depending on the limit selected for results.  This method should be ran first to build initial database
     *
     * @param type          Type of  histodata call being made; histoday, histominute, histohour, default value histohour - required
     * @param fsym          Name of From Symbol, default value BTC - required
     * @param tsym          Name of To Symbol, default value USD -required
     * @param e             Name of exchange, default value CCCAGG - required
     * @param extraParams   Name of your application - optional
     * @param sign          If set to true, the server will sign the requests. default value false - optional
     * @param tryConversion If set to false, it will try to get values without using any conversion at all, default value true - optional
     * @param aggregate    Number of aggregate to query by, default value 1 - optional
     * @param limit         Limit of results to return - optional
     * @param toTs          toTimestamp - optional
     * @param year          Year(s) to change current year by for required backload of data, default value -1 (previous year)
     * @return              Arraylist of each Histopojo object made for each API call until time achieved
     *
     */
    public ArrayList<HistoPojo> getbackloadYear(String type, String fsym, String tsym, String e, String extraParams, Boolean sign,
                             Boolean tryConversion, Integer aggregate, Integer limit, long toTs, int year){
        //gets current calendar date
        Calendar cal = Calendar.getInstance();
        //gets current unixTime
        long unixTimeCurrent = cal.getTime().getTime();
        //adds year parameter change to current calendar year for time requested
        cal.add(Calendar.YEAR, year);
        //gets previous calendar date to unixtime
        long unixTimePreviousYear = cal.getTime().getTime()/1000;
        //new histopojo to store all data retrieved
        ArrayList<HistoPojo> histoPojoCompiled = new ArrayList<>();
        //sets time equal to current time
        long time = unixTimeCurrent;
        //while time is greater than previous year
        while (time >= unixTimePreviousYear){
            HistoPojo histoPojo = new HistoPojo();
            //makes api call with given parameters
            histoPojo = getHistoData(type, fsym, tsym, e, extraParams, sign, tryConversion, aggregate, limit, time);
            //adds histopojo to arraylist
            histoPojoCompiled.add(histoPojo);
            //gets timeFrom histopojo and assigns to time
            time = histoPojo.getTimeFrom();
        }
        return histoPojoCompiled;
    }
}
