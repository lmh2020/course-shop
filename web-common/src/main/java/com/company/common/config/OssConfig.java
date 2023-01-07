package com.company.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssConfig {

    private String endpoint;

    private String id;

    private String secret;

    private String bucket;

    private Long expire;

}
