package com.example.inventorymanagementapp;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductRepository (In-Memory Singleton)
 * Holds inventory products in memory before database persistence (Stage 4).
 * Provides CRUD operations (Create, Read, Update, Delete).
 */
public class ProductRepository {

    private static ProductRepository instance;
    private final List<Product> productList;

    private ProductRepository() {
        productList = new ArrayList<>();
        seedSampleData();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    // Seed initial inventory items for practice and immediate UI feedback
    private void seedSampleData() {
        productList.add(new Product(
                "Wireless Ergonomic Mouse",
                "MSE-101",
                "Electronics",
                25,
                15.00,
                29.99,
                10,
                "Bluetooth & 2.4G wireless dual-mode mouse with ergonomic grip."
        ));

        productList.add(new Product(
                "Mechanical Gaming Keyboard",
                "KBD-202",
                "Electronics",
                7, // Low Stock (7 < 10)
                45.00,
                79.99,
                10,
                "RGB mechanical keyboard with tactile blue switches."
        ));

        productList.add(new Product(
                "USB-C Multiport Adapter",
                "HUB-303",
                "Accessories",
                0, // Out of Stock (0)
                12.50,
                24.99,
                5,
                "7-in-1 USB-C hub with 4K HDMI, USB 3.0, and Power Delivery."
        ));
    }

    // CREATE
    public void addProduct(Product product) {
        productList.add(0, product); // Add to beginning of list
    }

    // READ ALL
    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }

    // READ BY ID
    public Product getProductById(String id) {
        for (Product p : productList) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    // UPDATE
    public boolean updateProduct(Product updatedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(updatedProduct.getId())) {
                productList.set(i, updatedProduct);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean deleteProduct(String id) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(id)) {
                productList.remove(i);
                return true;
            }
        }
        return false;
    }

    // STATISTICS FOR DASHBOARD
    public int getTotalProductTypesCount() {
        return productList.size();
    }

    public int getTotalStockCount() {
        int total = 0;
        for (Product p : productList) {
            total += p.getQuantity();
        }
        return total;
    }

    public int getLowStockCount() {
        int count = 0;
        for (Product p : productList) {
            if (p.isLowStock()) {
                count++;
            }
        }
        return count;
    }

    public int getOutOfStockCount() {
        int count = 0;
        for (Product p : productList) {
            if (p.isOutOfStock()) {
                count++;
            }
        }
        return count;
    }
}
