package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 课程信息编辑接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Api(value = "课程信息管理接口",tags = "课程信息管理接口")
@RestController //响应json数据，相当于@Controller和@ResponseBody
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @ApiOperation("课程查询接口")
    // @RequestMapping("/course/list")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
//        CourseBase courseBase = new CourseBase();
//        courseBase.setName("测试名称");
//        courseBase.setCreateDate(LocalDateTime.now());
//        List<CourseBase> courseBases = new ArrayList();
//        courseBases.add(courseBase);
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams,queryCourseParamsDto);
//        PageResult pageResult = new PageResult<CourseBase>(courseBases,10,1,10);

        return courseBasePageResult;

    }

    @ApiOperation("新增课程基础信息")
    // @RequestMapping("/course/list")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){ // @RequestBody:将请求的json数据转为java对象
        // 获取用户所属的机构的id
        Long companyId = 1232141425L;
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBase;

    }

}
