package com.example.inventorymanagementapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ProductAdapter
 * Standard RecyclerView Adapter following the ViewHolder pattern.
 * Efficiently recycles item views as the user scrolls.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final Context context;
    private List<Product> productList;
    private final OnProductClickListener clickListener;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener clickListener) {
        this.context = context;
        this.productList = new ArrayList<>(productList);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the XML layout for individual list items
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.textName.setText(product.getName());
        String catSku = product.getCategory() + " • SKU: " + product.getSku();
        holder.textCatSku.setText(catSku);
        holder.textPrice.setText(String.format(Locale.getDefault(), "$%.2f", product.getSellPrice()));

        // Stock Badge Coloring & Text
        if (product.isOutOfStock()) {
            holder.textStockBadge.setText("Out of Stock (0)");
            holder.textStockBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.status_red_bg));
            holder.textStockBadge.setTextColor(ContextCompat.getColor(context, R.color.status_red_text));
        } else if (product.isLowStock()) {
            holder.textStockBadge.setText(String.format(Locale.getDefault(), "Low Stock (%d)", product.getQuantity()));
            holder.textStockBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.status_orange_bg));
            holder.textStockBadge.setTextColor(ContextCompat.getColor(context, R.color.status_orange_text));
        } else {
            holder.textStockBadge.setText(String.format(Locale.getDefault(), "Qty: %d in stock", product.getQuantity()));
            holder.textStockBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.status_green_bg));
            holder.textStockBadge.setTextColor(ContextCompat.getColor(context, R.color.status_green_text));
        }

        // Click Handler
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Update list data dynamically
    public void updateList(List<Product> newList) {
        this.productList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // Filter products by search query
    public void filter(String query, List<Product> originalList) {
        if (query == null || query.trim().isEmpty()) {
            updateList(originalList);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<Product> filtered = new ArrayList<>();
        for (Product p : originalList) {
            if (p.getName().toLowerCase().contains(lowerQuery) ||
                p.getSku().toLowerCase().contains(lowerQuery) ||
                p.getCategory().toLowerCase().contains(lowerQuery)) {
                filtered.add(p);
            }
        }
        updateList(filtered);
    }

    /**
     * ViewHolder Class
     * Holds direct references to view children to avoid repeated findViewById() calls.
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textCatSku;
        TextView textStockBadge;
        TextView textPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textCatSku = itemView.findViewById(R.id.textItemCategorySku);
            textStockBadge = itemView.findViewById(R.id.textItemStockBadge);
            textPrice = itemView.findViewById(R.id.textItemPrice);
        }
    }
}
