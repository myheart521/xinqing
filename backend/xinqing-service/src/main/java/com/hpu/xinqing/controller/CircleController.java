package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.ICircleService;
import com.hpu.xinqingcommon.result.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 圈子 前端控制器
 * </p>
 *
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/circle")
public class CircleController {
    @Resource
    private ICircleService circleService;

    //获取精品圈子
    @GetMapping("/getTop")
    public Result getTop(){
        return circleService.getTop();
    }

    //获取所有圈子
    @GetMapping("/getAll")
    public Result getAll(){
        return circleService.getAll();
    }

    //根据id查询圈子
    @GetMapping("/{id}")
    public Result queryById(@PathVariable Long id){
        return circleService.queryById(id);
    }
}
