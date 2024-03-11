package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @description 课程分类树型结点dto
 * @author Mr.M
 * @date 2022/9/7 15:16
 * @version 1.0
 */

@Data
@ToString
// 文档中的childrenTreeNodes
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    //根据接口文档，发现是个树状结构，因此还要定义子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;

}
