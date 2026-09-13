package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.NearService;
import com.hpu.xinqingcommon.result.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 附近 前端控制器
 * </p>
 *
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/near")
public class NearController {
    @Resource
    private NearService nearService;

    @GetMapping("/user")
    public Result queryUserByGeo(
//            @RequestParam("userId") Long userId,
//            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "latitude",required = false) Double latitude,//维度
            @RequestParam(value = "longitude",required = false/*表示可以为空*/) Double longitude//经度
    ) {
        return nearService.queryUserByGeo(longitude,latitude);

    }

    @GetMapping("/blog")
    public Result queryBlogByGeo(
//            @RequestParam("userId") Long userId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "latitude",required = false) Double latitude,//维度
            @RequestParam(value = "longitude",required = false/*表示可以为空*/) Double longitude//经度
    ) {
        return nearService.queryBlogByGeo(current,longitude,latitude);
    }
}
