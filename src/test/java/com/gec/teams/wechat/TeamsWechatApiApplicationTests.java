package com.gec.teams.wechat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.gec.teams.wechat.entity.TbMeeting;
import com.gec.teams.wechat.service.TbMeetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author xin
 * @since 2023/7/25 15:31
 */
@SpringBootTest
public class TeamsWechatApiApplicationTests {

    @Autowired
    private TbMeetingService tbMeetingService;

    @Test
    void Test() {
        for (int i = 1; i <= 100; i++) {
            TbMeeting meeting = new TbMeeting();
            meeting.setId((long) i);
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setTitle("测试会议" + i);
            meeting.setCreatorId(28L); //ROOT用户ID
            meeting.setDate(DateUtil.today());
            meeting.setPlace("线上会议室");
            meeting.setStart("08:30");
            meeting.setEnd("10:30");
            meeting.setType(1);
            meeting.setMembers("[28,15]");
            meeting.setDesc("会议研讨Teams项目性能测试");
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setStatus(3);
            meeting.setRoomId(new Date().getTime());
            tbMeetingService.insertMeeting(meeting);

        }
    }
}
