package com.hpu.xinqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.entity.Modules;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;


public interface ModulesMapper extends BaseMapper<Modules> {
    @Select("select * from modules where user_id=0 and status = 1")
    List<Modules> getAllDefault();
    @Select("select * from modules where id=#{id}")
    Modules getById(Long id);
}
