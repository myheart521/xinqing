package com.hpu.xinqing.mapper;

import com.hpu.xinqingpojo.DTO.SportDTO;
import com.hpu.xinqingpojo.VO.SportVO;
import com.hpu.xinqingpojo.VO.SportVideoVO;
import com.hpu.xinqingpojo.entity.Sport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SportMapper {
    /**
     * 运动查询
     * @param sportDTO
     * @return
     */
    List<SportVO> select(SportDTO sportDTO);

//    @Select("select * from sport where id=#{id};")
    SportVideoVO selectById(Long id);

    Sport selectSportById(Long id);
}
