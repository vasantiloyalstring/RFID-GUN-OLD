package com.loyalstring.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loyalstring.Adapters.DailyStockListAdapter;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.DailyStockReportDateBinding;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.List;

public class DailyStockReportDate  extends AppCompatActivity  implements CounterClickListener {

    DailyStockReportDateBinding b;


    EntryDatabase entryDatabase;
    List<Itemmodel> itemmodelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b  = DailyStockReportDateBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

      Toolbar toolbar = findViewById(R.id.toolbar_date);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daily Stock Report"); // Set title
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }
        // If you are using a custom toolbar, set it as ActionBar
      //  setSupportActionBar(b.toolbar);

        entryDatabase = new EntryDatabase(this);
        itemmodelList=new ArrayList<>();
        initdata();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // or use onBackPressed();
        return true;
    }

    private void initdata() {

        try {

            itemmodelList = entryDatabase.getAllItems();
            if (itemmodelList != null || itemmodelList.size() > 0) {


                DailyStockListAdapter adapter = new DailyStockListAdapter(DailyStockReportDate.this, itemmodelList,this, "counter");
                b.rvDailyStock.setLayoutManager(new LinearLayoutManager(this));
                b.rvDailyStock.setAdapter(adapter);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }



        b.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DailyStockReportDate.this, DailyStockReportCounter.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onCounterClick(String counterName,String date) {
        Intent intent=new Intent(DailyStockReportDate.this, DailyStockReportCounter.class);
        startActivity(intent);
    }
}
