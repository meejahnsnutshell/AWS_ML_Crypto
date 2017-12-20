package com.codingnomads.AWSMLCrypto.controller;


import com.codingnomads.AWSMLCrypto.model.HistoPojo;
import com.codingnomads.AWSMLCrypto.service.HistoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by Jialor Cheung on 12/19/17
 */

@RequestMapping ("/histo")
@RestController
class HistoController {

    @Autowired
    HistoService histoService;

    @RequestMapping("/day")
    public HistoPojo getHistoDay(){
        return histoService.getHistoDay();
    }

    //TODO Possibly add after HistoDay is fully functional
    @RequestMapping("/hour")
    public HistoPojo getHistoHour(){
        return histoService.getHistoHour();
    }

    @RequestMapping("/min")
    public HistoPojo getHistoMin(){
        return histoService.getHistoMin();
    }

}
