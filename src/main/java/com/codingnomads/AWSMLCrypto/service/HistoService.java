package com.codingnomads.AWSMLCrypto.service;

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

    public HistoPojo getHistoDay() {

        HistoPojo histoPojo = restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/histoday?fsym=BTC&tsym=USD&limit=365&aggregate=1&e=CCCAGG", HistoPojo.class);
        return histoPojo;
    }

    //TODO Possibly add after HistoDay is fully functional
//    public HistoPojo getHistoHour() {
//
//        HistoPojo histoPojo = restTemplate.getForObject(
//                "https://min-api.cryptocompare.com/data/histohour", HistoPojo.class);
//        return histoPojo;
//    }
//
//    public HistoPojo getHistoMin() {
//
//        HistoPojo histoPojo = restTemplate.getForObject(
//                "https://min-api.cryptocompare.com/data/histominute", HistoPojo.class);
//        return histoPojo;
//    }

}
