package com.hpu.xinqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.entity.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicMapper extends BaseMapper<Music> {

    @Select("SELECT * FROM music")
    List<Music> findAll();
}
