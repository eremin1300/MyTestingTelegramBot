package vik.test.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Messages extends AbstractEntity  {

    @ManyToOne(fetch=FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name="userid")
    private User user;

    private String message;

    private String answer;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Messages() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Messages(User user, String message, String answer, Date date) {
        this.user = user;
        this.message = message;
        this.answer = answer;
        this.date = date;
    }

}
