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
