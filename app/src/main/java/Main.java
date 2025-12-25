// SOLID: SRP✅ (single entry point), DIP⚠️ (directly launches a concrete GUI class).
public class Main {

    public static void main(String[] args) {
        // JavaFX entry point
        gui.LoginUI.main(args);
    }
}
