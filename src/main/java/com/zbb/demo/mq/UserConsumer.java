package com.zbb.demo.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbb.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;



@Component
@Slf4j
public class UserConsumer {


    @Resource
    private RedisTemplate redisTemplate;  //存储对象


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name=""),
            exchange = @Exchange(name="mq_user",type = "fanout")
    ))
    public void receive(String message) throws JsonProcessingException {
        log.info("MQ将接收用户信息为: {}",message);
        User user = new ObjectMapper().readValue(message, User.class);
        redisTemplate.opsForHash().put("mq_user",user.getId(),user);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name=""),
            exchange = @Exchange(name="mq_user",type = "fanout")
    ))
    public void receiveA(String message) throws JsonProcessingException {
        log.info("MQ将接收用户信息为: {}",message);
        User user = new ObjectMapper().readValue(message, User.class);
        redisTemplate.opsForHash().put("mq_user",user.getId(),user);
    }
}
