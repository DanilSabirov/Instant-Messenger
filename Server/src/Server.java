import database.AuthenticationData;
import database.dialog.Dialog;
import database.user.User;

public interface Server {
    Dialog getDialog(int idDialog);

    int authenticate(AuthenticationData authenticationData);

    boolean register(User user);

    void start();
}
