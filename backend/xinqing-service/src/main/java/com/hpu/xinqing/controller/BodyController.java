package com.hpu.xinqing.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.EquipmentMapper;
import com.hpu.xinqing.service.IBodyService;
import com.hpu.xinqingcommon.constant.MessageConstant;
import com.hpu.xinqingcommon.exception.BaseException;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.BodyReportVO;
import com.hpu.xinqingpojo.entity.Body;
import com.hpu.xinqingpojo.entity.Equipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 体脂秤 前端控制器
 * </p>
 *
 * @since 2024-10-23
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/body")
public class BodyController {
    private final IBodyService bodyService;

    private final EquipmentMapper equipmentMapper;
    /**
     *用户查询数据，未存储的数据存到数据库中
     * @return
     */
    @GetMapping("/update")
    public Result<Body> update(){
        Long userId= Long.parseLong(StpUtil.getLoginId().toString());
        log.info("查询体脂秤数据,更新：");
        Body body=bodyService.selectLoTDA(userId);
        return Result.success(body);
    }

    @GetMapping("/select")
    public Result<Body> select(){
        //用户的设备id
        String equipId=StpUtil.getSession().get("body").toString();
        log.info("查询体脂秤数据设备id:{}",equipId);
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        Body body=bodyService.lambdaQuery()
                .eq(Body::getEquipId,equipId)
                .orderByDesc(Body::getCreateTime)
                .list().get(0);
        return Result.success(body);
    }


    @GetMapping("/data")
    public Result<BodyReportVO> dataReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        log.info("身体数据统计，日期范围：{}~{}",begin,end);
        BodyReportVO bodyReportVO= bodyService.dataReport(begin,end);
        return Result.success(bodyReportVO);
    }

    @GetMapping("/test")
    public Result test(){
        log.info("查询平台更新体脂秤数据");
        List<Equipment> equipmentList=equipmentMapper.selectByEquipType("body");
        for (Equipment equipment:equipmentList){
            String equipId = equipment.getEquipId();
            String[] s = equipId.split("\\$");
            String id=s[1];
            bodyService.updateLoTDA(id);
        }
        return Result.success();
    }
}
