package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.CommentAnalyseDTO;
import com.hpu.xinqingpojo.VO.CommentVO;
import com.hpu.xinqingpojo.entity.Comments;

import java.util.List;

/**
 * <p>
 * 评论表,包括多级评论 服务类
 * </p>
 *
 * @since 2025-02-18
 */
public interface ICommentsService extends IService<Comments> {

    Result add(Comments comments);

    Result delect(Long id);

    Result likeCommentById(Long id);

    List<CommentVO> queryFirstComment(Long blogId, Integer current);

    //添加评论的用户信息
    void queryCommentUser(CommentVO commentVO);

    //判断用户是否点赞
    boolean isCommentLike(Long commentId);


    List<CommentVO> queryTwoComment(Long parentId, Integer current);

    PageResult queryBlogByUserId(Integer current);

    //根据用户id获取一定数量的用户发布的评论
    List<Comments> queryCommentByUserId(Long userId, Integer size);

    List<CommentAnalyseDTO> queryCommentRecentByUserId(Long userId);
}
