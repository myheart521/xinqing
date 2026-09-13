package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.VO.TempStudentVO;
import com.hpu.xinqingpojo.entity.TempStudent;

/**
 * <p>
 * 心理危急学生表 服务类
 * </p>
 *
 * @since 2025-07-08
 */
public interface ITempStudentService extends IService<TempStudent> {

    IPage<TempStudentVO> getStudentByPage(int pageNum, int pageSize);

    boolean updateByStudentId(Long studentId);
}
