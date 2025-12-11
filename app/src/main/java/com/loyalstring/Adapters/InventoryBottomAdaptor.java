package com.loyalstring.Adapters;

import static com.loyalstring.MainActivity.decimalFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.loyalstring.R;
import com.loyalstring.fsupporters.Itemdialog;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.network.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryBottomAdaptor extends RecyclerView.Adapter<InventoryBottomAdaptor.Viewholder> {

    private Map<String, Itemmodel> bottomlist;
    private Context context;
    Itemmodel m;
    String viewitem;
    int matchedCounter = 0;
    int unmatchedCounter = 0;
    NetworkUtils networkUtils;
    Itemdialog itemdialog = new Itemdialog();


    public InventoryBottomAdaptor(HashMap<String, Itemmodel> bottomlist, Context activity, Itemmodel o, String all) {

        this.bottomlist = bottomlist;
        this.context = activity;
        this.m = o;
        this.viewitem = all;
        this.networkUtils = new NetworkUtils(activity);
    }

    public void updatedata(HashMap<String, Itemmodel> bottommatch) {
        this.bottomlist = bottommatch;
    }

    public void updateview(String st){
        this.viewitem = st;
    }

    @NonNull
    @Override
    public InventoryBottomAdaptor.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_list_layout, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryBottomAdaptor.Viewholder holder, int position) {


        List<String> itemKeys = new ArrayList<>(bottomlist.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = bottomlist.get(itemKey);


        if (viewitem.equalsIgnoreCase("all")) {
            holder.sno.setText(String.valueOf(holder.getAdapterPosition() + 1));
            holder.product.setText(item.getProduct());
            holder.barcode.setText(item.getBarCode());
            holder.itemcode.setText(item.getItemCode());
            holder.tgwt.setText(String.valueOf(decimalFormat.format(item.getGrossWt())));
//            holder.inwt.setText(String.valueOf(item.getNetWt()));
//            holder.ipurity.setText(item.getPurity());
//            holder.ibox.setText(item.getBox());
            Log.d("check inve  1", "  " + item.getAvlQty() + "  " + item.getMatchQty());
            if (item.getAvlQty() == item.getMatchQty()) {
                holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.i_success));
            } else {
                holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.i_cross));
            }

        } else if (viewitem.equalsIgnoreCase("matched")) {

            if (item.getAvlQty() == item.getMatchQty()) {
                holder.sno.setText(String.valueOf(++matchedCounter));
                holder.product.setText(item.getProduct());
                holder.barcode.setText(item.getBarCode());
                holder.itemcode.setText(item.getItemCode());
                holder.tgwt.setText(String.valueOf(decimalFormat.format(item.getGrossWt())));
                holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.i_success));
            } else {
                // Hide unmatched items
                holder.itemholder.setVisibility(View.GONE);

            }
        } else if (viewitem.equalsIgnoreCase("unmatched")) {
            if (item.getAvlQty() != item.getMatchQty()) {
                holder.sno.setText(String.valueOf(++unmatchedCounter));
                holder.product.setText(item.getProduct());
                holder.barcode.setText(item.getBarCode());
                holder.itemcode.setText(item.getItemCode());
                holder.tgwt.setText(String.valueOf(decimalFormat.format(item.getGrossWt())));
                holder.status.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.i_cross));
            } else {
                holder.itemholder.setVisibility(View.GONE);
            }
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
        return bottomlist.size();// Math.min(batchSize, bottomlist.size() - (currentPage * batchSize));
    }




    public class Viewholder extends RecyclerView.ViewHolder {
        TextView sno, product, box, tqty, mqty, tgwt, mgwtm, barcode, itemcode;
        ImageView status;
        RelativeLayout itemholder;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            itemholder = itemView.findViewById(R.id.bottomitemlay);
            sno = itemView.findViewById(R.id.isno);
            product = itemView.findViewById(R.id.iproduct);
            barcode = itemView.findViewById(R.id.checkb1text);
            itemcode = itemView.findViewById(R.id.checkb2text);

//            box = itemView.findViewById(R.id.checkb1text);
//            tqty = itemView.findViewById(R.id.checkb2text);
//            mqty = itemView.findViewById(R.id.checkb3text);
            tgwt = itemView.findViewById(R.id.checkb3text);
//            mgwt = itemView.findViewById(R.id.checkb5text);
            status = itemView.findViewById(R.id.istatus);
        }
    }


    private void showItemDetails(int position, Context context) {
        // Retrieve the item details based on the clicked position
        List<String> itemKeys = new ArrayList<>(bottomlist.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = bottomlist.get(itemKey);

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
                        "Pieces: " + checknull(item.getPcs()) + "\n" +
                        "Gross weight: " + item.getGrossWt() + "\n" +
                        "Stone weight: " + item.getStoneWt() + "\n" +
                        "Net weight: " + item.getNetWt());
        String basePath = getExternalStoragePath(context);
        // Load the image into the ImageView
        // Here you need to replace `item.getImageUrl()` with the actual method to get the image URL or resource ID

        String onlineimage = item.getImageUrl();
        String iname = item.getItemCode();


        String imageUrl = item.getItemCode()+".jpg"; // Assuming you have a method to get the image URL
        Log.e("loadimage", ""+imageUrl);

        if(onlineimage != null && !onlineimage.isEmpty()){
            Glide.with(context)
                    .load(onlineimage)
                    .placeholder(R.drawable.logo) // Optional: a placeholder image while loading
                    .error(R.drawable.logo) // Optional: an error image if loading fails
                    .into(itemImage);
        }else{
            itemImage.setImageResource(R.drawable.logo);
        }


//        File imageFile = new File(context.getExternalFilesDir(null), "Documents/Loyalstring files/images/" + imageUrl);

        /*File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/Loyalstring files/images/"+imageUrl);


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
        }*/



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

    /*private void showItemDetails(int position, Context itemView) {
        // Retrieve the item details based on the clicked position
        List<String> itemKeys = new ArrayList<>(bottomlist.keySet());
        String itemKey = itemKeys.get(position);
        Itemmodel item = bottomlist.get(itemKey);

        // Build and show the AlertDialog with item details
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView);
        builder.setTitle("Item Details");
        builder.setMessage(
                "Category: " + checknull(item.getCategory()) + "\n" +
                        "Product: " + checknull(item.getProduct()) + "\n" +
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
    }*/

    private String checknull(String s){
        if(s== null){
            return "";
        }
        return s;
    }

//    private boolean fileExists(String filePath) {
//        File file = new File(filePath);
//        return file.exists();
//    }

    /*private boolean checkIfFileExists(String zipName *//*ex: fileName.zip*//*) {
        File file = new File(Environment.getExternalStorageDirectory(), "Documents/Loyalstring files/images/" + zipName);
        if(file.exists()) {
//            Toast.makeText(this, "Exists", Toast.LENGTH_SHORT).show();
            return true;
        } else {
//            Toast.makeText(this, "Doesn't exist", Toast.LENGTH_SHORT).show();
            return false;
        }
    }*/


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
