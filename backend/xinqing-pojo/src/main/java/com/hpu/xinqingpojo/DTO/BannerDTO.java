package com.hpu.xinqingpojo.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BannerDTO {
    private static final long serialVersionUID = 1L;


    private String imageUrl;

    private String link;
    /**
     * 创建人id，-1代表游客或匿名
     */
    private Long createId;

    public boolean isMainNotNull(){
        return imageUrl != null&&
                link != null;
    }
}
