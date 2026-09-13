package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.VO.BodyReportVO;
import com.hpu.xinqingpojo.entity.Body;

import java.time.LocalDate;

/**
 * <p>
 * 体脂秤 服务类
 * </p>
 *
 * @since 2024-10-23
 */
public interface IBodyService extends IService<Body> {

   Body selectLoTDA(Long userId) ;

   BodyReportVO dataReport(LocalDate begin, LocalDate end);

   void updateLoTDA(String equipId);
}
