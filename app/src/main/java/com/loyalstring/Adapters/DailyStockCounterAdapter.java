package com.loyalstring.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.R;
import com.loyalstring.interfaces.CounterClickListener;

import java.util.List;

public class DailyStockCounterAdapter extends RecyclerView.Adapter<DailyStockCounterAdapter.StringViewHolder> {

    private Context context;
    private List<String> stringList;
    private CounterClickListener listener;



    public DailyStockCounterAdapter(Context context, List<String> counterList, CounterClickListener listener) {
        this.context = context;
        this.stringList = counterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_stock_report_counter_item, parent, false);
        return new StringViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        String item = stringList.get(position);
        holder.textView.setText(item);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCounterClick(item,"category");
            }
        });
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public static class StringViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public StringViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_counter);  // using built-in layout
        }
    }
}
