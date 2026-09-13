package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MusicVo {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌名
     */
    private String title;

    /**
     * 歌手
     */
    private String artist;

    /**
     * 列表页面的图片
     */
    private String cover;



    /**
     * 播放页面的背景图片
     */
    private String[] coverBg;

    /**
     * 音乐音频
     */
    private String url;

    /**
     * 音乐歌词
     */
    private String lrcUrl;
}
