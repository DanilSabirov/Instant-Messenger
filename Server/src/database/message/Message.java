package database.message;

import java.time.ZonedDateTime;

public interface Message {
    int getAutorId();

    String getText();

    ZonedDateTime getDateReceipt();
}
