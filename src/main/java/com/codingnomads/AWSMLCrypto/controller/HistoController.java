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
            @RequestParam (value = "type", required = true, defaultValue = "histohour")String type,
            @RequestParam (value = "fsym", required = true, defaultValue = "BTC")String fsym,
            @RequestParam (value = "tsym", required = true, defaultValue = "USD")String tsym,
            @RequestParam (value = "e", required = true, defaultValue = "CCCAGG")String e,
            @RequestParam (value = "extraParams", required = false)String extraParams,
            @RequestParam (value = "sign", required = false, defaultValue = "false")Boolean sign,
            @RequestParam (value = "tryConversion", required = false, defaultValue = "true")Boolean tryConversion,
            @RequestParam (value = "aggregate", required = false)Integer aggregate,
            @RequestParam (value = "limit", required = false)Integer limit,
            @RequestParam (value = "toTs", required = false)Timestamp toTs,
            @RequestParam (value = "allData", required = false, defaultValue = "false")Boolean allData

    ){ return histoService.getHistoData(type,fsym,tsym,e,extraParams,sign, tryConversion, aggregate,limit,toTs,allData);
    }

    @RequestMapping("/backload")
    public HistoPojo backloader(){
        return histoService.getBackload();
    }

}
