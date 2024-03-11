package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
public class CourseBaseInfoServiceTests {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Test
    public void testCourseBaseInfoService(){
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");//课程名称查询条件
        queryCourseParamsDto.setAuditStatus("202004"); //课程审核状态
        queryCourseParamsDto.setPublishStatus("203002"); //课程发布状态
        //分页参数对象
        PageParams pageParams =  new PageParams();
        pageParams.setPageNo(1L); //页码
        pageParams.setPageSize(2L); //每页记录数
        // CourseBaseInfoServiceImpl courseBaseInfoService = new CourseBaseInfoServiceImpl(); 为什么注入可以，new不行？
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        System.out.println(courseBasePageResult);
    }
}
