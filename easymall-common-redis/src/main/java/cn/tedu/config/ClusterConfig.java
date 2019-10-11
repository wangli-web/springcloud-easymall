package cn.tedu.config;

import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "redis.cluster")
@Data
public class ClusterConfig {
    //四个属性
    private List<String> nodes;
    private Integer maxTotal;
    private Integer maxIdle;
    private Integer minIdle;

    //初始化读取属性后创建的bean对象JedisCluster
    @Bean
    public JedisCluster initJedisCluster() {
        //收集节点信息
        Set<HostAndPort> set = new HashSet<HostAndPort>();
        for (String node : nodes) {
            //每次循环拿到 ip:port
            String host = node.split(":")[0];
            int port = Integer.parseInt(node.split(":")[1]);
            set.add(new HostAndPort(host, port));
            System.out.println(host+"------------------------"+port);
        }
        //配置对象
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        //创建JedisCluster对象
        System.out.println("********************************************");
        JedisCluster cluster = new JedisCluster(set, config);
        System.out.println(cluster);
        return cluster;
    }
}
