package com.gec.teams.wechat;

import cn.hutool.core.util.IdUtil;
import com.gec.teams.wechat.entity.MessageEntity;
import com.gec.teams.wechat.entity.MessageRefEntity;
import com.gec.teams.wechat.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class TeamsWechatApiApplicationTests {

    @Autowired
    private MessageService messageService;

    @Test
    void contextLoads() {

    }
    @Test
    public void test1(){
        for (int i = 1; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id=messageService.insertMessage(message);

            MessageRefEntity ref=new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(30); //接收人ID
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }

}
