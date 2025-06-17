package com.loyalstring.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loyalstring.Activities.DailyStockReportCounter;
import com.loyalstring.Activities.DailyStockreportCategory;
import com.loyalstring.Adapters.DailyStockListAdapter;
import com.loyalstring.MainActivity;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.DailyStockReportFragmentBinding;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyStockreportfragment extends Fragment implements CounterClickListener {
    MainActivity mainActivity;
    DailyStockReportFragmentBinding b;
    EntryDatabase entryDatabase;
    List<Itemmodel> itemmodelList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = DailyStockReportFragmentBinding.inflate(inflater, container, false);


        // Format the timestamp as a string
        mainActivity = (MainActivity) getActivity();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Daily Stock Report");
            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        entryDatabase = new EntryDatabase(getActivity());
        itemmodelList = new ArrayList<>();

        try {
            // Retrieve all items from the database
            itemmodelList = entryDatabase.getAllItems();

            // Date formatter for dd/MM/yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Map to store date string (dd/MM/yyyy) as key, and Itemmodel as accumulated value
            Map<String, Itemmodel> dateSummaryMap = new HashMap<>();

            for (Itemmodel item : itemmodelList) {

                Log.d("@@","@@"+item.getInventoryStatus());
                // Convert EntryDate (timestamp) to date string dd/MM/yyyy
                String dateKey = sdf.format(new Date(item.getEntryDate()));

                if (dateSummaryMap.containsKey(dateKey)) {
                    Itemmodel existing = dateSummaryMap.get(dateKey);
                    existing.setAvlQty(existing.getAvlQty() + item.getAvlQty());
                    existing.setMatchQty(existing.getMatchQty() + item.getMatchQty());
                } else {
                    Itemmodel summaryItem = new Itemmodel();
                    summaryItem.setEntryDate(item.getEntryDate()); // We keep original timestamp
                    summaryItem.setAvlQty(item.getAvlQty());
                    summaryItem.setMatchQty(item.getMatchQty());
                    dateSummaryMap.put(dateKey, summaryItem);
                }
            }

            // Convert map values to list
            List<Itemmodel> groupedList = new ArrayList<>(dateSummaryMap.values());

            // Optional: sort by EntryDate descending
            Collections.sort(groupedList, (o1, o2) -> Long.compare(o2.getEntryDate(), o1.getEntryDate()));

            // Optional: calculate UnmatchQty if you need
            for (Itemmodel item : groupedList) {
                double unmatchQty = item.getAvlQty() - item.getMatchQty();
                //item.setUnmatchQty(unmatchQty);
            }

            // Show in RecyclerView if data exists
            if (!groupedList.isEmpty()) {
                DailyStockListAdapter adapter = new DailyStockListAdapter(getContext(), groupedList, this::onCounterClick, "counter");
                b.rvDailyStock.setLayoutManager(new LinearLayoutManager(getActivity()));
                b.rvDailyStock.setAdapter(adapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }





      /*  b.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), DailyStockReportDate.class);
                startActivity(intent);

            }
        });*/
    }

    @Override
    public void onCounterClick(String dateData,String string) {
        for(int i=0; i<itemmodelList.size(); i++) {
            if(itemmodelList.get(i).getCounterName()=="") {
                Intent intent = new Intent(getActivity(), DailyStockreportCategory.class);
                startActivity(intent);
                break;
            }else {

                Intent intent = new Intent(getActivity(), DailyStockReportCounter.class);
                intent.putExtra("dateData",dateData);
                startActivity(intent);
                break;

            }
        }

    }
}
