package com.hpu.xinqingpojo.enmus;

public enum PageEnums {

    PAGE_COMMON(10L)
    ;


    private final Long pageSize;

    private PageEnums(Long pageSize){
        this.pageSize = pageSize;
    }
    public Long getSize() {
        return pageSize;
    }

}
