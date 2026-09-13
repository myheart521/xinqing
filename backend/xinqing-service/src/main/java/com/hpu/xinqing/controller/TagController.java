package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.ITagService;
import com.hpu.xinqingcommon.result.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 标签 前端控制器
 * </p>
 *
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    public ITagService tagService;

    //查询热门标签
    @GetMapping("/getTop")
    public Result queryTagTop(){
        return tagService.getTop();
    }

    //查询全部标签
    @GetMapping("/all")
    public Result queryTagAll(){
        return tagService.getAll();
    }

}
