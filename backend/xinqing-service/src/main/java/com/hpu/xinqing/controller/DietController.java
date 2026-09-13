package com.hpu.xinqing.controller;
import com.hpu.xinqing.service.DietService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.DietDetailVo;
import com.hpu.xinqingpojo.VO.DietVO;
import com.hpu.xinqingpojo.entity.Diet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/diet")
public class DietController {

    @Autowired
    private DietService dietService;

    @GetMapping("/pages")
    public Result getDietPages(PageDTO pageDTO){
        List<DietVO> dietVOS= dietService.pageQuery(pageDTO);
        return Result.success(dietVOS);
    }
    @GetMapping("/detail/{id}")
    public Result getDietPages(@PathVariable("id") Long id){
        Diet diet = dietService.getById(id);
        return Result.success(new DietDetailVo(diet.getTitle(),diet.getCreateTime(),diet.getContent()));
    }
}
