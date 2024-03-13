
package com.xuecheng.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description minio的配置类
 * @author Mr.M
 * @date 2022/9/12 19:32
 * @version 1.0
 */
@Configuration
public class MinioConfig {

//去nacos上的media-service-dev.yaml查看来得到该值
    @Value("${minio.endpoint}") // minio访问地址
    private String endpoint;
    @Value("${minio.accessKey}") //minio账号
    private String accessKey;
    @Value("${minio.secretKey}") // minio密码
    private String secretKey;


    @Bean
    //将minioClient对象放到Spring容器中
    public MinioClient minioClient() {

        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }
}
