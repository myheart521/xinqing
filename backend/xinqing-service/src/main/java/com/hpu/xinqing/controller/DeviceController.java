package com.hpu.xinqing.controller;

import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingcommon.utils.HuaweiIOTUtil;
import com.hpu.xinqingpojo.enmus.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private HuaweiIOTUtil huaweiIOTUtil;
    @GetMapping("/{deviceId}")
    public Result checkEmail(@PathVariable DeviceType deviceId){
        log.info("设备ID为",deviceId.getType());
//        List<Map<String, Object>> data = huaweiIOTUtil.getData(deviceId);
        return Result.success();
    }
}
