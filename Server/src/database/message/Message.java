package database.message;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public interface Message {
    int getAuthorId();

    int getDialogId();

    String getText();

    String getAuthorName();

    ZonedDateTime getDateReceipt();
}
