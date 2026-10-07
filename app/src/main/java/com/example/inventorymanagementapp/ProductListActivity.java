package com.example.inventorymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class ProductListActivity extends AppCompatActivity {

    private LinearLayout containerProductList;
    private TextView textEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        containerProductList = findViewById(R.id.containerProductList);
        textEmptyList = findViewById(R.id.textEmptyList);
        Button btnHeaderAddProduct = findViewById(R.id.btnHeaderAddProduct);

        btnHeaderAddProduct.setOnClickListener(view -> {
            Intent intent = new Intent(ProductListActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderProductList();
    }

    private void renderProductList() {
        // Clear previous views from container
        containerProductList.removeAllViews();

        List<Product> products = ProductRepository.getInstance().getAllProducts();

        if (products.isEmpty()) {
            textEmptyList.setVisibility(View.VISIBLE);
            return;
        }

        textEmptyList.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Product p : products) {
            // Dynamically inflate item layout XML for each product
            View cardView = inflater.inflate(R.layout.item_product_card, containerProductList, false);

            TextView textName = cardView.findViewById(R.id.textItemName);
            TextView textCatSku = cardView.findViewById(R.id.textItemCategorySku);
            TextView textStockBadge = cardView.findViewById(R.id.textItemStockBadge);
            TextView textPrice = cardView.findViewById(R.id.textItemPrice);

            textName.setText(p.getName());
            String catSku = p.getCategory() + " • SKU: " + p.getSku();
            textCatSku.setText(catSku);

            textPrice.setText(String.format(Locale.getDefault(), "$%.2f", p.getSellPrice()));

            // Set stock badge color based on stock status
            if (p.isOutOfStock()) {
                textStockBadge.setText("Out of Stock (0)");
                textStockBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_red_bg));
                textStockBadge.setTextColor(ContextCompat.getColor(this, R.color.status_red_text));
            } else if (p.isLowStock()) {
                textStockBadge.setText(String.format(Locale.getDefault(), "Low Stock (%d)", p.getQuantity()));
                textStockBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_orange_bg));
                textStockBadge.setTextColor(ContextCompat.getColor(this, R.color.status_orange_text));
            } else {
                textStockBadge.setText(String.format(Locale.getDefault(), "Qty: %d in stock", p.getQuantity()));
                textStockBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_green_bg));
                textStockBadge.setTextColor(ContextCompat.getColor(this, R.color.status_green_text));
            }

            // Click listener on item card to view details
            cardView.setOnClickListener(view -> {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, p.getId());
                startActivity(intent);
            });

            // Add inflated card to parent container
            containerProductList.addView(cardView);
        }
    }
}
