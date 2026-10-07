package com.example.inventorymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textTotalProductsVal;
    private TextView textTotalStockVal;
    private TextView textLowStockVal;
    private TextView textOutOfStockVal;

    private EditText editQuickNote;
    private Button btnViewCatalog;
    private Button btnShowToast;
    private Button btnOpenAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Dashboard Metric Views
        textTotalProductsVal = findViewById(R.id.textTotalProductsVal);
        textTotalStockVal = findViewById(R.id.textTotalStockVal);
        textLowStockVal = findViewById(R.id.textLowStockVal);
        textOutOfStockVal = findViewById(R.id.textOutOfStockVal);

        // Bind Action Views
        editQuickNote = findViewById(R.id.editQuickNote);
        btnViewCatalog = findViewById(R.id.btnViewCatalog);
        btnShowToast = findViewById(R.id.btnShowToast);
        btnOpenAddProduct = findViewById(R.id.btnOpenAddProduct);

        // View Catalog Button Listener
        btnViewCatalog.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Quick Toast Demo Listener
        btnShowToast.setOnClickListener(view -> {
            String inputText = editQuickNote.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter some text first!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Entered: " + inputText, Toast.LENGTH_SHORT).show();
            }
        });

        // Quick Add Product Listener
        btnOpenAddProduct.setOnClickListener(view -> {
            String noteText = editQuickNote.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            intent.putExtra("EXTRA_PREFILLED_NAME", noteText);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardMetrics();
    }

    // Refresh Dashboard metrics in real time whenever returning to MainActivity
    private void updateDashboardMetrics() {
        ProductRepository repo = ProductRepository.getInstance();

        textTotalProductsVal.setText(String.valueOf(repo.getTotalProductTypesCount()));
        textTotalStockVal.setText(String.valueOf(repo.getTotalStockCount()));
        textLowStockVal.setText(String.valueOf(repo.getLowStockCount()));
        textOutOfStockVal.setText(String.valueOf(repo.getOutOfStockCount()));
    }
}
