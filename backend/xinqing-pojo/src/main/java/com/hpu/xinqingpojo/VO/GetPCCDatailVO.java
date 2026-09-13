package com.hpu.xinqingpojo.VO;

import com.hpu.xinqingpojo.entity.Doctor;
import com.hpu.xinqingpojo.entity.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPCCDatailVO {
    private String phoneNumber;
    private String email;
    private String location;
    private String publicAccount;
    private String method;
    private String school;
    private String description;
    private String province;
    private String src;
    private List<Doctor> doctorList;
    private List<TimeSlot> timeSlots;
}
