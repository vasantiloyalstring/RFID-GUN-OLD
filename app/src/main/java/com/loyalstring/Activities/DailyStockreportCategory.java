package com.loyalstring.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.Adapters.DailyStockCounterAdapter;
import com.loyalstring.Adapters.DailyStockListAdapter;
import com.loyalstring.Adapters.InventoryBottomAdaptor;
import com.loyalstring.Excels.InventoryExcelCreation;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.database.support.Valuesdb;
import com.loyalstring.databinding.DailyStockReportCategoryBinding;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyStockreportCategory extends AppCompatActivity implements CounterClickListener {
    DailyStockReportCategoryBinding b;
    EntryDatabase entryDatabase;
    RecyclerView recyclerView;
    List<Itemmodel> itemmodelList;
    String selectedCounter;

    Globalcomponents globalcomponents;
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DailyStockReportCategoryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        globalcomponents = new Globalcomponents();

        Toolbar toolbar = findViewById(R.id.toolbar_counter);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daily Stock Report"); // Set title
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        initData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // or use onBackPressed();
        return true;
    }

    private void initData() {
        entryDatabase = new EntryDatabase(this);
        recyclerView=findViewById(R.id.rv_category);

        intent=getIntent();
        b.tvCounter.setText(intent.getStringExtra("counter"));

        Valuesdb db = new Valuesdb(this);
        db.getCounters();
        List<String> counterList=new ArrayList<>();
        counterList=db.getcatpro();
        DailyStockCounterAdapter adapter = new DailyStockCounterAdapter(this, counterList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        // Step 1: Get data from Intent
        selectedCounter = intent.getStringExtra("counter");  // e.g., "Counter A"
        String selectedDate = intent.getStringExtra("dateData");        // e.g., "2025-06-13"
        Log.d("@@ INTENT", "Counter: " + selectedCounter + ", Date: " + selectedDate);

// Step 2: Get full item list
        itemmodelList = entryDatabase.getAllItems();
        Log.d("@@", "Total Items: " + itemmodelList.size());

// Step 3: Filter by selected counter AND selected date
        List<Itemmodel> filteredList = new ArrayList<>();

        for (Itemmodel item : itemmodelList) {
            long timestamp = item.getEntryDate();
            Log.d("@@timestamp", "Entry timestamp: " + item);

            Log.d("@@timestamp", "Entry timestamp: " + timestamp);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            String itemDate = sdf.format(new Date(timestamp));

            if (item.getCounterName().equalsIgnoreCase(selectedCounter)
                    && itemDate.equals(selectedDate)) {
                filteredList.add(item);
            }
        }
        Log.d("@@", "Filtered List Size: " + filteredList.size());

// Step 4: Group by Category and sum AvlQty + MatchQty
        Map<String, Itemmodel> categoryMap = new LinkedHashMap<>();

        for (Itemmodel item : filteredList) {
            String category = item.getCategory();  // Or use product if needed

            if (!categoryMap.containsKey(category)) {
                categoryMap.put(category, item);  // Use original or clone if needed
            } else {
                Itemmodel existing = categoryMap.get(category);
                existing.setAvlQty(existing.getAvlQty() + item.getAvlQty());
                existing.setMatchQty(existing.getMatchQty());
            }
        }

// Step 5: Convert to list
        itemmodelList = new ArrayList<>(categoryMap.values());

// Step 6: Set to adapter
        DailyStockListAdapter adapter1 = new DailyStockListAdapter(this, itemmodelList, this, "category");
        b.rvDailyStock.setLayoutManager(new LinearLayoutManager(this));
        b.rvDailyStock.setAdapter(adapter1);



        b.tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DailyStockreportCategory.this, "clicked", Toast.LENGTH_SHORT).show();

                if (itemmodelList.isEmpty()) {
                    Toast.makeText(DailyStockreportCategory.this, "no item to send email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (areStoragePermissionsGranted()) {
                    boolean folder = globalcomponents.checkfileexist("inventory");
                    if (folder) {
                        File topmatch = createfile("matcheditems");
                        File topunmatch = createfile("unmatcheditems");
                        File bottommatch = createfile("matcheditemdetails");
                        File botomunmatch = createfile("unmatcheditemdetails");
                        File topall = createfile("allitems");
                        File bottomall = createfile("allitemdetails");

                        if (topmatch != null && topunmatch != null && bottommatch != null && botomunmatch != null && topall != null && bottomall != null) {

                            ArrayList<Itemmodel> top = new ArrayList<>();
                            top.add(itemmodelList.get(0));
                            ArrayList<Itemmodel> bottom = new ArrayList<>();
                            HashMap<String, ArrayList<Itemmodel>> excelmap = new HashMap<>();
                            excelmap.put(topmatch.getAbsolutePath(), top);
                            excelmap.put(topunmatch.getAbsolutePath(), top);
                            excelmap.put(bottommatch.getAbsolutePath(), bottom);
                            excelmap.put(botomunmatch.getAbsolutePath(), bottom);
                            excelmap.put(topall.getAbsolutePath(), top);
                            excelmap.put(bottomall.getAbsolutePath(), bottom);
                            InventoryBottomAdaptor inventoryBottomAdaptor=null;

                            InventoryExcelCreation excelTask = new InventoryExcelCreation(top, bottom, "allitem", "itemdetails", DailyStockreportCategory.this, "inventory", "scan", excelmap, inventoryBottomAdaptor);
                            excelTask.execute();


                        } else {
                            Toast.makeText(DailyStockreportCategory.this, "failed to create file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ArrayList<String> folders = new ArrayList<>();
                        folders.add("inventory");
                        boolean f = globalcomponents.createFolders(folders);
                        if (!f) {
                            Toast.makeText(DailyStockreportCategory.this, "failed to create file", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DailyStockreportCategory.this, "created file please click again", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(DailyStockreportCategory.this, "File read permission required please restart app", Toast.LENGTH_SHORT).show();
//                    requestStoragePermissions();
                }

            }
        });


    }

    private boolean areStoragePermissionsGranted() {
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private File createfile(String fname) {
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 and above
            try {
                file = File.createTempFile(fname, ".xlsx", getFilesDir());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in internal storage: " + e.getMessage());
                return null;
            }
        } else { // Android versions below 10
            try {
                file = File.createTempFile(fname, ".xlsx", Environment.getExternalStorageDirectory());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in external storage: " + e.getMessage());
                return null;
            }
        }
    }
    @Override
    public void onCounterClick(String category,String categoryName) {
        Intent intent=new Intent(this,DailyStockReportProduct.class);
      //  intent.putExtra("counter",b.tvCounter.getText().toString());
        intent.putExtra("category",categoryName);
        intent.putExtra("counter",selectedCounter);
        intent.putExtra("dateData",category);
        Log.d("@@ dateData","dateData"+category);
        Log.d("@@ category","category"+category+","+b.tvCounter.getText().toString());
        startActivity(intent);

    }
}

