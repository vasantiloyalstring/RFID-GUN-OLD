package com.loyalstring.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loyalstring.Activities.ApiConfigActivity;
import com.loyalstring.Activities.Customapis;
import com.loyalstring.Activities.SyncSettingsActivity;
import com.loyalstring.Activities.googlesheet;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.MainActivity;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.database.DatabaseHelper;
import com.loyalstring.database.StorageClass;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.FragmentSettingsfragmentBinding;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.mainscreens.Activationpage;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.tools.DownloadImagesTask;
import com.loyalstring.transactionhelper.TransactionIDGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Settingsfragment extends Fragment implements interfaces.Imagedownload{

    FragmentSettingsfragmentBinding s;

    EntryDatabase entryDatabase;
    private MainActivity mainActivity;
    StorageClass storageClass;
    Globalcomponents globalcomponents;
    HashMap<String, Itemmodel> totalitems = new HashMap<>();
    MyApplication myapp;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "DownloadPrefs";
    private static final String KEY_PROGRESS = "progress";
    SharedPreferencesManager sharedPreferencesManager;
    AtomicInteger completedTasks = new AtomicInteger(0);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        s = FragmentSettingsfragmentBinding.inflate(inflater, container, false);
//        entryDatabase = new EntryDatabase(getActivity());
//        MyApplication app = (MyApplication) requireContext().getApplicationContext();
//        entryDatabase.cleardata(getActivity(), app);
        mainActivity = (MainActivity) getActivity();
//        mainActivity.currentFragment = Settingsfragment.this;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

        myapp = (MyApplication) requireActivity().getApplicationContext();
        storageClass = new StorageClass(getActivity());
        globalcomponents = new Globalcomponents();

        s.preadtext.setText(storageClass.getppower());
        s.ireadtext.setText(storageClass.getipower());
        s.sreadtext.setText(storageClass.getspower());
        s.treadtext.setText(storageClass.gettpower());
        s.streadtext.setText(storageClass.getstpower());
        s.shreadtext.setText(storageClass.getshpower());

        sharedPreferencesManager = new SharedPreferencesManager(getActivity());

        s.setmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainActivity.mReader.setFastID(true)){
                    Toast.makeText(mainActivity, "success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mainActivity, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        s.preadlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Product", storageClass, s.preadtext, null);
            }
        });

        s.ireadlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Inventory", storageClass, s.ireadtext, null);
            }
        });

        s.sreadlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Search", storageClass, s.sreadtext, null);
            }
        });

        s.treadlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Transaction", storageClass, s.treadtext, null);

            }
        });

        s.streadlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Stock transfer", storageClass, s.streadtext, null);

            }
        });

        s.shreadlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Stock history", storageClass, s.shreadtext, null);

            }
        });

        s.dynamicapis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(getActivity(), Customapis.class);
//               Intent go = new Intent(getActivity(), ApiConfigActivity.class);
                startActivity(go);
            }
        });

        s.synclay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(getActivity(), SyncSettingsActivity.class);
                startActivity(go);
            }
        });

        s.googlesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                DatabaseHelper.replaceDatabase(getActivity());
                Intent go = new Intent(getActivity(), googlesheet.class);
                startActivity(go);

            }
        });
        s.stockTransferUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showStockTransferUrlDialog();

            }
        });


        s.resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                storageClass.setActivationStatus(false, "", "", "", "", "");
                Intent go = new Intent(getActivity(), Activationpage.class);
                startActivity(go);
                getActivity().finish();

                /*Toast.makeText(mainActivity, "reset app", Toast.LENGTH_SHORT).show();
                EntryDatabase db = new EntryDatabase(getActivity());
                db.deletetable();


                Intent intent = getActivity().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getPackageName());
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    System.exit(0); // This ensures that the app is fully terminated
                }*/


            }
        });


        s.uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = sharedPreferencesManager.readLoginData().getEmployee().getClientCode();

                if(code == null || code.isEmpty()){
                    Toast.makeText(mainActivity, "code missing", Toast.LENGTH_SHORT).show();
                return;
                }

                upload(view, code);
            }
        });



        /*s.uploadbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalitems.clear();
                if (!myapp.isCountMatch()) {
                    // Show progress dialog
                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setMessage("Loading data ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Runnable onCountMatched = new Runnable() {
                        @Override
                        public void run() {
                            // Dismiss the progress dialog
                            progressDialog.dismiss();
                            HashMap<String, Itemmodel> totalitems1 = new HashMap<String, Itemmodel>();

                            totalitems1 = myapp.getInventoryMap();
                            for(Map.Entry<String, Itemmodel> entry : totalitems1.entrySet()){
                                String key = entry.getKey();
                                Itemmodel m = entry.getValue();
                                Itemmodel item = new Itemmodel(m);
                                totalitems.put(key, item);

                            }


                        }
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Check for count match
                            while (!myapp.isCountMatch()) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            requireActivity().runOnUiThread(onCountMatched);
                        }
                    }).start();
                } else {
                    HashMap<String, Itemmodel> totalitems1 = new HashMap<String, Itemmodel>();

                    totalitems1 = myapp.getInventoryMap();
                    for(Map.Entry<String, Itemmodel> entry : totalitems1.entrySet()){
                        String key = entry.getKey();
                        Itemmodel m = entry.getValue();
                        Itemmodel item = new Itemmodel(m);
                        totalitems.put(key, item);

                    }

                }

                Toast.makeText(mainActivity, "total items "+totalitems.size(), Toast.LENGTH_SHORT).show();



                // Add your items to the totalitems map here
                new DownloadImagesTask(requireContext(), Settingsfragment.this).execute(totalitems);
            }
        });*/


        s.ptext.setText("Downloaded: 0 of 0");
        s.uploadbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = storageClass.getSheeturl();
                sheetprocess(getActivity(),url );
            }
        });



        return s.getRoot();
    }

    List<String> imageurls = new ArrayList<>();
    List<File> destinationFiles = new ArrayList<>();

    private void showStockTransferUrlDialog() {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set Stock Transfer URL");

        final EditText input = new EditText(context);
        input.setHint("https://sapphirejewelryny.com/RFID/");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);

        String savedUrl = sharedPreferencesManager.getStockTransferUrl();
        input.setText(savedUrl);

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String url = input.getText().toString().trim();

            if (!url.isEmpty() && android.util.Patterns.WEB_URL.matcher(url).matches()) {
                sharedPreferencesManager.saveStockTransferUrl(url);
                Toast.makeText(context, "Stock Transfer URL saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    public void sheetprocess(FragmentActivity activity, String sheeturl) {
        // Create and show a loading message without blocking the UI
        String url = "https://docs.google.com/spreadsheets/d/" + sheeturl + "/gviz/tq?tqx=out:json&sheet=Sheet2";
        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        HashMap<String, Itemmodel> nmap = new HashMap<>();
                        List<Itemmodel> dmap = new ArrayList<>();
                        String jsonString = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONObject table = jsonObject.getJSONObject("table");
                        JSONArray rows = table.getJSONArray("rows");

                        imageurls.clear();
                        destinationFiles.clear();

                        for (int i1 = 0; i1 < rows.length(); i1++) {
                            JSONObject entryObj = rows.getJSONObject(i1);
                            JSONArray rowData = entryObj.getJSONArray("c");
                            String stonewt = "0";
                            // Extract relevant data
                            String itemcode = rowData.optJSONObject(1) != null ? rowData.getJSONObject(1).optString("v", "") : "";
                            String simageurl = rowData.optJSONObject(11) != null ? rowData.getJSONObject(11).optString("v", "") : "";
                            if (simageurl.contains("drive.google.com")) {
                                // Extract file ID from URL
                                String fileId = extractFileId(simageurl);
                                simageurl = "https://drive.google.com/uc?export=download&id=" + fileId;
                            }

                            if (!simageurl.isEmpty() && !itemcode.isEmpty()) {
                                String fileName = itemcode + ".jpg";
                                File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + fileName);

                                imageurls.add(simageurl);
                                destinationFiles.add(destinationFile);
                            }
                        }

                        Toast.makeText(activity, "total image "+imageurls.size(), Toast.LENGTH_SHORT).show();
                        if (!imageurls.isEmpty()) {
                            // Start downloading images in the background without blocking UI
                            downloadImagesConcurrently(activity, imageurls, destinationFiles);

                        } else {
                            Toast.makeText(activity, "No images to download.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(activity, "Failed to read data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(activity, "Failed to read data", Toast.LENGTH_SHORT).show();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("check error", errorMessage);
                    } else {
                        Log.e("check error", "Unknown error occurred.");
                    }
                });

        queue.add(stringRequest);
    }

    private String extractFileId(String url) {
        String fileId = "";
        try {
            if (url.contains("/file/d/")) {
                int start = url.indexOf("/file/d/") + 8;
                int end = url.indexOf("/", start);
                fileId = url.substring(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileId;
    }

    private void downloadImagesConcurrently(Context context, List<String> imageUrls, List<File> destinationFiles) {
        int totalImages = imageUrls.size();
        ExecutorService executor = Executors.newFixedThreadPool(15); // Adjust thread pool size


        for (int i = 0; i < totalImages; i++) {
            String imageUrl = imageUrls.get(i);
            File destinationFile = destinationFiles.get(i);

            executor.execute(() -> {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        FileOutputStream outputStream = new FileOutputStream(destinationFile);

                        byte[] buffer = new byte[4096]; // Increase buffer size
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.close();
                        inputStream.close();
                    }

                    // Update progress
                    int completed = completedTasks.incrementAndGet();
                    ((FragmentActivity) context).runOnUiThread(() -> {
                        // You can add a UI update here if you need, e.g., updating a progress bar or a message.
                        // For example:
                        // progressBar.setProgress(completed * 100 / totalImages);
                        s.ptext.setText("Downloaded: " + completed + " of " + totalImages);
                        if (completed == totalImages) {
                            Toast.makeText(context, "All images downloaded!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    Log.e("Image Download", "Error downloading image: " + imageUrl, e);
                }
            });
        }

        executor.shutdown();
    }

    public void sheetprocess1( FragmentActivity activity, String sheeturl ) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading data");
        dialog.show();

        String url = "https://docs.google.com/spreadsheets/d/" + sheeturl + "/gviz/tq?tqx=out:json&sheet=Sheet2";
        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        HashMap<String, Itemmodel> nmap = new HashMap<>();
                        List<Itemmodel> dmap = new ArrayList<>();
                        String jsonString = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONObject table = jsonObject.getJSONObject("table");
                        JSONArray rows = table.getJSONArray("rows");

                        imageurls.clear();
                        destinationFiles.clear();

                        for (int i1 = 0; i1 < rows.length(); i1++) {
                            JSONObject entryObj = rows.getJSONObject(i1);
                            JSONArray rowData = entryObj.getJSONArray("c");
                            String stonewt = "0";
                            // Extract relevant data
                            String itemcode = rowData.optJSONObject(1) != null ? rowData.getJSONObject(1).optString("v", "") : "";
                             String simageurl = rowData.optJSONObject(11) != null ? rowData.getJSONObject(11).optString("v", "") : "";
                            if (simageurl.contains("drive.google.com")) {
                                // Extract file ID from URL
                                String fileId = extractFileId(simageurl);
                                simageurl = "https://drive.google.com/uc?export=download&id=" + fileId;
                            }

                            if (!simageurl.isEmpty() && !itemcode.isEmpty()) {
                                String fileName = itemcode + ".jpg";
                                File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + fileName);

                                // Download image
//                                downloadImage(activity, simageurl, destinationFile);

                                imageurls.add(simageurl);
                                destinationFiles.add(destinationFile);

                            }


                        }
                        if (!imageurls.isEmpty()) {
//                                    ProgressDialog dialog = new ProgressDialog(activity);
//                                    dialog.setMessage("Downloading images...");
//                                    dialog.setCancelable(false);
//                                    dialog.show();

                            if(!dialog.isShowing()){
                                dialog.setMessage("loading images");
                                dialog.show();
                                dialog.setCanceledOnTouchOutside(false);
                            }

                            downloadImagesConcurrently1(activity, imageurls, destinationFiles, dialog);
                            // Database operations
                            dialog.dismiss();
                        }





                    } catch (JSONException e) {
                        dialog.dismiss();
                        Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("check error", errorMessage);
                    } else {
                        Log.e("check error", "Unknown error occurred.");
                    }
                });

        queue.add(stringRequest);
    }
    private String extractFileId1(String url) {
        String fileId = "";
        try {
            if (url.contains("/file/d/")) {
                int start = url.indexOf("/file/d/") + 8;
                int end = url.indexOf("/", start);
                fileId = url.substring(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileId;
    }
    private void downloadImagesConcurrently1(Context context, List<String> imageUrls, List<File> destinationFiles, ProgressDialog dialog) {
        int totalImages = imageUrls.size();
        ExecutorService executor = Executors.newFixedThreadPool(15); // Adjust thread pool size
        AtomicInteger completedTasks = new AtomicInteger(0);

        for (int i = 0; i < totalImages; i++) {
            String imageUrl = imageUrls.get(i);
            File destinationFile = destinationFiles.get(i);

            executor.execute(() -> {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        FileOutputStream outputStream = new FileOutputStream(destinationFile);

                        byte[] buffer = new byte[4096]; // Increase buffer size
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.close();
                        inputStream.close();
                    }

                    // Update progress
                    int completed = completedTasks.incrementAndGet();
                    ((FragmentActivity) context).runOnUiThread(() -> {
                        dialog.setMessage("Downloaded " + completed + " of " + totalImages + " images");
                        if (completed == totalImages) {
                            dialog.dismiss();
                            Toast.makeText(context, "All images downloaded!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    Log.e("Image Download", "Error downloading image: " + imageUrl, e);
                }
            });
        }

        executor.shutdown();
    }



    public void upload(View view, String code) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("uploading");
        dialog.show();
        StorageReference storageReference;
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // Local file path
        String filePath = "/data/data/com.loyalstring/databases/loyalstring.db";
        File file = new File(filePath);



//        String timestamp = new SimpleDateFormat("yyMMddHH").format(new Date());
        String timestamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
        Log.e("check code", "  "+code+"   "+timestamp);

        // Create a reference to the file in Cloud Storage
        StorageReference fileReference = storageReference.child("DataBaseBackups").child(code).child(timestamp).child("loyalstrings.db");

        // Upload file to Cloud Storage
        UploadTask uploadTask = fileReference.putFile(Uri.fromFile(file));

        // Monitor the upload progress and handle success or failure
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // File uploaded successfully
            // Handle success logic
            Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }).addOnFailureListener(exception -> {
            // File upload failed
            // Handle failure logic
            Log.d("failed", exception.getMessage().toString());
            dialog.dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int progress = sharedPreferences.getInt("download_progress", 0);
        // Display the progress in your settings UI
        s.uploadbtn1.setText("Download Progress: " + progress + "%");
//        refreshData();
    }

    private void refreshData() {
        totalitems.clear();
        if (!myapp.isCountMatch()) {
            // Show progress dialog
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Loading data ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Runnable onCountMatched = new Runnable() {
                @Override
                public void run() {
                    // Dismiss the progress dialog
                    progressDialog.dismiss();
                    HashMap<String, Itemmodel> totalitems1 = new HashMap<String, Itemmodel>();

                    totalitems1 = myapp.getInventoryMap();
                    for(Map.Entry<String, Itemmodel> entry : totalitems1.entrySet()){
                        String key = entry.getKey();
                        Itemmodel m = entry.getValue();
                        Itemmodel item = new Itemmodel(m);
                        totalitems.put(key, item);

                    }


                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Check for count match
                    while (!myapp.isCountMatch()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    requireActivity().runOnUiThread(onCountMatched);
                }
            }).start();
        } else {
            HashMap<String, Itemmodel> totalitems1 = new HashMap<String, Itemmodel>();

            totalitems1 = myapp.getInventoryMap();
            for(Map.Entry<String, Itemmodel> entry : totalitems1.entrySet()){
                String key = entry.getKey();
                Itemmodel m = entry.getValue();
                Itemmodel item = new Itemmodel(m);
                totalitems.put(key, item);

            }

        }

        Toast.makeText(mainActivity, "total items "+totalitems.size(), Toast.LENGTH_SHORT).show();



        // Add your items to the totalitems map here
        new DownloadImagesTask(requireContext(), Settingsfragment.this).execute(totalitems);

    }

    @Override
    public void onSaveSuccess(int scount) {
        s.ptext.setText("success "+scount);
    }

    @Override
    public void onSaveFailure(int fcount) {
        s.ptext.setText("failed  "+fcount);
    }
}