package database;

// SOLID: SRP✅ (session state only), DIP⚠️ (Singleton/global access can hide coupling).
import users.User;

public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    // Private constructor (Singleton)
    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Set logged-in user
    public void login(User user) {
        this.currentUser = user;
    }

    // Clear session
    public void logout() {
        this.currentUser = null;
    }

    // Get logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Check login status
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}

