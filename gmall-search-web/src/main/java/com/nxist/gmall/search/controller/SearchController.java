package com.nxist.gmall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author YuanmaoXu
 * @date 2020/8/10 23:05
 */
@Controller
public class SearchController {

    @RequestMapping("list.html")
    public String list(String catalog3Id){
        return "index";
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }
}
