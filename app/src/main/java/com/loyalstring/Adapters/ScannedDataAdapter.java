package com.loyalstring.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.R;
import com.loyalstring.modelclasses.ScannedDataToService;

import java.util.List;

public class ScannedDataAdapter extends RecyclerView.Adapter<ScannedDataAdapter.ViewHolder> {

    private final List<ScannedDataToService> list;

    public ScannedDataAdapter(List<ScannedDataToService> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSrNo, tvEpc, tvProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSrNo = itemView.findViewById(R.id.tv_sr_no);
            tvEpc = itemView.findViewById(R.id.tv_epc);
            tvProduct = itemView.findViewById(R.id.tv_product);
        }
    }

    @NonNull
    @Override
    public ScannedDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scanned_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScannedDataToService item = list.get(position);
        holder.tvSrNo.setText(String.valueOf(position + 1));
        holder.tvEpc.setText(item.getTIDValue());
        holder.tvProduct.setText(item.getRFIDCode());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
