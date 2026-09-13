package com.hpu.xinqing.controller;

import com.hpu.xinqing.service.MusicService;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.MusicVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hpu.xinqingcommon.result.Result;

import java.util.List;
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/music")
public class MusicController {
    @Autowired
    private MusicService musicService ;

    @GetMapping("/pages")
    public Result getMusicPages(PageDTO pageDTO){
        List<MusicVo> dietVOS= musicService.pageQuery(pageDTO);
        return Result.success(dietVOS);
    }

}
