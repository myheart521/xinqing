package com.hpu.xinqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.VO.CollegeCountVo;
import com.hpu.xinqingpojo.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("select school as name,count(*) as count, latitude ,longitude from user join universities on user.school=CONVERT(universities.name USING utf8) COLLATE utf8_unicode_ci group by school")
    List<CollegeCountVo> selectCollegeCount();
}
