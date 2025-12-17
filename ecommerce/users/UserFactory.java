package users;

public class UserFactory {

    public static User createUser(String role, String u, String p) {
        if (role.equalsIgnoreCase("admin"))
            return new Admin(u, p);
        else
            return new Customer(u, p);
    }
}
