package ru.job4j.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "mb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "client_id")
    private long clientId;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "daily_advice_on")
    private Boolean dailyAdviceOn;

    public User() {
    }

    public User(Long id, long clientId, long chatId) {
        this.id = id;
        this.clientId = clientId;
        this.chatId = chatId;
    }

    public User(Long id, long clientId, long chatId, boolean dailyAdviceOn) {
        this.id = id;
        this.clientId = clientId;
        this.chatId = chatId;
        this.dailyAdviceOn = dailyAdviceOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public boolean isDailyAdviceOn() {
        return dailyAdviceOn;
    }

    public void setDailyAdviceOn(boolean dailyAdviceOn) {
        this.dailyAdviceOn = dailyAdviceOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}