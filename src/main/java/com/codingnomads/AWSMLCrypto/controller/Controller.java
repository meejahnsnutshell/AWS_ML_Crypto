package com.codingnomads.AWSMLCrypto.controller;

import com.codingnomads.AWSMLCrypto.model.Data;
import com.codingnomads.AWSMLCrypto.model.Test;
import com.codingnomads.AWSMLCrypto.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class Controller {

    @Autowired
    TestService testService;

    @RequestMapping("/test")
    public ArrayList<Data> selectingAll (){
        return testService.selectAll();
    }

}
