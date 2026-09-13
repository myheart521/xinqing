package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.IFollowService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Follow;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户之间关注信息 前端控制器
 * </p>
 *
 * @since 2025-02-22
 */
@RestController
@RequestMapping("/fellow")
public class FollowController {
    @Resource
    private IFollowService followService;

    @GetMapping("/or/not/{id}")
    public Result followOrNot(@PathVariable Long id){
        return Result.success(followService.queryFollow(id));
    }

    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable Long id,@PathVariable Boolean isFollow){
        return followService.follow(id,isFollow);
    }

    //查询我关注的人列表
    @GetMapping("/follow/list")
    public Result followerList(  @RequestParam(value = "current", defaultValue = "1") Integer current){
        PageResult pageResult=followService.followerList(current);
        return Result.success(pageResult);
    }
}
