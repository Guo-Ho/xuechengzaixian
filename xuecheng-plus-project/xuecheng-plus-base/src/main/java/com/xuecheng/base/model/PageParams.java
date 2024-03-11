package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @description 分页查询分页参数
 * @author Mr.M
 * @date 2022/9/6 14:02
 * @version 1.0
 */
@Data
@ToString
public class PageParams {
    //当前页码
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    //每页显示记录数
    @ApiModelProperty("每页显示记录数")
    private Long pageSize =30L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
