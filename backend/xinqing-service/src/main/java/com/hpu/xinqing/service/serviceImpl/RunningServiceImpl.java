package com.hpu.xinqing.service.serviceImpl;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.EquipmentMapper;
import com.hpu.xinqing.mapper.RunningMapper;
import com.hpu.xinqing.service.IRunningService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqingcommon.constant.MessageConstant;
import com.hpu.xinqingcommon.exception.BaseException;
import com.hpu.xinqingcommon.utils.HuaweiIOTUtil;
import com.hpu.xinqingpojo.DTO.RunningReportDTO;
import com.hpu.xinqingpojo.VO.RunningReportVO;
import com.hpu.xinqingpojo.entity.Equipment;
import com.hpu.xinqingpojo.entity.Running;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @since 2024-10-23
 */
@Service
@Slf4j
public class RunningServiceImpl extends ServiceImpl<RunningMapper, Running> implements IRunningService {
    @Autowired
    private HuaweiIOTUtil huaweiIOTUtil;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private RunningMapper runningMapper;
    @Override
    public Running selectLoTDA(Long userId) {
        String equipId;
        try{
            equipId=StpUtil.getSession().get("running").toString();
        }catch (Exception ex){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        Running running=getLoTDA(equipId);
        running.setUserId(userId);
//        List<Map<String, String>> data = huaweiIOTUtil.getData(DeviceType.Running);
//        log.info(data.toString());
////       时间日期格式：2023-07-26T10:30:00
//        int index=data.size()-1;
//        running.setDiatance(Float.parseFloat(data.get(index).get("distance")));
//        running.setLongTime(Float.parseFloat(data.get(index).get("longTime")));
//        running.setCreateTime(LocalDateTime.parse(data.get(index).get("dateTime")));
//        String equipmentId=data.get(index).get("id");
//        if(running.getEquipId().equals(equipId)){
            QueryWrapper<Running> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("create_time", running.getCreateTime()).eq("equip_id",equipId);
            Running one = this.getOne(queryWrapper);
            if(one==null){
                this.save(running);
            }
//        }else{

//        }
//        QueryWrapper<Running> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("create_time", running.getCreateTime()).eq("user_id",id);
//        Running one = this.getOne(queryWrapper);
//        if(one==null){
//            this.save(running);
//        }
        return running;
    }

    @Override
    public void updateLoTDA(String equipId) {
        Running running=getLoTDA(equipId);
        QueryWrapper<Running> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_time", running.getCreateTime()).eq("equip_id",running.getEquipId());
        Running one = this.getOne(queryWrapper);
        if (one == null) {
//      插入数据
            List<Equipment> equipmentList = equipmentMapper.selectByMap(Map.of("equip_id", "running$" + running.getEquipId()));
            running.setUserId(equipmentList.get(0).getUserId());
            this.save(running);
        }
    }

    public Running getLoTDA(String equipId){
        Running running=new Running();
        List<Map<String, Object>> data = huaweiIOTUtil.getData(equipId);
        log.info(data.toString());
//       时间日期格式：2023-07-26T10:30:00
        int index=data.size()-1;
        running.setDiatance(Float.parseFloat(data.get(index).get("distance").toString()));
        running.setLongTime(Float.parseFloat(data.get(index).get("longTime").toString()));
        running.setCreateTime(LocalDateTime.parse(data.get(index).get("dateTime").toString()));
        String equipmentId=data.get(index).get("id").toString();
        running.setEquipId(equipmentId);
        return running;
    }

    @Override
    public RunningReportVO dataReport(LocalDate begin, LocalDate end) {
        //用户的设备id
        String equipId=StpUtil.getSession().get("running").toString();
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
//      LongTime(单位min)
        List<Float> longTimeList = new ArrayList<>();
//        distance集合
        List<Float> distanceList = new ArrayList<>();
        //            用于create_time 格式为LocalDateTime
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<LocalDate> date = new ArrayList<>();
//            由于没有对应的实体类，用Map类型来传数据
        Map map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("equip_id", equipId);
//            计算一天的身高体重,的平均值值
        List<RunningReportDTO> runningReportDTOList=runningMapper.selectCountByDate(map);

        for (RunningReportDTO running : runningReportDTOList) {

            if(running!=null){
              if(running.getDistance()!=null){
                  distanceList.add(running.getDistance());
              }else{
                  distanceList.add(0F);
              }
                if(running.getLongTime()!=null){
                    longTimeList.add(running.getLongTime());
                }else{
                    longTimeList.add(0F);
                }
            }else{
                distanceList.add(0F);
                longTimeList.add(0F);
            }
            date.add(running.getCreateTime());
        }
//        把集合中数据转成字符串，并用“,”连接起来
        String dateList = StringUtils.join(date, ",");
        String distance=StringUtils.join(distanceList,',');
        String longTime=StringUtils.join(longTimeList,',');
        return RunningReportVO.builder()
                .dateList(dateList)
                .distanceCountList(distance)
                .longTimeList(longTime)
                .build();
    }
}
