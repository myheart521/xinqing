package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.IActivityFollowService;
import com.hpu.xinqing.service.ICircleFollowService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 活动参与表 前端控制器
 * </p>
 *
 * @since 2025-02-27
 */
@RestController
@RequestMapping("/activity-follow")
public class ActivityFollowController {

    @Resource
    private IActivityFollowService activityFollowService;

    //查询是否参与
    @GetMapping("/or/not/{id}")
    public Result followOrNot(@PathVariable Long id){
        return Result.success(activityFollowService.queryFollow(id));
    }

    //参与,不参与
    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable Long id,@PathVariable Boolean isFollow){
        return activityFollowService.follow(id,isFollow);
    }

    //查询参与的活动
    @GetMapping("/list")
    public Result queryFollowList(
            @RequestParam(value = "current", defaultValue = "1") Integer current){
        PageResult page= activityFollowService.queryFollowList(current);
        return Result.success(page);

    }

}
