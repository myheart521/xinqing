package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.ITypeService;
import com.hpu.xinqingcommon.constant.TypeContants;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 分类表 前端控制器
 * </p>
 *
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/type")
public class TypeController {

    @Autowired
    private ITypeService typeService;

    @GetMapping
    public Result selectAll(){
        List<Type> typeList = typeService.selectBySort(TypeContants.SLEEP);
        return Result.success(typeList);
    }

    @GetMapping("/feedback")
    public Result<?> getFeedback(){
        List<Type> typeList = typeService.selectBySort(TypeContants.FEEDBACK);
        return Result.success(typeList);
    }

}
