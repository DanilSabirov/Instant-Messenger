package database;

import java.util.Arrays;

public class AuthenticationData {
    String login;

    char[] password;

    public AuthenticationData(String login, char[] password) {
        this.login = login;
        this.password = password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationData that = (AuthenticationData) o;

        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        return Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(password);
        return result;
    }
}
