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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyStockListAdapter extends RecyclerView.Adapter<DailyStockListAdapter.ItemViewHolder> {

private final Context context;
private final List<Itemmodel> itemList;

private  CounterClickListener counterClickListener;
String displayMode;

public DailyStockListAdapter(Context context, List<Itemmodel> itemList, CounterClickListener  counterClickListener, String displayModedata) {
    this.context = context;
    this.itemList = itemList;
    this.counterClickListener=counterClickListener;
    this.displayMode=displayModedata;
}

public static class ItemViewHolder extends RecyclerView.ViewHolder {
    TextView tvDate, tvTotQuantity, tvMatchTotQty, tvUnmatchTotQty,tvName;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tvDate = itemView.findViewById(R.id.tv_date);
        tvTotQuantity = itemView.findViewById(R.id.tv_tot_qty);
        tvMatchTotQty = itemView.findViewById(R.id.tv_match_qty);
        tvUnmatchTotQty = itemView.findViewById(R.id.tv_um_qty);
        tvName=itemView.findViewById(R.id.tv_name);
    }
}

@NonNull
@Override
public DailyStockListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.daily_stock_item, parent, false);
    return new ItemViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull DailyStockListAdapter.ItemViewHolder holder, int position) {
    Itemmodel item = itemList.get(position);
    long timestamp = item.getEntryDate();
    Log.d("@@timestamp", "Entry timestamp: " + item);

    Log.d("@@timestamp", "Entry timestamp: " + timestamp);

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    String formattedDate = sdf.format(new Date(timestamp));
    Log.d("@@formattedDate", "Formatted Date: " + formattedDate);
    holder.tvDate.setText(formattedDate);
    holder.tvTotQuantity.setText(String.valueOf(item.getAvlQty()));
    holder.tvMatchTotQty.setText(String.valueOf(item.getMatchQty()));
    int unmatchedQty = (int) (item.getAvlQty() - item.getMatchQty());
    holder.tvUnmatchTotQty.setText(String.valueOf(unmatchedQty));
    Log.d("item.getCounterName()", "item.getCounterName(): " + item.getCounterName());
    String displayName = ""; // Store name based on display mode
    switch (displayMode.toLowerCase()) {
        case "category":
            displayName = item.getCategory();
            holder.tvName.setText(displayName);
            break;
        case "product":
            displayName = item.getProduct();
            holder.tvName.setText(displayName);
            break;
        case "counter":
            displayName = item.getCounterName();
            holder.tvName.setText(displayName);
            break;
    }

    String finalDisplayName = displayName;
    holder.itemView.setOnClickListener(view -> {
        counterClickListener.onCounterClick(formattedDate, finalDisplayName); // 👈 Pass both
    });

    if (position % 2 == 0) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
    } else {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.underlinecolor));
    }
}

@Override
public int getItemCount() {
    return itemList.size();
}
}
