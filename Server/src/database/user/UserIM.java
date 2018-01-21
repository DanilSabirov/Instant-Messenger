package database.user;

import database.AuthenticationData;

public class UserIM implements User {
    private AuthenticationData authenticationData;

    private String name;

    private String email;

    private int id;

    public UserIM(AuthenticationData authenticationData, String name, String email) {
        this.authenticationData = authenticationData;
        this.name = name;
        this.email = email;
    }

    @Override
    public AuthenticationData getAuthenticationData() {
        return authenticationData;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public boolean setPassword(char[] password) {
        authenticationData.setPassword(password);
        return true;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}
