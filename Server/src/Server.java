import database.AuthenticationData;
import database.dialog.Dialog;
import database.user.User;

import java.util.List;
import java.util.Set;

public interface Server {
    Dialog getDialog(int idDialog);

    User authenticate(AuthenticationData authenticationData);

    boolean register(User user, AuthenticationData authenticationData);

    List<User> searchUsers(String namePrefix);

    void start();

    int createDialog(Set<Integer> usersId);
}
