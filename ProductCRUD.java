import java.sql.*;
import java.util.Scanner;

public class ProductCRUD {

    // Database connection details
    private static final String URL = "jdbc:sqlite:inventory.db";
    
    // Create the Product table if it doesn't exist
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Product ("
                   + "ProductID INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "ProductName TEXT NOT NULL, "
                   + "Price REAL NOT NULL, "
                   + "Quantity INTEGER NOT NULL);";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created or already exists.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Insert a new product into the database
    public static void createProduct() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);  // Start transaction

            String productName;
            double price;
            int quantity;
            
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter product name: ");
            productName = scanner.nextLine();
            System.out.print("Enter product price: ");
            price = scanner.nextDouble();
            System.out.print("Enter product quantity: ");
            quantity = scanner.nextInt();

            String sql = "INSERT INTO Product (ProductName, Price, Quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, productName);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);
                pstmt.executeUpdate();
                conn.commit();
                System.out.println("Product added successfully!");
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction if error occurs
                System.out.println("Error inserting product: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Display all products from the database
    public static void readProducts() {
        String sql = "SELECT * FROM Product";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nProductID | ProductName | Price | Quantity");
            System.out.println("-------------------------------------------");
            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                String productName = rs.getString("ProductName");
                double price = rs.getDouble("Price");
                int quantity = rs.getInt("Quantity");
                System.out.printf("%d | %s | %.2f | %d\n", productId, productName, price, quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error reading products: " + e.getMessage());
        }
    }

    // Update an existing product's details
    public static void updateProduct() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);  // Start transaction

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter ProductID to update: ");
            int productId = scanner.nextInt();
            scanner.nextLine();  // Consume newline character
            System.out.print("Enter new product name: ");
            String productName = scanner.nextLine();
            System.out.print("Enter new price: ");
            double price = scanner.nextDouble();
            System.out.print("Enter new quantity: ");
            int quantity = scanner.nextInt();

            String sql = "UPDATE Product SET ProductName = ?, Price = ?, Quantity = ? WHERE ProductID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, productName);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);
                pstmt.setInt(4, productId);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    conn.commit();
                    System.out.println("Product updated successfully!");
                } else {
                    conn.rollback();
                    System.out.println("No product found with that ID.");
                }
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction if error occurs
                System.out.println("Error updating product: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Delete a product from the database
    public static void deleteProduct() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);  // Start transaction

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter ProductID to delete: ");
            int productId = scanner.nextInt();

            String sql = "DELETE FROM Product WHERE ProductID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, productId);
                int rowsDeleted = pstmt.executeUpdate();

                if (rowsDeleted > 0) {
                    conn.commit();
                    System.out.println("Product deleted successfully!");
                } else {
                    conn.rollback();
                    System.out.println("No product found with that ID.");
                }
            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction if error occurs
                System.out.println("Error deleting product: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Main method with menu-driven options
    public static void main(String[] args) {
        createTable();  // Ensure table exists

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    createProduct();
                    break;
                case 2:
                    readProducts();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    exit = true;
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}

