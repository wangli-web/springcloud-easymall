package cn.tedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EasymallEurekaServer02Application {

    public static void main(String[] args) {
        SpringApplication.run(EasymallEurekaServer02Application.class, args);
    }

}
