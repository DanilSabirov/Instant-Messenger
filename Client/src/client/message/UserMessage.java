package client.message;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UserMessage implements Message {
    private int authorId;

    private String authorName;

    private int dialogId;

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
    public ZonedDateTime getDateReceipt() {
        return dateReceipt;
    }

    @Override
    public String toString() {
        return authorName + "\n" + text + "\n" + DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm").format(dateReceipt);
    }
}
