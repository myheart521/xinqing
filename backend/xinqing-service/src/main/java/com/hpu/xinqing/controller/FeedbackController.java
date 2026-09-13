package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.IFeedbackService;
import com.hpu.xinqing.utils.standard.ToStandard;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.FeedbackDTO;
import com.hpu.xinqingpojo.DTO.FeedbackPage;
import com.hpu.xinqingpojo.entity.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 反馈表 前端控制器
 * </p>
 *
 * @since 2025-04-07
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;
    @Autowired
    private ToStandard toStandard;

    @PostMapping("/create")
    public Result<?> create(@RequestBody FeedbackDTO feedbackDTO) {
        return feedbackService.create(feedbackDTO);
    }


    @PostMapping("/admin/list")
    public Result<?> list(@RequestBody FeedbackPage feedback) {
        PageResult<Feedback> page=feedbackService.getList(feedback);
        return Result.success(toStandard.standardFeedback(page));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Feedback feedback = feedbackService.getById(id);
        return Result.success(toStandard.standardFeedbackVO(feedback));
    }


}
