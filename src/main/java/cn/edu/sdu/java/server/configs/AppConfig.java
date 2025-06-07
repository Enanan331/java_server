package cn.edu.sdu.java.server.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class AppConfig {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Value("${attach.folder}")
    private String attachFolder;

    @Bean
    public CommandLineRunner initFolders() {
        return args -> {
            log.info("初始化文件夹结构");

            // 确保主文件夹存在
            File mainFolder = new File(attachFolder);
            if (!mainFolder.exists()) {
                boolean created = mainFolder.mkdirs();
                if (created) {
                    log.info("创建主文件夹: {}", mainFolder.getAbsolutePath());
                } else {
                    log.error("无法创建主文件夹: {}", mainFolder.getAbsolutePath());
                }
            }

            // 确保照片文件夹存在
            File photoFolder = new File(attachFolder + "photo");
            if (!photoFolder.exists()) {
                boolean created = photoFolder.mkdirs();
                if (created) {
                    log.info("创建照片文件夹: {}", photoFolder.getAbsolutePath());
                } else {
                    log.error("无法创建照片文件夹: {}", photoFolder.getAbsolutePath());
                }
            }

            // 检查文件夹权限
            if (!mainFolder.canWrite()) {
                log.error("主文件夹不可写: {}", mainFolder.getAbsolutePath());
            }

            if (!photoFolder.canWrite()) {
                log.error("照片文件夹不可写: {}", photoFolder.getAbsolutePath());
            }
        };
    }
}