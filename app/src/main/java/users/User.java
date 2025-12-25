package users;

// SOLID: SRP✅ (user identity data only).
public abstract class User {
    protected String username;
    protected String password;

    public User(String u, String p) {
        this.username = u;
        this.password = p;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
