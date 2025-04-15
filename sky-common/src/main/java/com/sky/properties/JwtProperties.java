package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ConfigurationProperties(prefix = "sky.jwt") 是 Spring Boot 框架中的一个注解，表示当前类是一个配置属性类。
 * 类的作用就是封装配置文件中的一些配置项
 * 用于将配置文件（如 application.yml 或 application.properties）中的属性值 批量绑定到一个 Java 对象中。
 * prefix = "sky.jwt" 表示会绑定配置文件中所有以 sky.jwt 开头的属性
 * sky.jwt.admin-secret-key → 对应类的 adminSecretKey 字段（自动将 admin-secret-key 转换为 adminSecretKey）。
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;// jwt密钥
    private long adminTtl;// jwt有效期
    private String adminTokenName;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
