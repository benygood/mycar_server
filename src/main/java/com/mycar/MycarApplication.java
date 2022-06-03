package com.mycar;

import com.mycar.AudioTo.AudioToNettyTcpServer;
import com.mycar.audioFrom.AudioFromNettyTcpServer;
import com.mycar.image.ImageNettyTcpServer;
import com.mycar.rabbit.TransferMQClient;
import com.rabbitmq.client.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    ImageNettyTcpServer imageNettyTcpServer;
    @Autowired
    AudioFromNettyTcpServer audioFromNettyTcpServer;
    @Autowired
    AudioToNettyTcpServer audioToNettyTcpServer;
    public static void main(String[] args) {
        SpringApplication.run(MycarApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("iot car start.....");
        imageNettyTcpServer.start();
        audioFromNettyTcpServer.start();
        audioToNettyTcpServer.start();
        Channel instance = TransferMQClient.getInstance();
        TransferMQClient.execute(instance);

    }
}
