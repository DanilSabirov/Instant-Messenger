package client.message;

import java.time.ZonedDateTime;

public interface Message {
    int getAuthorId();

    String getText();

    ZonedDateTime getDateReceipt();
}
