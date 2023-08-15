package com.zbb.demo.config;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@AllArgsConstructor
public class RabbitMqConfig {

    private final ConnectionFactory connectionFactory;

//    private final RabbitLogsMapper rabbitLogsMapper;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //confirm确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            String msgId = correlationData.getId();
            if (ack) {
                //发送成功
                log.info("消息成功发送 , msgId: {},", 1);
                //状态更新  消息发送成功
//                BiddingRabbitLogs biddingRabbitLogs = new BiddingRabbitLogs();
//                biddingRabbitLogs.setStatus(SendStatus.SEND_SUCCESS.getValue());
//                rabbitLogsMapper.update(biddingRabbitLogs, Wrappers.lambdaUpdate(BiddingRabbitLogs.class).eq(BiddingRabbitLogs::getId,msgId).notIn(BiddingRabbitLogs::getStatus,"4"));
            } else {
                //发送失败
                log.error("消息发送失败, {}, cause: {}, msgId: {}", correlationData, cause, 1);
//                //状态更新  消息发送失败
//                BiddingRabbitLogs biddingRabbitLogs = new BiddingRabbitLogs();
//                biddingRabbitLogs.setStatus(SendStatus.SEND_FAILD.getValue());
//                rabbitLogsMapper.update(biddingRabbitLogs, Wrappers.lambdaUpdate(BiddingRabbitLogs.class).eq(BiddingRabbitLogs::getId,msgId).notIn(BiddingRabbitLogs::getStatus,"4"));
            }
        });
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            //触发回调  只有交换机找不到队列时才会触发
            log.error("消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
            //状态更新 消息发送失败
            String msgId = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
//            BiddingRabbitLogs biddingRabbitLogs = new BiddingRabbitLogs();
//            biddingRabbitLogs.setStatus(SendStatus.SEND_FAILD.getValue());
//            rabbitLogsMapper.update(biddingRabbitLogs, Wrappers.lambdaUpdate(BiddingRabbitLogs.class).eq(BiddingRabbitLogs::getId,msgId).notIn(BiddingRabbitLogs::getStatus,"4"));
        });
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;

    }

}
