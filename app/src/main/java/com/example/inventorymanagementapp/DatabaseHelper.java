package com.example.inventorymanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper
 * Stage 4: SQLite Persistence using SQLiteOpenHelper & Cursor.
 * Manages database creation, table migrations, and direct SQL operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory_db.db";
    private static final int DATABASE_VERSION = 1;

    // Table & Columns
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SKU = "sku";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_BUY_PRICE = "buy_price";
    public static final String COLUMN_SELL_PRICE = "sell_price";
    public static final String COLUMN_MIN_STOCK = "minimum_stock";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_AT = "created_at";

    private static final String CREATE_TABLE_PRODUCTS =
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_SKU + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_QUANTITY + " INTEGER DEFAULT 0, " +
                    COLUMN_BUY_PRICE + " REAL DEFAULT 0.0, " +
                    COLUMN_SELL_PRICE + " REAL DEFAULT 0.0, " +
                    COLUMN_MIN_STOCK + " INTEGER DEFAULT 10, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CREATED_AT + " INTEGER" +
                    ");";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // INSERT PRODUCT
    public boolean insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, p.getId());
        values.put(COLUMN_NAME, p.getName());
        values.put(COLUMN_SKU, p.getSku());
        values.put(COLUMN_CATEGORY, p.getCategory());
        values.put(COLUMN_QUANTITY, p.getQuantity());
        values.put(COLUMN_BUY_PRICE, p.getBuyPrice());
        values.put(COLUMN_SELL_PRICE, p.getSellPrice());
        values.put(COLUMN_MIN_STOCK, p.getMinimumStock());
        values.put(COLUMN_DESCRIPTION, p.getDescription());
        values.put(COLUMN_CREATED_AT, p.getCreatedAt());

        long result = db.insert(TABLE_PRODUCTS, null, values);
        return result != -1;
    }

    // GET ALL PRODUCTS (READ CURSOR)
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COLUMN_CREATED_AT + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Product p = parseProductFromCursor(cursor);
                list.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // GET PRODUCT BY ID
    public Product getProductById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_ID + "=?",
                new String[]{id}, null, null, null);

        Product p = null;
        if (cursor != null && cursor.moveToFirst()) {
            p = parseProductFromCursor(cursor);
            cursor.close();
        }
        return p;
    }

    // UPDATE PRODUCT
    public boolean updateProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, p.getName());
        values.put(COLUMN_SKU, p.getSku());
        values.put(COLUMN_CATEGORY, p.getCategory());
        values.put(COLUMN_QUANTITY, p.getQuantity());
        values.put(COLUMN_BUY_PRICE, p.getBuyPrice());
        values.put(COLUMN_SELL_PRICE, p.getSellPrice());
        values.put(COLUMN_MIN_STOCK, p.getMinimumStock());
        values.put(COLUMN_DESCRIPTION, p.getDescription());

        int rowsAffected = db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=?", new String[]{p.getId()});
        return rowsAffected > 0;
    }

    // UPDATE QUANTITY (STOCK IN / STOCK OUT)
    public boolean updateStockQuantity(String id, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQuantity);

        int rows = db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=?", new String[]{id});
        return rows > 0;
    }

    // DELETE PRODUCT
    public boolean deleteProduct(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PRODUCTS, COLUMN_ID + "=?", new String[]{id});
        return rowsDeleted > 0;
    }

    // COUNT QUERY
    public int getProductCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Helper: Parse Cursor row into Product object
    private Product parseProductFromCursor(Cursor cursor) {
        int idIdx = cursor.getColumnIndexOrThrow(COLUMN_ID);
        int nameIdx = cursor.getColumnIndexOrThrow(COLUMN_NAME);
        int skuIdx = cursor.getColumnIndexOrThrow(COLUMN_SKU);
        int catIdx = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY);
        int qtyIdx = cursor.getColumnIndexOrThrow(COLUMN_QUANTITY);
        int buyIdx = cursor.getColumnIndexOrThrow(COLUMN_BUY_PRICE);
        int sellIdx = cursor.getColumnIndexOrThrow(COLUMN_SELL_PRICE);
        int minIdx = cursor.getColumnIndexOrThrow(COLUMN_MIN_STOCK);
        int descIdx = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
        int timeIdx = cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT);

        return new Product(
                cursor.getString(idIdx),
                cursor.getString(nameIdx),
                cursor.getString(skuIdx),
                cursor.getString(catIdx),
                cursor.getInt(qtyIdx),
                cursor.getDouble(buyIdx),
                cursor.getDouble(sellIdx),
                cursor.getInt(minIdx),
                cursor.getString(descIdx),
                cursor.getLong(timeIdx)
        );
    }
}
