package com.hpu.xinqingcommon.context;

import com.hpu.xinqingcommon.constant.CommonConstant;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqInfo {
    private String username;
    private String token;
    private String ip;
    private String url;
    private String method;
    private Long userId;

    public static ReqInfo anonymous(String ip, String url,String method) {
        return ReqInfo.builder()
                .username(CommonConstant.ANONYMOUS_USERNAME)
                .token(CommonConstant.ANONYMOUS_TOKEN)
                .userId(CommonConstant.ANONYMOUS_USER_ID)
                .ip(ip)
                .method(method)
                .url(url).build();
    }
}
