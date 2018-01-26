import database.AuthenticationData;
import database.dialog.Dialog;
import database.user.User;

public interface Server {
    Dialog getDialog(int idDialog);

    User authenticate(AuthenticationData authenticationData);

    boolean register(User user, AuthenticationData authenticationData);

    void start();
}
