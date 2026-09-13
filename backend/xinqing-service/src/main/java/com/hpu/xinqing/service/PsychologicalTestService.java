package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.entity.PsychologicalTest;
import com.hpu.xinqingpojo.VO.TestVO;

import java.util.List;

public interface PsychologicalTestService {
    List<PsychologicalTest> findAll();

    TestVO calculateAns(String ans);
}
