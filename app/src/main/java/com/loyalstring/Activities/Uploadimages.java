package com.loyalstring.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.loyalstring.R;
import com.loyalstring.databinding.ActivityUploadimagesBinding;
import com.loyalstring.modelclasses.Itemmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Uploadimages extends AppCompatActivity {

    ActivityUploadimagesBinding b;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_CODE = 101;
    private Uri photoUri;
    private String currentPhotoPath;
    Itemmodel item;

    String itemdetails = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityUploadimagesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());


         item = getIntent().getParcelableExtra("itemModel");

        Log.e("checking item ", ""+ item);

        if (item != null) {
            // Use the item object as needed

            b.itemdetails.setText(
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
        }

        b.captureimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item == null ){
                    Toast.makeText(Uploadimages.this, "failed to load item", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestCameraPermission();
            }
        });

        b.saveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item == null){
                    Toast.makeText(Uploadimages.this, "failed to load item", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveImageToFile();
            }
        });




//        setContentView(R.layout.activity_uploadimages);
    }


    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.loyalstring.provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK) {
            b.imageview.setImageURI(photoUri);
        }
    }

    private void saveImageToFile() {
        try {
            // Construct the desired file path
            String itemCode = item.getItemCode(); // Replace this with actual item code
            String imageUrl = itemCode + ".jpg";
            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);

            // Ensure the directory exists
            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }

            // Copy the image to the desired location
            File sourceFile = new File(currentPhotoPath);
            copyFile(sourceFile, destinationFile);

            Toast.makeText(this, "Image saved to: " + destinationFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }
    private String checknull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

}