package client.user;

import java.util.List;
import java.util.Set;

public interface User {
    int getId();

    String getName();

    String getEmail();

    void setEmail(String  email);

    void setId(int id);

    List<Integer> getDialogs();
}
