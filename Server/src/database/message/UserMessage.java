package database.message;

import java.time.ZonedDateTime;

public class UserMessage implements Message {
    private int autorId;

    private String text;

    private ZonedDateTime dateReceipt;

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
