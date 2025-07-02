package com.loyalstring.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.Adapters.CommonStockAdapter;
import com.loyalstring.Adapters.InventoryBottomAdaptor;
import com.loyalstring.Apis.ApiManager;
import com.loyalstring.Apis.ApiProcess;
import com.loyalstring.Apis.RetrofitClient;
import com.loyalstring.Excels.InventoryExcelCreation;
import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Item;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.MatchQuantityRequest;
import com.loyalstring.modelclasses.StockVerificationFilter;
import com.loyalstring.modelclasses.StockVerificationFilterModel;
import com.loyalstring.modelclasses.StockVerificationRequestData;
import com.loyalstring.modelclasses.StockVerificationResponseNew;
import com.loyalstring.network.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DailyStockReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommonStockAdapter adapter;
    private EntryDatabase entryDatabase;
    private List<Itemmodel> allItems;
    private Toolbar toolbar;
    Globalcomponents globalcomponents;
    ApiManager apiManager;
    Button emailButton, stockVerificationButton;
    NetworkUtils networkUtils;
    private SharedPreferencesManager sharedPreferencesManager;

    private final Stack<LevelState> levelStack = new Stack<>();
    private Map<String, List<Itemmodel>> groupedMap;
    private TextView tvSelectedDate;

    List<Itemmodel> itemsForDate=new ArrayList<>();

    static class LevelState {
        CommonStockAdapter.LevelType level;
        List<Object> displayList;
        Map<String, List<Itemmodel>> groupedData;

        LevelState(CommonStockAdapter.LevelType level, List<Object> displayList, Map<String, List<Itemmodel>> groupedData) {
            this.level = level;
            this.displayList = displayList;
            this.groupedData = groupedData;
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_stock_report);
        entryDatabase = new EntryDatabase(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        recyclerView = findViewById(R.id.recyclerViewStock);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        globalcomponents = new Globalcomponents();

         emailButton = findViewById(R.id.tv_email);
         stockVerificationButton = findViewById(R.id.tv_stock_verification);

        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Optionally, set the title of the toolbar
        getSupportActionBar().setTitle("Daily Stock Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back_white);

        emailButton.setVisibility(View.GONE);
        stockVerificationButton.setVisibility(View.GONE);
        // After updating date in DB...
        allItems = entryDatabase.getAllItems();
        if (allItems == null || allItems.isEmpty()) {
            Toast.makeText(this, "No stock data found", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<Itemmodel>> dateMap = CommonStockAdapter.groupBy(allItems, "date");
        List<Object> dateList = new ArrayList<>(dateMap.keySet());


        adapter = new CommonStockAdapter(
                this,
                dateList,
                CommonStockAdapter.LevelType.DATE,
                dateMap,
                (level, value) -> {
                    switch (level) {
                        case DATE:
                            emailButton.setVisibility(View.VISIBLE);
                            stockVerificationButton.setVisibility(View.VISIBLE);
                            String selectedDate = (String) value;
                            tvSelectedDate.setVisibility(View.VISIBLE);
                            tvSelectedDate.setText("Date: " + selectedDate);
                            itemsForDate = dateMap.get(selectedDate);
                            showCounters(itemsForDate);
                            break;
                        case COUNTER:
                            emailButton.setVisibility(View.VISIBLE);
                            stockVerificationButton.setVisibility(View.VISIBLE);
                            String selectedCounter = (String) value;
                            List<Itemmodel> itemsForCounter = groupedMap.get(selectedCounter);
                            showCategories(itemsForCounter);
                            break;
                        case CATEGORY:
                            emailButton.setVisibility(View.VISIBLE);
                            stockVerificationButton.setVisibility(View.VISIBLE);
                            String selectedCategory = (String) value;
                            List<Itemmodel> itemsForCategory = groupedMap.get(selectedCategory);
                            showProducts(itemsForCategory);
                            break;
                    }
                }
        );


        recyclerView.setAdapter(adapter);

        // ✅ 4. Now it's safe to call this
        adapter.updateGroupedData(dateMap);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DailyStockReportActivity.this, "clicked", Toast.LENGTH_SHORT).show();

                if (itemsForDate.isEmpty()) {
                    Toast.makeText(DailyStockReportActivity.this, "no item to send email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (areStoragePermissionsGranted()) {
                    boolean folder = globalcomponents.checkfileexist("dailystock");
                    if (folder) {
                        File topmatch = createfile("matcheditems");
                        File topunmatch = createfile("unmatcheditems");
                        File bottommatch = createfile("matcheditemdetails");
                        File botomunmatch = createfile("unmatcheditemdetails");
                        File topall = createfile("allitems");
                        File bottomall = createfile("allitemdetails");
                        File topalll = createfile("allitemsreport");

                        if (topmatch != null && topunmatch != null && bottommatch != null && botomunmatch != null && topall != null && bottomall != null) {

                            ArrayList<Itemmodel> top = new ArrayList<>();
                            ArrayList<Itemmodel> bottom = new ArrayList<>();
                            double totQty = 0;
                            double matchQty = 0;
                            double totGrossWt = 0;
                            double matchGrossWt = 0;
                            double unmatchGrossWt = 0;
                           // top.add(itemsForDate.get(0));
                           /* for (Itemmodel item : itemsForDate) {
                                if(item.getAvlQty()==item.getMatchQty()){
                                    totQty += item.getAvlQty();              // Total available quantity
                                    matchQty += item.getMatchQty();          // Total matched quantity
                                    totGrossWt += item.getGrossWt();         // Total gross weight
                                    matchGrossWt += item.getGrossWt();       // Matched gross weight
                                    unmatchGrossWt += 0;
                                   // top.add(item);
                                    Itemmodel itemmodel=new Itemmodel();
                                    top.add(itemmodel);

                                }

                                bottom.add(item);  // Add item to the bottom list
                            }
*/
// Grouping items by counter
                            Map<String, List<Itemmodel>> counterMap = new LinkedHashMap<>(); // Map to group items by counter

// Group items by counter
                            for (Itemmodel item : itemsForDate) {
                                String counter = item.getCounterName(); // Get the counter of the item
                                if (!counterMap.containsKey(counter)) {
                                    counterMap.put(counter, new ArrayList<>());
                                }
                                counterMap.get(counter).add(item);
                            }

// Iterate through the counters and process items
                            for (Map.Entry<String, List<Itemmodel>> entry : counterMap.entrySet()) {
                                String counter = entry.getKey();
                                List<Itemmodel> itemsInCounter = entry.getValue();

                                // Initialize variables to track totals for matched and unmatched items for each counter
                                double matchedQty = 0, matchedGrossWt = 0;
                                double unmatchedQty = 0, unmatchedGrossWt = 0;

                                // Create a new Itemmodel for the specific counter
                                Itemmodel counterItemModel = new Itemmodel();
                                counterItemModel.setCounterName(counter);  // Set the counter name for this specific counter

                                // Initialize a map to group items by category for this counter
                                Map<String, List<Itemmodel>> categoryMap = new LinkedHashMap<>();

                                // Group items by category within the current counter
                                for (Itemmodel item : itemsInCounter) {
                                    String category = item.getCategory(); // Get the category of the item
                                    if (!categoryMap.containsKey(category)) {
                                        categoryMap.put(category, new ArrayList<>());
                                    }
                                    categoryMap.get(category).add(item);
                                }

                                // Iterate through the categories for the current counter
                                for (Map.Entry<String, List<Itemmodel>> categoryEntry : categoryMap.entrySet()) {
                                    String category = categoryEntry.getKey();
                                    List<Itemmodel> itemsInCategory = categoryEntry.getValue();

                                    // Initialize variables to track totals for matched and unmatched items for this category
                                    double categoryMatchedQty = 0, categoryMatchedGrossWt = 0;
                                    double categoryUnmatchedQty = 0, categoryUnmatchedGrossWt = 0;

                                    // Create a new Itemmodel for the specific category
                                    Itemmodel categoryItemModel = new Itemmodel();
                                    categoryItemModel.setCategory(category);  // Set the category name for this specific category
                                    categoryItemModel.setCounterName(counter); // Set the counter name for this category

                                    // Initialize a map to group items by product for this category
                                    Map<String, List<Itemmodel>> productMap = new LinkedHashMap<>();

                                    // Group items by product within the current category
                                    for (Itemmodel item : itemsInCategory) {
                                        String product = item.getProduct(); // Get the product of the item
                                        if (!productMap.containsKey(product)) {
                                            productMap.put(product, new ArrayList<>());
                                        }
                                        productMap.get(product).add(item);
                                    }

                                    // Iterate through the products for the current category
                                    for (Map.Entry<String, List<Itemmodel>> productEntry : productMap.entrySet()) {
                                        String product = productEntry.getKey();
                                        List<Itemmodel> itemsInProduct = productEntry.getValue();

                                        // Initialize variables to track totals for matched and unmatched items for this product
                                        double productMatchedQty = 0, productMatchedGrossWt = 0;
                                        double productUnmatchedQty = 0, productUnmatchedGrossWt = 0;

                                        // Create a new Itemmodel for the specific product
                                        Itemmodel productItemModel = new Itemmodel();
                                        productItemModel.setProduct(product);  // Set the product name for this specific product
                                        productItemModel.setCategory(category); // Set the category for this product
                                        productItemModel.setCounterName(counter); // Set the counter for this product

                                        // Process each item for the current product
                                        for (Itemmodel item : itemsInProduct) {
                                            // Check if the item meets the condition (avlQty == matchQty)
                                            if (item.getAvlQty() == item.getMatchQty()) {
                                                // Accumulate total quantities and weights for matched items
                                                productMatchedQty += item.getAvlQty();
                                                productMatchedGrossWt += item.getGrossWt();
                                            } else {
                                                // Accumulate total quantities and weights for unmatched items
                                                productUnmatchedQty += item.getAvlQty();
                                                productUnmatchedGrossWt += item.getGrossWt();
                                            }

                                            // Always add the individual item to the bottom list
                                            bottom.add(item);  // Add item to the bottom list
                                        }

                                        // Set the total matched and unmatched values for this specific product
                                        productItemModel.setMatchQty(productMatchedQty);          // Set the matched quantity for this product
                                        productItemModel.setAvlQty(productMatchedQty + productUnmatchedQty);  // Set the total available quantity
                                        productItemModel.setTotalGwt(productMatchedGrossWt + productUnmatchedGrossWt);  // Set the total gross weight
                                        productItemModel.setMatchGwt(productMatchedGrossWt);      // Set the matched gross weight
                                        productItemModel.setTotUnmatchGrswt(productUnmatchedGrossWt);  // Set the unmatched gross weight
                                        productItemModel.setTotUnMatchQty(productUnmatchedQty);   // Set the unmatched quantity for this product

                                        // Add the Itemmodel for this product to the top list
                                        top.add(productItemModel);
                                    }

                                    // Set the total matched and unmatched values for this specific category
                                    categoryItemModel.setMatchQty(categoryMatchedQty);          // Set the matched quantity for this category
                                    categoryItemModel.setAvlQty(categoryMatchedQty + categoryUnmatchedQty);  // Set the total available quantity
                                    categoryItemModel.setTotalGwt(categoryMatchedGrossWt + categoryUnmatchedGrossWt);  // Set the total gross weight
                                    categoryItemModel.setMatchGwt(categoryMatchedGrossWt);      // Set the matched gross weight
                                    categoryItemModel.setTotUnmatchGrswt(categoryUnmatchedGrossWt);  // Set the unmatched gross weight
                                    categoryItemModel.setTotUnMatchQty(categoryUnmatchedQty);   // Set the unmatched quantity for this category

                                    // Add the Itemmodel for this category to the top list
                                    top.add(categoryItemModel);
                                }

                                // Set the total matched and unmatched values for this specific counter
                                counterItemModel.setMatchQty(matchedQty);          // Set the matched quantity for this counter
                                counterItemModel.setAvlQty(matchedQty + unmatchedQty);  // Set the total available quantity
                                counterItemModel.setTotalGwt(matchedGrossWt + unmatchedGrossWt);  // Set the total gross weight
                                counterItemModel.setMatchGwt(matchedGrossWt);      // Set the matched gross weight
                                counterItemModel.setTotUnmatchGrswt(unmatchedGrossWt);  // Set the unmatched gross weight
                                counterItemModel.setTotUnMatchQty(unmatchedQty);   // Set the unmatched quantity for this counter

                                // Add the Itemmodel for this counter to the top list
                                top.add(counterItemModel);
                            }



                            HashMap<String, ArrayList<Itemmodel>> excelmap = new HashMap<>();
                            excelmap.put(topmatch.getAbsolutePath(), top);
                            excelmap.put(topunmatch.getAbsolutePath(), top);
                            excelmap.put(bottommatch.getAbsolutePath(), bottom);
                            excelmap.put(botomunmatch.getAbsolutePath(), bottom);
                            excelmap.put(topall.getAbsolutePath(), top);
                            excelmap.put(topalll.getAbsolutePath(), top);
                            excelmap.put(bottomall.getAbsolutePath(), bottom);

                            InventoryBottomAdaptor inventoryBottomAdaptor=null;

                            InventoryExcelCreation excelTask = new InventoryExcelCreation(top, bottom, "allitem", "itemdetails", DailyStockReportActivity.this, "dailystock", "scan", excelmap, inventoryBottomAdaptor);
                            excelTask.execute();


                        } else {
                            Toast.makeText(DailyStockReportActivity.this, "failed to create file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ArrayList<String> folders = new ArrayList<>();
                        folders.add("dailystock");
                        boolean f = globalcomponents.createFolders(folders);
                        if (!f) {
                            Toast.makeText(DailyStockReportActivity.this, "failed to create file", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DailyStockReportActivity.this, "created file please click again", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(DailyStockReportActivity.this, "File read permission required please restart app", Toast.LENGTH_SHORT).show();
//                    requestStoragePermissions();
                }

            }
        });



        stockVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // topmatch.clear();
                // bottommatch.clear();

                String categoty = "";
                String product = "";
                String counterName = "";
                Clients clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();
                String clientCode = clients.getClientCode();
               /* for (Map.Entry<String, Itemmodel> entry : bottommap.entrySet()) {
                    String key = entry.getKey();
                    Itemmodel item = entry.getValue();
                    categoty=item.getCategory();
                    product=item.getProduct();
                    counterName=item.getCounterName();
                    if (item.getAvlQty() == item.getMatchQty()) {
                        topmatch.put(key, item);
                    }
                }
                for (Map.Entry<String, Itemmodel> entry : bottommap.entrySet()) {
                    String key = entry.getKey();
                    Itemmodel item = entry.getValue();
                    categoty=item.getCategory();
                    product=item.getProduct();
                    if (item.getAvlQty() == item.getMatchQty()) {
                        bottommatch.put(key, item);
                    }
                }*/
                List<MatchQuantityRequest> matchQuantityRequestList = new ArrayList<>();


                int categoryId = 0;
                int productId = 0;
                int designId = 0;
                int purityId = 0;
                int counterId = 0;


                for (int i = 0; i < itemsForDate.size(); i++) {
                    Log.d("@@ category", "category" + itemsForDate.get(i).getCategory());
                    Log.d("@@ category", "category categoty" + itemsForDate);
                    if (itemsForDate.get(i).getCategory().equalsIgnoreCase(categoty)) {
                        categoryId = itemsForDate.get(i).getCategoryId();
                        break;
                    }
                }

                for (int i1 = 0; i1 < itemsForDate.size(); i1++) {
                    if (itemsForDate.get(i1).getProduct().equalsIgnoreCase(product)) {
                        productId = itemsForDate.get(i1).getProductId();
                        break;
                    }
                }

                for (int i2 = 0; i2 < itemsForDate.size(); i2++) {

                    if (itemsForDate.get(i2).getCounterName().equalsIgnoreCase(counterName)) {
                        counterId = Integer.parseInt(itemsForDate.get(i2).getCounterId());

                    }

                    designId = 0;
                    purityId = 0;


                }

                Log.d("@@ categoryId", "categoryId" + categoryId);
                Log.d("@@ productId", "productId" + productId);
                Log.d("@@ categoryId", "categoryId" + purityId);
                Log.d("@@ categoryId", "categoryId" + designId);
                Log.d("@@ categoryId", "categoryId" + counterId);


                StockVerificationRequestData stockVerificationRequestData = new StockVerificationRequestData();
                stockVerificationRequestData.setClientCode(clientCode);


                StockVerificationFilterModel stockVerificationFilterModel = new StockVerificationFilterModel();
                StockVerificationFilter stockVerificationFilter = new StockVerificationFilter();
                stockVerificationFilter.setId(0);
                stockVerificationFilter.setCreatedOn("");
                stockVerificationFilter.setLastUpdated("");
                stockVerificationFilter.setStatusType(true);
                stockVerificationFilter.setClientCode(clientCode);
                stockVerificationFilter.setCounterId(counterId);
                stockVerificationFilter.setCategoryId(categoryId);
                stockVerificationFilter.setProductId(productId);
                stockVerificationFilter.setDesignId(designId);
                stockVerificationFilter.setPurityId(purityId);
                stockVerificationFilter.setGrossWeight("string");
                stockVerificationFilter.setNetWeight("string");
                stockVerificationFilter.setQuantity("string");
                stockVerificationFilter.setItemCode("string");


                MatchQuantityRequest matchQuantityRequest = new MatchQuantityRequest();

                List<String> itemCodes = new ArrayList<>();

                List<Item> items = new ArrayList<>(); // ✅ Initialize once outside the loop

                for (int i = 0; i < itemsForDate.size(); i++) {
                    //   Log.d("@@ itemcodeSize", "@@" + bottommap.size());

                    Itemmodel item = itemsForDate.get(i);
                    Item itemData = new Item(); // ✅ Create a new item each loop

                    itemData.setBranchId(0);
                    itemData.setBranchName(item.getBranch());

                    itemData.setCounterId(Integer.valueOf(item.getCounterId()));
                    itemData.setCounterName(item.getCounterName());
                    itemData.setCategoryId(item.getCategoryId());
                    itemData.setCategoryName(item.getCategory());
                    itemData.setProductId(item.getProductId());
                    itemData.setProductName(item.getProduct());
                    itemData.setPurityId(0);
                    itemData.setPurityName(item.getPurity());
                    itemData.setDesignId(item.getDesignId());
                    itemData.setDesignName("");
                    itemData.setCompanyId(0);
                    itemData.setCompanyName("");
                    itemData.setGrossWeight(0);
                    itemData.setNetWeight(0);
                    itemData.setQuantity(0);

                    itemData.setItemCode(item.getItemCode());
                    itemCodes.add(item.getItemCode());

                    if (item.getAvlQty() == item.getMatchQty()) {
                        item.setInventoryStatus("match");
                        itemData.setStatus("match");
                        Log.d("@@ itemcodeData", "@@" + item.getInventoryStatus());
                    } else {
                        item.setInventoryStatus("unmatch");
                        itemData.setStatus("unmatch");
                        Log.d("@@ itemcodeinavctive", "@@" + item.getInventoryStatus());
                    }

                    items.add(itemData); // ✅ Add to list
                }


                stockVerificationRequestData.setItems(items);
                // stockVerificationFilterModel.setStockVerificationFilter(stockVerificationFilter);
                //stockVerificationFilterModel.setMatchQuantityRequest(matchQuantityRequest);


                networkUtils = new NetworkUtils(DailyStockReportActivity.this);


                networkUtils = new NetworkUtils(DailyStockReportActivity.this);

                ApiProcess apiprocess = new ApiProcess();
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                apiManager = new ApiManager(apiService);
                if (networkUtils.isNetworkAvailable()) {
                    apiManager.stockVarificationDataDataNew(stockVerificationRequestData, new interfaces.FetchAllVerificxationDataNew() {
                        @Override
                        public void onSuccess(StockVerificationResponseNew result) {
                            // if (!result=null) {
                         /*   Activity activity = getActivity();
                            if (activity != null) {
                                activity.runOnUiThread(() -> {
                                    new AlertDialog.Builder(activity)
                                            .setTitle("Success")
                                            .setMessage("Stock status has be updated to the server")
                                            .setPositiveButton("OK", null)
                                            .show();
                                });
                            }*/
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(DailyStockReportActivity.this)
                                            .setTitle("Success")
                                            .setMessage("Stock status has be updated to the server")
                                            .setPositiveButton("OK", null)
                                            .show();
                                    //  entryDatabase.makerfidentry(getActivity(), app, result);
                                    // rfidList.addAll(result);
                                    Log.e("RfidListCheck", "Rfid Scanned data: " + result);
                                }
                            });
                        }


                        @Override
                        public void onError(Exception e) {
                           /*// Activity activity = getActivity();
                            //if (activity != null) {
                              //  activity.runOnUiThread(() -> {
                                    new AlertDialog.Builder(getApplicationContext())
                                            .setTitle("Error")
                                            .setMessage("Stock status has not uploaded. something went wrong")
                                            .setPositiveButton("OK", null)
                                            .show();
                              //  });*/
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(DailyStockReportActivity.this)
                                            .setTitle("Error")
                                            .setMessage("Stock status has not uploaded. something went wrong")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            });

                        }
                        // }
                    });
                }


            }


//

        });
    }

    private boolean areStoragePermissionsGranted() {
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private File createfile(String fname) {
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 and above
            try {
                file = File.createTempFile(fname, ".xlsx", getFilesDir());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in internal storage: " + e.getMessage());
                return null;
            }
        } else { // Android versions below 10
            try {
                file = File.createTempFile(fname, ".xlsx", Environment.getExternalStorageDirectory());
                return file;
            } catch (IOException e) {
                Log.e("TAG", "Error creating temporary file in external storage: " + e.getMessage());
                return null;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle the toolbar's back button (Home button)
        if (id == android.R.id.home) {
            onBackPressed();  // Trigger back press when the home button is clicked
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDates(List<Itemmodel> items) {
        Map<String, List<Itemmodel>> dateMap = CommonStockAdapter.groupBy(items, "date");
        List<Object> dateList = new ArrayList<>(dateMap.keySet());

        adapter = new CommonStockAdapter(this, dateList, CommonStockAdapter.LevelType.DATE, dateMap, (level, value) -> {
            String selectedDate = (String) value;
            List<Itemmodel> itemsForDate = dateMap.get(selectedDate);
            levelStack.push(new LevelState(CommonStockAdapter.LevelType.DATE, dateList, dateMap));
            showCounters(itemsForDate);
        });

        recyclerView.setAdapter(adapter);
    }

    private void showCounters(List<Itemmodel> itemsForDate) {
        levelStack.push(new LevelState(CommonStockAdapter.LevelType.DATE, new ArrayList<>(adapter.getDisplayList()), adapter.getGroupedData()));

        groupedMap = CommonStockAdapter.groupBy(itemsForDate, "counter");
        List<Object> counterList = new ArrayList<>(groupedMap.keySet());

        adapter.updateGroupedData(groupedMap);
        adapter.updateList(counterList, CommonStockAdapter.LevelType.COUNTER, groupedMap);
    }

    private void showCategories(List<Itemmodel> itemsForCounter) {
        levelStack.push(new LevelState(CommonStockAdapter.LevelType.COUNTER, new ArrayList<>(adapter.getDisplayList()), adapter.getGroupedData()));

        groupedMap = CommonStockAdapter.groupBy(itemsForCounter, "category");
        List<Object> categoryList = new ArrayList<>(groupedMap.keySet());

        adapter.updateGroupedData(groupedMap);
        adapter.updateList(categoryList, CommonStockAdapter.LevelType.CATEGORY, groupedMap);
    }

   /* private void showProducts(List<Itemmodel> itemsForCategory) {
        // ✅ Push current category state before navigating to products
        levelStack.push(new LevelState(adapter.getCurrentLevel(), adapter.getDisplayList(), adapter.getGroupedData()));

        adapter.updateGroupedData(null); // No grouped data at product level
        adapter.updateList(new ArrayList<>(itemsForCategory), CommonStockAdapter.LevelType.PRODUCT, null);
    }*/
/*   private void showProducts(List<Itemmodel> itemsForCategory) {
       // ✅ Push current category state before navigating to products
       levelStack.push(new LevelState(CommonStockAdapter.LevelType.CATEGORY, new ArrayList<>(adapter.getDisplayList()), adapter.getGroupedData()));

       // Group items by product
       groupedMap = CommonStockAdapter.groupBy(itemsForCategory, "product");

       // Create a list of product keys
       List<Object> productList = new ArrayList<>(groupedMap.keySet());

       // Update the grouped data in the adapter (groupedMap contains the grouped data by product)
       adapter.updateGroupedData(groupedMap);

       // Update the list in the adapter to display products
       adapter.updateList(productList, CommonStockAdapter.LevelType.PRODUCT, groupedMap);
   }*/
   private void showProducts(List<Itemmodel> itemsForCategory) {
       // ✅ Push current category state before navigating to products
       levelStack.push(new LevelState(CommonStockAdapter.LevelType.CATEGORY, new ArrayList<>(adapter.getDisplayList()), adapter.getGroupedData()));

       // Group items by product (you will aggregate items with the same product name)
       Map<String, List<Itemmodel>> tempGroupedMap = new LinkedHashMap<>(); // Temporary map to store aggregated data

       // Iterate over the items and group them by product name
       for (Itemmodel item : itemsForCategory) {
           String productName = item.getProduct();

           // If the product already exists in the map, we add the item to the existing list and aggregate the quantities
           if (tempGroupedMap.containsKey(productName)) {
               List<Itemmodel> existingItems = tempGroupedMap.get(productName);
               // Aggregate the quantities and update the existing item data
               Itemmodel existingItem = existingItems.get(0);  // Assuming all items of the same product have the same details (like product name, category, etc.)

               // Aggregate fields (quantity, weight, match quantity, etc.)
               existingItem.setAvlQty(existingItem.getAvlQty() + item.getAvlQty()); // Aggregate the available quantity
               existingItem.setMatchQty(existingItem.getMatchQty() + item.getMatchQty()); // Aggregate the match quantity
               existingItem.setGrossWt(existingItem.getGrossWt() + item.getGrossWt()); // Aggregate the gross weight
               existingItem.setMatchGwt(existingItem.getMatchGwt() + item.getMatchGwt()); // Aggregate the matched gross weight

               // You can add more fields to aggregate if needed, based on your data structure.
           } else {
               // If the product is not in the map, add it to the map
               tempGroupedMap.put(productName, new ArrayList<>(List.of(item)));
           }
       }

       // Now the items are grouped by product and aggregated. We will update the adapter.
       groupedMap = tempGroupedMap;

       // Create a list of product keys
       List<Object> productList = new ArrayList<>(groupedMap.keySet());

       // Update the grouped data in the adapter (groupedMap contains the grouped data by product)
       adapter.updateGroupedData(groupedMap);

       // Update the list in the adapter to display products
       adapter.updateList(productList, CommonStockAdapter.LevelType.PRODUCT, groupedMap);
   }



    @Override
    public void onBackPressed() {
        if (!levelStack.isEmpty()) {
            LevelState previous = levelStack.pop();

            // Restore groupedMap in case of counter/category click
            groupedMap = previous.groupedData;

            // Restore adapter data
            adapter.updateList(previous.displayList, previous.level, previous.groupedData);

            // Update selected date visibility
            if (previous.level == CommonStockAdapter.LevelType.DATE) {
                tvSelectedDate.setVisibility(View.GONE);
                emailButton.setVisibility(View.GONE);
                stockVerificationButton.setVisibility(View.GONE);
            } else if (previous.level == CommonStockAdapter.LevelType.COUNTER) {
                emailButton.setVisibility(View.VISIBLE);
                stockVerificationButton.setVisibility(View.VISIBLE);
                // Re-show the selected date
                // Try to extract a date from any one item in groupedMap
                if (groupedMap != null && !groupedMap.isEmpty()) {
                    List<Itemmodel> anyList = groupedMap.values().iterator().next();
                    if (!anyList.isEmpty()) {
                        long entryDate = anyList.get(0).getEntryDate();
                        String dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH).format(new java.util.Date(entryDate));
                        tvSelectedDate.setVisibility(View.VISIBLE);
                        tvSelectedDate.setText("Date: " + dateStr);
                    }
                }
            }

        } else {
            super.onBackPressed();
        }
    }

}
