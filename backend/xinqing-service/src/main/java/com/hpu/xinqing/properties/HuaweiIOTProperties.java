package com.hpu.xinqing.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "xinqing.huawei.iot")
@Data
public class HuaweiIOTProperties {
    private String tokenUrl ;//获取token的url
    private String username ;
    private String password ;
    private String IAMAccount ; //IAM用户所属账号名
    private String endpoint;
    private String projectId;
    private String deviceId;
}
