package com.example.inventorymanagementapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID";

    private TextView textAddProductTitle;
    private TextView textAddProductSubtitle;
    private EditText editProductName;
    private EditText editProductSku;
    private EditText editProductCategory;
    private EditText editProductQuantity;
    private EditText editProductMinStock;
    private EditText editProductBuyPrice;
    private EditText editProductSellPrice;
    private EditText editProductDescription;
    private Button btnSaveProduct;

    private Product existingProduct = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind Views
        textAddProductTitle = findViewById(R.id.textAddProductTitle);
        textAddProductSubtitle = findViewById(R.id.textAddProductSubtitle);
        editProductName = findViewById(R.id.editProductName);
        editProductSku = findViewById(R.id.editProductSku);
        editProductCategory = findViewById(R.id.editProductCategory);
        editProductQuantity = findViewById(R.id.editProductQuantity);
        editProductMinStock = findViewById(R.id.editProductMinStock);
        editProductBuyPrice = findViewById(R.id.editProductBuyPrice);
        editProductSellPrice = findViewById(R.id.editProductSellPrice);
        editProductDescription = findViewById(R.id.editProductDescription);
        Button btnCancelProduct = findViewById(R.id.btnCancelProduct);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        // Cancel button click handler
        btnCancelProduct.setOnClickListener(view -> finish());

        // Check if editing an existing product
        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId != null && !productId.isEmpty()) {
            existingProduct = ProductRepository.getInstance().getProductById(productId);
            if (existingProduct != null) {
                isEditMode = true;
                populateFormWithProduct(existingProduct);
            }
        }

        // Prefill name if coming from intent extra
        String prefilledName = getIntent().getStringExtra("EXTRA_PREFILLED_NAME");
        if (!isEditMode && prefilledName != null && !prefilledName.isEmpty()) {
            editProductName.setText(prefilledName);
        }

        btnSaveProduct.setOnClickListener(view -> saveProduct());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateFormWithProduct(Product p) {
        textAddProductTitle.setText(R.string.title_edit_product);
        textAddProductSubtitle.setText(R.string.subtitle_edit_product);
        btnSaveProduct.setText(R.string.btn_update_product);

        editProductName.setText(p.getName());
        editProductSku.setText(p.getSku());
        editProductCategory.setText(p.getCategory());
        editProductQuantity.setText(String.valueOf(p.getQuantity()));
        editProductMinStock.setText(String.valueOf(p.getMinimumStock()));
        editProductBuyPrice.setText(String.valueOf(p.getBuyPrice()));
        editProductSellPrice.setText(String.valueOf(p.getSellPrice()));
        editProductDescription.setText(p.getDescription());
    }

    private void saveProduct() {
        String name = editProductName.getText().toString().trim();
        String sku = editProductSku.getText().toString().trim();
        String category = editProductCategory.getText().toString().trim();
        String quantityStr = editProductQuantity.getText().toString().trim();
        String minStockStr = editProductMinStock.getText().toString().trim();
        String buyPriceStr = editProductBuyPrice.getText().toString().trim();
        String sellPriceStr = editProductSellPrice.getText().toString().trim();
        String description = editProductDescription.getText().toString().trim();

        // Form Validation
        if (name.isEmpty()) {
            editProductName.setError("Product name is required!");
            editProductName.requestFocus();
            return;
        }

        if (quantityStr.isEmpty()) {
            editProductQuantity.setError("Quantity is required!");
            editProductQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            editProductQuantity.setError("Enter a valid integer quantity");
            editProductQuantity.requestFocus();
            return;
        }

        int minStock = 10; // Default minimum stock
        if (!minStockStr.isEmpty()) {
            try {
                minStock = Integer.parseInt(minStockStr);
            } catch (NumberFormatException ignored) {}
        }

        double buyPrice = 0.0;
        if (!buyPriceStr.isEmpty()) {
            try {
                buyPrice = Double.parseDouble(buyPriceStr);
            } catch (NumberFormatException ignored) {}
        }

        double sellPrice = 0.0;
        if (!sellPriceStr.isEmpty()) {
            try {
                sellPrice = Double.parseDouble(sellPriceStr);
            } catch (NumberFormatException ignored) {}
        }

        if (isEditMode && existingProduct != null) {
            // Update existing product properties
            existingProduct.setName(name);
            existingProduct.setSku(sku);
            existingProduct.setCategory(category.isEmpty() ? "General" : category);
            existingProduct.setQuantity(quantity);
            existingProduct.setMinimumStock(minStock);
            existingProduct.setBuyPrice(buyPrice);
            existingProduct.setSellPrice(sellPrice);
            existingProduct.setDescription(description);

            ProductRepository.getInstance().updateProduct(existingProduct);
            Toast.makeText(this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Create new Product
            Product newProduct = new Product(
                    name,
                    sku.isEmpty() ? "SKU-GEN" : sku,
                    category.isEmpty() ? "General" : category,
                    quantity,
                    buyPrice,
                    sellPrice,
                    minStock,
                    description
            );

            ProductRepository.getInstance().addProduct(newProduct);
            Toast.makeText(this, "New product added to inventory!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
