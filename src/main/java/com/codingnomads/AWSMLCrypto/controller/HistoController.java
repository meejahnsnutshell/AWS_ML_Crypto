package com.codingnomads.AWSMLCrypto.controller;


import com.codingnomads.AWSMLCrypto.model.CoinOutput;
import com.codingnomads.AWSMLCrypto.model.HistoPojo;
import com.codingnomads.AWSMLCrypto.service.HistoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * created by Jialor Cheung on 12/19/17
 */

@RequestMapping ("/histo")
@RestController
class HistoController {

    @Autowired
    HistoService histoService;

    @RequestMapping("/data")
    public HistoPojo getHistoData(
            @RequestParam (value = "type", required = true, defaultValue = "histohour")String type,
            @RequestParam (value = "fsym", required = true, defaultValue = "BTC")String fsym,
            @RequestParam (value = "tsym", required = true, defaultValue = "USD")String tsym,
            @RequestParam (value = "e", required = true, defaultValue = "CCCAGG")String e,
            @RequestParam (value = "extraParams", required = false)String extraParams,
            @RequestParam (value = "sign", required = false, defaultValue = "false")Boolean sign,
            @RequestParam (value = "tryConversion", required = false, defaultValue = "true")Boolean tryConversion,
            @RequestParam (value = "aggregate", required = false, defaultValue = "1")Integer aggregate,
            @RequestParam (value = "limit", required = false, defaultValue = "50")Integer limit,
            @RequestParam (value = "toTs", required = false, defaultValue = "0") long toTs

    ){ return histoService.getHistoData(type,fsym,tsym,e,extraParams,sign, tryConversion, aggregate,limit,toTs);
    }

    @RequestMapping("/backload")
    public HistoPojo backloader(
            @RequestParam (value = "type", required = true, defaultValue = "histohour")String type,
            @RequestParam (value = "fsym", required = true, defaultValue = "BTC")String fsym,
            @RequestParam (value = "tsym", required = true, defaultValue = "USD")String tsym,
            @RequestParam (value = "e", required = true, defaultValue = "CCCAGG")String e,
            @RequestParam (value = "extraParams", required = false)String extraParams,
            @RequestParam (value = "sign", required = false, defaultValue = "false")Boolean sign,
            @RequestParam (value = "tryConversion", required = false, defaultValue = "true")Boolean tryConversion,
            @RequestParam (value = "aggregate", required = false, defaultValue = "1")Integer aggregate,
            @RequestParam (value = "limit", required = false, defaultValue = "50")Integer limit,
            @RequestParam (value = "toTs", required = false, defaultValue = "0") long toTs
    ){
        return histoService.getBackload(type,fsym,tsym,e,extraParams,sign, tryConversion, aggregate,limit,toTs);
    }

    @RequestMapping("/coin")
    public CoinOutput loadCoin(){ return histoService.getCoin();
    }

    @RequestMapping("/backloadYear")
    public ArrayList<HistoPojo> backloaderYear(
            @RequestParam (value = "type", required = true, defaultValue = "histohour")String type,
            @RequestParam (value = "fsym", required = true, defaultValue = "BTC")String fsym,
            @RequestParam (value = "tsym", required = true, defaultValue = "USD")String tsym,
            @RequestParam (value = "e", required = true, defaultValue = "CCCAGG")String e,
            @RequestParam (value = "extraParams", required = false)String extraParams,
            @RequestParam (value = "sign", required = false, defaultValue = "false")Boolean sign,
            @RequestParam (value = "tryConversion", required = false, defaultValue = "true")Boolean tryConversion,
            @RequestParam (value = "aggregate", required = false, defaultValue = "1")Integer aggregate,
            @RequestParam (value = "limit", required = false, defaultValue = "50")Integer limit,
            @RequestParam (value = "toTs", required = false, defaultValue = "0") long toTs,
            @RequestParam (value = "year", required = false, defaultValue = "-1") int year
    ){
        return histoService.getbackloadYear(type,fsym,tsym,e,extraParams,sign, tryConversion, aggregate,limit,toTs, year);
    }
}
