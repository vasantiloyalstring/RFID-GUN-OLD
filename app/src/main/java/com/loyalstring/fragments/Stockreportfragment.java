package com.loyalstring.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.loyalstring.Adapters.Stockverfyreportadapter;
import com.loyalstring.Excels.InventoryExcelCreation;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.databinding.FragmentStockreportfragmentBinding;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.svmodel;
import com.loyalstring.readersupport.KeyDwonFragment;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class Stockreportfragment extends KeyDwonFragment implements Stockverfyreportadapter.Onclick, Stockverfyreportadapter.scrolllisten, Stockverfyreportadapter.Onpause {

    FragmentStockreportfragmentBinding b;
    boolean bfromdate = false;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    Stockverfyreportadapter stockverfyreportadapter;
    LinkedHashMap<String, svmodel> stockreportlist = new LinkedHashMap<String, svmodel>();
    Map<String, List<Itemmodel>> stockreportdlist = new HashMap<>();
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;
    Globalcomponents globalcomponents;

    public static boolean userScrolling = false;
    public static boolean childscroll = false;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = FragmentStockreportfragmentBinding.inflate(inflater, container, false);



        // Format the timestamp as a string
        mainActivity = (MainActivity) getActivity();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Current Scan Stock Report");
            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }

        globalcomponents = new Globalcomponents();
        DateTimeFormatter formatter;
        String timestamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            timestamp = now.format(formatter);
        }
        b.fromtext.setText(timestamp);
        b.totext.setText(timestamp);
        b.srrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        stockverfyreportadapter = new Stockverfyreportadapter(stockreportlist, getActivity(), this, Stockreportfragment.this, this);
        b.srrecycler.setAdapter(stockverfyreportadapter);

        try {
            createtodaylist(b.fromtext.getText().toString(), b.totext.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        b.fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bfromdate = true;
                showDatePickerDialog();

            }
        });
        b.todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bfromdate = false;
                showDatePickerDialog();
//                createtodaylist(fromdate.getText().toString(), todate.getText().toString());
            }
        });

//        b.mainscroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                stockverfyreportadapter.setScrolling(true);
//                stockverfyreportadapter.updatescrolling(i, i1);
//                stockverfyreportadapter.setScrolling(false);
//            }
//        });


        // Inflate the layout for this fragment
        return b.getRoot();
    }

    private void showDatePickerDialog() {
        // Get the current date as the default selection
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the selected date
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = dayOfMonth;

                        // Display the selected date in your desired format
                        String formattedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                        if (bfromdate) {
                            try {
                                createtodaylist(formattedDate, b.totext.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            b.fromtext.setText(formattedDate);
                        } else {
                            b.totext.setText(formattedDate);
                            try {
                                createtodaylist(b.fromtext.getText().toString(), formattedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                year, month, day);

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private String formatDate(int year, int month, int day) {
        // Format the date as desired (e.g., "MM/dd/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return dateFormat.format(calendar.getTime());
    }

    private void createtodaylist(String fromdate, String todate) throws ParseException {
//        alltagscanlist.clear();
//        alltagslist.clear();
//        finallist.clear();
//        finallist1.clear();
//        alldates.clear();
//        alltagscanlist1.clear();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date sdate = dateFormat.parse(fromdate);
            Date edate = dateFormat.parse(todate);
//            Date edate = dateFormat.parse(todate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(edate);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            edate = calendar.getTime();

            Log.d("stockreport", "dates " + sdate + "  " + edate + fromdate + "  " + todate);
            getreports("stockreport", getActivity(), sdate, edate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getreports(String stockreport, FragmentActivity activity, Date startDate, Date endDate) {
        interfaces.RetrieveDataListener1 listener1 = new interfaces.RetrieveDataListener1() {
            @Override
            public void onRetrieveData(Map<String, List<Itemmodel>> resultList) {
                Log.d("rfrag", "  " + resultList.toString() + "   " + resultList.size());
                Log.d("rfragsize", "size  " + resultList.size());

                stockreportdlist.clear();
                stockreportdlist.putAll(resultList);
                stockreportlist.clear();
                Set<String> timeSet = resultList.keySet();
                List<String> timeList = new ArrayList<>(timeSet);
                Collections.sort(timeList);


                LinkedHashMap<String, List<Itemmodel>> sortedmap = new LinkedHashMap<>();
                for (String s : timeList) {
                    List<Itemmodel> i = resultList.get(s);
                    sortedmap.put(s, i);

                    double grosswt = 0;
                    int totalqty = 0;
                    double totalwt = 0;
                    int matchedqty = 0;
                    double matchedwt = 0;
                    int unmatchedqty = 0;
                    double unmatchedwt = 0;
                    String status;
                    for (Itemmodel m : i) {

                        grosswt += m.getGrossWt();
                        totalqty += m.getAvlQty();

                        if (m.getOperation().equals("found")) {
                            totalwt += m.getGrossWt();
                            matchedwt += m.getMatchGwt();
                            matchedqty += m.getMatchQty();
                        } else {
                            unmatchedqty++;
                            unmatchedwt += m.getGrossWt();
                        }
                        Log.d("reports", "check  1  " + totalqty + "  " + m.getOperation() + "   " + m.getOperationTime());
                    }
                    svmodel sv = new svmodel(s, grosswt, totalqty, totalwt, matchedqty, matchedwt, unmatchedqty, unmatchedwt, 0, 0, "");
                    stockreportlist.put(s, sv);
                }


                stockverfyreportadapter.notifyDataSetChanged();

            }
        };
        EntryDatabase db = new EntryDatabase(activity);
        db.stockreport(activity, listener1, startDate, endDate);
    }

    @Override
    public void onclick(int position, String date, svmodel item) {
        Toast.makeText(getActivity(), "checking reports", Toast.LENGTH_SHORT).show();
        if (stockreportdlist.containsKey(date)) {
            List<Itemmodel> m = stockreportdlist.get(date);

            if (areStoragePermissionsGranted()) {
                boolean folder = globalcomponents.checkfileexist("stockreports");
                if (folder) {
                    File cfile = createfile();
                    if (cfile != null) {
                        String filePath = cfile.getAbsolutePath();
                        ArrayList<Itemmodel> ml = new ArrayList<>(m);
                        HashMap<String, ArrayList<Itemmodel>> excelmap = new HashMap<>();
                        excelmap.put(filePath, ml);

                        InventoryExcelCreation excelTask = new InventoryExcelCreation(null, null, filePath, "", getActivity(), "stockreport", "excel", excelmap, null);
                        excelTask.execute();

                    } else {
                        Toast.makeText(getActivity(), "failed to create file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ArrayList<String> folders = new ArrayList<>();
                    folders.add("stockreports");
                    boolean f = globalcomponents.createFolders(folders);
                    if (!f) {
                        Toast.makeText(getActivity(), "failed to create file", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "created file please click again", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(getActivity(), "request permission", Toast.LENGTH_SHORT).show();
                requestStoragePermissions();
            }

//            stockverfylistadapter = new Stockverfylistadapter(m, getActivity());
//            listrecycler.setAdapter(stockverfylistadapter);
        }
    }

    private File createfile() {
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 and above
            try {
                file = File.createTempFile("stockreport", ".xlsx", getActivity().getFilesDir());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in internal storage: " + e.getMessage());
                return null;
            }
        } else { // Android versions below 10
            try {
                file = File.createTempFile("stockrepor", ".xlsx", Environment.getExternalStorageDirectory());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in external storage: " + e.getMessage());
                return null;
            }
        }
    }

    private boolean areStoragePermissionsGranted() {
        int readPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY) {
//        if (!stockverfyreportadapter.isScrolling) {
            // Update the scrolling of b.mainscroll
            b.mainscroll.scrollTo(scrollX, scrollY);
//        }
    }

    @Override
    public void onpause(int position, String date, svmodel item) {
        Toast.makeText(getActivity(), "checking scanning", Toast.LENGTH_SHORT).show();
        if (stockreportdlist.containsKey(date)) {
            List<Itemmodel> m = stockreportdlist.get(date);

            Inventoryfragment h = new Inventoryfragment();
            Bundle args = new Bundle();
            args.putSerializable("searchlist", (Serializable) m);  // Cast the list to Serializable
            h.setArguments(args);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainfragment, h);
            transaction.addToBackStack(null); // Add the transaction to the back stack
            transaction.commit();
//            mListener.onFragmentChanged(h.getId());



        }

    }
}