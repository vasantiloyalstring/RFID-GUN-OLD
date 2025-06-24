package com.loyalstring.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.R;
import com.loyalstring.modelclasses.Itemmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommonStockAdapter extends RecyclerView.Adapter<CommonStockAdapter.ViewHolder> {

    public void updateGroupedData(Map<String, List<Itemmodel>> data) {
        this.groupedData = data;
    }
    public enum LevelType {
        DATE, COUNTER, CATEGORY, PRODUCT
    }

    public interface OnItemClickListener {
        void onItemClick(LevelType level, Object data);
    }

    private final Context context;
    private OnItemClickListener listener;
    private List<Object> displayList;
    private LevelType currentLevel;

    private Map<String, List<Itemmodel>> groupedData = new LinkedHashMap<>();


    public CommonStockAdapter(Context context, List<Object> initialList, LevelType level,
                              Map<String, List<Itemmodel>> groupedData, OnItemClickListener listener) {
        this.context = context;
        this.displayList = initialList;
        this.currentLevel = level;
        this.groupedData = groupedData;
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<Object> newList, LevelType newLevel, Map<String, List<Itemmodel>> newDataMap) {
        this.displayList = newList;
        this.currentLevel = newLevel;
        this.groupedData = newDataMap != null ? newDataMap : new LinkedHashMap<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvTotQty, tvMatchQty, tvUnmatchQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotQty = itemView.findViewById(R.id.tv_tot_qty);
            tvMatchQty = itemView.findViewById(R.id.tv_match_qty);
            tvUnmatchQty = itemView.findViewById(R.id.tv_um_qty);
        }
    }

    @NonNull
    @Override
    public CommonStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_stock_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonStockAdapter.ViewHolder holder, int position) {
        Object item = displayList.get(position);

        // Reset views
      //  holder.tvDate.setVisibility(View.VISIBLE);
        holder.tvTotQty.setVisibility(View.VISIBLE);
        holder.tvMatchQty.setVisibility(View.VISIBLE);
        holder.tvUnmatchQty.setVisibility(View.VISIBLE);

        String name = "";
        String dateStr = "";
        double totQty = 0;
        double matchQty = 0;
        double unmatchQty = 0;

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.underlinecolor));  // Gray color for even rows
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));  // White color for odd rows
        }

        switch (currentLevel) {
            case DATE:
                name = (String) item;
                List<Itemmodel> dateItems = groupedData.get(name);
                if (dateItems != null) {
                    for (Itemmodel i : dateItems) {
                        totQty += i.getAvlQty();
                        matchQty += i.getMatchQty();
                    }
                }
                dateStr = name;
                break;

            case COUNTER:
            case CATEGORY:
                name = (String) item;
                List<Itemmodel> groupItems = groupedData.get(name);
                if (groupItems != null && !groupItems.isEmpty()) {
                    long minDate = groupItems.get(0).getEntryDate();
                    for (Itemmodel i : groupItems) {
                        totQty += i.getAvlQty();
                        matchQty += i.getMatchQty();
                        if (i.getEntryDate() < minDate) {
                            minDate = i.getEntryDate();
                        }
                    }
                    dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date(minDate));
                }
                break;

            case PRODUCT:
                Itemmodel model = (Itemmodel) item;
                name = model.getProduct();
                totQty = model.getAvlQty();
                matchQty = model.getMatchQty();
                dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date(model.getEntryDate()));
                break;
        }

        unmatchQty = totQty - matchQty;

        holder.tvName.setText(name);
       // holder.tvDate.setText(dateStr);
        holder.tvTotQty.setText(String.valueOf(totQty));
        holder.tvMatchQty.setText(String.valueOf(matchQty));
        holder.tvUnmatchQty.setText(String.valueOf(unmatchQty));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && item != null) {
                listener.onItemClick(currentLevel, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayList != null ? displayList.size() : 0;
    }
    public List<Object> getDisplayList() {
        return displayList;
    }

    public Map<String, List<Itemmodel>> getGroupedData() {
        return groupedData;
    }
    public LevelType getCurrentLevel() {
        return currentLevel;
    }
    // Optionally keep groupBy() method here
    public static Map<String, List<Itemmodel>> groupBy(List<Itemmodel> items, String type) {
        Map<String, List<Itemmodel>> map = new LinkedHashMap<>();
        for (Itemmodel item : items) {
            String key = "";
            if ("date".equals(type)) {
                key = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date(item.getEntryDate()));
            } else if ("counter".equals(type)) {
                key = item.getCounterName();
            } else if ("category".equals(type)) {
                key = item.getCategory();
            }

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(item);
        }
        return map;
    }
}
