package com.codingnomads.AWSMLCrypto.service;

import com.codingnomads.AWSMLCrypto.mapper.TestTableMapper;
import com.codingnomads.AWSMLCrypto.model.Data;
import com.codingnomads.AWSMLCrypto.model.HistoPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * created by Jialor Cheung on 12/19/17
 */

@Service
public class HistoService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TestTableMapper mapper;

    String endpoint = "https://min-api.cryptocompare.com/data/";

    public HistoPojo getHistoDay() {

        //rest API call
        HistoPojo histoPojo = restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/histoday?fsym=BTC&tsym=USD&limit=365&aggregate=1&e=CCCAGG", HistoPojo.class);
        //insert data into database
        insertHistoData(histoPojo.getData());
        return histoPojo;
    }

    public HistoPojo getHistoHour() {

        HistoPojo histoPojo = restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/histohour?fsym=BTC&tsym=USD&limit=2000&aggregate=1&e=CCCAGG", HistoPojo.class);
        insertHistoData(histoPojo.getData());
        return histoPojo;
    }

    public HistoPojo getHistoMin() {

        HistoPojo histoPojo = restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/histominute?fsym=BTC&tsym=USD&limit=2000&aggregate=1&e=CCCAGG", HistoPojo.class);
        insertHistoData(histoPojo.getData());
        return histoPojo;
    }

    //method to check if data exists in DB and if not insert data
    public void insertHistoData (Data[] data){

        Integer time;

        for(Data item: data){

            try {
                time = mapper.getTime(item.getTime());
            } catch (Exception e) {
                time = null;
            }
            // check to see if the time already exists in our DB
            if (time == null) {
                mapper.insertData(item);
            }
        }
    }

    public HistoPojo getBackload() {

        //rest API call - need to make limit a url queryparam that's determined by the last time stamp (TimeTo)
        // in the db, so it only goes back that far plus the time increment we're using (minutes, hours, etc)
        // Seems we can't say the time to start from (ie anything other than right now) so this and real time
        // will have to start at the exact same time to avoid any gaps in data
        // want all time in mins (if that's the call we end up using)

        // get the last time that data was captured(in unixtime-seconds), convert to mins
        long latestTime = mapper.selectLatestTime();

        // get current time in ms, convert to mins
        // not sure how to handle switch over to real time data so we don't miss a minute or double up on a min
        long currentTime = (long) ((System.currentTimeMillis() * .001) / 60);
        System.out.println(currentTime);

        // # of time increments between latest and current time (here we're using mins)
        long timeDiff = (currentTime - latestTime);

        // call the API w/ limit = timeDiff (this specifies # of time increments we want to go back and get)
        HistoPojo histoPojo = restTemplate.getForObject(
                endpoint + "histoday?fsym=BTC&tsym=USD&limit=" + timeDiff +"&aggregate=1&e=CCCAGG", HistoPojo.class);
//        //insert data into database
//
//        Data[] dataobj = histoPojo.getData();
//        for ( Data item : dataobj) {
//            mapper.insertData(item);
//        }
//
//
        return histoPojo;
    }
}
