package com.loyalstring.database.support;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.loyalstring.modelclasses.Ratemodel;
import com.loyalstring.modelclasses.catmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Valuesdb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "loyalstring.db";
    private static final int DATABASE_VERSION = 1;
    public static String CATTABLE = "categorytable";
    public static String C_CATEGORY = "category";
    public static String C_CATEGORYID = "categoryId";
    public static String C_PRODUCT = "product";
    public static String BOXTABLE = "boxtable";
    public static String C_BOX = "boxname";
    public static String PROTABLE = "producttable";
    public static String RATETABLE = "ratestable";
    public static final String C_PURITY = "purity";
    public static String G_RATE = "rate";
    public static String EMAIL_TABLE = "emailstable";
    private static final String C_EMAILID = "emailid";
    public static String COUNTER_TABLE = "counter";

    public static  String COUNTER_NAME="counter_name";


    public Valuesdb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Any other initialization if needed
    }
    
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<String> getcatpro() {
        List<String> catList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String CREATE_CATEGORY_TABLE =
                "CREATE TABLE IF NOT EXISTS " + CATTABLE + " (" +
                        C_CATEGORY + " TEXT, " +
                        C_PRODUCT + " TEXT" +
                        ")";

        database.execSQL(CREATE_CATEGORY_TABLE);
        String[] projection = {
                C_CATEGORY,
                C_PRODUCT
        };
        Cursor cursor = database.query(
                CATTABLE,      // Table name
                projection,    // Columns to retrieve
                null,          // Selection
                null,          // SelectionArgs
                null,          // GroupBy
                null,          // Having
                null           // OrderBy
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                catmodel model = new catmodel();
                model.setCategory(cursor.getString(cursor.getColumnIndex(C_CATEGORY)));
                model.setProduct(cursor.getString(cursor.getColumnIndex(C_PRODUCT)));

                catList.add(cursor.getString(cursor.getColumnIndex(C_CATEGORY)));
            } while (cursor.moveToNext());

            cursor.close();
        }
        database.close();

        return catList;

    }

    public long addbox(String name, Context activity) {
        SQLiteDatabase db = getWritableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + BOXTABLE + "("
                + C_BOX + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
        if(!boxExists(db, name)){
            ContentValues values = new ContentValues();
            values.put(C_BOX, name);

            return db.insert(BOXTABLE, null, values);
        }else{
//            globaltoast(activity, "box already exist", "", "");
            return  -1;
        }
    }



    /*Counter*/
    public long addCounter(String name, Context activity) {
        SQLiteDatabase db = getWritableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + COUNTER_TABLE + "("
                + COUNTER_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
        if(!counterExists(db, name)){
            ContentValues values = new ContentValues();
            values.put(COUNTER_NAME, name);

            return db.insert(COUNTER_TABLE, null, values);
        }else{
//            globaltoast(activity, "box already exist", "", "");
            return  -1;
        }
    }

    private boolean counterExists(SQLiteDatabase db, String bname) {
        Cursor cursor = db.query(
                COUNTER_TABLE,             // Table name
                new String[]{COUNTER_NAME},   // Columns to retrieve
                COUNTER_NAME + "=?",    // Selection
                new String[]{bname},    // SelectionArgs
                null,                 // GroupBy
                null,                 // Having
                null                  // OrderBy
        );

        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }


    private boolean boxExists(SQLiteDatabase db, String bname) {
        Cursor cursor = db.query(
                BOXTABLE,             // Table name
                new String[]{C_BOX},   // Columns to retrieve
                C_BOX + "=?",    // Selection
                new String[]{bname},    // SelectionArgs
                null,                 // GroupBy
                null,                 // Having
                null                  // OrderBy
        );

        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }


    public List<String> getboxes() {
        List<String> itemNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + BOXTABLE + "("
                + C_BOX + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
        Cursor cursor = db.query(BOXTABLE, new String[]{C_BOX}, null, null, null, null, null);
//        if (isTableExists(db, tablename)) {
        if (cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndex(C_BOX));
                itemNames.add(itemName);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        }
        db.close();
        return itemNames;
    }

    public List<String> getCounters() {
        List<String> itemNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + COUNTER_TABLE + "("
                + COUNTER_NAME+ " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
        Cursor cursor = db.query(COUNTER_TABLE, new String[]{COUNTER_NAME}, null, null, null, null, null);
//        if (isTableExists(db, tablename)) {
        if (cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndex(COUNTER_NAME));
                itemNames.add(itemName);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        }
        db.close();
        return itemNames;
    }

    public long addcategory(String name, Context activity) {

        SQLiteDatabase database = getWritableDatabase();
        String CREATE_CATEGORY_TABLE =
                "CREATE TABLE IF NOT EXISTS " + CATTABLE + " (" +
                        C_CATEGORY + " TEXT, " +
                        C_PRODUCT + " TEXT" +
                        ")";

        database.execSQL(CREATE_CATEGORY_TABLE);

        if (!categoryExists(database, name)) {
            // If the category does not exist, insert it
            ContentValues values = new ContentValues();
            values.put(C_CATEGORY, name);

            // Insert the new row, returning the primary key value of the new row
            return database.insert(CATTABLE, null, values);

        } else {
            // If the category already exists, return -1 or handle it accordingly
            return -1;
        }

    }

    private boolean categoryExists(SQLiteDatabase db, String category) {
        Cursor cursor = db.query(
                CATTABLE,             // Table name
                new String[]{C_CATEGORY},   // Columns to retrieve
                C_CATEGORY + "=?",    // Selection
                new String[]{category},    // SelectionArgs
                null,                 // GroupBy
                null,                 // Having
                null                  // OrderBy
        );

        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    public List<String> getProductsByCategory(String category) {
        List<String> productList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        String createRatesTableQuery = "CREATE TABLE IF NOT EXISTS " + PROTABLE + "("
                + C_CATEGORY + " TEXT,"
                + C_PRODUCT + " TEXT,"
                + "PRIMARY KEY (" + C_CATEGORY + ", " + C_PRODUCT + ")"
                + ")";
        database.execSQL(createRatesTableQuery);
        // Define the columns you want to retrieve
        String[] projection = {C_PRODUCT};

        // Define the selection and selectionArgs to filter by category
        String selection = C_CATEGORY + "=?";
        String[] selectionArgs = {category};

        // Perform the query
        Cursor cursor = database.query(
                PROTABLE,      // Table name
                projection,    // Columns to retrieve
                selection,     // Selection
                selectionArgs, // SelectionArgs
                null,          // GroupBy
                null,          // Having
                null           // OrderBy
        );

        // Iterate through the cursor and add products to the list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String product = cursor.getString(cursor.getColumnIndex(C_PRODUCT));
                productList.add(product);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return productList;
    }

    public List<String> getCategoryByCounter(String counter) {
        List<String> categoryList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        // Create the table if not exists (optional safety)
        String createCategoryTableQuery = "CREATE TABLE IF NOT EXISTS " + COUNTER_TABLE + " ("
                + COUNTER_NAME + " TEXT,"
                + C_CATEGORY + " TEXT,"
                + "PRIMARY KEY (" + COUNTER_NAME + ", " + COUNTER_TABLE + ")"
                + ")";
        database.execSQL(createCategoryTableQuery);

        // Define the columns to retrieve
        String[] projection = {C_CATEGORY};

        // Filter by counter
        String selection = COUNTER_NAME + "=?";
        String[] selectionArgs = {counter};

        // Query the database
        Cursor cursor = database.query(
                COUNTER_TABLE,  // Table name
                projection,      // Columns to return
                selection,       // WHERE clause
                selectionArgs,   // WHERE args
                null, null, null // GroupBy, Having, OrderBy
        );

        // Parse result
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(C_CATEGORY));
                categoryList.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return categoryList;
    }


    public boolean addproduct(String pro, Context activity, String cattext) {

        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean success = false;

        try {
            db = getWritableDatabase();

            String createRatesTableQuery = "CREATE TABLE IF NOT EXISTS " + PROTABLE + "("
                    + C_CATEGORY + " TEXT,"
                    + C_PRODUCT + " TEXT,"
                    + "PRIMARY KEY (" + C_CATEGORY + ", " + C_PRODUCT + ")"
                    + ")";
            db.execSQL(createRatesTableQuery);

            String selection = C_CATEGORY + " = ? AND " + C_PRODUCT + " = ?";
            String[] selectionArgs = {cattext, pro};

            cursor = db.query(PROTABLE, null, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                // Combination already exists, handle accordingly
                Toast.makeText(activity, "Product already exists", Toast.LENGTH_SHORT).show();
            } else {
                // Combination does not exist, insert the new rate
                ContentValues values = new ContentValues();
                values.put(C_CATEGORY, cattext);
                values.put(C_PRODUCT, pro);

                long id = db.insert(PROTABLE, null, values);

                if (id != -1) {
                    // Rate inserted successfully
                    Toast.makeText(activity, "Product added successfully", Toast.LENGTH_SHORT).show();
                    success = true;
                } else {
                    // Error occurred while inserting the rate
                    Toast.makeText(activity, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return success;


    }

    public List<Ratemodel> getgoldratelist() {
        List<Ratemodel> ratesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            // Create the rate table if it doesn't exist
            String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + RATETABLE + "("
                    + C_CATEGORY + " TEXT, "
                    + C_PURITY + " TEXT, "
                    + G_RATE + " TEXT"
                    + ")";
            db.execSQL(CREATE_TABLE);

            // Retrieve all rates from the RATETABLE
            String query = "SELECT * FROM " + RATETABLE;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    String category = cursor.getString(cursor.getColumnIndex(C_CATEGORY));
                    String purity = cursor.getString(cursor.getColumnIndex(C_PURITY));
                    double rate = Double.parseDouble(cursor.getString(cursor.getColumnIndex(G_RATE)));

                    Ratemodel rateModel = new Ratemodel(category, purity, rate);
                    ratesList.add(rateModel);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return ratesList;

    }

    public boolean updaterate(String category, String purity, double rate) {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + RATETABLE + "("
                + C_CATEGORY + " TEXT, "
                + C_PURITY + " TEXT, "
                + G_RATE + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
//        ContentValues values = new ContentValues();
//        values.put(G_RATE, rate);

        try {
           /* int rowsAffected = db.update(RATETABLE, values, C_CATEGORY + " = ? AND " + C_PURITY + " = ?",
                    new String[]{category, purity});

            return rowsAffected > 0;*/

            String query = "SELECT * FROM " + RATETABLE +
                    " WHERE " + C_CATEGORY + " = ? AND " + C_PURITY + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{category, purity});

            ContentValues values = new ContentValues();
            values.put(C_CATEGORY, category);
            values.put(C_PURITY, purity);
            values.put(G_RATE, rate);

            if (cursor.moveToFirst()) {
                // Update rate if the combination exists
                String whereClause = C_CATEGORY + " = ? AND " + C_PURITY + " = ?";
                String[] whereArgs = {category, purity};
                int rowsAffected = db.update(RATETABLE, values, whereClause, whereArgs);
                return rowsAffected > 0;
            } else {
                // Insert rate if the combination doesn't exist
                long rowsAffected = db.insert(RATETABLE, null, values);
                return rowsAffected > 0;
            }


        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        } finally {
//            cursor.close();
            db.close();
        }
    }

    public List<String> reademails(Context context) {
        List<String> emailList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + EMAIL_TABLE +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_EMAILID + " TEXT);";
        db.execSQL(createTableQuery);
        Cursor cursor = db.rawQuery("SELECT * FROM " + EMAIL_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                String email = cursor.getString(cursor.getColumnIndex(C_EMAILID));
                emailList.add(email);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return emailList;
    }

}
