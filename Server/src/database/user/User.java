package database.user;

public interface User {
    int getId();

    String getName();

    String getEmail();

    void setEmail(String  email);

    void setId(int id);
}
