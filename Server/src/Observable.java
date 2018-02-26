import database.dialog.Dialog;
import database.message.Message;
import database.user.User;

public interface Observable {
    void notifyOfMessage(Message message);

    void notifyOfDialog(Dialog dialog);

    User getUser();
}
