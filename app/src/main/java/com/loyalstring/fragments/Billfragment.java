package com.loyalstring.fragments;

import static com.loyalstring.MainActivity.binarySearch;
import static com.loyalstring.MainActivity.decimalFormat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loyalstring.Activities.Billlistactivity;
import com.loyalstring.Adapters.BillAdapter;
import com.loyalstring.Adapters.BillAdapterbottom;
import com.loyalstring.Adapters.UserDatumAdapter;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.database.StorageClass;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.FragmentBillfragmentBinding;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.jjjcustomermodel;
import com.loyalstring.network.NetworkUtils;
import com.loyalstring.readersupport.KeyDwonFragment;
import com.loyalstring.tools.StringUtils;
import com.loyalstring.transactionhelper.TransactionIDGenerator;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.deviceapi.entity.BarcodeEntity;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Billfragment extends KeyDwonFragment implements BillAdapter.Removeitem, interfaces.ItemUpdateListener, BillAdapterbottom.Removebottomitem {
    FragmentBillfragmentBinding b;
    List<String> bottomlist;

    String transactionno = "";
    MyApplication myApplication;
    MainActivity mainActivity;
    List<String> tempDatas = new ArrayList<String>();
    Globalcomponents globalcomponents;
    StorageClass storageClass;
    HashMap<String, Itemmodel> totalitems = new HashMap<>();
    TreeMap<String, Itemmodel> searchitems = new TreeMap<>();
    TreeMap<String, Itemmodel> bottomitem = new TreeMap<>();
    Map<String, Itemmodel> alllsit = new HashMap<>();
    boolean ploopFlag = false;
    BillAdapter billAdapter;
    BillAdapterbottom billAdapterbottom;
    double totalgwt = 0;
    double totalnwt = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UHFTAGInfo info = (UHFTAGInfo) msg.obj;
            Log.d("checktidva", "t" + info.getTid() + " e" + info.getEPC() + " r" + info.getReserved() + " " + info.getUser() + "  " + info.toString());
            addDataToList(info.getEPC(), info.getTid(), info.getRssi());
        }
    };

    private Handler mHandler = new Handler();

    EntryDatabase entryDatabase;
    int invoicenumber = 0;
    List<Issuemode> issueitem = new ArrayList<>();
    int count = 0;
    int bottomcount = 0;

    String operation = "";
    ArrayList<Itemmodel> updatedBillList = new ArrayList<>();

    List<jjjcustomermodel.UserDatum> clist = new ArrayList<>();
    private List<jjjcustomermodel.UserDatum> originalList = new ArrayList<>();
    private UserDatumAdapter adapter;

    NetworkUtils networkUtils;
    String userid = "";


//    private BarcodeDecoder mbarcodereader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = FragmentBillfragmentBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

//        globalcomponents = new Globalcomponents();
//        storageClass = new StorageClass(getActivity());

        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Bill");
            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }
        globalcomponents = new Globalcomponents();
        storageClass = new StorageClass(getActivity());
        myApplication = (MyApplication) requireActivity().getApplicationContext();
        entryDatabase = new EntryDatabase(getActivity());
//         myApplication = new MyApplication();

        mainActivity.toolpower.setVisibility(View.VISIBLE);
        mainActivity.toolpower.setText(String.valueOf(mainActivity.mReader.getPower()));
        mainActivity.toolpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "transaction", storageClass, mainActivity.toolpower, mainActivity.mReader);
            }
        });


        bottomlist = new ArrayList<>();
        networkUtils = new NetworkUtils(getActivity());
        if (networkUtils.isNetworkAvailable()) {
            String url = storageClass.getSheeturl();

            fetchUserDatafromsheet(getActivity(), url);
        } else {
            fetchofflnedata();
        }


        mainActivity.currentFragment = Billfragment.this;
        if (!myApplication.isCountMatch()) {
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
                    totalitems = myApplication.getInventoryMap();

                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Check for count match
                    while (!myApplication.isCountMatch()) {
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
            totalitems = myApplication.getInventoryMap();

        }

        b.tcatgorylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetDialog("tt");
            }
        });

        b.trecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        billAdapter = new BillAdapter(getActivity(), searchitems, this, this);
        b.trecycler.setAdapter(billAdapter);
        b.brecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
        b.scanbtntext.setText("Scan");


        entryDatabase.checkdatabase(getActivity());
        invoicenumber = entryDatabase.getinvoicenumber(getActivity());
        if (operation.equalsIgnoreCase("update")) {

            String in = updatedBillList.get(0).getInvoiceNumber();
            if (in.startsWith("OE")) {
                String numericPart = in.substring(2);
                invoicenumber = Integer.parseInt(numericPart);
            } else if (in.startsWith("E") || in.startsWith("R") || in.startsWith("B") || in.startsWith("O")) {
                String numericPart = in.substring(2);
                invoicenumber = Integer.parseInt(numericPart);
            } else {
                invoicenumber = Integer.parseInt(in);
            }

        }


        Log.e("check invnumber", "  " + invoicenumber);

        b.tinvoiceno.setText(String.valueOf(invoicenumber));
        b.scanbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (totalitems.isEmpty()) {
                    Toast.makeText(mainActivity, "No data found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mainActivity.mReader.isInventorying()) {
                    ploopFlag = false;
                    boolean s = stopscanner();
                    if (s) {
                        b.scanbtntext.setText("Scan");
                        b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_scanblack));
//                        stopTemperatureCheck(getActivity());
                    } else {
                        Toast.makeText(mainActivity, "failed to stop scanning", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    stopUpdatingUI();
                    boolean checkpower = globalcomponents.checkpower(getActivity(), mainActivity.mReader, getpvalue(storageClass.gettpower()), mainActivity.toolpower);
                    if (checkpower) {

                        performsinglescan();
                    } else {
                        Toast.makeText(mainActivity, "failed to set power", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        b.tcategorytext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String ot = editable.toString();
                if (ot.equalsIgnoreCase("order estimation")) {

                    b.bottomholder.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = b.trecycler;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                    params.weight = 1;
                    recyclerView.setLayoutParams(params);


                    billAdapterbottom = new BillAdapterbottom(getActivity(), bottomitem, Billfragment.this, Billfragment.this);
                    b.brecycler.setAdapter(billAdapterbottom);

                } else {
                    b.bottomholder.setVisibility(View.GONE);
                    RecyclerView recyclerView = b.trecycler;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                    params.weight = 2;
                    recyclerView.setLayoutParams(params);

                }

            }
        });


        //S0100054-4
        b.listbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(getActivity(), Billlistactivity.class);
                startActivity(go);

            }
        });


        b.resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetstate();
                billAdapter.notifyDataSetChanged();
            }
        });

        b.tname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(myApplication, "loading", Toast.LENGTH_SHORT).show();
                openBottomSheetDialog1("Customer Name");
            }
        });


        b.billbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (b.tcategorytext.getText().toString().trim().equalsIgnoreCase("Transaction Type")) {
                    Toast.makeText(getActivity(), "choose transaction type", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (b.tname.getText().toString().isEmpty()) {
                    Toast.makeText(myApplication, "Please enter customer name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (b.tinvoiceno.getText().toString().isEmpty()) {
                    Toast.makeText(myApplication, "Please enter invoice number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (searchitems.isEmpty()) {
                    Toast.makeText(myApplication, "Please add items for billing", Toast.LENGTH_SHORT).show();
                    return;
                }

                showBillingPopup();


//                String tty = b.tcategorytext.getText().toString().trim();
//                String inv = "";
//                if (tty.equalsIgnoreCase("Estimation")) {
//                    inv = "E" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Reserved")) {
//                    inv = "R" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Bill")) {
//                    inv = "B" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Sample in")) {
//                    inv = "SI" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Sample out")) {
//                    inv = "SO" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("order")) {
//                    inv = "O" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("order estimation")) {
//                    inv = "OE" + b.tinvoiceno.getText().toString().trim();
//                }
//
//
//                List<Itemmodel> itemlist = new ArrayList<>();
//                itemlist.addAll(searchitems.values());
//                for (Itemmodel item : itemlist) {
//                    item.setDescription("");
//                    item.setDescription(b.tmelting.getText().toString());
//                    item.setDiamondSize(userid);
//                    item.setCustomerName(b.tname.getText().toString().trim());
//                    if (!operation.equalsIgnoreCase("update")) {
//                        item.setOperation(b.tcategorytext.getText().toString().trim());
//                    } else {
//                        item.setOperation(b.tcategorytext.getText().toString().trim() + "update");
//                    }
//                    item.setInvoiceNumber(inv);
//                    item.setOperationTime(System.currentTimeMillis());
//                    item.setTransactionType(tty);
//
//                }
//
//                entryDatabase.checkdatabase(getActivity());
//
//
//                Log.e("billupdate", "check 1" + itemlist.size() + "  " + itemlist.toString());
//                Log.e("billupdate", "check 1" + issueitem.size() + "  " + issueitem.toString());
//
//
//                if (!operation.equalsIgnoreCase("update")) {
//                    entryDatabase.makeentry(getActivity(), itemlist, tty, "bill", myApplication, issueitem, new SaveCallback() {
//
//                        @Override
//                        public void onSaveSuccess() {
//                            Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
//                            resetstate();
//                            invoicenumber = entryDatabase.getinvoicenumber(getActivity());
//                            b.tinvoiceno.setText(String.valueOf(invoicenumber));
//                            billAdapter.notifyDataSetChanged();
//
//
//                        }
//
//                        @Override
//                        public void onSaveFailure(List<Itemmodel> failedItems) {
//                            Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//
//                    entryDatabase.makeentry(getActivity(), itemlist, tty, "billupdate", myApplication, issueitem, new SaveCallback() {
//
//                        @Override
//                        public void onSaveSuccess() {
//                            Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
//                            resetstate();
//                            invoicenumber = entryDatabase.getinvoicenumber(getActivity());
//                            b.tinvoiceno.setText(String.valueOf(invoicenumber));
//                            billAdapter.notifyDataSetChanged();
//
//
//                        }
//
//                        @Override
//                        public void onSaveFailure(List<Itemmodel> failedItems) {
//                            Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }

            }
        });


        b.scanbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mainActivity.barcodeDecoder
                mainActivity.barcodeDecoder.startScan();

                mainActivity.barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
                    @Override
                    public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                        Log.e("TAG", "BarcodeDecoder==========================:" + barcodeEntity.getResultCode());
                        if (barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS) {
//                            editText.setText(barcodeEntity.getBarcodeData());

                            b.tbarcode.setText(barcodeEntity.getBarcodeData());
                            manualadd(barcodeEntity.getBarcodeData());
                            /*for (Map.Entry<String, Itemmodel> entry : totalitems.entrySet()) {
                                String key = entry.getKey();
                                Itemmodel value = entry.getValue();
                                if(value.getBarCode().equalsIgnoreCase(barcodeEntity.getBarcodeData())){
                                    Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                                    searchitems.put(String.valueOf(count), item);
                                    totalgwt = totalgwt + item.getGrossWt();
                                    totalnwt = totalnwt + item.getNetWt();
                                    tempDatas.add(value.getTidValue());
                                    b.tbarcode.setText("");
                                    count ++;
                                    billAdapter.notifyDataSetChanged();


                                }
                            }*/


                            Log.e("TAG", "data==========================:" + barcodeEntity.getBarcodeData());
                        } else {
//                            editText.setText("");
//                        showtoast("Failed to read bar code");
                        }
                    }
                });

            }
        });

        b.addusebarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (b.tbarcode.getText().toString().trim().isEmpty()) {
                    Toast.makeText(myApplication, "Please enter barcode", Toast.LENGTH_SHORT).show();
                }

                manualadd(b.tbarcode.getText().toString().trim());


            }

        });

        b.fineadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchitems.isEmpty()) {
                    Toast.makeText(myApplication, "Please scan/add items", Toast.LENGTH_SHORT).show();

                    return;
                }
                String finper = b.fineper.getText().toString();
                String wasper = b.wastper.getText().toString();

                if (finper.isEmpty()) {
                    Toast.makeText(myApplication, "Please add fine percentage", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (wasper.isEmpty()) {
                    Toast.makeText(myApplication, "Please add waste percentage", Toast.LENGTH_SHORT).show();

                    return;
                }
                double tg = 0;
                double tn = 0;
                double st = 0;
                double tfine = 0;
                double tfix = 0;
                for (Map.Entry<String, Itemmodel> entry : searchitems.entrySet()) {
                    String key = entry.getKey();
                    Itemmodel titem = entry.getValue();
                    if (titem.getItemAddmode().equals("yes")) {
//                        titem.setDiamondSize(String.valueOf(bottomcount));
//                        titem.setBilladdCount(String.valueOf(bottomcount));
                        titem.setFixedWastage(Double.parseDouble(wasper));
                        titem.setMakingPer(Double.parseDouble(finper));
                        tg = tg + titem.getGrossWt();
                        tn = tn + titem.getNetWt();
                        st = st + titem.getStoneAmount();
                        tfix = tfix + titem.getFixedWastage() + titem.getMakingPer();
                        tfine = (titem.getNetWt() * (titem.getFixedWastage() + titem.getMakingPer())) / 100;
                        titem.setItemAddmode("no");
                    }
                    Log.e("checktop4", "allitems " + titem.toString());
                }
                Itemmodel item1 = new Itemmodel();
                item1.setGrossWt(tg);
                item1.setNetWt(tn);
                item1.setStoneAmount(st);
                item1.setFixedWastage(tfix);
                item1.setMakingPer(tfine);
                bottomitem.put(String.valueOf(bottomcount), item1);
                bottomcount++;

                billAdapter = new BillAdapter(getActivity(), searchitems, Billfragment.this, Billfragment.this);
                b.trecycler.setAdapter(billAdapter);
                billAdapterbottom.notifyDataSetChanged();
//                billAdapter.notifyDataSetChanged();


            }
        });


        // Inflate the layout for this fragment
        return b.getRoot();
    }

    private void fetchofflnedata() {
        clist.addAll(entryDatabase.getcustomer(getActivity(), myApplication));
    }

    // Method to show the popup dialog
    private void showBillingPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Billing Details");

        // Create a layout for the dialog
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);

        // Create input fields
        EditText branchInput = new EditText(getActivity());
        branchInput.setHint("Branch");
        layout.addView(branchInput);

        EditText viaInput = new EditText(getActivity());
        viaInput.setHint("Via");
        layout.addView(viaInput);

        // KT Dropdown (Spinner)
        Spinner ktSpinner = new Spinner(getActivity());
        ArrayAdapter<String> ktAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Select KT", "18KT", "22KT", "24KT"});
        ktSpinner.setAdapter(ktAdapter);
        layout.addView(ktSpinner);


        // KT Dropdown (Spinner)
        Spinner tagSpinner = new Spinner(getActivity());
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Seperate tags", "yes", "no"});
        tagSpinner.setAdapter(tagAdapter);
        layout.addView(tagSpinner);


        // Screw Dropdown (Spinner)
        Spinner screwSpinner = new Spinner(getActivity());
        ArrayAdapter<String> screwAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Select Screw Type", "ss", "s", "ssns","Hook","Plain Dendi"});
        screwSpinner.setAdapter(screwAdapter);
        layout.addView(screwSpinner);

        builder.setView(layout);

        // Set "Save & Continue" button
        builder.setPositiveButton("Save & Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get input values
                String branch = branchInput.getText().toString().trim();
                String via = viaInput.getText().toString().trim();
                String kt = ktSpinner.getSelectedItem().toString();
                String screw = screwSpinner.getSelectedItem().toString();
                String tags = tagSpinner.getSelectedItem().toString();


                // Validate Inputs
                if (branch.isEmpty() || kt.isEmpty()) {
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with billing
                proceedWithBilling(branch, via, kt, screw, tags);
            }
        });

        // Set "Cancel" button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to proceed with billing after saving popup inputs
    private void proceedWithBilling(String branch, String via, String kt, String screw, String tags) {
        String tty = b.tcategorytext.getText().toString().trim();
        String inv = "";

        if (tty.equalsIgnoreCase("Estimation")) {
            inv = "E" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Reserved")) {
            inv = "R" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Bill")) {
            inv = "B" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Sample in")) {
            inv = "SI" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Sample out")) {
            inv = "SO" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("order")) {
            inv = "O" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("order estimation")) {
            inv = "OE" + b.tinvoiceno.getText().toString().trim();
        }

        List<Itemmodel> itemlist = new ArrayList<>(searchitems.values());
        for (Itemmodel item : itemlist) {
            item.setDescription("");
            item.setDescription(b.tmelting.getText().toString());
            item.setDiamondSize(userid);
            item.setCustomerName(b.tname.getText().toString().trim());
            if (!operation.equalsIgnoreCase("update")) {
                item.setOperation(b.tcategorytext.getText().toString().trim());
            } else {
                item.setOperation(b.tcategorytext.getText().toString().trim() + "update");
            }
            item.setInvoiceNumber(inv);
            item.setOperationTime(System.currentTimeMillis());
            item.setTransactionType(tty);

            // Save additional inputs
            item.setBranch(branch);
            item.setDiamondCertificate(via);
            item.setStockKeepingUnit(kt);
            item.setDiamondColor(screw);
            item.setDiamondMetal(tags);
        }

        entryDatabase.checkdatabase(getActivity());

        Log.e("billupdate", "check 1 " + itemlist.size() + "  " + itemlist.toString());
        Log.e("billupdate", "check 2 " + issueitem.size() + "  " + issueitem.toString());


        if (!operation.equalsIgnoreCase("update")) {
            entryDatabase.makeentry(getActivity(), itemlist, tty, "bill", myApplication, issueitem, new SaveCallback() {

                @Override
                public void onSaveSuccess() {
                    Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
                    resetstate();
                    invoicenumber = entryDatabase.getinvoicenumber(getActivity());
                    b.tinvoiceno.setText(String.valueOf(invoicenumber));
                    billAdapter.notifyDataSetChanged();


                }

                @Override
                public void onSaveFailure(List<Itemmodel> failedItems) {
                    Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            entryDatabase.makeentry(getActivity(), itemlist, tty, "billupdate", myApplication, issueitem, new SaveCallback() {

                @Override
                public void onSaveSuccess() {
                    Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
                    resetstate();
                    invoicenumber = entryDatabase.getinvoicenumber(getActivity());
                    b.tinvoiceno.setText(String.valueOf(invoicenumber));
                    billAdapter.notifyDataSetChanged();


                }

                @Override
                public void onSaveFailure(List<Itemmodel> failedItems) {
                    Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }



    private void openBottomSheetDialog1(String title) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity()); // or the appropriate context
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_customersheet_layout, null);

        TextView titleTextView = bottomSheetView.findViewById(R.id.title);
        titleTextView.setText(title);

        EditText searchEditText = bottomSheetView.findViewById(R.id.searchEditText);
        TextView addButton = bottomSheetView.findViewById(R.id.addButton);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Save the original list
        originalList = new ArrayList<>(clist); // Copy the original list to a new list for filtering

        // Initialize the adapter with the user list
        adapter = new UserDatumAdapter(originalList, new UserDatumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(jjjcustomermodel.UserDatum userDatum) {
                // Set the selected name to your TextView
                b.tname.setText(userDatum.getFull_name());
                userid = userDatum.getUser_id();
                bottomSheetDialog.dismiss(); // Close the bottom sheet
            }
        });

        recyclerView.setAdapter(adapter);

        // Add TextWatcher for search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().toLowerCase();
                List<jjjcustomermodel.UserDatum> filteredList = new ArrayList<>();

                // Filter the original list based on the search query
                for (jjjcustomermodel.UserDatum userDatum : originalList) {
                    if (userDatum.getFull_name().toLowerCase().contains(query)) {
                        filteredList.add(userDatum);
                    }
                }
                // Update the adapter with the filtered list
                adapter = new UserDatumAdapter(filteredList, new UserDatumAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(jjjcustomermodel.UserDatum userDatum) {
                        // Set the selected name to your TextView
                        b.tname.setText(userDatum.getFull_name());
                        bottomSheetDialog.dismiss(); // Close the bottom sheet
                    }
                });
                recyclerView.setAdapter(adapter); // Re-set the adapter with the filtered list
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        addButton.setOnClickListener(v -> {
            String newName = searchEditText.getText().toString().trim();

            if (!newName.isEmpty()) {
                boolean exists = false;

                // Check if the name already exists
                for (jjjcustomermodel.UserDatum userDatum : originalList) {
                    if (userDatum.getFull_name().equalsIgnoreCase(newName)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    // Add the new name to the list
                    jjjcustomermodel.UserDatum newUserDatum = new jjjcustomermodel.UserDatum();
                    newUserDatum.setFull_name(newName);
                    newUserDatum.setUser_id("0"); // You can assign a temporary ID
                    originalList.add(0, newUserDatum); // Add to the top of the list
                    adapter.notifyItemInserted(0);

                    // Set the new name to the TextView
                    b.tname.setText(newName);
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Name already exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

  /*  private void manualadd(String s) {
        for (Map.Entry<String, Itemmodel> entry : totalitems.entrySet()) {
            String key = entry.getKey();
            Itemmodel value = entry.getValue();
//            if (!value.getBarCode().equalsIgnoreCase(s)) {
//                Toast.makeText(myApplication, "item not exist", Toast.LENGTH_SHORT).show();
//            return;
//            }
            if (value.getBarCode().equalsIgnoreCase(s)) {
                if(tempDatas.contains(value.getTidValue())){
                    Toast.makeText(myApplication, "item already added", Toast.LENGTH_SHORT).show();

                    return;
                }

                Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                item.setItemAddmode("yes");
                searchitems.put(String.valueOf(count), item);
                totalgwt = totalgwt + item.getGrossWt();
                totalnwt = totalnwt + item.getNetWt();

                tempDatas.add(value.getTidValue());
                b.tbarcode.setText("");
                count++;
                billAdapter.notifyDataSetChanged();
                b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                b.tdtotalamount.setText(decimalFormat.format(totalnwt));

            }
        }

    }*/

    private void manualadd(String s) {
        boolean found = false;
        for (Map.Entry<String, Itemmodel> entry : totalitems.entrySet()) {
            String key = entry.getKey();
            Itemmodel value = entry.getValue();
            Log.d("@@","Bar code"+value.getBarCode());
//            if (!value.getBarCode().equalsIgnoreCase(s)) {
//                Toast.makeText(myApplication, "item not exist", Toast.LENGTH_SHORT).show();
//            return;
//            }


            //  for (Itemmodel value : list) {
            if (value.getBarCode().equalsIgnoreCase(s)) {
                Log.d("@@111","Bar code"+value.getBarCode());
                found = true;

                if (tempDatas.contains(value.getTidValue())) {
                    Toast.makeText(myApplication, "Item already added", Toast.LENGTH_SHORT).show();
                    break;
                }

                alllsit = entryDatabase.getBilledItems(getActivity());
                if (alllsit != null && !alllsit.isEmpty()) {
                    for (Itemmodel m1 : alllsit.values()) {
                        if (m1.getItemCode() != null &&
                                m1.getItemCode().equalsIgnoreCase(value.getItemCode())) {
                            value.setItemAddmode(m1.getItemAddmode());
                            break;
                        }
                    }

                    if ("yes".equalsIgnoreCase(value.getItemAddmode())) {
                        Toast.makeText(myApplication, "Item Already Sold", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                // Add item
                Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                item.setItemAddmode("yes");
                searchitems.put(String.valueOf(count), item);
                totalgwt += item.getGrossWt();
                totalnwt += item.getNetWt();

                tempDatas.add(value.getTidValue());
                b.tbarcode.setText("");
                count++;
                billAdapter.notifyDataSetChanged();
                b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                b.tdtotalamount.setText(decimalFormat.format(totalnwt));

                break; // Done processing
            }
        }

        if (!found) {
            Toast.makeText(myApplication, "Please Check Item Is Available or Not In Stock", Toast.LENGTH_SHORT).show();
        }
    }



    private void resetstate() {
        searchitems.clear();
        b.tinvoiceno.setText("");
        b.tname.setText("");
        b.tcategorytext.setText("Transaction Type");
        tempDatas.clear();
        totalgwt = 0;
        totalnwt = 0;
        b.tdtotalitems.setText("");
        b.tdtotalamount.setText("");
        b.tdtotalgwt.setText("");
        invoicenumber = entryDatabase.getinvoicenumber(getActivity());
        b.tinvoiceno.setText(String.valueOf(invoicenumber));
        issueitem.clear();

    }

    private void performsinglescan() {
//        if(!mainActivity.mReader.setFastID(true)){
//            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (!mainActivity.mReader.setEPCMode()) {
            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
            return;
        }

        readTag();

    }

    private void readTag() {
        if (mainActivity.mReader.startInventoryTag()) {
            ploopFlag = true;
            globalcomponents.keepScreenOn(true, mainActivity);
            startUpdatingUI();
            b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_cancelblack));
            b.scanbtntext.setText("Stop");
            new TagThread().start();

        } else {
            if (stopscanner()) {

                b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
                b.scanbtntext.setText("Scan");

            }
        }
    }

    @Override
    public void onRemoveitem(Itemmodel i, int position, String itemKey) {

//        if (searchitems.containsKey(i.getTidValue())) {
        Toast.makeText(myApplication, "check " + itemKey, Toast.LENGTH_SHORT).show();
        if (operation.equalsIgnoreCase("update")) {

            Issuemode is = new Issuemode(searchitems.get(itemKey));
            issueitem.add(is);
        }
        searchitems.remove(itemKey);
        totalgwt = totalgwt - i.getGrossWt();
        totalnwt = totalnwt - i.getNetWt();
        //Log.d("@@","Stone 3nd"+searchitems.get(0).getStoneAmount());
        billAdapter = new BillAdapter(getActivity(), searchitems, this, this);
        b.trecycler.setAdapter(billAdapter);

        b.tdtotalitems.setText(String.valueOf(searchitems.size()));
        b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
        b.tdtotalamount.setText(decimalFormat.format(totalnwt));
//        }
        tempDatas.remove(i.getTidValue());

        billAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemUpdated(Itemmodel updatedItem, double gwt, double swt, double nwt) {

        totalgwt = totalgwt - gwt + updatedItem.getGrossWt();
        totalnwt = totalnwt - nwt + updatedItem.getNetWt();
        b.tdtotalitems.setText(String.valueOf(searchitems.size()));
        b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
        b.tdtotalamount.setText(decimalFormat.format(totalnwt));
        billAdapter.notifyDataSetChanged();


    }

    @Override
    public void onRemovebottomitem(Itemmodel i, int position, String itemKey, String type) {
        if (type.equalsIgnoreCase("remove")) {
            bottomitem.remove(itemKey);

            List<String> keysToRemove = new ArrayList<>();
            for (Map.Entry<String, Itemmodel> entry : searchitems.entrySet()) {
                String key = entry.getKey();
                Itemmodel item = entry.getValue();
                if (item.getBilladdCount().equals(itemKey)) {
                    keysToRemove.add(key);
                }
            }

            // Remove the collected keys from searchitems
            for (String key : keysToRemove) {
                searchitems.remove(key);
            }

        } else {
//            TreeMap<String, Itemmodel> searchitems1 = new TreeMap<String, Itemmodel>();

            for (Map.Entry<String, Itemmodel> entry : searchitems.entrySet()) {
                String key = entry.getKey();
                Itemmodel item = entry.getValue();
                if (item.getBilladdCount().equals(itemKey)) {
                    item.setItemAddmode("yes");

//                    searchitems1.put(key, item);
                    Log.e("checktop3", "check " + itemKey + " " + item.getBilladdCount());
                }
            }
            billAdapter = new BillAdapter(getActivity(), searchitems, this, this);
            b.trecycler.setAdapter(billAdapter);
            bottomitem.remove(itemKey);

        }

        billAdapterbottom.notifyDataSetChanged();
        billAdapter.notifyDataSetChanged();

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

    private void startUpdatingUI() {
        mHandler.post(updateUIRunnable1);
    }

    // Method to stop updating UI
    private void stopUpdatingUI() {
        mHandler.removeCallbacks(updateUIRunnable1);
    }

  /*  private void addDataToList(String fepc, String tidv, String rssi) {
        Log.d("check fastid ", "  " + fepc + " " + tidv + "  " + rssi);

        if (StringUtils.isNotEmpty(fepc)) {
//            String epcValue = fepc;//fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
//            String tidValue = fepc;//fepc.substring(fepc.length() - 24);

            *//*String epcValue = fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
            String tidValue = fepc.substring(fepc.length() - 24);*//*

            String trimmedTidValue = fepc;
            if (fepc.startsWith("00")) {
                // Compare ignoring the first two characters
                trimmedTidValue = fepc.substring(2); // Remove "00"
            }


            String epcValue = trimmedTidValue;
            // Extract TID value (last 24 digits)
            String tidValue = trimmedTidValue;

            if (StringUtils.isNotEmpty(tidValue)) {
                int index = checkIsExist(tidValue);
                if (index == -1) {
                    if (totalitems.containsKey(tidValue)) {
                        Itemmodel item = new Itemmodel(totalitems.get(tidValue));
                        item.setItemAddmode("yes");
                        searchitems.put(String.valueOf(count), item);
                        totalgwt = totalgwt + item.getGrossWt();
                        totalnwt = totalnwt + item.getNetWt();
                        count++;
                    }
                    tempDatas.add(tidValue);
                }
            }

        }
    }*/
  private void addDataToList(String fepc, String tidv, String rssi) {
      Log.d("check fastid ", "  " + fepc + " " + tidv + "  " + rssi);

      if (StringUtils.isNotEmpty(fepc)) {
//            String epcValue = fepc;//fepc.substring(0, 24);
          // Extract TID value (last 24 digits)
//            String tidValue = fepc;//fepc.substring(fepc.length() - 24);

            /*String epcValue = fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
            String tidValue = fepc.substring(fepc.length() - 24);*/

          String trimmedTidValue = fepc;
        /*  if (fepc.startsWith("00")) {
              // Compare ignoring the first two characters
              trimmedTidValue = fepc.substring(2); // Remove "00"
          }*/


          String epcValue = trimmedTidValue;
          // Extract TID value (last 24 digits)
          String tidValue = trimmedTidValue;

          if (StringUtils.isNotEmpty(tidValue)) {
              int index = checkIsExist(tidValue);
              if (index == -1) {
                  if (totalitems.containsKey(tidValue)) {
                      Itemmodel item = new Itemmodel(totalitems.get(tidValue));
                      item.setItemAddmode("yes");
                      searchitems.put(String.valueOf(count), item);
                      totalgwt = totalgwt + item.getGrossWt();
                      totalnwt = totalnwt + item.getNetWt();
                      count++;
                  }
                  tempDatas.add(tidValue);
              }
          }

      }
  }

    public int checkIsExist(String epc) {
        if (StringUtils.isEmpty("epc")) {
            return -1;
        }
        return binarySearch(tempDatas, epc);
    }

    private final Runnable updateUIRunnable1 = new Runnable() {
        @Override
        public void run() {

            billAdapter.notifyDataSetChanged();

            b.tdtotalitems.setText(String.valueOf(searchitems.size()));
            b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
            b.tdtotalamount.setText(decimalFormat.format(totalnwt));

            if (ploopFlag) {
                mHandler.postDelayed(this, 500); // 50 milliseconds interval for 20 updates per second
            }

        }
    };


    private boolean stopscanner() {
        Log.d("removed", "handle");
        ploopFlag = false;
        stopUpdatingUI();
        globalcomponents.keepScreenOn(false, getActivity());
        if (mainActivity.mReader.isInventorying()) {

            return mainActivity.mReader.stopInventory();
        } else {
            return true;
        }
    }

    @Override
    public void myOnKeyDwon(String process) {
//        super.myOnKeyDwon();
        if (process.equalsIgnoreCase("scan")) {
            b.scanbtn1.performClick();
        } else {
            b.scanbarcode.performClick();
        }

    }

    private int getpvalue(String power) {
        if (power == null || power.isEmpty() || power.matches("0")) {
            return 5;
        } else {
            return Integer.parseInt(power);
        }
    }

    private void openBottomSheetDialog(String s) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.setCancelable(false);

        View contentView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        bottomSheetDialog.setContentView(contentView);

        ImageButton close = contentView.findViewById(R.id.closeButton);
        TextView title = contentView.findViewById(R.id.maintitle);
        Button addbtn = contentView.findViewById(R.id.additem);
        EditText itemname = contentView.findViewById(R.id.itemname);
        ListView spinnerlist = contentView.findViewById(R.id.spinnerlist);
        LinearLayout bholder = contentView.findViewById(R.id.beholder);

        bholder.setVisibility(View.GONE);

        itemname.setVisibility(View.GONE);
        addbtn.setVisibility(View.GONE);

        if (s.matches("tt")) {
            bottomlist.clear();
           // bottomlist.add("Order");
            bottomlist.add("Order Estimation");
            bottomlist.add("Estimation");
          /*  bottomlist.add("Reserved");
            bottomlist.add("Bill");
            bottomlist.add("Sample in");
            bottomlist.add("Sample out");
            title.setText("Transaction Type");*/
        }
//        else if (s.matches("si")) {
//            title.setText("Sample In");
//            bottomlist.clear();
//            bottomlist.add("Repair");
//            bottomlist.add("Order");
//        }
        else {
            title.setText(b.tcategorytext.getText().toString());
            bottomlist.clear();
            bottomlist.add("Repair");
            bottomlist.add("Order");
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });


//        final List<String>[] allItemNames = new List[]{};//new List[]{categoryDb.getAllItemNames(s.toLowerCase(Locale.ROOT))};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, bottomlist);
        spinnerlist.setAdapter(adapter);


        spinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selecteditem = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(requireContext(), "Selected " + s + " : " + selecteditem, Toast.LENGTH_SHORT).show();
//                transactionno.setText("");

                if (s.matches("tt")) {
                    if (!selecteditem.equalsIgnoreCase("Estimation") && !selecteditem.equalsIgnoreCase("reserved") && !selecteditem.equalsIgnoreCase("order") && !selecteditem.equalsIgnoreCase("order estimation")) {
                        Toast.makeText(getActivity(), "not enabled", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selecteditem.equalsIgnoreCase("order")) {
                        billAdapter.updatebilltype("order");
                    } else {
                        billAdapter.updatebilltype("");
                    }

                    b.tcategorytext.setText(selecteditem);
                    transactionno = TransactionIDGenerator.generateBillRepairTransactionID("E");
//                    if (selecteditem.toLowerCase(Locale.ROOT).matches("sample in")
//                            || selecteditem.toLowerCase(Locale.ROOT).matches("sample out")) {
//
//                        tsamplelay.setVisibility(View.VISIBLE);
//
//                    } else if (selecteditem.toLowerCase(Locale.ROOT).matches("bill")) {
//                    tsamplelay.setVisibility(View.GONE);
//                    transactionno.setText(TransactionIDGenerator.generateBillRepairTransactionID("E"));
//                    }
                } else {
//                    tsampletext.setText(selecteditem);
//                    if (selecteditem.toLowerCase(Locale.ROOT).matches("repair")) {
//                        transactionno.setText(TransactionIDGenerator.generateRepairTransactionID());
//                    } else {
//                        transactionno.setText(TransactionIDGenerator.generateOrderTransactionID());
//                    }

                }
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("BillPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("updatedBillList", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Itemmodel>>() {
            }.getType();
            updatedBillList = gson.fromJson(json, type);
            if (!updatedBillList.isEmpty()) {
                operation = "update";
            }

            if (operation.equalsIgnoreCase("update")) {

                String in = updatedBillList.get(0).getInvoiceNumber();
                if (in.startsWith("OE")) {
                    String numericPart = in.substring(2);
                    invoicenumber = Integer.parseInt(numericPart);
                } else if (in.startsWith("E") || in.startsWith("R") || in.startsWith("B") || in.startsWith("O")) {
                    String numericPart = in.substring(1);
                    invoicenumber = Integer.parseInt(numericPart);
                } else {
                    invoicenumber = Integer.parseInt(in);
                }
                Log.e("check invnumber", "  " + invoicenumber);

                b.tinvoiceno.setText(String.valueOf(invoicenumber));
                b.tname.setText(updatedBillList.get(0).getCustomerName());
                //here need to set id and vocher number
                b.tmelting.setText(updatedBillList.get(0).getDescription());
                userid = updatedBillList.get(0).getDiamondShape();


                b.tcategorytext.setText(updatedBillList.get(0).getTransactionType());

                searchitems.clear();
                bottomitem.clear();
                Map<String, Itemmodel> tempMap = new HashMap<>();
                Map<String, Integer> countMap = new HashMap<>();

                for (Itemmodel item : updatedBillList) {
                    if (item.getTransactionType().equalsIgnoreCase("order estimation")) {

                        String fixedWastage = String.valueOf(item.getFixedWastage());
                        String makingPer = String.valueOf(item.getMakingPer());
                        String key = fixedWastage + "_" + makingPer;
                        if (!countMap.containsKey(key)) {
                            countMap.put(key, bottomcount);
                            bottomcount++;
                        }

                        // Get the billaddCount for the current key
                        int billAddCount = countMap.get(key);

                        // Update or create item in tempMap
                        if (!tempMap.containsKey(key)) {
                            Itemmodel newItem = new Itemmodel(item);
                            newItem.setGrossWt(item.getGrossWt());
                            newItem.setNetWt(item.getNetWt());
                            newItem.setStoneAmount(item.getStoneAmount());
                            newItem.setBilladdCount(String.valueOf(billAddCount));
                            newItem.setFixedWastage(item.getFixedWastage());
                            newItem.setMakingPer(item.getMakingPer());
                            newItem.setAvlQty(1);
                            tempMap.put(key, newItem);
                        } else {
                            // Update the existing item in tempMap
                            Itemmodel existingItem = tempMap.get(key);
                            existingItem.setGrossWt(existingItem.getGrossWt() + item.getGrossWt());
                            existingItem.setNetWt(existingItem.getNetWt() + item.getNetWt());
                            existingItem.setStoneAmount(existingItem.getStoneAmount() + item.getStoneAmount());
                            existingItem.setFixedWastage(existingItem.getFixedWastage() + item.getFixedWastage());
                            existingItem.setMakingPer(existingItem.getMakingPer() + item.getMakingPer());
                        }

                        // Add to searchitems
                        Itemmodel item1 = new Itemmodel(item);
                        item1.setItemAddmode("no");
                        item1.setBilladdCount(String.valueOf(billAddCount));
                        searchitems.put(String.valueOf(count), item1);
                        count++;
                        totalgwt += item1.getGrossWt();
                        totalnwt += item1.getNetWt();


                    } else {
                        Itemmodel item1 = new Itemmodel(item);
                        item1.setItemAddmode("yes");
                        searchitems.put(String.valueOf(count), item1);
                        count++;
                        totalgwt = totalgwt + item1.getGrossWt();
                        totalnwt = totalnwt + item1.getNetWt();
                    }
                }


                for (Map.Entry<String, Itemmodel> entry : tempMap.entrySet()) {
                    String key = entry.getKey();
                    Itemmodel aggregatedItem = entry.getValue();

                    double tnwt = aggregatedItem.getNetWt();
                    double wa = aggregatedItem.getFixedWastage();
                    double m = aggregatedItem.getMakingPer();

                    double tfix = wa + m;
                    double tfine = (tnwt * (tfix)) / 100;
                    aggregatedItem.setFixedWastage(tfix);
                    aggregatedItem.setMakingPer(tfine);

                    bottomitem.put(aggregatedItem.getBilladdCount(), aggregatedItem);
                }


                b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                b.tdtotalamount.setText(decimalFormat.format(totalnwt));
                if (!bottomitem.isEmpty()) {
                    billAdapterbottom.notifyDataSetChanged();
                }
                billAdapter.notifyDataSetChanged();

            }
            // Optionally, clear the saved list if you only want to use it once
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("updatedBillList");
            editor.apply();
        }
    }


    private void fetchUserDatafromsheet(FragmentActivity activity, String sheeturl) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading data");
        dialog.show();

        String url = "https://docs.google.com/spreadsheets/d/" + sheeturl + "/gviz/tq?tqx=out:json&sheet=Sheet1";
        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,

                response -> {
                    try {
                        clist.clear();
                        HashMap<String, Itemmodel> nmap = new HashMap<>();
                        List<Itemmodel> dmap = new ArrayList<>();
                        Log.d("Sheet Response", ""+response);
                        String jsonString = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                        JSONObject jsonObject = new JSONObject(jsonString);
//                        JSONObject table = jsonObject.getJSONObject("table");
//                        JSONArray rows = table.getJSONArray("rows");
                        JSONObject table = jsonObject.optJSONObject("table");
                        if (table == null) {
                            Log.e("Sheet Error", "Table data is missing");
                            dialog.dismiss();
                            return;
                        }
                        JSONArray rows = table.optJSONArray("rows");
                        if (rows == null || rows.length() == 0) {
                            Log.e("Sheet Error", "Rows are empty or missing");
                            dialog.dismiss();
                            return;
                        }


                        for (int i1 = 0; i1 < rows.length(); i1++) {
                            JSONObject entryObj = rows.getJSONObject(i1);
                            JSONArray rowData = entryObj.getJSONArray("c");
                            jjjcustomermodel.UserDatum data = new jjjcustomermodel.UserDatum();
                            String stonewt = "0";
                            // Extract relevant data
                            String branch = rowData.optJSONObject(12) != null ? rowData.getJSONObject(12).optString("v", "") : "";
                            String category = rowData.optJSONObject(10) != null ? rowData.getJSONObject(10).optString("v", "") : "";

                            if(branch != null && !branch.isEmpty()){
                                data.setFull_name(branch);
                                data.setUser_id(String.valueOf(i1+1));

                                clist.add(data);
                            }

                        }
                        if(clist.isEmpty()){
                            dialog.dismiss();
                        }
                        Log.e("check clist ", " "+clist.get(1).getFull_name());
                        Collections.sort(clist, (o1, o2) -> o1.getFull_name().compareTo(o2.getFull_name()));
                        entryDatabase.makecustomer(getActivity(), myApplication, clist);

                        dialog.dismiss();
                    } catch (JSONException e) {
                        dialog.dismiss();
                     //   Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
                  //  Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("check error", errorMessage);
                    } else {
                        Log.e("check error", "Unknown error occurred.");
                    }
                });

        queue.add(stringRequest);
    }

//    private void fetchUserData() {
//        String url = "https://jjj.panel.jewelmarts.in/callback/UserData";
//        OkHttpClient client = new OkHttpClient();
//
//        // Create form data
//        RequestBody formBody = new FormBody.Builder()
//                .add("username", "RFID")
//                .add("password", "Rg^%6mkj676G%$)jhAZ")
//                .build();
//
//        // Create the request
//        Request request = new Request.Builder()
//                .url(url)
//                .post(formBody)
//                .build();
//
//        // Execute the request
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                // Handle failure
//                getActivity().runOnUiThread(() -> {
//                    Toast.makeText(getActivity(), "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                if (response.isSuccessful()) {
//                    String responseData = response.body().string();
//                    // Handle the response data here
//                    processUserData(responseData);
//                    Collections.sort(clist, (o1, o2) -> o1.getFull_name().compareTo(o2.getFull_name()));
//
//                    entryDatabase.makecustomer(getActivity(), myApplication, clist);
//
//                } else {
//                    // Handle error response
//                    getActivity().runOnUiThread(() -> {
//                        Toast.makeText(getActivity(), "Error code: " + response.code(), Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }
//        });
//    }

//    private void processUserData(String responseData) {
//        try {
//            JSONObject jsonResponse = new JSONObject(responseData);
//            int ack = jsonResponse.getInt("ack");
//            if (ack == 1) {
//
//                JSONArray userDataArray = jsonResponse.getJSONArray("user_data");
//
//                // Iterate through the user_data array
//                for (int i = 0; i < userDataArray.length(); i++) {
//                    JSONObject userObject = userDataArray.getJSONObject(i);
//                    jjjcustomermodel.UserDatum data = new jjjcustomermodel.UserDatum();
//
//                    // Extract user_id and full_name from each user object
//                    String name = userObject.getString("full_name");
//                    String id = userObject.getString("user_id");
//                    Log.e("check userresponse", "  " + name + "   " + id);
//                    data.setFull_name(name);
//                    data.setUser_id(id);
//                    clist.add(data);
//                }
//            } else {
//                Log.e("User Data Error", "Acknowledgment failed");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("User Data Error", "Failed to parse JSON: " + e.getMessage());
//        }
//    }


}

/*
package com.loyalstring.fragments;

import static com.loyalstring.MainActivity.binarySearch;
import static com.loyalstring.MainActivity.decimalFormat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loyalstring.Activities.Billlistactivity;
import com.loyalstring.Adapters.BillAdapter;
import com.loyalstring.Adapters.BillAdapterbottom;
import com.loyalstring.Adapters.UserDatumAdapter;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.database.StorageClass;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.FragmentBillfragmentBinding;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.jjjcustomermodel;
import com.loyalstring.network.NetworkUtils;
import com.loyalstring.readersupport.KeyDwonFragment;
import com.loyalstring.tools.StringUtils;
import com.loyalstring.transactionhelper.TransactionIDGenerator;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.deviceapi.entity.BarcodeEntity;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Billfragment extends KeyDwonFragment implements BillAdapter.Removeitem, interfaces.ItemUpdateListener, BillAdapterbottom.Removebottomitem {
    FragmentBillfragmentBinding b;
    List<String> bottomlist;

    String transactionno = "";
    MyApplication myApplication;
    Map<String, Itemmodel> alllsit = new HashMap<>();
    MainActivity mainActivity;
    List<String> tempDatas = new ArrayList<String>();
    Globalcomponents globalcomponents;
    StorageClass storageClass;
    HashMap<String, Itemmodel> totalitems = new HashMap<>();
    TreeMap<String, Itemmodel> searchitems = new TreeMap<>();
    TreeMap<String, Itemmodel> bottomitem = new TreeMap<>();
    boolean ploopFlag = false;
    BillAdapter billAdapter;
    BillAdapterbottom billAdapterbottom;
    double totalgwt = 0;
    double totalnwt = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UHFTAGInfo info = (UHFTAGInfo) msg.obj;
            Log.d("checktidva", "t" + info.getTid() + " e" + info.getEPC() + " r" + info.getReserved() + " " + info.getUser() + "  " + info.toString());
            addDataToList(info.getEPC(), info.getTid(), info.getRssi());
        }
    };

    private Handler mHandler = new Handler();

    EntryDatabase entryDatabase;
    int invoicenumber = 0;
    List<Issuemode> issueitem = new ArrayList<>();
    int count = 0;
    int bottomcount = 0;

    String operation = "";
    ArrayList<Itemmodel> updatedBillList = new ArrayList<>();

    List<jjjcustomermodel.UserDatum> clist = new ArrayList<>();
    private List<jjjcustomermodel.UserDatum> originalList = new ArrayList<>();
    private UserDatumAdapter adapter;

    NetworkUtils networkUtils;
    String userid = "";


//    private BarcodeDecoder mbarcodereader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = FragmentBillfragmentBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

//        globalcomponents = new Globalcomponents();
//        storageClass = new StorageClass(getActivity());

        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Bill");
            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }
        globalcomponents = new Globalcomponents();
        storageClass = new StorageClass(getActivity());
        myApplication = (MyApplication) requireActivity().getApplicationContext();
        entryDatabase = new EntryDatabase(getActivity());
//         myApplication = new MyApplication();

        mainActivity.toolpower.setVisibility(View.VISIBLE);
        mainActivity.toolpower.setText(String.valueOf(mainActivity.mReader.getPower()));
        mainActivity.toolpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalcomponents.changepowerg(getActivity(), "transaction", storageClass, mainActivity.toolpower, mainActivity.mReader);
            }
        });


        bottomlist = new ArrayList<>();
        networkUtils = new NetworkUtils(getActivity());
        if (networkUtils.isNetworkAvailable()) {
            String url = storageClass.getSheeturl();

            fetchUserDatafromsheet(getActivity(), url);
        } else {
            fetchofflnedata();
        }


        mainActivity.currentFragment = Billfragment.this;
        if (!myApplication.isCountMatch()) {
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
                    totalitems = myApplication.getInventoryMap();

                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Check for count match
                    while (!myApplication.isCountMatch()) {
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
            totalitems = myApplication.getInventoryMap();

        }

        b.tcatgorylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetDialog("tt");
            }
        });

        b.trecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        billAdapter = new BillAdapter(getActivity(), searchitems, this, this);
        b.trecycler.setAdapter(billAdapter);
        b.brecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
        b.scanbtntext.setText("Scan");


        entryDatabase.checkdatabase(getActivity());
        invoicenumber = entryDatabase.getinvoicenumber(getActivity());
        if (operation.equalsIgnoreCase("update")) {

            String in = updatedBillList.get(0).getInvoiceNumber();
            if (in.startsWith("OE")) {
                String numericPart = in.substring(2);
                invoicenumber = Integer.parseInt(numericPart);
            } else if (in.startsWith("E") || in.startsWith("R") || in.startsWith("B") || in.startsWith("O")) {
                String numericPart = in.substring(2);
                invoicenumber = Integer.parseInt(numericPart);
            } else {
                invoicenumber = Integer.parseInt(in);
            }

        }


        Log.e("check invnumber", "  " + invoicenumber);

        b.tinvoiceno.setText(String.valueOf(invoicenumber));
        b.scanbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (totalitems.isEmpty()) {
                    Toast.makeText(mainActivity, "No data found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mainActivity.mReader.isInventorying()) {
                    ploopFlag = false;
                    boolean s = stopscanner();
                    if (s) {
                        b.scanbtntext.setText("Scan");
                        b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_scanblack));
//                        stopTemperatureCheck(getActivity());
                    } else {
                        Toast.makeText(mainActivity, "failed to stop scanning", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    stopUpdatingUI();
                    boolean checkpower = globalcomponents.checkpower(getActivity(), mainActivity.mReader, getpvalue(storageClass.gettpower()), mainActivity.toolpower);
                    if (checkpower) {

                        performsinglescan();
                    } else {
                        Toast.makeText(mainActivity, "failed to set power", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        b.tcategorytext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String ot = editable.toString();
                if (ot.equalsIgnoreCase("order estimation")) {

                    b.bottomholder.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = b.trecycler;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                    params.weight = 1;
                    recyclerView.setLayoutParams(params);


                    billAdapterbottom = new BillAdapterbottom(getActivity(), bottomitem, Billfragment.this, Billfragment.this);
                    b.brecycler.setAdapter(billAdapterbottom);

                } else {
                    b.bottomholder.setVisibility(View.GONE);
                    RecyclerView recyclerView = b.trecycler;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                    params.weight = 2;
                    recyclerView.setLayoutParams(params);

                }

            }
        });


        //S0100054-4
        b.listbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(getActivity(), Billlistactivity.class);
                startActivity(go);

            }
        });


        b.resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetstate();
                billAdapter.notifyDataSetChanged();
            }
        });

        b.tname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(myApplication, "loading", Toast.LENGTH_SHORT).show();
                openBottomSheetDialog1("Customer Name");
            }
        });


        b.billbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (b.tcategorytext.getText().toString().trim().equalsIgnoreCase("Transaction Type")) {
                    Toast.makeText(getActivity(), "choose transaction type", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (b.tname.getText().toString().isEmpty()) {
                    Toast.makeText(myApplication, "Please enter customer name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (b.tinvoiceno.getText().toString().isEmpty()) {
                    Toast.makeText(myApplication, "Please enter invoice number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (searchitems.isEmpty()) {
                    Toast.makeText(myApplication, "Please add items for billing", Toast.LENGTH_SHORT).show();
                    return;
                }

                showBillingPopup();


//                String tty = b.tcategorytext.getText().toString().trim();
//                String inv = "";
//                if (tty.equalsIgnoreCase("Estimation")) {
//                    inv = "E" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Reserved")) {
//                    inv = "R" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Bill")) {
//                    inv = "B" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Sample in")) {
//                    inv = "SI" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("Sample out")) {
//                    inv = "SO" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("order")) {
//                    inv = "O" + b.tinvoiceno.getText().toString().trim();
//                }
//                if (tty.equalsIgnoreCase("order estimation")) {
//                    inv = "OE" + b.tinvoiceno.getText().toString().trim();
//                }
//
//
//                List<Itemmodel> itemlist = new ArrayList<>();
//                itemlist.addAll(searchitems.values());
//                for (Itemmodel item : itemlist) {
//                    item.setDescription("");
//                    item.setDescription(b.tmelting.getText().toString());
//                    item.setDiamondSize(userid);
//                    item.setCustomerName(b.tname.getText().toString().trim());
//                    if (!operation.equalsIgnoreCase("update")) {
//                        item.setOperation(b.tcategorytext.getText().toString().trim());
//                    } else {
//                        item.setOperation(b.tcategorytext.getText().toString().trim() + "update");
//                    }
//                    item.setInvoiceNumber(inv);
//                    item.setOperationTime(System.currentTimeMillis());
//                    item.setTransactionType(tty);
//
//                }
//
//                entryDatabase.checkdatabase(getActivity());
//
//
//                Log.e("billupdate", "check 1" + itemlist.size() + "  " + itemlist.toString());
//                Log.e("billupdate", "check 1" + issueitem.size() + "  " + issueitem.toString());
//
//
//                if (!operation.equalsIgnoreCase("update")) {
//                    entryDatabase.makeentry(getActivity(), itemlist, tty, "bill", myApplication, issueitem, new SaveCallback() {
//
//                        @Override
//                        public void onSaveSuccess() {
//                            Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
//                            resetstate();
//                            invoicenumber = entryDatabase.getinvoicenumber(getActivity());
//                            b.tinvoiceno.setText(String.valueOf(invoicenumber));
//                            billAdapter.notifyDataSetChanged();
//
//
//                        }
//
//                        @Override
//                        public void onSaveFailure(List<Itemmodel> failedItems) {
//                            Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//
//                    entryDatabase.makeentry(getActivity(), itemlist, tty, "billupdate", myApplication, issueitem, new SaveCallback() {
//
//                        @Override
//                        public void onSaveSuccess() {
//                            Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
//                            resetstate();
//                            invoicenumber = entryDatabase.getinvoicenumber(getActivity());
//                            b.tinvoiceno.setText(String.valueOf(invoicenumber));
//                            billAdapter.notifyDataSetChanged();
//
//
//                        }
//
//                        @Override
//                        public void onSaveFailure(List<Itemmodel> failedItems) {
//                            Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }

            }
        });


        b.scanbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mainActivity.barcodeDecoder
                mainActivity.barcodeDecoder.startScan();

                mainActivity.barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
                    @Override
                    public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                        Log.e("TAG", "BarcodeDecoder==========================:" + barcodeEntity.getResultCode());
                        Log.e("TAG", "BarcodeDecoder==========================:" + barcodeEntity.getBarcodeData());
                        if (barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS) {
//                            editText.setText(barcodeEntity.getBarcodeData());

                            b.tbarcode.setText(barcodeEntity.getBarcodeData());
                            manualadd(barcodeEntity.getBarcodeData());
                            */
/*for (Map.Entry<String, Itemmodel> entry : totalitems.entrySet()) {
                                String key = entry.getKey();
                                Itemmodel value = entry.getValue();
                                if(value.getBarCode().equalsIgnoreCase(barcodeEntity.getBarcodeData())){
                                    Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                                    searchitems.put(String.valueOf(count), item);
                                    totalgwt = totalgwt + item.getGrossWt();
                                    totalnwt = totalnwt + item.getNetWt();
                                    tempDatas.add(value.getTidValue());
                                    b.tbarcode.setText("");
                                    count ++;
                                    billAdapter.notifyDataSetChanged();


                                }
                            }*//*



                            Log.e("TAG", "data==========================:" + barcodeEntity.getBarcodeData());
                        } else {
//                            editText.setText("");
//                        showtoast("Failed to read bar code");
                        }
                    }
                });

            }
        });

        b.addusebarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (b.tbarcode.getText().toString().trim().isEmpty()) {
                    Toast.makeText(myApplication, "Please enter barcode", Toast.LENGTH_SHORT).show();
                }

                manualadd(b.tbarcode.getText().toString().trim());


            }

        });

        b.fineadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchitems.isEmpty()) {
                    Toast.makeText(myApplication, "Please scan/add items", Toast.LENGTH_SHORT).show();

                    return;
                }
                String finper = b.fineper.getText().toString();
                String wasper = b.wastper.getText().toString();

                if (finper.isEmpty()) {
                    Toast.makeText(myApplication, "Please add fine percentage", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (wasper.isEmpty()) {
                    Toast.makeText(myApplication, "Please add waste percentage", Toast.LENGTH_SHORT).show();

                    return;
                }
                double tg = 0;
                double tn = 0;
                double st = 0;
                double tfine = 0;
                double tfix = 0;
                for (Map.Entry<String, Itemmodel> entry : searchitems.entrySet()) {
                    String key = entry.getKey();
                    Itemmodel titem = entry.getValue();
                    if (titem.getItemAddmode().equals("yes")) {
//                        titem.setDiamondSize(String.valueOf(bottomcount));
//                        titem.setBilladdCount(String.valueOf(bottomcount));
                        titem.setFixedWastage(Double.parseDouble(wasper));
                        titem.setMakingPer(Double.parseDouble(finper));
                        tg = tg + titem.getGrossWt();
                        tn = tn + titem.getNetWt();
                        st = st + titem.getStoneAmount();
                        tfix = tfix + titem.getFixedWastage() + titem.getMakingPer();
                        tfine = (titem.getNetWt() * (titem.getFixedWastage() + titem.getMakingPer())) / 100;
                        titem.setItemAddmode("no");
                    }
                    Log.e("checktop4", "allitems " + titem.toString());
                }
                Itemmodel item1 = new Itemmodel();
                item1.setGrossWt(tg);
                item1.setNetWt(tn);
                item1.setStoneAmount(st);
                item1.setFixedWastage(tfix);
                item1.setMakingPer(tfine);
                bottomitem.put(String.valueOf(bottomcount), item1);
                bottomcount++;

                billAdapter = new BillAdapter(getActivity(), searchitems, Billfragment.this, Billfragment.this);
                b.trecycler.setAdapter(billAdapter);
                billAdapterbottom.notifyDataSetChanged();
//                billAdapter.notifyDataSetChanged();


            }
        });


        // Inflate the layout for this fragment
        return b.getRoot();
    }

    private void fetchofflnedata() {
        clist.addAll(entryDatabase.getcustomer(getActivity(), myApplication));
    }

    // Method to show the popup dialog
    private void showBillingPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Billing Details");

        // Create a layout for the dialog
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);

        // Create input fields
        EditText branchInput = new EditText(getActivity());
        branchInput.setHint("Branch");
        layout.addView(branchInput);

        EditText viaInput = new EditText(getActivity());
        viaInput.setHint("Via");
        layout.addView(viaInput);

        // KT Dropdown (Spinner)
        Spinner ktSpinner = new Spinner(getActivity());
        ArrayAdapter<String> ktAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Select KT", "18KT", "22KT", "24KT"});
        ktSpinner.setAdapter(ktAdapter);
        layout.addView(ktSpinner);


        // KT Dropdown (Spinner)
        Spinner tagSpinner = new Spinner(getActivity());
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Seperate tags", "yes", "no"});
        tagSpinner.setAdapter(tagAdapter);
        layout.addView(tagSpinner);


        // Screw Dropdown (Spinner)
        Spinner screwSpinner = new Spinner(getActivity());
        ArrayAdapter<String> screwAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Select Screw Type", "ss", "s", "ssns","Hook","Plain Dendi"});
        screwSpinner.setAdapter(screwAdapter);
        layout.addView(screwSpinner);

        builder.setView(layout);

        // Set "Save & Continue" button
        builder.setPositiveButton("Save & Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get input values
                String branch = branchInput.getText().toString().trim();
                String via = viaInput.getText().toString().trim();
                String kt = ktSpinner.getSelectedItem().toString();
                String screw = screwSpinner.getSelectedItem().toString();
                String tags = tagSpinner.getSelectedItem().toString();


                // Validate Inputs
                if (branch.isEmpty() || kt.isEmpty()) {
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with billing
                proceedWithBilling(branch, via, kt, screw, tags);
            }
        });

        // Set "Cancel" button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to proceed with billing after saving popup inputs
    private void proceedWithBilling(String branch, String via, String kt, String screw, String tags) {
        String tty = b.tcategorytext.getText().toString().trim();
        String inv = "";

        if (tty.equalsIgnoreCase("Estimation")) {
            inv = "E" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Reserved")) {
            inv = "R" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Bill")) {
            inv = "B" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Sample in")) {
            inv = "SI" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("Sample out")) {
            inv = "SO" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("order")) {
            inv = "O" + b.tinvoiceno.getText().toString().trim();
        } else if (tty.equalsIgnoreCase("order estimation")) {
            inv = "OE" + b.tinvoiceno.getText().toString().trim();
        }

        List<Itemmodel> itemlist = new ArrayList<>(searchitems.values());
        for (Itemmodel item : itemlist) {
            item.setDescription("");
            item.setDescription(b.tmelting.getText().toString());
            item.setDiamondSize(userid);
            item.setCustomerName(b.tname.getText().toString().trim());
            if (!operation.equalsIgnoreCase("update")) {
                item.setOperation(b.tcategorytext.getText().toString().trim());
            } else {
                item.setOperation(b.tcategorytext.getText().toString().trim() + "update");
            }
            item.setInvoiceNumber(inv);
            item.setOperationTime(System.currentTimeMillis());
            item.setTransactionType(tty);

            // Save additional inputs
            item.setBranch(branch);
            item.setDiamondCertificate(via);
            item.setStockKeepingUnit(kt);
            item.setDiamondColor(screw);
            item.setDiamondMetal(tags);
        }

        entryDatabase.checkdatabase(getActivity());

        Log.e("billupdate", "check 1 " + itemlist.size() + "  " + itemlist.toString());
        Log.e("billupdate", "check 2 " + issueitem.size() + "  " + issueitem.toString());


        if (!operation.equalsIgnoreCase("update")) {
            entryDatabase.makeentry(getActivity(), itemlist, tty, "bill", myApplication, issueitem, new SaveCallback() {

                @Override
                public void onSaveSuccess() {
                    Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
                    resetstate();
                    invoicenumber = entryDatabase.getinvoicenumber(getActivity());
                    b.tinvoiceno.setText(String.valueOf(invoicenumber));
                    billAdapter.notifyDataSetChanged();


                }

                @Override
                public void onSaveFailure(List<Itemmodel> failedItems) {
                    Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            entryDatabase.makeentry(getActivity(), itemlist, tty, "billupdate", myApplication, issueitem, new SaveCallback() {

                @Override
                public void onSaveSuccess() {
                    Toast.makeText(mainActivity, "Item billed", Toast.LENGTH_SHORT).show();
                    resetstate();
                    invoicenumber = entryDatabase.getinvoicenumber(getActivity());
                    b.tinvoiceno.setText(String.valueOf(invoicenumber));
                    billAdapter.notifyDataSetChanged();


                }

                @Override
                public void onSaveFailure(List<Itemmodel> failedItems) {
                    Toast.makeText(mainActivity, "Failed to bill items", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }



    private void openBottomSheetDialog1(String title) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity()); // or the appropriate context
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_customersheet_layout, null);

        TextView titleTextView = bottomSheetView.findViewById(R.id.title);
        titleTextView.setText(title);

        EditText searchEditText = bottomSheetView.findViewById(R.id.searchEditText);
        TextView addButton = bottomSheetView.findViewById(R.id.addButton);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Save the original list
        originalList = new ArrayList<>(clist); // Copy the original list to a new list for filtering

        // Initialize the adapter with the user list
        adapter = new UserDatumAdapter(originalList, new UserDatumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(jjjcustomermodel.UserDatum userDatum) {
                // Set the selected name to your TextView
                b.tname.setText(userDatum.getFull_name());
                userid = userDatum.getUser_id();
                bottomSheetDialog.dismiss(); // Close the bottom sheet
            }
        });

        recyclerView.setAdapter(adapter);

        // Add TextWatcher for search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().toLowerCase();
                List<jjjcustomermodel.UserDatum> filteredList = new ArrayList<>();

                // Filter the original list based on the search query
                for (jjjcustomermodel.UserDatum userDatum : originalList) {
                    if (userDatum.getFull_name().toLowerCase().contains(query)) {
                        filteredList.add(userDatum);
                    }
                }
                // Update the adapter with the filtered list
                adapter = new UserDatumAdapter(filteredList, new UserDatumAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(jjjcustomermodel.UserDatum userDatum) {
                        // Set the selected name to your TextView
                        b.tname.setText(userDatum.getFull_name());
                        bottomSheetDialog.dismiss(); // Close the bottom sheet
                    }
                });
                recyclerView.setAdapter(adapter); // Re-set the adapter with the filtered list
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        addButton.setOnClickListener(v -> {
            String newName = searchEditText.getText().toString().trim();

            if (!newName.isEmpty()) {
                boolean exists = false;

                // Check if the name already exists
                for (jjjcustomermodel.UserDatum userDatum : originalList) {
                    if (userDatum.getFull_name().equalsIgnoreCase(newName)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    // Add the new name to the list
                    jjjcustomermodel.UserDatum newUserDatum = new jjjcustomermodel.UserDatum();
                    newUserDatum.setFull_name(newName);
                    newUserDatum.setUser_id("0"); // You can assign a temporary ID
                    originalList.add(0, newUserDatum); // Add to the top of the list
                    adapter.notifyItemInserted(0);

                    // Set the new name to the TextView
                    b.tname.setText(newName);
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Name already exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void manualadd(String s) {
        boolean found = false;
        for (Map.Entry<String, Itemmodel> entry : totalitems.entrySet()) {
            String key = entry.getKey();
            Itemmodel value = entry.getValue();
            Log.d("@@","Bar code"+value.getBarCode());
//            if (!value.getBarCode().equalsIgnoreCase(s)) {
//                Toast.makeText(myApplication, "item not exist", Toast.LENGTH_SHORT).show();
//            return;
//            }


            //  for (Itemmodel value : list) {
            if (value.getBarCode().equalsIgnoreCase(s)) {
                Log.d("@@111","Bar code"+value.getBarCode());
                found = true;

                if (tempDatas.contains(value.getTidValue())) {
                    Toast.makeText(myApplication, "Item already added", Toast.LENGTH_SHORT).show();
                    break;
                }

                alllsit = entryDatabase.getBilledItems(getActivity());
                if (alllsit != null && !alllsit.isEmpty()) {
                    for (Itemmodel m1 : alllsit.values()) {
                        if (m1.getItemCode() != null &&
                                m1.getItemCode().equalsIgnoreCase(value.getItemCode())) {
                            value.setItemAddmode(m1.getItemAddmode());
                            break;
                        }
                    }

                    if ("yes".equalsIgnoreCase(value.getItemAddmode())) {
                        Toast.makeText(myApplication, "Item Already Sold", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                // Add item
                Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                item.setItemAddmode("yes");
                searchitems.put(String.valueOf(count), item);
                totalgwt += item.getGrossWt();
                totalnwt += item.getNetWt();

                tempDatas.add(value.getTidValue());
                b.tbarcode.setText("");
                count++;
                billAdapter.notifyDataSetChanged();
                b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                b.tdtotalamount.setText(decimalFormat.format(totalnwt));

                break; // Done processing
            }
        }

        if (!found) {
            Toast.makeText(myApplication, "Please Check Item Is Available or Not In Stock", Toast.LENGTH_SHORT).show();
        }
    }


          */
/*  if (value.getBarCode().equalsIgnoreCase(s)) {
                if (tempDatas.contains(value.getTidValue())) {
                    Toast.makeText(myApplication, "item already added", Toast.LENGTH_SHORT).show();

                    return;
                }
               alllsit = entryDatabase.getBilledItems(getActivity());
                if (alllsit != null && alllsit.size()  != 0) {
                    // Create a map for faster lookup based on item code
                    Map<String, AlllabelResponse.LabelItem> labelItemMap = new HashMap<>();
                    if (alllsit != null && alllsit.size() != 0) {
                        for (Itemmodel m1 : alllsit.values()) {
                            // AlllabelResponse.LabelItem labelItem = labelItemMap.get(m1.getItemCode());
                            if (m1.getItemCode() != null) {
                                if (m1.getItemCode().equalsIgnoreCase(value.getItemCode())) {
                                    value.setItemAddmode(m1.getItemAddmode());
                                    break;
                                }

                            }
                        }
                    }


                    if (value.getItemAddmode() != null) {
                        if (!value.getItemAddmode().equalsIgnoreCase("yes")) {

                            Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                            item.setItemAddmode("yes");
                            searchitems.put(String.valueOf(count), item);
                            totalgwt = totalgwt + item.getGrossWt();
                            totalnwt = totalnwt + item.getNetWt();

                            tempDatas.add(value.getTidValue());
                            b.tbarcode.setText("");
                            count++;
                            billAdapter.notifyDataSetChanged();
                            b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                            b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                            b.tdtotalamount.setText(decimalFormat.format(totalnwt));
                        } else {
                            Toast.makeText(myApplication, "Item Already Sold", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                        item.setItemAddmode("yes");
                        searchitems.put(String.valueOf(count), item);
                        totalgwt = totalgwt + item.getGrossWt();
                        totalnwt = totalnwt + item.getNetWt();

                        tempDatas.add(value.getTidValue());
                        b.tbarcode.setText("");
                        count++;
                        billAdapter.notifyDataSetChanged();
                        b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                        b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                        b.tdtotalamount.setText(decimalFormat.format(totalnwt));
                    }
                } else {
                    Itemmodel item = new Itemmodel(totalitems.get(value.getTidValue()));
                    item.setItemAddmode("yes");
                    searchitems.put(String.valueOf(count), item);
                    totalgwt = totalgwt + item.getGrossWt();
                    totalnwt = totalnwt + item.getNetWt();

                    tempDatas.add(value.getTidValue());
                    b.tbarcode.setText("");
                    count++;
                    billAdapter.notifyDataSetChanged();
                    b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                    b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                    b.tdtotalamount.setText(decimalFormat.format(totalnwt));
                }


            } else {
               Toast.makeText(myApplication, "Please Check Item Is Available or Not In Stock", Toast.LENGTH_SHORT).show();
               break;
            }*//*





    private void resetstate() {
        searchitems.clear();
        b.tinvoiceno.setText("");
        b.tname.setText("");
        b.tcategorytext.setText("Transaction Type");
        tempDatas.clear();
        totalgwt = 0;
        totalnwt = 0;
        b.tdtotalitems.setText("");
        b.tdtotalamount.setText("");
        b.tdtotalgwt.setText("");
        invoicenumber = entryDatabase.getinvoicenumber(getActivity());
        b.tinvoiceno.setText(String.valueOf(invoicenumber));
        issueitem.clear();

    }

    private void performsinglescan() {
//        if(!mainActivity.mReader.setFastID(true)){
//            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (!mainActivity.mReader.setEPCMode()) {
            Toast.makeText(mainActivity, "failed to set mode", Toast.LENGTH_SHORT).show();
            return;
        }

        readTag();

    }

    private void readTag() {
        if (mainActivity.mReader.startInventoryTag()) {
            ploopFlag = true;
            globalcomponents.keepScreenOn(true, mainActivity);
            startUpdatingUI();
            b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_cancelblack));
            b.scanbtntext.setText("Stop");
            new TagThread().start();

        } else {
            if (stopscanner()) {

                b.scanbtnimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_scanblack));
                b.scanbtntext.setText("Scan");

            }
        }
    }

    @Override
    public void onRemoveitem(Itemmodel i, int position, String itemKey) {

//        if (searchitems.containsKey(i.getTidValue())) {
        Toast.makeText(myApplication, "check " + itemKey, Toast.LENGTH_SHORT).show();
        if (operation.equalsIgnoreCase("update")) {

            Issuemode is = new Issuemode(searchitems.get(itemKey));
            issueitem.add(is);
        }
        searchitems.remove(itemKey);
        totalgwt = totalgwt - i.getGrossWt();
        totalnwt = totalnwt - i.getNetWt();
        billAdapter = new BillAdapter(getActivity(), searchitems, this, this);
        b.trecycler.setAdapter(billAdapter);

        b.tdtotalitems.setText(String.valueOf(searchitems.size()));
        b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
        b.tdtotalamount.setText(decimalFormat.format(totalnwt));
//        }
        tempDatas.remove(i.getTidValue());

//        billAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemUpdated(Itemmodel updatedItem, double gwt, double swt, double nwt) {

        totalgwt = totalgwt - gwt + updatedItem.getGrossWt();
        totalnwt = totalnwt - nwt + updatedItem.getNetWt();
        b.tdtotalitems.setText(String.valueOf(searchitems.size()));
        b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
        b.tdtotalamount.setText(decimalFormat.format(totalnwt));
        billAdapter.notifyDataSetChanged();


    }

    @Override
    public void onRemovebottomitem(Itemmodel i, int position, String itemKey, String type) {
        if (type.equalsIgnoreCase("remove")) {
            bottomitem.remove(itemKey);

            List<String> keysToRemove = new ArrayList<>();
            for (Map.Entry<String, Itemmodel> entry : searchitems.entrySet()) {
                String key = entry.getKey();
                Itemmodel item = entry.getValue();
                if (item.getBilladdCount().equals(itemKey)) {
                    keysToRemove.add(key);
                }
            }

            // Remove the collected keys from searchitems
            for (String key : keysToRemove) {
                searchitems.remove(key);
            }

        } else {
//            TreeMap<String, Itemmodel> searchitems1 = new TreeMap<String, Itemmodel>();

            for (Map.Entry<String, Itemmodel> entry : searchitems.entrySet()) {
                String key = entry.getKey();
                Itemmodel item = entry.getValue();
                if (item.getBilladdCount().equals(itemKey)) {
                    item.setItemAddmode("yes");

//                    searchitems1.put(key, item);
                    Log.e("checktop3", "check " + itemKey + " " + item.getBilladdCount());
                }
            }
            billAdapter = new BillAdapter(getActivity(), searchitems, this, this);
            b.trecycler.setAdapter(billAdapter);
            bottomitem.remove(itemKey);

        }

        billAdapterbottom.notifyDataSetChanged();
        billAdapter.notifyDataSetChanged();

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

    private void startUpdatingUI() {
        mHandler.post(updateUIRunnable1);
    }

    // Method to stop updating UI
    private void stopUpdatingUI() {
        mHandler.removeCallbacks(updateUIRunnable1);
    }

    private void addDataToList(String fepc, String tidv, String rssi) {
        Log.d("check fastid ", "  " + fepc + " " + tidv + "  " + rssi);

        if (StringUtils.isNotEmpty(fepc)) {
//            String epcValue = fepc;//fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
//            String tidValue = fepc;//fepc.substring(fepc.length() - 24);

            */
/*String epcValue = fepc.substring(0, 24);
            // Extract TID value (last 24 digits)
            String tidValue = fepc.substring(fepc.length() - 24);*//*


            String trimmedTidValue = fepc;
            if (fepc.startsWith("00")) {
                // Compare ignoring the first two characters
                trimmedTidValue = fepc.substring(2); // Remove "00"
            }


            String epcValue = trimmedTidValue;
            // Extract TID value (last 24 digits)
            String tidValue = trimmedTidValue;

            if (StringUtils.isNotEmpty(tidValue)) {
                int index = checkIsExist(tidValue);
                if (index == -1) {
                    if (totalitems.containsKey(tidValue)) {
                        Itemmodel item = new Itemmodel(totalitems.get(tidValue));
                        item.setItemAddmode("yes");
                        searchitems.put(String.valueOf(count), item);
                        totalgwt = totalgwt + item.getGrossWt();
                        totalnwt = totalnwt + item.getNetWt();
                        count++;
                    }
                    tempDatas.add(tidValue);
                }
            }

        }
    }

    public int checkIsExist(String epc) {
        if (StringUtils.isEmpty(epc)) {
            return -1;
        }
        return binarySearch(tempDatas, epc);
    }

    private final Runnable updateUIRunnable1 = new Runnable() {
        @Override
        public void run() {

            billAdapter.notifyDataSetChanged();

            b.tdtotalitems.setText(String.valueOf(searchitems.size()));
            b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
            b.tdtotalamount.setText(decimalFormat.format(totalnwt));

            if (ploopFlag) {
                mHandler.postDelayed(this, 500); // 50 milliseconds interval for 20 updates per second
            }

        }
    };


    private boolean stopscanner() {
        Log.d("removed", "handle");
        ploopFlag = false;
        stopUpdatingUI();
        globalcomponents.keepScreenOn(false, getActivity());
        if (mainActivity.mReader.isInventorying()) {

            return mainActivity.mReader.stopInventory();
        } else {
            return true;
        }
    }

    @Override
    public void myOnKeyDwon(String process) {
//        super.myOnKeyDwon();
        if (process.equalsIgnoreCase("scan")) {
            b.scanbtn1.performClick();
        } else {
            b.scanbarcode.performClick();
        }

    }

    private int getpvalue(String power) {
        if (power == null || power.isEmpty() || power.matches("0")) {
            return 5;
        } else {
            return Integer.parseInt(power);
        }
    }

    private void openBottomSheetDialog(String s) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.setCancelable(false);

        View contentView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        bottomSheetDialog.setContentView(contentView);

        ImageButton close = contentView.findViewById(R.id.closeButton);
        TextView title = contentView.findViewById(R.id.maintitle);
        Button addbtn = contentView.findViewById(R.id.additem);
        EditText itemname = contentView.findViewById(R.id.itemname);
        ListView spinnerlist = contentView.findViewById(R.id.spinnerlist);
        LinearLayout bholder = contentView.findViewById(R.id.beholder);

        bholder.setVisibility(View.GONE);

        itemname.setVisibility(View.GONE);
        addbtn.setVisibility(View.GONE);

        if (s.matches("tt")) {
            bottomlist.clear();
            //    bottomlist.add("Order");
            bottomlist.add("Order Estimation");
            bottomlist.add("Estimation");
  */
/*          bottomlist.add("Reserved");
            bottomlist.add("Bill");
            bottomlist.add("Sample in");
            bottomlist.add("Sample out");*//*

            title.setText("Transaction Type");
        }
//        else if (s.matches("si")) {
//            title.setText("Sample In");
//            bottomlist.clear();
//            bottomlist.add("Repair");
//            bottomlist.add("Order");
//        }
        else {
            title.setText(b.tcategorytext.getText().toString());
            bottomlist.clear();
            bottomlist.add("Repair");
            bottomlist.add("Order");
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });


//        final List<String>[] allItemNames = new List[]{};//new List[]{categoryDb.getAllItemNames(s.toLowerCase(Locale.ROOT))};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, bottomlist);
        spinnerlist.setAdapter(adapter);


        spinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selecteditem = (String) adapterView.getItemAtPosition(position);
            //    Toast.makeText(requireContext(), "Selected " + s + " : " + selecteditem, Toast.LENGTH_SHORT).show();
//                transactionno.setText("");

                if (s.matches("tt")) {
                    if (!selecteditem.equalsIgnoreCase("Estimation") && !selecteditem.equalsIgnoreCase("reserved") && !selecteditem.equalsIgnoreCase("order") && !selecteditem.equalsIgnoreCase("order estimation")) {
                        Toast.makeText(getActivity(), "not enabled", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selecteditem.equalsIgnoreCase("order")) {
                        billAdapter.updatebilltype("order");
                    } else {
                        billAdapter.updatebilltype("");
                    }

                    b.tcategorytext.setText(selecteditem);
                    transactionno = TransactionIDGenerator.generateBillRepairTransactionID("E");
//                    if (selecteditem.toLowerCase(Locale.ROOT).matches("sample in")
//                            || selecteditem.toLowerCase(Locale.ROOT).matches("sample out")) {
//
//                        tsamplelay.setVisibility(View.VISIBLE);
//
//                    } else if (selecteditem.toLowerCase(Locale.ROOT).matches("bill")) {
//                    tsamplelay.setVisibility(View.GONE);
//                    transactionno.setText(TransactionIDGenerator.generateBillRepairTransactionID("E"));
//                    }
                } else {
//                    tsampletext.setText(selecteditem);
//                    if (selecteditem.toLowerCase(Locale.ROOT).matches("repair")) {
//                        transactionno.setText(TransactionIDGenerator.generateRepairTransactionID());
//                    } else {
//                        transactionno.setText(TransactionIDGenerator.generateOrderTransactionID());
//                    }

                }
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("BillPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("updatedBillList", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Itemmodel>>() {
            }.getType();
            updatedBillList = gson.fromJson(json, type);
            if (!updatedBillList.isEmpty()) {
                operation = "update";
            }

            if (operation.equalsIgnoreCase("update")) {

                String in = updatedBillList.get(0).getInvoiceNumber();
                if (in.startsWith("OE")) {
                    String numericPart = in.substring(2);
                    invoicenumber = Integer.parseInt(numericPart);
                } else if (in.startsWith("E") || in.startsWith("R") || in.startsWith("B") || in.startsWith("O")) {
                    String numericPart = in.substring(1);
                    invoicenumber = Integer.parseInt(numericPart);
                } else {
                    invoicenumber = Integer.parseInt(in);
                }
                Log.e("check invnumber", "  " + invoicenumber);

                b.tinvoiceno.setText(String.valueOf(invoicenumber));
                b.tname.setText(updatedBillList.get(0).getCustomerName());
                //here need to set id and vocher number
                b.tmelting.setText(updatedBillList.get(0).getDescription());
                userid = updatedBillList.get(0).getDiamondShape();


                b.tcategorytext.setText(updatedBillList.get(0).getTransactionType());

                searchitems.clear();
                bottomitem.clear();
                Map<String, Itemmodel> tempMap = new HashMap<>();
                Map<String, Integer> countMap = new HashMap<>();

                for (Itemmodel item : updatedBillList) {
                    if (item.getTransactionType().equalsIgnoreCase("order estimation")) {

                        String fixedWastage = String.valueOf(item.getFixedWastage());
                        String makingPer = String.valueOf(item.getMakingPer());
                        String key = fixedWastage + "_" + makingPer;
                        if (!countMap.containsKey(key)) {
                            countMap.put(key, bottomcount);
                            bottomcount++;
                        }

                        // Get the billaddCount for the current key
                        int billAddCount = countMap.get(key);

                        // Update or create item in tempMap
                        if (!tempMap.containsKey(key)) {
                            Itemmodel newItem = new Itemmodel(item);
                            newItem.setGrossWt(item.getGrossWt());
                            newItem.setNetWt(item.getNetWt());
                            newItem.setStoneAmount(item.getStoneAmount());
                            newItem.setBilladdCount(String.valueOf(billAddCount));
                            newItem.setFixedWastage(item.getFixedWastage());
                            newItem.setMakingPer(item.getMakingPer());
                            newItem.setAvlQty(1);
                            newItem.setPcs(item.getPcs());
                            tempMap.put(key, newItem);
                        } else {
                            // Update the existing item in tempMap
                            Itemmodel existingItem = tempMap.get(key);
                            existingItem.setGrossWt(existingItem.getGrossWt() + item.getGrossWt());
                            existingItem.setNetWt(existingItem.getNetWt() + item.getNetWt());
                            existingItem.setStoneAmount(existingItem.getStoneAmount() + item.getStoneAmount());
                            existingItem.setFixedWastage(existingItem.getFixedWastage() + item.getFixedWastage());
                            existingItem.setMakingPer(existingItem.getMakingPer() + item.getMakingPer());
                        }

                        // Add to searchitems
                        Itemmodel item1 = new Itemmodel(item);
                        item1.setItemAddmode("no");
                        item1.setBilladdCount(String.valueOf(billAddCount));
                        searchitems.put(String.valueOf(count), item1);
                        count++;
                        totalgwt += item1.getGrossWt();
                        totalnwt += item1.getNetWt();


                    } else {
                        Itemmodel item1 = new Itemmodel(item);
                        item1.setItemAddmode("yes");
                        searchitems.put(String.valueOf(count), item1);
                        count++;
                        totalgwt = totalgwt + item1.getGrossWt();
                        totalnwt = totalnwt + item1.getNetWt();
                    }
                }


                for (Map.Entry<String, Itemmodel> entry : tempMap.entrySet()) {
                    String key = entry.getKey();
                    Itemmodel aggregatedItem = entry.getValue();

                    double tnwt = aggregatedItem.getNetWt();
                    double wa = aggregatedItem.getFixedWastage();
                    double m = aggregatedItem.getMakingPer();

                    double tfix = wa + m;
                    double tfine = (tnwt * (tfix)) / 100;
                    aggregatedItem.setFixedWastage(tfix);
                    aggregatedItem.setMakingPer(tfine);

                    bottomitem.put(aggregatedItem.getBilladdCount(), aggregatedItem);
                }


                b.tdtotalitems.setText(String.valueOf(searchitems.size()));
                b.tdtotalgwt.setText(decimalFormat.format(totalgwt));
                b.tdtotalamount.setText(decimalFormat.format(totalnwt));
                if (!bottomitem.isEmpty()) {
                    billAdapterbottom.notifyDataSetChanged();
                }
                billAdapter.notifyDataSetChanged();

            }
            // Optionally, clear the saved list if you only want to use it once
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("updatedBillList");
            editor.apply();
        }
    }


    private void fetchUserDatafromsheet(FragmentActivity activity, String sheeturl) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading data");
        dialog.show();

        String url = "https://docs.google.com/spreadsheets/d/" + sheeturl + "/gviz/tq?tqx=out:json&sheet=Sheet1";
        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,

                response -> {
                    try {
                        clist.clear();
                        HashMap<String, Itemmodel> nmap = new HashMap<>();
                        List<Itemmodel> dmap = new ArrayList<>();
                        Log.d("Sheet Response", ""+response);
                        String jsonString = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                        JSONObject jsonObject = new JSONObject(jsonString);
//                        JSONObject table = jsonObject.getJSONObject("table");
//                        JSONArray rows = table.getJSONArray("rows");
                        JSONObject table = jsonObject.optJSONObject("table");
                        if (table == null) {
                            Log.e("Sheet Error", "Table data is missing");
                            dialog.dismiss();
                            return;
                        }
                        JSONArray rows = table.optJSONArray("rows");
                        if (rows == null || rows.length() == 0) {
                            Log.e("Sheet Error", "Rows are empty or missing");
                            dialog.dismiss();
                            return;
                        }


                        for (int i1 = 0; i1 < rows.length(); i1++) {
                            JSONObject entryObj = rows.getJSONObject(i1);
                            JSONArray rowData = entryObj.getJSONArray("c");
                            jjjcustomermodel.UserDatum data = new jjjcustomermodel.UserDatum();
                            String stonewt = "0";
                            // Extract relevant data
                            String branch = rowData.optJSONObject(12) != null ? rowData.getJSONObject(12).optString("v", "") : "";
                            String category = rowData.optJSONObject(10) != null ? rowData.getJSONObject(10).optString("v", "") : "";

                            if(branch != null && !branch.isEmpty()){
                                data.setFull_name(branch);
                                data.setUser_id(String.valueOf(i1+1));

                                clist.add(data);
                            }

                        }
                        if(clist.isEmpty()){
                            dialog.dismiss();
                        }
                        Log.e("check clist ", " "+clist.get(1).getFull_name());
                        Collections.sort(clist, (o1, o2) -> o1.getFull_name().compareTo(o2.getFull_name()));
                        entryDatabase.makecustomer(getActivity(), myApplication, clist);

                        dialog.dismiss();
                    } catch (JSONException e) {
                        dialog.dismiss();
                      //  Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    dialog.dismiss();
                   // Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("check error", errorMessage);
                    } else {
                        Log.e("check error", "Unknown error occurred.");
                    }
                });

        queue.add(stringRequest);
    }

//    private void fetchUserData() {
//        String url = "https://jjj.panel.jewelmarts.in/callback/UserData";
//        OkHttpClient client = new OkHttpClient();
//
//        // Create form data
//        RequestBody formBody = new FormBody.Builder()
//                .add("username", "RFID")
//                .add("password", "Rg^%6mkj676G%$)jhAZ")
//                .build();
//
//        // Create the request
//        Request request = new Request.Builder()
//                .url(url)
//                .post(formBody)
//                .build();
//
//        // Execute the request
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                // Handle failure
//                getActivity().runOnUiThread(() -> {
//                    Toast.makeText(getActivity(), "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                if (response.isSuccessful()) {
//                    String responseData = response.body().string();
//                    // Handle the response data here
//                    processUserData(responseData);
//                    Collections.sort(clist, (o1, o2) -> o1.getFull_name().compareTo(o2.getFull_name()));
//
//                    entryDatabase.makecustomer(getActivity(), myApplication, clist);
//
//                } else {
//                    // Handle error response
//                    getActivity().runOnUiThread(() -> {
//                        Toast.makeText(getActivity(), "Error code: " + response.code(), Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }
//        });
//    }

//    private void processUserData(String responseData) {
//        try {
//            JSONObject jsonResponse = new JSONObject(responseData);
//            int ack = jsonResponse.getInt("ack");
//            if (ack == 1) {
//
//                JSONArray userDataArray = jsonResponse.getJSONArray("user_data");
//
//                // Iterate through the user_data array
//                for (int i = 0; i < userDataArray.length(); i++) {
//                    JSONObject userObject = userDataArray.getJSONObject(i);
//                    jjjcustomermodel.UserDatum data = new jjjcustomermodel.UserDatum();
//
//                    // Extract user_id and full_name from each user object
//                    String name = userObject.getString("full_name");
//                    String id = userObject.getString("user_id");
//                    Log.e("check userresponse", "  " + name + "   " + id);
//                    data.setFull_name(name);
//                    data.setUser_id(id);
//                    clist.add(data);
//                }
//            } else {
//                Log.e("User Data Error", "Acknowledgment failed");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("User Data Error", "Failed to parse JSON: " + e.getMessage());
//        }
//    }


}*/
