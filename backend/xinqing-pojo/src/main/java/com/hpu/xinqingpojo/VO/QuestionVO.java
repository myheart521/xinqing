package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVO {
    private Integer questionNumber;//第几题
    private String question;//题目是什么
    private String[] options;//每一道题都有哪些选项
    private String[] val;//这些选项代表哪些分数
}
