package com.hpu.xinqing.controller;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.hpu.xinqing.service.ModulesService;
import com.hpu.xinqing.service.TestService;
import com.hpu.xinqingpojo.DTO.QuestionSubmitDTO;
import com.hpu.xinqingpojo.VO.AnswerVO;
import com.hpu.xinqingpojo.VO.QuestionVO;
import com.hpu.xinqingpojo.entity.Modules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hpu.xinqingcommon.result.Result;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("questions/")
public class TestController {
    @Autowired
    private TestService testService;
    @GetMapping("/{id}")
    public Result<List<QuestionVO>> getAll(@PathVariable Long id)
    {
        log.info("查询指定模块的所有测试题");
        return Result.success(testService.findAll(id));
    }

    @PostMapping("/submit")
    public Result<AnswerVO> submit(@RequestBody QuestionSubmitDTO questionSubmitDTO)
    {
        log.info("计算试题结果");

        Long userId = Long.valueOf(StpUtil.getLoginId().toString());
//        Long userId = 2L;
        return Result.success(testService.calculateAns(questionSubmitDTO,userId));
    }


}
