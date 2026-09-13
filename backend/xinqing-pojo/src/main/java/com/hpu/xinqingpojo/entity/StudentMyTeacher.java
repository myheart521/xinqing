package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学生和专属心理医生关联表
 * </p>
 *
 * @since 2025-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student_my_teacher")
public class StudentMyTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学生id,一个学生只能由一个心理咨询医生，因此是唯一的
     */
    private Integer studentId;

    /**
     * 教师id
     */
    private Integer teacherId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
