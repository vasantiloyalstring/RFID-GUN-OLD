package com.loyalstring.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.Adapters.DailyStockCounterAdapter;
import com.loyalstring.Adapters.DailyStockListAdapter;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.database.support.Valuesdb;

import com.loyalstring.databinding.DaliyStockReportProductActivityBinding;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.List;

public class DailyStockReportProduct extends AppCompatActivity implements CounterClickListener {
    DaliyStockReportProductActivityBinding b;
    EntryDatabase entryDatabase;
    RecyclerView recyclerView;
    List<Itemmodel> itemmodelList;



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

        Intent intent=getIntent();
        b.tvCounter.setText(intent.getStringExtra("counter"));
        b.tvCategory.setText(intent.getStringExtra("category"));

        Valuesdb db = new Valuesdb(this);
        db.getCounters();
        List<String> counterList=new ArrayList<>();
        counterList=db.getProductsByCategory(intent.getStringExtra("category"));
        DailyStockCounterAdapter adapter = new DailyStockCounterAdapter(this, counterList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        itemmodelList=new ArrayList<>();

        itemmodelList=entryDatabase.getAllItems();
        Log.d("@@","@@"+itemmodelList.get(0).getCounterName());

        DailyStockListAdapter DailyStockadapter = new DailyStockListAdapter(this, itemmodelList,this);
        b.rvDailyStock.setLayoutManager(new LinearLayoutManager(this));
        b.rvDailyStock.setAdapter(DailyStockadapter);


    }

    @Override
    public void onCounterClick(String counterName) {
       // Intent intent=new Intent(this,DailyStockReportProduct.class);
       // startActivity(intent);

    }
}
