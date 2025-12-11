package com.loyalstring.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.R;
import com.loyalstring.fsupporters.Itemdialog;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Viewholder> {

    private Map<String, Itemmodel> searchlist;
    Context context;
    Itemdialog itemdialog = new Itemdialog();

    public SearchAdapter(Context activity, HashMap<String, Itemmodel> searchitems) {

        this.context = activity;
        this.searchlist = searchitems;


    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_layout, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        List<String> itemKeys = new ArrayList<>(searchlist.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = searchlist.get(itemKey);

        holder.sno.setText(String.valueOf(holder.getAdapterPosition()) + 1);
        holder.barcode.setText(item.getBarCode());
        holder.itemcode.setText(item.getItemCode());

        double value = Double.parseDouble(item.getOperation());
        int progress = (int) value; // You may need to adjust this based on your value range
//        holder.progressBar.setProgress(progress);

        if (value < 50 && value > 0) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            holder.percentage.setText("100"+" "+value);
            holder.progressBar.setProgress(100);
        }
        else if(value > 50 && value < 60 ){
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
            holder.percentage.setText("90"+" "+value);
            holder.progressBar.setProgress(60);
        }
        else if(value > 60 && value < 70){
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
            holder.percentage.setText("70"+" "+value);
            holder.progressBar.setProgress(70);
        }
        else if(value > 70){
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.MAGENTA));
            holder.percentage.setText("50"+" "+value);
            holder.progressBar.setProgress(30);
        }
//        else if( value > 60 && value < 70){
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
//            holder.percentage.setText("50"+" "+value);
//            holder.progressBar.setProgress(50);
//        }
        else if(value == 0){
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
            holder.percentage.setText("0"+" "+value);
            holder.progressBar.setProgress(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click here

                itemdialog.showItemDetails(holder.getAdapterPosition(), context, item, "");

//                showItemDetails(holder.getAdapterPosition(), context);
            }
        });

//        else {
//            mainActivity.playSound(1);
//        }
//        if (value < 50) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
//        } else if (value < 80) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
//        } else {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
//        }


    }

    @Override
    public int getItemCount() {
        return searchlist.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView sno, barcode, itemcode, percentage;
        ProgressBar progressBar;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            sno = itemView.findViewById(R.id.sno);
            barcode = itemView.findViewById(R.id.barcode);
            itemcode = itemView.findViewById(R.id.itemcode);
            percentage = itemView.findViewById(R.id.percent);
            progressBar = itemView.findViewById(R.id.progressBar);


        }
    }

    private void showItemDetails(int position, Context itemView) {
        // Retrieve the item details based on the clicked position
        List<String> itemKeys = new ArrayList<>(searchlist.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = searchlist.get(itemKey);

        // Build and show the AlertDialog with item details
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView);
        builder.setTitle("Item Details");
        builder.setMessage(
                "Category: " + checknull(item.getCategory()) + "\n" +
                        "Product: " + checknull(item.getProduct()) + "\n" +
                        "Design: " + checknull(item.getDesignName()) + "\n" +
                        "Purity: " + checknull(item.getPurity()) + "\n" +
                        "Barcode: " + checknull(item.getBarCode()) + "\n" +
                        "Itemcode: " + checknull(item.getItemCode()) + "\n" +
                        "Box: " + checknull(item.getBox()) + "\n" +
                        "Gross weight: " + item.getGrossWt() + "\n" +
                        "Stone weight: " + item.getStoneWt() + "\n" +
                        "net weight: " + item.getNetWt());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the dialog if needed
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private String checknull(String s){
        if(s== null){
            return "";
        }
        return s;
    }


}
