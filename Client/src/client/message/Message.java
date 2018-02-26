package client.message;

import java.time.ZonedDateTime;

public interface Message {
    int getAuthorId();

    int getDialogId();

    String getText();

    ZonedDateTime getDateReceipt();
}
