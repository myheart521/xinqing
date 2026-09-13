package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.VO.RunningReportVO;
import com.hpu.xinqingpojo.entity.Running;

import java.time.LocalDate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2024-10-23
 */
public interface IRunningService extends IService<Running> {

    Running selectLoTDA(Long userId);

    RunningReportVO dataReport(LocalDate begin, LocalDate end);

    void updateLoTDA(String equipId);
}
