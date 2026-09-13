package com.hpu.xinqingpojo.enmus;

public enum RoleEnums {

    ADMIN(1,"管理员"),
    TEACHER(2,"老师"),
    STUDENT(3,"学生"),
    VISITOR(4,"游客")
    ;


    private final Integer type;
    private final String desc;
     private RoleEnums(Integer type,String desc){
        this.desc=desc;
        this.type=type;
    }

    public Integer getType(){
         return type;
    }
    public Long getLongType(){
         return Long.valueOf(type);
    }
    public String getDesc(){
         return desc;
    }

}
