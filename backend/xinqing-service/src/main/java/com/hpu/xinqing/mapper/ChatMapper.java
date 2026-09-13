package com.hpu.xinqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.VO.DataVo;
import com.hpu.xinqingpojo.entity.Chat;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChatMapper extends BaseMapper<Chat> {

    List<DataVo> selectTopTeachersWithChatCounts();

    Integer selectConsultCount(@Param("date")  LocalDate date);
}
