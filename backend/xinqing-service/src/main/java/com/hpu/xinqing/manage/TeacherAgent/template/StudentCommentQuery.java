package com.hpu.xinqing.manage.TeacherAgent.template;

import com.hpu.xinqing.service.ICommentsService;
import com.hpu.xinqingpojo.entity.Comments;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentCommentQuery extends AbstractStudentQueryTemplate{
    @Resource
    private ICommentsService commentsService;
    @Override
    protected List<?> fetchData(Long userId, Integer count) {
        return commentsService.queryCommentByUserId(userId,count);
    }

    @Override
    protected String formatData(List<?> dataList) {
        List<Comments> comments = (List<Comments>) dataList;
        StringBuilder sb = new StringBuilder();
        //循环遍历格式化输出
        for(Comments comments1:comments){
            sb.append(formatComment(comments1)).append("\n");
        }
        return sb.toString();
    }


    private String formatComment(Comments comments){
        return "评论内容"+comments.getContent()+
                "评论时间"+comments.getCreateTime()+
                "评论点赞数"+comments.getLiked();
    }
}
