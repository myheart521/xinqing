package com.hpu.xinqing.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.MusicMapper;
import com.hpu.xinqing.service.MusicService;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.MusicVo;
import com.hpu.xinqingpojo.entity.Music;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {

    @Override
    public List<MusicVo> pageQuery(PageDTO pageDTO) {
        Page<Music> musicPage = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        OrderItem create_time = new OrderItem().setAsc(true).setColumn("create_time");
        musicPage.addOrder(create_time);
        List<String> list = List.of(new String[]{"id","title","artist","cover","cover_bg","url","lrc_url"});
        //去数据库查
        Page<Music> page = page(musicPage,new QueryWrapper<Music>().select(list));
        List<MusicVo> records = page.getRecords().stream().map((music -> new MusicVo(music.getId(),music.getTitle(), music.getArtist(),music.getCover(),music.getCoverBg().split(","),music.getUrl(),music.getLrcUrl()))).collect(Collectors.toList());
        return records;
    }
}
