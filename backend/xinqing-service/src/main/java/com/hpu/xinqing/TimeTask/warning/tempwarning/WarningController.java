package com.hpu.xinqing.TimeTask.warning.tempwarning;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hpu.xinqing.service.ITempStudentService;
import com.hpu.xinqing.service.TempAlertLogsService;
import com.hpu.xinqing.service.TempEmotionRecordsService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.TempAlertLogsVO;
import com.hpu.xinqingpojo.VO.TempEmotionRecordsVO;
import com.hpu.xinqingpojo.VO.TempStudentVO;
import com.hpu.xinqingpojo.entity.TempAlertLogs;
import com.hpu.xinqingpojo.entity.TempEmotionRecords;
import com.hpu.xinqingpojo.entity.TempStudent;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warning")
public class WarningController {

    @Resource
    private TempEmotionRecordsService recordsService;

    @Resource
    private TempAlertLogsService alertLogsService;

    @Resource
    private ITempStudentService tempStudentService;

    /**
     * 获取检测记录分页
     *
     * @return
     */
    @GetMapping("record")
    public Result<IPage<TempEmotionRecordsVO>> record(@RequestParam(required = false, defaultValue = "1") int pageNum,
                                                      @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false) Long studentId) {
        IPage<TempEmotionRecordsVO> recordsByPage = recordsService.getRecordsByPage(pageNum, pageSize,studentId);
        return Result.success(recordsByPage);
    }

    /**
     * 获取预警的记录分页
     *
     * @return
     */
    @GetMapping("warning")
    public Result<IPage<TempAlertLogsVO>> warningByStudentId(
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long studentId
    ) {
        IPage<TempAlertLogsVO> alertLogsByPage = alertLogsService.getAlertLogsByPage(pageNum, pageSize, studentId);
        return Result.success(alertLogsByPage);
    }
    //根据用户查询
    @GetMapping("/warning/student")
    public Result<IPage<TempStudentVO>> warning(
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize
            ) {
        IPage<TempStudentVO> alertLogsByPage = tempStudentService.getStudentByPage(pageNum, pageSize);
        return Result.success(alertLogsByPage);
    }
    //点击去处理问题学生
    @PutMapping("/update/warning")
    public Result<String> update(@RequestParam Long studentId){
        boolean success=tempStudentService.updateByStudentId(studentId);
        return Result.success(success?"处理成功":"处理失败");
    }


}
