package com.hpu.xinqingpojo.DTO;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class SwiperDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    private String url;

    private String name;

    private String text;

    /**
     * 存储格式: {type}:{对应的id}
     * type枚举, 只能是sport或diet
     * 例如: sport:1 -----> 表示引用的是sport表的id为1那个数据记录
     */
    private String link;

    /**
     * -1代表匿名
     */
    private Long createId;

    public boolean isMainNotNull(){
        return url != null && name != null && text != null && link != null;
    }

    public boolean isLinkOK(){
        String type = link.split(":")[0];
        return type.equals("sport") || type.equals("diet");
    }


}
