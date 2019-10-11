package cn.tedu;

import cn.tedu.user.cservice.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import redis.clients.jedis.JedisCluster;

@SpringBootApplication
@MapperScan("cn.tedu.user.mapper")
@EnableEurekaClient
public class EasymallUser01Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(EasymallUser01Application.class, args);
        run.getBean(UserService.class).ff();
    }

}
