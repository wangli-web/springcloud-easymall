package cn.tedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EasymallEurekaServer01Application {

    public static void main(String[] args) {
        SpringApplication.run(EasymallEurekaServer01Application.class, args);
    }

}
