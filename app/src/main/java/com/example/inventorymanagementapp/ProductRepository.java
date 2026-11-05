package com.example.inventorymanagementapp;

import android.content.Context;

import java.util.List;

/**
 * ProductRepository
 * Acts as the single source of truth for Inventory data.
 * delegates database persistence to DatabaseHelper (SQLite).
 */
public class ProductRepository {

    private static ProductRepository instance;
    private final DatabaseHelper dbHelper;

    private ProductRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        seedSampleDataIfEmpty();
    }

    public static synchronized ProductRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProductRepository(context.getApplicationContext());
        }
        return instance;
    }

    // Overload for backward compatibility when instance is already initialized
    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ProductRepository is not initialized. Call getInstance(context) first.");
        }
        return instance;
    }

    private void seedSampleDataIfEmpty() {
        if (dbHelper.getProductCount() == 0) {
            dbHelper.insertProduct(new Product(
                    "Wireless Ergonomic Mouse",
                    "MSE-101",
                    "Electronics",
                    25,
                    15.00,
                    29.99,
                    10,
                    "Bluetooth & 2.4G wireless dual-mode mouse with ergonomic grip."
            ));

            dbHelper.insertProduct(new Product(
                    "Mechanical Gaming Keyboard",
                    "KBD-202",
                    "Electronics",
                    7, // Low Stock (7 < 10)
                    45.00,
                    79.99,
                    10,
                    "RGB mechanical keyboard with tactile blue switches."
            ));

            dbHelper.insertProduct(new Product(
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
    }

    // CREATE
    public boolean addProduct(Product product) {
        return dbHelper.insertProduct(product);
    }

    // READ ALL
    public List<Product> getAllProducts() {
        return dbHelper.getAllProducts();
    }

    // READ BY ID
    public Product getProductById(String id) {
        return dbHelper.getProductById(id);
    }

    // UPDATE
    public boolean updateProduct(Product updatedProduct) {
        return dbHelper.updateProduct(updatedProduct);
    }

    // STOCK ADJUSTMENT (STOCK IN / STOCK OUT)
    public boolean adjustStock(String id, int delta) {
        Product p = getProductById(id);
        if (p == null) return false;

        int newQty = p.getQuantity() + delta;
        if (newQty < 0) newQty = 0;

        return dbHelper.updateStockQuantity(id, newQty);
    }

    // DELETE
    public boolean deleteProduct(String id) {
        return dbHelper.deleteProduct(id);
    }

    // DASHBOARD STATS
    public int getTotalProductTypesCount() {
        return dbHelper.getProductCount();
    }

    public int getTotalStockCount() {
        int total = 0;
        for (Product p : getAllProducts()) {
            total += p.getQuantity();
        }
        return total;
    }

    public int getLowStockCount() {
        int count = 0;
        for (Product p : getAllProducts()) {
            if (p.isLowStock()) {
                count++;
            }
        }
        return count;
    }

    public int getOutOfStockCount() {
        int count = 0;
        for (Product p : getAllProducts()) {
            if (p.isOutOfStock()) {
                count++;
            }
        }
        return count;
    }
}
