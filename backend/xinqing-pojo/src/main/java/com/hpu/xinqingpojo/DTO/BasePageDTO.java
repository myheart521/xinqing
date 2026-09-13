package com.hpu.xinqingpojo.DTO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqingpojo.enmus.PageEnums;
import jakarta.annotation.Nullable;
import lombok.*;

import javax.validation.constraints.Min;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BasePageDTO {
    @Min(value = 1,message = "页数不可以小于1")
    private Long current;

    @Min(value = 1,message = "页内元素最少是1")
    private Long pageSize;

    public static void legal(BasePageDTO page){
        if (page.getCurrent() == null) page.setCurrent(1L);
        if (page.getPageSize() == null) page.setPageSize(PageEnums.PAGE_COMMON.getSize());
    }

    public static Page<?> mybatisPage(BasePageDTO pageDTO){
        return new Page<>(pageDTO.getCurrent(),pageDTO.getPageSize());
    }


}
