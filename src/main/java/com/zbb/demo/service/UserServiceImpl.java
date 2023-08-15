package com.zbb.demo.service;


import com.zbb.demo.dao.TuserDao;
import com.zbb.demo.entity.User;
import com.zbb.demo.util.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private TuserDao tuserDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate redisTemplate;  //存储对象


    @Override
    public User findOne(Integer id) {
        Boolean mq_user = redisTemplate.opsForHash().hasKey("mq_user", String.valueOf(id));
        if (mq_user) {
            User mqUser = (User) redisTemplate.opsForHash().get("mq_user", String.valueOf(id));
            log.info("redis中用户信息为: {}",mqUser);
            return mqUser;
        }
        return tuserDao.findOne(id);
    }

    @Override
    public List<User> findAll() {
        return tuserDao.findAll();
    }

    @Override
    public void insertUser(User user) {
        tuserDao.insertUser(user);
        //生产消息
        rabbitTemplate.convertAndSend("mq_user","", JSONUtils.writeValueAsString(user));
    }

    @Override
    public void updateUser(User user) {
        if (!ObjectUtils.isEmpty(tuserDao.findOne(Integer.valueOf(user.getId())))) {
            tuserDao.updateUser(user);
            //生产消息
            rabbitTemplate.convertAndSend("mq_user","", JSONUtils.writeValueAsString(user));
        }
    }

    @Override
    public void testExcel(User user) {

    }

}
