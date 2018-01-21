package database.user;

import database.AuthenticationData;

public interface User {
    AuthenticationData getAuthenticationData();

    int getId();

    String getName();

    String getEmail();

    boolean setPassword(char[] password);

    void setEmail(String  email);
}
