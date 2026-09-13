package com.hpu.xinqing.service.serviceImpl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.FeedbackMapper;
import com.hpu.xinqing.service.IFeedbackService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingcommon.utils.StringUtils;
import com.hpu.xinqingpojo.DTO.FeedbackDTO;
import com.hpu.xinqingpojo.DTO.FeedbackPage;
import com.hpu.xinqingpojo.entity.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 反馈表 服务实现类
 * </p>
 *
 * @since 2025-04-07
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {

    @Autowired
    private UserService userService;

    @Override
    public Result<?> create(FeedbackDTO feedbackDTO) {
        Long userId = userService.checkLogin();
        Feedback feedback = BeanUtil.copyProperties(feedbackDTO, Feedback.class);
        feedback.setUserId(userId);
        feedback.setImages(StringUtils.listToImage(feedbackDTO.getImageList()));
        boolean save = save(feedback);
        if (save) {
            return Result.success("谢谢您的反馈");
        }
        return Result.error("抱歉反馈失败");
    }

    //查询反馈信息
    @Override
    public PageResult<Feedback> getList(FeedbackPage feedback) {
        Page<Feedback> page = lambdaQuery().eq(feedback.getTypeId() != null, Feedback::getTypeId, feedback.getTypeId())
                .like(feedback.getContent() != null, Feedback::getContent, feedback.getContent())
                .eq(feedback.getUserId() != null, Feedback::getUserId, feedback.getUserId())
                .between((feedback.getStartDateTime() != null && feedback.getEndDateTime() != null)
                        , Feedback::getCreateTime, feedback.getStartDateTime(), feedback.getEndDateTime())
                .page(new Page<>(feedback.getPageNo(), feedback.getPageSize()));
        List<Feedback> records = page.getRecords();
        if(records.isEmpty()){
            return PageResult.isEmpty();
        }
        return new PageResult<Feedback>(page.getTotal(), records);
    }
}
