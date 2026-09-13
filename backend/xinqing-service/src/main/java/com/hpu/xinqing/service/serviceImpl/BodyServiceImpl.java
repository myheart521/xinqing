package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.mapper.BodyMapper;
import com.hpu.xinqing.mapper.EquipmentMapper;
import com.hpu.xinqing.service.IBodyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqingcommon.constant.MessageConstant;
import com.hpu.xinqingcommon.exception.BaseException;
import com.hpu.xinqingcommon.utils.HuaweiIOTUtil;
import com.hpu.xinqingpojo.DTO.BodyReportDTO;
import com.hpu.xinqingpojo.VO.BodyReportVO;
import com.hpu.xinqingpojo.enmus.DeviceType;
import com.hpu.xinqingpojo.entity.Body;
import com.hpu.xinqingpojo.entity.Equipment;
import org.apache.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 体脂秤 服务实现类
 * </p>
 *
 * @since 2024-10-23
 */
@Service
@Slf4j
public class BodyServiceImpl extends ServiceImpl<BodyMapper, Body> implements IBodyService {

    @Autowired
    private HuaweiIOTUtil huaweiIOTUtil;
    @Autowired
    private BodyMapper bodyMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public Body selectLoTDA(Long userId) {
        Body body = new Body();
        //用户的设备id
        String equipId;
        try{
            equipId=StpUtil.getSession().get("body").toString();
        }catch (Exception ex){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
       /* body.setUserId(id);
//        1.从华为云平台获取数据
        List<Map<String, String>> data = huaweiIOTUtil.getData(DeviceType.Body);
        log.info(data.toString());
//       时间日期格式：2023-07-26T10:30:00
        int index=data.size()-1;
//        2.返回前返回前端,同时把数据存入数据库
        body.setHeight(Integer.parseInt(data.get(index).get("height")));
        body.setWeight(Float.parseFloat(data.get(index).get("weight")));
        body.setBmi(Float.parseFloat(data.get(index).get("BMI")));
        body.setTiZhiLv(Float.parseFloat(data.get(index).get("tiZhiLv")));
        String string = data.get(index).get("dateTime");
        LocalDateTime dateTime=LocalDateTime.parse(string);
        body.setCreateTime(dateTime);
        String equipmentId=data.get(index).get("id");*/
        body=getLoTDA(equipId);
        body.setUserId(userId);
//        if(body.getEquipId().equals(equipId)){
            QueryWrapper<Body> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("create_time", body.getCreateTime()).eq("equip_id",equipId);
            Body one = this.getOne(queryWrapper);
            if (one == null) {
//      插入数据
                this.save(body);
            }
//
        return body;
    }

    @Override
    public void updateLoTDA(String equipId) {
        Body body = new Body();
        body=getLoTDA(equipId);
        QueryWrapper<Body> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_time", body.getCreateTime()).eq("equip_id",body.getEquipId());
        Body one = this.getOne(queryWrapper);
        if (one == null) {
//      插入数据
            List<Equipment> equipmentList = equipmentMapper.selectByMap(Map.of("equip_id", "body$" + body.getEquipId()));
            body.setUserId(equipmentList.get(0).getUserId());
            this.save(body);
        }
    }

    /**
     * 把华为云中数据解析出来
     * @return
     */
    public Body getLoTDA(String equipId){
        Body body=new Body();
//        1.从华为云平台获取数据
        List<Map<String, Object>> data = huaweiIOTUtil.getData(equipId);
        log.info(data.toString());
//       时间日期格式：2023-07-26T10:30:00
        int index=data.size()-1;
//        2.返回前返回前端,同时把数据存入数据库
        body.setHeight(Integer.parseInt(data.get(index).get("height").toString()));
        body.setWeight(Float.parseFloat(data.get(index).get("weight").toString()));
        body.setBmi(Float.parseFloat(data.get(index).get("BMI").toString()));
        body.setTiZhiLv(Float.parseFloat(data.get(index).get("tiZhiLv").toString()));
        String string = data.get(index).get("dateTime").toString();
        LocalDateTime dateTime=LocalDateTime.parse(string);
        body.setCreateTime(dateTime);
        body.setEquipId(equipId);
        return body;
    }

    /**
     * 生成数据报表
     * @param begin
     * @param end
     * @return
     */
    @Override
    public BodyReportVO dataReport(LocalDate begin, LocalDate end) {
        Long id= Long.parseLong(StpUtil.getLoginId().toString());
        //用户的设备id
        String equipId=StpUtil.getSession().get("body").toString();
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        List<LocalDate> date = new ArrayList<>();
//      身高集合(单位cm)
        List<Float> heightList = new ArrayList<>();
//        体重集合(单位kg)
        List<Float> weightList = new ArrayList<>();
//        bmi集合
        List<Float> bmiList = new ArrayList<>();
//        tizhilv集合
        List<Float> tiZhiLvList = new ArrayList<>();
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //由于没有对应的实体类，用Map类型来传数据
        Map map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("equipId", equipId);
        List<BodyReportDTO> bodyList = bodyMapper.avgBody(map);
        for (BodyReportDTO body : bodyList) {
            //用于create_time 格式为LocalDateTime
            //计算一天的身高体重,的平均值值
            if(body!=null){
                if (body.getHeight() != null) {
                    heightList.add(body.getHeight());
                } else {
                    heightList.add(0F);
                }
                if (body.getWeight() != null) {
                    weightList.add(body.getWeight());
                } else {
                    weightList.add(0F);
                }
                if (body.getBmi() != null) {
                    bmiList.add(body.getBmi());
                } else {
                    bmiList.add(0F);
                }
                if (body.getTiZhiLv() != null) {
                    tiZhiLvList.add(body.getTiZhiLv());
                } else {
                    tiZhiLvList.add(0F);
                }
            }else{
                heightList.add(0F);
                weightList.add(0F);
                bmiList.add(0F);
                tiZhiLvList.add(0F);
            }
            date.add(body.getCreateTime());
        }

//        list转String
//        把集合中数据转成字符串，并用“,”连接起来
        String dateList = StringUtils.join(date, ",");
        String height = StringUtils.join(heightList, ',');
        String weight = StringUtils.join(weightList, ',');
        String bmi = StringUtils.join(bmiList, ',');
        String tiZhiLv = StringUtils.join(tiZhiLvList, ',');
        return BodyReportVO.builder()
                .dateList(dateList)
                .heightList(height)
                .weightList(weight)
                .bmiList(bmi)
                .tiZhiLvList(tiZhiLv)
                .build();
    }
}
