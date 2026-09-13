package com.hpu.xinqing.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SportImageMapper {

    /**
     * 运动图片查询
     * @param id
     * @return
     */
    @Select("select image from sport_image where sportId=#{id};")
    List<String> selectBySportId(Long id);
}
