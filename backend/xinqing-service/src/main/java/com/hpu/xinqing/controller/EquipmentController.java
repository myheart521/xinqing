package com.hpu.xinqing.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.service.IEquipmentService;
import com.hpu.xinqingcommon.exception.CreateDeviceException;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingcommon.utils.HuaweiIOTUtil;
import com.hpu.xinqingpojo.VO.EquipmentVO;
import com.hpu.xinqingpojo.enmus.DeviceType;
import com.hpu.xinqingpojo.entity.Equipment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @since 2024-10-27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {

    private final HuaweiIOTUtil huaweiIOTUtil;
    private final IEquipmentService equipmentService;


    @GetMapping("/create/{deviceType}")
    public Result createEquipment(@PathVariable DeviceType deviceType){
        Equipment device = huaweiIOTUtil.createDevice(deviceType);
        if(device==null){
            throw new CreateDeviceException("请检查是否重新建立设备");
        }
        device.setEquipId(deviceType.getType()+"$"+device.getEquipId());
        equipmentService.save(device);
        return Result.success();
    }

}
