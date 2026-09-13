package com.hpu.xinqing.mapper;

import com.hpu.xinqingpojo.DTO.IntroductionPCCDTO;
import com.hpu.xinqingpojo.entity.Doctor;
import com.hpu.xinqingpojo.entity.PsychologicalCounselingCenter;
import com.hpu.xinqingpojo.entity.TimeSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IntroductionMapper {

    List<PsychologicalCounselingCenter> findPCCs(IntroductionPCCDTO psychologicalCounselingCenter);
    @Select("select * from doctor where pcc_id = #{pccId}")
    List<Doctor> findDoctors(Long pccId);

    @Select("select * from time_slot where pcc_id=#{pccId}")
    List<TimeSlot> findTime(Long pccId);
    @Select("select * from psychological_counseling_center where id = #{id}")
    PsychologicalCounselingCenter findPCCById(Long id);
}
