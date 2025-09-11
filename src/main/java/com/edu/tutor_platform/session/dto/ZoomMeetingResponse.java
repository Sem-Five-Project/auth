package com.edu.tutor_platform.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoomMeetingResponse {
    private String id;
    private String topic;
    private String join_url;
    private String start_url;
    private String password;
    private String start_time;
    private int duration;
    private ZoomSettings settings;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZoomSettings {
        private boolean host_video;
        private boolean participant_video;
        private boolean waiting_room;
        private boolean mute_upon_entry;
    }
}
