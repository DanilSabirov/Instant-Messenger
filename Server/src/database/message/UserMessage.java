package database.message;


import java.time.ZonedDateTime;

public class UserMessage implements Message {
    private int authorId;

    private String text;

    private ZonedDateTime dateReceipt;

    public UserMessage(int autorId, String text, ZonedDateTime dateReceipt) {
        this.authorId = autorId;
        this.text = text;
        this.dateReceipt = dateReceipt;
    }

    @Override
    public int getAuthorId() {
        return authorId;
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
                "authorId=" + authorId +
                ", text='" + text + '\'' +
                ", dateReceipt=" + dateReceipt +
                '}';
    }
}
