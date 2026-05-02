package com.example.graduation_project.dto;

import lombok.Data;

@Data
public class WebSocketMessage {

    private String type;

    private Object data;

}
