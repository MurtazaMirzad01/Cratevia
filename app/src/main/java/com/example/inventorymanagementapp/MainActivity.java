package com.example.inventorymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textTotalProductsVal;
    private TextView textTotalStockVal;
    private TextView textLowStockVal;
    private TextView textOutOfStockVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Repository with Context on startup
        ProductRepository.getInstance(this);

        // Bind Dashboard Metric Views
        textTotalProductsVal = findViewById(R.id.textTotalProductsVal);
        textTotalStockVal = findViewById(R.id.textTotalStockVal);
        textLowStockVal = findViewById(R.id.textLowStockVal);
        textOutOfStockVal = findViewById(R.id.textOutOfStockVal);

        // Bind Action Views
        Button btnViewCatalog = findViewById(R.id.btnViewCatalog);
        Button btnOpenAddProduct = findViewById(R.id.btnOpenAddProduct);

        // View Catalog Button Listener
        btnViewCatalog.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Add Product Listener
        btnOpenAddProduct.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardMetrics();
    }

    // Refresh Dashboard metrics in real time from SQLite DB
    private void updateDashboardMetrics() {
        ProductRepository repo = ProductRepository.getInstance(this);

        textTotalProductsVal.setText(String.valueOf(repo.getTotalProductTypesCount()));
        textTotalStockVal.setText(String.valueOf(repo.getTotalStockCount()));
        textLowStockVal.setText(String.valueOf(repo.getLowStockCount()));
        textOutOfStockVal.setText(String.valueOf(repo.getOutOfStockCount()));
    }
}
