package com.lion.ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "KisToken")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KisOAuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 512)
    private String token;

    private LocalDateTime timestamp;
}
