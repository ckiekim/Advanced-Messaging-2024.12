package com.lion.ws.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crid", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = this.joinedAt == null ? LocalDateTime.now() : this.joinedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ChatUser chatUser = (ChatUser) obj;
        if (!chatRoom.equals(chatUser.chatRoom))
            return false;
        return user.equals(chatUser.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoom.hashCode(), user.hashCode());
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "id=" + id +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
