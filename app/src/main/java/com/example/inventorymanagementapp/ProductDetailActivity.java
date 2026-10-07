package com.example.inventorymanagementapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID";

    private TextView textDetailName;
    private TextView textDetailCategorySku;
    private MaterialCardView cardStockStatus;
    private TextView textStockStatusBadge;
    private TextView textStockQuantityDetail;
    private TextView textDetailBuyPrice;
    private TextView textDetailSellPrice;
    private TextView textDetailProfitMargin;
    private TextView textDetailDescription;
    private Button btnEditProduct;
    private Button btnDeleteProduct;

    private String productId;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Bind Views
        textDetailName = findViewById(R.id.textDetailName);
        textDetailCategorySku = findViewById(R.id.textDetailCategorySku);
        cardStockStatus = findViewById(R.id.cardStockStatus);
        textStockStatusBadge = findViewById(R.id.textStockStatusBadge);
        textStockQuantityDetail = findViewById(R.id.textStockQuantityDetail);
        textDetailBuyPrice = findViewById(R.id.textDetailBuyPrice);
        textDetailSellPrice = findViewById(R.id.textDetailSellPrice);
        textDetailProfitMargin = findViewById(R.id.textDetailProfitMargin);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        btnEditProduct = findViewById(R.id.btnEditProduct);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);

        btnEditProduct.setOnClickListener(view -> {
            if (currentProduct != null) {
                Intent intent = new Intent(ProductDetailActivity.this, AddProductActivity.class);
                intent.putExtra(AddProductActivity.EXTRA_PRODUCT_ID, currentProduct.getId());
                startActivity(intent);
            }
        });

        btnDeleteProduct.setOnClickListener(view -> showDeleteConfirmationDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductDetails();
    }

    private void loadProductDetails() {
        if (productId == null) {
            finish();
            return;
        }

        currentProduct = ProductRepository.getInstance().getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Product not found or deleted", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textDetailName.setText(currentProduct.getName());
        String catSku = currentProduct.getCategory() + " • SKU: " + currentProduct.getSku();
        textDetailCategorySku.setText(catSku);

        // Stock Status Logic & Formatting
        if (currentProduct.isOutOfStock()) {
            cardStockStatus.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_red_bg));
            textStockStatusBadge.setText(R.string.status_out_of_stock);
            textStockStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_red_text));
        } else if (currentProduct.isLowStock()) {
            cardStockStatus.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_orange_bg));
            textStockStatusBadge.setText(R.string.status_low_stock);
            textStockStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_orange_text));
        } else {
            cardStockStatus.setCardBackgroundColor(ContextCompat.getColor(this, R.color.status_green_bg));
            textStockStatusBadge.setText(R.string.status_in_stock);
            textStockStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.status_green_text));
        }

        String qtyDetail = String.format(Locale.getDefault(), "Current Stock: %d units (Min Stock: %d)",
                currentProduct.getQuantity(), currentProduct.getMinimumStock());
        textStockQuantityDetail.setText(qtyDetail);

        // Pricing & Margin
        textDetailBuyPrice.setText(String.format(Locale.getDefault(), "$%.2f", currentProduct.getBuyPrice()));
        textDetailSellPrice.setText(String.format(Locale.getDefault(), "$%.2f", currentProduct.getSellPrice()));

        double profit = currentProduct.getSellPrice() - currentProduct.getBuyPrice();
        double marginPercent = (currentProduct.getBuyPrice() > 0) ? (profit / currentProduct.getBuyPrice()) * 100 : 0;
        String profitText = String.format(Locale.getDefault(), "+$%.2f (%.1f%%)", profit, marginPercent);
        textDetailProfitMargin.setText(profitText);

        // Description
        if (currentProduct.getDescription() != null && !currentProduct.getDescription().isEmpty()) {
            textDetailDescription.setText(currentProduct.getDescription());
        } else {
            textDetailDescription.setText("No description available.");
        }
    }

    private void showDeleteConfirmationDialog() {
        if (currentProduct == null) return;

        String message = getString(R.string.delete_confirm_msg, currentProduct.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    ProductRepository.getInstance().deleteProduct(currentProduct.getId());
                    Toast.makeText(ProductDetailActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
