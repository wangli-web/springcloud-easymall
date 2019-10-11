package cn.tedu.user.cservice;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.utils.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.common.pojo.User;
import com.jt.common.utils.MD5Util;

import cn.tedu.user.mapper.UserMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public int checkUserExists(String userName) {
        //返回1存在 0不存在
        return userMapper.selectUserCountByUserName(userName);
    }

    public void doRegister(User user) {
        //补齐一个userId
        user.setUserId(UUID.randomUUID().toString());
        //对密码进行加密
        //对密码加密方式 安全可以使用md5加盐
        //user.getUserPassword
        user.setUserPassword(MD5Util.md5(user.getUserPassword()));
        userMapper.insertUser(user);
    }

//    @Autowired(required = false)
//    private ShardedJedisPool pool;
    @Autowired
    private JedisCluster jedis;

    public void ff(){
        System.out.println(jedis);
    }

    public String doLogin(User user) {
        //判断登录权限校验 select where user_name and password
        //加密
        user.setUserPassword(MD5Util.md5(user.getUserPassword()));
        User exist = userMapper.selectUserByUserNameAndPassword(user);
        String loginKey = "login_" + user.getUserName();
        String newTicket = "";
        //判断loginKey是不是存在
        if (jedis.exists(loginKey)) {
            //曾经有人登陆过
            //将oldTicket
            String oldTicket = jedis.get(loginKey);
            jedis.del(oldTicket);
        }
        //正常设置newTicket-userJson
        try {
            newTicket = "EM_TICKET" + System.currentTimeMillis() + exist.getUserId();
            //对象转化userJson
            String userJson = MapperUtil.MP.writeValueAsString(exist);
            jedis.setex(newTicket, 60 * 60 * 2, userJson);
            //设置有效的ticket使用
            jedis.setex(loginKey, 60 * 60 * 2, newTicket);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return newTicket;


		/*String ticket="";
		//判断对象是否存在
		if(exist==null){
			//登录失败
			return ticket;
		}else{
			//登录成功
			//生成ticket redis的key,生成value userJson
			//ticket生成公式:”EM_TICKET”+currentTime+userId;
			ticket="EM_TICKET"
			+System.currentTimeMillis()+exist.getUserId();
			//Jedis jedis =new Jedis("10.9.39.13",6379);
			//ShardedJedis jedis = pool.getResource();
			try{
				String userJson=
				MapperUtil.MP.writeValueAsString(exist);
				//设置超时存储
				jedis.setex(ticket, 60*60*2, userJson);
			}catch(Exception e){
				e.printStackTrace();
				return "";
			}finally{
				//jedis.close();
				pool.returnResource(jedis);
			}
			//存储在redis,将ticket返回
			return ticket;
		}*/
    }

    public String queryTicket(String ticket) {
        //判断剩余时间
        Long leftTime = jedis.pttl(ticket);
        if (leftTime < 1000 * 60 * 30) {//小于30分钟
            //续约 续约1小时
            jedis.pexpire(ticket, leftTime + 1000 * 60 * 60);
        }
        return jedis.get(ticket);

		/*Jedis jedis=new Jedis("10.9.39.13",6379);
		//ShardedJedis jedis = pool.getResource();
		try{
			return jedis.get(ticket);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			pool.returnResource(jedis);
		}*/
    }
}
