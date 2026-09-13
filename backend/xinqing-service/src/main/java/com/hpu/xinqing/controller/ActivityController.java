package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.IActivityService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.ActivityDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 活动 前端控制器
 * </p>
 *
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Resource
    private IActivityService activityService;



    @PostMapping("/add")
    public Result add(@RequestBody ActivityDTO activityDTO){
        return activityService.add(activityDTO);
    }

    //删除活动
    @DeleteMapping("/{id}")
    public Result delectById(@PathVariable Long id){
        return activityService.delectById(id);
    }

    //查询活动详细信息
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id){
        return activityService.selectById(id);
    }

    @GetMapping("/all")
    public Result selectAll(
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ){
        return activityService.selectAll(current);
    }
}
