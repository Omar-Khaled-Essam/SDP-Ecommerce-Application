package users;

// Pattern: Factory (Simple Factory) - centralizes creation of Admin/Customer users.
public class UserFactory {

    public static User createUser(String role, String username, String password) {
        if (role.equalsIgnoreCase("Admin")) {
            return new Admin(username, password);
        }
        return new Customer(username, password);
    }
}
