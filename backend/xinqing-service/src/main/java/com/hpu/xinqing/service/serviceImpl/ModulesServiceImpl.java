package com.hpu.xinqing.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.ModulesMapper;
import com.hpu.xinqing.service.ModulesService;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.BannerPageVO;
import com.hpu.xinqingpojo.VO.ModulesPageVO;
import com.hpu.xinqingpojo.entity.Banner;
import com.hpu.xinqingpojo.entity.Modules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModulesServiceImpl implements ModulesService {
    @Autowired
    private ModulesMapper modulesMapper;

    public List<ModulesPageVO> getTestByPages(PageDTO pageDTO) {
        Page<Modules> modulesPage = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        OrderItem orderItem1 = new OrderItem().setAsc(true).setColumn("create_time");
        modulesPage.addOrder(orderItem1);
        List<String> list = List.of(new String[]{"id", "title", "introductions", "src"});
        //去数据库查
        Page<Modules> page = modulesMapper.selectPage(modulesPage, new QueryWrapper<Modules>().select(list).eq("status", 1));
        List<ModulesPageVO> records = page.getRecords().stream().map(module -> new ModulesPageVO(module.getId(), module.getTitle(), module.getIntroductions(), module.getSrc())).collect(Collectors.toList());
        return records;
    }

    @Override
    public Modules getTestTypeById(Long id) {
        return modulesMapper.selectById(id);
    }
}
