package com.loyalstring.Excels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.Adapters.MyFieldsAdapter;
import com.loyalstring.Excels.reader.Cell;
import com.loyalstring.Excels.reader.CellType;
import com.loyalstring.Excels.reader.ReadableWorkbook;
import com.loyalstring.Excels.reader.Row;
import com.loyalstring.Excels.reader.Sheet;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.transactionhelper.TransactionIDGenerator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Excelopener {

    Map<String, String> selectedMappings = new HashMap<String, String>();
    Map<String, String> savedMappings = new HashMap<String, String>();
    HashMap<String, Itemmodel> issuerows = new HashMap<String, Itemmodel>();
    List<Itemmodel> importlist = new ArrayList<Itemmodel>();
    MyApplication myapp;
    Context act;
    EntryDatabase entryDatabase;
    List<Row> rowList = new ArrayList<>();
    ConcurrentMap<Integer, Row> rowMap = new ConcurrentHashMap<>();
    List<Rfidresponse.ItemModel> rfidList = new ArrayList<>();
    List<Issuemode> issueitem = new ArrayList<>();

    public void processsheet(Uri uri, FragmentActivity activity, ProgressDialog progressDialog, MyApplication app, List<Rfidresponse.ItemModel> rfidList) {
        myapp = app;
        act = activity;
        entryDatabase = new EntryDatabase(activity);
        this.rfidList = rfidList;
        new Handler().postDelayed(() -> processExcelInBackground(uri, activity, progressDialog), 100);
    }

    private void processExcelInBackground(Uri uri, FragmentActivity activity, ProgressDialog pd) {
        final ProgressDialog progressDialog;
        if (pd == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Processing excel");
            progressDialog.show();
        } else {
            progressDialog = pd;
            progressDialog.setMessage("Processing excel");
            progressDialog.show();
        }
        AsyncTask.execute(() -> {
            try {
                activity.runOnUiThread(() -> {

                    progressDialog.show();
                });
                InputStream is = activity.getContentResolver().openInputStream(uri);
                ReadableWorkbook wb = new ReadableWorkbook(is);
                Sheet sheet1 = wb.getFirstSheet();
                List<String> headings = new ArrayList<>();
                // Read the first row to get headings

//                Log.e("checkexc0el", "row count  "+sheet1.openStream().count());

                rowList.clear();
                /*try {
                    Stream<Row> rows = sheet1.openStream();
                    rows.forEach(r -> {
                        headings.addAll(rows.findFirst().map(this::getRowData).orElse(Collections.emptyList()));
//                        rowList.add(r);
                        Log.e("rownum ", "number " + r.getRowNum());
//                        progressDialog.setMessage("Processed " + r.getRowNum()+ " items");
                    });
                    if (headings.isEmpty()) {
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Failed to read excel", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    activity.runOnUiThread(() -> showSelection(headings, rows, activity, progressDialog));
                } catch (Exception e) {
                    Log.e("rownumcatch ", "number " + e.getMessage());
                }*/
                try (Stream<Row> rows = sheet1.openStream()) {
//                    rows.forEach(r -> {
                        headings.addAll(rows.findFirst().map(this::getRowData).orElse(Collections.emptyList()));
//                        rowList.add(r);
//                        Log.e("rownum ", "number " + r.getRowNum());
//                        progressDialog.setMessage("Processed " + r.getRowNum()+ " items");
//                    });

//                    rowList = rows.collect(Collectors.toList());
//                    headings.addAll(rows.findFirst().map(this::getRowData).orElse(Collections.emptyList()));
                }
                Log.e("rownum ", "number  clear ");

                if (headings.isEmpty()) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Failed to read excel", Toast.LENGTH_SHORT).show());
                    return;
                }
                activity.runOnUiThread(() -> showSelection(headings, sheet1, activity, progressDialog));
//                try (Stream<Row> rows = sheet1.openStream()) {
//                    activity.runOnUiThread(() -> showSelection(headings, rows, activity, progressDialog));
////                    rows.forEach(r -> {
////
////                        Log.e("rownum ", "number " + r.getRowNum());
//////                        progressDialog.setMessage("Processed " + r.getRowNum()+ " items");
////                    });
//
////                    rowList = rows.collect(Collectors.toList());
////                    headings.addAll(rows.findFirst().map(this::getRowData).orElse(Collections.emptyList()));
//                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("checkfiletoexceler1", e.getMessage());
            } finally {
                activity.runOnUiThread(() -> progressDialog.dismiss());
            }
        });
    }

    /*private List<String> getRowData(Row row) {

        final List<String> rowData = new ArrayList<>();

        try {
            row.forEach(cell -> {
                if (cell != null && !cell.toString().isEmpty() && cell.getType() != CellType.EMPTY
                        && cell.getType() != CellType.FORMULA) {
                    List<String> cellValue = getCellValueAsString(cell);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        rowData.addAll(cellValue);
                    }
                }
            });
            if (rowData.isEmpty()) {
                return null;
            }
            Log.d("excelimport size", "checkrow " + rowData.size());
        } catch (Exception e) {
            Log.e("getRowData", "Error while processing row: " + e.getMessage());
        }
        return rowData;
    }*/


    private List<String> getRowData(Row row) {
        final List<String> rowData = new ArrayList<>();

        try {
            boolean isValidRow = true;

            for (Cell cell : row) {
                Log.d("excelimport once", "checkrow " + row.getRowNum());
                if ( cell != null && cell.getType() != CellType.FORMULA) {

                    List<String> cellValue = getCellValueAsString(cell);
                    Log.d("excelimport once1", "checkrow " +cellValue);
                    if (cellValue != null) {
                        rowData.addAll(cellValue);
                    }
                }else{
                    rowData.add("");
                }
            }

//            if (!isValidRow || rowData.isEmpty()) {
//                return null;
//            }

            Log.d("excelimport size", "checkrow " + rowData.size());
        } catch (Exception e) {
            Log.e("getRowData", "Error while processing row: " + e.getMessage());
            return null;
        }

        return rowData;
    }

    private List<String> getCellValueAsString(Cell cell) {



        List<String> cellData = new ArrayList<>();
        switch (cell.getType()) {
            case NUMBER:
                cellData.add(cell.asNumber().toString());
                Log.d("check celldata", "celldata1 " + cellData);
                break;
            case BOOLEAN:
                cellData.add(cell.asBoolean().toString());
                Log.d("check celldata", "celldata2 " + cellData);
                break;
            case STRING:
                cellData.add(cell.asString());
                Log.d("check celldata", "celldata3 " + cellData);
                break;
            case FORMULA:
                // You may handle formulas if needed
                Log.d("check celldata", "celldata4 " + cellData);

                break;
            case ERROR:
                // You may handle error values if needed
                Log.d("check celldata", "celldata5 " + cellData);

                break;
            case EMPTY:
                Log.d("check celldata", "celldata6 " + cellData);
                break;
            default:
                Log.d("check celldata", "celldata7 " + cellData);
                Log.d("check celldata", "celldata8 " + cell);
                break;
        }

        return cellData;
    }

    private void showSelection(List<String> excelHeadings, Sheet sheet1, FragmentActivity activity, ProgressDialog progressDialog) {

        String[] heading = {"TID Value", "EPC Value", "Category", "Product",
                "Purity", "RFID Code", "Item Code", "Box", "Gross Weight", "Stone Weight", "Net Weight",
                "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                "Party code", "Updated Date", "Updated By"};



        List<String> myFields = Arrays.asList(heading);
        if (excelHeadings.size()<1) {
            Toast.makeText(activity, "excel you are trying to import missing columns please add columns and import again", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_dynamicexcel1, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Spinner spinnerExcelHeadings = dialogView.findViewById(R.id.spinnerExcelHeadings);
        List<String> mappingNames = ExcelMappingManager.getSavedMappingNames(activity);
        mappingNames.add(0, "Custom");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, mappingNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExcelHeadings.setAdapter(spinnerAdapter);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewFields);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        MyFieldsAdapter adapter = new MyFieldsAdapter(myFields, excelHeadings);
        adapter.updatemapname("custom");
//                    adapter.updateSavedMappings(Collections.emptyMap()); // Clear saved mappings too if needed
//        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        AtomicBoolean isFirstTime = new AtomicBoolean(true);
        Log.d("checkmapings", "  " + myFields.toString() + "  " + excelHeadings.toString());
        boolean scustom = false;
        Button savebtn = dialogView.findViewById(R.id.saveButton);
        EditText emap = dialogView.findViewById(R.id.emap);

        spinnerExcelHeadings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isFirstTime.getAndSet(false)) {
                    return; // Ignore the event the first time
                }

                String selectedItem = (String) parent.getItemAtPosition(position);
                Log.d("selected", "spinner  " + selectedItem);
                if ("Custom".equals(selectedItem)) {
                    emap.setEnabled(true);
                    emap.setText("");
                    emap.setHint("enter map name");
                    savebtn.setText("save");
                    selectedMappings.clear();
                    adapter.updatemapname(selectedItem);
//                    adapter.updateSavedMappings(Collections.emptyMap()); // Clear saved mappings too if needed
                    adapter.notifyDataSetChanged();
                } else {
                    emap.setEnabled(false);
                    emap.setText(selectedItem);
                    savebtn.setText("update");

                    // Handle saved mapping selection
                    String selectedName = parent.getItemAtPosition(position).toString();
                    savedMappings = ExcelMappingManager.getSavedMappings(activity, selectedName);
                    Log.d("checksaved", "" + savedMappings.toString());
                    if (savedMappings != null) {
                        adapter.updatemapname(selectedItem);
                        adapter.updateSavedMappings(savedMappings);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emap.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please enter map name", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> selectedMappings1 = adapter.getSelectedMappings();
                ExcelMappingManager.saveMappings(activity, selectedMappings1, emap.getText().toString().trim());
            }
        });

        Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> {
            try {
                selectedMappings = adapter.getSelectedMappings();


                processexcel(selectedMappings, myFields, sheet1, excelHeadings, alertDialog, activity, progressDialog);

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
    }

    private void processexcel(Map<String, String> selectedMappings, List<String> myFields, Sheet sheet1, List<String> excelHeadings, AlertDialog builder, FragmentActivity activity, ProgressDialog progressDialog) {
//        if (selectedMappings.values().size() == myFields.size()) {

            boolean hasDuplicates = hasDuplicates(selectedMappings.values());

            if (hasDuplicates) {
                // No duplicates found, proceed with your logic here
                Toast.makeText(activity, "mappings are same please change", Toast.LENGTH_SHORT).show();
                return;
            }

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Processing Excel");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        if (activity instanceof MainActivity) {
            Activity activity1 = (MainActivity) activity;
            if (!activity1.isFinishing() && !activity1.isDestroyed()) {
                progressDialog.show(); // SAFE TO SHOW
            } else {
                Log.w("ExcelOpener", "Activity is finishing or destroyed, not showing dialog.");
            }
        } else {
            Log.w("ExcelOpener", "Context is not an Activity, cannot show dialog.");
        }
            ProgressDialog finalProgressDialog = progressDialog;
            ProgressDialog finalProgressDialog1 = progressDialog;

//            finalProgressDialog.show();
            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Integer, List<Itemmodel>> task = new AsyncTask<Void, Integer, List<Itemmodel>>() {
                @Override
                protected List<Itemmodel> doInBackground(Void... voids) {
                    List<Itemmodel> itemlist = new ArrayList<>();
                    List<List<String>> rowDataList = new ArrayList<>();
                    /*sheet1.forEach(r -> {
                        finalProgressDialog.setMessage("Processed " + r.getRowNum()+ " items");
                        List<String> s = getRowData(r);

                        if (s != null && !s.isEmpty()) {
                            int rc = s.size();
//                            Log.d("importexport", "c2  " + s.size()+"  "+excelHeadings.size());
                            if (rc < excelHeadings.size()) {
                                // Add empty strings for missing columns
                                int missingColumns = excelHeadings.size() - rc;
                                for (int i = 0; i < missingColumns; i++) {
                                    s.add(""); // Add empty string for each missing column
                                }
                            }
                            rowDataList.add(s);
                        } else {
                            Log.e("doInBackground", "Skipping row due to issue." + r.getRowNum());
                        }


                    });*/
                    try (Stream<Row> rows = sheet1.openStream()) {
                        // Process each row
                        rows.forEach(r -> {
                            List<String> s = getRowData(r);

                            finalProgressDialog.setMessage("Processed " + r.getRowNum()+ " items");
                            if (s != null && !s.isEmpty()) {
                                int rc = s.size();
//                            Log.d("importexport", "c2  " + s.size()+"  "+excelHeadings.size());
//                                if (rc < excelHeadings.size()) {
//                                    // Add empty strings for missing columns
//                                    int missingColumns = excelHeadings.size() - rc;
//                                    for (int i = 0; i < missingColumns; i++) {
//                                        s.add(""); // Add empty string for each missing column
//                                    }
//                                }
                                rowDataList.add(s);
                            } else {
                                Log.e("doInBackground", "Skipping row due to issue." + r.getRowNum());
                            }


                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("checkfiletoexceler1", e.getMessage());
                    }
                    Log.d("importexport size", "c1  "  + rowDataList.size()+"\n" + rowDataList.toString());

                    // Process the selected mappings
                    processSelectedMappings(selectedMappings, rowDataList, itemlist);

                    return itemlist;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    finalProgressDialog.setProgress(values[0]);
                }

                @Override
                protected void onPostExecute(List<Itemmodel> itemlist) {
//                    finalProgressDialog.dismiss();
                    if (!importlist.isEmpty()) {
//                        Log.d("this is inset", " ");
                        EntryDatabase entryDatabase = new EntryDatabase(activity);

                        entryDatabase.checkdatabase(activity);
                        entryDatabase.makeentry(activity, importlist, "excel", "product", myapp, issueitem, new SaveCallback() {
                            @Override
                            public void onSaveSuccess() {
                                Toast.makeText(activity, "saved items successfully", Toast.LENGTH_SHORT).show();
//                                itemlist.clear();
                                finalProgressDialog.dismiss();
                                builder.dismiss();
                                importlist.clear();

                            }

                            @Override
                            public void onSaveFailure(List<Itemmodel> failedItems) {

                                Toast.makeText(activity, "Failed to add items", Toast.LENGTH_SHORT).show();
                            }
                        });
//                        instance.saveitem(getActivity(), importlist, "excel", "p", null, new SaveCallback() {
//                            @Override
//                            public void onSaveSuccess() {
//                                // Handle success
//                                builder.create().dismiss();
//                            }
//
//                            @Override
//                            public void onSaveFailure() {
//                                // Handle failure
//                            }
//                        });
                    }

                }
            };

            task.execute();
//            builder.create().dismiss();
//        } else {
//            Toast.makeText(activity, "Please choose all headings", Toast.LENGTH_SHORT).show();
//        }
    }


    private boolean hasDuplicates(Collection<String> values) {
        Set<String> set = new HashSet<>();
        for (String value : values) {
//            Log.d("checkingmaps", "  " + value);
            if (!set.add(value)) {
                return true; // Duplicate found
            }
        }
        return false; // No duplicates found
    }

    private void processSelectedMappings(Map<String, String> selectedMappings, List<List<String>> dataMap, List<Itemmodel> itemlist) {

        Map<String, Itemmodel> itemMap = new HashMap<>();
        for (Map.Entry<String, String> entry : selectedMappings.entrySet()) {
            String myField = entry.getKey();
            String selectedHeading = entry.getValue();
//            Log.e("checking heading", "  "+myField+"  "+selectedHeading);
            int columnIndex = findColumnIndex(dataMap.get(0), selectedHeading);
            try {

                if (columnIndex != -1) {
                    // Iterate over each row in dataMap
                    for (int i = 1; i < dataMap.size(); i++) {
                        Itemmodel item = itemMap.computeIfAbsent(String.valueOf(i), k -> new Itemmodel());
                        try {
                            setItemModelField(item, myField, dataMap.get(i).get(columnIndex), itemMap, i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    Log.d("check import for", "  " + itemMap.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String bar = "";
        for (Map.Entry<String, Itemmodel> entry : itemMap.entrySet()) {


            try {

                bar = entry.getValue().getBarCode().trim();
            } catch (Exception e) {
                e.printStackTrace();


            }

            if (bar != null && !bar.isEmpty()) {
                String tid = findTidByBarcode(rfidList, bar);
                Log.e("checking barcode", "  "+bar+"  "+tid);
                Itemmodel m = entry.getValue();
                m.setTidValue(tid);


                if (entry.getValue().getTidValue() != null && !entry.getValue().getTidValue().isEmpty()) {

                    Itemmodel nitem = null;
                    Log.d("eitem tidvalue", "" + myapp.getInventoryMap().size());
                    nitem = myapp.checkitem(entry.getValue().getTidValue(), entryDatabase, act);
                    Itemmodel it = entry.getValue();
                    if (nitem == null) {

                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, it.getTidValue(), it.getTidValue(),
                                "home", it.getCategory(), it.getProduct(), it.getPurity(), it.getDiamondMetal(), it.getDiamondColor(), it.getDiamondClarity(),
                                it.getDiamondSetting(), it.getDiamondShape(), it.getDiamondSize(), it.getDiamondCertificate(), it.getBarCode().toUpperCase(), it.getItemCode(),
                                it.getBox(), it.getHuidCode(), it.getPartyCode(), it.getDescription(), "Active", TransactionIDGenerator.generateTransactionNumber("E"),
                                "exceladd", it.getTransactionType(), it.getInvoiceNumber(), it.getCustomerName(), it.getItemAddmode(), it.getPaymentMode(),
                                it.getPaymentDescription(), it.getGstApplied(), it.getDiamondWt(), it.getDiamondPcs(), it.getDiamondRate(), it.getDiamondAmount(), it.getGrossWt(), it.getStoneWt(),
                                it.getNetWt(), it.getMakingGm(), it.getMakingPer(), it.getFixedAmount(), it.getFixedWastage(), it.getStoneAmount(), it.getMrp(),
                                it.getHallmarkCharges(), 1, 0, 0, 0, 0, 0,
                                0, 0, it.getGoldRate(), it.getTotalMaking(), it.getItemPrice(), it.getAppliedDiscount(),
                                it.getItempriceAfterdiscount(), it.getGstRate(), it.getPayableAmount(), it.getPayableAmountincgst(), it.getItemGst(), it.getTotalBilleditems(),
                                it.getTotalBilledgwt(), it.getTotalBilledamount(), it.getTotalBillAmountExcGst(), it.getTotalBillAmountincgst(), it.getTotalGst(),
                                it.getTotalDiscount(), it.getPaidAmount(), it.getBalance(), it.getGunUpdate(), it.getWebUpdate(),it.getProductCode(),it.getCounterId(),it.getCounterName(),it.getTotPcs(),it.getTotMPcs(),it.getCategoryId(),it.getProductId(),it.getDesignId(),it.getPurityId());

                        item.setImageUrl(it.getHuidCode());
                        itemlist.add(item);
                    } else {
                        Log.d("eitem notfoudnd", " " + nitem);
                        Itemmodel it1 = nitem;
                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), it1.getEntryDate(), 0, 0, it.getTidValue(), it.getTidValue(),
                                it1.getBranch(), it.getCategory(), it.getProduct(), it.getPurity(), it.getDiamondMetal(), it.getDiamondColor(), it.getDiamondClarity(),
                                it.getDiamondSetting(), it.getDiamondShape(), it.getDiamondSize(), it.getDiamondCertificate(), it.getBarCode().toUpperCase(), it.getItemCode(),
                                it.getBox(), it.getHuidCode(), it.getPartyCode(), it.getDescription(), it1.getStatus(), it1.getTagTransaction(), "excelupdate",
                                it.getTransactionType(), it.getInvoiceNumber(), it.getCustomerName(), it.getItemAddmode(), it.getPaymentMode(), it.getPaymentDescription(),
                                it.getGstApplied(), it.getDiamondWt(), it.getDiamondPcs(), it.getDiamondRate(), it.getDiamondAmount(), it.getGrossWt(), it.getStoneWt(),
                                it.getNetWt(), it.getMakingGm(), it.getMakingPer(), it.getFixedAmount(), it.getFixedWastage(), it.getStoneAmount(), it.getMrp(),
                                it.getHallmarkCharges(), 1, 0, it.getTotalGwt(), 0, it.getTotalStonewt(), 0,
                                it.getTotalNwt(), 0, it.getGoldRate(), it.getTotalMaking(), it.getItemPrice(), it.getAppliedDiscount(),
                                it.getItempriceAfterdiscount(), it.getGstRate(), it.getPayableAmount(), it.getPayableAmountincgst(), it.getItemGst(), it.getTotalBilleditems(),
                                it.getTotalBilledgwt(), it.getTotalBilledamount(), it.getTotalBillAmountExcGst(), it.getTotalBillAmountincgst(), it.getTotalGst(),
                                it.getTotalDiscount(), it.getPaidAmount(), it.getBalance(), "updateitem", "",it.getProductCode(),it.getCounterId(),it.getCounterName(),it.getTotPcs(),it.getTotMPcs(),it.getCategoryId(),it.getProductId(),it.getDesignId(),it.getPurityId());
                        item.setImageUrl(it.getHuidCode());
                        itemlist.add(item);
                    }

                }
            } else {

                Itemmodel nitem = null;
                Log.d("eitem tidvalue", "" + myapp.getInventoryMap().size());
                nitem = myapp.checkitem(entry.getValue().getTidValue(), entryDatabase, act);
                Itemmodel it = entry.getValue();
                if (nitem == null) {

                    Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, it.getTidValue(), it.getTidValue(),
                            "home", it.getCategory(), it.getProduct(), it.getPurity(), it.getDiamondMetal(), it.getDiamondColor(), it.getDiamondClarity(),
                            it.getDiamondSetting(), it.getDiamondShape(), it.getDiamondSize(), it.getDiamondCertificate(), convertToHex(it.getItemCode()), it.getItemCode(),
                            it.getBox(), it.getHuidCode(), it.getPartyCode(), it.getDescription(), "Active", TransactionIDGenerator.generateTransactionNumber("E"),
                            "exceladd", it.getTransactionType(), it.getInvoiceNumber(), it.getCustomerName(), it.getItemAddmode(), it.getPaymentMode(),
                            it.getPaymentDescription(), it.getGstApplied(), it.getDiamondWt(), it.getDiamondPcs(), it.getDiamondRate(), it.getDiamondAmount(), it.getGrossWt(), it.getStoneWt(),
                            it.getNetWt(), it.getMakingGm(), it.getMakingPer(), it.getFixedAmount(), it.getFixedWastage(), it.getStoneAmount(), it.getMrp(),
                            it.getHallmarkCharges(), 1, 0, 0, 0, 0, 0,
                            0, 0, it.getGoldRate(), it.getTotalMaking(), it.getItemPrice(), it.getAppliedDiscount(),
                            it.getItempriceAfterdiscount(), it.getGstRate(), it.getPayableAmount(), it.getPayableAmountincgst(), it.getItemGst(), it.getTotalBilleditems(),
                            it.getTotalBilledgwt(), it.getTotalBilledamount(), it.getTotalBillAmountExcGst(), it.getTotalBillAmountincgst(), it.getTotalGst(),
                            it.getTotalDiscount(), it.getPaidAmount(), it.getBalance(), it.getGunUpdate(), it.getWebUpdate(), it.getProductCode(), it.getCounterId(), it.getCounterName(), it.getTotPcs(), it.getTotMPcs(), it.getCategoryId(), it.getProductId(), it.getDesignId(), it.getPurityId());

                    item.setImageUrl(it.getHuidCode());
                    item.setBarCode(it.getItemCode());
                    String hexvalue = convertToHex(item.getItemCode());
                    Log.d("", "hexvalue" +hexvalue);
                    item.setTidValue(hexvalue);
                    itemlist.add(item);

                    Log.d("", "item code" + item.getItemCode() + "tid value " + item.getTidValue() + "epc value " + item.getEpcValue());

                }
            }

        }
        addItemsToDatabase(itemlist);

    }

    public String convertToHex(String input) {
       /* StringBuilder hexBuilder = new StringBuilder();
        for (char ch : input.toCharArray()) {
            hexBuilder.append(String.format("%02X", (int) ch)); // Using uppercase hex format
        }
        return hexBuilder.toString();*/
        StringBuilder hexBuilder = new StringBuilder();

        // Step 1: Convert each character to 2-digit hex
        for (char ch : input.toCharArray()) {
            String hex = String.format("%02X", (int) ch); // e.g., 'A' -> "41"
            hexBuilder.append(hex);
        }

        // Step 2: Add "00" at the beginning until total length is a multiple of 4
        while (hexBuilder.length() % 4 != 0) {
            hexBuilder.insert(0, "00"); // ✅ Adds at the start
        }

        return hexBuilder.toString();
    }

    private String findTidByBarcode(List<Rfidresponse.ItemModel> rfidList, String barcode) {

        for (Rfidresponse.ItemModel item : rfidList) {

            if (item.getBarcodeNumber().equalsIgnoreCase(barcode)) {
                return item.getTid();
            }
        }
        return null;
    }

    private int findColumnIndex(List<String> rowData, String selectedHeading) {
        // Find the index of the selected heading in the row data
        for (int i = 0; i < rowData.size(); i++) {
            if (rowData.get(i).equals(selectedHeading)) {
                return i;
            }
        }
        return -1; // Heading not found
    }

    private void addItemsToDatabase(List<Itemmodel> items) {
        // Add logic to save items to your database or perform other processing
        importlist.addAll(items);
        Log.d("BatchProcessed", "Batch size: " + items.size() + "  " + importlist.size() + "  " + items.toString());
    }

    private void setItemModelField(Itemmodel item, String field, String value, Map<String, Itemmodel> itemMap, int i) {
//        Log.d("chekck manul ","  "+field+ "  "+value);
        switch (field) {
            case "TID Value":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setTidValue(value);
                }
                break;
            case "EPC Value":
//                if (value == null || value.isEmpty()) {
//                    issue(item, i, "tidvalue");
//
//                } else {
//                    item.setEpcValue(value);
//                }
                break;
            case "Category":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setCategory(value);
                }
                break;
            case "Product":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setProduct(value);
                }
                break;
            /*for counter change*/
            case "CounterId":
                if (value == null || value.isEmpty()) {
                   issue(item, i, "tidvalue");

                } else {
                    item.setCounterId(value);
                }
                break;

            case "CategoryId":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setCategoryId(Integer.parseInt(value));
                }
                break;


            case "ProductId":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setProductId(Integer.parseInt(value));
                }
                break;

            case "PurityId":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setPurityId(Integer.parseInt(value));
                }
                break;

            case "DesignId":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setDesignId(Integer.parseInt(value));
                }
                break;



            case "CounterName":
                if (value == null || value.isEmpty()) {
                   issue(item, i, "tidvalue");

                } else {
                    item.setCounterName(value);
                }
                break;
            case "RFID Code":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setBarCode(value);
                }
                break;
            case "Item Code":
                if (value == null || value.isEmpty()) {
                    issue(item, i, "tidvalue");

                } else {
                    item.setItemCode(value);
                }
                break;
            case "Box":
                if (value == null || value.isEmpty()) {
                    item.setBox("");
                } else {
                    item.setBox(value);
                }

                break;

            case "Gross Weight":
                try {
                    double grossWt = 0;
                    if (!value.isEmpty()) {
                        grossWt = Double.parseDouble(value);
                    }
                    item.setGrossWt(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Gross Weight", "Invalid numeric value: " + value + "  " + 1);
                    issue(item, i, "tidvalue");
                }
                break;
            case "Stone Weight":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setStoneWt(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Stone Weight", "Invalid numeric value: " + value + "  " + 2);
                    issue(item, i, "tidvalue");
                    // Remove the item from the list
                }
                break;
            case "Net Weight":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setNetWt(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Net Weight", "Invalid numeric value: " + value + "  " + 3);
                    issue(item, i, "tidvalue");

                }
                break;
            case "Making gm":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setMakingGm(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Making gm", "Invalid numeric value: " + value + "  " + 4);
                    issue(item, i, "tidvalue");

                }
                break;
            case "Making %":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setMakingPer(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Making per", "Invalid numeric value: " + value + "  " + 5);
                    issue(item, i, "tidvalue");

                }
                break;
            case "Fixed amount":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setFixedAmount(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Fixed Amount", "Invalid numeric value: " + value + "  " + 6);
                    issue(item, i, "tidvalue");

                }
                break;
            case "Fixed Wastage":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setFixedWastage(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Fixed wastage", "Invalid numeric value: " + value + "  " + 7);
                    issue(item, i, "tidvalue");

                }
                break;
            case "Stone amount":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setStoneAmount(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Stone amount", "Invalid numeric value: " + value + "  " + 8);
                    issue(item, i, "tidvalue");
                }
                break;
            case "Mrp":
                try {
                    double grossWt = Double.parseDouble(value);
                    item.setMrp(grossWt);
                } catch (NumberFormatException e) {
                    // Handle the case where 'value' is not a valid numeric string
                    // Remove the item from the list and log the information
                    Log.e("Mrp", "Invalid numeric value: " + value + "  " + 9 + "  " + field);
                    issue(item, i, "tidvalue");
                }
                break;
            case "Huid code":
                if (value == null || value.isEmpty()) {
                    item.setHuidCode("");
                } else {
                    item.setHuidCode(value);
                }
                break;
            case "Party code":
                if (value == null || value.isEmpty()) {
                    item.setPartyCode("");
                } else {
                    item.setPartyCode(value);
                }
                break;
            case "Updated Date":
//                item.setupda(value);
                break;
            case "Updated By":
//                item.setUpdatedBy(value);
                break;
        }
    }

    private void issue(Itemmodel item, int i, String tidvalue) {
        issuerows.put(String.valueOf(i), item);
    }


}
