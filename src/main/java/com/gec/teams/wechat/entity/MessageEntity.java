package com.gec.teams.wechat.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "message")
public class MessageEntity implements Serializable {
    @Id
    private String _id;

    @Indexed(unique = true)
    private String uuid;
    @Indexed
    private Integer senderId;
    private String senderPhoto = "https://cssl.duitang.com/uploads/item/201810/18/20181018163608_kwrle.jpg";
    private String senderName;
    private String msg;
    @Indexed
    private Date sendTime;
}