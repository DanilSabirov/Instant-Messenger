package database.message;


import java.time.ZonedDateTime;

public class UserMessage implements Message {
    private int authorId;

    private int dialogId;

    private String authorName;

    private String text;

    private ZonedDateTime dateReceipt;

    public UserMessage(int autorId, String authorName, int dialogId, String text, ZonedDateTime dateReceipt) {
        this.authorId = autorId;
        this.authorName = authorName;
        this.text = text;
        this.dateReceipt = dateReceipt;
        this.dialogId = dialogId;
    }

    @Override
    public int getAuthorId() {
        return authorId;
    }

    @Override
    public int getDialogId() {
        return dialogId;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public ZonedDateTime getDateReceipt() {
        return dateReceipt;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "authorId=" + authorId +
                ", authorName " + authorName +
                ", dialogId=" + dialogId +
                ", text='" + text + '\'' +
                '}';
    }
}
