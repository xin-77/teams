package com.gec.teams.wechat.task;


import com.gec.teams.wechat.entity.MessageEntity;
import com.gec.teams.wechat.entity.MessageRefEntity;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.service.MessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MessageTask {
    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private MessageService messageService;

    /**
     * 消息发送
     * @param topic
     * @param entity
     */
    public void send(String topic, MessageEntity entity) {
        String id = messageService.insertMessage(entity);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
        ) {
            channel.queueDeclare(topic, true, false, false, null);
            HashMap map = new HashMap();
            map.put("messageId", id);
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(map).build();
            channel.basicPublish("", topic, properties, entity.getMsg().getBytes());
            log.debug("消息发送成功！");
        } catch (Exception e) {
            log.error("执行异常", e);
            throw new TeamsException("向MQ发送消息失败");
        }

    }

    /**
     * 异步消息发送
     * @param topic
     * @param entity
     */
    @Async
    public void sendAsync(String topic, MessageEntity entity) {
        send(topic, entity);
    }

    /**
     * 消息接收
     * @param topic
     * @return
     */
    public Integer receive(String topic) {
        int i = 0;
        // 获取链接
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
        ) {

            channel.queueDeclare(topic, true, false, false, null);
            while (true) {
                GetResponse response = channel.basicGet(topic, false);
                if (response != null) {
                    AMQP.BasicProperties properties = response.getProps();
                    Map<String, Object> map = properties.getHeaders();
                    String messageId = map.get("messageId").toString();
                    byte[] body = response.getBody();
                    String message = new String(body);
                    log.debug("从RabbitMQ接收的消息：" + message);
                    MessageRefEntity entity = new MessageRefEntity();
                    entity.setMessageId(messageId);
                    entity.setReceiverId(Integer.parseInt(topic));
                    entity.setReadFlag(false);
                    entity.setLastFlag(true);
                    messageService.insertRef(entity);
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                    i++;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("执行异常", e);
            throw new TeamsException("接收消息失败");
        }
        return i;
    }

    /**
     * 异步消息接收
     * @param topic
     * @return
     */
    @Async
    public Integer receiveAsync(String topic) {
        return receive(topic);
    }

    /**
     * 删除消息队列
     * @param topic
     */
    public void deleteQueue(String topic) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
        ) {
            channel.queueDelete(topic);
            log.debug("消息队列成功删除");
        } catch (Exception e) {
            log.error("删除队列失败", e);
            throw new TeamsException("删除队列失败");
        }
    }

    /**
     * 异步删除消息队列
     * @param topic
     */
    @Async
    public void deleteQueueAsync(String topic) {
        deleteQueue(topic);
    }
}
