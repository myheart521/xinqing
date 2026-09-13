package com.hpu.xinqing.controller;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.hpu.xinqing.service.IntroductionService;
import com.hpu.xinqingpojo.DTO.IntroductionPCCDTO;
import com.hpu.xinqingpojo.VO.GetPCCDatailVO;
import com.hpu.xinqingpojo.VO.GetPCCVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hpu.xinqingcommon.result.Result;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/introduction")
public class IntroductionController {
    @Autowired
    private IntroductionService introductionService;
    @SaCheckRole(value = {"student","teacher","admin"},mode = SaMode.OR)
    @GetMapping("/pcc")
    public Result<List<GetPCCVO>> getPCC(IntroductionPCCDTO introductionPCCDTO)
    {
        log.info("心理咨询中心查询{}",introductionPCCDTO);
        return Result.success(introductionService.findPCCs(introductionPCCDTO));
    }
    @SaCheckRole(value = {"student","teacher"},mode = SaMode.OR)
    @GetMapping("/pcc/{id}")
    public Result<GetPCCDatailVO> getPCC(@PathVariable Long id)
    {
        log.info("心理咨询中心具体细节查询，id为：{}",id);
        return Result.success(introductionService.findDatial(id));
    }
}
