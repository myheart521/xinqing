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
 * AI 聊天角色表
 * </p>
 *
 * @since 2025-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_chat_role")
public class AiChatRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 模型编号
     */
    private Long modelId;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色类别
     */
    private String category;

    /**
     * 角色排序
     */
    private Integer sort;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色上下文
     */
    private String systemMessage;

    /**
     * 关联的知识库编号数组
     */
    private String knowledgeIds;

    /**
     * 关联的工具编号数组
     */
    private String toolIds;

    /**
     * 是否公开
     */
    private Boolean publicStatus;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
