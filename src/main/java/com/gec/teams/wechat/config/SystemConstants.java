package com.gec.teams.wechat.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class SystemConstants {
    public String attendanceStartTime;
    public String attendanceTime;
    public String attendanceEndTime;
    public String closingStartTime;
    public String closingTime;
    public String closingEndTime;
}