package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.AnswerVO;
import com.hpu.xinqingpojo.VO.HistoryTestPageVO;
import com.hpu.xinqingpojo.VO.HistoryTestReportVO;
import com.hpu.xinqingpojo.entity.HistoryTest;

import java.util.List;

public interface HistoryTestService {
    List<HistoryTestPageVO> getHistoryPage(PageDTO pageDTO);

    AnswerVO getDetail(Long id);

    HistoryTestReportVO dataReport(Long moduleId, Long recent);


    /**
     * 指定数量获取，用户的测评记录
     */
    List<HistoryTest> getHistoryByUserId(Long userId, Integer count);

    /**
     * 获取用户的测评记录数量
     */
    Long getHistoryCount(Long userId);

    List<HistoryTestReportVO> dataReportByStudentId(Long recent, Long studentId);
}
