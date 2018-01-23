package client.message;

import java.time.ZonedDateTime;

public class UserMessage implements Message {
    private int autorId;

    private String text;

    private ZonedDateTime dateReceipt;

    public UserMessage(int autorId, String text) {
        this.autorId = autorId;
        this.text = text;
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
}
