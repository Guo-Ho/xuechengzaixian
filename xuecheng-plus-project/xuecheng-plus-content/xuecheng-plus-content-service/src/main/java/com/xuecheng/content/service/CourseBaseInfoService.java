package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @description 课程基本信息管理业务接口
 * @author Mr.M
 * @date 2022/9/6 21:42
 * @version 1.0
*/
public interface CourseBaseInfoService  {

    /*
     * @description 课程分页查询接口
     * @param pageParams 分页查询参数
     * @param queryCourseParamsDto 查询条件
     * @return 返回的查询结果
     * @author Mr.M
     * @date 2022/9/6 21:44
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /*
     * @description 新增课程接口
     * @param AddCourseDto 用户输入的课程信息
     * @param companyId 用户所属的机构id
     * @param queryCourseParamsDto 查询条件
     * @return 添加成功的课程详细信息
     * @author Mr.M
     * @date 2022/9/6 21:44
     */
    public CourseBaseInfoDto createCourseBase(Long companyId ,AddCourseDto addCourseDto); // 新增课程的接口
    // 在数据库course_base表中，有一个机构id字段，但是由于用户增加课程时，不会输入机构Id，因为机构id是用户登录时获取到的，因此AddCourseDto不包含机构id，需要增加一个companyId参数


}