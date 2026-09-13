package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.IBlogService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.BlogDTO;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.entity.Blog;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 帖子 前端控制器
 * </p>
 *
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;

    //发布动态
    @PostMapping("/add")
    public Result add(@RequestBody BlogDTO blogDTO){
        return blogService.add(blogDTO);
    }

    //删除动态
    @DeleteMapping("/{id}")
    public Result delectById(@PathVariable Long id){
       return blogService.delectById(id);
    }

    //查询动态详细信息
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id){
        return blogService.selectById(id);
    }

    //根据标签动态搜索
    @GetMapping("/searchTag")
    public Result searchByTag(
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "circleId",required = false) Long circleId,
            @RequestParam(value = "current", defaultValue = "1") Integer current){
        return blogService.searchByTag(tag,circleId,current);
    }

    //根据内容动态搜索
    @GetMapping("/searchContent")
    public Result searchByContent(
            @RequestParam(value = "text",required = false) String text,
            @RequestParam(value = "circleId",required = false) Long circleId,
            @RequestParam(value = "current", defaultValue = "1") Integer current){
        return blogService.searchByContent(text,circleId,current);
    }

    //根据用户id查询
    @GetMapping("/searchUserId")
    public Result selectByUserId(
            @RequestParam Long userId,
            @RequestParam(value = "current", defaultValue = "1") Integer current){
        List<BlogVO> list= blogService.selectByUserId(userId,current);
        return Result.success(list);
    }

    //查询我的圈子的用户信息
    @GetMapping("/user/{userId}")
    public Result queryUserDate(@PathVariable Long userId){
        return blogService.queryUserDate(userId);
    }

    //查询我的圈子的的数据信息
    @GetMapping("/dateMessage/{userId}")
    public Result queryUserdataMessage(@PathVariable Long userId){
        return blogService.queryUserDateMessage(userId);
    }

    //对动态点赞
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlogById(id);
    }

    //查询点赞用户前10名
    @GetMapping("/likes/{id}")
    public Result queryLikedUser(@PathVariable Long id){
        return Result.success(blogService.queryLikedUser(id));
    }

    //查询我点赞的动态,废弃了
    @GetMapping("/myLike")
    public Result queryMyLike(@RequestParam(value = "current", defaultValue = "1") Integer current){
//        PageResult page= blogService.queryMyLike(current);
        return Result.success(null);
    }

}
