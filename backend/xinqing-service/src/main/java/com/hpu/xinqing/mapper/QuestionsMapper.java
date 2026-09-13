package com.hpu.xinqing.mapper;

import com.hpu.xinqingpojo.entity.Questions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionsMapper {
    @Select("select * from questions where test_id=#{testId} order by question_number")
    List<Questions> getByTestId(Long testId);

    @Select("select * from questions where test_id=#{testId} and is_reverse=1 order by question_number")
    List<Questions> getByTestIdAndReverse(Long id);
}
