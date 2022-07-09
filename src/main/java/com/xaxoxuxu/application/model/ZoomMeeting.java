package com.xaxoxuxu.application.model;

import lombok.*;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Component
public class ZoomMeeting
{
    private String meetingId;
    private String meetingPassword;
}
