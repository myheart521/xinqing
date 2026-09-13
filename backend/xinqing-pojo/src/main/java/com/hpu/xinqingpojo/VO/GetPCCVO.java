package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPCCVO {
    private long id;
    private String phoneNumber;
    private String email;
    private String location;
    private String school;
    private String src;
    private String province;
}
