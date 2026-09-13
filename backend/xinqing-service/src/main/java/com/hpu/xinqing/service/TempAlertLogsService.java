package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hpu.xinqingpojo.VO.TempAlertLogsVO;
import com.hpu.xinqingpojo.entity.TempAlertLogs;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description 针对表【temp_alert_logs】的数据库操作Service
 * @createDate 2025-06-09 16:47:21
 */
public interface TempAlertLogsService extends IService<TempAlertLogs> {

    IPage<TempAlertLogsVO> getAlertLogsByPage(int pageNum, int pageSize,long userId);
}
