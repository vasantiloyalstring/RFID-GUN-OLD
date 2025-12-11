package com.loyalstring.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.loyalstring.R;
import com.loyalstring.fsupporters.Itemdialog;
import com.loyalstring.modelclasses.Itemmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillViewAdapter extends RecyclerView.Adapter<BillViewAdapter.Viewholder> {

    private Map<String, Itemmodel> searchlist;
    Context context;
    private Removeitem removeitem;
    Itemdialog itemdialog = new Itemdialog();
    public BillViewAdapter(FragmentActivity activity, HashMap<String, Itemmodel> searchitems) {
        this.context = activity;
        this.searchlist = searchitems;
        this.removeitem = removeitem;
    }

    public interface Removeitem{
        void onRemoveitem(Itemmodel i, int position);
    }

    @NonNull
    @Override
    public BillViewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_view_layout, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        List<String> itemKeys = new ArrayList<>(searchlist.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = searchlist.get(itemKey);

        holder.sno.setText(String.valueOf(position+1));
        holder.product.setText(String.valueOf(item.getProduct()));
        holder.barcode.setText(String.valueOf(item.getBarCode()));
        holder.gwt.setText(String.valueOf(item.getGrossWt()));
        holder.itemcode.setText(item.getItemCode());
        holder.netwt.setText(String.valueOf(item.getNetWt()));
        String imageUrl = item.getItemCode()+".jpg";
        File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/Loyalstring files/images/"+imageUrl);
        if (checkIfFileExists(imageFile)) {
            Toast.makeText(context, "image found", Toast.LENGTH_SHORT).show();
//            Log.e("loadimage", "File exists: " + imageFile.getAbsolutePath());
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.drawable.logo) // Optional: a placeholder image while loading
                    .error(R.drawable.logo) // Optional: an error image if loading fails
                    .into(holder.image);
        } else {
            Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
//            Log.e("loadimage", "File does not exist: " + imagePath);
            holder.image.setImageResource(R.drawable.logo); // Set a default image if file does not exist
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click here
                itemdialog.showItemDetails(holder.getAdapterPosition(), context, item, "");
//                showItemDetails(holder.getAdapterPosition(), context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchlist.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView sno, product, barcode, gwt, itemcode, netwt;
        ImageView image;
        RelativeLayout remove;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            sno = itemView.findViewById(R.id.sno);
            product = itemView.findViewById(R.id.product);
            barcode = itemView.findViewById(R.id.barcode);
            gwt = itemView.findViewById(R.id.gwt);
            itemcode = itemView.findViewById(R.id.itemcode);
            netwt = itemView.findViewById(R.id.netwt);
            image = itemView.findViewById(R.id.image);
//            remove = itemView.findViewById(R.id.itemremove);

        }
    }

    private void showItemDetails(int position, Context context) {
        // Retrieve the item details based on the clicked position
        List<String> itemKeys = new ArrayList<>(searchlist.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = searchlist.get(itemKey);

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_item_details, null);

        // Get references to the views in the custom layout
        ImageView itemImage = dialogView.findViewById(R.id.item_image);
        TextView itemDetails = dialogView.findViewById(R.id.item_details);

        // Set the item details
        itemDetails.setText(
                "Category: " + checknull(item.getCategory()) + "\n" +
                        "Product: " + checknull(item.getProduct()) + "\n" +
                        "Design: " + checknull(item.getDesignName()) + "\n" +
                        "Purity: " + checknull(item.getPurity()) + "\n" +
                        "Barcode: " + checknull(item.getBarCode()) + "\n" +
                        "Itemcode: " + checknull(item.getItemCode()) + "\n" +
                        "Box: " + checknull(item.getBox()) + "\n" +
                        "Gross weight: " + item.getGrossWt() + "\n" +
                        "Stone weight: " + item.getStoneWt() + "\n" +
                        "Net weight: " + item.getNetWt());
        String basePath = getExternalStoragePath(context);
        // Load the image into the ImageView
        // Here you need to replace `item.getImageUrl()` with the actual method to get the image URL or resource ID
        String imageUrl = item.getItemCode()+".jpg"; // Assuming you have a method to get the image URL
        Log.e("loadimage", ""+imageUrl);


//        File imageFile = new File(context.getExternalFilesDir(null), "Documents/Loyalstring files/images/" + imageUrl);

        File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/Loyalstring files/images/"+imageUrl);


        if (checkIfFileExists(imageFile)) {
            Toast.makeText(context, "image found", Toast.LENGTH_SHORT).show();
//            Log.e("loadimage", "File exists: " + imageFile.getAbsolutePath());
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.drawable.logo) // Optional: a placeholder image while loading
                    .error(R.drawable.logo) // Optional: an error image if loading fails
                    .into(itemImage);
        } else {
            Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
//            Log.e("loadimage", "File does not exist: " + imagePath);
            itemImage.setImageResource(R.drawable.logo); // Set a default image if file does not exist
        }

        /*if (checkIfFileExists(imageUrl)) {
            Toast.makeText(context, "image found", Toast.LENGTH_SHORT).show();
            Log.e("loadimage", "File exist: " + imageUrl);
            Glide.with(context)
                    .load(new File(Environment.getExternalStorageDirectory(), "Documents/Loyalstring files/images/"+imageUrl))
                    .placeholder(R.drawable.logo) // Optional: a placeholder image while loading
                    .error(R.drawable.logo) // Optional: an error image if loading fails
                    .into(itemImage);
        } else {
            Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
            Log.e("loadimage", "File does not exist: " + imageUrl);
            itemImage.setImageResource(R.drawable.logo); // Set a default image if file does not exist
        }*/


        /*Glide.with(context)
                .load(new File(imageUrl))
                .placeholder(R.drawable.logo) // Optional: a placeholder image while loading
                .error(R.drawable.logo) // Optional: an error image if loading fails
                .into(itemImage);*/

        // Build and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Item Details")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    private boolean checkIfFileExists(File file) {
        if (file.exists()) {
            Log.d("FileCheck", "File exists: " + file.getAbsolutePath());
            return true;
        } else {
            Log.d("FileCheck", "File does not exist: " + file.getAbsolutePath());
            return false;
        }
    }
    private static String getExternalStoragePath(Context context) {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        return primaryExternalStorage.getAbsolutePath();
    }

}
