package com.hpu.xinqing.manage.TeacherAgent.template;

import cn.hutool.core.util.ObjectUtil;
import com.hpu.xinqing.service.IActivityService;
import com.hpu.xinqingpojo.entity.Activity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentActivityQuery extends AbstractStudentQueryTemplate {
    @Resource
    private IActivityService activityService;

    @Override
    protected List<?> fetchData(Long userId, Integer count) {
        // 差异化实现：查询活动
        return activityService.selectActiveListByUserId(userId, ObjectUtil.defaultIfNull(count, 2));
    }

    @Override
    protected String formatData(List<?> dataList) {
        if (ObjectUtil.isNotEmpty(dataList)) {
            //遍历列表拼接字符串
            StringBuilder sb = new StringBuilder();
            for (Object activity : dataList) {
                sb.append(this.formatActivityInfo((Activity) activity)).append("\n");
            }
            return sb.toString();
        } else {
            return "该学生未参与活动";
        }
    }


    private String formatActivityInfo(Activity activity) {
        return "活动名称：" + activity.getTitle()
                + "，活动内容：" + activity.getContent()
                + "，活动地点：" + activity.getAddress()
                + "，活动时间：" + activity.getStartTime() + "至" + activity.getEndTime()
                + "，活动发起人：" + activity.getUserId()
                + "，活动标签：" + activity.getTag();
    }
}


