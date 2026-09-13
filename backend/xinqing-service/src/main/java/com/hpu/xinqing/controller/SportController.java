package com.hpu.xinqing.controller;

import com.hpu.xinqing.aspect.Log;
import com.hpu.xinqing.service.SportService;
import com.hpu.xinqing.service.UserAndSportService;
import com.hpu.xinqing.utils.standard.ToStandard;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.DTO.SportDTO;
import com.hpu.xinqingpojo.VO.SportVO;
import com.hpu.xinqingpojo.VO.SportVideoVO;
import com.hpu.xinqingpojo.entity.UserAndSport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hpu.xinqingcommon.result.Result;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/sport")
@Slf4j
@Log
public class SportController {
    @Autowired
    SportService sportService;
    @Autowired
    UserAndSportService userAndSportService;

    @Autowired
    ToStandard toStandard;
    /**
     * 运动查询，名称，难度，时间
     * @param sportDTO
     * @return
     */
    @PostMapping
    public Result select(@RequestBody SportDTO sportDTO){
        log.info("运动查询，参数为：{}",sportDTO);
        List<SportVO> list=sportService.select(sportDTO);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id){
        log.info("查询运动信息的id为：{}",id);
        SportVideoVO sportVideoVO=sportService.selectById(id);
        return Result.success(sportVideoVO);
    }

    @PostMapping("watch/{sportId}")
    public Result<?> watch(@PathVariable Long sportId){
        Long userId = StpUtils.userId();
        userAndSportService.watch(userId,sportId);
        return Result.success("新增成功");
    }


    @GetMapping("watch/history")
    public Result<?> watchHistory(){
        Long userId = StpUtils.userId();
        List<UserAndSport> userAndSports = userAndSportService.watchHistory(userId);
        return Result.success(toStandard.userAndSports(userAndSports));
    }

    @GetMapping("watch/graph/{scale}")
    public Result<?> watchGraph(@PathVariable Integer scale){
        Long userId = StpUtils.userId();
        List<UserAndSport> userAndSports = userAndSportService.watchHistoryByScale(userId,scale);
        return Result.success(toStandard.userAndSportGraph(userAndSports));
    }

}
