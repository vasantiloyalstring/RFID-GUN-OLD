package com.loyalstring.database.product;

import androidx.work.Worker;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loyalstring.modelclasses.Itemmodel;

import java.lang.reflect.Type;
import java.util.List;



public class SaveItemWorker extends Worker {

    private final Context context;

    public SaveItemWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        SQLiteDatabase db = null;
        try {
            String jsonList = getInputData().getString("itemListJson");
            if (jsonList == null || jsonList.isEmpty()) {
                Log.e("SaveItemWorker", "Empty input data");
                return Result.failure();
            }

            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
            Type listType = new TypeToken<List<Itemmodel>>() {}.getType();
            List<Itemmodel> itemList = gson.fromJson(jsonList, listType);

            EntryDatabase dbHelper = new EntryDatabase(context);
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            String sql = "INSERT INTO " + "All_SAVE_TABLE" + " (" + "COL_DATA" + ") VALUES (?);";
            SQLiteStatement stmt = db.compileStatement(sql);

            for (Itemmodel item : itemList) {
                try {
                    String itemJson = gson.toJson(item);
                    stmt.clearBindings();
                    stmt.bindString(1, itemJson);
                    stmt.executeInsert();
                } catch (Exception itemEx) {
                    Log.e("SaveItemWorker", "Failed to insert item: " + itemEx.getMessage());
                    // Optionally continue inserting other items
                }
            }

            db.setTransactionSuccessful();
            return Result.success();

        } catch (Exception e) {
            Log.e("SaveItemWorker", "Error: " + Log.getStackTraceString(e));
            return Result.failure();

        } finally {
            if (db != null) {
                try {
                    if (db.inTransaction()) db.endTransaction();
                    db.close();
                } catch (Exception closeEx) {
                    Log.e("SaveItemWorker", "Error closing DB: " + closeEx.getMessage());
                }
            }
        }
    }
}
