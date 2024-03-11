package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper; // 课程基本信息的mapper
    @Autowired
    CourseMarketMapper courseMarketMapper; // 课程营销信息的mapper
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto){
        // 测试分页查询
        // selectPage需要page和查询条件两个参数,因此:
        // 1.拼装查询条件 使用wrapper对象
        // 2. 创建page分页参数对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称模糊查询,在sql中拼接 course_base.name like '%值%'
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());

        //根据课程审核状态查询,在sql中拼接 course_base.audit_Status = ?
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());

        // todo：按照课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        // 创建Page分页参数对象， 参数：当前页码， 每页记录数
        //Page<CourseBase> page = new Page<>(1,2);
        //分页参数对象
        // Page分页参数对象， 参数：当前页码， 每页记录数
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
        return courseBasePageResult;
    }

    // 用户添加课程信息，涉及到数据库中course_base表和course_market的写入
    @Transactional //事务控制注解 凡是涉及增删改的操作，都需要加上事物控制注解
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        // 用户填入内容是否合法？ 参数的合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            //throw new RuntimeException("课程名称为空");
            XueChengPlusException.cast("课程名称为空"); //抛出我们自己定义的异常类型

        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }

        // 1. 向数据库课程基本信息表course_base写入数据
        // new 一个对象
        CourseBase courseBaseNew = new CourseBase();
        //将传入的页面的参数放到courseBaseNew对象
//        courseBaseNew.setName(dto.getName());
//        courseBaseNew.setDescription(dto.getDescription());
        // 上面这种写法从原始对象中拿数据向新对象set，太复杂
        BeanUtils.copyProperties(dto,courseBaseNew); //只要属性名称一致就可以从dto中拷贝，但是如果dto中的某个属性其值为空，而courseBaseNew中该属性中有值，那么会被空覆盖
        courseBaseNew.setCompanyId(companyId); // 该句必须在BeanUtils.copyProperties(dto,courseBaseNew)的下面，因为dto中没有companyId属性，若放在前面会被空覆盖

        courseBaseNew.setCreateDate(LocalDateTime.now()); // 创建时间取当前时间
        // 由于不是修改操作，所以修改时间不用管,创建人和更新人是当前登录的用户，也先不用管
        // 默认审核状态为未提交，发布状态为未发布 (可以去查询dictionary表来看编码)
        courseBaseNew.setAuditStatus("202002");
        courseBaseNew.setStatus("203001");
        // 向数据库中基本信息表course_base插入数据
        int insert = courseBaseMapper.insert(courseBaseNew); //返回的插入的记录行
        if(insert<=0){
            throw new RuntimeException("添加课程信息失败");
        }

        // 2. 向数据库课程营销表course_market插入数据
        CourseMarket courseMarketNew = new CourseMarket();
        // course_market和course_base主键(课程id)相同，并是一对一关系
        Long courseId = courseBaseNew.getId();
        // 将页面输入信息拷贝到courseMarketNew
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        int insert2 = saveCourseMarket(courseMarketNew);
        if(insert2<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }
        // 从数据库中查询课程的详细信息，返回该信息
        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfo(courseId);
        return courseBaseInfoDto;
        //return getCourseBaseInfo(courseId);

    }
    //单独写一个方法保存营销信息，逻辑：存在即更新，不存在则添加
    private int saveCourseMarket(CourseMarket courseMarketNew){
        // 1. 参数的合法性校验
        // 2. 从数据库中查询营销信息，存在则更新，不存在则添加
        // 收费规则
        String charge  = courseMarketNew.getCharge(); // 添加的课程是收费的还是免费的？
        if(StringUtils.isEmpty(charge)){
            throw new RuntimeException("收费规则未选择");
        }
        // 收费规则为收费，若没有填写价格，也需要抛出异常
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice()==null || courseMarketNew.getPrice().floatValue()<=0){
                // throw new RuntimeException("课程的收费价格不能为空且必须大于0");
                XueChengPlusException.cast("课程的收费价格不能为空且必须大于0");

            }
        }
        // 根据id从课程营销表查询
        //CourseMarket courseMarketObjTemp = courseMarketMapper.selectById(121);
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarketObj==null){ //如果表中没有该营销信息，则插入
            return courseMarketMapper.insert(courseMarketNew);
        }else{
            // 如果表中已有记录，则更新
            // 将courseMarketNew拷贝到courseMarketObj
            BeanUtils.copyProperties(courseMarketNew,courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId()); // ???这句有必要吗？ 视频中说有必要，因为courseMarketNew可能没有主键(id)，那么此时courseMarketObj的主键就会被覆盖为null。 但是在此处，我感觉还是有问题
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }

    // 由于在createCourseBase方法中，除了要在数据库表中写入数据，还要返回一个CourseBaseInfoDto类型的数据，下列方法目的是创建该类型的对象
    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(long courseId){
        // 1.从课程基本信息表查询
        // 2.从课程营销表查询
        // 3.组装在一起
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
       // CourseBase courseBasetemp = courseBaseMapper.selectById(121);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //todo：课程的分类的名称设置到courseBaseInfoDto中
        CourseCategory courseCategorySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategorySt.getName()); //小分类
        CourseCategory courseCategoryMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryMt.getName()); // 大分类
        return courseBaseInfoDto;
    }
}
