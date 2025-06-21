package com.loyalstring.fragments;


import static com.loyalstring.MainActivity.binarySearch;
import static com.loyalstring.fsupporters.Pemissionscheck.PICK_EXCEL_REQUEST;
import static com.loyalstring.fsupporters.Pemissionscheck.STORAGE_PERMISSION_READWRITE_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.loyalstring.Activities.ListActivity;
import com.loyalstring.Adapters.ProductAdapter;
import com.loyalstring.Apis.ApiManager;
import com.loyalstring.Apis.ApiProcess;
import com.loyalstring.Apis.RetrofitClient;
import com.loyalstring.Excels.AllExcel;
import com.loyalstring.Excels.Excelopener;
import com.loyalstring.Excels.InventoryExcelCreation;
import com.loyalstring.Excels.XlsReader;
import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestApis.LoginApiSupport.Employee;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.database.StorageClass;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.FragmentProductfragmentBinding;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.fsupporters.Pemissionscheck;
import com.loyalstring.fsupporters.ReaderSingleton;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.FetchRfidCallback;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.network.NetworkUtils;
import com.loyalstring.readersupport.KeyDwonFragment;
import com.loyalstring.tools.StringUtils;
import com.loyalstring.transactionhelper.TransactionIDGenerator;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class productfragment extends KeyDwonFragment implements interfaces.PermissionCallback {
    FragmentProductfragmentBinding b;
    private MainActivity mainActivity;
    String stid, sepc, scategory, sproduct, spurity, sbarcode, sitemcode, sbox, sgrosswt, sstonewt, snetwt, smakinggm,
            smakingper, sfixedamount, sfixedwastage, sstoneamount, smrp, shuidcode, spartycode, sdesp, shallmark;


    EntryDatabase entryDatabase;

    List<Itemmodel> itemlist = new ArrayList<>();

    List<ScannedDataToService> scannedDataToServiceList = new ArrayList<>();
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

            if (ssingle) {
                ploopFlag = false;
            }
            if (bsingle) {
                ploopFlag = false;
            }


            addDataToList(info.getEPC(), info.getTid(), info.getRssi());
        }
    };
    List<String> tempDatas = new ArrayList<String>();

    ProductAdapter productAdapter;
    int existitems = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    Excelopener excelopener;

    AllExcel allexcelreader;
    XlsReader xlsreader;

    private ProgressDialog progressDialog;
    ApiProcess apiprocess;

    Pemissionscheck pcheck;
    interfaces interfaces;

    String tidBarcodeString = "";
    List<Rfidresponse.ItemModel> rfidList = new ArrayList<>();
    List<Issuemode> issueitem = new ArrayList<>();
    NetworkUtils networkUtils;
    List<Itemmodel> ubilllist = new ArrayList<>();
    ApiManager apiManager;
    SharedPreferencesManager sharedPreferencesManager;
    Employee employee;
    Clients clients;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentProductfragmentBinding.inflate(inflater, container, false);


        mainActivity = (MainActivity) getActivity();
        mainActivity.currentFragment = productfragment.this;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Product");

            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }

        pcheck = new Pemissionscheck(getActivity(), this);
        interfaces = new interfaces();
        networkUtils = new NetworkUtils(getActivity());


        apiprocess = new ApiProcess();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiManager = new ApiManager(apiService);
        sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        mainActivity.toolpower.setVisibility(View.VISIBLE);
        mainActivity.toolpower.setText(String.valueOf(mainActivity.mReader.getPower()));
        mainActivity.toolpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "Product", storageClass, mainActivity.toolpower, mainActivity.mReader);
            }
        });

        resetsstate();


        b.bgimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
        b.bgtext.setText("Gscan");
        b.bsimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
        b.bstext.setText("Scan");
        b.singletext.setText("Scan");
        b.singleimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
        storageClass = new StorageClass(getActivity());
        globalcomponents = new Globalcomponents();
        app = (MyApplication) requireActivity().getApplicationContext();
        excelopener = new Excelopener();
        allexcelreader = new AllExcel();
        xlsreader = new XlsReader();
        progressDialog = new ProgressDialog(getActivity());

        b.singlelay.setVisibility(View.VISIBLE);
        b.bulklay.setVisibility(View.GONE);
        b.impexplay.setVisibility(View.GONE);
        entryDatabase = new EntryDatabase(getActivity());

        b.singlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemlist.clear();
                itemexist = "";
                tempDatas.clear();
                b.singlelay.setVisibility(View.VISIBLE);
                b.bulklay.setVisibility(View.GONE);
                b.impexplay.setVisibility(View.GONE);
                resetsstate();
            }
        });
        b.bulkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.bulkrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                productAdapter = new ProductAdapter(itemlist, mainActivity.barcodeDecoder, getActivity());
                b.bulkrecycler.setAdapter(productAdapter);
                resetbstate();

                b.singlelay.setVisibility(View.GONE);
                b.bulklay.setVisibility(View.VISIBLE);
                b.impexplay.setVisibility(View.GONE);

            }
        });
        b.impextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemlist.clear();
                itemexist = "";
                tempDatas.clear();
                b.singlelay.setVisibility(View.GONE);
                b.bulklay.setVisibility(View.GONE);
                b.impexplay.setVisibility(View.VISIBLE);

            }
        });


        b.singlescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssingle = true;
                if (mainActivity.mReader.isInventorying()) {
                    ploopFlag = false;

                    boolean s = stopscanner();
                    if (s) {
                        b.singletext.setText("Scan");
                        b.singleimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
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

        b.singlelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        b.singlereset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        b.sbarno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.showbarcode(getActivity(), b.sbarno, "barcode", mainActivity.barcodeDecoder);
            }
        });
        b.sitemno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.showbarcode(getActivity(), b.sitemno, "itemcode", mainActivity.barcodeDecoder);
            }
        });
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
        b.catgorylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stid == null || stid.matches("")) {
//                    globaltoast(getActivity(), "Please scan tag first", "", "");
                    return;
                }
                globalcomponents.getlist("Category", b.categorytext, getActivity());
            }
        });

        b.productlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b.categorytext.getText().toString().isEmpty() || b.categorytext.getText().toString().equalsIgnoreCase("category")) {
//                    globaltoast(getActivity(), "Please choose category", "", "");
                    return;
                }
                globalcomponents.getprolist(b.categorytext.getText().toString(), b.producttext, getActivity());

            }
        });

        b.puritylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b.categorytext.getText().toString().isEmpty() || b.categorytext.getText().toString().equalsIgnoreCase("category")
                        || b.producttext.getText().toString().isEmpty() || b.producttext.getText().toString().equalsIgnoreCase("product")) {
//                    globaltoast(getActivity(), "Please choose category and product", "", "");
                    return;
                }
                if (b.categorytext.getText().toString().equalsIgnoreCase("diamond")) {
//                    showdiamonddialog();
                } else {
                    if (b.categorytext.getText().toString().isEmpty() || b.categorytext.getText().toString().equalsIgnoreCase("category")) {
//                        globaltoast(getActivity(), "please choose category", "", "");
                        return;
                    }
                    globalcomponents.getpurity(b.categorytext.getText().toString(), b.puritytext, getActivity());


                }
            }
        });
        b.boxlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.getboxes(getActivity(), b.boxtext);
            }
        });
        b.igrossweight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateNetWeight();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        b.istoneweight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateNetWeight();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        b.singlesavelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    // Proceed with saving data
                    savesData();
                }


            }
        });
        b.singlelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainActivity.mReader.isInventorying()) {
                    Toast.makeText(getActivity(), "stop scanning", Toast.LENGTH_SHORT).show();
                    return;
                }
                ReaderSingleton.setReader(mainActivity.mReader);
                ReaderSingleton.setbarcode(mainActivity.barcodeDecoder);
                Intent go = new Intent(getActivity(), ListActivity.class);
                startActivity(go);
            }
        });

        b.singlereset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetsstate();
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
                if (b.bcategorytext.getText().toString().isEmpty() || b.bcategorytext.getText().toString().equalsIgnoreCase("category")) {
                    Toast.makeText(mainActivity, "Please choose category", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (b.bproducttext.getText().toString().isEmpty() || b.bproducttext.getText().toString().equalsIgnoreCase("product")) {
                    Toast.makeText(mainActivity, "Please choose product", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (itemlist.isEmpty()) {
                    Toast.makeText(mainActivity, "no items added", Toast.LENGTH_SHORT).show();
                    return;
                }

                String u = "https://firebasestorage.googleapis.com/v0/b/loyalstrings-3c53b.appspot.com/o/imagestray%2Fth.jpg?alt=media&token=0fbc54b4-e708-42de-8a02-37f33bbd7be1";


                for (Itemmodel entry : itemlist) {
                    entry.setCategory(b.bcategorytext.getText().toString().trim());
                    entry.setProduct(b.bproducttext.getText().toString().trim());
                    String bs = b.boxtext.getText().toString().trim();
                    if (bs.equalsIgnoreCase("box") || bs.isEmpty()) {
                        bs = "";
                    }
                    entry.setBox(bs);
                    entry.setImageUrl(u);


                }

                Log.d("@@","@@"+itemlist.size());

                for(Itemmodel itemmodel:itemlist)
                {
                    /*api call new add all data vasanti*/

                    Clients clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();
                        String clientCode = clients.getClientCode();
                        String androidId="";
                        Log.e("check body client code", "  " + clientCode);
                        if (clientCode != null || !clientCode.isEmpty()) {
                            androidId = Settings.Secure.getString(
                                    getActivity().getContentResolver(),
                                    Settings.Secure.ANDROID_ID
                            );
                            ScannedDataToService scannedDataToService = new ScannedDataToService();
                            try {
                                LocalDateTime currentDateTime = LocalDateTime.now();
                                String formatted = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

                                Log.d("deviceId", "deviceId" +    androidId );
                               scannedDataToService.setClientCode(clientCode);
                                scannedDataToService.setCreatedOn(formatted);
                                scannedDataToService.setLastUpdated(formatted);
                                scannedDataToService.setRFIDCode(itemmodel.getBarCode());
                                scannedDataToService.setTIDValue(itemmodel.getTidValue());
                                scannedDataToService.setStatusType(true);
                                scannedDataToService.setId(0);
                                scannedDataToService.setDeviceId(androidId );
                                scannedDataToServiceList.add(scannedDataToService);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            Gson gson = new Gson();
                            String json = gson.toJson(scannedDataToService);
                            Log.d("JSON  output scanned data", json);


                        }

                }
                if (networkUtils.isNetworkAvailable()) {
                    apiManager.addAllScannedData(scannedDataToServiceList, new interfaces.FetchAllRFIDData() {
                        @Override
                        public void onSuccess(List<ScannedDataToService> result) {
                            if (!result.isEmpty()) {
                                //  entryDatabase.makerfidentry(getActivity(), app, result);
                                // rfidList.addAll(result);
                                Log.e("RfidListCheck", "Rfid Scanned data: " + result.size());
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }

                entryDatabase.checkdatabase(getActivity());
                entryDatabase.makeentry(getActivity(), itemlist, "adding", "product", app, issueitem, new SaveCallback() {
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
        b.blistlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainActivity.mReader.isInventorying()) {
                    Toast.makeText(getActivity(), "stop scanning", Toast.LENGTH_SHORT).show();
                    return;
                }
                ReaderSingleton.setReader(mainActivity.mReader);
                ReaderSingleton.setbarcode(mainActivity.barcodeDecoder);
                Intent go = new Intent(getActivity(), ListActivity.class);
                startActivity(go);
            }
        });

        b.bresetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetbstate();
            }
        });

        //import/export
        //importexport

        employee = sharedPreferencesManager.readLoginData().getEmployee();
        clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();

        b.pimportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openFilePicker();

//                progressDialog.setTitle("loading...");
//                progressDialog.show();
//                fetchrfid(new FetchRfidCallback() {
//                    @Override
//                    public void onFetchCompleted(List<Rfidresponse.ItemModel> rfidList) {
//
//                        progressDialog.dismiss();
//                        if (rfidList.isEmpty()) {
//                            Toast.makeText(mainActivity, "rfid list is empty", Toast.LENGTH_SHORT).show();
//
//                            return;
//                        }
//
//
//
//                        String[] mimetypes = {
//                                "application/vnd.ms-excel", // .xls
//                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
//                                "text/csv", // .csv
//                                "application/csv" // .csv
//                        };
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("*/*");//("application/vnd.ms-excel"); // Set the MIME type to filter only Excel files
//                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_REQUEST);
//                    }
//                });


                String clientcode = employee.getClientCode();
                if (networkUtils.isNetworkAvailable()) {

                    Log.e("check body client code", "  " + clientcode);
                    if (clientcode != null || !clientcode.isEmpty()) {
                        apiManager.fetchallrfid(clientcode, new interfaces.OnRFIDFetched() {
                            @Override
                            public void onSuccess(List<Rfidresponse.ItemModel> result) {
                                if (!result.isEmpty()) {
                                    entryDatabase.makerfidentry(getActivity(), app, result);
                                    rfidList.addAll(result);
//                                    String[] mimetypes = {
//                                            "application/vnd.ms-excel", // .xls
//                                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
//                                            "text/csv", // .csv
//                                            "application/csv" // .csv
//                                    };
//                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                    intent.setType("*/*");//("application/vnd.ms-excel"); // Set the MIME type to filter only Excel files
//                                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                    startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_REQUEST);

                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                } else {
                    rfidList.addAll(entryDatabase.getrfid(getActivity(), app));
                }
                if(clients.getRfidType().contains("reusable") && !rfidList.isEmpty()){
                    String[] mimetypes = {
                            "application/vnd.ms-excel", // .xls
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                            "text/csv", // .csv
                            "application/csv" // .csv
                    };
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//("application/vnd.ms-excel"); // Set the MIME type to filter only Excel files
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_REQUEST);
                }else{

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");

                    String[] mimetypes = {
                            "application/vnd.ms-excel", // .xls
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                            "text/csv" // .csv
                    };
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

                    startActivityForResult(intent, PICK_EXCEL_REQUEST); // NO Chooser here!
                                  }
//                    String[] mimetypes = {
//                            "application/vnd.ms-excel", // .xls
//                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
//                            "text/csv", // .csv
//                            "application/csv" // .csv
//                    };
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("*/*");//("application/vnd.ms-excel"); // Set the MIME type to filter only Excel files
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_REQUEST);
//                }


//                String[] mimetypes = {
//                        "application/vnd.ms-excel", // .xls
//                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
//                        "text/csv", // .csv
//                        "application/csv" // .csv
//                };
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");//("application/vnd.ms-excel"); // Set the MIME type to filter only Excel files
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_REQUEST);

            }
        });

        b.psyncbills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("loading...");
                progressDialog.show();

                fetchbills(new interfaces.Fetchbills() {
                    @Override
                    public void onFetched(List<Itemmodel> items) {
                        if (items.isEmpty()) {
                            Toast.makeText(mainActivity, "no items found", Toast.LENGTH_SHORT).show();

                            return;
                        }

                        entryDatabase.makeentry(getActivity(), items, items.get(0).getTransactionType(), "bill", app, issueitem, new SaveCallback() {
                            @Override
                            public void onSaveSuccess() {
                                Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSaveFailure(List<Itemmodel> failedItems) {
                                Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
                            }
                        });


                        /*TreeMap<String, List<Itemmodel>> groupedItems = new TreeMap<>();

                        items.sort((item1, item2) -> Long.compare(item1.getOperationTime(), item2.getOperationTime()));


                        for (Itemmodel item : items) {
                            String buyerName = item.getCustomerName(); // Get the buyer's name

                            // Initialize the list if not present
                            groupedItems.putIfAbsent(buyerName, new ArrayList<>());
//
//                            // Add the item to the corresponding buyer's list
                            groupedItems.get(buyerName).add(item);

//                            if(!groupedItems.containsKey(buyerName)){
//                                groupedItems.put(buyerName, new ArrayList<>());
//                                groupedItems.get(buyerName).add(item);
//                            }else{
//
//                            }
                        }

                        for (Map.Entry<String, List<Itemmodel>> entry : groupedItems.entrySet()) {
                            List<Itemmodel> itemList = entry.getValue();
                            Set<String> uniqueItems = new HashSet<>(); // Set to track unique items

                            // Create an iterator to safely remove items from the list
                            Iterator<Itemmodel> iterator = itemList.iterator();

                            while (iterator.hasNext()) {
                                Itemmodel m = iterator.next();
                                String uniqueKey = m.getItemCode() + "_" + m.getGrossWt() + "_" + m.getStoneWt() + "_" + m.getNetWt();

                                // If the item is already in the set, it's a duplicate; remove it
                                if (uniqueItems.contains(uniqueKey)) {
                                    iterator.remove();
                                } else {
                                    uniqueItems.add(uniqueKey); // Otherwise, add it to the set
                                }
                            }
                        }

                        List<Map.Entry<String, List<Itemmodel>>> sortedEntries = new ArrayList<>(groupedItems.entrySet());

                        // Sort the entries by the earliest operationTime in each group
                        sortedEntries.sort((entry1, entry2) -> {
                            long time1 = entry1.getValue().get(0).getOperationTime();
                            long time2 = entry2.getValue().get(0).getOperationTime();
                            return Long.compare(time1, time2);
                        });
                        entryDatabase.checkdatabase(getActivity());
                        int invoicenumber = entryDatabase.getinvoicenumber(getActivity());
                        // Log the grouped items to verify
                        for (Map.Entry<String, List<Itemmodel>> entry : sortedEntries) {
                            String buyer = entry.getKey();
//                            List<Itemmodel> itemList = entry.getValue();
//                            Date date = new Date(itemList.get(0).getOperationTime());
//                            System.out.println(buyer + " has bought " + itemList.size() + " items: " + date);

                            List<Itemmodel> itemList = entry.getValue();
                            String invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                            String customerName = entry.getValue().get(0).getCustomerName();

                            String prefix = "";
                            String inv = "";

                            if (invoiceNumber.startsWith("OE")) {
                                prefix = "OE";
                                inv = prefix + invoicenumber;
                            } else if (invoiceNumber.startsWith("E") || invoiceNumber.startsWith("R") || invoiceNumber.startsWith("B") || invoiceNumber.startsWith("O")) {
                                prefix = invoiceNumber.substring(0, 1);
                                inv = prefix + invoicenumber;
                            } else {
                                inv = String.valueOf(invoicenumber);
                            }

                            // Update all items in the itemList with the new invoice number
                            for (Itemmodel item : itemList) {
                                if (customerName.equalsIgnoreCase(item.getCustomerName())) {
                                    item.setInvoiceNumber(inv);
                                }
                            }


                            // Call makeentry for each group


                            invoicenumber++;


                        }*/


                    }
                });

            }
        });

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

        b.pupsyncbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Itemmodel> ml = new ArrayList<>();

                if (app.getInventoryMap().size() > 0) {
                    for (Map.Entry<String, Itemmodel> entry : app.getInventoryMap().entrySet()) {
                        Itemmodel m = new Itemmodel(entry.getValue());
                        m.setCounterId("1");
                        m.setCounterName("name");

                        ml.add(m);
                    }
                }
                if (!ml.isEmpty() && storageClass.getBaseUrl() != null && !storageClass.getBaseUrl().isEmpty()) {
                    apiprocess.updateproduct(ml, getActivity(), storageClass.getBaseUrl());

                }
            }
        });

        b.psheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String clientcode = employee.getClientCode();
                if (networkUtils.isNetworkAvailable()) {

                    Log.e("check body client code", "  " + clientcode);
                    if (clientcode != null || !clientcode.isEmpty()) {
                        apiManager.fetchallrfid(clientcode, new interfaces.OnRFIDFetched() {
                            @Override
                            public void onSuccess(List<Rfidresponse.ItemModel> result) {
                                if (!result.isEmpty()) {
                                    entryDatabase.makerfidentry(getActivity(), app, result);
                                    rfidList.addAll(result);

                                    Log.e("RfidListCheck", "RfidList size: " + rfidList.size());
                                    Log.e("RfidListContent", "RfidList: " + rfidList);

                                    getActivity().runOnUiThread(() -> {

                                        if( !rfidList.isEmpty()){

                                            HashMap<String, Itemmodel> ml = new HashMap<>();
                                            if (app.getInventoryMap().size() > 0) {
                                                for (Map.Entry<String, Itemmodel> entry : app.getInventoryMap().entrySet()) {
                                                    Itemmodel m = new Itemmodel(entry.getValue());
                                                    ml.put(m.getTidValue(), m);
                                                }
                                            }
                                            if (storageClass.getSheeturl() != null && !storageClass.getSheeturl().isEmpty()) {
                                                apiprocess.sheetprocess(ml, getActivity(), storageClass.getSheeturl(), entryDatabase, app, rfidList);
                                            } else {
                                                Toast.makeText(mainActivity, "api url not found", Toast.LENGTH_SHORT).show();
                                            }

                                        }else{

                                            Toast.makeText(mainActivity, "rfid data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                } else {
                    rfidList.addAll(entryDatabase.getrfid(getActivity(), app));
                }

                Log.e("checking  new", "check1 "+clients.getRfidType()+"   "+rfidList);



            }
        });

        b.psyncbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ArrayList<Itemmodel> ml = new ArrayList<>();

                if(!clients.getRfidType().contains("Web")){
                    Toast.makeText(mainActivity, "you not opted for apis ", Toast.LENGTH_SHORT).show();
                return;
                }

                HashMap<String, Itemmodel> ml = new HashMap<>();

//                storageClass.setbaseurl("http://43.241.147.162:9991/WS/");
                String urll = "";
                urll = storageClass.getBaseUrl();
                if(urll == null || urll.isEmpty()){
                    //urll = "https://testing.loyalstring.co.in/";
                    urll="https://rrgold.loyalstring.co.in/";
                }


                if (app.getInventoryMap().size() > 0) {
                    for (Map.Entry<String, Itemmodel> entry : app.getInventoryMap().entrySet()) {
                        Itemmodel m = new Itemmodel(entry.getValue());
                        ml.put(m.getTidValue(), m);
                    }
                    apiprocess.getproductsn(ml, getActivity(), urll, rfidList, entryDatabase, app, clients.getRfidType());

                } else {
                    apiprocess.getproductsn(ml, getActivity(), urll, rfidList, entryDatabase, app, clients.getRfidType());

                }
//                apiprocess.getproductsn(ml, getActivity(), storageClass.getBaseUrl(), rfidList, entryDatabase, app);


                /*progressDialog.setTitle("loading...");
                progressDialog.show();
                fetchrfid(new FetchRfidCallback() {
                    @Override
                    public void onFetchCompleted(List<Rfidresponse.ItemModel> rfidList) {

                        progressDialog.dismiss();
                        if (rfidList.isEmpty()) {
                            Toast.makeText(mainActivity, "rfid list is empty", Toast.LENGTH_SHORT).show();

                            return;
                        }

                        HashMap<String, Itemmodel> ml = new HashMap<>();
                        if (app.getInventoryMap().size() > 0) {
                            for (Map.Entry<String, Itemmodel> entry : app.getInventoryMap().entrySet()) {
                                Itemmodel m = new Itemmodel(entry.getValue());
                                ml.put(m.getTidValue(), m);
                            }
                        }
//                if (storageClass.getBaseUrl() != null && !storageClass.getBaseUrl().isEmpty()) {
//                    apiprocess.getproductsn(ml, getActivity(), storageClass.getBaseUrl(), storageClass.getrfidUrl(), entryDatabase, app);
                        apiprocess.getproductsn(ml, getActivity(), storageClass.getBaseUrl(), rfidList, entryDatabase, app, clients.getRfidType());
//                } else {
//                    Toast.makeText(mainActivity, "api url not found", Toast.LENGTH_SHORT).show();
//                }


                    }
                });*/

            }
        });


        return b.getRoot();
    }

    private void fetchbills(com.loyalstring.interfaces.interfaces.Fetchbills fetchbills) {
        ubilllist.clear();
        if (networkUtils.isNetworkAvailable()) {
            Toast.makeText(mainActivity, "no network", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        FirebaseDatabase.getInstance().getReference().child("settings").child("data").child("testing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String fetch = snapshot.child("transaction").getValue(String.class);
                            if (fetch.equals("yes")) {
                                FirebaseDatabase.getInstance().getReference().child("transactiondata").child("testing").child("Sheet1")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snap : snapshot.getChildren()) {
                                                    try {
                                                        Itemmodel item = snap.getValue(Itemmodel.class);
                                                        ubilllist.add(item);
                                                    } catch (DatabaseException e) {
                                                        Log.e("Firebase Error", "Data conversion error: " + e.getMessage());
                                                        // Handle the error, possibly skip this item or notify the user
                                                    }
                                                }
                                                progressDialog.dismiss();
                                                fetchbills.onFetched(ubilllist);
                                                Log.e("check billlis", "check " + ubilllist.size());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                            } else {
                                Toast.makeText(mainActivity, "no permission", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mainActivity, "error " + error, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void fetchrfid(FetchRfidCallback fetchRfidCallback) {
        rfidList.clear();
        if (networkUtils.isNetworkAvailable()) {

            FirebaseDatabase.getInstance().getReference().child("settings").child("data").child("navgran")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String fetch = snapshot.child("rfid").getValue(String.class);
                                if (fetch.equals("yes")) {

                                    FirebaseDatabase.getInstance().getReference().child("rfiddata").child("shudh").child("Sheet1")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot snap : snapshot.getChildren()) {

                                                        String barcode = snap.child("SN").getValue(String.class);
                                                        String tid = snap.child("TID").getValue(String.class);
                                                        Log.e("checkrfid", "yes" + barcode + "  " + tid);
                                                        if (barcode != null && !barcode.isEmpty() && tid != null && !tid.isEmpty()) {
                                                            Rfidresponse.ItemModel i = new Rfidresponse.ItemModel(tid, barcode);
                                                            rfidList.add(i);
                                                        }
                                                    }
                                                    entryDatabase.makerfidentry(getActivity(), app, rfidList);
                                                    fetchRfidCallback.onFetchCompleted(rfidList);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(mainActivity, "failed to fetch rfid data" + error, Toast.LENGTH_SHORT).show();
                                                    fetchRfidCallback.onFetchCompleted(rfidList);
                                                }

                                            });


                                } else {
                                    fetchFromDatabase();

                                }

                            } else {
                                fetchFromDatabase();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(mainActivity, "failed to fetch rfid data" + error, Toast.LENGTH_SHORT).show();
                            fetchRfidCallback.onFetchCompleted(rfidList);
                        }

                        private void fetchFromDatabase() {
                            rfidList.addAll(entryDatabase.getrfid(getActivity(), app));
                            fetchRfidCallback.onFetchCompleted(rfidList);
                        }
                    });

        } else {

            rfidList.addAll(entryDatabase.getrfid(getActivity(), app));
            fetchRfidCallback.onFetchCompleted(rfidList);

        }
    }

    private void checkfolder() {
        boolean folder = globalcomponents.checkfileexist("product");
        if (folder) {
            File cfile = createfile();
            if (cfile != null) {
//                            readitems(getActivity(), importfragment.this, "", inventorylist, "export", cfile);
                ArrayList<Itemmodel> ml = new ArrayList<>();
                if (app.getInventoryMap().size() > 0) {
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

                } else {
                    Toast.makeText(mainActivity, "No data found", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), "failed to create file", Toast.LENGTH_SHORT).show();
            }
        } else {
            ArrayList<String> folders = new ArrayList<>();
            folders.add("product");
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

                        String extension = getFileExtension(getActivity(), uri);
                        HashMap<String, Itemmodel> ml = new HashMap<>();
                        if (app.getInventoryMap().size() > 0) {
                            for (Map.Entry<String, Itemmodel> entry : app.getInventoryMap().entrySet()) {
                                Itemmodel m = new Itemmodel(entry.getValue());
                                ml.put(m.getTidValue(), m);
                            }
                        }

                        if ("xls".equalsIgnoreCase(extension)) {
                            // Handle .xls file
//                            processXlsFile(is);
                            xlsreader.processsheet(ml, uri, getActivity(), progressDialog, app, rfidList, clients.getRfidType());
                        } else if ("xlsx".equalsIgnoreCase(extension)) {
                            // Handle .xlsx file
//                            processXlsxFile(is);
                            excelopener.processsheet(uri, getActivity(), progressDialog, app, rfidList);
                        } else if ("csv".equalsIgnoreCase(extension)) {
                            // Handle .csv file
//                            processCsvFile(is);
                        } else {
                            throw new IllegalArgumentException("Unsupported file type");
                        }

//                        excelopener.processsheet(uri, getActivity(), progressDialog, app, rfidList);
//                        allexcelreader.processsheet(uri, getActivity(), progressDialog, app, rfidList);
                    }
                }
                break;
            case STORAGE_PERMISSION_READWRITE_CODE:
                // Handle storage permission result if needed
                checkfolder();
                break;
        }


    }

    public static String getFileExtension(Context context, Uri uri) {
        String extension = null;

        // Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            // If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            // If scheme is a File
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }


    @Override
    public void onPermissionGranted(String s, Intent data) {

        if (s.equalsIgnoreCase("excelopen")) {
            Uri uri = data.getData();
            if (uri != null) {

                excelopener.processsheet(uri, getActivity(), progressDialog, app, rfidList);
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
        b.sbarno.setText("Barcode");
        b.sitemno.setText("Itemcode");
        b.categorytext.setText("Category");
        b.producttext.setText("Product");
        b.puritytext.setText("Purity");
        b.boxtext.setText("Box");
        b.igrossweight.setText("");
        b.istoneweight.setText("");
        b.inetweight.setText("");
        tempDatas.clear();

        b.singlebarlay.setVisibility(View.GONE);
        itemlist.clear();
        itemexist = "";
    }

    private void calculateNetWeight() {
        sgrosswt = b.igrossweight.getText().toString();
        sstonewt = b.istoneweight.getText().toString();
        snetwt = b.inetweight.getText().toString();
//        Log.d("tag", "checkword  " + diamondweight);
//        if (diamondweight == null || diamondweight.isEmpty()) {
//            diamondweight = "0";
//        }
        if (sgrosswt.isEmpty()) {
            sgrosswt = "0";
        }
        if (sstonewt.isEmpty()) {
            sstonewt = "0";
        }
        if (snetwt.isEmpty()) {
            snetwt = "0";
        }
        double g = Double.parseDouble(sgrosswt);
        double s = Double.parseDouble(sstonewt);
//        double d = Double.parseDouble(diamondweight);
        b.inetweight.setText(decimalFormat.format(g - s));
    }

    private void performsinglescan() {
//        if(!mainActivity.mReader.setEPCAndTIDMode()){
//            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
//            return;
//        }
        /*if(!mainActivity.mReader.setFastID(true)){
            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (!mainActivity.mReader.setEPCMode()) {
            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
            return;
        }

        readTag();
    }

    private void readTag() {
        if (mainActivity.mReader.startInventoryTag()) {
            b.totalitemstxt.setText("0");

            ploopFlag = true;
            if (b.singlelay.getVisibility() == View.VISIBLE) {
                b.singletext.setText("Stop");
                b.singleimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancelblack));
            }
            if (b.bulklay.getVisibility() == View.VISIBLE) {
                if (bsingle) {
                    b.bsimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancelblack));
                    b.bstext.setText("Stop");
                } else {
                    b.bgimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancelblack));
                    b.bgtext.setText("Stop");
                }
            }
//            mainActivity.mReader.setFastID(true);
            new TagThread().start();

        } else {
            if (stopscanner()) {
                if (b.singlelay.getVisibility() == View.VISIBLE) {
                    b.singletext.setText("Scan");
                    b.singleimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
                }
                if (b.bulklay.getVisibility() == View.VISIBLE) {
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
        if (tidBarcodeString != null && !tidBarcodeString.isEmpty()) {
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

    private void addDataToLists(String fepc, String tidv, String rssi) {


    }

    private void addDataToListbs(String fepc, String tidv, String rssi) {

    }

    private void addDataToListbb(String fepc, String tidv, String rssi) {

    }

    private void addDataToList(String fepc, String tidv, String rssi) {
//        Toast.makeText(mainActivity, "epc "+fepc, Toast.LENGTH_SHORT).show();
        if (StringUtils.isNotEmpty(fepc) && fepc.length() == 24) {

//            Random random = new Random();
//            int randomNumber = random.nextInt(20001);
//            textView.setText(String.valueOf(randomNumber));


//            String epcValue = String.valueOf(randomNumber);// fepc;//fepc.substring(0, 24);
//            // Extract TID value (last 24 digits)
//            String tidValue = String.valueOf(randomNumber);//fepc+System.currentTimeMillis();//fepc;//fepc.substring(fepc.length() - 24);

            /*String epcValue = fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
            String tidValue = fepc.substring(fepc.length() - 24);*/

            String epcValue = fepc;
            // Extract TID value (last 24 digits)
            String tidValue = fepc;

            if (StringUtils.isNotEmpty(tidValue)) {
                int index = checkIsExist(tidValue);
                /*Itemmodel nitem = null;


//                Itemmodel nitem = null;
                nitem = app.checkitem(tidValue, entryDatabase, getActivity());
                if(nitem == null) {
                    nitem = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, tidValue, epcValue, storageClass.getBranch(),

                            "", "", "", "", "", "", "", "", "", "", "",
                            "", "", "", "", "", "Active", TransactionIDGenerator.generateTransactionNumber("B"), "Added", "", "",
                            "", "", "", "", "", 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, "", "");

                    itemlist.add(nitem);
                    b.totalitemstxt.setText(String.valueOf(Integer.parseInt(b.totalitemstxt.getText().toString()) + 1));
                    productAdapter.notifyItemInserted(itemlist.size() - 1);
                    totalc = totalc+1;
                }*/
                if (index == -1) {
                    if (bsingle) {
                        ploopFlag = false;
                    }

                    Itemmodel nitem = null;
                    nitem = app.checkitem(tidValue, entryDatabase, getActivity());

                    if (ssingle) {
                        ploopFlag = false;
                        if (stopscanner()) {
                            b.singletext.setText("Scan");
                            b.singleimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
                        }
                        if (nitem == null) {
                            stid = tidValue;
                            sepc = epcValue;
                            b.singlebarlay.setVisibility(View.VISIBLE);
                        } else {
//                    Itemmodel item = inventorydata.get(tidv);
                            StringBuilder stringBuilder = new StringBuilder();
                            String tidValue1 = nitem.getTidValue();
                            String barcode;
                            if(nitem.getBarCode()==null){
                                barcode = nitem.getItemCode();
                            }else {
                                barcode = nitem.getBarCode();
                            }
                            String cat = nitem.getCategory();
                            String pro = nitem.getProduct();
                            stringBuilder.append("Item: ").append(cat).append("/").append(pro).append(" Barcode: ").append(barcode).append("\n");
//
                            tidBarcodeString = stringBuilder.toString();
                            if (tidBarcodeString != null && !tidBarcodeString.isEmpty()) {
                                showdialog();
                            }

//                    itemexist = itemexist + stringBuilder.toString();
                        }
                    } else {

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
                                    0, 0, 0, 0, 0, 0, 0, 0, 0, "", "","","","",0,0,0,0,0,0);

                            itemlist.add(nitem);

                            b.totalitemstxt.setText(String.valueOf(productAdapter.getItemCount()));
                            productAdapter.notifyItemInserted(itemlist.size() - 1);
                        } else {
                            Log.d("check item exist", "  " + nitem.toString());
                            StringBuilder stringBuilder = new StringBuilder();
                            String tidValue1 = nitem.getTidValue();
                            String barcode;
                            if(nitem.getBarCode()==null){
                                 barcode = nitem.getItemCode();
                            }else {
                                 barcode = nitem.getBarCode();
                            }
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
                0, 0, 0, 0, 0, 0, "", "","","","",0,0,0,0,0,0);


        itemlist.clear();
        itemlist.add(i);



        Log.e("checking ", "check1 old "+clients.getRfidType()+"   "+rfidList);

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

    private boolean validateInput() {
        scategory = b.categorytext.getText().toString().trim();
        sproduct = b.producttext.getText().toString().trim();
        spurity = b.puritytext.getText().toString().trim();
        sbarcode = b.sbarno.getText().toString().trim();
        sitemcode = b.sitemno.getText().toString().trim();
        sgrosswt = b.igrossweight.getText().toString().trim();
        sstonewt = b.istoneweight.getText().toString().trim();
        snetwt = b.inetweight.getText().toString().trim();
        if (stid == null || stid.isEmpty() || sepc == null || sepc.isEmpty()) {
            showtaost("failed to read tag");
            return false;
        }

        if (scategory == null || scategory.isEmpty() || scategory.equalsIgnoreCase("category")) {
            showtaost("Please choose category");
            return false;
        }
        if (sproduct == null || sproduct.isEmpty() || sproduct.equalsIgnoreCase("product")) {
            showtaost("Please choose product");
            return false;
        }
        if (spurity == null || spurity.isEmpty() || spurity.equalsIgnoreCase("purity")) {
            showtaost("Please choose purity");
            return false;
        }
        if (sbarcode == null || sbarcode.isEmpty() || sbarcode.equalsIgnoreCase("barcode")) {

            showtaost("Please choose barcode");
            return false;
        }
        if (sitemcode == null || sitemcode.isEmpty() || sitemcode.equalsIgnoreCase("itemcode")) {
            showtaost("Please choose itemcode");
            return false;
        }
        if (sgrosswt == null || sgrosswt.isEmpty()) {
            showtaost("Please choose gross weight");
            return false;
        }
        if (sstonewt == null || sstonewt.isEmpty()) {
            showtaost("Please choose stone weight");
            return false;
        }
        if (snetwt == null || snetwt.isEmpty()) {
            showtaost("Please choose net weight");
            return false;
        }

        return true;
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
            if (b.singlelay.getVisibility() == View.VISIBLE) {
                b.singlescan.performClick();
            }
            if (b.bulklay.getVisibility() == View.VISIBLE) {
                b.bsscanlay.performClick();
            }
        } else {
            if (b.singlelay.getVisibility() == View.VISIBLE) {
                b.singlescan.performClick();
            }
            if (b.bulklay.getVisibility() == View.VISIBLE) {
                b.bsscanlay.performClick();
            }
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