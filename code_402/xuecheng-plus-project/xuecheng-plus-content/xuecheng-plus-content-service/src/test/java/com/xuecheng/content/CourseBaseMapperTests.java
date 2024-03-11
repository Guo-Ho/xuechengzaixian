package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class CourseBaseMapperTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Test
    public void testCourseBaseMapper(){
        CourseBase couseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(couseBase);
        // 测试分页查询
        // selectPage需要page和查询条件两个参数,因此:
        // 1.拼装查询条件 使用wrapper对象
        // 2. 创建page分页参数对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称模糊查询,在sql中拼接 course_base.name like '%值%'
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");//课程名称查询条件
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());

        //根据课程审核状态查询,在sql中拼接 course_base.audit_Status = ?
        queryCourseParamsDto.setAuditStatus("202004");
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());

        // todo：按照课程发布状态查询
        queryCourseParamsDto.setPublishStatus("203002");
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        // 创建Page分页参数对象， 参数：当前页码， 每页记录数
        //Page<CourseBase> page = new Page<>(1,2);
        //分页参数对象
        PageParams pageParams =  new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(2L);
        // 创建Page分页参数对象， 参数：当前页码， 每页记录数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        //开始进行分页查询
     //<E extends IPage<T>> E selectPage(E page, @Param("ew") Wrapper<T> queryWrapper);
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page,queryWrapper);
        // 由于在api中定义的接口是返回PageResult<CourseBase>类型
        // 而PageResult类中属性有 List<T> items, long counts, long page, long pageSize
        //数据列表
        List<CourseBase> items = pageResult.getRecords();
        // 总记录数counts
        long total =  pageResult.getTotal();

        PageResult<CourseBase>  courseBasePageResult = new PageResult<>(items, total,pageParams.getPageNo(),pageParams.getPageSize());
        System.out.println(courseBasePageResult);

    }
}
