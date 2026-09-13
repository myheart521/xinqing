package com.hpu.xinqing.service.serviceImpl;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.hpu.xinqing.mapper.IntroductionMapper;
import com.hpu.xinqing.service.IntroductionService;
import com.hpu.xinqingpojo.DTO.IntroductionPCCDTO;
import com.hpu.xinqingpojo.VO.GetPCCDatailVO;
import com.hpu.xinqingpojo.VO.GetPCCVO;
import com.hpu.xinqingpojo.entity.Doctor;
import com.hpu.xinqingpojo.entity.PsychologicalCounselingCenter;
import com.hpu.xinqingpojo.entity.TimeSlot;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class IntroductionServiceImpl implements IntroductionService {
    @Autowired
    IntroductionMapper introductionMapper;
    @Override
    @SaCheckRole(value = {"admin,student,teacher"},mode = SaMode.OR)
    public List<GetPCCVO> findPCCs(IntroductionPCCDTO introductionPCCDTO) {
        List<PsychologicalCounselingCenter> PCCList = introductionMapper.findPCCs(introductionPCCDTO);
        List<GetPCCVO> pccvos = new ArrayList<>();
        for (PsychologicalCounselingCenter pcc : PCCList)
        {
            GetPCCVO getPCCVO = new GetPCCVO();
            BeanUtils.copyProperties(pcc,getPCCVO);
            pccvos.add(getPCCVO);
        }
        return pccvos;
    }

    @Override
    public GetPCCDatailVO findDatial(Long id) {

        PsychologicalCounselingCenter newPcc = introductionMapper.findPCCById(id);
        List<Doctor> doctors =  introductionMapper.findDoctors(id);
        List<TimeSlot> timeSlots = introductionMapper.findTime(id);
        GetPCCDatailVO getPCCDatailVO = new GetPCCDatailVO();
        BeanUtils.copyProperties(newPcc,getPCCDatailVO);
        getPCCDatailVO.setDoctorList(doctors);
        getPCCDatailVO.setTimeSlots(timeSlots);
        return getPCCDatailVO;
    }
}
