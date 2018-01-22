package database.user;

import database.AuthenticationData;

public interface User {
    int getId();

    String getName();

    String getEmail();

    void setEmail(String  email);
}
