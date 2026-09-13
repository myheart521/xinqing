package com.hpu.xinqing.service.serviceImpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.NootBookMapper;
import com.hpu.xinqing.service.INootBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqingpojo.DTO.NootBookDTO;
import com.hpu.xinqingpojo.entity.NootBook;
import org.springframework.stereotype.Service;
import com.hpu.xinqingcommon.result.PageResult;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @since 2024-09-11
 */
@Service
public class NootBookServiceImpl extends ServiceImpl<NootBookMapper, NootBook> implements INootBookService {

    @Override
    public PageResult listByUserId(NootBookDTO nootBookDTO) {
        Page<NootBook> page = this.lambdaQuery()
                .eq(NootBook::getUserId, nootBookDTO.getUserId())
                .page(new Page<>(nootBookDTO.getPageNo(), nootBookDTO.getPageSize()));
        if(page==null){
            return null;
        }
        Long total=page.getTotal();
        List<NootBook> records = page.getRecords();
        return new PageResult(total,records);
    }
}
