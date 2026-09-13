package com.hpu.xinqing.controller;
import com.alibaba.fastjson.JSONObject;
import com.hpu.xinqing.service.MbtiService;
import com.hpu.xinqingpojo.entity.MbtiTopic;
import com.hpu.xinqingpojo.VO.MbtiTestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hpu.xinqingcommon.result.Result;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/manage")
public class MBTIController {

    @Autowired
    private MbtiService mbtiService;

    //查询全部
    @GetMapping("/getAll")
    public Result<List<MbtiTopic>> getAll() {
        //查询全部mbti测试题
        return Result.success(mbtiService.findAll());
    }
    @PostMapping("/submit")
    public Result<MbtiTestVO> calculateAns(@RequestBody JSONObject object) {
        //计算mbti计算结果
        String ans = object.getString("ans");
        return Result.success(mbtiService.calculateAns(ans));

    }
}
