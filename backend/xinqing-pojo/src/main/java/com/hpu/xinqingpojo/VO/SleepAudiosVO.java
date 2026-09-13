package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpu.xinqingpojo.entity.SleepAudios;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 助眠
 * </p>
 *
 * @since 2025-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SleepAudiosVO implements Serializable {

    /**
     * 类型id
     */
    private Integer typeId;

    /**
     * 音乐数据
     */
    private List<SleepAudios> list;


}
