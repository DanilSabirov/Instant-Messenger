package database.message;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public interface Message {
    int getAuthorId();

    String getText();

    ZonedDateTime getDateReceipt();
}
