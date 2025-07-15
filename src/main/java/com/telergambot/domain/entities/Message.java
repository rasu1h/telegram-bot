package com.telergambot.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime sentAt;
    private boolean deliveredToTelegram;
    private LocalDateTime deliveredAt;
}