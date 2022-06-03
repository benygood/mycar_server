package com.mycar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableCaching
public class MycarApplication extends SpringBootServletInitializer implements ApplicationRunner {
    private static final Logger logger= LogManager.getLogger(MycarApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MycarApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("iot car start.....");

    }
}
