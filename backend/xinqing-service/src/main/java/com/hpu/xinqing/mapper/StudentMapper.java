package com.hpu.xinqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 学生表 Mapper 接口
 * </p>
 *
 * @since 2025-05-29
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

}
