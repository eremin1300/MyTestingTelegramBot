package vik.test.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Integer id;

    private String username;

    private Long chat_id;

    private Date last_message_at;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getChatId() {
        return chat_id;
    }

    public void setChatId(Long chatId) {
        this.chat_id = chatId;
    }

    public Date getLastMessageAt() {
        return last_message_at;
    }

    public void setLastMessageAt(Date lastMessageAt) {
        this.last_message_at = lastMessageAt;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User(Integer id, String username, Long chat_id, Date last_message_at) {
        this.id = id;
        this.username = username;
        this.chat_id = chat_id;
        this.last_message_at = last_message_at;
    }


}
