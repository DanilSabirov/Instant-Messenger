package database.message;


import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class UserMessage implements Message {
    private int autorId;

    private String text;

    private ZonedDateTime dateReceipt;

    public UserMessage(int autorId, String text, ZonedDateTime dateReceipt) {
        this.autorId = autorId;
        this.text = text;
        this.dateReceipt = dateReceipt;
    }

    @Override
    public int getAutorId() {
        return autorId;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public ZonedDateTime getDateReceipt() {
        return dateReceipt;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "autorId=" + autorId +
                ", text='" + text + '\'' +
                ", dateReceipt=" + dateReceipt +
                '}';
    }
}
