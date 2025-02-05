import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private User sender;
    private User recipient;
    private String content;
    private Date timestamp;

    public Message(User sender, User recipient, String content)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = new Date();
    }
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return"From: "+sender + "\nTo: "+ recipient + "\nSent: "+ timestamp +"\nMessage: "+ content;
    }

}
