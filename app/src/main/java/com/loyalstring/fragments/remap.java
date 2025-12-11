package com.loyalstring.fragments;

import static com.loyalstring.MainActivity.binarySearch;
import static com.loyalstring.fsupporters.Pemissionscheck.PICK_EXCEL_REQUEST;
import static com.loyalstring.fsupporters.Pemissionscheck.STORAGE_PERMISSION_READWRITE_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loyalstring.Adapters.ProductAdapter;
import com.loyalstring.Apis.ApiProcess;
import com.loyalstring.Excels.Excelopener;
import com.loyalstring.Excels.InventoryExcelCreation;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.database.StorageClass;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.FragmentRemapBinding;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.fsupporters.Pemissionscheck;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.readersupport.KeyDwonFragment;
import com.loyalstring.tools.StringUtils;
import com.loyalstring.transactionhelper.TransactionIDGenerator;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class remap extends KeyDwonFragment implements interfaces.PermissionCallback {



    FragmentRemapBinding b;

    private MainActivity mainActivity;
    String stid, sepc, scategory, sproduct, spurity, sbarcode, sitemcode, sbox, sgrosswt, sstonewt, snetwt, smakinggm,
            smakingper, sfixedamount, sfixedwastage, sstoneamount, smrp, shuidcode, spartycode, sdesp, shallmark;


    EntryDatabase entryDatabase;

    List<Itemmodel> itemlist = new ArrayList<>();
    StorageClass storageClass;
    Globalcomponents globalcomponents;
    boolean ssingle = true;
    boolean bsingle = false;
    MyApplication app;
    String itemexist = "";
    boolean ploopFlag = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UHFTAGInfo info = (UHFTAGInfo) msg.obj;
            Log.d("checktidva", "t" + info.getTid() + " e" + info.getEPC() + " r" + info.getReserved() + " " + info.getUser() + "  " + info.toString());
            addDataToList(info.getEPC(), info.getTid(), info.getRssi());
        }
    };
    List<String> tempDatas = new ArrayList<String>();

    ProductAdapter productAdapter;
    int existitems = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    Excelopener excelopener;

    private ProgressDialog progressDialog;
    ApiProcess apiprocess;

    Pemissionscheck pcheck;
    com.loyalstring.interfaces.interfaces interfaces;

    String tidBarcodeString = "";
    List<Issuemode> issueitem = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        b = FragmentRemapBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();
        mainActivity.currentFragment = remap.this;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Remap");

            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }


        pcheck = new Pemissionscheck(getActivity(), this);
        interfaces = new interfaces();


        apiprocess = new ApiProcess();
        mainActivity.toolpower.setVisibility(View.VISIBLE);
        mainActivity.toolpower.setText(String.valueOf(mainActivity.mReader.getPower()));
        mainActivity.toolpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Product", storageClass, mainActivity.toolpower, mainActivity.mReader);
            }
        });

        resetsstate();
        b.bulkrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        productAdapter = new ProductAdapter(itemlist, mainActivity.barcodeDecoder, getActivity());
        b.bulkrecycler.setAdapter(productAdapter);
        b.bgimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
        b.bgtext.setText("Gscan");
        b.bsimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
        b.bstext.setText("Scan");

        storageClass = new StorageClass(getActivity());
        globalcomponents = new Globalcomponents();
        app = (MyApplication) requireActivity().getApplicationContext();
        excelopener = new Excelopener();
        progressDialog = new ProgressDialog(getActivity());


        entryDatabase = new EntryDatabase(getActivity());




        b.bbcheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b1) {

                if (!ploopFlag) {
                    if (b1) {
                        checkbox("barcode");
                    }
                } else {
                    Toast.makeText(mainActivity, "stop scanning", Toast.LENGTH_SHORT).show();
                    b.bbcheckbox.setChecked(false);
                }
            }
        });
        b.bicheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b1) {
                if (!ploopFlag) {
                    if (b1) {
                        checkbox("itemcode");
                    }
                } else {
                    Toast.makeText(mainActivity, "stop scanning", Toast.LENGTH_SHORT).show();
                    b.bicheckbox.setChecked(false);
                }
            }
        });


        //bulk pages
        b.bcatgorylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.getlist("Category", b.bcategorytext, getActivity());
            }
        });
        b.bproductlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.getprolist(b.bcategorytext.getText().toString(), b.bproducttext, getActivity());

            }
        });
        b.bbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.getboxes(getActivity(), b.bboxtext);
            }
        });
        b.bsscanlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssingle = false;
                bsingle = true;
                if (mainActivity.mReader.isInventorying()) {
                    ploopFlag = false;

                    boolean s = stopscanner();
                    if (s) {
                        b.bstext.setText("Scan");
                        b.bsimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
//                        stopTemperatureCheck(getActivity());
                    } else {
                        Toast.makeText(mainActivity, "failed to stop scanning", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    boolean checkpower = globalcomponents.checkpower(getActivity(), mainActivity.mReader, getpvalue(storageClass.getppower()), mainActivity.toolpower);
                    if (checkpower) {
//                        mainActivity.mReader.setFastID(true);
                        performsinglescan();
                    } else {
                        Toast.makeText(mainActivity, "failed to set power", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        b.bgscanlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssingle = false;
                bsingle = false;
                if (mainActivity.mReader.isInventorying()) {
                    ploopFlag = false;
                    boolean s = stopscanner();
                    if (s) {
                        b.bgtext.setText("GScan");
                        b.bgimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
//                        stopTemperatureCheck(getActivity());
                    } else {
                        Toast.makeText(mainActivity, "failed to stop scanning", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    boolean checkpower = globalcomponents.checkpower(getActivity(), mainActivity.mReader, getpvalue(storageClass.getppower()), mainActivity.toolpower);
                    if (checkpower) {
//                        mainActivity.mReader.setFastID(true);
                        performsinglescan();
                    } else {
                        Toast.makeText(mainActivity, "failed to set power", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        b.bsavelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mainActivity.mReader.isInventorying()) {
                    Toast.makeText(mainActivity, "Please stop scanning", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (itemlist.isEmpty()) {
                    Toast.makeText(mainActivity, "no items added", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Itemmodel entry : itemlist) {
                    entry.setCategory(b.bcategorytext.getText().toString().trim());
                    entry.setProduct(b.bproducttext.getText().toString().trim());
                    String bs = b.bboxtext.getText().toString().trim();
                    if (bs.equalsIgnoreCase("box") || bs.isEmpty()) {
                        bs = "";
                    }
                    entry.setBox(bs);
                }

                entryDatabase.checkdatabase(getActivity());




                                entryDatabase.makeentry(getActivity(), itemlist, "adding", "remap", app, issueitem, new SaveCallback() {
                    @Override
                    public void onSaveSuccess() {
                        Toast.makeText(mainActivity, "saved items successfully", Toast.LENGTH_SHORT).show();

                        itemlist.clear();
                        productAdapter.notifyDataSetChanged();
                        b.totalexistitemstxt.setText("");
                        b.totalitemstxt.setText("");
                        tempDatas.clear();
                        b.bcategorytext.setText("Category");
                        b.bproducttext.setText("Product");

                    }

                    @Override
                    public void onSaveFailure(List<Itemmodel> failedItems) {

                        Toast.makeText(mainActivity, "Failed to add items", Toast.LENGTH_SHORT).show();
                        itemlist.clear();
                        itemlist.addAll(failedItems);
                        productAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
//        b.blistlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mainActivity.mReader.isInventorying()) {
//                    Toast.makeText(getActivity(), "stop scanning", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                ReaderSingleton.setReader(mainActivity.mReader);
//                ReaderSingleton.setbarcode(mainActivity.barcodeDecoder);
//                Intent go = new Intent(getActivity(), ListActivity.class);
//                startActivity(go);
//            }
//        });

        b.bresetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetbstate();
            }
        });

        //import/export
        //importexport



        b.pexportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkreadandwrite(getActivity())) {
                    checkfolder();


                } else {
                    requestreadwrite(getActivity());
                }
            }
        });



        return b.getRoot();
    }

    private void checkfolder() {
        boolean folder = globalcomponents.checkfileexist("remap");
        if (folder) {
            File cfile = createfile();
            if (cfile != null) {
//                            readitems(getActivity(), importfragment.this, "", inventorylist, "export", cfile);
                ArrayList<Itemmodel> ml = new ArrayList<>();

                int count = entryDatabase.gettotalcount1(getActivity());
                if (count > 0) {
//                    app.setcount(count);
                    HashMap<String, Itemmodel> inventoryMap = entryDatabase.loadRemapItems(getActivity(), app);
                    if(!inventoryMap.isEmpty()){
                        for (Map.Entry<String, Itemmodel> entry : inventoryMap.entrySet()) {
                            Itemmodel item = entry.getValue();
                            if (item.getPurity() == null || item.getPurity().isEmpty()) {
                                ml.add(item);
                            }

                        }
                        if (ml.isEmpty()) {
                            Toast.makeText(mainActivity, "No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String filePath = cfile.getAbsolutePath();//externalDir.getAbsolutePath() + "/unfilleddata.xlsx";
//                    excelConverter.unfilledexcel(resultList,filePath, getActivity(), "unfilled" );
//                    ExcelCreationTask excelTask = new ExcelCreationTask(resultList, filePath, getActivity());
//                    excelTask.execute();

                        Log.d("tag", "check unfilled path " + filePath);


                        HashMap<String, ArrayList<Itemmodel>> excelmap = new HashMap<>();
                        excelmap.put(filePath, ml);

                        InventoryExcelCreation excelTask = new InventoryExcelCreation(null, null, filePath, "", getActivity(), "product", "excel", excelmap, null);
                        excelTask.execute();
                    }
                }


               /* if (app.getInventoryMap().size() > 0) {
                    for (Map.Entry<String, Itemmodel> entry : app.getInventoryMap().entrySet()) {
                        Itemmodel item = entry.getValue();
                        if (item.getPurity() == null || item.getPurity().isEmpty()) {
                            ml.add(item);
                        }

                    }
                    if (ml.isEmpty()) {
                        Toast.makeText(mainActivity, "No data found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String filePath = cfile.getAbsolutePath();//externalDir.getAbsolutePath() + "/unfilleddata.xlsx";
//                    excelConverter.unfilledexcel(resultList,filePath, getActivity(), "unfilled" );
//                    ExcelCreationTask excelTask = new ExcelCreationTask(resultList, filePath, getActivity());
//                    excelTask.execute();

                    Log.d("tag", "check unfilled path " + filePath);


                    HashMap<String, ArrayList<Itemmodel>> excelmap = new HashMap<>();
                    excelmap.put(filePath, ml);

                    InventoryExcelCreation excelTask = new InventoryExcelCreation(null, null, filePath, "", getActivity(), "product", "excel", excelmap, null);
                    excelTask.execute();

                }
                else {
                    Toast.makeText(mainActivity, "No data found", Toast.LENGTH_SHORT).show();
                }*/

            } else {
                Toast.makeText(getActivity(), "failed to create file", Toast.LENGTH_SHORT).show();
            }
        } else {
            ArrayList<String> folders = new ArrayList<>();
            folders.add("remap");
            boolean f = globalcomponents.createFolders(folders);
            if (!f) {
                Toast.makeText(getActivity(), "failed to create file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "created file please click again", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        pcheck.onActivityResults(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_EXCEL_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null) {
//                    callback.onPermissionGranted("excelopen", data);
                    Uri uri = data.getData();
                    if (uri != null) {

//                        excelopener.processsheet(uri, getActivity(), progressDialog, app);
                    }
                }
                break;
            case STORAGE_PERMISSION_READWRITE_CODE:
                // Handle storage permission result if needed
                checkfolder();
                break;
        }


    }


    @Override
    public void onPermissionGranted(String s, Intent data) {

        if (s.equalsIgnoreCase("excelopen")) {
            Uri uri = data.getData();
            if (uri != null) {

//                excelopener.processsheet(uri, getActivity(), progressDialog, app);
            }
        } else {
            checkfolder();
        }
    }

    private void checkbox(String s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Tag already exists");

// Set a custom layout for the dialog
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

// Find the EditText in the dialog layout
        EditText editText = dialogView.findViewById(R.id.editText);
        TextView textView = dialogView.findViewById(R.id.dialogtext);
        ImageView scanner = dialogView.findViewById(R.id.bscanner);

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                barcodeDecoder.startScan();
//                showtoast("okay");
                globalcomponents.barcodescan(mainActivity.barcodeDecoder, editText);
            }
        });

        /*barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
            @Override
            public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                Log.e("TAG", "BarcodeDecoder==========================:" + barcodeEntity.getResultCode());
                if (barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS) {
                    editText.setText(barcodeEntity.getBarcodeData());
                    Log.e("TAG", "data==========================:" + barcodeEntity.getBarcodeData());
                } else {
                    editText.setText("");
//                    showtoast("Failed to read bar code");
                }
            }
        });*/

        if (s.matches("barcode")) {
            textView.setText("Enter Barcode No For All Items");
            globalcomponents.barcodescan(mainActivity.barcodeDecoder, editText);
        } else {
            textView.setText("Enter Itemcode No For All Items");
            scanner.setVisibility(View.GONE);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        }
// Set any additional properties or listeners for the EditText if needed

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the text entered in the EditText
                String enteredText = editText.getText().toString().trim();

                // Perform any required actions with the entered text
                // ...

                // Dismiss the dialog
                if (s.matches("barcode")) {
//                    textView.setText("Enter Barcode No");
                    b.bbcheckbox.setChecked(false);

                    for (int i = 0; i < itemlist.size(); i++) {
                        itemlist.get(i).setBarCode(enteredText);
                    }
//                    adapter.notifyDataSetChanged();


                } else {
                    b.bicheckbox.setChecked(false);
                    for (int i = 0; i < itemlist.size(); i++) {
                        itemlist.get(i).setItemCode(enteredText);
                    }

                }
                productAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the cancellation of the dialog if needed
                // ...

                // Dismiss the dialog
                if (s.matches("barcode")) {
//                    textView.setText("Enter Barcode No");
                    b.bbcheckbox.setChecked(false);
                } else {
                    b.bicheckbox.setChecked(false);
                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }

    private void resetbstate() {
        tidBarcodeString = "";
        itemlist.clear();
        itemexist = "";
        tempDatas.clear();
        b.totalitemstxt.setText("");
        b.totalexistitemstxt.setText("");
        productAdapter.notifyDataSetChanged();
    }

    private void resetsstate() {
        tidBarcodeString = "";
        stid = "";
        sepc = "";
        sbarcode = "";
        sitemcode = "";
        sgrosswt = "";
        sstonewt = "";
        snetwt = "";
        spurity = "";
        sbox = "";
        sproduct = "";
        scategory = "";

        tempDatas.clear();


        itemlist.clear();
        itemexist = "";
    }



    private void performsinglescan() {
       /* if(!mainActivity.mReader.setEPCAndTIDMode()){
            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if(!mainActivity.mReader.setEPCAndTIDMode()){
            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
            return;
        }

//        mainActivity.mReader.setEPCMode();
        readTag();
    }

    private void readTag() {
        if (mainActivity.mReader.startInventoryTag()) {

            ploopFlag = true;


                if (bsingle) {
                    b.bsimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancelblack));
                    b.bstext.setText("Stop");
                } else {
                    b.bgimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancelblack));
                    b.bgtext.setText("Stop");
                }

//            mainActivity.mReader.setFastID(true);
            new TagThread().start();

        } else {
            if (stopscanner()) {

                    if (bsingle) {
                        b.bsimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
                        b.bstext.setText("Scan");
                    } else {
                        b.bgimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
                        b.bgtext.setText("Gscan");
                    }

            }
        }
    }

    class TagThread extends Thread {
        public void run() {
            UHFTAGInfo uhftagInfo;
            Message msg;
            Log.d("product1", "check 1" + ploopFlag);
            while (ploopFlag) {
                uhftagInfo = mainActivity.mReader.readTagFromBuffer();
                if (uhftagInfo != null) {
                    msg = handler.obtainMessage();
                    msg.obj = uhftagInfo;
                    handler.sendMessage(msg);
                    mainActivity.playSound(1);
                }
            }

        }
    }

    private boolean stopscanner() {
        ploopFlag = false;
        if(tidBarcodeString != null && !tidBarcodeString.isEmpty()) {
            showdialog();
        }
        if (mainActivity.mReader.isInventorying()) {


            return mainActivity.mReader.stopInventory();
        } else {
            return true;
        }
    }

    private void showdialog() {
        if (!tidBarcodeString.matches("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("TID and Barcode already exist");

            // Set the tidBarcodeString as the message of the AlertDialog
            builder.setMessage(tidBarcodeString);

            // Add a button to dismiss the dialog
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle the button click if needed
                    tidBarcodeString = "";
                }
            });

            // Create and show the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    private void addDataToList(String fepc, String tidv, String rssi) {
//        Toast.makeText(mainActivity, "epc "+fepc, Toast.LENGTH_SHORT).show();
        if (StringUtils.isNotEmpty(fepc) && fepc.length()==24) {
            String epcValue = fepc;//fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
            String tidValue = tidv;//fepc;//fepc.substring(fepc.length() - 24);

            /*String epcValue = fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
            String tidValue = fepc.substring(fepc.length() - 24);*/

            if (StringUtils.isNotEmpty(tidValue)) {
                int index = checkIsExist(tidValue);
                if (index == -1) {
                    if (bsingle) {
                        ploopFlag = false;
                    }

                    Itemmodel nitem = null;
                    nitem = null;//app.checkitem(tidValue, entryDatabase, getActivity());

                        Log.d("check speed", " " + index + "   " + tidv + "   " + "epc" + epcValue + "  " + ssingle + "  " + bsingle + "  " + tempDatas.size());

                        if (bsingle) {
                            ploopFlag = false;
                            if (stopscanner()) {
                                b.bstext.setText("Scan");
                                b.bsimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
                            }
                        }
                        if (nitem == null) {
                            nitem = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, tidValue, epcValue, storageClass.getBranch(),
                                    "", "", "", "", "", "", "", "", "", "", "",
                                    "", "", "", "", "", "Active", TransactionIDGenerator.generateTransactionNumber("B"), "Added", "", "",
                                    "", "", "", "", "", 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                    0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0, 0, 0, "", "","","","",0,0,0,0,0,0,"");

                            itemlist.add(nitem);

                            b.totalitemstxt.setText(String.valueOf(productAdapter.getItemCount()));
                            productAdapter.notifyItemInserted(itemlist.size() - 1);
                        } else {
                            Log.d("check item exist", "  " + nitem.toString());
                            StringBuilder stringBuilder = new StringBuilder();
                            String tidValue1 = nitem.getTidValue();
                            String barcode = nitem.getBarCode();
                            String cat = nitem.getCategory();
                            String pro = nitem.getProduct();
                            stringBuilder.append("Item: ").append(cat).append("/").append(pro).append(" Barcode: ").append(barcode).append("\n");
                            tidBarcodeString = tidBarcodeString + stringBuilder.toString();
                            itemexist = itemexist + stringBuilder.toString();
                            existitems = existitems + 1;
                            b.totalexistitemstxt.setText(String.valueOf(existitems));

                            if (bsingle) {
                                if (tidBarcodeString != null && !tidBarcodeString.isEmpty()) {
                                    showdialog();
                                }
                            }

                        }

                }

                tempDatas.add(tidValue);
            }
        }
    }

    public int checkIsExist(String epc) {
        if (StringUtils.isEmpty(epc)) {
            return -1;
        }
        return binarySearch(tempDatas, epc);
    }


    private int getpvalue(String getppower) {
        if (getppower == null || getppower.isEmpty() || getppower.matches("0")) {
            return 5;
        }
        return Integer.parseInt(getppower);
    }

    private void savesData() {
        Itemmodel i = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, stid, sepc, storageClass.getBranch(), scategory,
                sproduct, spurity, "", "", "", "", "", "", "",
                sbarcode, sitemcode, sbox, shuidcode, spartycode, sdesp, "Active", TransactionIDGenerator.generateTransactionNumber("S"), "Added", "", "",
                "", "", "", "", "", 0, 0, 0, 0,
                getdvalue(sgrosswt), getdvalue(sstonewt), getdvalue(snetwt), getdvalue(smakinggm), getdvalue(smakingper), getdvalue(sfixedamount), getdvalue(sfixedwastage),
                getdvalue(sstoneamount), getdvalue(smrp), getdvalue(shallmark), 1,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, "", "","","","",0,0,0,0,0,0,"");


        itemlist.clear();
        itemlist.add(i);

        entryDatabase.checkdatabase(getActivity());
        entryDatabase.makeentry(getActivity(), itemlist, "adding", "product", app, issueitem, new SaveCallback() {

            @Override
            public void onSaveSuccess() {
                Toast.makeText(mainActivity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
                resetsstate();


            }

            @Override
            public void onSaveFailure(List<Itemmodel> failedItems) {
                Toast.makeText(mainActivity, "Failed to save some items", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private Double getdvalue(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }



    private void showtaost(String s) {
        Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
    }


    //import export
    private void openFilePicker() {
        pcheck.getexcelfile(getActivity());
    }

    private boolean areStoragePermissionsGranted() {
        int readPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private File createfile() {
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 and above
            try {
                file = File.createTempFile("unfilleddata", ".xlsx", getActivity().getFilesDir());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in internal storage: " + e.getMessage());
                return null;
            }
        } else { // Android versions below 10
            try {
                file = File.createTempFile("unfilleddata", ".xlsx", Environment.getExternalStorageDirectory());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in external storage: " + e.getMessage());
                return null;
            }
        }
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_EXCEL_REQUEST:
                if (data != null) {

//                    Uri uri = data.getData();
//                    getheadings(getActivity(), uri);
//                    ReadExcelFile(getActivity()
//                            , uri);

//                    Uri uri = data.getData();
                    Uri uri = data.getData();
                    if (uri != null) {

                        excelopener.processsheet(uri, getActivity(), progressDialog, app);
                    }
//


//                    processExcelInBackground(uri);


                }
            case 2:


        }
    }*/


    @Override
    public void myOnKeyDwon(String barcode) {
        Toast.makeText(getContext(), "scanning", Toast.LENGTH_SHORT).show();
        if (mainActivity.mReader.isInventorying()) {

                b.bsscanlay.performClick();

        } else {

                b.bsscanlay.performClick();

        }
    }

    @Override
    public void onPause() {
        if (mainActivity.mReader.isInventorying()) {
            mainActivity.mReader.stopInventory();
        }
        super.onPause();

    }

    @Override
    public void onDestroy() {
        if (mainActivity.mReader.isInventorying()) {
            mainActivity.mReader.stopInventory();
        }
        super.onDestroy();
    }

}