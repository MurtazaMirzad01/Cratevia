package com.example.inventorymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private String productId;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        Button btnBackDetail = findViewById(R.id.btnBackDetail);
        Button btnStockIn = findViewById(R.id.btnStockIn);
        Button btnStockOut = findViewById(R.id.btnStockOut);
        Button btnEditProduct = findViewById(R.id.btnEditProduct);
        Button btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        if (btnBackDetail != null) {
            btnBackDetail.setOnClickListener(view -> finish());
        }

        productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);

        btnStockIn.setOnClickListener(view -> showStockAdjustmentDialog(true));
        btnStockOut.setOnClickListener(view -> showStockAdjustmentDialog(false));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        currentProduct = ProductRepository.getInstance(this).getProductById(productId);
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

    private void showStockAdjustmentDialog(boolean isStockIn) {
        if (currentProduct == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isStockIn ? R.string.title_stock_in : R.string.title_stock_out);
        builder.setMessage(isStockIn ? R.string.msg_stock_in : R.string.msg_stock_out);

        // Programmatically create EditText inside Dialog
        final EditText inputQty = new EditText(this);
        inputQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputQty.setHint("Quantity");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(40, 20, 40, 20);
        inputQty.setLayoutParams(lp);

        builder.setView(inputQty);

        builder.setPositiveButton(isStockIn ? "Stock In (+)" : "Stock Out (-)", (dialog, which) -> {
            String qtyStr = inputQty.getText().toString().trim();
            if (qtyStr.isEmpty()) return;

            try {
                int qty = Integer.parseInt(qtyStr);
                if (qty <= 0) {
                    Toast.makeText(this, "Enter a valid positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isStockIn && qty > currentProduct.getQuantity()) {
                    Toast.makeText(this, "Cannot issue more stock than currently available (" + currentProduct.getQuantity() + ")", Toast.LENGTH_LONG).show();
                    return;
                }

                int delta = isStockIn ? qty : -qty;
                boolean success = ProductRepository.getInstance(this).adjustStock(currentProduct.getId(), delta);

                if (success) {
                    String action = isStockIn ? "Added " + qty + " units to stock." : "Issued " + qty + " units from stock.";
                    Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
                    loadProductDetails(); // Refresh UI
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number entered", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteConfirmationDialog() {
        if (currentProduct == null) return;

        String message = getString(R.string.delete_confirm_msg, currentProduct.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    ProductRepository.getInstance(this).deleteProduct(currentProduct.getId());
                    Toast.makeText(ProductDetailActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
