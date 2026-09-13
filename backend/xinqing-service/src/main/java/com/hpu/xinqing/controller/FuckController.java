package com.hpu.xinqing.controller;

import com.hpu.xinqing.aspect.Log;
import com.hpu.xinqingcommon.constant.CommonConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log(desc = "描述点东西\n",expression = "'"+CommonConstant.TOKEN_HEADER+"'+'这里是我的表达式, 我的入参是： '+ #arg")
@RequestMapping("/fuck")
public class FuckController {

//    @Log(expression = "#name")
    @GetMapping("a")
    public String yean(@RequestParam String name){
        System.out.println("yean");
        return "yean";
    }

//    @Log(desc = "描述点东西\n",expression = "'"+CommonConstant.TOKEN_HEADER+"'+'这里是我的表达式, 我的入参是： '+ #arg")
    @GetMapping("b")
    public String a(@RequestParam String arg){
        System.out.println("b");
        return "b";
    }
}
