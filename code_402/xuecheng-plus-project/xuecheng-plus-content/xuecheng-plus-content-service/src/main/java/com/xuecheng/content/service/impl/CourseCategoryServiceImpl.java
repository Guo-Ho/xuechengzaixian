package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    public List<CourseCategoryTreeDto> queryTreeNodes(String id){
        // 调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto>  courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id); //此时，未包含childrenTreeNodes
        // 因此需要找到每个节点的childrenTreeNodes，最后封装成List<CourseCategoryTreeDto>类型

        //1.先将list转成map，key就是节点的id，value就是CourseCategoryTreeDto对象，目的就是为了方便map获取节点

        //2.从头开始遍历List<CourseCategoryTreeDto>，一边遍历一边找子节点放在父节点的childrenTreeNodes中

        // 1.
        // 将List转成map的方式，最直观的就是遍历List，然后Map.put，  此处使用Stream流的方式
        //Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().collect(Collectors.toMap(key->key.getId(),value->value,(key1,key2)->key2));
        // 但是需要将根节点过滤掉,过滤条件filter中的内容为此时的id不等于传进来的id（因为传进来的id为1，是根节点） filter(item->!id.equals(item.getId()))把根节点排除
        // filer条件不满足时，不会从stream流中取该条数据，因此会接着从流中取下一个.(key1,key2)->key2)表示往map put数据时，如果map中已经存在了key对应的该条数据，那么用该条数据覆盖前面的数据
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).collect(Collectors.toMap(key->key.getId(),value->value,(key1,key2)->key2));

        // 定义一个list，作为最终返回的list
        List<CourseCategoryTreeDto> courseCategoryList= new ArrayList<>();
        //2.
        // 同样可以是用stream流的方式来从头遍历
        courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item->{
            // 向list写入元素
            // 判断如果该item的父节点是传入的'1'，那么将这些item作为第一级别的目录，放入list
            if(item.getParentid().equals(id)){
                courseCategoryList.add(item);
            }
            // 找到节点的父节点
            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid()); // 在map中已经将根节点'1'过滤掉了，map中不存在根节点'1'
            if(courseCategoryTreeDto!=null){
                // 如果该父节点的ChildrenTreeNodes属性为空，需要new一个集合，因为要向该集合中放它的子节点
                if(courseCategoryTreeDto.getChildrenTreeNodes()==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                // 找到每个节点的子节点，放在该节点的childrenTreeNodes属性中
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }

        });
        return courseCategoryList;

    }

}
