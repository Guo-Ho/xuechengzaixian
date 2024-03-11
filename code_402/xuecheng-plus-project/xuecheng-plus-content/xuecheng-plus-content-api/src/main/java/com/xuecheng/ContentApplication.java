package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @description 内容管理服务启动类
 * @author Mr.M
 * @date 2022/9/6 14:15
 * @version 1.0
 */
@EnableSwagger2Doc //生成swagger接口文档
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
