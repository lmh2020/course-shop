package com.company.common.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.company.common.config.OssConfig;
import com.company.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @Description
 */
@Slf4j
@Component
public class AliOssClient {

    @Autowired
    private OssConfig ossConfig;

    private OSS ossClient;

    @PostConstruct
    private final void init() {
        // 创建OSSClient实例。
        ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getId(), ossConfig.getSecret());
    }

    public String simpleUpload(MultipartFile file) {
        // 创建PutObjectRequest对象。
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        try {
            String fileName = this.generateFileName(file.getOriginalFilename());
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossConfig.getBucket(), fileName, file.getInputStream());
            ossClient.putObject(putObjectRequest);
            String url = ossClient.generatePresignedUrl(ossConfig.getBucket(), fileName, new Date(System.currentTimeMillis() + ossConfig.getExpire())).toString();
            log.info("文件上传成功， 访问路径： {}", url);
            return url;
        } catch (Exception e){
            log.warn("文件上传异常", e);
            throw new ServiceException("文件上传失败");
        }
    }


    /**
     * @description 生成文件名（避免文件名重复） 如：1376831577992024066-video2.mp4
     */
    private String generateFileName(String originalFilename){
        return IdWorker.getId() + "-" + originalFilename;
    }

    public void shutdown() {
        ossClient.shutdown();
    }

}
