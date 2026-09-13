package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.NootBookDTO;
import com.hpu.xinqingpojo.entity.NootBook;
import com.hpu.xinqingcommon.result.PageResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2024-09-11
 */
public interface INootBookService extends IService<NootBook> {

    PageResult listByUserId(NootBookDTO nootBookDTO);
}
