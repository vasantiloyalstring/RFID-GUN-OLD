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
import com.loyalstring.Activities.DailyStockReportDate;
import com.loyalstring.Adapters.DailyStockCounterAdapter;
import com.loyalstring.Adapters.DailyStockListAdapter;
import com.loyalstring.MainActivity;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.DailyStockReportFragmentBinding;
import com.loyalstring.databinding.FragmentStockreportfragmentBinding;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        itemmodelList=new ArrayList<>();
        try {

            itemmodelList = entryDatabase.getAllItems();

            List<Itemmodel> groupedList = new ArrayList<>();
            Map<Long, int[]> dateQtyMap = new HashMap<>();

            for (Itemmodel item : itemmodelList) {
                long dateKey = item.getEntryDate();
                double totalQty = item.getAvlQty();
                double matchQty = item.getMatchQty();

                if (!dateQtyMap.containsKey(dateKey)) {
                    dateQtyMap.put(dateKey, new int[]{0, 0});
                }

                int[] qtySums = dateQtyMap.get(dateKey);
                qtySums[0] += totalQty;
                qtySums[1] += matchQty;
            }

// Now convert to Itemmodel list
            for (Map.Entry<Long, int[]> entry : dateQtyMap.entrySet()) {
                Itemmodel summaryItem = new Itemmodel();
                summaryItem.setEntryDate(entry.getKey());
                summaryItem.setAvlQty(entry.getValue()[0]);
                summaryItem.setMatchQty(entry.getValue()[1]);
                groupedList.add(summaryItem);
            }
            if (groupedList != null || groupedList.size() > 0) {


                DailyStockListAdapter adapter = new DailyStockListAdapter(getContext(), groupedList,this::onCounterClick);
                b.rvDailyStock.setLayoutManager(new LinearLayoutManager(getActivity()));
                b.rvDailyStock.setAdapter(adapter);
            }
        }catch (Exception e)
        {
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
    public void onCounterClick(String counterName) {
        Intent intent=new Intent(getActivity(), DailyStockReportDate.class);
        startActivity(intent);

    }
}
