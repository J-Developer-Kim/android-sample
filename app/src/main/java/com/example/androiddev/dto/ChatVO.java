package com.example.androiddev.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChatVO {
    private String content;
    private boolean is_my;

    public ChatVO(String content, boolean is_my) {
        this.content = content;
        this.is_my   = is_my;
    }
}
