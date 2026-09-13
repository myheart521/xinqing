package com.hpu.xinqing.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqing.mapper.DietMapper;
import com.hpu.xinqing.service.DietService;
import com.hpu.xinqingpojo.DTO.BasePageDTO;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.DietVO;
import com.hpu.xinqingpojo.entity.Diet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DietServiceImpl extends ServiceImpl<DietMapper, Diet> implements DietService {

    @Autowired
    ObjectMapper objectMapper;
    @Override
    public List<DietVO> pageQuery(PageDTO pageDTO) {
        Integer pageSize = pageDTO.getPageSize();
        Integer pageNo = pageDTO.getPageNo();
        //添加分页基本参数
        Page<Diet> dietPage = new Page<>(pageNo, pageSize);
        //设置排序参数，按照时间顺序，升序
        OrderItem orderItem1 = new OrderItem().setAsc(true).setColumn("create_time");
        dietPage.addOrder(orderItem1);
        List<String> list = List.of(new String[]{"id","title","name","main_image","tags"});
        //去数据库查
        Page<Diet> page = null;
        page = page(dietPage,new QueryWrapper<Diet>().select(list));
        List<DietVO> records = page.getRecords().stream().map((diet -> new DietVO(diet.getId(),diet.getTitle(), diet.getName(),diet.getMainImage(),diet.getTags().split(",")))).collect(Collectors.toList());
        return records;
    }

    @Override
    public List<Diet> getByUserId(Long userId, BasePageDTO basePageDTO) {
        Page<Diet> page = new Page<>(basePageDTO.getCurrent(),basePageDTO.getPageSize());
        return lambdaQuery()
                .eq(Diet::getUserId,userId)
                .page(page)
                .getRecords();
    }

    @Override
    public List<Map> getMapByUserId(Long userId, BasePageDTO basePageDTO) {
        return getByUserId(userId,basePageDTO)
                .stream()
                .map(diet -> objectMapper.convertValue(diet,Map.class))
                .toList();
    }
}
