package database.user;

import database.AuthenticationData;

public class UserIM implements User {
    private int id;

    private String name;

    private String email;

    public UserIM(String name, String email) {
        this.name = name;
        this.email = email;
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
    public void setEmail(String email) {
        this.email = email;
    }
}
