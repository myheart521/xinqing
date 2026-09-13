package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.FeedbackDTO;
import com.hpu.xinqingpojo.DTO.FeedbackPage;
import com.hpu.xinqingpojo.entity.Feedback;

import java.util.List;

/**
 * <p>
 * 反馈表 服务类
 * </p>
 *
 * @since 2025-04-07
 */
public interface IFeedbackService extends IService<Feedback> {

    Result<?> create(FeedbackDTO feedbackDTO);

    PageResult<Feedback> getList(FeedbackPage feedback);
}
