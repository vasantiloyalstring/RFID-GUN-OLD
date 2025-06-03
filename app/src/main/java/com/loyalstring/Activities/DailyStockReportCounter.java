package com.loyalstring.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.Adapters.DailyStockCounterAdapter;
import com.loyalstring.Adapters.DailyStockListAdapter;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.database.support.Valuesdb;
import com.loyalstring.databinding.DailyStockReportCounterFragmentBinding;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.List;

public class DailyStockReportCounter extends AppCompatActivity implements CounterClickListener {
    DailyStockReportCounterFragmentBinding b;
    EntryDatabase entryDatabase;
    RecyclerView recyclerView;


    List<Itemmodel> itemmodelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DailyStockReportCounterFragmentBinding.inflate(getLayoutInflater());
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
        recyclerView = findViewById(R.id.rv_counter);

        Valuesdb db = new Valuesdb(this);
        db.getCounters();
        List<String> counterList = new ArrayList<>();
        counterList = db.getCounters();
        DailyStockCounterAdapter adapter = new DailyStockCounterAdapter(this, counterList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        itemmodelList = new ArrayList<>();
        try {
            itemmodelList = entryDatabase.getAllItems();


            DailyStockListAdapter DailyStockadapter = new DailyStockListAdapter(this, itemmodelList,this);
            b.rvDailyStock.setLayoutManager(new LinearLayoutManager(this));
            b.rvDailyStock.setAdapter(DailyStockadapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCounterClick(String counterName) {
        Intent intent = new Intent(this, DailyStockreportCategory.class);
        intent.putExtra("counter", counterName);
        startActivity(intent);

    }
}
