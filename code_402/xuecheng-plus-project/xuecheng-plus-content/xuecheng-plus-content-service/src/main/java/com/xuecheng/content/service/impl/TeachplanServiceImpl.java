package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @description 课程计划service接口实现类
 * @author Mr.M
 * @date 2022/9/9 11:14
 * @version 1.0
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    /**
     * @description 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     * @author Mr.M
     * @date 2022/9/9 13:43
     */
    private int getTeachplanCount(Long courseId,Long parentId){
        //确定排序字段，找到它的同级节点个数，排序字段就是个数+1 select count(1) from teachplan where course_id=117 and parentid=268;
        LambdaQueryWrapper <Teachplan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 根据courseid和parantid来查询大章节或小节 （若parentid为0，则查询的是该课程的大章节）
        LambdaQueryWrapper<Teachplan> eq = lambdaQueryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        // 查找大章节或小节的个数
        Integer count = teachplanMapper.selectCount(eq);
        return count;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        // 通过课程计划id判断是新增还是修改
        Long teachplanId = saveTeachplanDto.getId();
        if(teachplanId==null){
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确定排序字段，找到它的同级节点个数，排序字段就是个数+1 select count(1) from teachplan where course_id=117 and parentid=268;
            Long courseId = saveTeachplanDto.getCourseId(); // 找到courseid
            Long parentid = saveTeachplanDto.getParentid(); // 找到parentid
//            LambdaQueryWrapper <Teachplan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            // 根据courseid和parantid来查询大章节或小节 （若parentid为0，则查询的是该课程的大章节）
//            LambdaQueryWrapper<Teachplan> eq = lambdaQueryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
//            // 查找大章节或小节的个数
//            Integer count = teachplanMapper.selectCount(eq);
            int teachplanCount = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(teachplanCount+1);
            teachplanMapper.insert(teachplan);
        }else{
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            //将参数复制到teachplan
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }

    }
}
