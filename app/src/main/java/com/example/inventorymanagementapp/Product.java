package com.example.inventorymanagementapp;

import java.io.Serializable;
import java.util.UUID;

/**
 * Product Model Class
 * Represents a single product in the inventory.
 * Implements Serializable so it can be passed via Intents if needed.
 */
public class Product implements Serializable {

    private String id;
    private String name;
    private String sku;
    private String category;
    private int quantity;
    private double buyPrice;
    private double sellPrice;
    private int minimumStock;
    private String description;
    private long createdAt;

    // Constructor for creating a new Product (generates random UUID and sets timestamp)
    public Product(String name, String sku, String category, int quantity, double buyPrice, double sellPrice, int minimumStock, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.minimumStock = minimumStock;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    // Full Constructor (for updating or loading from database)
    public Product(String id, String name, String sku, String category, int quantity, double buyPrice, double sellPrice, int minimumStock, String description, long createdAt) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.minimumStock = minimumStock;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Stock status logic
    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public boolean isLowStock() {
        return quantity > 0 && quantity <= minimumStock;
    }
}
