package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.HistoryTestService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.AnswerVO;
import com.hpu.xinqingpojo.VO.HistoryTestPageVO;
import com.hpu.xinqingpojo.VO.HistoryTestReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historyTest")
@Slf4j
public class HistoryTestController {

    @Autowired
    private HistoryTestService historyTestService;

    /***
     * 分页查询获取登陆的人近几次测评记录
     * @param pageDTO
     * @return
     */
    @GetMapping("/pages")
    public Result<List<HistoryTestPageVO>> getHistoryRecent(PageDTO pageDTO)
    {
        // 分页获取测试记录模块
        return Result.success(historyTestService.getHistoryPage(pageDTO));
    }


    /***
     * 获取某次测评记录的细节
     * @return
     */
    @GetMapping("/pages/{id}")
    public Result<AnswerVO> getHistoryTestDetail(@PathVariable("id") Long id)
    {
        AnswerVO answerVO = historyTestService.getDetail(id);

        return Result.success(answerVO);
    }



    //获取登陆者近几次测评记录，绘制折线图
    @GetMapping("/dataReport")
    public Result<HistoryTestReportVO> dataReport(Long moduleId,Long recent)
    {
        if (recent<=0){
            throw new RuntimeException("recent应该大于0");
        }
        HistoryTestReportVO historyTestReportVO=historyTestService.dataReport(moduleId,recent);
        return Result.success(historyTestReportVO);
    }

    @GetMapping("/admin/dataReport")
    public Result<List<HistoryTestReportVO>> adminGetDataReport(@RequestParam(required = false) Long recent, Long studentId)
    {
        recent=recent==null?5:recent;
        List<HistoryTestReportVO> historyTestReportVO=historyTestService.dataReportByStudentId(recent,studentId);
        return Result.success(historyTestReportVO);
    }

}
