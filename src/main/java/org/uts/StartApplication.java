package org.uts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @Author 86180
 * @Date 2022/12/7 23:11
 * @Version 1.0
 * @Description: uts-seckill启动类
 **/
@SpringBootApplication
@EnableCaching
@Slf4j
public class StartApplication implements CommandLineRunner {

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("===================================================");
        log.info("      [" + appName + "] SERVER START SUCCESS       ");
        log.info("===================================================");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
