package com.hpu.xinqingcommon.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hpu.xinqingcommon.exception.CreateDeviceException;
import com.hpu.xinqingpojo.enmus.DeviceType;
import com.hpu.xinqingpojo.entity.Equipment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@AllArgsConstructor
@Slf4j
public class HuaweiIOTUtil {
    private String tokenUrl ;//获取token的url
    private String username ;
    private String password ;
    private String IAMAccount ; //IAM用户所属账号名
    private String endpoint;
    private String projectId;
    private String deviceId;
    public String iotLogin() {
        String login = "{\n" +
                "    \"auth\": {\n" +
                "        \"identity\": {\n" +
                "            \"methods\": [\n" +
                "                \"password\"\n" +
                "            ],\n" +
                "            \"password\": {\n" +
                "                \"user\": {\n" +
                "                    \"domain\": {\n" +
                "                        \"name\": \"" + IAMAccount + "\"       \n" +
                "                    },\n" +
                "                    \"name\": \"" + username + "\",           \n" +
                "                    \"password\": \"" + password + "\"    \n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"scope\": {\n" +
                "            \"domain\": {\n" +
                "                \"name\": \"" + IAMAccount + "\"       \n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        String result = null;
        try {
            result = HttpClientUtil.doPostGetToken(this.tokenUrl, login);
        } catch (Exception e) {
            System.out.println("连接出错");
        }
        return result;
    }
    public List<Map<String, Object>> getData(String deviceId){
        String token = StpUtil.getSession().get("iot-token").toString();
        if (token.isEmpty()) {
            log.error("请先登录，获取设备token");
            return null;
        }
//        String deviceId=this.deviceId+deviceType.getType();
        String url = "https://"+this.endpoint+"/v5/iot/"+this.projectId+"/devices/"+deviceId+"/shadow";
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Content-Type","application/json");
        headersMap.put("X-Auth-Token", token);
        String jsonStr = HttpClientUtil.doGet(url, null, headersMap);
        System.out.println("获取的JSON字符串为："+jsonStr);
        // 使用hutool的JSONUtil将字符串转换为JSONObject
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

        // 提取shadow数组
        JSONArray shadowArray = jsonObject.getJSONArray("shadow");
        List<Map<String, Object>> list = new ArrayList<>();
        // 遍历shadow数组
        for (int i = 0; i < shadowArray.size(); i++) {
            // 提取每个shadow对象
            JSONObject shadowObj = shadowArray.getJSONObject(i);

            // 提取reported对象
            JSONObject reportedObj = shadowObj.getJSONObject("reported");

            // 提取properties对象
            JSONObject propertiesObj = reportedObj.getJSONObject("properties");

            // 打印properties对象
            String s = propertiesObj.toStringPretty();
            Map<String, Object> propertiesMap = propertiesObj.toBean(Map.class);
            list.add(propertiesMap);
        }
        return list;
    }
    public Equipment createDevice(DeviceType deviceType){
        String token = StpUtil.getSession().get("iot-token").toString();
        Long l = Long.parseLong(StpUtil.getLoginId().toString());
        if (token.isEmpty()) {
            log.error("请先登录，获取设备token");
            return null;
        }
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Content-Type","application/json");
        headersMap.put("X-Auth-Token", token);
//        HashMap<String, Object> paramaMap = new HashMap<>();
        String nodeId = deviceType.getType()+l;
//        paramaMap.put("node_id",nodeId);
//        paramaMap.put("product_id","67130c5f65629b340098d245");
//        paramaMap.put("device_name",nodeId);

//        Map<String, Object> shadowServiceMap = new HashMap<>();
//        Map<String, Object> desiredMap = new HashMap<>();
        // 创建一个LocalDateTime实例
        LocalDateTime dateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        // 格式化日期时间
        String formattedDateTime = dateTime.format(formatter);
        String jsonData = null;
        //不同设备的初始属性值要和服务对应
        if(deviceType==DeviceType.smartWatch){
            jsonData = "{"
                    + "\"device_name\":\"smartWatch"+l+"\","
                    + "\"shadow\":[{"
                    + "\"desired\":{"
                    + "\"dateTime\":\""+formattedDateTime+"\","
                    + "\"isSleepTime\":false,"
                    + "\"isBodyTemperature\":false,"
                    + "\"isHeartRate\":false,"
                    + "\"heartRate\":\"-1\","
                    + "\"isSedentaryTime\":false,"
                    + "\"bodyTemperature\":\"-1\","
                    + "\"sleepTime\":\"-1\","
                    + "\"sedentaryTime\":\"-1\""
                    + "},"
                    + "\"service_id\":\"smartWatch\""
                    + "}],"
                    + "\"product_id\":\"67130c5f65629b340098d245\","
                    + "\"node_id\":\"smartWatch"+l+"\""
                    + "}";
//            desiredMap.put("sleepTime", "-1");
//            desiredMap.put("sedentaryTime","-1");
//            desiredMap.put("heartRate","-1");
//            desiredMap.put("bodyTemperature","-1");
//            desiredMap.put("dateTime", formattedDateTime);
//            desiredMap.put("isSleepTime",false);
//            desiredMap.put("isSedentaryTime",false);
//            desiredMap.put("isHeartRate",false);
//            desiredMap.put("isBodyTemperature",false);

        }
        if(deviceType==DeviceType.Body){
            jsonData="{\n" +
                    "    \"device_name\": \"body"+l+"\",\n" +
                    "    \"shadow\": [\n" +
                    "        {\n" +
                    "            \"desired\": {\n" +
                    "                \"dateTime\": \""+formattedDateTime+"\",\n" +
                    "                \"tiZhiLv\": \"-1\",\n" +
                    "                \"weight\": \"-1\",\n" +
                    "                \"id\": \"CONFIGURE_ON_SERVERbody"+l+"\",\n" +
                    "                \"height\": \"-1\",\n" +
                    "                \"BMI\": \"-1\"\n" +
                    "            },\n" +
                    "            \"service_id\": \"body\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"product_id\": \"67130c5f65629b340098d245\",\n" +
                    "    \"node_id\": \"body"+l+"\"\n" +
                    "}";
//            desiredMap.put("height", "-1");
//            desiredMap.put("BMI","-1");
//            desiredMap.put("tiZhiLv","-1");
//            desiredMap.put("dateTime",formattedDateTime);
//            desiredMap.put("weight","-1");
//            desiredMap.put("id",this.deviceId+nodeId);
        }
        if(deviceType==DeviceType.Running){
            jsonData = "{\n" +
                    "    \"device_name\": \"running"+l+"\",\n" +
                    "    \"shadow\": [{\n" +
                    "        \"desired\": {\n" +
                    "            \"dateTime\": \""+formattedDateTime+"\",\n" +
                    "            \"longTime\": \"-1\",\n" +
                    "            \"distance\": \"-1\",\n" +
                    "            \"id\": \"CONFIGURE_ON_SERVERrunning"+l+"\"\n" +
                    "        },\n" +
                    "        \"service_id\": \"running\"\n" +
                    "    }],\n" +
                    "    \"product_id\": \"67130c5f65629b340098d245\",\n" +
                    "    \"node_id\": \"running"+l+"\"\n" +
                    "}";

//            desiredMap.put("longTime","-1");
//            desiredMap.put("distance","-1");
//            desiredMap.put("dateTime",formattedDateTime);
//            desiredMap.put("id",this.deviceId+nodeId);
        }

//        shadowServiceMap.put("service_id", deviceType.getType());
//        shadowServiceMap.put("desired", desiredMap);
//
//        // 将单个shadow对象放入ArrayList中
//        paramaMap.put("shadow", Collections.singletonList(shadowServiceMap));


        String url = "https://"+this.endpoint+"/v5/iot/"+this.projectId+"/devices";
        Equipment device =null;
        try {
            String s = HttpClientUtil.doPost(url, null, headersMap,jsonData);
            JSONObject jsonObject = JSONUtil.parseObj(s);
            // 从JSON对象中提取device_id
            String device_id = jsonObject.getStr("device_id");

            if (device_id==null){
                throw new CreateDeviceException("请检查是否重复建立设备");
            }else {
                device= Equipment.builder().equipId(device_id).userId(l).build();
            }
        } catch (IOException e) {
            log.error("发送创建设备请求异常");
        }
        return device;
    }


}
