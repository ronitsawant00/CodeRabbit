import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ProductManager manager = new ProductManager();
        Scanner scanner = new Scanner(System.in);

        // Intentionally manual additions
        manager.addProduct(new Product(1, "Laptop", 1200.00));
        manager.addProduct(new Product(2, "Mouse", 25.50));

        System.out.println("Welcome to Product Management System!");

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. View all products");
            System.out.println("2. Add a new product");
            System.out.println("3. Find a product by ID");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    List<Product> allProducts = manager.getAllProducts();
                    if (allProducts.isEmpty()) {
                        System.out.println("No products available.");
                    } else {
                        System.out.println("\n--- All Products ---");
                        for (Product p : allProducts) {
                            System.out.println(p);
                        }
                    }
                    break;
                case "2":
                    System.out.print("Enter product ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter product name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter product price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    manager.addProduct(new Product(id, name, price));
                    System.out.println("Product added successfully!");
                    break;
                case "3":
                    System.out.print("Enter product ID to find: ");
                    int findId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    Product foundProduct = manager.getProductById(findId);
                    if (foundProduct != null) {
                        System.out.println("Found product: " + foundProduct);
                    } else {
                        System.out.println("Product not found with ID: " + findId);
                    }
                    break;
                case "4":
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}