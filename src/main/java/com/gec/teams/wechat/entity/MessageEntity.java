package com.gec.teams.wechat.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息发送实体
 */
@Data
@Document(collection = "message")
public class MessageEntity implements Serializable {
    @Id
    private String _id;

    @Indexed(unique = true)
    private String uuid;
    @Indexed
    private Integer senderId;
    private String senderPhoto = "";
    private String senderName;
    private String msg;
    @Indexed
    private Date sendTime;
}
