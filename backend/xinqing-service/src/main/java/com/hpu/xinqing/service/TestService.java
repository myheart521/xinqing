package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.DTO.QuestionSubmitDTO;
import com.hpu.xinqingpojo.VO.AnswerVO;
import com.hpu.xinqingpojo.VO.QuestionVO;

import java.util.List;

public interface TestService {
    //查找全部
    List<QuestionVO> findAll(Long id);
    // 计算答案
    AnswerVO calculateAns(QuestionSubmitDTO questionSubmitDTO,Long userId);
}
