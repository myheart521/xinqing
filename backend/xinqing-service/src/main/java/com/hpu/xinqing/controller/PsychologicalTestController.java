package com.hpu.xinqing.controller;

import com.alibaba.fastjson.JSONObject;
import com.hpu.xinqing.service.PsychologicalTestService;
import com.hpu.xinqingpojo.entity.PsychologicalTest;
import com.hpu.xinqingpojo.VO.TestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hpu.xinqingcommon.result.Result;

import java.util.List;
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/test")
public class PsychologicalTestController {

    @Autowired
    private PsychologicalTestService psychologicalTestService;

    @GetMapping("/getAll")
    public Result<List<PsychologicalTest>> getAll()
    {
        log.info("获取全部的心理测试试题");
        return Result.success(psychologicalTestService.findAll());
    }

    @PostMapping("/submit")
    public Result<TestVO> submit(@RequestBody JSONObject json)
    {
        String ans = json.getString("ans");
        return Result.success(psychologicalTestService.calculateAns(ans));
    }
}
