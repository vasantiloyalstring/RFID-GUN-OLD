package com.loyalstring.Excels;


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
import com.loyalstring.R;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.transactionhelper.TransactionIDGenerator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class XlsReader {


    private MyApplication myapp;
    private FragmentActivity act;
    private EntryDatabase entryDatabase;
    private List<Rfidresponse.ItemModel> rfidList;
    Map<String, String> selectedMappings = new HashMap<String, String>();
    Map<String, String> savedMappings = new HashMap<String, String>();
    HashMap<String, Itemmodel> issuerows = new HashMap<String, Itemmodel>();
    Map<String, Itemmodel>  importlist = new HashMap<>();

    List<Issuemode> issueitem = new ArrayList<>();

    Long ot = System.currentTimeMillis();
    String Issueid = "IE" + String.valueOf(ot);
    HashMap<String, Itemmodel> ml = new HashMap<>();
    String rfidtype = "";


    public void processsheet(HashMap<String, Itemmodel> ml, Uri uri, FragmentActivity activity, ProgressDialog progressDialog, MyApplication app, List<Rfidresponse.ItemModel> rfidList, String rfidType) {
        myapp = app;
        act = activity;
        entryDatabase = new EntryDatabase(activity);
        this.rfidList = rfidList;
        this.ml = ml;
        this.rfidtype = rfidType;
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
                activity.runOnUiThread(() -> progressDialog.show());
                InputStream is = activity.getContentResolver().openInputStream(uri);
                List<String> headings = new ArrayList<>();
                HSSFSheet sheet = processXlsFile(is, headings);

                if (headings.isEmpty()) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Failed to read excel", Toast.LENGTH_SHORT).show());
                    return;
                }

//                Log.e("check headings ", "  "+headings);
                activity.runOnUiThread(() -> showSelection(headings, sheet, activity, progressDialog));
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("checkfiletoexceler1", e.getMessage());
            } finally {
                activity.runOnUiThread(() -> progressDialog.dismiss());
            }
        });
    }

    private void showSelection(List<String> excelHeadings, HSSFSheet sheet1, FragmentActivity activity, ProgressDialog progressDialog) {

        String[] heading = {"TID Value", "EPC Value", "Category", "Product",
                "Purity", "RFID Code", "Item Code", "Box", "Gross Weight", "Stone Weight", "Net Weight",
                "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                "Party code", "Updated Date", "Updated By"};

        List<String> myFields = Arrays.asList(heading);
        if (excelHeadings.size() < 1) {
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
            selectedMappings = adapter.getSelectedMappings();


            processexcel(selectedMappings, myFields, sheet1, excelHeadings, alertDialog, activity, progressDialog);
        });
    }

    private void processexcel(Map<String, String> selectedMappings, List<String> myFields, HSSFSheet sheet, List<String> excelHeadings, AlertDialog builder, FragmentActivity activity, ProgressDialog progressDialog) {
//        if (selectedMappings.values().size() == myFields.size()) {

        boolean hasDuplicates = hasDuplicates(selectedMappings.values());

        if (hasDuplicates) {
            // No duplicates found, proceed with your logic here
            Toast.makeText(activity, "mappings are same please change", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Processing");
        progressDialog.show();
        ProgressDialog finalProgressDialog = progressDialog;
        ProgressDialog finalProgressDialog1 = progressDialog;

        AsyncTask<Void, Integer, List<Itemmodel>> task = new AsyncTask<Void, Integer, List<Itemmodel>>() {
            @Override
            protected List<Itemmodel> doInBackground(Void... voids) {
                List<Itemmodel> itemlist = new ArrayList<>();
                List<List<String>> rowDataList = new ArrayList<>();

                try {
                    for (int i = 0; i <= sheet.getLastRowNum(); i++) { // Start from 1 to skip the heading row
                        HSSFRow row = sheet.getRow(i);

                        if (row != null) {
                            List<String> s = getRowData(row);

                            if (s != null && !s.isEmpty()) {
                                int rc = s.size();
                                rowDataList.add(s);

                            } else {
                                Issuemode im = new Issuemode();
                                im.setIssue("issue with row  " + row.getRowNum());
                                im.setIssueId(Issueid);
                                im.setOperationTime(ot);
                                issueitem.add(im);
//                                Log.e("doInBackground", "Skipping row due to issue." + row.getRowNum());
                            }
                            activity.runOnUiThread(() -> finalProgressDialog.setMessage("Processed " + row.getRowNum() + " items"));

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("checkfiletoexceler1", e.getMessage());
                }


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
                    Log.e("checkissueitem", "sise "+importlist.size());


                    List<Itemmodel> dmap = new ArrayList<>();
                    for (String key : ml.keySet()) {
                        // Check if the key exists in umap
                        if (!importlist.containsKey(key)) {
                            // If the key does not exist in umap, add it to dmap
                            dmap.add(ml.get(key));
                        }
                    }
                    List<Itemmodel> nmap = new ArrayList<>(importlist.values());



                    entryDatabase.checkdatabase(activity);
                    entryDatabase.makeentry(activity, nmap, "excel", "product", myapp, issueitem, new SaveCallback() {
                        @Override
                        public void onSaveSuccess() {
                            int t = importlist.size()-dmap.size();
                            Toast.makeText(activity, "saved items successfully"+t, Toast.LENGTH_SHORT).show();
//                                itemlist.clear();
                            issueitem.clear();
                            Toast.makeText(activity, "Deleting items "+dmap.size(), Toast.LENGTH_SHORT).show();
                            deleteitems(entryDatabase, activity, dmap, myapp);
                            finalProgressDialog.dismiss();
                            builder.dismiss();
                            importlist.clear();

                            nmap.clear();
                            dmap.clear();

                        }

                        @Override
                        public void onSaveFailure(List<Itemmodel> failedItems) {

                            deleteitems(entryDatabase, activity, dmap, myapp);
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
    }

    private void deleteitems(EntryDatabase entryDatabase, Context activity, List<Itemmodel> dmap, MyApplication app) {
        Log.e("checkditems","check"+dmap.size());

        entryDatabase.makeentry(activity, dmap, "delete", "product", app, issueitem, new SaveCallback() {

            @Override
            public void onSaveSuccess() {
                Toast.makeText(activity, "Item updated succesfully", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onSaveFailure(List<Itemmodel> failedItems) {
                Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processSelectedMappings(Map<String, String> selectedMappings, List<List<String>> dataMap, List<Itemmodel> itemlist) {
//        Log.e("checking row ", "" + dataMap.get(0).toString());

        Map<String, Itemmodel> itemMap = new HashMap<>();
        Map<String, Itemmodel> itemlist1 = new HashMap<>();

        for (Map.Entry<String, String> entry : selectedMappings.entrySet()) {
            String myField = entry.getKey();
            String selectedHeading = entry.getValue();
//            Log.e("checking heading", "  "+myField+"  "+selectedHeading);
            int columnIndex = findColumnIndex(dataMap.get(0), selectedHeading);

            if (columnIndex != -1) {
                // Iterate over each row in dataMap
                for (int i = 1; i < dataMap.size(); i++) {
//                    Itemmodel item = itemMap.computeIfAbsent(String.valueOf(i), k -> new Itemmodel());
//                    setItemModelField(item, myField, dataMap.get(i).get(columnIndex), itemMap, i);

                    List<String> rowData = dataMap.get(i);
                    if (columnIndex < rowData.size()) {
                        Itemmodel item = itemMap.computeIfAbsent(String.valueOf(i), k -> new Itemmodel());
                        setItemModelField(item, myField, rowData.get(columnIndex), itemMap, i);
                    } else {
                        Log.e("processSelectedMappings", "Column index " + columnIndex + " is out of bounds for row " + i + " with size " + rowData.size());
                    }

                }

            }
        }

        Set<String> seenBarcodes = new HashSet<>();
        Set<String> duplicateBarcodes = new HashSet<>();

        // First pass: Identify duplicate barcodes

        for (Map.Entry<String, Itemmodel> entry : itemMap.entrySet()) {
            String barcode = entry.getValue().getBarCode().trim().toLowerCase();
//            if(barcode.equalsIgnoreCase("mj3832")){
//                Log.e("checking barcodes ", "check "+barcode);
//            }
            if (!seenBarcodes.add(barcode)) {

                duplicateBarcodes.add(barcode);
            }
        }
        Log.e("checking barcodes ", "check "+duplicateBarcodes.size());


        // Second pass: Remove duplicates and add them to issueitem
        Iterator<Map.Entry<String, Itemmodel>> iterator = itemMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Itemmodel> entry = iterator.next();
            String barcode = entry.getValue().getBarCode().trim().toLowerCase();
            if (duplicateBarcodes.contains(barcode)) {
                Issuemode im = new Issuemode(entry.getValue());
                im.setIssue("Barcode entered multiple times :"+entry.getValue().getBarCode() + "  " + "INO  " + entry.getValue().getItemCode());
                im.setIssueId(Issueid);
                im.setOperationTime(ot);
                issueitem.add(im);

//                issueitem.add(new Issuemode(entry.getValue())); // Assuming Issuemode has a constructor that takes Itemmodel
                iterator.remove();
            }
        }




        int a = 0;
        int b= 0;
        for (Map.Entry<String, Itemmodel> entry : itemMap.entrySet()) {

            String ba = entry.getValue().getBarCode();
            Log.d("@@","@@ ba "+ba);

            if (ba != null && !ba.isEmpty()) {
                String tid;
                if(rfidtype.toLowerCase().contains("single")){
                    tid = convertToHex(ba.toUpperCase().trim());
                }else{
                    tid = findTidByBarcode(rfidList, ba.trim());
                }
//                 tid = convertToHex(ba.toUpperCase().trim());//findTidByBarcode(rfidList, ba.trim());//
//                Log.e("checking barcode", "  " + ba + "  " + tid);
                Itemmodel m = entry.getValue();
                m.setTidValue(tid);
                if (entry.getValue().getTidValue() != null && !entry.getValue().getTidValue().isEmpty()) {

                    Itemmodel nitem = null;
//                    Log.d("eitem tidvalue", "" + myapp.getInventoryMap().size());
                    nitem = myapp.checkitem(entry.getValue().getTidValue(), entryDatabase, act);
                    Itemmodel it = entry.getValue();
                    if (nitem == null) {

                        Log.e("checkingnulls1 ", "check1 "+a++);

                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0, it.getTidValue().trim(), it.getTidValue().trim(),
                                "home", it.getCategory().trim(), it.getProduct().trim(), it.getPurity(), it.getDiamondMetal(), it.getDiamondColor(), it.getDiamondClarity(),
                                it.getDiamondSetting(), it.getDiamondShape(), it.getDiamondSize(), it.getDiamondCertificate(), it.getBarCode().toUpperCase().trim(), it.getItemCode(),
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
//                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
//
//                            itemlist1.put(item.getTidValue(), item);
//                        }
                        if ( item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {

                            itemlist1.put(item.getTidValue(), item);
                        }
//                        itemlist.add(item);
                    } else {
//                        Log.d("eitem notfoudnd", " " + nitem);
                        Log.e("checkingnulls2 ", "check2 "+b++);
                        Itemmodel it1 = nitem;
                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), it1.getEntryDate(), 0, 0, it.getTidValue().trim(), it.getTidValue().trim(),
                                it1.getBranch(), it.getCategory().trim(), it.getProduct().trim(), it.getPurity(), it.getDiamondMetal(), it.getDiamondColor(), it.getDiamondClarity(),
                                it.getDiamondSetting(), it.getDiamondShape(), it.getDiamondSize(), it.getDiamondCertificate(), it.getBarCode().toUpperCase().trim(), it.getItemCode(),
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
//                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
//
//                            itemlist1.put(item.getTidValue(), item);
//                        }
                        if ( item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
//
                            Log.e("checkingnulls3 ", "check2 "+b);
                            itemlist1.put(item.getTidValue(), item);
                        }
//                        itemlist.add(item);
                    }

                } else {
                    Issuemode im = new Issuemode(entry.getValue());
                    im.setIssue("issue with tid or barcode " + entry.getValue().getBarCode() + "  " + "INO" + entry.getValue().getItemCode());
                    im.setIssueId(Issueid);
                    im.setOperationTime(ot);
                    issueitem.add(im);

                }
            } else {
                Issuemode im = new Issuemode(entry.getValue());
                im.setIssue("issue with barcode" + "  " + "INO" + entry.getValue().getItemCode());
                im.setIssueId(Issueid);
                im.setOperationTime(ot);
                issueitem.add(im);

            }

        }
        addItemsToDatabase(itemlist1);

    }

    public String convertToHex(String input) {
        StringBuilder hexBuilder = new StringBuilder();
        for (char ch : input.toCharArray()) {
            hexBuilder.append(String.format("%02X", (int) ch)); // Using uppercase hex format
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

    private void addItemsToDatabase(Map<String, Itemmodel> itemlist1) {
        // Add logic to save items to your database or perform other processing
        importlist.putAll(itemlist1);
        Log.d("BatchProcessed", "Batch size: " + itemlist1.size() + "  " + importlist.size() + "  ");
    }

    private List<String> getRowData(HSSFRow row) {
        List<String> rowData = new ArrayList<>();
//        Log.e("checking excel1  ", "  "+row.getLastCellNum()+"   m  "+row.getRowNum());
        for (int cn = 0; cn < row.getLastCellNum(); cn++) {
            HSSFCell cell = row.getCell(cn, HSSFRow.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String cellValue = getCellValueAsString1(cell);
//            Log.e("checking excel  ", "  " + cellValue);
            rowData.add(cellValue);
           /* List<String> cv = getCellValueAsString(cell);
            if (!cv.isEmpty()) {
                Log.e("checking excel  ", "  " + cv.toString());
                rowData.addAll(cv);
            }*/
        }
        return rowData;
    }

    private String getCellValueAsString1(HSSFCell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                // Check if the numeric value is an integer
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Handle date formatted cells if necessary
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // Convert to integer if there's no fractional part
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException ex) {
                        return "";
                    }
                }
            case ERROR:
            case BLANK:
            default:
                return "";
        }
    }


    /*private String getCellValueAsString1(HSSFCell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException ex) {
                        return "";
                    }
                }
            case ERROR:
            case BLANK:
            default:
                return "";
        }
    }*/

    private List<String> getCellValueAsString(HSSFCell cell) {
        List<String> cellData = new ArrayList<>();
        switch (cell.getCellType()) {
            case NUMERIC:
                cellData.add(String.valueOf(cell.getNumericCellValue()));
                break;
            case BOOLEAN:
                cellData.add(String.valueOf(cell.getBooleanCellValue()));
                break;
            case STRING:
                cellData.add(cell.getStringCellValue());
                break;
            case FORMULA:
                try {
                    cellData.add(cell.getStringCellValue());
                } catch (IllegalStateException e) {
                    try {
                        cellData.add(String.valueOf(cell.getNumericCellValue()));
                    } catch (IllegalStateException ex) {
                        cellData.add("");
                    }
                }
                break;
            case ERROR:
                cellData.add("");
                break;
            case BLANK:
                cellData.add("");
                break;
            default:
                cellData.add("");
                break;
        }
        return cellData;
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
            case "Purity":
                if (value == null || value.isEmpty()) {
//                    issue(item, i, "tidvalue");

                } else {
                    item.setPurity(value);
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
                /*counter changes*/

            case "CounterId":
                if (value == null || value.isEmpty()) {
                    item.setCounterId("");
                } else {
                    item.setCounterId(value);
                }

                break;
            case "CounterName":
                if (value == null || value.isEmpty()) {
                    item.setCounterName("");
                } else {
                    item.setCounterName(value);
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


    private HSSFSheet processXlsFile(InputStream is, List<String> headings) throws IOException {
        // Use Apache POI to handle .xls file
        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet sheet = workbook.getSheetAt(0);
        processSheet(sheet, headings);
        return sheet;
    }

    private void processSheet(HSSFSheet sheet, List<String> headings) {
        if (sheet != null) {
            // Process the first row for .xls file
            HSSFRow row = sheet.getRow(0);
            if (row != null) {
                for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                    HSSFCell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    headings.add(cell.toString());
                }
            }
        }
    }


}
