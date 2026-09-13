package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.ICircleFollowService;
import com.hpu.xinqing.service.IFollowService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户关注圈子信息表 前端控制器
 * </p>
 *
 * @since 2025-02-22
 */
@RestController
@RequestMapping("/circle-follow")
public class CircleFollowController {
    @Resource
    private ICircleFollowService circleFollowService;

    @GetMapping("/or/not/{id}")
    public Result followOrNot(@PathVariable Long id){
        return Result.success(circleFollowService.queryFollow(id));
    }

    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable Long id,@PathVariable Boolean isFollow){
        return circleFollowService.follow(id,isFollow);
    }

    //查询我关注的圈子
    @GetMapping("/list")
    public Result getList(
            @RequestParam(value = "current", defaultValue = "1") Integer current){

        PageResult page= circleFollowService.selectList(current);
        return Result.success(page);
    }

}
