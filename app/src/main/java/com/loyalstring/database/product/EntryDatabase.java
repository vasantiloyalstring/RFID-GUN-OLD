package com.loyalstring.database.product;

import static com.loyalstring.database.support.Valuesdb.BOXTABLE;
import static com.loyalstring.database.support.Valuesdb.CATTABLE;
import static com.loyalstring.database.support.Valuesdb.COUNTER_NAME;
import static com.loyalstring.database.support.Valuesdb.COUNTER_TABLE;
import static com.loyalstring.database.support.Valuesdb.C_BOX;
import static com.loyalstring.database.support.Valuesdb.C_CATEGORY;
import static com.loyalstring.database.support.Valuesdb.C_PRODUCT;
import static com.loyalstring.database.support.Valuesdb.PROTABLE;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.loyalstring.Adapters.ProductAdapter;
import com.loyalstring.LatestApis.BillSupport.UpdateStatusTask;
import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestApis.LoginApiSupport.Employee;
import com.loyalstring.LatestCallBacks.ActivationCallback;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.apiresponse.SKUStoneItem;
import com.loyalstring.apiresponse.SKUStoneMain;
import com.loyalstring.apiresponse.SkuResponse;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.jjjcustomermodel;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EntryDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "loyalstring.db";
    private static final int DATABASE_VERSION = 1;
    private static final int BATCH_SIZE = 1000;
    String ALL_TABLE = "alltable";
    String D_TABLE = "detailstable";
    String T_TABLE = "transactiontable";
    String TR_TABLE = "tracktable";
    String RFID_TABLE = "rfidtable";
    String EMAIL_TABLE = "emailstable";
    private static final String C_EMAILID = "emailid";
    String R_TABLE = "remaptable";
    String I_TABLE = "issuetable";
    String SKU_TABLE = "skutable";
    String VENDOR_TABLE = "vendortable";
    String SKU_STONE = "skustone";
    String STONE_ITEMDETAILS = "stonedetails";

    String C_TABLE = "customertable";
    private ExecutorService executorService;
    String logintable = "LoginTable";
    String clienttable = "ClientTable";

    String countertable = "CounterTable";

    String INVENTORY_SAVE_TABLE = "InventoryTable";

    String All_SAVE_TABLE = "All_SAVE_DATA";
    private static final String COL_ID = "id";
    private static final String COL_DATA = "data"; // Serialized object stored here

    private final Gson gson = new Gson();


    public EntryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Any other initialization if needed
        executorService = Executors.newFixedThreadPool(8);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Implementation of onUpgrade if needed
    }


    public void checkdatabase(Context mContext) {
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = entryDatabase.getWritableDatabase();
        createalltable(db, Itemmodel.class, ALL_TABLE);
        createalltable(db, Itemmodel.class, D_TABLE);
        createalltable(db, Itemmodel.class, T_TABLE);
        createalltable(db, Itemmodel.class, R_TABLE);
        createalltable(db, Issuemode.class, I_TABLE);
        createalltable(db, SkuResponse.SKUVendor.class, VENDOR_TABLE);
        createalltable(db, SkuResponse.class, SKU_TABLE);
        createalltable(db, SKUStoneMain.class, SKU_STONE);

        createalltable(db, Employee.class, logintable);
        createalltable(db, Clients.class, clienttable);
     //   createalltable(db,Itemmodel.class,INVENTORY_SAVE_TABLE);

        createalltable(db, SKUStoneItem.class, STONE_ITEMDETAILS);

        String inventoryTableQuery = "CREATE TABLE IF NOT EXISTS " + INVENTORY_SAVE_TABLE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                COL_DATA + " TEXT)";
        db.execSQL(inventoryTableQuery);

        String SaveAllTableQuery = "CREATE TABLE IF NOT EXISTS " + All_SAVE_TABLE + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATA + " TEXT)";
        db.execSQL(SaveAllTableQuery);


        String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + CATTABLE + " (" +
                C_CATEGORY + " TEXT, " +
                C_PRODUCT + " TEXT" +
                ")";

        db.execSQL(CREATE_CATEGORY_TABLE);

        // Create Product Table
        String createRatesTableQuery = "CREATE TABLE IF NOT EXISTS " + PROTABLE + " (" +
                C_CATEGORY + " TEXT, " +
                C_PRODUCT + " TEXT, " +
                "PRIMARY KEY (" + C_CATEGORY + ", " + C_PRODUCT + ")" +
                ")";

        db.execSQL(createRatesTableQuery);


        // Create Box Table
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + BOXTABLE + " (" +
                C_BOX + " TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);

        String CREATE_COUNTER_TABLE = "CREATE TABLE IF NOT EXISTS " + COUNTER_TABLE + " (" +
                COUNTER_NAME + " TEXT," +
                C_CATEGORY + " TEXT, " +
                C_PRODUCT + " TEXT," +
                C_BOX + " TEXT" +
                ")";

        db.execSQL(CREATE_COUNTER_TABLE);
    }

    public List<Itemmodel> getAllFromAllTable() {
        List<Itemmodel> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + ALL_TABLE, null);

            if (cursor.moveToFirst()) {
                do {
                    Itemmodel item = new Itemmodel();
                    item.setProduct(cursor.getString(cursor.getColumnIndex("product")));
                    item.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                    item.setEntryDate(cursor.getLong(cursor.getColumnIndex("entryDate")));
                    item.setAvlQty(cursor.getDouble(cursor.getColumnIndex("avlQty")));
                    item.setMatchQty(cursor.getDouble(cursor.getColumnIndex("matchQty")));
                    item.setInventoryStatus(cursor.getString(cursor.getColumnIndex("inventoryStatus")));

                    itemList.add(item);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        return itemList;
    }




    // Save object
  /*  public void saveItem(List<Itemmodel> itemList) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE " + INVENTORY_SAVE_TABLE + " ADD COLUMN product TEXT");
        } catch (Exception e) {
            Log.d("@@schema", "Column 'product' already exists or error: " + e.getMessage());
        }

        try {
            db.execSQL("ALTER TABLE " + INVENTORY_SAVE_TABLE + " ADD COLUMN entryDate INTEGER");
        } catch (Exception e) {
            Log.d("@@schema", "Column 'entryDate' already exists or error: " + e.getMessage());
        }
        try {
            db.execSQL("ALTER TABLE " + INVENTORY_SAVE_TABLE + " ADD COLUMN inventoryStatus String");
        } catch (Exception e) {
            Log.d("@@schema", "Column 'entryDate' already exists or error: " + e.getMessage());
        }
       *//* for (Itemmodel item : itemList) {
            String json = gson.toJson(item);
            ContentValues values = new ContentValues();
            values.put(COL_DATA, json);
            db.insert(INVENTORY_SAVE_TABLE, null, values);
        }*//*
        for (Itemmodel item : itemList) {
            String json = gson.toJson(item);

            ContentValues values = new ContentValues();
            values.put("product", item.getProduct());            // <-- Add this
            values.put("entryDate", item.getEntryDate());        // <-- Add this
            values.put("inventoryStatus", item.getInventoryStatus());
            values.put(COL_DATA, json);
            db.insert(INVENTORY_SAVE_TABLE, null, values);
        }// Keep full JSON if needed

        Log.d("@@ data", "data added");
        db.close();
    }*/

    public void saveItem(List<Itemmodel> itemList) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Begin transaction for atomic operations
        db.beginTransaction();

        try {
            // Add new columns to the table if they don't already exist
            addColumnIfNotExists(db, "BarCode", "TEXT");
            addColumnIfNotExists(db, "entryDate", "LONG");
            addColumnIfNotExists(db, "inventoryStatus", "TEXT");
            addColumnIfNotExists(db, "ItemCode", "TEXT");
            addColumnIfNotExists(db, "MatchQty", "TEXT");

            for (Itemmodel item : itemList) {
                // Check if the item already exists by a unique key (e.g., ItemCode)
                String ItemCode = item.getItemCode();
                Cursor cursor = db.query(INVENTORY_SAVE_TABLE, null, "ItemCode = ?", new String[]{ItemCode}, null, null, null);

                ContentValues values = new ContentValues();
                values.put("BarCode", item.getBarCode());
                values.put("entryDate", item.getEntryDate());
                values.put("inventoryStatus", "unmatch");
                values.put("ItemCode", item.getItemCode());
                values.put("MatchQty", item.getMatchQty());
                values.put(COL_DATA, gson.toJson(item)); // Update the full JSON data

                if (cursor != null && cursor.getCount() > 0) {
                    // If item exists, update the existing entry
                    cursor.moveToFirst();
                    String existingItemId = cursor.getString(cursor.getColumnIndex("ItemCode"));
                    db.update(INVENTORY_SAVE_TABLE, values, "ItemCode = ?", new String[]{existingItemId});
                    Log.d("@@UpdateStatus", "Updated item with ItemCode: " + existingItemId);
                } else {
                    // If the item doesn't exist, insert a new record
                    db.insert(INVENTORY_SAVE_TABLE, null, values);
                    Log.d("@@InsertStatus", "Inserted new item with ItemCode: " + ItemCode);
                }

                // Close cursor after use
                if (cursor != null) {
                    cursor.close();
                }
            }

            // Commit transaction
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("@@Error", "Error saving/updating item: " + e.getMessage());
        } finally {
            // End transaction
            db.endTransaction();
            db.close();
        }

        Log.d("@@Done", "Data added or updated successfully");
    }

    // Utility method to add columns if they don't already exist
    private void addColumnIfNotExists(SQLiteDatabase db, String columnName, String columnType) {
        try {
            db.execSQL("ALTER TABLE " + INVENTORY_SAVE_TABLE + " ADD COLUMN " + columnName + " " + columnType);
        } catch (Exception e) {
            Log.d("@@schema", "Column '" + columnName + "' already exists or error: " + e.getMessage());
        }
    }


   /* public void updateInventoryStatus(List<Itemmodel> itemList) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {
            for (Itemmodel item : itemList) {

                String productCode = item.getItemCode();  // 🔥 Use correct primary key field here
                long entryDate = item.getEntryDate();
                String inventoryStatus = "match";

                Log.d("Query Debug", "Executing query with ItemCode: " + productCode + " and entryDate: " + entryDate);

                Cursor cursor = db.query(INVENTORY_SAVE_TABLE, null,
                        "ItemCode = ?", new String[]{productCode},
                        null, null, null);

                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        if (cursor.moveToFirst()) {
                            String existingInventoryStatus = cursor.getString(cursor.getColumnIndexOrThrow("inventoryStatus"));
                            Log.d("ExistingStatus", "InventoryStatus in DB: " + existingInventoryStatus);

                            ContentValues values = new ContentValues();
                            values.put("inventoryStatus", inventoryStatus);

                            int rowsUpdated = db.update(
                                    INVENTORY_SAVE_TABLE,
                                    values,
                                    "ItemCode = ?",
                                    new String[]{productCode}
                            );

                            if (rowsUpdated > 0) {
                                Log.d("@@UpdateStatus", "Updated inventoryStatus for: " + productCode);
                            } else {
                                Log.d("@@NoUpdate", "Failed to update inventoryStatus for: " + productCode);
                            }
                        }
                    } else {
                        Log.d("@@NotFound", "Item not found for update: " + productCode);
                    }
                    cursor.close();
                } else {
                    Log.d("@@CursorNull", "Cursor is null for ItemCode: " + productCode);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("@@Exception", "Error during inventoryStatus update: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        db.close();
        Log.d("@@Done", "InventoryStatus update completed");
    }*/
   public void updateInventoryStatus(List<Itemmodel> itemList) {
       SQLiteDatabase db = this.getWritableDatabase();

       // Start a transaction to ensure atomicity
       db.beginTransaction();

       try {
           for (Itemmodel item : itemList) {
               String productCode = item.getItemCode();  // Use ItemCode as the unique identifier
               long entryDate = item.getEntryDate();  // Make sure the entryDate is in the correct format
               double matchQty = item.getMatchQty();
               Log.d("@@ matchQty", "@@ matchQty" + matchQty);

               String inventoryStatus =item.getInventoryStatus(); // Set inventory status based on conditions

               Log.d("Query Debug", "Executing query with ItemCode: " + productCode + " and entryDate: " + entryDate);

               // Query the database to check if the item already exists
               Cursor cursor = db.query(INVENTORY_SAVE_TABLE, null, "ItemCode = ?", new String[]{productCode}, null, null, null);

               if (cursor != null) {
                   if (cursor.moveToFirst()) {
                       // Item found, prepare to update the inventoryStatus
                       String existingInventoryStatus = cursor.getString(cursor.getColumnIndexOrThrow("inventoryStatus"));
                       Log.d("ExistingStatus", "InventoryStatus in DB: " + existingInventoryStatus);
                       Log.d("@@", "vasanti matchQty" + matchQty);
                       // Prepare content values with the updated inventoryStatus
                       ContentValues values = new ContentValues();
                       values.put("inventoryStatus", inventoryStatus);
                       values.put("matchQty", matchQty);// Update the status

                       // Perform the update operation
                       int rowsUpdated = db.update(INVENTORY_SAVE_TABLE, values, "ItemCode = ?", new String[]{productCode});

                       if (rowsUpdated > 0) {
                           Log.d("@@UpdateStatus", "Updated inventoryStatus for ItemCode: " + productCode);
                       } else {
                           Log.d("@@NoUpdate", "Failed to update inventoryStatus for ItemCode: " + productCode);
                       }



                       // Optionally log or use the updated data

                   } else {
                       // Item not found in the database
                       Log.d("@@NotFound", "Item not found for ItemCode: " + productCode);
                   }
                   cursor.close();
               } else {
                   // If cursor is null, log the error
                   Log.d("@@CursorNull", "Cursor is null for ItemCode: " + productCode);
               }
           }

           // Mark the transaction as successful
           db.setTransactionSuccessful();
       } catch (Exception e) {
           Log.e("@@Exception", "Error during inventoryStatus update: " + e.getMessage());
       } finally {
           // End the transaction
           db.endTransaction();
       }

       // Close the database connection
       db.close();
       Log.d("@@Done", "InventoryStatus update completed");
   }



    /*for stock*/
    public void saveAllItem(List<Itemmodel> itemList) {
        SQLiteDatabase db = this.getWritableDatabase();


        for (Itemmodel item : itemList) {
            String json = gson.toJson(item);

            ContentValues values = new ContentValues();
            values.put("product", item.getProduct());            // <-- Add this
            values.put("entryDate", item.getEntryDate());        // <-- Add this
            values.put(COL_DATA, json);                          // Keep full JSON if needed

            db.insert(INVENTORY_SAVE_TABLE, null, values);
        }
        Log.d("@@ data", "data added");
        db.close();
    }

    public void deleteItemsOlderThan(long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "EntryDate < ?";
        String[] selectionArgs = {String.valueOf(timestamp)};
        db.delete(INVENTORY_SAVE_TABLE, selection, selectionArgs);
        db.close();
    }


    //delete the item by date
    public void deleteItemsByDate(long targetTimestamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                INVENTORY_SAVE_TABLE,
                new String[]{COL_ID, COL_DATA},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String json = cursor.getString(cursor.getColumnIndex(COL_DATA));
                int rowId = cursor.getInt(cursor.getColumnIndex(COL_ID));
                try {
                    Itemmodel item = gson.fromJson(json, Itemmodel.class);
                    if (item != null && item.getEntryDate() == targetTimestamp) {
                        db.delete(INVENTORY_SAVE_TABLE, COL_ID + "=?", new String[]{String.valueOf(rowId)});
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Handle or log invalid JSON entries if any
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
    }

    // Get all objects
    public List<Itemmodel> getAllItems() {
        List<Itemmodel> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Fetch both the JSON data and inventoryStatus column
        Cursor cursor = db.rawQuery("SELECT * FROM " + INVENTORY_SAVE_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                // Fetch the JSON data for Itemmodel
                String json = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA));
                Itemmodel item = gson.fromJson(json, Itemmodel.class); // Convert JSON to Itemmodel

                // Retrieve the inventoryStatus directly from the database
                String inventoryStatus = cursor.getString(cursor.getColumnIndexOrThrow("inventoryStatus"));
                item.setInventoryStatus(inventoryStatus);  // Set the inventoryStatus for the item

                double matchQty = cursor.getDouble(cursor.getColumnIndexOrThrow("MatchQty"));
                item.setMatchQty(matchQty);

                // Add the item to the list

                Log.d("@@", "@@ match qty" + item.getMatchQty());
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }


    public List<Itemmodel> getAllSavedItems() {
        List<Itemmodel> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + All_SAVE_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                String json = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATA));
                Itemmodel item = gson.fromJson(json, Itemmodel.class); // ✅ Correct way
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    public List<Itemmodel> getAllItemsFromDatabase() {
        List<Itemmodel> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ALL_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                Itemmodel item = new Itemmodel();

                item.setItemCode(cursor.getString(cursor.getColumnIndexOrThrow("ItemCode")));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")));
                item.setCounterId(cursor.getString(cursor.getColumnIndexOrThrow("counterId")));
                item.setDesignId(cursor.getInt(cursor.getColumnIndexOrThrow("designId")));
                item.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("productId")));
                item.setEntryDate(cursor.getLong(cursor.getColumnIndexOrThrow("EntryDate")));
                item.setProduct(cursor.getString(cursor.getColumnIndexOrThrow("Product")));
                item.setPurityId(cursor.getInt(cursor.getColumnIndexOrThrow("purityId")));
              //  item.setCounterName(cursor.getString(cursor.getColumnIndexOrThrow("diamondClarity")));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("Category")));
                item.setCounterName(cursor.getString(cursor.getColumnIndexOrThrow("counterName")));
                item.setAvlQty(cursor.getInt(cursor.getColumnIndexOrThrow("AvlQty")));
                item.setMatchQty(cursor.getInt(cursor.getColumnIndexOrThrow("MatchQty")));
                // ... add other fields from your schema

                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }


    public void checkdatabaset(Context mContext) {
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = entryDatabase.getWritableDatabase();
        createalltable(db, Itemmodel.class, ALL_TABLE);
    }


    public void makeentry(Context activity, List<Itemmodel> itemlist, String etype, String frag, MyApplication app, List<Issuemode> issueitem, SaveCallback saveCallback) {
        SaveItemAsyncTask asyncTask = new SaveItemAsyncTask(activity, itemlist, etype, frag, app, saveCallback, issueitem);
        asyncTask.execute();



    }

    public void deleteBills(String invoiceNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the WHERE clause and arguments
        String whereClause = "InvoiceNumber = ?";
        String[] whereArgs = {invoiceNumber};

        // Execute the delete operation
        int rowsDeleted = db.delete(T_TABLE, whereClause, whereArgs);

        // Optionally log the number of rows deleted
        Log.d("EntryDatabase", "Deleted " + rowsDeleted + " rows with invoice number: " + invoiceNumber);

        db.close();
    }


    public void updatebillstoweb(String clientCode, ActivationCallback activationCallback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<String> ItemCodes = getItemCodesForEstimation();
                Log.d("Billed ItemCode", "" + ItemCodes.size());
                if (!ItemCodes.isEmpty()) {
                    new UpdateStatusTask(ItemCodes, clientCode, activationCallback).execute();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Handle results on the main thread
                        for (String code : ItemCodes) {
                            Log.d("ItemCode", code);
                        }
                    }
                });
            }
        });
    }

    public List<String> getItemCodesForEstimation() {
        List<String> ItemCodes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

//        String query = "SELECT ItemCode FROM " + T_TABLE + " WHERE Operation = 'Estimation'";
        String query = "SELECT ItemCode FROM " + T_TABLE + " WHERE Operation IN ('Estimation', 'Order Estimation')";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("BILL_API", "Number of items found: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                String item = cursor.getString(0);
                if (item != null && !item.isEmpty()) {
                    ItemCodes.add(item);
                    Log.e("BILL_API", "items  " + item);
                }
            } while (cursor.moveToNext());
        } else {
            Log.e("BILL_API", "No items found.");
        }


        cursor.close();
        return ItemCodes;
    }


    public void AutoSync(List<Itemmodel> response, ProgressDialog dialog, MyApplication myapp, SaveCallback saveCallback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Itemmodel> failedItems = new ArrayList<>();
                Log.e("check processing items", "  " + response.size());
                boolean success = insertItems(response, dialog, failedItems, myapp);

                // Notify the callback on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (success) {
                        saveCallback.onSaveSuccess();
                    } else {
                        saveCallback.onSaveFailure(failedItems);
                    }
                });
            }
        });
    }

    public boolean insertItems(List<Itemmodel> items, ProgressDialog dialog, List<Itemmodel> failedItems, MyApplication myapp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();  // Start a database transaction
        int totalItems = items.size();
        int processedItems = 0; // To track processed items

        try {
            Set<String> uniqueCounter = new HashSet<>();
            Set<String> uniqueCategories = new HashSet<>();
            Set<String> uniqueProducts = new HashSet<>();
            Set<String> uniqueBoxes = new HashSet<>();


            /*for counter*/
            for (Itemmodel item : items) {
                uniqueCounter.add(item.getCounterName());
                //  uniqueCounter.add(item.getCounterName() + "|" + item.getProduct());
                if (item.getCounterName() != null && !item.getCounterName().isEmpty()) {
                    uniqueCounter.add(item.getCounterName() + "|" + item.getCategory() + "|" + item.getProduct()+ "|" + item.getBox());

                }
            }

            // Populate unique lists
            for (Itemmodel item : items) {
                uniqueCategories.add(item.getCategory());
                uniqueProducts.add(item.getCategory() + "|" + item.getProduct());
                if (item.getBox() != null && !item.getBox().isEmpty()) {
                    uniqueBoxes.add(item.getBox());
                }
            }

            // Insert categories
            for (String category : uniqueCategories) {
                if (!categoryExists(db, category)) {
                    ContentValues categoryValues = new ContentValues();
                    categoryValues.put(C_CATEGORY, category);
                    db.insert(CATTABLE, null, categoryValues);
                    Log.d("check catcat", "Inserted category: " + category);
                }
            }

            // Insert products
            for (String uniqueProduct : uniqueProducts) {
                String[] parts = uniqueProduct.split("\\|");
                String category = parts[0];
                String product = parts[1];
                if (!productExists(db, category, product)) {
                    ContentValues productValues = new ContentValues();
                    productValues.put(C_CATEGORY, category);
                    productValues.put(C_PRODUCT, product);
                    db.insert(PROTABLE, null, productValues);
                    Log.d("check product", "Inserted product: " + product);
                }
            }


            // Insert counter
            for (String counter : uniqueCounter) {

                String[] parts = counter.split("\\|");
                String counter1 = parts[0];
                String category = parts[1];
                String product = parts[2];
                String box = parts[3];
                if (!counterExist(db, counter)) {
                    ContentValues counterValue = new ContentValues();
                    counterValue.put(COUNTER_NAME, counter1);
                    counterValue.put(C_CATEGORY, category);
                    counterValue.put(C_PRODUCT, product);
                    counterValue.put(C_BOX, box);


                    db.insert(COUNTER_TABLE, null, counterValue);
                    Log.d("check counter", "Inserted counter: " + counter);
                }
            }



            // Insert boxes
            for (String box : uniqueBoxes) {
                if (!boxExists(db, box)) {
                    ContentValues boxValues = new ContentValues();
                    boxValues.put(C_BOX, box);
                    db.insert(BOXTABLE, null, boxValues);
                    Log.d("check box", "Inserted box: " + box);
                }
            }

            // Insert unique categories, products, and boxes as before...



            // Insert items in bulk
            for (Itemmodel item : items) {
                ContentValues values = new ContentValues();
                Field[] fields = Itemmodel.class.getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);
                    String columnName = field.getName();
                    Object value = field.get(item);
                    if (!columnName.equals("id")) { // Skip the ID field if it exists
                        if (value instanceof Long) {
                            values.put(columnName, (Long) value);
                        } else if (value instanceof String) {
                            values.put(columnName, (String) value);
                        } else if (value instanceof Double) {
                            values.put(columnName, (Double) value);
                        } else if (value instanceof Double) {
                            values.put(columnName, (Double) value);
                        }
                    }
                }

                // Using insert or replace to avoid unnecessary checks
                long result = db.insertWithOnConflict(ALL_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (result == -1) {
                    failedItems.add(item); // Add to failed items if the insert fails
                    Log.e("Insert Error", "Failed to insert item: " + item.getTidValue());
                } else {
                    myapp.putitem(item); // Successfully inserted, add to application inventory
                }

                processedItems++;
                // Update the progress dialog
                if (dialog != null && processedItems % 1000 == 0) { // Update every 1000 items
                    dialog.setMessage("Processed " + processedItems + " of " + totalItems);
                }
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
            return true; // Return true indicating success
        } catch (Exception e) {
            Log.e("Transaction Error", "Transaction failed", e);
            return false; // Return false indicating failure
        } finally {
            db.endTransaction(); // Always end the transaction
        }
    }


    public boolean insertItemsold(List<Itemmodel> items, ProgressDialog dialog, List<Itemmodel> failedItems, MyApplication myapp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();  // Start a database transaction
        int totalItems = items.size();
        int processedItems = 0; // To track processed items
        try {
            // Prepare unique lists for categories, products, and boxes
            Set<String> uniqueCategories = new HashSet<>();
            Set<String> uniqueProducts = new HashSet<>();
            Set<String> uniqueBoxes = new HashSet<>();

            // Populate unique lists
            for (Itemmodel item : items) {
                uniqueCategories.add(item.getCategory());
                uniqueProducts.add(item.getCategory() + "|" + item.getProduct()); // Combine category and product as key
                if (item.getBox() != null && !item.getBox().isEmpty()) {
                    uniqueBoxes.add(item.getBox());
                }
            }

            // Insert categories
            for (String category : uniqueCategories) {
                if (!categoryExists(db, category)) {
                    ContentValues categoryValues = new ContentValues();
                    categoryValues.put(C_CATEGORY, category);
                    db.insert(CATTABLE, null, categoryValues);
                    Log.d("check catcat", "Inserted category: " + category);
                }
            }

            // Insert products
            for (String uniqueProduct : uniqueProducts) {
                String[] parts = uniqueProduct.split("\\|");
                String category = parts[0];
                String product = parts[1];
                if (!productExists(db, category, product)) {
                    ContentValues productValues = new ContentValues();
                    productValues.put(C_CATEGORY, category);
                    productValues.put(C_PRODUCT, product);
                    db.insert(PROTABLE, null, productValues);
                    Log.d("check product", "Inserted product: " + product);
                }
            }

            // Insert boxes
            for (String box : uniqueBoxes) {
                if (!boxExists(db, box)) {
                    ContentValues boxValues = new ContentValues();
                    boxValues.put(C_BOX, box);
                    db.insert(BOXTABLE, null, boxValues);
                    Log.d("check box", "Inserted box: " + box);
                }
            }

            // Handle item insertions/updates
            for (Itemmodel item : items) {
                String whereClause = "TidValue = ?";
                String[] whereArgs = {item.getTidValue()};
                Cursor cursor1 = null;

                try {
                    cursor1 = db.query(ALL_TABLE, null, whereClause, whereArgs, null, null, null);
                    ContentValues values = new ContentValues();
                    ContentValues dvalue = new ContentValues();

                    Field[] fields = Itemmodel.class.getDeclaredFields();
                    for (Field field : fields) {
                        try {
                            field.setAccessible(true);
                            String columnName = field.getName();
                            Object value = field.get(item);
                            // Skip the ID field if it exists
                            if (!columnName.equals("id")) {
                                if (value instanceof Long) {
                                    values.put(columnName, (Long) value);
                                    dvalue.put(columnName, (Long) value);
                                } else if (value instanceof String) {
                                    values.put(columnName, (String) value);
                                    dvalue.put(columnName, (String) value);
                                } else if (value instanceof Double) {
                                    values.put(columnName, (Double) value);
                                    dvalue.put(columnName, (Double) value);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Insert Error", "Error accessing field: " + field.getName(), e);
                        }
                    }

                    if (cursor1 != null && cursor1.moveToFirst()) {
                        // Item exists, update it
                        db.update(ALL_TABLE, values, whereClause, whereArgs);
                        db.insert(D_TABLE, null, dvalue);
                        myapp.putitem(item);
                    } else {
                        // Item does not exist, add it
                        db.insert(ALL_TABLE, null, values);
                        db.insert(D_TABLE, null, dvalue);
                        myapp.putitem(item);
                    }

                } catch (Exception e) {
                    Issuemode is = new Issuemode();
                    is.setIssue("failed at db" + e.getMessage());
                    failedItems.add(item); // Add to failed items on error
                    Log.e("Insert Error", "Failed to insert/update item: " + item.getTidValue(), e);
                } finally {
                    if (cursor1 != null) {
                        cursor1.close();
                    }
                }
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
            return true; // Return true indicating success
        } catch (Exception e) {
            Log.e("Transaction Error", "Transaction failed", e);
            return false; // Return false indicating failure
        } finally {
            db.endTransaction(); // Always end the transaction
        }
    }

    public boolean insertItems1(List<Itemmodel> items, List<Itemmodel> failedItems, MyApplication myapp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();  // Start a database transaction
        try {
            // Prepare unique lists for categories, products, and boxes
            Set<String> uniqueCategories = new HashSet<>();
            Set<String> uniqueProducts = new HashSet<>();
            Set<String> uniqueBoxes = new HashSet<>();
            // Populate unique lists

            for (Itemmodel item : items) {
                uniqueCategories.add(item.getCategory());
                uniqueProducts.add(item.getCategory() + "|" + item.getProduct()); // Combine category and product as key
                if (item.getBox() != null && !item.getBox().isEmpty()) {
                    uniqueBoxes.add(item.getBox());
                }
            }

            // Insert categories
            for (String category : uniqueCategories) {
                if (!categoryExists(db, category)) {
                    ContentValues categoryValues = new ContentValues();
                    categoryValues.put(C_CATEGORY, category);
                    db.insert(CATTABLE, null, categoryValues);
                    Log.d("check catcat", "Inserted category: " + category);
                }
            }
            // Insert products
            for (String uniqueProduct : uniqueProducts) {
                String[] parts = uniqueProduct.split("\\|");
                String category = parts[0];
                String product = parts[1];
                if (!productExists(db, category, product)) {
                    ContentValues productValues = new ContentValues();
                    productValues.put(C_CATEGORY, category);
                    productValues.put(C_PRODUCT, product);
                    db.insert(PROTABLE, null, productValues);
                    Log.d("check product", "Inserted product: " + product);
                }
            }

            // Insert boxes
            for (String box : uniqueBoxes) {
                if (!boxExists(db, box)) {
                    ContentValues boxValues = new ContentValues();
                    boxValues.put(C_BOX, box);
                    db.insert(BOXTABLE, null, boxValues);
                    Log.d("check box", "Inserted box: " + box);
                }
            }

            // Prepare the fields for dynamic SQL statement generation
            Field[] fields = Itemmodel.class.getDeclaredFields();
            StringBuilder columnNames = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();

            for (int i = 0; i < fields.length; i++) {
                if (i > 0) {
                    columnNames.append(", ");
                    placeholders.append(", ");
                }
                columnNames.append(fields[i].getName()); // Column name
                placeholders.append("?"); // Placeholder for value
            }
            // Construct the SQL statement
            String sql = "INSERT INTO " + ALL_TABLE + " (" + columnNames + ") VALUES (" + placeholders + ")";
            SQLiteStatement insertStatement = db.compileStatement(sql);

            // Insert items in batches
            for (int i = 0; i < items.size(); i++) {
                Itemmodel item = items.get(i);
                bindValuesToStatement(insertStatement, item, fields);

                Log.e("check inserting", "Inserting item: " + item);

                try {
                    long id = insertStatement.executeInsert(); // Perform the insert
                    if (id == -1) {
                        failedItems.add(item); // If insert fails, add item to failed list
                        Log.e("Insert Error", "Failed to insert item: " + item);
                    } else {
                        myapp.putitem(item); // Add item to MyApplication if insert is successful
                        Log.d("Insert Success", "Inserted item with ID: " + id);
                    }
                } catch (SQLiteConstraintException e) {
                    failedItems.add(item); // If insert fails, add item to failed list
                    Log.e("Insert Constraint Error", "Constraint error for item: " + item + " - " + e.getMessage());
                }

                // Commit every 1000 inserts (or whatever batch size you prefer)
                if (i > 0 && i % 10000 == 0) {
                    db.setTransactionSuccessful(); // Mark the transaction as successful
                    db.endTransaction(); // End the current transaction
                    db.beginTransaction(); // Start a new transaction for the next batch
                    insertStatement = db.compileStatement(sql); // Recompile the insert statement for the next batch
                }
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
            return true; // Indicate successful insertion
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions
            return false; // Indicate failure
        } finally {
            db.endTransaction(); // Always end the transaction
        }
    }

    private void bindValuesToStatement(SQLiteStatement insertStatement, Itemmodel item, Field[] fields) {
        int index = 1; // Start binding from 1
        for (Field field : fields) {
            try {
                field.setAccessible(true); // Access private fields
                Object value = field.get(item); // Get the value of the field
                // Check the type of value and bind it accordingly
                if (value instanceof Long) {
                    insertStatement.bindLong(index++, (Long) value); // Bind Long value
                } else if (value instanceof String) {
                    insertStatement.bindString(index++, (String) value); // Bind String value
                } else if (value instanceof Double) {
                    insertStatement.bindDouble(index++, (Double) value); // Bind Double value
                }
                // You can add more conditions for other types as necessary
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle access issues
            }
        }
    }

    public boolean insertItems1(List<Itemmodel> items, List<Itemmodel> failedItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Itemmodel item : items) {
                // Check and insert category
                if (!categoryExists(db, item.getCategory())) {
                    ContentValues categoryValues = new ContentValues();
                    categoryValues.put(C_CATEGORY, item.getCategory());
                    db.insert(CATTABLE, null, categoryValues);
                    Log.d("check catcat", "Inserted category: " + item.getCategory());
                }

                // Check and insert product
                if (!productExists(db, item.getCategory(), item.getProduct())) {
                    ContentValues productValues = new ContentValues();
                    productValues.put(C_CATEGORY, item.getCategory());
                    productValues.put(C_PRODUCT, item.getProduct());
                    db.insert(PROTABLE, null, productValues);
                    Log.d("check product", "Inserted product: " + item.getProduct());
                }

                // Check and insert box if it exists
                if (item.getBox() != null && !item.getBox().isEmpty() && !boxExists(db, item.getBox())) {
                    ContentValues boxValues = new ContentValues();
                    boxValues.put(C_BOX, item.getBox());
                    db.insert(BOXTABLE, null, boxValues);
                    Log.d("check box", "Inserted box: " + item.getBox());
                }
                ContentValues values = new ContentValues();
                // Get all fields in the inventorymodel class
                Field[] fields = Itemmodel.class.getDeclaredFields();
                // Iterate through the fields and add data to ContentValues
                for (Field field : fields) {
                    try {
                        field.setAccessible(true);
                        String columnName = field.getName();
                        Object value = field.get(item);
                        if (value instanceof Long) {
                            values.put(columnName, (Long) value);
//                                    dvalue.put(columnName, (Long) dval);
                        } else if (value instanceof String) {
                            values.put(columnName, (String) value);
//                                    dvalue.put(columnName, (String) dval);
                        } else if (value instanceof Double) {
                            values.put(columnName, (Double) value);
//                                    dvalue.put(columnName, (Double) dval);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                long result = db.insert(ALL_TABLE, null, values);
//                            long result1 = db.insert(D_TABLE, null, values);
                if (result == -1) {
                    failedItems.add(item); // Add item to the list of failed items
                } else {
//                    mapp.putitem(item);
                }
            }
            db.setTransactionSuccessful(); // Mark the transaction as successful
            return true; // Indicate successful insertion
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Indicate failure
        } finally {
            db.endTransaction(); // Always end the transaction
        }
    }

    public void deleteItemsInBackground(final List<Itemmodel> itemsToDelete, MyApplication myapp) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                deleteItems(itemsToDelete, myapp);
            }
        });
    }

    private void deleteItems(List<Itemmodel> itemsToDelete, MyApplication myapp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();  // Start a database transaction
        try {
            for (Itemmodel item : itemsToDelete) {
                String whereClause = "ItemCode = ? OR TidValue = ?"; // Use actual column names
                String[] whereArgs = new String[]{item.getItemCode(), item.getTidValue()}; // Replace with actual values

                // Execute delete operation for each item in the batch
                int rowsAffected = db.delete(ALL_TABLE, whereClause, whereArgs);
                if (rowsAffected > 0) {
                    myapp.removeite(item); // Update cache or memory structure if needed
                }
                Log.e("deleted", "allitems   " + item.getItemCode());
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions
        } finally {
            db.endTransaction(); // Always end the transaction
        }
    }

    public void deleteItems(MyApplication myapp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();  // Start a database transaction
        try {
            // Execute delete operation to remove all items from the table
            int rowsAffected = db.delete(ALL_TABLE, null, null); // No conditions, delete all rows
            if (rowsAffected > 0) {
                Log.e("deleted", "All data deleted from ALL_TABLE");
              //  myapp.clearItems(); // Update cache or memory structure if needed (this method should be defined in MyApplication)
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions
        } finally {
            db.endTransaction(); // Always end the transaction
        }
    }


    public class SaveItemAsyncTask extends AsyncTask<Void, Integer, List<Itemmodel>> {
        private Context mContext;
        private List<Itemmodel> mItemList;
        private String mEType;
        private String mFrag;
        private ProgressDialog progressDialog;
        private MyApplication mapp;
        private ProductAdapter mproductAdapter;
        private SaveCallback callback;
        private List<Issuemode> issueitem;

        public SaveItemAsyncTask(Context context, List<Itemmodel> itemlist, String etype, String frag, MyApplication app, SaveCallback saveCallback, List<Issuemode> issueitems) {
            mContext = context;
            this.mItemList = itemlist;
            this.mEType = etype;
            this.mFrag = frag;
            mapp = app;
            this.callback = saveCallback;
            this.issueitem = issueitems;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Saving items...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(mItemList.size());
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<Itemmodel> doInBackground(Void... voids) {
            List<Itemmodel> failedItems = new ArrayList<>();
            EntryDatabase entryDatabase = new EntryDatabase(mContext);
            SQLiteDatabase db = entryDatabase.getWritableDatabase();
//            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            //  saveAllItem(mItemList);

            try {
                db.beginTransaction(); // Start transaction
                int index = 0;

                Log.e("totaldeleteatdb4", "count  " + mItemList.size() + "  " + mEType);
                List<Itemmodel> localItemList = new ArrayList<>(mItemList);
                if (mFrag.equalsIgnoreCase("product")) {


                    String CREATE_CATEGORY_TABLE =
                            "CREATE TABLE IF NOT EXISTS " + CATTABLE + " (" +
                                    C_CATEGORY + " TEXT, " +
                                    C_PRODUCT + " TEXT" +
                                    ")";

                    db.execSQL(CREATE_CATEGORY_TABLE);
                    String createRatesTableQuery = "CREATE TABLE IF NOT EXISTS " + PROTABLE + "("
                            + C_CATEGORY + " TEXT,"
                            + C_PRODUCT + " TEXT,"
                            + "PRIMARY KEY (" + C_CATEGORY + ", " + C_PRODUCT + ")"
                            + ")";
                    db.execSQL(createRatesTableQuery);
                    String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + BOXTABLE + "("
                            + C_BOX + " TEXT"
                            + ")";
                    db.execSQL(CREATE_TABLE);

                    String CREATE_COUNTER_TABLE = "CREATE TABLE IF NOT EXISTS " + COUNTER_TABLE + "("
                            + COUNTER_NAME + " TEXT,"
                            + C_CATEGORY + " TEXT,"
                            + C_PRODUCT + " TEXT,"
                            + COUNTER_NAME + " TEXT"
                            + ")";
                    db.execSQL(CREATE_COUNTER_TABLE);

//                    for(Itemmodel item : mItemList){
                    for (Itemmodel item : new ArrayList<>(mItemList)) {
                        Cursor cursor = null;

                        cursor = db.query(
                                CATTABLE,                            // Table name
                                null,                                // All columns
                                C_CATEGORY + " COLLATE NOCASE = ?", // Selection
                                new String[]{item.getCategory().toLowerCase(Locale.ROOT)}, // SelectionArgs
                                null,                                // GroupBy
                                null,                                // Having
                                null                                 // OrderBy

                        );
                        boolean exists1 = cursor != null && cursor.getCount() > 0;
                        if (cursor != null) {
                            cursor.close();
                        }
                        if (!exists1) {

                            Log.e("countername", "count  " + mItemList.size() + "  " + item.getCategory());

                            Log.d("check catcat", "" + item.getCategory());
                            ContentValues values = new ContentValues();
                            values.put(C_CATEGORY, item.getCategory());

                            // Insert the new row, returning the primary key value of the new row
                            db.insert(CATTABLE, null, values);
                        }
                        //adding product
                        cursor = db.query(
                                PROTABLE,
                                null, // Select all columns
                                C_CATEGORY + " COLLATE NOCASE = ? AND " + C_PRODUCT + " COLLATE NOCASE = ?",
                                new String[]{item.getCategory().toLowerCase(Locale.ROOT), item.getProduct().toLowerCase(Locale.ROOT)},
                                null, // No group by
                                null, // No having clause
                                null  // No order by
                        );

                        boolean exists = cursor != null && cursor.getCount() > 0;
                        if (cursor != null) {
                            cursor.close();
                        }

                        if (!exists) {
                            // Combination does not exist, insert the new rate
                            ContentValues pvalues = new ContentValues();
                            pvalues.put(C_CATEGORY, item.getCategory());
                            pvalues.put(C_PRODUCT, item.getProduct());

                            long id = db.insert(PROTABLE, null, pvalues);
                        }
                        //adding box
                        if (item.getBox() != null && !item.getBox().isEmpty()) {
                            if (!boxExists(db, item.getBox())) {
                                ContentValues values1 = new ContentValues();
                                values1.put(C_BOX, item.getBox());

                                db.insert(BOXTABLE, null, values1);
                            }
                        }

                        //adding counter
                        if (item.getCounterName() != null && !item.getCounterName().isEmpty()) {
                            Log.e("countername", "count  " + mItemList.size() + "  " + item.getCounterName());

                            if (!counterExist(db, item.getCounterName())) {
                                ContentValues values1 = new ContentValues();
                                values1.put(COUNTER_NAME, item.getCounterName());
                                values1.put(C_CATEGORY, item.getCategory());
                                values1.put(C_PRODUCT, item.getProduct());
                                values1.put(C_BOX, item.getBox());

                                db.insert(COUNTER_TABLE, null, values1);
                                Log.d("check catcat", "Inserted Counter: " + "COUNTER");
                            }
                        }
                    }

                    Log.e("totaldeleteatdb3", "count  " + mItemList.size() + "  " + mEType);
                    if (mEType.equalsIgnoreCase("adding")) {

                        /*for(Itemmodel item : mItemList){
                            Cursor cursor = null;

                            cursor = db.query(
                                    CATTABLE,                            // Table name
                                    null,                                // All columns
                                    C_CATEGORY + " COLLATE NOCASE = ?", // Selection
                                    new String[]{item.getCategory().toLowerCase(Locale.ROOT)}, // SelectionArgs
                                    null,                                // GroupBy
                                    null,                                // Having
                                    null                                 // OrderBy

                            );
                            boolean exists1 = cursor != null && cursor.getCount() > 0;
                            if (cursor != null) {
                                cursor.close();
                            }
                            if (!exists1) {


                                Log.d("check catcat", "" + item.getCategory());
                                ContentValues values = new ContentValues();
                                values.put(C_CATEGORY, item.getCategory());

                                // Insert the new row, returning the primary key value of the new row
                                db.insert(CATTABLE, null, values);
                            }
                            //adding product
                            cursor = db.query(
                                    PROTABLE,
                                    null, // Select all columns
                                    C_CATEGORY + " COLLATE NOCASE = ? AND " + C_PRODUCT + " COLLATE NOCASE = ?",
                                    new String[]{item.getCategory().toLowerCase(Locale.ROOT), item.getProduct().toLowerCase(Locale.ROOT)},
                                    null, // No group by
                                    null, // No having clause
                                    null  // No order by
                            );

                            boolean exists = cursor != null && cursor.getCount() > 0;
                            if (cursor != null) {
                                cursor.close();
                            }

                            if (!exists) {
                                // Combination does not exist, insert the new rate
                                ContentValues pvalues = new ContentValues();
                                pvalues.put(C_CATEGORY, item.getCategory());
                                pvalues.put(C_PRODUCT, item.getProduct());

                                long id = db.insert(PROTABLE, null, pvalues);
                            }
                            //adding box
                            if (item.getBox() != null && !item.getBox().isEmpty()) {
                                if (!boxExists(db, item.getBox())) {
                                    ContentValues values1 = new ContentValues();
                                    values1.put(C_BOX, item.getBox());

                                    db.insert(BOXTABLE, null, values1);
                                }
                            }
                        }*/

                        saveAllItem(mItemList);


                        for (Itemmodel item : mItemList) {

                            ContentValues values = new ContentValues();

                            // Get all fields in the inventorymodel class
                            Field[] fields = Itemmodel.class.getDeclaredFields();

                            // Iterate through the fields and add data to ContentValues
                            for (Field field : fields) {
                                try {
                                    field.setAccessible(true);
                                    String columnName = field.getName();
                                    Object value = field.get(item);
                                    if (value instanceof Long) {
                                        values.put(columnName, (Long) value);
//                                    dvalue.put(columnName, (Long) dval);
                                    } else if (value instanceof String) {
                                        values.put(columnName, (String) value);
//                                    dvalue.put(columnName, (String) dval);
                                    } else if (value instanceof Double) {
                                        values.put(columnName, (Double) value);
//                                    dvalue.put(columnName, (Double) dval);
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            long result1 = db.insert(All_SAVE_TABLE, null, values);
                            long result = db.insert(ALL_TABLE, null, values);
//                            long result1 = db.insert(D_TABLE, null, values);
                            if (result == -1) {
                                failedItems.add(item); // Add item to the list of failed items
                            } else {
                                publishProgress(index + 1);
                                mapp.putitem(item);
                            }
                            index++;
                        }
                        db.setTransactionSuccessful();
                    }
                    if (mEType.equalsIgnoreCase("excel")) {

                        for (Itemmodel item : mItemList) {
                            String whereClause = "TidValue = ?";
                            String[] whereArgs = {item.getTidValue()};
                            Cursor cursor1 = null;

                            try {
                                cursor1 = db.query(ALL_TABLE, null, whereClause, whereArgs, null, null, null);
                                ContentValues values = new ContentValues();
                                ContentValues dvalue = new ContentValues();

                                Field[] fields = Itemmodel.class.getDeclaredFields();
                                for (Field field : fields) {
                                    try {
                                        field.setAccessible(true);
                                        String columnName = field.getName();
//                                            Log.d("importdata1", ""+field.getType());
                                        Object value = field.get(item);
                                        Object dval = field.get(item);
                                        if (!columnName.equals("id")) {
                                            if (value instanceof Long) {
                                                values.put(columnName, (Long) value);
                                                dvalue.put(columnName, (Long) dval);
                                            } else if (value instanceof String) {
                                                values.put(columnName, (String) value);
                                                dvalue.put(columnName, (String) dval);
                                            } else if (value instanceof Double) {
                                                values.put(columnName, (Double) value);
                                                dvalue.put(columnName, (Double) dval);
                                            }
                                        }

                                    } catch (Exception e) {
                                    }

                                }
                                if (cursor1 != null && cursor1.moveToFirst()) {
                                    // Item exists, update it
                                    // Handle update operation
                                    db.update(ALL_TABLE, values, whereClause, whereArgs);
                                    db.insert(D_TABLE, null, dvalue);
                                    mapp.putitem(item);

                                } else {
                                    // Item does not exist, add it
                                    // Handle add operation
                                    db.insert(ALL_TABLE, null, values);
                                    db.insert(D_TABLE, null, dvalue);
                                    mapp.putitem(item);
                                }
                                publishProgress(index + 1);
                                index++;
                            } catch (Exception e) {
                                Issuemode is = new Issuemode();
                                is.setIssue("failed at db" + e.getMessage());
                                issueitem.add(is);
                            } finally {
                                if (cursor1 != null) {
                                    cursor1.close();
                                }
                            }

                        }
                        db.setTransactionSuccessful();

                    }
                    if (mEType.equalsIgnoreCase("delete")) {


                        int totalDeleted = 0;

                        List<Itemmodel> copyOfItemList = new ArrayList<>(localItemList);

                        Log.e("totaldeleteatdb1", "count  " + copyOfItemList.size() + "   " + mItemList.size());
                        for (Itemmodel item : copyOfItemList) {
//                            String whereClause = "TidValue = ?";
//                            String[] whereArgs = {item.getTidValue()};

                            String whereClause = "TidValue = ? OR ItemCode = ?";
                            String[] whereArgs = {item.getTidValue(), item.getItemCode()};


                            // Delete the item from ALL_TABLE
                            int rowsDeleted = db.delete(ALL_TABLE, whereClause, whereArgs);
                            Log.e("totaldeleteatdb", "count  " + rowsDeleted + "  " + item.getTidValue());
                            if (rowsDeleted > 0) {
                                mapp.removeite(item);
                                totalDeleted++;
                                ContentValues values = new ContentValues();
                                ContentValues dvalue = new ContentValues();
                                Field[] fields = Itemmodel.class.getDeclaredFields();
                                for (Field field : fields) {
                                    try {
                                        field.setAccessible(true);
                                        String columnName = field.getName();
//                                            Log.d("importdata1", ""+field.getType());
                                        Object value = field.get(item);
                                        Object dval = field.get(item);
                                        if (!columnName.equals("id")) {
                                            if (columnName.equalsIgnoreCase("operation")) {
                                                value = "delete";
                                                dval = "delete";
                                            }
                                            if (value instanceof Long) {
                                                values.put(columnName, (Long) value);
                                                dvalue.put(columnName, (Long) dval);
                                            } else if (value instanceof String) {
                                                values.put(columnName, (String) value);
                                                dvalue.put(columnName, (String) dval);
                                            } else if (value instanceof Double) {
                                                values.put(columnName, (Double) value);
                                                dvalue.put(columnName, (Double) dval);
                                            }
                                        }

                                    } catch (Exception e) {
                                        Log.d("deleteexp", "exp " + e.getMessage());
                                    }

                                }
                                long result = db.insert(D_TABLE, null, values);
                                if (result == -1) {
//                                         Insertion failed, handle error if needed
                                }
                                publishProgress(index + 1);
                                index++;

                                // Optionally, you can perform additional actions here after successful deletion
                            }
                        }

                        Log.d("totalDeleted", "Count: " + totalDeleted);

                        // Mark the transaction as successful if all deletions were successful
                        db.setTransactionSuccessful();

                    }
                    if (mEType.equalsIgnoreCase("productdemo")) {
                        for (Itemmodel item : mItemList) {
                            ContentValues values = new ContentValues();

                            // Get all fields in the Itemmodel class
                            Field[] fields = Itemmodel.class.getDeclaredFields();

                            for (Field field : fields) {
                                try {
                                    field.setAccessible(true);
                                    String columnName = field.getName();
                                    Object value = field.get(item);
                                    if (value instanceof Long) {
                                        values.put(columnName, (Long) value);
                                    } else if (value instanceof String) {
                                        values.put(columnName, (String) value);
                                    } else if (value instanceof Double) {
                                        values.put(columnName, (Double) value);
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Check if an item with the same JobId and TidValue exists in ALL_TABLE
                            Cursor cursor = db.rawQuery("SELECT * FROM " + ALL_TABLE + " WHERE Product = ? AND TidValue = ?",
                                    new String[]{item.getProduct(), item.getTidValue()});

                            if (cursor.moveToFirst()) {
                                // If the item exists, insert it into D_TABLE before deletion
                                db.insert(D_TABLE, null, values);

                                // Delete the existing item from ALL_TABLE
                                db.delete(ALL_TABLE, "Product = ? AND TidValue = ?",
                                        new String[]{item.getProduct(), item.getTidValue()});
                            }
                            cursor.close();

                            // Insert the new item into ALL_TABLE
                            long result = db.insert(ALL_TABLE, null, values);

                            if (result == -1) {
                                failedItems.add(item);
                            } else {
                                publishProgress(index + 1);
                                mapp.putitem(item);
                            }
                            index++;
                        }
                        db.setTransactionSuccessful();
                    }

                    if (mEType.equalsIgnoreCase("productdemo1")) {
                        for (Itemmodel item : mItemList) {

                            ContentValues values = new ContentValues();

                            // Get all fields in the inventorymodel class
                            Field[] fields = Itemmodel.class.getDeclaredFields();

                            // Iterate through the fields and add data to ContentValues
                            for (Field field : fields) {
                                try {
                                    field.setAccessible(true);
                                    String columnName = field.getName();
                                    Object value = field.get(item);
                                    if (value instanceof Long) {
                                        values.put(columnName, (Long) value);
//                                    dvalue.put(columnName, (Long) dval);
                                    } else if (value instanceof String) {
                                        values.put(columnName, (String) value);
//                                    dvalue.put(columnName, (String) dval);
                                    } else if (value instanceof Double) {
                                        values.put(columnName, (Double) value);
//                                    dvalue.put(columnName, (Double) dval);
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            long result = db.insert(ALL_TABLE, null, values);
//                            long result1 = db.insert(D_TABLE, null, values);
                            if (result == -1) {
                                failedItems.add(item); // Add item to the list of failed items
                            } else {
                                publishProgress(index + 1);
                                mapp.putitem(item);
                            }
                            index++;
                        }
                        db.setTransactionSuccessful();
                    }

                }

                if (mFrag.equalsIgnoreCase("remap")) {
                    if (mEType.equalsIgnoreCase("adding")) {
                        for (Itemmodel item : mItemList) {
                            ContentValues values = new ContentValues();
                            // Get all fields in the inventorymodel class
                            Field[] fields = Itemmodel.class.getDeclaredFields();

                            // Iterate through the fields and add data to ContentValues
                            for (Field field : fields) {
                                try {
                                    field.setAccessible(true);
                                    String columnName = field.getName();
                                    Object value = field.get(item);
                                    if (!columnName.equals("id")) {
                                        if (value instanceof Long) {
                                            values.put(columnName, (Long) value);
//                                    dvalue.put(columnName, (Long) dval);
                                        } else if (value instanceof String) {
                                            values.put(columnName, (String) value);
//                                    dvalue.put(columnName, (String) dval);
                                        } else if (value instanceof Double) {
                                            values.put(columnName, (Double) value);
//                                    dvalue.put(columnName, (Double) dval);
                                        }
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            long result = db.insertWithOnConflict(R_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                            long result = db.insert(R_TABLE, null, values);

                            if (result == -1) {
                                failedItems.add(item); // Add item to the list of failed items
                            } else {
                                publishProgress(index + 1);
//                                mapp.putitem(item);
                            }
                            index++;
                        }
                        db.setTransactionSuccessful();
                    }
                }

                if (mFrag.equalsIgnoreCase("inventory")) {

                    long time = System.currentTimeMillis();
                    for (Itemmodel item : mItemList) {

                        ContentValues values = new ContentValues();

                        // Get all fields in the inventorymodel class
                        Field[] fields = Itemmodel.class.getDeclaredFields();

                        // Iterate through the fields and add data to ContentValues
                        for (Field field : fields) {
                            try {
                                field.setAccessible(true);
                                String columnName = field.getName();
                                Object value = field.get(item);
                                if (!columnName.equals("id")) {
                                    if (columnName.equalsIgnoreCase("operation")) {
                                        if (item.getAvlQty() == item.getMatchQty()) {
                                            value = "found";
                                        } else {
                                            value = "not found";
                                        }
                                    }
                                    if (columnName.equalsIgnoreCase("operationtime")) {
                                        value = time;
                                    }

                                    if (value instanceof Long) {
                                        values.put(columnName, (Long) value);
//                                    dvalue.put(columnName, (Long) dval);
                                    } else if (value instanceof String) {
                                        values.put(columnName, (String) value);
//                                    dvalue.put(columnName, (String) dval);
                                    } else if (value instanceof Double) {
                                        values.put(columnName, (Double) value);
//                                    dvalue.put(columnName, (Double) dval);
                                    }
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
//                        long result = db.insert(ALL_TABLE, null, values);
                        long result1 = db.insert(D_TABLE, null, values);
                        if (result1 == -1) {
                            failedItems.add(item); // Add item to the list of failed items
                        } else {
                            publishProgress(index + 1);
//                            mapp.putitem(item);
                        }
                        index++;
                    }
                    db.setTransactionSuccessful();
                }

                if (mFrag.equalsIgnoreCase("bill")) {
                    Log.e("check frag", "  " + mEType);
                    if (mEType.equalsIgnoreCase("Reserved") || mEType.equalsIgnoreCase("order") || mEType.equalsIgnoreCase("order estimation")) {
                        for (Itemmodel item : mItemList) {
//                        String whereClause = "InvoiceNumber = ?";
//                        String[] whereArgs = {item.getInvoiceNumber()};
//                        Cursor cursor1 = null;

                            try {
//                            cursor1 = db.query(T_TABLE, null, whereClause, whereArgs, null, null, null);
                                ContentValues values = new ContentValues();
                                ContentValues dvalue = new ContentValues();

                                Field[] fields = Itemmodel.class.getDeclaredFields();
                                for (Field field : fields) {
                                    try {
                                        field.setAccessible(true);
                                        String columnName = field.getName();
//                                            Log.d("importdata1", ""+field.getType());
                                        Object value = field.get(item);
                                        Object dval = field.get(item);
                                        if (!columnName.equals("id")) {
                                            if (value instanceof Long) {
                                                values.put(columnName, (Long) value);
                                                dvalue.put(columnName, (Long) dval);
                                            } else if (value instanceof String) {
                                                values.put(columnName, (String) value);
                                                dvalue.put(columnName, (String) dval);
                                            } else if (value instanceof Double) {
                                                values.put(columnName, (Double) value);
                                                dvalue.put(columnName, (Double) dval);
                                            }
                                        }

                                    } catch (Exception e) {
                                        Log.e("entrydatabasebill", "check" + e.getMessage().toString());
                                    }

                                }
//                            if (cursor1 != null && cursor1.moveToFirst()) {
//                                // Item exists, update it
//                                // Handle update operation
//                                db.update(T_TABLE, values, whereClause, whereArgs);
                                db.insert(D_TABLE, null, dvalue);
////                                mapp.putitem(item);
//
//                            } else {
                                long result = db.insert(T_TABLE, null, dvalue);

//                            }

                                publishProgress(index + 1);
                                index++;
                            } finally {
//                            if (cursor1 != null) {
//                                cursor1.close();
//                            }
                            }

                        }
                        db.setTransactionSuccessful();
                    } else {
                        for (Itemmodel item : mItemList) {
//                        String whereClause = "TidValue = ?";
//                        String[] whereArgs = {item.getTidValue()};
//                        Cursor cursor1 = null;

                            try {
//                            cursor1 = db.query(ALL_TABLE, null, whereClause, whereArgs, null, null, null);
                                ContentValues values = new ContentValues();
                                ContentValues dvalue = new ContentValues();

                                Field[] fields = Itemmodel.class.getDeclaredFields();
                                for (Field field : fields) {
                                    try {
                                        field.setAccessible(true);
                                        String columnName = field.getName();
//                                            Log.d("importdata1", ""+field.getType());
                                        Object value = field.get(item);
                                        Object dval = field.get(item);
                                        if (!columnName.equals("id")) {
                                            if (value instanceof Long) {
                                                values.put(columnName, (Long) value);
                                                dvalue.put(columnName, (Long) dval);
                                            } else if (value instanceof String) {
                                                values.put(columnName, (String) value);
                                                dvalue.put(columnName, (String) dval);
                                            } else if (value instanceof Double) {
                                                values.put(columnName, (Double) value);
                                                dvalue.put(columnName, (Double) dval);
                                            }
                                        }

                                    } catch (Exception e) {
                                    }

                                }
//                            if (cursor1 != null && cursor1.moveToFirst()) {
//                                // Item exists, update it
//                                // Handle update operation
//                                db.update(ALL_TABLE, values, whereClause, whereArgs);
//                                db.insert(D_TABLE, null, dvalue);
//                                mapp.putitem(item);
//
//                            } else {
                                // Item does not exist, add it
                                // Handle add operation
//                                db.insert(ALL_TABLE, null, values);
                                long result = db.insert(T_TABLE, null, dvalue);
                                if (result == -1) {
//                                         Insertion failed, handle error if needed

                                } else {
                                    long result1 = db.insert(D_TABLE, null, dvalue);
                                    String whereClause = "TidValue = ?";
                                    String[] whereArgs = {item.getTidValue()};

                                    // Delete the item from ALL_TABLE
                                    int rowsDeleted = db.delete(ALL_TABLE, whereClause, whereArgs);

                                    Log.d("totaldelete", "count  " + rowsDeleted + "  " + item.getTidValue());
                                    if (rowsDeleted > 0) {
                                        mapp.removeite(item);
                                    }
                                }

//                            }
                                publishProgress(index + 1);
                                index++;
                            } finally {
//                            if (cursor1 != null) {
//                                cursor1.close();
//                            }
                            }

                        }
                        db.setTransactionSuccessful();
                    }

                }


                if (mFrag.equalsIgnoreCase("billupdate")) {
                    if (mEType.equalsIgnoreCase("Reserved") || mEType.equalsIgnoreCase("order")) {
                        for (Itemmodel item : mItemList) {
//                        String whereClause = "InvoiceNumber = ? AND BarCode = ?";
//                        String[] whereArgs = {item.getInvoiceNumber(), item.getBarCode()};
                            String whereClause = "id = ?";
                            String[] whereArgs = {String.valueOf(item.getId1())};
                            Cursor cursor1 = null;

                            try {
                                cursor1 = db.query(T_TABLE, null, whereClause, whereArgs, null, null, null);
                                ContentValues values = new ContentValues();
                                ContentValues dvalue = new ContentValues();

                                // Populate ContentValues with item fields
                                Field[] fields = Itemmodel.class.getDeclaredFields();
                                for (Field field : fields) {
                                    try {
                                        field.setAccessible(true);
                                        String columnName = field.getName();
                                        Object value = field.get(item);
                                        if (!columnName.equals("id")) {
                                            if (value instanceof Long) {
                                                values.put(columnName, (Long) value);
                                                dvalue.put(columnName, (Long) value);
                                            } else if (value instanceof String) {
                                                values.put(columnName, (String) value);
                                                dvalue.put(columnName, (String) value);
                                            } else if (value instanceof Double) {
                                                values.put(columnName, (Double) value);
                                                dvalue.put(columnName, (Double) value);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("billupdate", "Error accessing field " + field.getName() + ": " + e.getMessage());
                                    }
                                }

                                db.insert(D_TABLE, null, dvalue);

                                if (cursor1 != null && cursor1.moveToFirst()) {
                                    // Item exists, update it
                                    db.update(T_TABLE, values, whereClause, whereArgs);
                                    Log.e("billupdate", "Item exists, updated successfully.");
                                } else {
                                    // Item does not exist, insert new
                                    long result = db.insert(T_TABLE, null, dvalue);
                                    Log.e("billupdate", "Item did not exist, inserted new item with result: " + "result");
                                }

                                publishProgress(index + 1);
                                index++;

                            } finally {
                                if (cursor1 != null) {
                                    cursor1.close(); // Ensure the cursor is always closed
                                }
                            }
                        }
                    } else if (mEType.equalsIgnoreCase("order estimation")) {


                    } else {


                    }


                    // Loop through issueitem to delete items based on InvoiceNumber and TidValue
                    for (Issuemode is : issueitem) {
                        String whereClause = "id = ?";
                        String[] whereArgs = {String.valueOf(is.getId())};

                        Cursor cursor1 = null;
                        try {
                            cursor1 = db.query(T_TABLE, null, whereClause, whereArgs, null, null, null);
                            Log.e("billupdate", "checking list " + issueitem.size() + "  " + is.getInvoiceNumber() + "   " + is.getBarCode());
                            if (cursor1 != null && cursor1.moveToFirst()) {
                                // If the item exists, delete it
                                int rowsDeleted = db.delete(T_TABLE, whereClause, whereArgs);
                                Log.d("delete", "Rows deleted: " + "rowsDeleted");
                            }
                        } finally {
                            if (cursor1 != null) {
                                cursor1.close(); // Ensure the cursor is always closed
                            }
                        }
                    }

                    db.setTransactionSuccessful();


                }

                if (mFrag.equalsIgnoreCase("reserved")) {

                    for (Itemmodel item : mItemList) {

                        String whereClause1 = "TidValue = ?";
                        String[] whereArgs1 = {item.getTidValue()};

                        Cursor cursor1 = null;

                        try {
                            cursor1 = db.query(ALL_TABLE, null, whereClause1, whereArgs1, null, null, null);
                            //                            cursor1 = db.query(ALL_TABLE, null, whereClause, whereArgs, null, null, null);
                            ContentValues values = new ContentValues();
                            ContentValues dvalue = new ContentValues();

                            Field[] fields = Itemmodel.class.getDeclaredFields();
                            for (Field field : fields) {
                                try {
                                    field.setAccessible(true);
                                    String columnName = field.getName();
//                                            Log.d("importdata1", ""+field.getType());
                                    Object value = field.get(item);
                                    Object dval = field.get(item);
                                    if (!columnName.equals("id")) {
                                        if (value instanceof Long) {
                                            values.put(columnName, (Long) value);
                                            dvalue.put(columnName, (Long) dval);
                                        } else if (value instanceof String) {
                                            values.put(columnName, (String) value);
                                            dvalue.put(columnName, (String) dval);
                                        } else if (value instanceof Double) {
                                            values.put(columnName, (Double) value);
                                            dvalue.put(columnName, (Double) dval);
                                        }
                                    }

                                } catch (Exception e) {
                                }

                            }
                            if (cursor1 != null && cursor1.moveToFirst()) {
                                // Item exists, update it
                                // Handle update operation
                                db.update(ALL_TABLE, values, whereClause1, whereArgs1);
                                db.insert(D_TABLE, null, dvalue);
                                mapp.putitem(item);

                            } else {




                            /*


                            long result = db.insert(T_TABLE, null, dvalue);
                            if (result == -1) {
//                                         Insertion failed, handle error if needed

                            }else{
                                long result1 = db.insert(D_TABLE, null, dvalue);
                                String whereClause = "TidValue = ?";
                                String[] whereArgs = {item.getTidValue()};

                                // Delete the item from ALL_TABLE
                                int rowsDeleted = db.delete(ALL_TABLE, whereClause, whereArgs);

                                Log.d("totaldelete", "count  " + rowsDeleted + "  " + item.getTidValue());
                                if (rowsDeleted > 0) {
                                    mapp.removeite(item);
                                }
                            }*/
                                db.insert(ALL_TABLE, null, values);
                                db.insert(D_TABLE, null, dvalue);
                                mapp.putitem(item);
                            }
                            publishProgress(index + 1);
                            index++;
                        } finally {
                            if (cursor1 != null) {
                                cursor1.close();
                            }
                        }


                    }
                    db.setTransactionSuccessful();

                }


                Log.e("check failed1", " ");
                for (Issuemode is : issueitem) {
                    ContentValues values = new ContentValues();

                    // Get all fields in the inventorymodel class
                    Field[] fields = Issuemode.class.getDeclaredFields();

                    // Iterate through the fields and add data to ContentValues
                    for (Field field : fields) {
                        try {
                            field.setAccessible(true);
                            String columnName = field.getName();
                            Object value = field.get(is);
                            if (!columnName.equals("id")) {
                                if (value instanceof Long) {
                                    values.put(columnName, (Long) value);
//                                    dvalue.put(columnName, (Long) dval);
                                } else if (value instanceof String) {
                                    values.put(columnName, (String) value);
//                                    dvalue.put(columnName, (String) dval);
                                } else if (value instanceof Double) {
                                    values.put(columnName, (Double) value);
//                                    dvalue.put(columnName, (Double) dval);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    long result = db.insert(I_TABLE, null, values);
//                    long result1 = db.insert(D_TABLE, null, values);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("check failed2", " ");

                for (Issuemode is : issueitem) {
                    ContentValues values = new ContentValues();

                    // Get all fields in the inventorymodel class
                    Field[] fields = Issuemode.class.getDeclaredFields();

                    // Iterate through the fields and add data to ContentValues
                    for (Field field : fields) {
                        try {
                            field.setAccessible(true);
                            String columnName = field.getName();
                            Object value = field.get(is);
                            if (!columnName.equals("id")) {
                                if (value instanceof Long) {
                                    values.put(columnName, (Long) value);
//                                    dvalue.put(columnName, (Long) dval);
                                } else if (value instanceof String) {
                                    values.put(columnName, (String) value);
//                                    dvalue.put(columnName, (String) dval);
                                } else if (value instanceof Double) {
                                    values.put(columnName, (Double) value);
//                                    dvalue.put(columnName, (Double) dval);
                                }
                            }
                        } catch (IllegalAccessException e1) {
                            e.printStackTrace();
                        }
                    }
                    long result = db.insert(I_TABLE, null, values);
//                    long result1 = db.insert(D_TABLE, null, values);
                }


                return failedItems;
            } finally {
                db.endTransaction();
                entryDatabase.close();
            }

            return failedItems; // Return the total count of successfully inserted items
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<Itemmodel> failedItems) {
            super.onPostExecute(failedItems);
            progressDialog.dismiss();
            if (!failedItems.isEmpty()) {
                callback.onSaveFailure(failedItems);
//                Toast.makeText(mContext, "Some items failed to add", Toast.LENGTH_SHORT).show();
//                mItemList.clear();
//                mItemList.addAll(failedItems);
//                if(mEType.matches("badding")) {
//                    mproductAdapter.notifyDataSetChanged();
//                }
            } else {
                callback.onSaveSuccess();
//                Toast.makeText(mContext, "Item saved", Toast.LENGTH_SHORT).show();
//                mItemList.clear();
//
//                if(mEType.matches("badding")) {
//                    mproductAdapter.notifyDataSetChanged();
//                }
            }
        }
    }


    // Method to check if a category exists
    private boolean categoryExists(SQLiteDatabase db, String category) {
        Cursor cursor = null;
        try {
            cursor = db.query(CATTABLE, null, C_CATEGORY + " COLLATE NOCASE = ?",
                    new String[]{category.toLowerCase(Locale.ROOT)},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Method to check if a product exists
    private boolean productExists(SQLiteDatabase db, String category, String product) {
        Cursor cursor = null;
        try {
            cursor = db.query(PROTABLE, null,
                    C_CATEGORY + " COLLATE NOCASE = ? AND " + C_PRODUCT + " COLLATE NOCASE = ?",
                    new String[]{category.toLowerCase(Locale.ROOT), product.toLowerCase(Locale.ROOT)},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Method to check if a box exists
    private boolean boxExists(SQLiteDatabase db, String box) {
        Cursor cursor = null;
        try {
            cursor = db.query(BOXTABLE, null, C_BOX + " COLLATE NOCASE = ?",
                    new String[]{box.toLowerCase(Locale.ROOT)},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean counterExist(SQLiteDatabase db, String counter) {
        Cursor cursor = null;
        try {
            cursor = db.query(COUNTER_TABLE, null, COUNTER_NAME + " COLLATE NOCASE = ?",
                    new String[]{counter.toLowerCase(Locale.ROOT)},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public void addskus(List<SkuResponse> result, Context context) {
        new SaveSkusTask(context).execute(result);
    }

    public class SaveSkusTask extends AsyncTask<List<SkuResponse>, Void, Void> {
        private Context context;

        // Constructor to initialize context
        public SaveSkusTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(List<SkuResponse>... lists) {
            List<SkuResponse> skuResponses = lists[0];
            EntryDatabase entryDatabase = new EntryDatabase(context);
            SQLiteDatabase database = entryDatabase.getWritableDatabase();

            database.beginTransaction();
            try {
                for (int i = 0; i < skuResponses.size(); i++) {
                    SkuResponse sku = skuResponses.get(i);
                    Cursor cursor = null;
                    try {
                        cursor = database.query(SKU_TABLE, null, "id1 = ?",
                                new String[]{String.valueOf(sku.id1)}, null, null, null);

                        ContentValues values = new ContentValues();
                        Field[] fields = SkuResponse.class.getDeclaredFields();
                        for (Field field : fields) {
                            try {
                                field.setAccessible(true);
                                String columnName = field.getName();
                                Object value = field.get(sku);

                                if (value instanceof Long) {
                                    values.put(columnName, (Long) value);
                                } else if (value instanceof Integer) {
                                    values.put(columnName, (Integer) value);
                                } else if (value instanceof String) {
                                    values.put(columnName, (String) value);
                                } else if (value instanceof Double) {
                                    values.put(columnName, (Double) value);
                                }
                                /*if(columnName.equals("id1")){
                                    values.put(columnName,  sku.id1);
                                }*/
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        if (cursor.moveToFirst()) {
                            // Update
                            database.update(SKU_TABLE, values, "id1 = ?",
                                    new String[]{String.valueOf(sku.id1)});
                        } else {
                            // Insert
                            database.insert(SKU_TABLE, null, values);
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }

                    // Insert/Update SKUVendor
                    insertOrUpdateVendor(database, sku);

                    // Insert/Update SKUStoneMain and SKUStoneItem
                    insertOrUpdateStones(database, sku);

                    if ((i + 1) % BATCH_SIZE == 0) {
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        database.beginTransaction();
                    }
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.close(); // Close the database when done
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Notify the UI thread that the operation has completed
        }
    }


    // Helper function to handle SKUVendor
    private void insertOrUpdateVendor(SQLiteDatabase database, SkuResponse sku) {
        for (SkuResponse.SKUVendor vendor : sku.sKUVendor) {
            Cursor vendorCursor = null;
            try {
                vendorCursor = database.query(VENDOR_TABLE, null, "SKUVendorId = ? AND id1 = ?",
                        new String[]{String.valueOf(vendor.sKUVendorId), String.valueOf(sku.id1)}, null, null, null);

                ContentValues vendorValues = new ContentValues();
                Field[] vendorFields = SkuResponse.SKUVendor.class.getDeclaredFields();
                for (Field field : vendorFields) {
                    field.setAccessible(true);
                    String columnName = field.getName();
                    Object value = field.get(vendor);
                    if (value instanceof Long) {
                        vendorValues.put(columnName, (Long) value);
                    } else if (value instanceof Integer) {
                        vendorValues.put(columnName, (Integer) value);
                    } else if (value instanceof String) {
                        vendorValues.put(columnName, (String) value);
                    } else if (value instanceof Double) {
                        vendorValues.put(columnName, (Double) value);
                    }
                }

                if (vendorCursor.moveToFirst()) {
                    // Update SKUVendor
                    database.update(VENDOR_TABLE, vendorValues, "SKUVendorId = ? AND id1 = ?",
                            new String[]{String.valueOf(vendor.sKUVendorId), String.valueOf(sku.id1)});
                } else {
                    // Insert SKUVendor
                    database.insert(VENDOR_TABLE, null, vendorValues);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                if (vendorCursor != null) {
                    vendorCursor.close();
                }
            }
        }
    }

    // Helper function to handle SKUStoneMain and SKUStoneItem
    private void insertOrUpdateStones(SQLiteDatabase database, SkuResponse sku) {
        for (SKUStoneMain stoneMain : sku.sKUStoneMain) {
            Cursor stoneMainCursor = null;
            try {
                stoneMainCursor = database.query(SKU_STONE, null, "Id = ? AND SKUId = ?",
                        new String[]{String.valueOf(stoneMain.id1), String.valueOf(sku.id1)}, null, null, null);

                ContentValues stoneMainValues = new ContentValues();
                Field[] stoneMainFields = SKUStoneMain.class.getDeclaredFields();
                for (Field field : stoneMainFields) {
                    field.setAccessible(true);
                    String columnName = field.getName();
                    Object value = field.get(stoneMain);
                    if (value instanceof Long) {
                        stoneMainValues.put(columnName, (Long) value);
                    } else if (value instanceof Integer) {
                        stoneMainValues.put(columnName, (Integer) value);
                    } else if (value instanceof String) {
                        stoneMainValues.put(columnName, (String) value);
                    } else if (value instanceof Double) {
                        stoneMainValues.put(columnName, (Double) value);
                    }
                }

                if (stoneMainCursor.moveToFirst()) {
                    // Update SKUStoneMain
                    database.update(SKU_STONE, stoneMainValues, "Id1 = ? AND SKUId = ?",
                            new String[]{String.valueOf(stoneMain.id1), String.valueOf(sku.id1)});
                } else {
                    // Insert SKUStoneMain
                    database.insert(SKU_STONE, null, stoneMainValues);
                }

                // Handle SKUStoneItem
                for (SKUStoneItem stoneItem : stoneMain.sKUStoneItem) {
                    Cursor stoneItemCursor = null;
                    try {
                        stoneItemCursor = database.query(STONE_ITEMDETAILS, null, "Id1 = ? AND SKUStoneMainId = ?",
                                new String[]{String.valueOf(stoneItem.id1), String.valueOf(stoneMain.id1)}, null, null, null);

                        ContentValues stoneItemValues = new ContentValues();
                        Field[] stoneItemFields = SKUStoneItem.class.getDeclaredFields();
                        for (Field field : stoneItemFields) {
                            field.setAccessible(true);
                            String columnName = field.getName();
                            Object value = field.get(stoneItem);
                            if (value instanceof Long) {
                                stoneItemValues.put(columnName, (Long) value);
                            } else if (value instanceof Integer) {
                                stoneItemValues.put(columnName, (Integer) value);
                            } else if (value instanceof String) {
                                stoneItemValues.put(columnName, (String) value);
                            } else if (value instanceof Double) {
                                stoneItemValues.put(columnName, (Double) value);
                            }
                        }

                        if (stoneItemCursor.moveToFirst()) {
                            // Update SKUStoneItem
                            database.update(STONE_ITEMDETAILS, stoneItemValues, "Id1 = ? AND SKUStoneMainId = ?",
                                    new String[]{String.valueOf(stoneItem.id1), String.valueOf(stoneMain.id1)});
                        } else {
                            // Insert SKUStoneItem
                            database.insert(STONE_ITEMDETAILS, null, stoneItemValues);
                        }
                    } finally {
                        if (stoneItemCursor != null) {
                            stoneItemCursor.close();
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                if (stoneMainCursor != null) {
                    stoneMainCursor.close();
                }
            }
        }
    }


    public List<SkuResponse> readSkusInBatches(Context context, int offset, int limit) {
        SQLiteDatabase database = null;
        Cursor skuCursor = null;
        Cursor vendorCursor = null;
        List<SkuResponse> skuList = new ArrayList<>();

        try {
            EntryDatabase entryDatabase = new EntryDatabase(context);
            database = entryDatabase.getReadableDatabase();

            String query = "SELECT * FROM " + SKU_TABLE + " LIMIT ? OFFSET ?";
            skuCursor = database.rawQuery(query, new String[]{String.valueOf(limit), String.valueOf(offset)});

            while (skuCursor.moveToNext()) {
                SkuResponse sku = getSkuItemFromCursor(skuCursor);
                sku.sKUVendor = new ArrayList<>();

                try {
                    vendorCursor = database.query(VENDOR_TABLE, null, "id1 = ?", new String[]{String.valueOf(sku.id1)}, null, null, null);
                    while (vendorCursor.moveToNext()) {
                        SkuResponse.SKUVendor vendor = getSkuVendorItemFromCursor(vendorCursor);
                        sku.sKUVendor.add(vendor);
                    }
                } finally {
                    if (vendorCursor != null) {
                        vendorCursor.close();
                    }
                }

                skuList.add(sku);
            }
        } finally {
            if (skuCursor != null) {
                skuCursor.close();
            }
            if (database != null) {
                database.close(); // Close the database connection properly
            }
        }

        return skuList;
    }


    private SkuResponse getSkuItemFromCursor(Cursor cursor) {
        SkuResponse item = new SkuResponse();
        Field[] fields = SkuResponse.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String columnName = field.getName();
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex != -1) {
                    Class<?> fieldType = field.getType();
                    if (fieldType == String.class) {
                        field.set(item, cursor.getString(columnIndex));
                    } else if (fieldType == int.class) {
                        field.set(item, cursor.getInt(columnIndex));
                    } else if (fieldType == long.class) {
                        field.setLong(item, cursor.getLong(columnIndex));
                    } else if (fieldType == double.class) {
                        field.setDouble(item, cursor.getDouble(columnIndex));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
//        int idIndex = cursor.getColumnIndex("id");
//        if (idIndex != -1) {
//            item.setId(cursor.getLong(idIndex));
//        }
        return item;
    }

    private SkuResponse.SKUVendor getSkuVendorItemFromCursor(Cursor cursor) {
        SkuResponse.SKUVendor item = new SkuResponse.SKUVendor();
        Field[] fields = SkuResponse.SKUVendor.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String columnName = field.getName();
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex != -1) {
                    Class<?> fieldType = field.getType();
                    if (fieldType == String.class) {
                        field.set(item, cursor.getString(columnIndex));
                    } else if (fieldType == int.class) {
                        field.set(item, cursor.getInt(columnIndex));
                    } else if (fieldType == long.class) {
                        field.setLong(item, cursor.getLong(columnIndex));
                    } else if (fieldType == double.class) {
                        field.setDouble(item, cursor.getDouble(columnIndex));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return item;
    }


    public HashMap<String, Itemmodel> loadInventoryItems(Context context, MyApplication app, int count) {
        EntryDatabase db = new EntryDatabase(context);
        HashMap<String, Itemmodel> inventoryMap = new HashMap<>();
//        int totalItems = items.size();
        int processedItems = 0; // To track processed items


        try (SQLiteDatabase sqLiteDatabase = db.getReadableDatabase()) {

            String query = "SELECT * FROM " + ALL_TABLE;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Itemmodel item = getItemFromCursor(cursor);
//                    Log.d("entrydatabase", "check read " + item.toString());
//                    inventoryMap.put(item.getTagTransaction(), item);
                    app.putitem(item);
                    processedItems++;

                } while (cursor.moveToNext());
            }
            cursor.close();
            return inventoryMap;

        } catch (Exception e) {
            return inventoryMap;
        }
        // Close the database connection
    }

    private Issuemode getIssueItemFromCursor(Cursor cursor) {
        Issuemode item = new Issuemode();
        Field[] fields = Issuemode.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String columnName = field.getName();
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex != -1) {
                    Class<?> fieldType = field.getType();
                    if (fieldType == String.class) {
                        field.set(item, cursor.getString(columnIndex));
                    } else if (fieldType == long.class) {
                        field.setLong(item, cursor.getLong(columnIndex));
                    } else if (fieldType == double.class) {
                        field.setDouble(item, cursor.getDouble(columnIndex));
                    }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        int idIndex = cursor.getColumnIndex("id");
        if (idIndex != -1) {
            item.setId(cursor.getLong(idIndex));
        }

        // You should continue this pattern for other columns in your itemmodel

        return item;
    }

    private Itemmodel getItemFromCursor(Cursor cursor) {
        Itemmodel item = new Itemmodel();
        Field[] fields = Itemmodel.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String columnName = field.getName();
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex != -1) {
                    Class<?> fieldType = field.getType();
                    if (fieldType == String.class) {
                        field.set(item, cursor.getString(columnIndex));
                    } else if (fieldType == long.class) {
                        field.setLong(item, cursor.getLong(columnIndex));
                    } else if (fieldType == double.class) {
                        field.setDouble(item, cursor.getDouble(columnIndex));
                    }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        int idIndex = cursor.getColumnIndex("id");
        if (idIndex != -1) {
            item.setId(cursor.getLong(idIndex));
        }
        // You should continue this pattern for other columns in your itemmodel

        return item;
    }

    public void cleardata(Context mContext, MyApplication app) {
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = entryDatabase.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ALL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + D_TABLE);
        app.cleardata(mContext);
    }

    public int gettotalcount(Context mContext) {
        int totalCount = 0;
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = entryDatabase.getReadableDatabase();

        try {
            db.beginTransaction(); // Start transaction

            // Query to get the count of items from the ALL_TABLE
            String queryAll = "SELECT COUNT(*) FROM " + ALL_TABLE;
            Cursor cursorAll = db.rawQuery(queryAll, null);
            if (cursorAll.moveToFirst()) {
                totalCount += cursorAll.getInt(0); // Add the count to the total count
            }
            cursorAll.close();
            // Add more queries for other tables if needed

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); // End the transaction
            entryDatabase.close(); // Close the database
        }

        return totalCount;
    }

    public Itemmodel getitembytid(String tidValue, Context mContext) {
        Itemmodel item = null;
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = entryDatabase.getReadableDatabase();

        try {
            db.beginTransaction(); // Start transaction

            // Query to select item from ALL_TABLE based on tidValue
            String queryAll = "SELECT * FROM " + ALL_TABLE + " WHERE tidValue = ? LIMIT 1";
            Cursor cursorAll = db.rawQuery(queryAll, new String[]{tidValue});
            if (cursorAll.moveToFirst()) {
                // Create and populate the item object
                item = getItemFromCursor(cursorAll);
            }
            cursorAll.close();


            // Add more queries for other tables if needed

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); // End the transaction
            entryDatabase.close(); // Close the database
        }

        return item;
    }

    public void deletetable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ALL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + D_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + RFID_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + "boxtable");
//        db.execSQL("DROP TABLE IF EXISTS " + "categorytable");
//        db.execSQL("DROP TABLE IF EXISTS " + "producttable");
//        db.execSQL("DROP TABLE IF EXISTS " + "ratestable");
//        db.execSQL("DROP TABLE IF EXISTS " + "ratetable");
//        db.execSQL("DROP TABLE IF EXISTS " + "rate");
//        db.execSQL("DROP TABLE IF EXISTS " + "all_entry");
//        db.execSQL("DROP TABLE IF EXISTS " + "allhistory");
//        db.execSQL("DROP TABLE IF EXISTS " + T_TABLE);

//        db.execSQL("DROP TABLE IF EXISTS "+SKU_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS "+VENDOR_TABLE);
    }

    public Map<String, Itemmodel> getBilledItems(Context mContext) {
        HashMap<String, Itemmodel> billList = new HashMap<>();
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = entryDatabase.getReadableDatabase();
            db.beginTransaction(); // Start transaction
            String query = "SELECT * FROM " + T_TABLE;
            cursor = db.rawQuery(query, null);
            int i = 1;

            if (cursor.moveToFirst()) {
                do {
                    Itemmodel item = getItemFromCursor(cursor);
                    if (item != null) {
                        String uniqueKey = String.valueOf(i);
                        billList.put(uniqueKey, item);
                        i++;
                    }
                } while (cursor.moveToNext());
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // Close the cursor
            }
            if (db != null && db.isOpen()) {
                db.endTransaction(); // End the transaction
                db.close(); // Close the database
            }
        }

        return billList;
    }

    public Map<String, Itemmodel> getBilledItems1(Context mContext) {
        HashMap<String, Itemmodel> billList = new HashMap<>();
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = entryDatabase.getReadableDatabase();
            db.beginTransaction(); // Start transaction
            String query = "SELECT * FROM " + T_TABLE;
            cursor = db.rawQuery(query, null);
            int i = 1;

            // Iterate through the cursor and add items to the billList map
            if (cursor.moveToFirst()) {
                do {
                    Itemmodel item = getItemFromCursor(cursor);
                    if (item != null) {
//                        String uniqueKey = item.getInvoiceNumber() + "|" + item.getTidValue();
                        String uniqueKey = String.valueOf(i);//item.getInvoiceNumber() + "|" + item.getTidValue();
                        billList.put(uniqueKey, item);
                        i++;
//                        billList.put(item.getTidValue(), item);
                    }
                } while (cursor.moveToNext());
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Close the cursor
            }
            if (db != null && db.isOpen()) {
                db.endTransaction(); // End the transaction
                db.close(); // Close the database
            }
        }

        return billList;
    }


    // Method to clear duplicate entries based on tid
    private void clearDuplicateEntries(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + RFID_TABLE + " WHERE id NOT IN (SELECT MIN(id) FROM " + RFID_TABLE + " GROUP BY tid)");
    }

    public void makerfidentry(FragmentActivity activity, MyApplication app, List<Rfidresponse.ItemModel> rfidList) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + RFID_TABLE);


        // Create the table if it doesn't exist
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + RFID_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tid TEXT, "
                + "barcode TEXT);";
        db.execSQL(createTableQuery);

        // Clear duplicates from the table
//        clearDuplicateEntries(db);

        // Insert or update each item
        try {
            db.beginTransaction();
            for (Rfidresponse.ItemModel item : rfidList) {
                ContentValues values = new ContentValues();
                values.put("tid", item.getTid());
                values.put("barcode", item.getBarcodeNumber());

                // Insert if not exists, update if exists
                long result = db.insertWithOnConflict(RFID_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (result == -1) {
                    // Handle insertion failure if needed
                    Log.e("SQLite", "Failed to insert item: " + item.getTid());
                } else {
                    Log.e("SQLite", "sucess");
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", "Error inserting items: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        db.close();
    }


    public List<Rfidresponse.ItemModel> getrfid(Context context, MyApplication app) {
        List<Rfidresponse.ItemModel> rfidList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Create the table if it doesn't exist
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + RFID_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tid TEXT, "
                + "barcode TEXT);";
        db.execSQL(createTableQuery);

        String query = "SELECT * FROM " + RFID_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String tid = cursor.getString(cursor.getColumnIndex("tid"));
                String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                Rfidresponse.ItemModel item = new Rfidresponse.ItemModel(tid, barcode);
                rfidList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return rfidList;
    }


    public void makecustomer(FragmentActivity activity, MyApplication app, List<jjjcustomermodel.UserDatum> rfidList) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + C_TABLE);

        // Create the table if it doesn't exist
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + C_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "userid TEXT);";
        db.execSQL(createTableQuery);

        // Clear duplicates from the table
//        clearDuplicateEntries(db);

        // Insert or update each item
        try {
            db.beginTransaction();
            for (jjjcustomermodel.UserDatum item : rfidList) {
                ContentValues values = new ContentValues();
                values.put("name", item.getFull_name());
                values.put("userid", item.getUser_id());

                Log.e("check customerinsert ", " " + item.getFull_name());

                // Insert if not exists, update if exists
                long result = db.insertWithOnConflict(C_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (result == -1) {
                    // Handle insertion failure if needed
                    Log.e("SQLite", "Failed to insert item: " + item.getUser_id());
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", "Error inserting items: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        db.close();
    }


    public List<jjjcustomermodel.UserDatum> getcustomer(Context context, MyApplication app) {
        List<jjjcustomermodel.UserDatum> rfidList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Create the table if it doesn't exist
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + RFID_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "userid TEXT);";
        db.execSQL(createTableQuery);

        String query = "SELECT * FROM " + C_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String tid = cursor.getString(cursor.getColumnIndex("name"));
                String barcode = cursor.getString(cursor.getColumnIndex("userid"));
                jjjcustomermodel.UserDatum item = new jjjcustomermodel.UserDatum(barcode, tid);
                rfidList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return rfidList;
    }


    public int gettotalcount1(Context mContext) {
        int totalCount = 0;
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = entryDatabase.getReadableDatabase();

        try {
            db.beginTransaction(); // Start transaction

            // Query to get the count of items from the ALL_TABLE
            String queryAll = "SELECT COUNT(*) FROM " + R_TABLE;
            Cursor cursorAll = db.rawQuery(queryAll, null);
            if (cursorAll.moveToFirst()) {
                totalCount += cursorAll.getInt(0); // Add the count to the total count
            }
            cursorAll.close();
            // Add more queries for other tables if needed

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); // End the transaction
            entryDatabase.close(); // Close the database
        }

        return totalCount;
    }

    public HashMap<String, Itemmodel> loadRemapItems(Context context, MyApplication app) {
        EntryDatabase db = new EntryDatabase(context);
        HashMap<String, Itemmodel> inventoryMap = new HashMap<>();

        try (SQLiteDatabase sqLiteDatabase = db.getReadableDatabase()) {

            String query = "SELECT * FROM " + R_TABLE;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Itemmodel item = getItemFromCursor(cursor);
//                    Log.d("entrydatabase", "check read " + item.toString());
                    inventoryMap.put(item.getTidValue(), item);
//                    app.putitem(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return inventoryMap;

        } catch (Exception e) {
            return inventoryMap;
        }
        // Close the database connection
    }

    public List<Issuemode> getIssueitems(FragmentActivity activity) {
        List<Issuemode> itemList = new ArrayList<>();
        String query = "SELECT * FROM " + I_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Issuemode item = getIssueItemFromCursor(cursor);
                if (item != null) {
                    itemList.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    public int getinvoicenumber(Context mContext) {
        int nextInvoiceNumber = 1;
        EntryDatabase entryDatabase = new EntryDatabase(mContext);
        SQLiteDatabase db = entryDatabase.getReadableDatabase();

        // SQL query to get all invoice numbers
        String query = "SELECT InvoiceNumber FROM " + T_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // Initialize the max number
            int maxNumber = -1;

            do {
                String invoiceNumber = cursor.getString(cursor.getColumnIndex("InvoiceNumber"));
                // Extract the numeric part
                if (invoiceNumber.startsWith("OE")) {
                    String numericPart = invoiceNumber.substring(2);
                    try {
                        int number = Integer.parseInt(numericPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        // Handle parsing error if needed
                    }
                } else if (invoiceNumber.startsWith("E") || invoiceNumber.startsWith("R") || invoiceNumber.startsWith("B") || invoiceNumber.startsWith("O")) {
                    String numericPart = invoiceNumber.substring(1);
                    try {
                        int number = Integer.parseInt(numericPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        // Handle parsing error if needed
                    }
                } else {
                    String numericPart = invoiceNumber;
                    try {
                        int number = Integer.parseInt(numericPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        // Handle parsing error if needed
                    }
                }
            } while (cursor.moveToNext());

            // Calculate the next invoice number
            nextInvoiceNumber = maxNumber + 1;
        }

        cursor.close();
        db.close();

        return nextInvoiceNumber;
    }





    /*private void createalltable(SQLiteDatabase db, Class<?> modelClass, String tableName) {
        // Check if the table already exists
        if (!isTableExists(db, tableName)) {
            // Get the model class fields
            Field[] fields = modelClass.getDeclaredFields();

            // StringBuilder to create the SQL CREATE TABLE statement
            StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName + " (");
            createTableQuery.append("id INTEGER PRIMARY KEY AUTOINCREMENT, ");

            // Iterate over model class fields and add columns to the CREATE TABLE statement
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String columnName = field.getName();
                Class<?> fieldType = field.getType();

                // Append column name and type to the query
                createTableQuery.append(columnName).append(" ").append(getSqliteType(fieldType));

                // Add a comma between columns, except for the last one
                if (i < fields.length - 1) {
                    createTableQuery.append(", ");
                }
            }

            // Close the CREATE TABLE statement
            createTableQuery.append(");");

            // Execute the CREATE TABLE query
            db.execSQL(createTableQuery.toString());
        }
    }*/


    private void createalltable(SQLiteDatabase db, Class<?> modelClass, String tableName) {
        // Check if the table already exists
        if (!isTableExists(db, tableName)) {
            // Get the model class fields
            Field[] fields = modelClass.getDeclaredFields();

            // StringBuilder to create the SQL CREATE TABLE statement
            StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName + " (");
            createTableQuery.append("id INTEGER PRIMARY KEY AUTOINCREMENT, ");

            // Iterate over model class fields and add columns to the CREATE TABLE statement
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String columnName = field.getName();
                if (!columnName.equalsIgnoreCase("id")) {
                    Class<?> fieldType = field.getType();

                    createTableQuery.append(columnName).append(" ").append(getSqliteType(fieldType));

                    if (i < fields.length - 1) {
                        createTableQuery.append(", ");
                    }
                }
            }

            // Close the CREATE TABLE statement
            createTableQuery.append(");");

            // Execute the CREATE TABLE query
            db.execSQL(createTableQuery.toString());
        } else {
            addMissingColumns(db, modelClass, tableName);

        }
    }

    private void addMissingColumns(SQLiteDatabase db, Class<?> modelClass, String tableName) {
        // Get existing columns
        List<String> existingColumns = getExistingColumns(db, tableName);

        // Normalize existing columns to lower case
        List<String> normalizedExistingColumns = new ArrayList<>();
        for (String column : existingColumns) {
            normalizedExistingColumns.add(column.toLowerCase());
        }

        // Get the model class fields
        Field[] fields = modelClass.getDeclaredFields();

        // Log existing columns for debugging
        Log.e("Existing Columns", existingColumns.toString());

        // Iterate over model class fields and check for missing columns
        for (Field field : fields) {
            String columnName = field.getName().toLowerCase(); // Normalize field name
            if (!normalizedExistingColumns.contains(columnName)) {
                // Column is missing, add it
                Class<?> fieldType = field.getType();
                String alterTableQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + field.getName() + " " + getSqliteType(fieldType) + ";";
                db.execSQL(alterTableQuery);
                Log.d("addColumn", "Added column " + field.getName() + " to table " + tableName);
            } else {
                Log.d("addColumn", "Column " + field.getName() + " already exists in table " + tableName);
            }
        }
    }


    private List<String> getExistingColumns(SQLiteDatabase db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        while (cursor.moveToNext()) {
            String columnName = cursor.getString(cursor.getColumnIndex("name"));
            columns.add(columnName);
        }
        cursor.close();
        return columns;
    }

    private String getSqliteType(Class<?> fieldType) {
        if (fieldType == String.class) {
            return "TEXT";
        } else if (fieldType == long.class || fieldType == Date.class) {
            return "INTEGER";
        } else if (fieldType == double.class) {
            return "REAL";
        } else if (fieldType == boolean.class) {
            return "INTEGER"; // SQLite does not have a boolean type, using INTEGER (0 or 1)
        } else {
            // Add additional cases as needed for other data types
            return "TEXT"; // Default to TEXT if the type is not recognized
        }
    }


    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName});
        boolean tableExists = cursor.moveToFirst();
        cursor.close();
        return tableExists;
    }

    public boolean insertEmail(String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + EMAIL_TABLE +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_EMAILID + " TEXT);";
            db.execSQL(createTableQuery);

            ContentValues values = new ContentValues();
            values.put(C_EMAILID, newEmail);
            long result = db.insert(EMAIL_TABLE, null, values);

            return result != -1; // Return true if insertion was successful, false otherwise
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false in case of an exception
        } finally {
            db.close();
        }
    }


    //stock report
    public void stockreport(FragmentActivity activity, interfaces.RetrieveDataListener1 listener, Date startDate, Date endDate) {
        new Retrievestockreport(activity, listener, startDate, endDate).execute();
    }


    public class Retrievestockreport extends AsyncTask<String, Integer, Map<String, List<Itemmodel>>> {
        private ProgressDialog progressDialog;
        private Context context;
        private interfaces.RetrieveDataListener1 listener;
        private Date fromdate;
        private Date todate;

        public Retrievestockreport(Context context, interfaces.RetrieveDataListener1 listener, Date fromdate, Date todate) {
            this.context = context;
            this.listener = listener;
            this.fromdate = fromdate;
            this.todate = todate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Retrieving items...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            EntryDatabase db = new EntryDatabase(context);
            SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
            long totalCount = getCount(sqLiteDatabase, fromdate, todate);
//            long totalItems = getCount(sqLiteDatabase);
            progressDialog.setMax(Integer.parseInt(String.valueOf(totalCount)));
            progressDialog.show();
        }

        @Override
        protected Map<String, List<Itemmodel>> doInBackground(String... params) {
            EntryDatabase db = new EntryDatabase(context);
            SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
            Map<String, List<Itemmodel>> filtereditems = new HashMap<>();

//            long totalItems = getCount(sqLiteDatabase);
            long chunkSize = 2000;
            List<Itemmodel> resultList = new ArrayList<>();

            Map<String, List<Itemmodel>> groupedItems = new HashMap<>();

            String query = "SELECT * FROM detailstable " +
                    "WHERE Operation IN ('found', 'not found') " +
                    "AND OperationTime BETWEEN ? AND ? " +
                    "ORDER BY OperationTime DESC";

            Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{
                    String.valueOf(fromdate.getTime()),
                    String.valueOf(todate.getTime())
            });
            //we have all items
            int itemcount = 0;

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    Log.d("checkgroup", "" + itemcount);
                    itemcount = itemcount + 1;
//                    inventorymodel currentItem = new inventorymodel();
                    Itemmodel currentItem = getItemFromCursor(cursor);
//                    Log.d("entrydatebasestock", "size " + currentItem.getOperationTime());
                    if (currentItem.getOperationTime() > 0) {
                        String key = String.valueOf(currentItem.getOperationTime());
                        if (groupedItems.containsKey(key)) {
                            List<Itemmodel> itemList = groupedItems.get(key);
                            itemList.add(currentItem);
                            groupedItems.put(key, itemList);
                            Log.d("reports1", "   " + itemList.size() + "  " + key);
                        } else {
                            // Key doesn't exist, create a new list and add the item to it
                            List<Itemmodel> itemList = new ArrayList<>();
                            itemList.add(currentItem);
                            groupedItems.put(key, itemList);
                        }

                    }
                    publishProgress(cursor.getPosition() + 1);
                } while (cursor.moveToNext());
                cursor.close();
                Log.d("reports2", "   " + groupedItems.size());

                DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
                filtereditems.clear();
                Set<String> keys = groupedItems.keySet();


                Map<String, Long> latestTimestamps = new TreeMap<>();//new HashMap<>();

// Iterate over the items to update latest timestamps
//                for (String key : keys) {
                for (Map.Entry<String, List<Itemmodel>> entry : groupedItems.entrySet()) {
                    String skey = entry.getKey();
                    long keyMillis = Long.parseLong(skey);
                    Date keyDate = new Date(keyMillis);
                    String formattedKey = dateFormat2.format(keyDate);

                    // Update latest timestamp for the day
                    if (!latestTimestamps.containsKey(formattedKey) || keyMillis > latestTimestamps.get(formattedKey)) {
                        latestTimestamps.put(skey, keyMillis);
                    }
                }
//                }

// Process the items using the latest timestamps
                for (String key : latestTimestamps.keySet()) {
                    long latestTimestamp = latestTimestamps.get(key);

                    // Get the latest items for the day based on the latest timestamp
                    List<Itemmodel> latestItems = groupedItems.get(String.valueOf(latestTimestamp));
                    Date keyDate = new Date(latestTimestamp);
                    String formattedKey = dateFormat2.format(keyDate);
                    filtereditems.put(formattedKey, latestItems);
                    // Do further processing with latestItems...
                }
                for (Map.Entry<String, List<Itemmodel>> entry : filtereditems.entrySet()) {
                    String key = entry.getKey();
                    List<Itemmodel> itemList = entry.getValue();

                    System.out.println("Keyn: " + key);
                    for (Itemmodel item : itemList) {
                        System.out.println("result listn: " + item.toString());
                    }
                }
                Log.d("reports", "result list " + filtereditems.toString());
//                Log.d("reports", "result list 1   "+"  "+filtereditems.size());

            } else {
                Log.d("no data found", "inventory");
            }
            db.close();
            return filtereditems;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Map<String, List<Itemmodel>> result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result != null) {
                listener.onRetrieveData(result);
                Log.d("tag", "retrievesize " + result.size());
//                globaltoast(context, "Items retrieved successfully", "", "");
            } else {
                Log.d("tag", "retrievesize " + result.size());
//                globaltoast(context, "Failed to retrieve items", "", "");
            }
        }

        private long getCount(SQLiteDatabase sqLiteDatabase, Date fromdate, Date todate) {
            String query = "SELECT COUNT(*) FROM detailstable " +
                    "WHERE Operation IN ('found', 'not found') " +
                    "AND OperationTime BETWEEN ? AND ?";

            Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{
                    String.valueOf(fromdate.getTime()),
                    String.valueOf(todate.getTime())
            });

            long count = 0;

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getLong(0);
                cursor.close();
            }

            return count;
        }


    }


}
