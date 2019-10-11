package cn.tedu.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;

import redis.clients.jedis.*;

/**
 * 实现单个节点的连接底层对象
 * 分布式的连接实现对象
 *
 * @author tedu
 */
public class RedisJedisTest {
    /*使用jedis创建一个连接对象,操作6379
     */
	/*@Test
	public void connection(){
		Jedis jedis=new Jedis("10.42.10.101", 6379);
		//jedis可以连接到10.9.39.13:6379
		//redis-cli -h(host) 10.9.39.13 -p(port)6379
		jedis.hset(key, field, value);
		jedis.hmset(key, hash);
		jedis.sadd(key, members);
		jedis.set("name", "王老师");
		System.out.println(jedis.get("name"));
	}
	
	@Test
	public void hashN(){
		//模拟生成大量的数据,存储6379 6380 6381
		//准备3个连接对象
		Jedis jedis1=new Jedis("10.42.10.101",6379);
		Jedis jedis2=new Jedis("10.42.10.101",6380);
		Jedis jedis3=new Jedis("10.42.10.101",6381);
		for(int i=0;i<100;i++){
			String key="key_"+i;
			String value="value_"+i;
			int result=(key.hashCode()&Integer.MAX_VALUE)%6;
			if(result==0){//6370
				jedis1.set(key, value);
			}else if(result==1){
				//6380
				jedis2.set(key, value);
			}else{
				//6381
				jedis3.set(key, value);
			}
		}
	}
	
	@Test
	public void shardedJedis(){
		//使用jedis客户端实现将大量数据存储在6379 6380 6381
		//收集的所有节点
		List<JedisShardInfo> list=new ArrayList<JedisShardInfo>();
		list.add(new JedisShardInfo("10.42.10.101", 6379));
		list.add(new JedisShardInfo("10.42.10.101", 6380));
		list.add(new JedisShardInfo("10.42.10.101", 6381));
		//通过节点信息 创建分片对象
		ShardedJedis sJedis=new ShardedJedis(list);
		for(int i=0;i<1000;i++){
			String key=UUID.randomUUID().toString();
			sJedis.set(key, "");
			
			
		}
		//System.out.println(Integer.toBinaryString(666));

	}*/
    @Test
    public void pool() {
        //收集节点信息
        List<JedisShardInfo> list = new ArrayList<JedisShardInfo>();
        list.add(new JedisShardInfo("10.42.10.101", 6379));
        list.add(new JedisShardInfo("10.42.10.101", 6380));
        list.add(new JedisShardInfo("10.42.10.101", 6381));
        //使用连接池的配置对象,配置连接池的各种属性
        //最大空闲,最小空闲,最大连接数量...
        GenericObjectPoolConfig config =
                new GenericObjectPoolConfig();
        config.setMaxIdle(8);
        config.setMinIdle(3);
        config.setMaxTotal(200);
        //list config 构造一个包装了多个分片连接对象的连接池对象
        ShardedJedisPool pool =
                new ShardedJedisPool(config, list);
        //从池子中获取连接资源
        ShardedJedis sJedis = pool.getResource();
        sJedis.set("location", "北京");
    }

    @Test
    public void sentinel() {
        //收集哨兵节点信息,连接哨兵获取集群master-slave的使用信息
        //给一个哨兵,指定多个,会从中挑选一个可连接的节点使用
        Set<String> sentinels = new HashSet<String>();
        sentinels.add(new HostAndPort("10.42.10.101", 26379).toString());
        sentinels.add(new HostAndPort("10.42.10.101", 26380).toString());
        //哨兵连接池
        JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);
        System.out.println("现役master：" + pool.getCurrentHostMaster());

        Jedis jedis = pool.getResource();
        jedis.set("name", "刘老师");

        System.out.println(jedis.get("name"));
        pool.returnResource(jedis);

        pool.destroy();
        System.out.println("ok");
    }

    @Test
    public void jedisCluster() {
        //收集节点信息,至少提供一个
        Set<HostAndPort> clusterSet = new HashSet<HostAndPort>();
        clusterSet.add(new HostAndPort("10.42.10.101", 8000));
        //cluster对象底层使用连接池,使用config配合连接池属性
        GenericObjectPoolConfig
                config = new GenericObjectPoolConfig();
        config.setMaxTotal(200);

        //创建JedisCluster
        JedisCluster cluster =
                new JedisCluster(clusterSet, config);

        cluster.set("name", "王老师");
        System.out.println(cluster.get("name"));
    }


}
