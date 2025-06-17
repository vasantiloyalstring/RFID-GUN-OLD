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
import com.loyalstring.databinding.DailyStockReportCounterFragmentBinding;
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

public class DailyStockReportCounter extends AppCompatActivity implements CounterClickListener {
    DailyStockReportCounterFragmentBinding b;
    EntryDatabase entryDatabase;
    RecyclerView recyclerView;

    Globalcomponents globalcomponents;


    List<Itemmodel> itemmodelList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DailyStockReportCounterFragmentBinding.inflate(getLayoutInflater());
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
       // recyclerView = findViewById(R.id.rv_counter);

        String selectedDate = getIntent().getStringExtra("dateData");

        Valuesdb db = new Valuesdb(this);
        db.getCounters();
        List<String> counterList = new ArrayList<>();
        counterList = db.getCounters();
       /* DailyStockCounterAdapter adapter = new DailyStockCounterAdapter(this, counterList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);*/




     /*   itemmodelList = new ArrayList<>();
        try {
            itemmodelList = entryDatabase.getAllItems();

            Log.d("@@","@@ size"+itemmodelList.size());

            Map<String, Itemmodel> counterMap = new LinkedHashMap<>();

            for (Itemmodel item : itemmodelList) {
                String counter = item.getCounterName();

                if (!counterMap.containsKey(counter)) {
                    // Clone item into the map
                    counterMap.put(counter, item);
                } else {
                    // Add quantities to existing entry
                    Itemmodel existing = counterMap.get(counter);
                    existing.setAvlQty(existing.getAvlQty() + item.getAvlQty());
                    existing.setMatchQty(existing.getMatchQty() + item.getMatchQty());
                }
            }

// Final list with summed quantities per counter
            itemmodelList = new ArrayList<>(counterMap.values());



            DailyStockListAdapter DailyStockadapter = new DailyStockListAdapter(this, itemmodelList,this,"counter");
            b.rvDailyStock.setLayoutManager(new LinearLayoutManager(this));
            b.rvDailyStock.setAdapter(DailyStockadapter);*/

        itemmodelList = new ArrayList<>();

        try {
            List<Itemmodel> allItems = entryDatabase.getAllItems(); // full list
            Log.d("@@", "@@ total size: " + allItems.size());

            Map<String, Itemmodel> counterMap = new LinkedHashMap<>();

            for (Itemmodel item : allItems) {

                long timestamp = item.getEntryDate();
                Log.d("@@timestamp", "Entry timestamp: " + item);

                Log.d("@@timestamp", "Entry timestamp: " + timestamp);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                String itemDate = sdf.format(new Date(timestamp));

                String counter = item.getCounterName();

                if (itemDate != null && itemDate.equals(selectedDate)) {
                    if (!counterMap.containsKey(counter)) {
                        counterMap.put(counter, item);
                    } else {
                        Itemmodel existing = counterMap.get(counter);
                        existing.setAvlQty(existing.getAvlQty() + item.getAvlQty());
                        existing.setMatchQty(existing.getMatchQty() + item.getMatchQty());
                    }
                }
            }

            itemmodelList = new ArrayList<>(counterMap.values());
            Log.d("@@", "@@ filtered and grouped size: " + itemmodelList.size());

            DailyStockListAdapter adapter = new DailyStockListAdapter(this, itemmodelList, this, "counter");
            b.rvDailyStock.setLayoutManager(new LinearLayoutManager(this));
            b.rvDailyStock.setAdapter(adapter);


    } catch (Exception e) {
            e.printStackTrace();
        }

        b.tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DailyStockReportCounter.this, "clicked", Toast.LENGTH_SHORT).show();

                if (itemmodelList.isEmpty()) {
                    Toast.makeText(DailyStockReportCounter.this, "no item to send email", Toast.LENGTH_SHORT).show();
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
                        File topalll = createfile("allitemsreport");

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
                            excelmap.put(topalll.getAbsolutePath(), top);
                            excelmap.put(bottomall.getAbsolutePath(), bottom);

                            InventoryBottomAdaptor inventoryBottomAdaptor=null;

                            InventoryExcelCreation excelTask = new InventoryExcelCreation(top, bottom, "allitem", "itemdetails", DailyStockReportCounter.this, "dailystock", "scan", excelmap, inventoryBottomAdaptor);
                            excelTask.execute();


                        } else {
                            Toast.makeText(DailyStockReportCounter.this, "failed to create file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ArrayList<String> folders = new ArrayList<>();
                        folders.add("dailystock");
                        boolean f = globalcomponents.createFolders(folders);
                        if (!f) {
                            Toast.makeText(DailyStockReportCounter.this, "failed to create file", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DailyStockReportCounter.this, "created file please click again", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(DailyStockReportCounter.this, "File read permission required please restart app", Toast.LENGTH_SHORT).show();
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
    public void onCounterClick(String counterName,String counterNameData) {
        Intent intent = new Intent(this, DailyStockreportCategory.class);
        intent.putExtra("dateData", counterName);

        intent.putExtra("counter", counterNameData);
        startActivity(intent);

    }
}
