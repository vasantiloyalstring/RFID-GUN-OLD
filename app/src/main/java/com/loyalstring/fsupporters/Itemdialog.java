package com.loyalstring.fsupporters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.loyalstring.R;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Itemmodel;

import java.io.File;

public class Itemdialog {

    @SuppressLint("MissingInflatedId")
    public void showItemDetails(int position, Context context, Itemmodel item, String type) {
        // Retrieve the item details based on the clicked position
//        List<String> itemKeys = new ArrayList<>(bottomlist.keySet());
//        String itemKey = itemKeys.get(position);
//        Itemmodel item = bottomlist.get(itemKey);

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_item_details, null);

        // Get references to the views in the custom layout
        ImageView itemImage = dialogView.findViewById(R.id.item_image);
        TextView itemDetails = dialogView.findViewById(R.id.item_details);

        EditText grossWtEdit = dialogView.findViewById(R.id.edit_grosswt);
        EditText stoneWtEdit = dialogView.findViewById(R.id.edit_stonewt);
        EditText netWtEdit = dialogView.findViewById(R.id.edit_netwt);
        EditText notesEdit = dialogView.findViewById(R.id.edit_notes);


        TextView tvGrWt = dialogView.findViewById(R.id.tv_gr_wt);
        TextView tvStoneWt = dialogView.findViewById(R.id.tv_stone_wt);
        TextView tvNetWt = dialogView.findViewById(R.id.tv_net_wt);


        Gson gson = new Gson();
        String json = gson.toJson(item);

        Log.d("@@", "@@ item details" + item);

        // Set the item details
        itemDetails.setText(
                "Date: " + checknull(item.getDiamondMetal()) + "\n" +
                "Category: " + checknull(item.getCategory()) + "\n" +
                        "Product: " + checknull(item.getProduct()) + "\n" +
                        "from: " + checknull(item.getDiamondClarity()) + "\n" +
                        "to: " + checknull(item.getDiamondColor()) + "\n" +
                        "Purity: " + checknull(item.getPurity()) + "\n" +
                        "Barcode: " + checknull(item.getBarCode()) + "\n" +
                        "Itemcode: " + checknull(item.getItemCode()) + "\n" +
                        "Box: " + checknull(item.getBox()) + "\n" +
                        "Pieces: " + checknull(item.getPcs()) + "\n" +
                        "Product Code: " + checknull(item.getProductCode()) + "\n" +
                        "Gross weight: " + item.getGrossWt() + "\n" +
                        "Stone weight: " + item.getStoneWt() + "\n" +
                        "Net weight: " + item.getNetWt());

        grossWtEdit.setText(String.valueOf(item.getGrossWt()));
        stoneWtEdit.setText(String.valueOf(item.getStoneWt()));
        netWtEdit.setText(String.valueOf(item.getNetWt()));
        notesEdit.setText(checknull(item.getDescription())); // Assuming you have a method to get the notes

        // Show or hide the edit fields based on the type
        if ("order".equals(type)) {
            grossWtEdit.setVisibility(View.VISIBLE);
            stoneWtEdit.setVisibility(View.VISIBLE);
            netWtEdit.setVisibility(View.VISIBLE);
            notesEdit.setVisibility(View.VISIBLE);
            tvGrWt.setVisibility(View.VISIBLE);
            tvStoneWt.setVisibility(View.VISIBLE);
            tvNetWt.setVisibility(View.VISIBLE);

        } else {
            grossWtEdit.setVisibility(View.GONE);
            stoneWtEdit.setVisibility(View.GONE);
            netWtEdit.setVisibility(View.GONE);
            notesEdit.setVisibility(View.GONE);
            tvGrWt.setVisibility(View.GONE);
            tvStoneWt.setVisibility(View.GONE);
            tvNetWt.setVisibility(View.GONE);
        }



        String basePath = getExternalStoragePath(context);
        // Load the image into the ImageView
        // Here you need to replace `item.getImageUrl()` with the actual method to get the image URL or resource ID

        String imageUrlString = item.getImageUrl(); // e.g., "img1.jpg,img2.jpg,img3.jpg"
        String onlineimage="";
        if (imageUrlString != null && !imageUrlString.isEmpty()) {
            String[] imageUrls = imageUrlString.split(",");
            String lastImage = imageUrls[imageUrls.length - 1].trim(); // get last and trim spaces
            onlineimage = "https://rrgold.loyalstring.co.in/" + lastImage;
            // Use `onlineImage` as needed
        } else {
            // fallback or placeholder
            onlineimage = "https://rrgold.loyalstring.co.in/default.jpg";
        }


        String iname = item.getItemCode();

        Log.d("IMAGE URL",onlineimage);


        String imageUrl = iname + ".jpg"; // Assuming you have a method to get the image URL
        Log.e("loadimage", "" + imageUrl);

        if (!onlineimage.isEmpty()) {
            Glide.with(context)
                    .load(onlineimage)
                    .placeholder(R.drawable.logo) // Optional: a placeholder image while loading
                    .error(R.drawable.logo) // Optional: an error image if loading fails
                    .into(itemImage);
        }
        else {

            if (item.getItemCode() != null && !item.getItemCode().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files/images");

                File imageFile = new File(directory, imageUrl);

                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if (created) {
                        Log.d("DirectoryCheck", "Directory created: " + directory.getAbsolutePath());
                    } else {
                        Log.d("DirectoryCheck", "Failed to create directory: " + directory.getAbsolutePath());
                    }
                } else {
                    Log.d("DirectoryCheck", "Directory exists: " + directory.getAbsolutePath());
                }


                //  File imageFile = new File(directory, imageUrl);
                String imagePath = imageFile.getAbsolutePath(); // Full file path

                if (!imageFile.exists()) {
                    // Fallback: check app-specific directory
                    File fallbackDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files/images");
                    imageFile = new File(fallbackDirectory, item.getProductCode() + ".jpg");
                    imagePath = imageFile.getAbsolutePath();
                }
                Uri imageUri = getImageUriFromFilePath(imagePath);


                if (checkIfFileExists(imageFile, directory)) {
                    if (imageUri != null) {
                        Glide.with(context)
                                .load(imageUri)
                                .into(itemImage);
                    } else {
                        Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();

                        itemImage.setImageResource(R.drawable.logo); // Set default if not found
                    }
                }
             /*   if (checkIfFileExists(imageFile)) {
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
            } else {
                itemImage.setImageResource(R.drawable.logo);
            }
        }
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
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public Uri getImageUriFromFilePath(String filePath) {
        return Uri.parse("file://" + filePath); // Convert path to URI
    }

    public Uri getImageUriFromMediaStore(Context context, String fileName) {
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{fileName};

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                return ContentUris.withAppendedId(collection, id);
            }
        }
        return null;
    }


    private String checknull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private static String getExternalStoragePath(Context context) {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        return primaryExternalStorage.getAbsolutePath();
    }

    private boolean checkIfFileExists(File file, File directory) {
        if (file.exists()) {
            Log.d("FileCheck", "File exists: " + file.getAbsolutePath());
            return true;
        } else {
            Log.d("FileCheck", "File does not exist: " + file.getAbsolutePath());

            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    Log.d("FilesInDir", "Found file: " + f.getName());
                }
            }
            return false;
        }
    }


    public void showItemDetails1(int position, Context context, Itemmodel item, String type , interfaces.ItemUpdateListener listener) {
        // Retrieve the item details based on the clicked position
//        List<String> itemKeys = new ArrayList<>(bottomlist.keySet());
//        String itemKey = itemKeys.get(position);
//        Itemmodel item = bottomlist.get(itemKey);

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_item_details, null);

        // Get references to the views in the custom layout
        ImageView itemImage = dialogView.findViewById(R.id.item_image);
        TextView itemDetails = dialogView.findViewById(R.id.item_details);

        EditText grossWtEdit = dialogView.findViewById(R.id.edit_grosswt);
        EditText stoneWtEdit = dialogView.findViewById(R.id.edit_stonewt);
        EditText netWtEdit = dialogView.findViewById(R.id.edit_netwt);
        EditText notesEdit = dialogView.findViewById(R.id.edit_notes);

        TextView tvGrWt = dialogView.findViewById(R.id.tv_gr_wt);
        TextView tvStoneWt = dialogView.findViewById(R.id.tv_stone_wt);
        TextView tvNetWt = dialogView.findViewById(R.id.tv_net_wt);




        // Set the item details
        itemDetails.setText(
                "Category: " + checknull(item.getCategory()) + "\n" +
                        "Product: " + checknull(item.getProduct()) + "\n" +
                        "Purity: " + checknull(item.getPurity()) + "\n" +
                        "Barcode: " + checknull(item.getBarCode()) + "\n" +
                        "Itemcode: " + checknull(item.getItemCode()) + "\n" +
                        "Box: " + checknull(item.getBox()) + "\n" +
                        "Pieces: " + checknull(item.getPcs()) + "\n" +
                        "Product Code: " + checknull(item.getProductCode()) + "\n" +
                        "Gross weight: " + item.getGrossWt() + "\n" +
                        "Stone weight: " + item.getStoneWt() + "\n" +
                        "Net weight: " + item.getNetWt()+"\n" +
                        "Remark: " + item.getDescription());

        grossWtEdit.setText(String.valueOf(item.getGrossWt()));
        stoneWtEdit.setText(String.valueOf(item.getStoneWt()));
        netWtEdit.setText(String.valueOf(item.getNetWt()));
        notesEdit.setText(checknull(item.getDescription())); // Assuming you have a method to get the notes

        // Show or hide the edit fields based on the type
        if ("order".equals(type)) {
            grossWtEdit.setVisibility(View.VISIBLE);
            stoneWtEdit.setVisibility(View.VISIBLE);
            netWtEdit.setVisibility(View.VISIBLE);
            notesEdit.setVisibility(View.VISIBLE);
            tvGrWt.setVisibility(View.VISIBLE);
            tvStoneWt.setVisibility(View.VISIBLE);
            tvNetWt.setVisibility(View.VISIBLE);
        } else {
            grossWtEdit.setVisibility(View.GONE);
            stoneWtEdit.setVisibility(View.GONE);
            netWtEdit.setVisibility(View.GONE);
            notesEdit.setVisibility(View.GONE);
            tvGrWt.setVisibility(View.GONE);
            tvStoneWt.setVisibility(View.GONE);
            tvNetWt.setVisibility(View.GONE);
        }



        String basePath = getExternalStoragePath(context);
        // Load the image into the ImageView
        // Here you need to replace `item.getImageUrl()` with the actual method to get the image URL or resource ID
        String imageUrlString = item.getImageUrl(); // e.g., "img1.jpg,img2.jpg,img3.jpg"
        String onlineimage="";
        if (imageUrlString != null && !imageUrlString.isEmpty()) {
            String[] imageUrls = imageUrlString.split(",");
            String lastImage = imageUrls[imageUrls.length - 1].trim(); // get last and trim spaces
            onlineimage = "https://rrgold.loyalstring.co.in/" + lastImage;
            // Use `onlineImage` as needed
        } else {
            // fallback or placeholder
            onlineimage = "https://rrgold.loyalstring.co.in/default.jpg";
        }
        String iname = item.getItemCode();


        String imageUrl = iname + ".jpg"; // Assuming you have a method to get the image URL
        Log.e("loadimage", "" + imageUrl);

        if (onlineimage != null && !onlineimage.isEmpty()) {
            Glide.with(context)
                    .load(onlineimage)
                    .placeholder(R.drawable.logo) // Optional: a placeholder image while loading
                    .error(R.drawable.logo) // Optional: an error image if loading fails
                    .into(itemImage);
        }
        else {

            if (item.getItemCode() != null && !item.getItemCode().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }

                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files/images");

                File imageFile = new File(directory, imageUrl);

                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if (created) {
                        Log.d("DirectoryCheck", "Directory created: " + directory.getAbsolutePath());
                    } else {
                        Log.d("DirectoryCheck", "Failed to create directory: " + directory.getAbsolutePath());
                    }
                } else {
                    Log.d("DirectoryCheck", "Directory exists: " + directory.getAbsolutePath());
                }


                //  File imageFile = new File(directory, imageUrl);
                String imagePath = imageFile.getAbsolutePath(); // Full file path

                if (!imageFile.exists()) {
                    // Fallback: check app-specific directory
                    File fallbackDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files/images");
                    imageFile = new File(fallbackDirectory, item.getProductCode() + ".jpg");
                    imagePath = imageFile.getAbsolutePath();
                }
                Uri imageUri = getImageUriFromFilePath(imagePath);


                if (checkIfFileExists(imageFile, directory)) {
                    if (imageUri != null) {
                        Glide.with(context)
                                .load(imageUri)
                                .into(itemImage);
                    } else {
                        Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
                        itemImage.setImageResource(R.drawable.logo); // Set default if not found
                    }
                }
            } else {
                itemImage.setImageResource(R.drawable.logo);
            }
        }
        // Build and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Item Details")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog if needed

                        double gwt = item.getGrossWt();
                        double swt = item.getStoneWt();
                        double nwt = item.getNetWt();
                        if ("order".equals(type)) {
                            // Save the edited values
                            item.setGrossWt(Double.parseDouble(grossWtEdit.getText().toString()));
                            item.setStoneWt(Double.parseDouble(stoneWtEdit.getText().toString()));
                            item.setNetWt(Double.parseDouble(netWtEdit.getText().toString()));
                            item.setDescription(notesEdit.getText().toString()); // Assuming you have a setter for notes

                            // Call the listener to update the item in the fragment
                            if (listener != null) {
                                listener.onItemUpdated(item, gwt, swt, nwt);
                            }
                        }else{
                            item.setDescription(notesEdit.getText().toString()); // Assuming you have a setter for notes

                            // Call the listener to update the item in the fragment
                            if (listener != null) {
                                listener.onItemUpdated(item, gwt, swt, nwt);
                            }
                        }
                        // Close the dialog
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
