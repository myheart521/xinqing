package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.VO.SmartWatchVO;
import com.hpu.xinqingpojo.VO.SmartwatchReportVO;
import com.hpu.xinqingpojo.enmus.DeviceType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface SmartWatchService {

    SmartWatchVO getSmartWatchData(DeviceType deviceType);

    SmartWatchVO updateSmartWatchData(DeviceType deviceType);

    void createDevice(DeviceType deviceType);

    SmartwatchReportVO dataReport(LocalDate begin, LocalDate end);
}
