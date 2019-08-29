package com.woowacourse.dsgram.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom extends DateTimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, length = 50, nullable = false)
    private long code;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatUser> chatUsers;

    public ChatRoom(long code) {
        this.code = code;
    }
}