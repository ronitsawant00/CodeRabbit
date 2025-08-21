import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public Product getProductById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null; // A good place for Sourcery to suggest using Optional
    }

    public void updateProduct(int id, String newName, double newPrice) {
        for (Product product : products) {
            if (product.getId() == id) {
                // This part could be cleaner with a new constructor or a method
                // Code Rabbit can suggest this as a potential improvement
                product = new Product(id, newName, newPrice);
                return;
            }
        }
    }

    public void deleteProduct(int id) {
        Product productToRemove = null;
        for (Product product : products) {
            if (product.getId() == id) {
                productToRemove = product;
                break;
            }
        }
        if (productToRemove != null) {
            products.remove(productToRemove);
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
}