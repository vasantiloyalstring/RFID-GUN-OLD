package com.loyalstring.Excels;

import static com.loyalstring.MainActivity.decimalFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.loyalstring.Adapters.InventoryBottomAdaptor;
import com.loyalstring.R;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.network.NetworkUtils;


import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryExcelCreation extends AsyncTask<Void, Integer, String> {
    private final List<Itemmodel> toplist;
    private final List<Itemmodel> bottomlist;
    private final String topfilepath;
    private final String bottomfilepath;
    private final FragmentActivity context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private String frag;
    private String type;
    private List<Itemmodel> itemslist = new ArrayList<>();

    private HashMap<String, ArrayList<Itemmodel>> excelmap = new HashMap<>();
    String outfile = "";
    Map<String, String> fileMap = new HashMap<>();
    NetworkUtils networkUtils;
    InventoryBottomAdaptor inventoryBottomAdaptor;

    public InventoryExcelCreation(List<Itemmodel> toplist, List<Itemmodel> bottomlist, String topfile, String bottomfile, FragmentActivity context, String frag, String type, HashMap<String, ArrayList<Itemmodel>> excelmap, InventoryBottomAdaptor inventoryBottomAdaptor) {
        this.toplist = toplist;
        this.bottomlist = bottomlist;
        this.topfilepath = topfile;
        this.bottomfilepath = bottomfile;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.frag = frag;
        this.type = type;
        this.excelmap = excelmap;
        networkUtils = new NetworkUtils(context);
        this.inventoryBottomAdaptor = inventoryBottomAdaptor;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Creating Excel file...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        if (frag.equalsIgnoreCase("inventory")) {
            progressDialog.setMax(bottomlist.size());
        } else {
            progressDialog.setMax(excelmap.size());
        }
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        fileMap.clear();
        if (frag.equalsIgnoreCase("inventory")) {

            for (Map.Entry<String, ArrayList<Itemmodel>> entry : excelmap.entrySet()) {
                try {
                    outfile = entry.getKey();

                    ArrayList<Itemmodel> processlist = new ArrayList<>();
                    processlist = entry.getValue();
                    String process = "";
                    if (outfile.contains("matcheditems")) {
                        process = "matcheditems";
                    }
                    if (outfile.contains("unmatcheditems")) {
                        process = "unmatcheditems";
                    }
                    if (outfile.contains("matcheditemdetails")) {
                        process = "matcheditemdetails";
                    }
                    if (outfile.contains("unmatcheditemdetails")) {
                        process = "unmatcheditemdetails";
                    }
                    if (outfile.contains("allitems")) {
                        process = "allitems";
                    }
                    if (outfile.contains("allitemdetails")) {
                        process = "allitemdetails";
                    }
                   /* if(outfile.contains("allitemsreport"))
                    {
                        process="allitemsreport";
                    }*/
                    Log.d("excelcreate", "inventory excel " + process);

                    OutputStream bottomOutputStream = new FileOutputStream(outfile);
                    // Create a new FastExcel Workbook instance for top sheet
                    Workbook bottomWorkbook = new Workbook(bottomOutputStream, process, "1.0");
                    // Create a worksheet named "Sheet 1" for bottom sheet
                    Worksheet bottomsheet = bottomWorkbook.newWorksheet("Sheet 1");
                    String[] headers;
                    if (process.contains("details")) {
                        if(process.equals("matcheditemdetails")){
                            headers = new String[]{"TID Value", "EPC Value","Counter Name" ,"Category", "Product","Product Code",
                                    "Purity", "Barcode Number", "Item Code", "Box","Pieces", "Designcode", "Image", "Gross Weight", "Stone Weight", "Net Weight",
                                    "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                                    "Party code", "Updated Date", "Updated By", "Status"};

                            for (int i = 0; i < headers.length; i++) {
                                bottomsheet.value(0, i, headers[i]);
                            }

                            int bottomMatchRowIndex = 1;
                            int rowsProcessed = 0;
                            int progressUpdateInterval = 100;
                            for (Itemmodel item1 : processlist) {
                                Itemmodel item = null;
                                item = item1;
                                if (item != null) {
                                    boolean op = item.getAvlQty() == item.getMatchQty();
                                    String op1 = "";
                                    if(op){
                                        op1 = "found";
                                        String[] values = {item.getTidValue(), item.getEpcValue(), item.getCounterName(),
                                                item.getCategory(), item.getProduct(),item.getProductCode() ,item.getPurity(), item.getBarCode(),
                                                item.getCategory(), item.getProduct(),item.getProductCode() ,item.getPurity(), item.getBarCode(),
                                                item.getItemCode(), item.getBox(),item.getPcs(), item.getDiamondClarity(), item.getImageUrl(), String.valueOf(item.getGrossWt()),
                                                String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()),
                                                String.valueOf(item.getMakingGm()), String.valueOf(item.getMakingPer()), String.valueOf(item.getFixedAmount()), String.valueOf(item.getFixedWastage()),
                                                String.valueOf(item.getStoneAmount()), String.valueOf(item.getMrp()), item.getHuidCode(), item.getPartyCode(),
                                                "", "", op1};
                                        createRow(bottomsheet, bottomMatchRowIndex, values);
                                        bottomMatchRowIndex++;
                                        rowsProcessed++;

                                        // Update progress every 100 rows
                                        if (rowsProcessed % progressUpdateInterval == 0) {
                                            publishProgress(bottomMatchRowIndex);
                                        }
                                    }else{
                                        op1 = "not found";
                                    }

                                }

                            }
                        }else{
                            headers = new String[]{"TID Value", "EPC Value", "Counter Name","Category", "Product","Product Code",
                                    "Purity", "Barcode Number", "Item Code", "Box","Pieces","Designcode", "Image", "Gross Weight", "Stone Weight", "Net Weight",
                                    "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                                    "Party code", "Updated Date", "Updated By", "Status"};

                            for (int i = 0; i < headers.length; i++) {
                                bottomsheet.value(0, i, headers[i]);
                            }


                            int bottomMatchRowIndex = 1;
                            int rowsProcessed = 0;
                            int progressUpdateInterval = 100;
                            for (Itemmodel item1 : processlist) {
                                Itemmodel item = null;
                                item = item1;
                                if (item != null) {
                                    boolean op = item.getAvlQty() == item.getMatchQty();
                                    String op1 = "";
                                    if(op){
                                        op1 = "found";
                                    }else{
                                        op1 = "not found";
                                        String[] values = {item.getTidValue(), item.getEpcValue(),item.getCounterName(),
                                                item.getCategory(), item.getProduct(),item.getProductCode() ,item.getPurity(), item.getBarCode(),
                                                item.getItemCode(), item.getBox(), item.getPcs(),item.getDiamondClarity(), item.getImageUrl(), String.valueOf(item.getGrossWt()),
                                                String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()),
                                                String.valueOf(item.getMakingGm()), String.valueOf(item.getMakingPer()), String.valueOf(item.getFixedAmount()), String.valueOf(item.getFixedWastage()),
                                                String.valueOf(item.getStoneAmount()), String.valueOf(item.getMrp()), item.getHuidCode(), item.getPartyCode(),
                                                "", "", op1};
                                        createRow(bottomsheet, bottomMatchRowIndex, values);
                                        bottomMatchRowIndex++;
                                        rowsProcessed++;

                                        // Update progress every 100 rows
                                        if (rowsProcessed % progressUpdateInterval == 0) {
                                            publishProgress(bottomMatchRowIndex);
                                        }
                                    }

                                }

                            }

                        }


                    } else {

                       /* if(process.equals("matcheditems") ){
                            headers = new String[]{"Category", "Product", "Box", "Total Quantity", "Match Quantity", "Total Grosswt", "Match Grosswt",
                                    "Total Stonewt", "Match Stonewt", "Total Netwt", "Match Netwt"};
                            for (int i = 0; i < headers.length; i++) {
                                bottomsheet.value(0, i, headers[i]);
                            }

                            int matchRowIndex = 1;

                            int rowsProcessed = 0;
                            int progressUpdateInterval = 100;
                            for (Itemmodel item : processlist) {
                                if(item.getAvlQty() == item.getMatchQty()) {
                                    String[] values = {item.getCategory(), item.getProduct(), item.getBox(), String.valueOf(item.getAvlQty()),
                                            String.valueOf(item.getMatchQty()), String.valueOf(item.getTotalGwt()), String.valueOf(item.getMatchGwt()), String.valueOf(item.getTotalStonewt()),
                                            String.valueOf(item.getMatchStonewt()), String.valueOf(item.getTotalNwt()), String.valueOf(item.getMatchNwt())};


                                    for (int i = 0; i < values.length; i++) {
                                        createCell(bottomsheet, matchRowIndex, i, values[i]);
                                    }
                                    matchRowIndex++;
                                    rowsProcessed++;

                                    // Update progress every 100 rows
                                    if (rowsProcessed % progressUpdateInterval == 0) {
                                        publishProgress(matchRowIndex);
                                    }
                                }

                            }

                            int totalRowIndex = matchRowIndex;
                            int totalQuantity = 0;
                            int matchQuantity = 0;
                            double totalGrosswt = 0.0;
                            double matchGrosswt = 0.0;
                            double totalStonewt = 0.0;
                            double matchStonewt = 0.0;
                            double totalNetwt = 0.0;
                            double matchNetwt = 0.0;

                            for (Itemmodel item : processlist) {
                                totalQuantity += item.getAvlQty();
                                matchQuantity += item.getMatchQty();
                                totalGrosswt += item.getTotalGwt();
                                matchGrosswt += item.getMatchQty();
                                totalStonewt += item.getTotalStonewt();
                                matchStonewt += item.getMatchStonewt();
                                totalNetwt += item.getTotalNwt();
                                matchNetwt += item.getMatchNwt();
                            }


                            String[] totalValues = {"Total", "", "", String.valueOf(totalQuantity), String.valueOf(matchQuantity),
                                    String.valueOf(totalGrosswt), String.valueOf(matchGrosswt),
                                    String.valueOf(totalStonewt), String.valueOf(matchStonewt),
                                    String.valueOf(totalNetwt), String.valueOf(matchNetwt)};

                            for (int i = 0; i < totalValues.length; i++) {
                                bottomsheet.value(totalRowIndex, i, totalValues[i]);
                            }
                        }else{*/


                        headers = new String[]{"Counter Name","Category", "Product","Product Code", "Box", "T Pieces","T Mpieces","Total Quantity", "Match Quantity", "Total Grosswt", "Match Grosswt",
                                "Total Stonewt", "Match Stonewt", "Total Netwt", "Match Netwt"};
                        for (int i = 0; i < headers.length; i++) {
                            bottomsheet.value(0, i, headers[i]);
                        }

                        int matchRowIndex = 1;

                        int rowsProcessed = 0;
                        int progressUpdateInterval = 100;
                        for (Itemmodel item : processlist) {
//                                if(item.getAvlQty() != item.getMatchQty()){
                            String[] values = {item.getCounterName(),item.getCategory(), item.getProduct(), item.getProductCode(), item.getBox(), String.valueOf(item.getTotPcs()), String.valueOf(item.getTotMPcs()), String.valueOf(item.getAvlQty()),
                                    String.valueOf(item.getMatchQty()), String.valueOf(item.getTotalGwt()), String.valueOf(item.getMatchGwt()), String.valueOf(item.getTotalStonewt()),
                                    String.valueOf(item.getMatchStonewt()), String.valueOf(item.getTotalNwt()), String.valueOf(item.getMatchNwt())};


                            for (int i = 0; i < values.length; i++) {
                                createCell(bottomsheet, matchRowIndex, i, values[i]);
                            }
                            matchRowIndex++;
                            rowsProcessed++;

                            // Update progress every 100 rows
                            if (rowsProcessed % progressUpdateInterval == 0) {
                                publishProgress(matchRowIndex);
                            }
//                                }
                        }

                        int totalRowIndex = matchRowIndex;
                        int totalQuantity = 0;
                        int matchQuantity = 0;
                        double totalGrosswt = 0.0;
                        double matchGrosswt = 0.0;
                        double totalStonewt = 0.0;
                        double matchStonewt = 0.0;
                        double totalNetwt = 0.0;
                        double matchNetwt = 0.0;
                        int totalPieces=0;
                        int totalMPices=0;

                        for (Itemmodel item : processlist) {
                            totalQuantity += item.getAvlQty();
                            matchQuantity += item.getMatchQty();
                            totalGrosswt += item.getTotalGwt();
                            matchGrosswt += item.getMatchGwt();
                            totalStonewt += item.getTotalStonewt();
                            matchStonewt += item.getMatchStonewt();
                            totalNetwt += item.getTotalNwt();
                            matchNetwt += item.getMatchNwt();
                            totalMPices +=(item.getTotMPcs());

                            totalPieces += (item.getTotPcs());
                        }


                        String[] totalValues = {"Total","", "", "","", String.valueOf(totalPieces), String.valueOf(totalMPices), String.valueOf(totalQuantity), String.valueOf(matchQuantity),
                                String.valueOf(totalGrosswt), String.valueOf(matchGrosswt),
                                String.valueOf(totalStonewt), String.valueOf(matchStonewt),
                                String.valueOf(totalNetwt), String.valueOf(matchNetwt)};

                        for (int i = 0; i < totalValues.length; i++) {
                            bottomsheet.value(totalRowIndex, i, totalValues[i]);
                        }
//                        }

                    }

                    bottomWorkbook.finish();
                    bottomOutputStream.close();
                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");
                    File productDir = new File(dir, "inventory");
                    if (!productDir.exists()) {
                        if (!productDir.mkdirs()) {
                            Log.e("TAG", "Failed to create directory: " + productDir.getAbsolutePath());

                        }
                    }

                    File targetFile = new File(productDir, process + ".xlsx");
                    try (FileInputStream in = new FileInputStream(outfile);
                         FileOutputStream out = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "Error copying file: " + e.getMessage());
                    }

                    fileMap.put(process + ".xlsx", targetFile.getAbsolutePath());


                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error creating Excel file: " + e.getMessage();
                }
            }
            return "Excel file created successfully";

        }

        if (frag.equalsIgnoreCase("dailystock")) {
            for (Map.Entry<String, ArrayList<Itemmodel>> entry : excelmap.entrySet()) {
                try {
                    outfile = entry.getKey();
                    ArrayList<Itemmodel> processlist = entry.getValue();
                    String process = "";

                    if (outfile.contains("allitemsreport")) {
                        process = "allitemsreport";
                    }
                    if (outfile.contains("unmatcheditemdetails")) {
                        process = "unmatcheditemdetails";
                    }

                    Log.d("excelcreate", "inventory excel " + process);

                    OutputStream bottomOutputStream = new FileOutputStream(outfile);
                    Workbook bottomWorkbook = new Workbook(bottomOutputStream, process, "1.0");
                    Worksheet bottomsheet = bottomWorkbook.newWorksheet("Sheet 1");

                    String[] headers;
                    if (process.contains("details")) {
                        if (process.equals("matcheditemdetails")) {
                            headers = new String[]{"TID Value", "EPC Value", "Counter Name", "Category", "Product", "Product Code",
                                    "Purity", "Barcode Number", "Item Code", "Box", "Pieces", "Designcode", "Image", "Gross Weight", "Stone Weight", "Net Weight",
                                    "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                                    "Party code", "Updated Date", "Updated By", "Status"};

                            for (int i = 0; i < headers.length; i++) {
                                bottomsheet.value(0, i, headers[i]);
                            }

                            int bottomMatchRowIndex = 1;
                            int rowsProcessed = 0;
                            int progressUpdateInterval = 100;

                            for (Itemmodel item : processlist) {
                                if (item != null && item.getAvlQty() == item.getMatchQty()) {
                                    String op1 = "found";
                                    String[] values = {item.getTidValue(), item.getEpcValue(), item.getCounterName(),
                                            item.getCategory(), item.getProduct(), item.getProductCode(), item.getPurity(), item.getBarCode(),
                                            item.getItemCode(), item.getBox(), item.getPcs(), item.getDiamondClarity(), item.getImageUrl(),
                                            String.valueOf(item.getGrossWt()), String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()),
                                            String.valueOf(item.getMakingGm()), String.valueOf(item.getMakingPer()), String.valueOf(item.getFixedAmount()),
                                            String.valueOf(item.getFixedWastage()), String.valueOf(item.getStoneAmount()), String.valueOf(item.getMrp()),
                                            item.getHuidCode(), item.getPartyCode(), "", "", op1};
                                    createRow(bottomsheet, bottomMatchRowIndex++, values);

                                    if (++rowsProcessed % progressUpdateInterval == 0) {
                                        publishProgress(bottomMatchRowIndex);
                                    }
                                }
                            }
                        } else {
                            headers = new String[]{"Counter Name", "Category", "Product",
                                    "Purity", "Barcode Number", "Item Code", "Pieces", "Gross Weight", "Stone Weight", "Net Weight", "Mrp", "Status"};

                            for (int i = 0; i < headers.length; i++) {
                                bottomsheet.value(0, i, headers[i]);
                            }

                            int bottomMatchRowIndex = 1;
                            int rowsProcessed = 0;
                            int progressUpdateInterval = 100;

                            for (Itemmodel item : processlist) {
                                if (item != null && item.getAvlQty() != item.getMatchQty()) {

                                        String op1 = "not found";
                                        String[] values = {item.getCounterName(),
                                                item.getCategory(), item.getProduct(),item.getPurity(), item.getBarCode(),
                                                item.getItemCode(),  item.getPcs(),
                                                String.valueOf(item.getGrossWt()), String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()),
                                                String.valueOf(item.getMrp()), op1};
                                        createRow(bottomsheet, bottomMatchRowIndex++, values);

                                        if (++rowsProcessed % progressUpdateInterval == 0) {
                                            publishProgress(bottomMatchRowIndex);
                                        }
                                    }
                                }

                        }
                    } else {
                        headers = new String[]{"Counter Name", "Category", "Product", "Total Quantity", "Match Quantity", "Tot Unmatch Qty",
                                "Total Grosswt", "Match Grosswt", "Un Match Gr Wt"};

                        for (int i = 0; i < headers.length; i++) {
                            bottomsheet.value(0, i, headers[i]);
                        }

                        int matchRowIndex = 1;
                        int rowsProcessed = 0;
                        int progressUpdateInterval = 100;

                        for (Itemmodel item : processlist) {
                            if (item.getAvlQty() != 0.0) {

                                String[] values = {item.getCounterName(), item.getCategory(), item.getProduct(), String.valueOf(item.getAvlQty()),
                                        String.valueOf(item.getMatchQty()), String.valueOf(item.getTotUnMatchQty()), String.valueOf(item.getTotalGwt()),
                                        String.valueOf(item.getMatchGwt()), String.valueOf(item.getTotUnmatchGrswt())};

                                for (int i = 0; i < values.length; i++) {
                                    createCell(bottomsheet, matchRowIndex, i, values[i]);
                                }

                                matchRowIndex++;
                                if (++rowsProcessed % progressUpdateInterval == 0) {
                                    publishProgress(matchRowIndex);
                                }
                            }

                            int totalRowIndex = matchRowIndex;
                            int totalQuantity = 0, matchQuantity = 0, totalPieces = 0, totalMPieces = 0;
                            double totalGrosswt = 0, matchGrosswt = 0, unmatchwt, unmatchGrWt = 0;

                            for (Itemmodel item1 : processlist) {
                                totalQuantity += item1.getAvlQty();
                                matchQuantity += item1.getMatchQty();
                                totalGrosswt += item1.getTotalGwt();
                                matchGrosswt += item1.getMatchGwt();
                                unmatchGrWt += item1.getTotUnmatchGrswt();
                                totalPieces += item1.getTotPcs();
                                totalMPieces += item1.getTotMPcs();
                            }

                            unmatchwt = totalQuantity - matchQuantity;

                            String[] totalValues = {"Total", "", "", String.valueOf(totalQuantity), String.valueOf(matchQuantity),
                                    String.valueOf(unmatchwt), String.valueOf(totalGrosswt), String.valueOf(matchGrosswt), String.valueOf(unmatchGrWt)};

                            for (int i = 0; i < totalValues.length; i++) {
                                bottomsheet.value(totalRowIndex, i, totalValues[i]);
                            }
                        }

                        bottomWorkbook.finish();
                        bottomOutputStream.close();
                    }

                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");
                    File productDir = new File(dir, "inventory");
                    if (!productDir.exists() && !productDir.mkdirs()) {
                        Log.e("TAG", "Failed to create directory: " + productDir.getAbsolutePath());
                    }

                    if (process.equalsIgnoreCase("")){

                    }

                    File targetFile = new File(productDir, process + ".xlsx");

                    try (FileInputStream in = new FileInputStream(outfile);
                         FileOutputStream out = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "Error copying file: " + e.getMessage());
                    }

                    // ✅ Delete the temporary .xls file
                    try {
                        File tempFile = new File(outfile);
                        if (tempFile.exists()) {
                            boolean deleted = tempFile.delete();
                            if (!deleted) {
                                Log.w("TAG", "Temporary file not deleted: " + outfile);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("TAG", "Failed to delete temp file: " + ex.getMessage());
                    }

                    // ✅ Use actual .xlsx path in fileMap
                    if (!process.equalsIgnoreCase("")){
                        fileMap.put(process + ".xlsx", targetFile.getAbsolutePath());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error creating Excel file: " + e.getMessage();
                }
            }
            return "Excel file created successfully";
        }


        if (frag.equalsIgnoreCase("product")) {

            for (Map.Entry<String, ArrayList<Itemmodel>> entry : excelmap.entrySet()) {

                outfile = entry.getKey();
                ArrayList<Itemmodel> processlist = new ArrayList<>();
                processlist = entry.getValue();
                try {

                    OutputStream bottomOutputStream = new FileOutputStream(outfile);
                    // Create a new FastExcel Workbook instance for top sheet
                    Workbook bottomWorkbook = new Workbook(bottomOutputStream, "unfilled list", "1.0");
                    // Create a worksheet named "Sheet 1" for bottom sheet
                    Worksheet bottomsheet = bottomWorkbook.newWorksheet("Sheet 1");

                    String[] bottomHeaders = {"TID Value", "EPC Value", "Counter Name", "Category", "Product","Product Code",
                            "Purity", "Barcode Number", "Item Code", "Box","Pieces", "Gross Weight", "Stone Weight", "Net Weight",
                            "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                            "Party code", "Updated Date", "Updated By"};

                    for (int i = 0; i < bottomHeaders.length; i++) {
                        bottomsheet.value(0, i, bottomHeaders[i]);
                    }

                    int bottomMatchRowIndex = 1;
                    int rowsProcessed = 0;
                    int progressUpdateInterval = 100;
                    for (Itemmodel item : processlist) {
                        String[] values = {item.getTidValue(), item.getEpcValue(),item.getCounterName(),
                                item.getCategory(), item.getProduct(),item.getProductCode() ,item.getPurity(), item.getBarCode(),
                                item.getItemCode(), item.getBox(),item.getPcs(), String.valueOf(item.getGrossWt()),
                                String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()),
                                String.valueOf(item.getMakingGm()), String.valueOf(item.getMakingPer()), String.valueOf(item.getFixedAmount()), String.valueOf(item.getFixedWastage()),
                                String.valueOf(item.getStoneAmount()), String.valueOf(item.getMrp()), item.getHuidCode(), item.getPartyCode(),
                                ""};
                        createRow(bottomsheet, bottomMatchRowIndex, values);
//                publishProgress(bottomMatchRowIndex);
                        bottomMatchRowIndex++;
                        rowsProcessed++;

                        // Update progress every 100 rows
                        if (rowsProcessed % progressUpdateInterval == 0) {
                            publishProgress(bottomMatchRowIndex);
                        }
                    }
                    publishProgress(bottomMatchRowIndex);
                    bottomWorkbook.finish();
                    bottomOutputStream.close();

                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");
                    File productDir = new File(dir, "product");
                    if (!productDir.exists()) {
                        if (!productDir.mkdirs()) {
                            Log.e("TAG", "Failed to create directory: " + productDir.getAbsolutePath());

                        }
                    }

                    File targetFile = new File(productDir, "unfilleddata.xlsx");
                    try (FileInputStream in = new FileInputStream(topfilepath);
                         FileOutputStream out = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "Error copying file: " + e.getMessage());
                    }

                    fileMap.put("unfilleddate.xlsx", outfile);

                    return "Excel file created successfully";
                } catch (Exception e) {
                    return "failed to create excel sheet";

                }
            }


        }

        if (frag.equalsIgnoreCase("stockreport")) {

            for (Map.Entry<String, ArrayList<Itemmodel>> entry : excelmap.entrySet()) {

                outfile = entry.getKey();
                ArrayList<Itemmodel> processlist = new ArrayList<>();
                processlist = entry.getValue();
                try {

                    OutputStream bottomOutputStream = new FileOutputStream(outfile);
                    // Create a new FastExcel Workbook instance for top sheet
                    Workbook bottomWorkbook = new Workbook(bottomOutputStream, "stock report", "1.0");
                    // Create a worksheet named "Sheet 1" for bottom sheet
                    Worksheet bottomsheet = bottomWorkbook.newWorksheet("Sheet 1");

                    String[] bottomHeaders = {"TID Value", "EPC Value","Counter Name",  "Category", "Product","Product Code",
                            "Purity", "Barcode Number", "Item Code", "Box","Pieces" ,"Gross Weight", "Stone Weight", "Net Weight",
                            "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                            "Party code", "Updated Date", "Updated By", "Status"};

                    for (int i = 0; i < bottomHeaders.length; i++) {
                        bottomsheet.value(0, i, bottomHeaders[i]);
                    }

                    int bottomMatchRowIndex = 1;
                    int rowsProcessed = 0;
                    int progressUpdateInterval = 100;
                    for (Itemmodel item : processlist) {
                        String[] values = {item.getTidValue(), item.getEpcValue(),item.getCounterName(),
                                item.getCategory(), item.getProduct(),item.getProductCode(), item.getPurity(), item.getBarCode(),
                                item.getItemCode(), item.getBox(),item.getPcs() ,String.valueOf(item.getGrossWt()),
                                String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()),
                                String.valueOf(item.getMakingGm()), String.valueOf(item.getMakingPer()), String.valueOf(item.getFixedAmount()), String.valueOf(item.getFixedWastage()),
                                String.valueOf(item.getStoneAmount()), String.valueOf(item.getMrp()), item.getHuidCode(), item.getPartyCode(),
                                "", "", item.getOperation()};
                        createRow(bottomsheet, bottomMatchRowIndex, values);
//                publishProgress(bottomMatchRowIndex);
                        bottomMatchRowIndex++;
                        rowsProcessed++;

                        // Update progress every 100 rows
                        if (rowsProcessed % progressUpdateInterval == 0) {
                            publishProgress(bottomMatchRowIndex);
                        }
                    }
                    publishProgress(bottomMatchRowIndex);
                    bottomWorkbook.finish();
                    bottomOutputStream.close();

                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");
                    File productDir = new File(dir, "stockreports");
                    if (!productDir.exists()) {
                        if (!productDir.mkdirs()) {
                            Log.e("TAG", "Failed to create directory: " + productDir.getAbsolutePath());

                        }
                    }

                    File targetFile = new File(productDir, "stockreport.xlsx");
                    try (FileInputStream in = new FileInputStream(topfilepath);
                         FileOutputStream out = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "Error copying file: " + e.getMessage());
                    }

                    fileMap.put("stockreport.xlsx", outfile);

                    return "Excel file created successfully";
                } catch (Exception e) {
                    return "failed to create excel sheet";

                }
            }

        }

        if (frag.equalsIgnoreCase("billlist")) {

            for (Map.Entry<String, ArrayList<Itemmodel>> entry : excelmap.entrySet()) {

                outfile = entry.getKey();
//                ArrayList<Itemmodel> processlist = new ArrayList<>();
//                processlist = entry.getValue();
//                int matchRowIndex = 1;
                ArrayList<Itemmodel> processlist = new ArrayList<>(entry.getValue());

                // Sort the processlist by product name
                processlist.sort(Comparator.comparing(Itemmodel::getProduct));


                /*try {

                    OutputStream bottomOutputStream = new FileOutputStream(outfile);
                    // Create a new FastExcel Workbook instance for top sheet
                    Workbook bottomWorkbook = new Workbook(bottomOutputStream, "unfilled list", "1.0");
                    // Create a worksheet named "Sheet 1" for bottom sheet
                    Worksheet bottomsheet = bottomWorkbook.newWorksheet("Sheet 1");


                    String[] bottomHeaders = {"Sno", "Item name", "Product", "Purity", "Gross Wt",
                            "Stone Wt", "Net Wt", "Barcode", "Itemcode", "Qty",
                            "Estimation number"};

                    for (int i = 0; i < bottomHeaders.length; i++) {
                        bottomsheet.value(0, i, bottomHeaders[i]);
                    }

                    int bottomMatchRowIndex = 1;
                    int rowsProcessed = 0;
                    int progressUpdateInterval = 100;
                    for (Itemmodel item : processlist) {
                        String[] values = {String.valueOf(rowsProcessed+1),item.getCategory(), item.getProduct(), item.getPurity(), String.valueOf(item.getGrossWt()),
                                String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()), item.getBarCode(), item.getItemCode(),"1", item.getInvoiceNumber() };

                        createRow(bottomsheet, bottomMatchRowIndex, values);
//                publishProgress(bottomMatchRowIndex);
                        bottomMatchRowIndex++;
                        rowsProcessed++;

                        // Update progress every 100 rows
                        if (rowsProcessed % progressUpdateInterval == 0) {
                            publishProgress(bottomMatchRowIndex);
                        }
                    }
                    publishProgress(bottomMatchRowIndex);

                    int totalRowIndex = bottomMatchRowIndex;
                    int totalQuantity = 0;

                    double totalGrosswt = 0.0;

                    double totalStonewt = 0.0;

                    double totalNetwt = 0.0;

                    String customername = processlist.get(0).getCustomerName();

                    for (Itemmodel item : processlist) {
                        totalQuantity ++;
                        totalGrosswt = totalGrosswt+ item.getGrossWt();
                        totalStonewt += item.getStoneWt();
                        totalNetwt += item.getNetWt();

                    }

                    String[] totalValues = {"Total", String.valueOf(totalQuantity), customername, String.valueOf(totalGrosswt), String.valueOf(totalStonewt),
                            String.valueOf(totalNetwt), "",
                            "", String.valueOf(entry.getValue().size()), entry.getValue().get(0).getInvoiceNumber()};

                    for (int i = 0; i < totalValues.length; i++) {
                        bottomsheet.value(totalRowIndex, i, totalValues[i]);
                    }

                    bottomWorkbook.finish();
                    bottomOutputStream.close();

                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");
                    File productDir = new File(dir, "Bill");
                    if (!productDir.exists()) {
                        if (!productDir.mkdirs()) {
                            Log.e("TAG", "Failed to create directory: " + productDir.getAbsolutePath());

                        }
                    }

                    File targetFile = new File(productDir, processlist.get(0).getInvoiceNumber() + "estimation.xlsx");
                    try (FileInputStream in = new FileInputStream(topfilepath);
                         FileOutputStream out = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "Error copying file: " + e.getMessage());
                    }

                    fileMap.put(processlist.get(0).getInvoiceNumber() + "estimation.xlsx", outfile);

                    return "Excel file created successfully";
                } catch (Exception e) {
                    return "failed to create excel sheet";

                }*/

                try {

                    OutputStream bottomOutputStream = new FileOutputStream(outfile);
                    // Create a new FastExcel Workbook instance for top sheet
                    Workbook bottomWorkbook = new Workbook(bottomOutputStream, "unfilled list", "1.0");
                    // Create a worksheet named "Sheet 1" for bottom sheet
                    Worksheet bottomsheet = bottomWorkbook.newWorksheet("Sheet 1");

                    int rowIndex = 0;

                    String companyName = "REEVAZZ CZ JEWELLS"; // Replace with actual company name
                    bottomsheet.value(rowIndex++, 0, companyName);

                    String melt = "";
                    if(processlist.get(0).getDescription() != null){
                        melt = String.valueOf(processlist.get(0).getDescription());
                    }

                    String mt = "Melting"; // Replace with actual company name
                    bottomsheet.value(0, 2, mt);
                    bottomsheet.value(1, 2, melt);

                    bottomsheet.width(0, 15);
                    // Add Customer Details
                    String customerDetails = "Customer Name : " + processlist.get(0).getCustomerName();
                    bottomsheet.value(rowIndex++, 0, customerDetails);
                    bottomsheet.width(0, 8);
                    // Add a blank row for separation
                    rowIndex++;
                    String[] bottomHeaders = {"Sno", "Item name", "Design number", "Gross Wt",
                            "Stone Wt", "Net Wt","Stone Amt"};

                    for (int i = 0; i < bottomHeaders.length; i++) {
                        bottomsheet.value(rowIndex, i, bottomHeaders[i]);
                    }

                    int bottomMatchRowIndex = 1;
                    rowIndex++;
                    int rowsProcessed = 0;
                    int progressUpdateInterval = 100;
                    for (Itemmodel item : processlist) {
//                        String[] values = {String.valueOf(rowsProcessed+1),item.getCategory(), item.getProduct(), item.getPurity(), String.valueOf(item.getGrossWt()),
//                                String.valueOf(item.getStoneWt()), String.valueOf(item.getNetWt()), item.getBarCode(), item.getItemCode(),"1", item.getInvoiceNumber() };

                        String[] values = {String.valueOf(rowsProcessed+1), item.getProduct(), item.getItemCode(), decimalFormat.format(item.getGrossWt()),
                                decimalFormat.format(item.getStoneWt()), decimalFormat.format(item.getNetWt()),decimalFormat.format(item.getStoneAmount())};

                        createRow(bottomsheet, rowIndex, values);
//                publishProgress(bottomMatchRowIndex);
                        bottomMatchRowIndex++;
                        rowsProcessed++;
                        rowIndex++;

                        // Update progress every 100 rows
                        if (rowsProcessed % progressUpdateInterval == 0) {
                            publishProgress(bottomMatchRowIndex);
                        }
                    }
                    publishProgress(bottomMatchRowIndex);

                    int totalRowIndex = bottomMatchRowIndex;
                    int totalQuantity = 0;

                    double totalGrosswt = 0.0;

                    double totalStonewt = 0.0;

                    double totalNetwt = 0.0;
                    double totalStoneAMt=0.0;

                    String customername = processlist.get(0).getCustomerName();
                    bottomsheet.value(rowIndex, 0, "TOTAL");
                    rowIndex++;
                    for (Itemmodel item : processlist) {
                        totalQuantity ++;
                        totalGrosswt = totalGrosswt+ item.getGrossWt();
                        totalStonewt += item.getStoneWt();
                        totalNetwt += item.getNetWt();
                        totalStoneAMt +=item.getStoneAmount();


                    }

                    String[] totalValues = { String.valueOf(totalQuantity),"", "", decimalFormat.format(totalGrosswt), decimalFormat.format(totalStonewt),
                            decimalFormat.format(totalNetwt),decimalFormat.format(totalStoneAMt), ""};

                    for (int i = 0; i < totalValues.length; i++) {
                        bottomsheet.value(rowIndex, i, totalValues[i]);
                    }

                    bottomWorkbook.finish();
                    bottomOutputStream.close();

                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");
                    File productDir = new File(dir, "Bill");
                    if (!productDir.exists()) {
                        if (!productDir.mkdirs()) {
                            Log.e("TAG", "Failed to create directory: " + productDir.getAbsolutePath());

                        }
                    }

                    File targetFile = new File(productDir, processlist.get(0).getInvoiceNumber() + "estimation.xlsx");
                    try (FileInputStream in = new FileInputStream(topfilepath);
                         FileOutputStream out = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "Error copying file: " + e.getMessage());
                    }

                    fileMap.put(processlist.get(0).getInvoiceNumber() + "estimation.xlsx", outfile);

                    return "Excel file created successfully";
                } catch (Exception e) {
                    return "failed to create excel sheet";

                }
            }
        }

        return "something went wrong";
    }

    private void createCell(Worksheet bottomsheet, int bottomMatchRowIndex, int i, String value) {
        bottomsheet.value(bottomMatchRowIndex, i, value);
    }

    private void createRow(Worksheet sheet, int rowIndex, String[] values) {
        for (int i = 0; i < values.length; i++) {
            sheet.value(rowIndex, i, values[i]);
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        // Handle the result, e.g., show a toast message
//        if(frag.equalsIgnoreCase("stockreport")){
//
//
//        }else {
//            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
//            Globalcomponents instance = new Globalcomponents();
//            List<String> emails = instance.readallemails(context);
////            showbottom(context, emails);
//        }

        if (result.equals("Excel file created successfully")) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            Globalcomponents instance = new Globalcomponents();
            List<String> emails = instance.readallemails(context);
            showbottom(context, emails);
        } else {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            // Handle the case when Excel file creation fails
        }

    }


    private void showbottom(Context context, List<String> emails) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        final String[][] emailsArray = {emails.toArray(new String[0])};
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCancelable(false);
//        View contentView = inflater.getContext().getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        View contentView = inflater.inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(contentView);
        ImageButton close = contentView.findViewById(R.id.closeButton);
        TextView title = contentView.findViewById(R.id.maintitle);
        Button addbtn = contentView.findViewById(R.id.additem);
        EditText itemname = contentView.findViewById(R.id.itemname);
        ListView spinnerlist = contentView.findViewById(R.id.spinnerlist);
        Button sendall = contentView.findViewById(R.id.allmails);
        sendall.setVisibility(View.VISIBLE);
        title.setText("Emails");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, emails);
        spinnerlist.setAdapter(adapter);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemname.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isValidGmailAddress(itemname.getText().toString())) {
                    Globalcomponents instance = new Globalcomponents();
                    boolean b = instance.insetemail(itemname.getText().toString().trim(), context);
                    if (b) {
                        Toast.makeText(context, "email address added", Toast.LENGTH_SHORT).show();
                        emailsArray[0] = instance.readallemails(context).toArray(new String[0]);
                        emails.clear();
                        emails.addAll(Arrays.asList(emailsArray[0]));
                        itemname.setText("");
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "failed to add email address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });


        spinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!networkUtils.isNetworkAvailable()) {
                    Toast.makeText(context, "Please check internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                String selecteditem = (String) adapterView.getItemAtPosition(position);
                Toast.makeText(context, "Selected " + selecteditem + " : " + selecteditem, Toast.LENGTH_SHORT).show();

                List<String> ems = new ArrayList<>();
                ems.add(selecteditem);
//                Map<String, String> fileMap = new HashMap<>();
//                if (frag.equalsIgnoreCase("inventory")) {
////                    fileMap = new HashMap<>();
//                    fileMap.put("allitems.xlsx", topfilepath);
//                    fileMap.put("itemdetails.xlsx", bottomfilepath);
//                } else {
////                    Map<String, String> fileMap = new HashMap<>();
//                    fileMap.put("allitems.xlsx", topfilepath);
//                }
              /*  Globalcomponents.sendglobalemil sendEmailTask = new Globalcomponents.sendglobalemil(
                        "reports@loyalstring.com", "Loyal@321", ems, "loyal sting", "", fileMap, "inventory", context
                );*/
                /*added original email*/
                Globalcomponents.sendglobalemil sendEmailTask = new Globalcomponents.sendglobalemil(
                        "android@loyalstring.com", "Android@456#" , ems, "loyal sting", "", fileMap, "inventory", context
                );
                sendEmailTask.execute();


            }
        });

//        spinnerlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String selectedItem = spinnerlist.getItemAtPosition(i).toString();
//                String tableName = "email"; // Replace with your actual table name
//                categorydatabase db = new categorydatabase(context);
//                db.deleteRow(selectedItem, tableName);
//
//                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerlist.getAdapter();
//                adapter.remove(selectedItem);
//                adapter.notifyDataSetChanged();
//
//                return true;
//            }
//        });

        sendall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                multiple = true;
//                emaildialog.show();
//                sendEmails();
            }
        });
        bottomSheetDialog.show();

    }

    public static boolean isValidGmailAddress(String email) {
        String regex = "\\b[A-Za-z0-9._%+-]+@gmail\\.com\\b";
        return true;//Pattern.matches(regex, email);
    }

}

 /*Workbook workbook = new XSSFWorkbook();
        Workbook workbook1 = new XSSFWorkbook();
        Sheet topsheet = workbook.createSheet("allitems list");
        Sheet bottomsheet = workbook1.createSheet("allitemdetails list");
        Row matchHeaderRow = topsheet.createRow(0);
        Row bmatchHeaderRow = bottomsheet.createRow(0);
        String[] headers = {"Category", "Product", "Box", "Total Quantity", "Match Quantity", "Total Grosswt", "Match Grosswt",
                "Total Stonewt", "Match Stonewt", "Total Netwt", "Match Netwt"};
                *//*{"Category", "EPC Value", "Category", "Product",
                "Purity", "Barcode Number", "Item Code", "Box", "Gross Weight", "Stone Weight", "Net Weight",
                "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                "Party code", "Updated Date", "Updated By", "Status", "Tagtx"};*//*

        String[] bottomheader = {"TID Value", "EPC Value", "Category", "Product",
                "Purity", "Barcode Number", "Item Code", "Box", "Gross Weight", "Stone Weight", "Net Weight",
                "Making gm", "Making %", "Fixed amount", "Fixed Wastage", "Stone amount", "Mrp", "Huid code",
                "Party code", "Updated Date", "Updated By", "Status", "Tagtx"};


        for (int i = 0; i < headers.length; i++) {
            createCell(matchHeaderRow, i, headers[i]);
        }
        for (int i = 0; i < bottomheader.length; i++) {
            createCell(bmatchHeaderRow, i, bottomheader[i]);
        }

        // Populate matchlist data
        int matchRowIndex = 1;
        for (inventorymodel item : toplist) {
            Row row = topsheet.createRow(matchRowIndex++);

            String[] values = {item.getCategory(), item.getProduct(), item.getBox(), item.getAvlqty(),
                    item.getMatchqty(), item.getTotalgwt(), item.getMatchgwt(), item.getTotalstonewt(),
                    item.getMatchstonewt(), item.getTotalnwt(), item.getMatchnwt()};
                    *//*{item.getTidvalue(), item.getEpcvalue(),
                    item.getCategory(), item.getProduct(), item.getPurity(), item.getBarcode(),
                    item.getItemcode(), item.getBox(), item.getGrosswt(),
                    item.getStonewt(), item.getNetwt(),
                    item.getMakinggm(), item.getMakingper(), item.getFixedamont(), item.getFixedwastage(),
                    item.getStoneamount(), item.getMrp(), item.getHuidcode(), item.getPartycode(),
                    "", "", ""};*//*

            for (int i = 0; i < values.length; i++) {
                createCell(row, i, values[i]);
            }
            publishProgress(matchRowIndex);
        }
        int totalQuantity = 0;
        int matchQuantity = 0;
        double totalGrosswt = 0.0;
        double matchGrosswt = 0.0;
        double totalStonewt = 0.0;
        double matchStonewt = 0.0;
        double totalNetwt = 0.0;
        double matchNetwt = 0.0;

        for (inventorymodel item : toplist) {
            totalQuantity += Integer.parseInt(item.getAvlqty());
            matchQuantity += Integer.parseInt(item.getMatchqty());
            totalGrosswt += Double.parseDouble(item.getTotalgwt());
            matchGrosswt += Double.parseDouble(item.getMatchgwt());
            totalStonewt += Double.parseDouble(item.getTotalstonewt());
            matchStonewt += Double.parseDouble(item.getMatchstonewt());
            totalNetwt += Double.parseDouble(item.getTotalnwt());
            matchNetwt += Double.parseDouble(item.getMatchnwt());
        }

// Create the last row for totals
        Row totalRow = topsheet.createRow(matchRowIndex);
        String[] totalValues = {"Total", "", "", String.valueOf(totalQuantity), String.valueOf(matchQuantity),
                String.valueOf(totalGrosswt), String.valueOf(matchGrosswt),
                String.valueOf(totalStonewt), String.valueOf(matchStonewt),
                String.valueOf(totalNetwt), String.valueOf(matchNetwt)};

        for (int i = 0; i < totalValues.length; i++) {
            createCell(totalRow, i, totalValues[i]);
        }
        for (inventorymodel item : bottomlist) {
            Row row = bottomsheet.createRow(matchRowIndex++);

            String[] values = {item.getTidvalue(), item.getEpcvalue(),
                    item.getCategory(), item.getProduct(), item.getPurity(), item.getBarcode(),
                    item.getItemcode(), item.getBox(), item.getGrosswt(),
                    item.getStonewt(), item.getNetwt(),
                    item.getMakinggm(), item.getMakingper(), item.getFixedamont(), item.getFixedwastage(),
                    item.getStoneamount(), item.getMrp(), item.getHuidcode(), item.getPartycode(),
                    "", "", ""};

            for (int i = 0; i < values.length; i++) {
                createCell(row, i, values[i]);
            }
            publishProgress(matchRowIndex);
        }




        try (FileOutputStream outputStream = new FileOutputStream(topfilepath)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating Excel file";
        }
        try (FileOutputStream outputStream = new FileOutputStream(bottomfilepath)) {
            workbook1.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating Excel file";
        }

        return "Excel file created successfully";*/



    /*private void createHeaderRow(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i]);
        }
    }

    private void populateSheetWithData(Sheet sheet, List<inventorymodel> dataList, int startRowIndex) {
        int matchRowIndex = startRowIndex;

        for (inventorymodel item : dataList) {
            Row row = sheet.createRow(matchRowIndex++);
            String[] values = {item.getTidvalue(), item.getEpcvalue(),
                    item.getCategory(), item.getProduct(), item.getPurity(), item.getBarcode(),
                    item.getItemcode(), item.getBox(), item.getGrosswt(),
                    item.getStonewt(), item.getNetwt(),
                    item.getMakinggm(), item.getMakingper(), item.getFixedamont(), item.getFixedwastage(),
                    item.getStoneamount(), item.getMrp(), item.getHuidcode(), item.getPartycode(),
                    "", "", ""};

            for (int i = 0; i < values.length; i++) {
                createCell(row, i, values[i]);
            }
            publishProgress(matchRowIndex);
        }
    }

    private void populateSheetWithtopData(Sheet sheet, List<inventorymodel> dataList, int startRowIndex) {
        int matchRowIndex = startRowIndex;

        for (inventorymodel item : dataList) {
            Row row = sheet.createRow(matchRowIndex++);
            String[] values = {item.getCategory(), item.getProduct(), item.getBox(), item.getAvlqty(),
                    item.getMatchqty(), item.getTotalgwt(), item.getMatchgwt(), item.getTotalstonewt(),
                    item.getMatchstonewt(), item.getTotalnwt(), item.getMatchnwt()};

            for (int i = 0; i < values.length; i++) {
                createCell(row, i, values[i]);
            }
        }
    }



    private static void createCell(Row row, int columnIdx, String value) {
        Cell cell = row.createCell(columnIdx);
        cell.setCellValue(value);
    }*/


  /*private void populateSheet(Sheet bottomsheet, List<inventorymodel> bottomlist) {
        // Populate bottom sheet data
        int bottomRowIndex = 1;
        for (inventorymodel item : bottomlist) {
            Row row = bottomsheet.createRow(bottomRowIndex++);

            String[] values = {
                    item.getTidvalue(), item.getEpcvalue(),
                    item.getCategory(), item.getProduct(), item.getPurity(), item.getBarcode(),
                    item.getItemcode(), item.getBox(), item.getGrosswt(),
                    item.getStonewt(), item.getNetwt(),
                    item.getMakinggm(), item.getMakingper(), item.getFixedamont(), item.getFixedwastage(),
                    item.getStoneamount(), item.getMrp(), item.getHuidcode(), item.getPartycode(),
                    "", "", ""
            };

            for (int i = 0; i < values.length; i++) {
                createCell(row, i, values[i]);
            }
            // publishProgress(bottomRowIndex);  // You may use this if needed
        }

    }

    private void populatetopSheet(Sheet topsheet, List<inventorymodel> toplist) {
        // Populate topsheet data
        int topRowIndex = 1;
        for (inventorymodel item : toplist) {
            Row row = topsheet.createRow(topRowIndex++);

            String[] values = {
                    item.getCategory(), item.getProduct(), item.getBox(), item.getAvlqty(),
                    item.getMatchqty(), item.getTotalgwt(), item.getMatchgwt(), item.getTotalstonewt(),
                    item.getMatchstonewt(), item.getTotalnwt(), item.getMatchnwt()
            };

            for (int i = 0; i < values.length; i++) {
                createCell(row, i, values[i]);
            }
            // publishProgress(topRowIndex);  // You may use this if needed
        }

// Calculate totals for topsheet
        int totalQuantity = 0;
        int matchQuantity = 0;
        double totalGrosswt = 0.0;
        double matchGrosswt = 0.0;
        double totalStonewt = 0.0;
        double matchStonewt = 0.0;
        double totalNetwt = 0.0;
        double matchNetwt = 0.0;

        for (inventorymodel item : toplist) {
            totalQuantity += Integer.parseInt(item.getAvlqty());
            matchQuantity += Integer.parseInt(item.getMatchqty());
            totalGrosswt += Double.parseDouble(item.getTotalgwt());
            matchGrosswt += Double.parseDouble(item.getMatchgwt());
            totalStonewt += Double.parseDouble(item.getTotalstonewt());
            matchStonewt += Double.parseDouble(item.getMatchstonewt());
            totalNetwt += Double.parseDouble(item.getTotalnwt());
            matchNetwt += Double.parseDouble(item.getMatchnwt());
        }

// Create the last row for totals
        Row totalRow = topsheet.createRow(topRowIndex);
        String[] totalValues = {
                "Total", "", "", String.valueOf(totalQuantity), String.valueOf(matchQuantity),
                String.valueOf(totalGrosswt), String.valueOf(matchGrosswt),
                String.valueOf(totalStonewt), String.valueOf(matchStonewt),
                String.valueOf(totalNetwt), String.valueOf(matchNetwt)
        };

        for (int i = 0; i < totalValues.length; i++) {
            createCell(totalRow, i, totalValues[i]);
        }

    }

    private static void createHeaders(Sheet sheet, String... headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i]);
        }
    }
    private void writeBufferedRows(Sheet sheet, List<String[]> bufferedRows, int startRowIndex) {
        for (String[] values : bufferedRows) {
            Row row = sheet.createRow(startRowIndex++);
            for (int i = 0; i < values.length; i++) {
                createCell(row, i, values[i]);
            }
        }
    }*/



        /*for (int i = 0; i < headers.length; i++) {
            createCell(matchHeaderRow, i, headers[i]);
        }

        int totalItems = matchlist.size();
        int maxBatchSize = 1000;
        int percentage = 5;
        int batchSize = Math.min(totalItems * percentage / 100, maxBatchSize);
        batchSize = Math.max(batchSize, 1);
        int matchRowIndex = 1;

        try (FileOutputStream outputStream = new FileOutputStream(filePath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {

            List<String[]> bufferedRows = new ArrayList<>(batchSize);

            for (itemmodel item : matchlist) {
                String[] values = { item.getTidvalue(), item.getEpcvalue(),
                        item.getCategory(), item.getProduct(), item.getPurity(), item.getBarcode(),
                        item.getItemcode(), item.getBox(), item.getGrosswt(),
                        item.getStonewt(), item.getNetwt(),
                        item.getMakinggm(), item.getMakingper(), item.getFixedamont(), item.getFixedwastage(),
                        item.getStoneamount(), item.getMrp(), item.getHuidcode(), item.getPartycode(),
                        "", ""};
                bufferedRows.add(values);

                if (bufferedRows.size() == batchSize) {
                    writeBufferedRows(matchSheet, bufferedRows, matchRowIndex);
                    matchRowIndex += bufferedRows.size();
                    publishProgress(matchRowIndex);

                    bufferedRows.clear();
                }
            }

            // Write the remaining buffered rows
            if (!bufferedRows.isEmpty()) {
                writeBufferedRows(matchSheet, bufferedRows, matchRowIndex);
            }

            workbook.write(bufferedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating Excel file";
        }

        return "Excel file created successfully";*/

//new excel code

                /*topsheet.value(matchRowIndex, 0, item.getCategory());
                topsheet.value(matchRowIndex, 1, item.getProduct());
                topsheet.value(matchRowIndex, 2, item.getBox());
                topsheet.value(matchRowIndex, 3, item.getAvlqty());
                topsheet.value(matchRowIndex, 4, item.getMatchqty());
                topsheet.value(matchRowIndex, 5, item.getTotalgwt());
                topsheet.value(matchRowIndex, 6, item.getMatchgwt());
                topsheet.value(matchRowIndex, 7, item.getTotalstonewt());
                topsheet.value(matchRowIndex, 8, item.getMatchstonewt());
                topsheet.value(matchRowIndex, 9, item.getTotalnwt());
                topsheet.value(matchRowIndex, 10, item.getMatchnwt());*/


 /* bottomsheet.value(bottomMatchRowIndex, 0, item.getTidvalue());
                bottomsheet.value(bottomMatchRowIndex, 1, item.getEpcvalue());
                bottomsheet.value(bottomMatchRowIndex, 2, item.getCategory());
                bottomsheet.value(bottomMatchRowIndex, 3, item.getProduct());
                bottomsheet.value(bottomMatchRowIndex, 4, item.getPurity());
                bottomsheet.value(bottomMatchRowIndex, 5, item.getBarcode());
                bottomsheet.value(bottomMatchRowIndex, 6, item.getItemcode());
                bottomsheet.value(bottomMatchRowIndex, 7, item.getBox());
                bottomsheet.value(bottomMatchRowIndex, 8, item.getGrosswt());
                bottomsheet.value(bottomMatchRowIndex, 9, item.getStonewt());
                bottomsheet.value(bottomMatchRowIndex, 10, item.getNetwt());
                bottomsheet.value(bottomMatchRowIndex, 11, item.getMakinggm());
                bottomsheet.value(bottomMatchRowIndex, 12, item.getMakingper());
                bottomsheet.value(bottomMatchRowIndex, 13, item.getFixedamont());
                bottomsheet.value(bottomMatchRowIndex, 14, item.getFixedwastage());
                bottomsheet.value(bottomMatchRowIndex, 15, item.getStoneamount());
                bottomsheet.value(bottomMatchRowIndex, 16, item.getMrp());
//                bottomsheet.value(bottomMatchRowIndex, 17, item.getHuidcode());
                bottomsheet.value(bottomMatchRowIndex, 18, item.getHuidcode());
                bottomsheet.value(bottomMatchRowIndex, 19, item.getPartycode());
                // Add empty values for the remaining columns
                bottomsheet.value(bottomMatchRowIndex, 20, "");
                bottomsheet.value(bottomMatchRowIndex, 21, "");
                bottomsheet.value(bottomMatchRowIndex, 22, "");*/