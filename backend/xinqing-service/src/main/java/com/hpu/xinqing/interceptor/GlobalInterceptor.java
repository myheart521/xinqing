package com.hpu.xinqing.interceptor;

import cn.hutool.Hutool;
import cn.hutool.extra.servlet.ServletUtil;
import com.hpu.xinqing.mapper.UserMapper;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.CommonConstant;
import com.hpu.xinqingcommon.context.ReqInfo;
import com.hpu.xinqingcommon.context.RequestHolder;
import com.hpu.xinqingcommon.utils.StpUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
@Slf4j
@Order(1)
public class GlobalInterceptor implements HandlerInterceptor {
    @Resource
    UserMapper userMapper;

    @PostConstruct
    public void init() {
        log.info("--- 全局拦截器初始化成功 ---");
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ip = getClientIp(request);
        Long userId = null;
        try {
            userId=StpUtils.userId();
        }catch (Exception e){
            userId= CommonConstant.ANONYMOUS_USER_ID;
        }
        //设为匿名
        if (userId.equals(CommonConstant.ANONYMOUS_USER_ID)) {
            RequestHolder.setReqInfo(ReqInfo.anonymous(ip, uri,method));
            return true;
        }
        //设为登录用户
        ReqInfo reqInfo = ReqInfo.builder().url(uri).ip(ip).userId(userId).method(method)
                .username(userMapper.selectById(userId).getUserName())
                .token(request.getHeader("token"))
                .build();
        RequestHolder.setReqInfo(reqInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.removeReqInfo();
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多个 IP，取第一个
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

}
