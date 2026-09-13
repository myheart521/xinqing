package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.VO.BlogMessageVO;
import com.hpu.xinqingpojo.VO.DataVo;
import com.hpu.xinqingpojo.entity.Blog;

import java.util.List;

/**
 * <p>
 * 帖子 Mapper 接口
 * </p>
 *
 * @since 2025-02-18
 */
public interface BlogMapper extends BaseMapper<Blog> {

    BlogMessageVO selectMessage(Long userId);

    List<DataVo> getPostData();
}
