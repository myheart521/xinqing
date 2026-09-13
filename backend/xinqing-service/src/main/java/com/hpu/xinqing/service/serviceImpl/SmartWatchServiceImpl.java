package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.EquipmentMapper;
import com.hpu.xinqing.mapper.SmartwatchDetailMapper;
import com.hpu.xinqing.mapper.SmartwatchMapper;
import com.hpu.xinqing.service.SmartWatchService;
import com.hpu.xinqingcommon.constant.MessageConstant;
import com.hpu.xinqingcommon.exception.BaseException;
import com.hpu.xinqingcommon.exception.CreateDeviceException;
import com.hpu.xinqingcommon.utils.HuaweiIOTUtil;
import com.hpu.xinqingpojo.DTO.SmartwatchReportDTO;
import com.hpu.xinqingpojo.VO.SmartWatchVO;
import com.hpu.xinqingpojo.VO.SmartwatchReportVO;
import com.hpu.xinqingpojo.enmus.DeviceType;
import com.hpu.xinqingpojo.entity.Equipment;
import com.hpu.xinqingpojo.entity.Smartwatch;
import com.hpu.xinqingpojo.entity.SmartwatchDetail;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartWatchServiceImpl implements SmartWatchService {
    //        String sleepTime = stringStringMap.get("sleepTime");
//        String sedentaryTime = stringStringMap.get("sedentaryTime");
//        String heartRate = stringStringMap.get("heartRate");
//        String bodyTemperature = stringStringMap.get("bodyTemperature");
//        String dateTime = stringStringMap.get("dateTime");
//        String isSleepTime = stringStringMap.get("isSleepTime");
//        String isSedentaryTime = stringStringMap.get("isSedentaryTime");
//        String isHeartRate = stringStringMap.get("isHeartRate");
//        String isBodyTemperature = stringStringMap.get("isBodyTemperature");
    @Autowired
    private HuaweiIOTUtil huaweiIOTUtil;
    @Autowired
    private SmartwatchMapper smartWatchMapper;
    @Autowired
    private SmartwatchDetailMapper smartwatchDetailMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Override
    @Transactional
    public SmartWatchVO updateSmartWatchData(DeviceType deviceType) {
        //使用huaweiIOTUtil来获取数据
        String equipId;
        try{
            equipId= StpUtil.getSession().get(deviceType.getType().toString()).toString();
        }catch (Exception ex){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        List<Map<String, Object>> data = huaweiIOTUtil.getData(equipId);
        Map<String, Object> stringStringMap = data.get(data.size()-1);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String dateTimeStr = stringStringMap.get("dateTime").toString();
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);//获取时间
        SmartWatchVO smartWatchVO = new SmartWatchVO();
        //设备侧最新时间不是今天，直接返回空值
        if(!dateTime.toLocalDate().equals(LocalDate.now())){
            return smartWatchVO;
        }
//        Smartwatch smartwatchByHuawei = JSONUtil.toBean(JSONUtil.toJsonStr(stringStringMap), Smartwatch.class);
        QueryWrapper<Smartwatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(StpUtil.getLoginId().toString()));
        queryWrapper.orderByDesc("device_time");
        queryWrapper.last("limit 1");
        //查询出今天的最后一条数据
        Smartwatch latestEntity = smartWatchMapper.selectOne(queryWrapper);
        //判断是由于数据更新而查询还是因为仅仅查询今日的最新数据
        //将日期格式化，取出华为云中的设备日期，也就是华为云中最后一次更新某字段的信息
        LocalDateTime deviceTime = latestEntity.getDeviceTime();
        if(!dateTime.equals(deviceTime)){
            //最后一条数据的日期不相等，说明一定有数据更新了，更新了，那么true或者false就一定可以信任
            //华为云平台中的数据是各个属性的最新值，数据库中记录的是每次更新所更新的属性，方便统计报表
            //谁没更新就置为null，插入均值操作放到统计报表里
            SmartwatchDetail smartwatchDetail = new SmartwatchDetail();
            //如果不相同，应该要更新
            latestEntity.setDeviceTime(dateTime);
            smartwatchDetail.setDeviceTime(dateTime);
            //设置用户id
            smartwatchDetail.setUserId(Long.parseLong(StpUtil.getLoginId().toString()));
            //分别对更新的值进行写入，并插入数据库的数据
            //记录每一次更新的结果，可看出用户今日最高心率，最高体温等统计量
            if((boolean)stringStringMap.get("isSleepTime")){//如果非空，说明修改了
                latestEntity.setSleepTime(Float.parseFloat(stringStringMap.get("sleepTime").toString()));
            }
            if((boolean)stringStringMap.get("isSedentaryTime")){
                latestEntity.setSedentaryTime(Float.parseFloat(stringStringMap.get("sedentaryTime").toString()));
            }
            if((boolean)stringStringMap.get("isHeartRate")){
                latestEntity.setHeartRate(Float.parseFloat(stringStringMap.get("heartRate").toString()));
            }
            if((boolean)stringStringMap.get("isBodyTemperature")){
                latestEntity.setBodyTemperature(Float.parseFloat(stringStringMap.get("bodyTemperature").toString()));
            }
            // 数据库中含有最新数据，对于记录每一条数据的smartwatchDetail来说，这条数据一定是插入
            smartwatchDetailMapper.insert(smartwatchDetail);
            //对于要直接返回给前端的smartWatch来说，这条数据可能是更新，也可能是插入
            smartWatchMapper.insertOrUpdate(latestEntity);
        }
        // 不用插入，将查询结果直接返回
        smartWatchVO.setBodyTemperature(latestEntity.getBodyTemperature().toString());
        smartWatchVO.setSleepTime(latestEntity.getSleepTime().toString());
        smartWatchVO.setHeartRate(latestEntity.getHeartRate().toString());
        smartWatchVO.setSedentaryTime(latestEntity.getSedentaryTime().toString());
        //如果今天不用更新
        return smartWatchVO;
    }

    //华为云平台中的数据不一定就是今天的数据，因此要持久化到数据库中
    //todo 数据要保存在数据库中，我们后期才能交给AI分析他的心理状况，所以每天十二点将我的数据
    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void getNewSmartWatch(){
        //查询今天的数据是不是真的是空的
    }

    //smartWatch表中含有的是真实的每天的数据，不会存在前段时间的数据的情况
    @Override
    public SmartWatchVO getSmartWatchData(DeviceType deviceType) {
        QueryWrapper<Smartwatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(StpUtil.getLoginId().toString()));
        queryWrapper.orderByDesc("device_time");
        queryWrapper.last("limit 1");
        Smartwatch latestEntity = smartWatchMapper.selectOne(queryWrapper);
        SmartWatchVO smartWatchVO = new SmartWatchVO();
        smartWatchVO.setBodyTemperature(latestEntity.getBodyTemperature().toString());
        smartWatchVO.setSleepTime(latestEntity.getSleepTime().toString());
        smartWatchVO.setHeartRate(latestEntity.getHeartRate().toString());
        smartWatchVO.setSedentaryTime(latestEntity.getSedentaryTime().toString());
        return smartWatchVO;
    }

    @Override
    public void createDevice(DeviceType deviceType) {
        Equipment device = huaweiIOTUtil.createDevice(deviceType);
        if (device==null){
            throw new CreateDeviceException("请检查是否重复建立设备");
        }else {
            device.setEquipId(deviceType.getType()+"$"+device.getEquipId());
            equipmentMapper.insert(device);
        }
    }
    public SmartwatchReportVO dataReport(LocalDate begin, LocalDate end) {
        //用户的设备id
        String equipId=StpUtil.getSession().get("smartWatch").toString();
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        //构造日期集合
        List<LocalDate> date = new ArrayList<>();

//      睡眠时长集合(单位cm)
        List<Float> sleepTimeList = new ArrayList<>();
//      久坐时间集合(单位kg)
        List<Float> sedentaryTimeList = new ArrayList<>();
//        bmi集合
        List<Float> heartRateList = new ArrayList<>();
//        tizhilv集合
        List<Float> bodyTemperatureList = new ArrayList<>();
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //由于没有对应的实体类，用Map类型来传数据
        Map map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("equipId", equipId);
        List<SmartwatchReportDTO> smartwatchList = smartWatchMapper.avgSmartwatch(map);
        for (SmartwatchReportDTO smartwatch : smartwatchList) {
            if(smartwatch!=null){
                if (smartwatch.getSleepTime() != null) {
                    sleepTimeList.add(smartwatch.getSleepTime());
                } else {
                    sleepTimeList.add(0F);
                }
                if (smartwatch.getBodyTemperature() != null) {
                    bodyTemperatureList.add(smartwatch.getBodyTemperature());
                } else {
                    bodyTemperatureList.add(0F);
                }
                if (smartwatch.getSedentaryTime() != null) {
                    sedentaryTimeList.add(smartwatch.getSedentaryTime());
                } else {
                    sedentaryTimeList.add(0F);
                }
                if (smartwatch.getHeartRate() != null) {
                    heartRateList.add(smartwatch.getHeartRate());
                } else {
                    heartRateList.add(0F);
                }
            }else{
                sedentaryTimeList.add(0F);
                sleepTimeList.add(0F);
                heartRateList.add(0F);
                bodyTemperatureList.add(0F);
            }
            date.add(smartwatch.getCreateTime());
        }
//        list转String
        //        把集合中数据转成字符串，并用“,”连接起来
        String dateList = StringUtils.join(date, ",");
        String sedentary = StringUtils.join(sedentaryTimeList, ',');
        String sleepTime =  StringUtils.join(sleepTimeList, ',');
        String bodyTemperature =  StringUtils.join(bodyTemperatureList, ',');
        String heartRate =  StringUtils.join(heartRateList, ',');
        return SmartwatchReportVO.builder()
                .dateList(dateList)
                .sedentaryTimeList(sedentary)
                .sleepTimeList(sleepTime)
                .bodyTemperatureList(bodyTemperature)
                .heartRateList(heartRate)
                .build();
    }
}
