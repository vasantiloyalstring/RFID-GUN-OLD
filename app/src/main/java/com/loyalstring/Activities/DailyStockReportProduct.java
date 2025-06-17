package com.loyalstring.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.Adapters.DailyStockListAdapter;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.DaliyStockReportProductActivityBinding;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyStockReportProduct extends AppCompatActivity implements CounterClickListener {
    DaliyStockReportProductActivityBinding b;
    EntryDatabase entryDatabase;
    RecyclerView recyclerView;
    List<Itemmodel> itemmodelList;
    String selectedcounter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DaliyStockReportProductActivityBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

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

       /* Intent intent=getIntent();
        b.tvCounter.setText(intent.getStringExtra("counter"));
        b.tvCategory.setText(intent.getStringExtra("category"));

        selectedcounter=intent.getStringExtra("counter");


        Valuesdb db = new Valuesdb(this);
        db.getCounters();
        List<String> counterList=new ArrayList<>();
        counterList=db.getProductsByCategory(intent.getStringExtra("category"));
        DailyStockCounterAdapter adapter = new DailyStockCounterAdapter(this, counterList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);*/

// Step 1: Fetch all items from DB
        itemmodelList = new ArrayList<>();
        itemmodelList = entryDatabase.getAllItems();

        Intent intent = getIntent();
        String selectedCounter = intent.getStringExtra("counter");
        String selectedCategory = intent.getStringExtra("category");
        String selectedDate = intent.getStringExtra("dateData"); // Expected format: "dd/MM/yyyy"

// Step 2: Filter by counter and category first
        List<Itemmodel> counterCategoryFilteredList = new ArrayList<>();
        for (Itemmodel item : itemmodelList) {
            if (
                    item.getCounterName().equalsIgnoreCase(selectedCounter) &&
                            item.getCategory().equalsIgnoreCase(selectedCategory)
            ) {
                counterCategoryFilteredList.add(item);
            }
        }
        Log.d("@@Step", "Counter+Category Filtered size: " + counterCategoryFilteredList.size());

// Step 3: Now filter that result by date
        List<Itemmodel> finalFilteredList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        for (Itemmodel item : counterCategoryFilteredList) {
            String itemDate = sdf.format(new Date(item.getEntryDate()));
            Log.d("@@Compare", "Item Date: " + itemDate + " | Selected Date: " + selectedDate);

            if (itemDate.equals(selectedDate)) {
                finalFilteredList.add(item);
            }
        }
        Log.d("@@Step", "Date Filtered size: " + finalFilteredList.size());

// Step 4: Group by Product
        Map<String, Itemmodel> productMap = new LinkedHashMap<>();

        for (Itemmodel item : finalFilteredList) {
            String product = item.getProduct();

            if (!productMap.containsKey(product)) {
                productMap.put(product, item); // First time adding
            } else {
                Itemmodel existing = productMap.get(product);
                existing.setAvlQty(existing.getAvlQty() + item.getAvlQty());
                existing.setMatchQty(existing.getMatchQty() + item.getMatchQty());
            }
        }

// Step 5: Set to RecyclerView
        itemmodelList = new ArrayList<>(productMap.values());
        DailyStockListAdapter adapter = new DailyStockListAdapter(this, itemmodelList, this, "product");
        b.rvDailyStock.setLayoutManager(new LinearLayoutManager(this));
        b.rvDailyStock.setAdapter(adapter);




    }

    @Override
    public void onCounterClick(String counterName, String name) {
       // Intent intent=new Intent(this,DailyStockReportProduct.class);
       // startActivity(intent);

    }
}
