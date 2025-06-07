package cn.edu.sdu.java.server.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FileStorageConfig {
    private static final Logger log = LoggerFactory.getLogger(FileStorageConfig.class);

    @Value("${attach.folder}")
    private String attachFolder;

    @Bean
    public CommandLineRunner initStorageFolders() {
        return args -> {
            log.info("初始化文件存储目录");

            // 确保主目录存在
            File mainDir = new File(attachFolder);
            if (!mainDir.exists()) {
                boolean created = mainDir.mkdirs();
                log.info("创建主目录 {}: {}", mainDir.getAbsolutePath(), created ? "成功" : "失败");
            } else {
                log.info("主目录已存在: {}", mainDir.getAbsolutePath());
            }

            // 确保照片目录存在
            File photoDir = new File(attachFolder + "photo");
            if (!photoDir.exists()) {
                boolean created = photoDir.mkdirs();
                log.info("创建照片目录 {}: {}", photoDir.getAbsolutePath(), created ? "成功" : "失败");
            } else {
                log.info("照片目录已存在: {}", photoDir.getAbsolutePath());
            }

            // 检查目录权限
            if (!mainDir.canWrite()) {
                log.warn("主目录不可写: {}", mainDir.getAbsolutePath());
            }

            if (!photoDir.canWrite()) {
                log.warn("照片目录不可写: {}", photoDir.getAbsolutePath());
            }

            // 记录当前工作目录，帮助调试
            log.info("当前工作目录: {}", new File(".").getAbsolutePath());
        };
    }
}
