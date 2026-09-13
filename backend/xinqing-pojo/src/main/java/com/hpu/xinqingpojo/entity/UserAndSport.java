package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @TableName user_and_sport
 */
@TableName(value ="user_and_sport")
@Data
@ToString
@NoArgsConstructor
public class UserAndSport implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long sportId;

    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;

    public UserAndSport(Long userId, Long sportId) {
        this.userId = userId;
        this.sportId = sportId;
        this.createTime = LocalDateTime.now();
    }

}