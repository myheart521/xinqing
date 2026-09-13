package com.hpu.xinqingcommon.context;

import lombok.Data;

@Data
public class RequestHolder {

    private static ThreadLocal<ReqInfo> reqInfo=new ThreadLocal<>();

    public static ReqInfo getReqInfo(){
        return reqInfo.get();
    }
    public static void setReqInfo(ReqInfo reqInfo1){
        reqInfo.set(reqInfo1);
    }
    public static void removeReqInfo(){
        reqInfo.remove();
    }
}

