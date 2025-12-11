package com.loyalstring.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.R;
import com.loyalstring.fsupporters.Itemdialog;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Listadapter extends RecyclerView.Adapter<Listadapter.Viewholder> {
    private Map<String, Itemmodel> itemList;
    private Context context;
    private EditClickListener editClickListener;
    private DeleteClickListener deleteClickListener;
    Itemdialog itemdialog = new Itemdialog();


    public interface EditClickListener {
        void onEditClick(int position, String tidValue, Itemmodel item, Map<String, Itemmodel> itemList);
    }

    public interface DeleteClickListener {
        void onDeleteClick(int position, String tidValue, Itemmodel item, Map<String, Itemmodel> itemList);
    }

    public Listadapter(Map<String, Itemmodel> itemList, Context context, EditClickListener editClickListener, DeleteClickListener deleteClickListener) {
        this.itemList = itemList;
        this.context = context;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listrecycler_layout, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Log.d("litadapt ", "  "+itemList.size());
        Collection<Itemmodel> items = itemList.values();

        List<Itemmodel> itemListAsList = new ArrayList<>(items);

        // Make sure the position is within the bounds
        if (position >= 0 && position < itemListAsList.size()) {
            Itemmodel item = itemListAsList.get(position);

            // Now you can use the retrieved item as before
            holder.lproduct.setText(item.getProduct());
            holder.lbarcode.setText(item.getBarCode());
            holder.litemcode.setText(item.getItemCode());
            holder.lgwt.setText(String.valueOf(item.getGrossWt()));

            holder.ldelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (deleteClickListener != null) {
                        deleteClickListener.onDeleteClick(position, item.getTidValue(), item, itemList);
                    }
//                SingleEntryDbHelper dbHelper = new SingleEntryDbHelper(context);
//                                        boolean tagDeleted = dbHelper.deletefinal1(item.getTidValue());
//                if (tagDeleted) {
//                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
//                    itemList.remove(item);
//                    notifyDataSetChanged();
//                } else {
//                    Toast.makeText(context, "failed to deleted item", Toast.LENGTH_SHORT).show();
//                }

//                notifyDataSetChanged();
                }
            });

            holder.ledit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editClickListener != null) {
                        editClickListener.onEditClick(position, item.getTidValue(), item, itemList);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click here
                    itemdialog.showItemDetails(holder.getAdapterPosition(), context, item, "");
//                    showItemDetails(holder.getAdapterPosition(), context);
                }
            });

        }
    }

    private void showItemDetails(int position, Context itemView) {
        // Retrieve the item details based on the clicked position
        List<String> itemKeys = new ArrayList<>(itemList.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = itemList.get(itemKey);

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

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {

        public TextView lproduct, lbarcode, litemcode, lgwt;
        public ImageView ledit, ldelete;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            lproduct = itemView.findViewById(R.id.lproduct);
            lbarcode = itemView.findViewById(R.id.lbarcode);
            litemcode = itemView.findViewById(R.id.litemcode);
            lgwt = itemView.findViewById(R.id.lgwt);
            ledit = itemView.findViewById(R.id.ledit);
            ldelete = itemView.findViewById(R.id.ldelete);
        }
    }
}
