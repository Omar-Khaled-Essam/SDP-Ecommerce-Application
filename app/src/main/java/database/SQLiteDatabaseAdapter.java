package database;

import orders.Order;
import products.Clothing;
import products.Electronics;
import products.Product;
import users.Admin;
import users.Customer;
import users.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class SQLiteDatabaseAdapter implements DatabaseAdapter {

    private final String url;

    public SQLiteDatabaseAdapter(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
    }

    @Override
    public void init() {
        try {
            Path parent = Path.of(url.replace("jdbc:sqlite:", "")).toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (Exception ignored) {}

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                      username TEXT PRIMARY KEY,
                      password TEXT NOT NULL,
                      role TEXT NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                      name TEXT PRIMARY KEY,
                      category TEXT,
                      price REAL,
                      stock INTEGER,
                      description TEXT
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      customer TEXT,
                      total REAL,
                      status TEXT,
                      created_at TEXT
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS order_items (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      order_id INTEGER,
                      product_name TEXT,
                      category TEXT,
                      price REAL,
                      qty INTEGER,
                      FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE
                    )
                    """);
            // Attempt to add created_at if missing (best effort)
            try {
                stmt.execute("ALTER TABLE orders ADD COLUMN created_at TEXT");
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init database", e);
        }
    }

    @Override
    public void seedDefaults() {
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        if (userExists("admin")) return;
        registerUser(new Admin("admin", "admin"));
        registerUser(new Customer("cus", "cus"));
    }

    private boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private void seedProducts() {
        String countSql = "SELECT COUNT(*) FROM products";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) return;
        } catch (SQLException e) {
            // proceed to attempt inserts
        }
        addProduct(new Electronics("Laptop", "Electronics", 15000, 10, "High performance laptop"));
        addProduct(new Electronics("Phone", "Electronics", 8000, 15, "Smartphone"));
        addProduct(new Clothing("T-Shirt", "Clothing", 500, 20, "Cotton T-Shirt"));
        addProduct(new Clothing("Jacket", "Clothing", 1200, 8, "Winter jacket"));
    }

    @Override
    public void registerUser(User user) {
        String sql = "INSERT OR IGNORE INTO users(username, password, role) VALUES(?,?,?)";
        String role = (user instanceof Admin) ? "Admin" : "Customer";
        String hashed = PasswordUtil.hash(user.getPassword());
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, hashed);
            ps.setString(3, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }

    public void updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        String hashed = PasswordUtil.hash(newPassword);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }

    public void deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT username, role FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                if ("Admin".equalsIgnoreCase(role)) {
                    list.add(new Admin(username, ""));
                } else {
                    list.add(new Customer(username, ""));
                }
            }
        } catch (SQLException e) {
            // ignore
        }
        return list;
    }

    @Override
    public User authenticate(String username, String password) {
        String sql = "SELECT password, role FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String stored = rs.getString("password");
                if (!PasswordUtil.verify(password, stored)) return null;
                String role = rs.getString("role");
                if ("Admin".equalsIgnoreCase(role)) {
                    return new Admin(username, stored);
                }
                return new Customer(username, stored);
            }
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Customer> getCustomers() {
        Map<String, Customer> customers = new HashMap<>();
        String sql = "SELECT username FROM users WHERE role = 'Customer'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String username = rs.getString("username");
                customers.put(username, new Customer(username, ""));
            }
        } catch (SQLException e) {
            // ignore
        }

        // attach orders for each customer
        List<Order> orders = getOrders();
        for (Order o : orders) {
            Customer c = customers.get(o.getCustomer().getUsername());
            if (c != null) {
                c.addOrder(o);
            }
        }
        return new ArrayList<>(customers.values());
    }

    @Override
    public List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT name, category, price, stock, description FROM products";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String category = rs.getString("category");
                Product p;
                if ("Electronics".equalsIgnoreCase(category)) {
                    p = new Electronics(
                            rs.getString("name"),
                            category,
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description")
                    );
                } else {
                    p = new Clothing(
                            rs.getString("name"),
                            category,
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getString("description")
                    );
                }
                list.add(p);
            }
        } catch (SQLException e) {
            // ignore
        }
        return list;
    }

    @Override
    public void addProduct(Product product) {
        String sql = "INSERT OR IGNORE INTO products(name, category, price, stock, description) VALUES(?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product", e);
        }
    }

    public void updateProduct(Product product) {
        String sql = "UPDATE products SET category = ?, price = ?, stock = ?, description = ? WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getCategory());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getDescription());
            ps.setString(5, product.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    public void deleteProduct(Product product) {
        String sql = "DELETE FROM products WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    @Override
    public void addOrder(Order order) {
        String insertOrder = "INSERT INTO orders(customer, total, status, created_at) VALUES(?,?,?,?)";
        String insertItem = "INSERT INTO order_items(order_id, product_name, category, price, qty) VALUES(?,?,?,?,?)";
        String updateStock = "UPDATE products SET stock = CASE WHEN stock - ? < 0 THEN 0 ELSE stock - ? END WHERE name = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psOrder = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getCustomer().getUsername());
                psOrder.setDouble(2, order.getTotal());
                psOrder.setString(3, order.getStatus());
                psOrder.setString(4, java.time.LocalDateTime.now().toString());
                psOrder.executeUpdate();

                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setId(keys.getInt(1));
                    }
                }
            }

            Map<String, ItemAggregate> counts = aggregateItems(order.getProducts());
            try (PreparedStatement psItem = conn.prepareStatement(insertItem);
                 PreparedStatement psStock = conn.prepareStatement(updateStock)) {
                for (ItemAggregate agg : counts.values()) {
                    psItem.setInt(1, order.getId());
                    psItem.setString(2, agg.name);
                    psItem.setString(3, agg.category);
                    psItem.setDouble(4, agg.price);
                    psItem.setInt(5, agg.qty);
                    psItem.addBatch();

                    psStock.setInt(1, agg.qty);
                    psStock.setInt(2, agg.qty);
                    psStock.setString(3, agg.name);
                    psStock.addBatch();
                }
                psItem.executeBatch();
                psStock.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add order", e);
        }
    }

    private Map<String, ItemAggregate> aggregateItems(List<Product> products) {
        Map<String, ItemAggregate> map = new LinkedHashMap<>();
        for (Product p : products) {
            ItemAggregate agg = map.getOrDefault(p.getName(), new ItemAggregate(p.getName(), p.getCategory(), p.getPrice(), 0));
            agg.qty += 1;
            map.put(p.getName(), agg);
        }
        return map;
    }

    @Override
    public List<Order> getOrders() {
        return fetchOrders("SELECT id, customer, total, status, created_at FROM orders", ps -> {});
    }

    @Override
    public List<Order> getOrdersByCustomer(String username) {
        return fetchOrders("SELECT id, customer, total, status, created_at FROM orders WHERE customer = ?",
                ps -> ps.setString(1, username));
    }

    @Override
    public List<Order> getOrdersBetween(LocalDate start, LocalDate end) {
        return fetchOrders("SELECT id, customer, total, status, created_at FROM orders WHERE date(created_at) BETWEEN date(?) AND date(?)",
                ps -> {
                    ps.setString(1, start.toString());
                    ps.setString(2, end.toString());
                });
    }

    @Override
    public void updateOrderStatus(Order order, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, order.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    @Override
    public double getTotalSales() {
        String sql = "SELECT SUM(total) as total FROM orders";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            // ignore
        }
        return 0;
    }

    @Override
    public double getTotalSalesBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT SUM(total) as total FROM orders WHERE date(created_at) BETWEEN date(?) AND date(?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, start.toString());
            ps.setString(2, end.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) {
            // ignore
        }
        return 0;
    }

    @Override
    public double getTotalSpentForCustomer(String username) {
        String sql = "SELECT SUM(total) as total FROM orders WHERE customer = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) {
            // ignore
        }
        return 0;
    }

    @Override
    public List<Product> getLowStock(int threshold) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT name, category, price, stock, description FROM products WHERE stock <= ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("category");
                    Product p;
                    if ("Electronics".equalsIgnoreCase(category)) {
                        p = new Electronics(
                                rs.getString("name"),
                                category,
                                rs.getDouble("price"),
                                rs.getInt("stock"),
                                rs.getString("description")
                        );
                    } else {
                        p = new Clothing(
                                rs.getString("name"),
                                category,
                                rs.getDouble("price"),
                                rs.getInt("stock"),
                                rs.getString("description")
                        );
                    }
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            // ignore
        }
        return list;
    }

    private List<Order> fetchOrders(String sqlOrders, SqlBinder binder) {
        List<Order> list = new ArrayList<>();
        String sqlItems = "SELECT product_name, category, price, qty FROM order_items WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlOrders)) {
            binder.bind(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("customer");
                    double total = rs.getDouble("total");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("created_at");

                    List<Product> items = new ArrayList<>();
                    try (PreparedStatement ps = conn.prepareStatement(sqlItems)) {
                        ps.setInt(1, id);
                        try (ResultSet irs = ps.executeQuery()) {
                            while (irs.next()) {
                                String category = irs.getString("category");
                                String name = irs.getString("product_name");
                                double price = irs.getDouble("price");
                                int qty = irs.getInt("qty");
                                for (int i = 0; i < qty; i++) {
                                    Product p;
                                    if ("Electronics".equalsIgnoreCase(category)) {
                                        p = new Electronics(name, category, price, 0, "");
                                    } else {
                                        p = new Clothing(name, category, price, 0, "");
                                    }
                                    items.add(p);
                                }
                            }
                        }
                    }

                    Customer customer = new Customer(username, "");
                    Order order = new Order(customer, items, total);
                    order.setId(id);
                    order.setStatus(status);
                    order.setCreatedAt(createdAt);
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            // ignore
        }
        return list;
    }

    @FunctionalInterface
    interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private static class ItemAggregate {
        String name;
        String category;
        double price;
        int qty;

        ItemAggregate(String name, String category, double price, int qty) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.qty = qty;
        }
    }
}
