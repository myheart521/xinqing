package com.hpu.xinqing.manage.TeacherAgent.template;

import cn.hutool.core.util.ObjectUtil;
import com.hpu.xinqing.service.IBlogService;
import com.hpu.xinqingpojo.VO.BlogVO;
import com.hpu.xinqingpojo.entity.Blog;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentBlogQuery extends AbstractStudentQueryTemplate {

    @Resource
    private IBlogService blogService;


    @Override
    protected List<BlogVO> fetchData(Long userId, Integer count) {
        return blogService.selectBlogByUserId(userId, ObjectUtil.defaultIfNull(count, 2));
    }

    @Override
    protected String formatData(List<?> dataList) {
        if (ObjectUtil.isEmpty(dataList)) {
            return "该学生暂无发布动态";
        }
        //将dataList转换为List<BlogVO>
        List<BlogVO> blogVOList = (List<BlogVO>) dataList;
        //循环遍历格式化
        StringBuilder sb = new StringBuilder();
        for (BlogVO blogVO : blogVOList) {
            sb.append(formatBlog(blogVO)).append("\n");
        }
        return sb.toString();
    }


    private String formatBlog(BlogVO blogVO) {
        return "动态标题：" + blogVO.getTitle() +
                "，动态内容：" + blogVO.getContent() +
                "，收藏数量" + blogVO.getCollectionCount() +
                "，浏览量" + blogVO.getViewUserCount() +
                "，动态标签：" + blogVO.getLabel() +
                "，动态发布时间：" + blogVO.getCreateTime();
    }
}
