package com.loyalstring.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.R;
import com.loyalstring.interfaces.CounterClickListener;
import com.loyalstring.modelclasses.Itemmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyStockListAdapter extends RecyclerView.Adapter<DailyStockListAdapter.ItemViewHolder> {

    private final Context context;
    private Map<String, Map<String, Map<String, List<Itemmodel>>>> itemList;
    private List<Itemmodel> flattenedList;
    private final CounterClickListener counterClickListener;
    private final String displayMode;

    // Constructor for nested grouped data (used in DailyStockReportCounter)
    public DailyStockListAdapter(Context context,
                                 Map<String, Map<String, Itemmodel>> formattedCounterData,
                                 CounterClickListener onCounterClick,
                                 String displayMode) {
        this.context = context;
        this.counterClickListener = onCounterClick;
        this.displayMode = displayMode;

        this.flattenedList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Itemmodel>> dateEntry : formattedCounterData.entrySet()) {
            String date = dateEntry.getKey();
            for (Map.Entry<String, Itemmodel> counterEntry : dateEntry.getValue().entrySet()) {
                Itemmodel item = counterEntry.getValue();
                item.setCounterName(counterEntry.getKey());
                try {
                    long dateMillis = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date).getTime();
                    item.setEntryDate(dateMillis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                flattenedList.add(item);
            }
        }
    }


    private List<Itemmodel> flattenFormattedData(Map<String, Map<String, Map<String, List<Itemmodel>>>> map) {
        List<Itemmodel> flatList = new java.util.ArrayList<>();

        for (Map.Entry<String, Map<String, Map<String, List<Itemmodel>>>> dateEntry : map.entrySet()) {
            String date = dateEntry.getKey();

            for (Map.Entry<String, Map<String, List<Itemmodel>>> counterEntry : dateEntry.getValue().entrySet()) {
                String counter = counterEntry.getKey();

                for (Map.Entry<String, List<Itemmodel>> categoryEntry : counterEntry.getValue().entrySet()) {
                    String category = categoryEntry.getKey();
                    List<Itemmodel> items = categoryEntry.getValue();

                    for (Itemmodel item : items) {
                        item.setCounterName(counter);
                        item.setCategory(category);
                        try {
                            long dateMillis = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date).getTime();
                            item.setEntryDate(dateMillis);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        flatList.add(item);
                    }
                }
            }
        }

        return flatList;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTotQuantity, tvMatchTotQty, tvUnmatchTotQty, tvName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotQuantity = itemView.findViewById(R.id.tv_tot_qty);
            tvMatchTotQty = itemView.findViewById(R.id.tv_match_qty);
            tvUnmatchTotQty = itemView.findViewById(R.id.tv_um_qty);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_stock_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Itemmodel item = flattenedList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(new Date(item.getEntryDate()));

        holder.tvDate.setText(formattedDate);
        holder.tvTotQuantity.setText(String.valueOf(item.getAvlQty()));
        holder.tvMatchTotQty.setText(String.valueOf(item.getMatchQty()));
        int unmatchedQty = (int) (item.getAvlQty() - item.getMatchQty());
        holder.tvUnmatchTotQty.setText(String.valueOf(unmatchedQty));

        String displayName = "";
        switch (displayMode.toLowerCase()) {
            case "category":
                displayName = item.getCategory();
                break;
            case "product":
                displayName = item.getProduct();
                break;
            case "counter":
                displayName = item.getCounterName();
                break;
        }
        holder.tvName.setText(displayName);

        String finalDisplayName = displayName;
        holder.itemView.setOnClickListener(view ->
                counterClickListener.onCounterClick(formattedDate, finalDisplayName)
        );

        holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, (position % 2 == 0) ? R.color.white : R.color.underlinecolor)
        );
    }

    @Override
    public int getItemCount() {
        return flattenedList != null ? flattenedList.size() : 0;
    }
}
