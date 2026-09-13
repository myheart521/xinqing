package com.hpu.xinqing.controller;

import com.hpu.xinqing.service.ModulesService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.ModulesPageVO;
import com.hpu.xinqingpojo.entity.Modules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@CrossOrigin
@RequestMapping("modules/")
public class ModulesController {
    //分页查询不同的模块
    @Autowired
    private ModulesService modulesService;
    @GetMapping("/pages")
    public Result<List<ModulesPageVO>> getDefaultTest(PageDTO pageDTO)
    {
        // 分页获取测试题模块
        return Result.success(modulesService.getTestByPages(pageDTO));
    }


}
