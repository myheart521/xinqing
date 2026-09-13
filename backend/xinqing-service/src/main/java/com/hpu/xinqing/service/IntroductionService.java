package com.hpu.xinqing.service;


import com.hpu.xinqingpojo.DTO.IntroductionPCCDTO;
import com.hpu.xinqingpojo.VO.GetPCCDatailVO;
import com.hpu.xinqingpojo.VO.GetPCCVO;

import java.util.List;

public interface IntroductionService {
    List<GetPCCVO> findPCCs(IntroductionPCCDTO introductionPCCDTO);

    GetPCCDatailVO findDatial(Long id);
}
