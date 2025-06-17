package com.loyalstring.LatestBackground;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.loyalstring.LatestApis.ApiClient;
import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestApis.ProductRequestPayload;
import com.loyalstring.LatestCallBacks.ActivationCallback;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.apiresponse.ClientCodeRequest;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.transactionhelper.TransactionIDGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";
    private ApiService apiService;
    EntryDatabase entryDatabase;
//    HashMap<String, Itemmodel> ml;
    MyApplication myapp;
    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    private Set<String> validTIDs = new HashSet<>();


    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        // Initialize Retrofit
        entryDatabase = new EntryDatabase(context);
        myapp = (MyApplication) context.getApplicationContext();
        context = context;
        sharedPreferencesManager = new SharedPreferencesManager(context);


        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.e(TAG, "Processing");

            Clients client = sharedPreferencesManager.readLoginData().getEmployee().getClients();
            if (client.getRfidType().equalsIgnoreCase("websingle")) {
                Log.d("@@","@@"+client.getRfidType());
                fetchsinglePaginatedData(client.getClientCode());
            } else if (client.getRfidType().equalsIgnoreCase("webreusable")) {
                fetchPaginatedData(client.getClientCode());
                Log.d("@@1","@@1"+client.getRfidType());
            }

            updatebills(client.getClientCode());
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error fetching data", e);
            return Result.failure();
        }
    }



    private void fetchsinglePaginatedData(String clientCode) {
        int currentId = 0;
        String size = "500"; // Set your desired page size

        fetchsingleData(currentId, size, clientCode); // Start fetching data
    }

    private void fetchsingleData(int currentId, String size, String clientCode) {
        ProductRequestPayload payload = new ProductRequestPayload(clientCode, "all", currentId, size);
        Log.d("@@2","@@2");
        ClientCodeRequest getAlllableproducts=new ClientCodeRequest(clientCode);
      //  Call<List<AlllabelResponse.LabelItem>> call = apiService.getBatchedLabelledStock(payload);
        Call<List<AlllabelResponse.LabelItem>> call = apiService.getAlllableproducts(getAlllableproducts);
        call.enqueue(new Callback<List<AlllabelResponse.LabelItem>>() {
            @Override
            public void onResponse(Call<List<AlllabelResponse.LabelItem>> call, Response<List<AlllabelResponse.LabelItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AlllabelResponse.LabelItem> productResponse = response.body();

                    // Check if the response is empty
                    if (productResponse.isEmpty()) {
                        Log.d(TAG, "No more items to process.");
                        return; // Exit the processing
                    }
                    List<AlllabelResponse.LabelItem> productResponse1 = new ArrayList<>();
                    for (AlllabelResponse.LabelItem item : productResponse) {

//                        item.setrFIDCode(item.getItemCode());
                        if (item.getStatus().equalsIgnoreCase("active")) {
                            Log.d(TAG, "No more items to process.@@");
                            if (item.getItemCode() != null && !item.getItemCode().isEmpty()) {
                                Log.d(TAG, "No more items to process."+"hexvalue");
                                String hexvalue = convertToHex(item.getItemCode());
                                if (hexvalue != null && !hexvalue.isEmpty()) {
                                    Log.d(TAG, "No more items to process."+hexvalue);
                                    item.settIDNumber(hexvalue);
                                    item.setrFIDCode(item.getItemCode());
                                    item.setProductName(item.getDesignName());
//                                productList.add(j);
                                }
                                if (item.gettIDNumber() != null && !item.gettIDNumber().isEmpty()) {
                                    productResponse1.add(item);
                                    Log.d("##","@@");
                                }

                            }
                        }
                    }
                    processResponse(productResponse1);
                    Log.d("##1","@@1");
                    // Check RecordsCount
                    int recordsCount = productResponse.get(0).getRecordsCount();
                    if (recordsCount <= Integer.parseInt(size)) {
                        Log.d(TAG, "Records count is less than or equal to size. Stopping further API calls.");
                        return; // Exit processing if there are no more records
                    }

                    // Fetch the next batch
                    fetchsingleData(currentId + 1, size, clientCode);
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<AlllabelResponse.LabelItem>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    public String convertToHex(String input) {
        StringBuilder hexBuilder = new StringBuilder();
        for (char ch : input.toCharArray()) {
            hexBuilder.append(String.format("%02X", (int) ch)); // Using uppercase hex format
        }
        return hexBuilder.toString();
    }

    private void fetchPaginatedData(String clientCode) {
        int currentId = 0;
        String size = "500"; // Set your desired page size

        fetchData(currentId, size, clientCode); // Start fetching data
    }

    private void fetchData(int currentId, String size, String clientCode) {
        ProductRequestPayload payload = new ProductRequestPayload(clientCode, "all", currentId, size);
        Log.d("@@1","@@1");
      //  Call<List<AlllabelResponse.LabelItem>> call = apiService.getBatchedLabelledStock(payload);
        ClientCodeRequest getAlllableproducts=new ClientCodeRequest(clientCode);
        //  Call<List<AlllabelResponse.LabelItem>> call = apiService.getBatchedLabelledStock(payload);
        Call<List<AlllabelResponse.LabelItem>> call = apiService.getAlllableproducts(getAlllableproducts);

        call.enqueue(new Callback<List<AlllabelResponse.LabelItem>>() {
            @Override
            public void onResponse(Call<List<AlllabelResponse.LabelItem>> call, Response<List<AlllabelResponse.LabelItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AlllabelResponse.LabelItem> productResponse = response.body();

                    // Check if the response is empty
                    if (productResponse.isEmpty()) {
                        Log.d(TAG, "No more items to process.");
                        return; // Exit the processing
                    }
                    List<AlllabelResponse.LabelItem> productResponse1 = new ArrayList<>();
                    for(AlllabelResponse.LabelItem item : productResponse) {
                        if(item.getStatus().equalsIgnoreCase("active")){
                        productResponse1.add(item);
                        }
                    }

                    processResponse(productResponse1);
                    // Check RecordsCount
                    int recordsCount = productResponse.get(0).getRecordsCount();
                    if (recordsCount <= Integer.parseInt(size)) {
                        Log.d(TAG, "Records count is less than or equal to size. Stopping further API calls.");
                        return; // Exit processing if there are no more records
                    }

                    // Fetch the next batch
                    fetchData(currentId + 1, size, clientCode);
                } else {
                    Log.e(TAG, "Response not successful::: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<AlllabelResponse.LabelItem>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void processResponse(List<AlllabelResponse.LabelItem> response) {
        Log.d(TAG, "Processing Product ID: " + response.size());
//        ml = myapp.getInventoryMap();
        HashMap<String, Itemmodel> ml = new HashMap<>();
        if (myapp.getInventoryMap().size() > 0) {
            for (Map.Entry<String, Itemmodel> entry : myapp.getInventoryMap().entrySet()) {
                Itemmodel m = new Itemmodel(entry.getValue());
                ml.put(m.getTidValue(), m);
            }
        }
        HashMap<String, Itemmodel> nmap = new HashMap<>();
        List<Itemmodel> dmap = new ArrayList<>();


        for (AlllabelResponse.LabelItem p : response) {

            if (!p.getStatus().equalsIgnoreCase("active") && p.getrFIDCode() != null && p.getrFIDCode().isEmpty()) {
                continue;
            }


            if (p.gettIDNumber() == null || p.gettIDNumber().isEmpty()) {
                if (p.getrFIDCode() != null && !p.getrFIDCode().isEmpty()) {
                    String[] barcodeArray = p.getrFIDCode().split(",");
                    for (String barcode : barcodeArray) {
                        barcode = barcode.trim();
                        String tid = "";//findTidByBarcode(rfidList, barcode);
                        if (tid != null && !tid.isEmpty()) {

                            if (ml.containsKey(tid)) {
                                Itemmodel o = ml.get(tid);
                                Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                        o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategoryName(), p.getProductName(), p.getPurityName(),
                                        o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                        o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                        p.gethUIDCode(), p.getImages(), "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrossWt()),
                                        getdvalue(p.getTotalStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMakingPerGram()),
                                        getdvalue(p.getMakingPercentage()), getdvalue(p.getMakingFixedAmt()), getdvalue(p.getMakingFixedWastage()),
                                        getdvalue(p.getTotalStoneAmount()), getdvalue(p.getmRP()), getdvalue(p.getHallmarkAmount()),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done",o.getProductCode(),o.getCounterId(),o.getCounterName(),o.getTotPcs(),o.getTotMPcs(),o.getCategoryId(),o.getProductId(),o.getDesignId(),o.getPurityId());
                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                    nmap.put(item.getTidValue(), item);
                                }
                            } else {
                                Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                        tid, "", getbvalue(p.getBranchName()), p.getCategoryName(), p.getProductName(), p.getPurityName(),
                                        "", "", "", "", "",
                                        "", "", barcode, p.getItemCode(), p.getBoxName(), p.gethUIDCode(),
                                        p.getImages(), "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                        "api add", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrossWt()),
                                        getdvalue(p.getTotalStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMakingPerGram()),
                                        getdvalue(p.getMakingPercentage()), getdvalue(p.getMakingFixedAmt()), getdvalue(p.getMakingFixedWastage()),
                                        getdvalue(p.getTotalStoneAmount()), getdvalue(p.getmRP()), getdvalue(p.getHallmarkAmount()),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done",p.productCode,String.valueOf(p.getCounterId()),p.getCounterName(),0,0,p.getCategoryId(),p.getProductId(),p.getDesignId(),p.getPurityId());
                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                    nmap.put(item.getTidValue(), item);
                                }
                            }

                        } else {
                            //count here
//                            totalissueitem.set(totalissueitem.get() + 1);

                        }
                    }
                }
            } else {
                String[] tidArray = p.gettIDNumber().split(",");
                String[] barcodeArray = p.getrFIDCode().split(",");

                for (int i = 0; i < tidArray.length; i++) {
                    String tid = tidArray[i].trim();
                    String barcode = barcodeArray[i].trim();

                    validTIDs.add(tid);

                    Log.e("checkrfid  ", "1  "+tid);
                    if (ml.containsKey(tid)) {
                        Itemmodel o = ml.get(tid);
                        Log.e("checkrfid ", "2  "+ml.size());
                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategoryName(), p.getProductName(), p.getPurityName(),
                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                p.gethUIDCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                "", "", "", "", "",
                                "", 0, 0, 0, 0, getdvalue(p.getGrossWt()),
                                getdvalue(p.getTotalStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMakingPerGram()),
                                getdvalue(p.getMakingPercentage()), getdvalue(p.getMakingFixedAmt()), getdvalue(p.getMakingFixedWastage()),
                                getdvalue(p.getTotalStoneAmount()), getdvalue(p.getmRP()), getdvalue(p.getHallmarkAmount()),
                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, "done", "done",o.getProductCode(),o.getCounterId(),o.getCounterName(),o.getTotPcs(),o.getTotMPcs(),o.getCategoryId(),o.getProductId(),o.getDesignId(),o.getPurityId());
                        item.setPcs(p.getPieces());
                        item.setPartyCode(p.getImages());
                        if (item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                            Log.e("checkrfid", "3  "+item.getItemCode());
                            nmap.put(item.getTidValue(), item);
                        }
                    } else {
                        Log.e("checkrfid", "4  "+p.getItemCode());
                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                tid, tid, getbvalue(p.getBranchName()), p.getCategoryName(), p.getProductName(), p.getPurityName(),
                                "", "", "", "", "",
                                "", "", barcode, p.getItemCode(), p.getBoxName(), p.gethUIDCode(),
                                "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                "api add", "",
                                "", "", "", "", "",
                                "", 0, 0, 0, 0, getdvalue(p.getGrossWt()),
                                getdvalue(p.getTotalStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMakingPerGram()),
                                getdvalue(p.getMakingPercentage()), getdvalue(p.getMakingFixedAmt()), getdvalue(p.getMakingFixedWastage()),
                                getdvalue(p.getTotalStoneAmount()), getdvalue(p.getmRP()), getdvalue(p.getHallmarkAmount()),
                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0,
                                0, "done", "done",p.getProductCode(),String.valueOf(p.getCounterId()),p.getCounterName(),0,0,p.getCategoryId(),p.getProductId(),p.getDesignId(),p.getPurityId());
                        item.setPcs(p.getPieces());
                        item.setPartyCode(p.getImages());
                        if (item.getTidValue() != null && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                            Log.e("checkrfid", "5  "+item.getItemCode());
                            nmap.put(item.getTidValue(), item);
//                            Log.e("checking all tidvalues", "" + item.toString());
                        }
                    }
                }
            }
        }
        for (String key : ml.keySet()) {
            Log.e("checkdeleted ", "  "+validTIDs);
            // Check if the key exists in umap
            if (!validTIDs.contains(key)) {
                // If the key does not exist in umap, add it to dmap

                dmap.add(ml.get(key));

//                Log.e("checkdeleted ", "  "+ml.get(key).toString());
            }
        }
        List<Itemmodel> itemlist = new ArrayList<>(nmap.values());
        if (!itemlist.isEmpty()) {
            entryDatabase.AutoSync(itemlist, null, myapp, new SaveCallback() {
                @Override
                public void onSaveSuccess() {
                    if (!dmap.isEmpty()) {
                        entryDatabase.deleteItemsInBackground(dmap, myapp);
                    }

                }

                @Override
                public void onSaveFailure(List<Itemmodel> failedItems) {

                }
            });
        }



    }

    private void updatebills(String clientCode) {

        Log.e("API_BILLS", "");
        entryDatabase.updatebillstoweb(clientCode, new ActivationCallback() {
            @Override
            public void onSaveSuccess() {
            }

            @Override
            public void onFailed(String error) {
            }
        });

    }

    private void deleteitems(EntryDatabase entryDatabase, Context activity, List<Itemmodel> dmap, MyApplication app) {
        List<Issuemode> issueitem = new ArrayList<>();
        entryDatabase.makeentry(context, dmap, "delete", "product", app, issueitem, new SaveCallback() {

            @Override
            public void onSaveSuccess() {
//                Toast.makeText(context, "Item updated succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();


            }

            @Override
            public void onSaveFailure(List<Itemmodel> failedItems) {
//                Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getbvalue(String branchName) {
        if (branchName == null || branchName.isEmpty()) {
            return "Home";
        }
        return branchName;
    }

    private double getdvalue(String stoneWeight) {
        if (stoneWeight == null || stoneWeight.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(stoneWeight);
    }
}