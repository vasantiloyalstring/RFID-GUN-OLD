package com.loyalstring.fsupporters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.loyalstring.interfaces.interfaces;

public class Pemissionscheck {
    public static final int STORAGE_PERMISSION_READWRITE_CODE = 1;
    public static final int PICK_EXCEL_REQUEST = 2;
    private interfaces.PermissionCallback callback;
    private Activity activity;

    public Pemissionscheck(Activity activity, interfaces.PermissionCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public boolean checkreadandwrite(Context context) {

        int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;

    }


    public void requestreadwrite(FragmentActivity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_READWRITE_CODE);
    }

    public void onActivityResults(int requestCode, int resultCode, Intent data) {
//        if (requestCode == STORAGE_PERMISSION_READWRITE_CODE) {

        if (callback != null) {
            switch (requestCode) {
                case PICK_EXCEL_REQUEST:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        callback.onPermissionGranted("excelopen", data);
                    }
                    break;
                case STORAGE_PERMISSION_READWRITE_CODE:
                    // Handle storage permission result if needed
                    break;
            }
        }
    }

    public void getexcelfile(FragmentActivity activity) {
        String[] mimetypes =
                {"application/vnd.ms-excel", // .xls
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
                };
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//("application/vnd.ms-excel"); // Set the MIME type to filter only Excel files
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_REQUEST);
    }
}
