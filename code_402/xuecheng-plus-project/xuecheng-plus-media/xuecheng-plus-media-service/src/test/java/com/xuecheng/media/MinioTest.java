package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
 * @description 测试MinIO的工具类库(SDK)
 * @author Mr.M
 * @date 2022/9/11 21:24
 * @version 1.0
 */
public class MinioTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000") //minio地址
                    .credentials("minioadmin", "minioadmin") // 账号和密码
                    .build();

    //上传文件
    @Test
    public  void upload() {
        // 通过扩展名得到媒体资源类型 mimeType
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".m4a");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        try {
            // 上传文件的参数信息
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket") //在minio上创建的桶
                    .filename("F:\\音频\\1.m4a") // 指定要上传的本地文件路径
//                    .object("1.m4a")//对象名，在桶下（根目录）存储该文件
                    .object("test/1.m4a")// 放在子目录下(上传到哪个目录中)
//                    .contentType("audio/m4a")//设置媒体文件类型
                    .contentType(mimeType)//设置媒体文件类型
                    .build();
            //上传文件
            minioClient.uploadObject(uploadObjectArgs);//使用uploadObject方法上传文件，该方法需要参数,在上面定义
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }

    }

    @Test
    public void test_delete(){
        try {
//            minioClient.removeObject(
//                    RemoveObjectArgs.builder().bucket("testbucket").object("001/test001.mp4").build());
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                            .bucket("testbucket")
                            .object("test/1.m4a")
                            .build(); //如果是
            //删除文件
            minioClient.removeObject(removeObjectArgs);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }

    // 查询文件 从minio中下载
    @Test
    public void test_getFile() throws Exception{
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("test/1.m4a").build();
        //从minio得到输入流（查询远程服务获取到一个流对象）
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        // 指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("F:\\音频\\1_test.m4a"));
        //把输入流拷贝到输出流中，就完成了下载
        IOUtils.copy(inputStream,outputStream);
        //校验下载的文件的完整性 ，对文件内容进行md5,原始文件和下载的本地文件都求一下md5值，如果md5一样，那么就是完整的 (原理就是计算 流 的Md5) （注意：不能计算minio上的流，因为网络流不稳定，要计算本地上的原始文件）
        String source_md5 = DigestUtils.md5Hex(new FileInputStream("F:\\音频\\1.m4a")); //原始文件的Md5
//        FileInputStream fileInputStream2 = new FileInputStream("F:\\音频\\1_test.m4a");
        String local_md5 = DigestUtils.md5Hex(new FileInputStream("F:\\音频\\1_test.m4a"));
        if(source_md5.equals(local_md5)){
            System.out.println("下载成功！");
        }
    }

}
