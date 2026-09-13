package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.ISleepAudiosService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.SleepAudiosVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 助眠 前端控制器
 * </p>
 *
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/sleep-audios")
public class SleepAudiosController {

    @Autowired
    private ISleepAudiosService sleepAudiosService;

    @GetMapping
    public Result select(){
        List<SleepAudiosVO> list=sleepAudiosService.selectAll();
        return Result.success(list);
    }

    @PutMapping("/{id}")
    public Result updatePlay(@PathVariable Long id){
        sleepAudiosService.update().setSql("plays=plays+1").eq("id",id).update();
        return Result.success();
    }

}
