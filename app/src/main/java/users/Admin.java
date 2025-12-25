package users;

// SOLID: SRP✅ (role marker/type only).
public class Admin extends User {
    public Admin(String u, String p) {
        super(u, p);
    }
}
