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
}
