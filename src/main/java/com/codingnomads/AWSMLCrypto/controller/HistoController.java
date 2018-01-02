package com.codingnomads.AWSMLCrypto.controller;


import com.codingnomads.AWSMLCrypto.model.HistoPojo;
import com.codingnomads.AWSMLCrypto.service.HistoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

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
            @RequestParam (value = "type", required = false, defaultValue = "histhour")String type,
            @RequestParam (value = "fsym", required = false, defaultValue = "BTC")String fsym,
            @RequestParam (value = "tsym", required = false, defaultValue = "USD")String tsym,
            @RequestParam (value = "e", required = false, defaultValue = "null")String e,
            @RequestParam (value = "extraParams", required = false, defaultValue = "null")String extraParams,
            @RequestParam (value = "sign", required = false, defaultValue = "false")Boolean sign,
            @RequestParam (value = "tryConversion", required = false, defaultValue = "false")Boolean tryConversion,
            @RequestParam (value = "aggregate", required = false, defaultValue = "null")Integer aggregate,
            @RequestParam (value = "limit", required = false, defaultValue = "null")Integer limit,
            @RequestParam (value = "toTs", required = false, defaultValue = "null")Timestamp toTs,
            @RequestParam (value = "allData", required = false, defaultValue = "false")Boolean allData

    ){ return histoService.getHistoData(type,fsym,tsym,e,extraParams,sign, tryConversion, aggregate,limit,toTs,allData);
    }

    @RequestMapping("/backload")
    public HistoPojo backloader(){
        return histoService.getBackload();
    }

}
