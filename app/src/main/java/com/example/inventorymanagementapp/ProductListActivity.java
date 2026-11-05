package com.example.inventorymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private TextView textEmptyList;
    private EditText editSearchQuery;

    private ProductAdapter adapter;
    private List<Product> allProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        textEmptyList = findViewById(R.id.textEmptyList);
        editSearchQuery = findViewById(R.id.editSearchQuery);
        Button btnBackList = findViewById(R.id.btnBackList);
        Button btnHeaderAddProduct = findViewById(R.id.btnHeaderAddProduct);

        // Back button listener
        btnBackList.setOnClickListener(view -> finish());

        // Setup RecyclerView with LinearLayoutManager
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        // Fetch products & initialize Adapter
        allProducts = ProductRepository.getInstance(this).getAllProducts();
        adapter = new ProductAdapter(this, allProducts, product -> {
            // Click handler: Open ProductDetailActivity
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });

        recyclerViewProducts.setAdapter(adapter);

        // Setup Add Product Button Click
        btnHeaderAddProduct.setOnClickListener(view -> {
            Intent intent = new Intent(ProductListActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Setup Live Search / Filter Listener
        editSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString(), allProducts);
                checkEmptyState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
        refreshProductList();
    }

    private void refreshProductList() {
        allProducts = ProductRepository.getInstance(this).getAllProducts();
        String currentQuery = editSearchQuery.getText().toString();
        adapter.filter(currentQuery, allProducts);
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            textEmptyList.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
        } else {
            textEmptyList.setVisibility(View.GONE);
            recyclerViewProducts.setVisibility(View.VISIBLE);
        }
    }
}
