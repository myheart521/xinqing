package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.ICommentsService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.CommentVO;
import com.hpu.xinqingpojo.entity.Comments;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 评论表,包括多级评论 前端控制器
 * </p>
 *
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Resource
    private ICommentsService commentsService;

    @PostMapping("/add")
    public Result add(@RequestBody Comments comments){
        return commentsService.add(comments);
    }

    @DeleteMapping("/delect/{id}")
    public Result delectById(@PathVariable Long id){
        return commentsService.delect(id);
    }

    //对评论点赞
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        return commentsService.likeCommentById(id);
    }

    //分页查询一级评论,同时下拉刷新
    @GetMapping("/first")
    public Result selectFirstComments(
            @RequestParam("blogId") Long blogId,
            @RequestParam(value = "current", defaultValue = "1") Integer current){
        List<CommentVO> list= commentsService.queryFirstComment(blogId,current);
        return Result.success(list);
    }

    //查询子评论
    @GetMapping("/two")
    public Result selectTwoComment(
            @RequestParam("parentId") Long parentId,
            @RequestParam(value = "current", defaultValue = "1") Integer current){
        List<CommentVO>list=commentsService.queryTwoComment(parentId,current);
        return Result.success(list);
    }

    //查询用户评论的相关动态
    @GetMapping("/forUser")
    public Result selectBlogByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current){
        PageResult page=commentsService.queryBlogByUserId(current);
        return Result.success(page);
    }

}
