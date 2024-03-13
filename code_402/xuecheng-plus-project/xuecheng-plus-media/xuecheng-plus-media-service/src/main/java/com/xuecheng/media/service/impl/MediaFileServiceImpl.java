package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaFileService currentProxy;

    @Autowired
    MinioClient minioClient;

    ///minio上存储普通文件的桶
    @Value("${minio.bucket.files}") //从nacos中的media-service-dev.yaml配置中拿到桶名
    private String bucket_mediafiles;
   //minio上存储视频的桶
    @Value("${minio.bucket.videofiles}") //从nacos中的media-service-dev.yaml配置中拿到桶名
    private String bucket_video;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        // 2024/03/12/
        return folder;
    }

    //获取文件的md5
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //根据扩展名取出mimeType
    private String getMimeType(String extension) {
        if(extension==null){
            extension = "";
        }
        // 通过扩展名得到媒体资源类型 mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * @description 将文件上传到minio
     * @param localFilePath  文件本地路径
     * @param mimeType  媒体类型
     * @param bucket  桶
     * @param objectName 对象名称
     * @return void
     * @author Mr.M
     * @date 2022/10/12 21:22
     */

    public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName){
        try {
            // 需要上传的文件的参数信息
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket) //在minio上创建的桶
                    .filename(localFilePath) // 指定要上传的本地文件路径
    //                    .object("1.m4a")//对象名，在桶下（根目录）存储该文件
                    .object(objectName)// 放在子目录下(上传到哪个目录中)
    //                    .contentType("audio/m4a")//设置媒体文件类型
                    .contentType(mimeType)//设置媒体文件类型
                    .build();
            //上传文件到minio
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功！bucket:{}, objectName:{}",bucket,objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错, bucket:{}, objectName:{}, 错误信息：{}",bucket,objectName,e.getMessage());
        }
        return false;
    }

    /**
     * @description 将文件信息添加到数据库表
     * @param companyId  机构id
     * @param fileMd5  文件md5值
     * @param uploadFileParamsDto  上传文件的信息
     * @param bucket  桶
     * @param objectName 对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    //如果在addMediaFilesToDb方法之前加了事务控制，想要事务控制生效，必须是代理对象来调用
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5); //文件id是其md5值
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5); //文件id
            mediaFiles.setFileId(fileMd5); //file_id
            mediaFiles.setCompanyId(companyId); //机构id
            mediaFiles.setUrl("/" + bucket + "/" + objectName); // url
            mediaFiles.setBucket(bucket); //桶
            mediaFiles.setFilePath(objectName); //file_path
            mediaFiles.setCreateDate(LocalDateTime.now()); //审核状态
            mediaFiles.setAuditStatus("002003"); //状态 , 审核通过
            mediaFiles.setStatus("1"); //状态,1:正常
            //保存文件信息到数据库表，例如 xc402_media.media_files
            int insert = mediaFilesMapper.insert(mediaFiles);
//            int i = 1/0;
            if (insert <= 0) {
                log.error("保存文件信息到数据库失败, bucket:{}, objectName:{}",bucket,objectName);
                XueChengPlusException.cast("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

        }
        return mediaFiles;

    }


    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        // 1. 将文件上传到minio

        // 文件名
        String filename = uploadFileParamsDto.getFilename();
        //得到扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 得到mimetype
        String mimeType = getMimeType(extension);
        // 把文件放到以当前日期为目录的文件夹下(即，设置子目录)
        String defaultFolderPath = getDefaultFolderPath();
        //给文件取名，可以用文件的md5值来取名
        File file = new File(localFilePath);
        if(!file.exists()){
            XueChengPlusException.cast("文件不存在");
        }
        String fileMd5 = getFileMd5(file); // 得到文件的md5值
        String objectName = defaultFolderPath+fileMd5+extension;
        //上传文件到minio
        boolean result = addMediaFilesToMinIO(localFilePath, mimeType, bucket_mediafiles, objectName);
        if(!result){
            XueChengPlusException.cast("上传文件失败");
        }

        //2. 将文件信息保存到数据库
        // 入库文件信息
//        MediaFiles mediaFiles = addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName);
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName); //如果在addMediaFilesToDb方法之前加了事务控制，想要事务控制生效，必须是代理对象来调用

        if(mediaFiles==null){
            XueChengPlusException.cast("文件上传后保存信息失败");
        }
        //准备返回的对象
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
        return uploadFileResultDto;
    }
}
