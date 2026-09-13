package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.BlogDTO;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.entity.Blog;

import java.util.List;

/**
 * <p>
 * 帖子 服务类
 * </p>
 *
 * @since 2025-02-18
 */
public interface IBlogService extends IService<Blog> {

    Result add(BlogDTO blogDTO);

    Result selectById(Long id);

    Result likeBlogById(Long id);

    Result delectById(Long id);
    void queryBolgUser(BlogVO blogVO);

    boolean isBolgLike(Long blogId);

    List<String> queryLikedUser(Long id);

    Result searchByTag(String tag,Long circleId,Integer current);

    Result searchByContent(String text,Long circleId, Integer current);

    List<BlogVO> selectByUserId(Long userId,Integer current);

    //获取count数目的博客VO
    List<BlogVO> selectBlogByUserId(Long userId,Integer count);

    Result queryUserDate(Long userId);

    Result queryUserDateMessage(Long userId);

    List<Long> selectListByUserId(Long user);

    PageResult queryMyLike(Integer current);

    //获取用户发表的博客数量
    Long queryBlogCount(Long userId);


    BlogVO getBlogVOById(Long id);
}
