package com.loyalstring.Apis;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.apiresponse.ClientCodeRequest;
import com.loyalstring.apiresponse.ProductResponse;
import com.loyalstring.apiresponse.ProductUpdate;
import com.loyalstring.apiresponse.ProductUpdateResponse;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.SaveCallback;
import com.loyalstring.modelclasses.Issuemode;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.Productmodel;
import com.loyalstring.transactionhelper.TransactionIDGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiProcess {

    List<Issuemode> issueitem = new ArrayList<>();

    List<AlllabelResponse.LabelItem> productList = new ArrayList<>();


    Long ot = System.currentTimeMillis();
    String Issueid = "SY" + String.valueOf(ot);

//    private String convertToHex(String input) {
//        StringBuilder hexBuilder = new StringBuilder();
//        for (char ch : input.toCharArray()) {
//            hexBuilder.append(String.format("%02x", (int) ch));
//        }
//        return hexBuilder.toString();
//    }

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
           // hexBuilder.append("00");// add at end
        }

        return hexBuilder.toString();
    }


    public void getproductsn(HashMap<String, Itemmodel> ml, Context activity, String baseUrl, List<Rfidresponse.ItemModel> rfidurl, EntryDatabase entryDatabase, MyApplication app, String rfidType) {

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading data from api");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(activity);
        Clients client = sharedPreferencesManager.readLoginData().getEmployee().getClients();


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .addInterceptor(loggingInterceptor)
//                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
                .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
                .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
//
        String code = client.getClientCode();
//
        ClientCodeRequest clientCodeRequest = new ClientCodeRequest(code);
//
        Call<List<AlllabelResponse.LabelItem>> call2 = apiService.getAlllableproducts(clientCodeRequest);
        AtomicInteger totalissueitem = new AtomicInteger();

        List<Rfidresponse.ItemModel> rfidList = new ArrayList<>();


//        rfidList.addAll(rfidurl);

        CountDownLatch latch = new CountDownLatch(2); // Count down for both API calls

        if (rfidurl == null || rfidurl.isEmpty()) {
//            latch.countDown();

//        } else {
            Retrofit retrofit1 = new Retrofit.Builder()
                   // .baseUrl("https://testing.loyalstring.co.in/")
                    .baseUrl("https://rrgold.loyalstring.co.in/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService1 = retrofit1.create(ApiService.class);


            Call<List<Rfidresponse.ItemModel>> call1 = apiService1.getRfiddata(clientCodeRequest);
            call1.enqueue(new Callback<List<Rfidresponse.ItemModel>>() {
                @Override
                public void onResponse(Call<List<Rfidresponse.ItemModel>> call, Response<List<Rfidresponse.ItemModel>> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Rfidresponse.ItemModel> apiResponse = response.body();
                            // Assuming 'Success' status check is not required here since it's a list
                            Log.e("check1allitemstid", "" +apiResponse);
                            rfidList.addAll(apiResponse);
                        } else {
                            Toast.makeText(activity, "Failed to load RFID data: Response not successful", Toast.LENGTH_SHORT).show();
                            Log.e("RFID Response Error", "Response Code: " + response.code() + ", Message: " + response.message());
                        }
                    } catch (Exception e) {
                        Log.e("Exception", "Error while processing RFID response", e);
                        Toast.makeText(activity, "An error occurred while loading RFID data.", Toast.LENGTH_SHORT).show();
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void onFailure(Call<List<Rfidresponse.ItemModel>> call, Throwable t) {
                    Log.e("RFID API Failure", "Error Message: " + t.getMessage(), t);
                    Toast.makeText(activity, "Failed to load RFID data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    latch.countDown();
                }
            });

//            call1.enqueue(new Callback<Rfidresponse>() {
//                @Override
//                public void onResponse(Call<Rfidresponse> call, Response<Rfidresponse> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        Rfidresponse apiResponse = response.body();
//                        if ("Success".equals(apiResponse.getStatus())) {
//                            rfidList.addAll(apiResponse.getData());
//                        } else {
//                            Toast.makeText(activity, "Failed to load RFID data", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(activity, "RFID response was not successful"+response, Toast.LENGTH_SHORT).show();
//                        Log.e("check failure", " "+response);
//                    }
//                    latch.countDown();
//                }
//
//                @Override
//                public void onFailure(Call<Rfidresponse> call, Throwable t) {
//                    Toast.makeText(activity, "Failed to load RFID data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    latch.countDown();
//                }
//            });
        }

        if (rfidType.toLowerCase().contains("webreusable") || rfidType.toLowerCase().contains("reusable")) {


            call2.enqueue(new Callback<List<AlllabelResponse.LabelItem>>() {
                @Override
                public void onResponse(Call<List<AlllabelResponse.LabelItem>> call, Response<List<AlllabelResponse.LabelItem>> response) {
                    dialog.dismiss();

                   // Log.e("checking response ", "product response " + response.body().size());
                    if (response.isSuccessful() && response.body() != null) {
                        List<AlllabelResponse.LabelItem> i = response.body();

//                        for (int j = 0; j < i.size(); j++) {
//                            AlllabelResponse.LabelItem it = i.get(j);
//                            String item = it.getItemCode();
//                            if (it.getStatus().equalsIgnoreCase("active")) {
//                                if (item != null && !item.isEmpty()) {
//                                    String hexvalue = convertToHex(item);
//                                    if (hexvalue != null && !hexvalue.isEmpty()) {
//                                        it.settIDNumber(hexvalue);
//                                        it.setrFIDCode(item);
//                                        it.setProductName(it.getDesignName());
////                                productList.add(j);
//                                    }
//                                    if (it.gettIDNumber() != null && !it.gettIDNumber().isEmpty()) {
//                                        productList.add(it);
//                                    }
//
//                                }
//                            }
//
//                        }
                        for (int j = 0; j < i.size(); j++) {
                            AlllabelResponse.LabelItem it = i.get(j);
                            if (it.getStatus().equalsIgnoreCase("active") || it.getStatus().equalsIgnoreCase("ApiActive")) {


                                productList.add(it);

                            }
                        }

//                    productList.addAll(i);
                    } else {
                        Toast.makeText(activity, "Product response was not successful", Toast.LENGTH_SHORT).show();
                    }
                    latch.countDown();
                }

                @Override
                public void onFailure(Call<List<AlllabelResponse.LabelItem>> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("check data", "labelstock  " + t.getMessage());
                    Toast.makeText(activity, "Failed to load product data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    latch.countDown();
                }
            });
        }
        else if (rfidType.toLowerCase().contains("custom")) {
            if (rfidType.toLowerCase().contains("reusable")) {
                Retrofit retrofitn = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiServicen = retrofitn.create(ApiService.class);
                Call<ProductResponse> call2n = apiServicen.getAllProducts();
                call2n.enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

//                        productList.addAll(response.body().getData());

                            List<Productmodel> items = response.body().getData();
                            for(Productmodel item : items){
                                AlllabelResponse.LabelItem i = new AlllabelResponse.LabelItem();
                                i.setCategoryName(item.getCategory_Name());
                                i.setProductName(item.getProductName());
                                i.setPieces(item.getPieces());
                                i.setPurityName(item.getPurity());
                                i.setrFIDCode(item.getBarcodeNumber());
                                i.setItemCode(item.getItemCode());
                                i.setGrossWt(item.getGrosswt());
                                i.setTotalStoneWeight(item.getStoneWeight());
                                i.setNetWt(item.getNetWt());

                                productList.add(i);
                            }





                            Log.e("check navrang  ", " " + response.body().getData().size());






                        } else {
                            Toast.makeText(activity, "Product response was not successful", Toast.LENGTH_SHORT).show();
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(activity, "Failed to load product data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        latch.countDown();
                    }
                });
            }
            else {
                Retrofit retrofitn = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiServicen = retrofitn.create(ApiService.class);
                Call<ProductResponse> call2n = apiServicen.getAllProducts();
                call2n.enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

//                        productList.addAll(response.body().getData());

                            List<Productmodel> items = response.body().getData();
                            for(Productmodel item : items){
                                AlllabelResponse.LabelItem i = new AlllabelResponse.LabelItem();
                                i.setCategoryName(item.getCategory_Name());
                                i.setProductName(item.getItemType());
                                i.setPieces(item.getPieces());
                                i.setPurityName(item.getPurity());
                                i.setrFIDCode(item.getBarcodeNumber());
                                i.setItemCode(item.getItemCode());
                                i.setGrossWt(item.getGrosswt());
                                i.setTotalStoneWeight(item.getStoneWeight());
                                i.setNetWt(item.getNetWt());
                                i.setPieces(item.getPcs());
                                i.setImages(item.getImageurl());
                                productList.add(i);
                            }

                            Log.e("check navrang  ", " " + response.body().getData().size());


                        } else {
                            Toast.makeText(activity, "Product response was not successful", Toast.LENGTH_SHORT).show();
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(activity, "Failed to load product data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        latch.countDown();
                    }
                });
            }

        }   else if(rfidType.equalsIgnoreCase("websingle")){
            call2.enqueue(new Callback<List<AlllabelResponse.LabelItem>>() {
                @Override
                public void onResponse(Call<List<AlllabelResponse.LabelItem>> call, Response<List<AlllabelResponse.LabelItem>> response) {
                    dialog.dismiss();
                    Log.e("checking response ", "product response " + response);
                    if (response.isSuccessful() && response.body() != null) {
                        List<AlllabelResponse.LabelItem> i = response.body();

                        for (int j = 0; j < i.size(); j++) {
                            AlllabelResponse.LabelItem it = i.get(j);
                            String item = it.getItemCode();
                            if (it.getStatus().equalsIgnoreCase("active") || it.getStatus().equalsIgnoreCase("ApiActive")) {
                                if (item != null && !item.isEmpty()) {
                                    String hexvalue = convertToHex(item);
                                    Log.d("@@","## hexvalue"+hexvalue);
                                    if (hexvalue != null && !hexvalue.isEmpty()) {
                                        it.settIDNumber(hexvalue);
                                        it.setrFIDCode(item);
                                        it.setProductName(it.getDesignName());
                                        Log.d("@@","## vasannti"+it.gettIDNumber());

//                                productList.add(j);
                                    }
                                    if (it.gettIDNumber() != null && !it.gettIDNumber().isEmpty()) {
                                        productList.add(it);
                                        Log.d("@@","## added"+it.gettIDNumber());

                                    }

                                }
                            }

                        }


//                    for(int j =0; j<i.size(); j++){
//                        AlllabelResponse.LabelItem it = i.get(j);
//                        if(it.getStatus().equalsIgnoreCase("active")){
//                            productList.add(it);
//                        }
//                    }

//                    productList.addAll(i);
                    } else {
                        Toast.makeText(activity, "Product response was not successful", Toast.LENGTH_SHORT).show();
                    }
                    latch.countDown();
                }

                @Override
                public void onFailure(Call<List<AlllabelResponse.LabelItem>> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("check data", "labelstock  " + t.getMessage());
                    Toast.makeText(activity, "Failed to load product data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    latch.countDown();
                }
            });
        }
        else {
            call2.enqueue(new Callback<List<AlllabelResponse.LabelItem>>() {
                @Override
                public void onResponse(Call<List<AlllabelResponse.LabelItem>> call, Response<List<AlllabelResponse.LabelItem>> response) {
                    dialog.dismiss();
                    Log.e("checking response ", "product response " + response);
                    if (response.isSuccessful() && response.body() != null) {
                        List<AlllabelResponse.LabelItem> i = response.body();

                        for (int j = 0; j < i.size(); j++) {
                            AlllabelResponse.LabelItem it = i.get(j);
                            String item = it.getItemCode();
                            if (it.getStatus().equalsIgnoreCase("active") || it.getStatus().equalsIgnoreCase("ApiActive")) {
                                if (item != null && !item.isEmpty()) {
                                    String hexvalue = convertToHex(item);
                                    if (hexvalue != null && !hexvalue.isEmpty()) {
                                        it.settIDNumber(hexvalue);
                                        it.setrFIDCode(item);
                                        it.setProductName(it.getDesignName());
//                                productList.add(j);
                                    }
                                    if (it.gettIDNumber() != null && !it.gettIDNumber().isEmpty()) {
                                        productList.add(it);
                                    }

                                }
                            }

                        }
//                    for(int j =0; j<i.size(); j++){
//                        AlllabelResponse.LabelItem it = i.get(j);
//                        if(it.getStatus().equalsIgnoreCase("active")){
//                            productList.add(it);
//                        }
//                    }

//                    productList.addAll(i);
                    } else {
                        Toast.makeText(activity, "Product response was not successful", Toast.LENGTH_SHORT).show();
                    }
                    latch.countDown();
                }

                @Override
                public void onFailure(Call<List<AlllabelResponse.LabelItem>> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("check data", "labelstock  " + t.getMessage());
                    Toast.makeText(activity, "Failed to load product data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    latch.countDown();
                }
            });
        }




        /*//jjj code
        String URL = "https://jjj.panel.jewelmarts.in/callback/Inventory_stock";
        MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

        // Create form data
        RequestBody formBody = new FormBody.Builder()
                .add("username", "RFID")
                .add("password", "Rg^%6mkj676G%$)jhAZ")
                .build();

        // Create the request
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URL)
                .post(formBody)
                .build();

        // Execute the request
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Failed to load inventory stock data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                latch.countDown();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                Toast.makeText(activity, "okay", Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Handle the response data here

                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        int ack = jsonResponse.getInt("ack");
                        if (ack == 1) {
                            String message = jsonResponse.getString("msg");

                            JSONArray productDataArray = jsonResponse.getJSONArray("product_data");

                            // List to store products
//                            List<AlllabelResponse.LabelItem> productList = new ArrayList<>();

                            // Iterate through the array
                            for (int i = 0; i < productDataArray.length(); i++) {
                                JSONObject productObject = productDataArray.getJSONObject(i);

                                String id = productObject.getString("id");
                                String cat = productObject.getString("product_type");
                                String pro = productObject.getString("category_name");
                                String manufacturingCode = productObject.getString("manufacturing_code");
                                String rfidCode = productObject.getString("rfid_code");
                                String grossWt = productObject.getString("gross_wt");
                                String netWt = productObject.getString("net_wt");
                                String tAmt = productObject.getString("t_amt");
                                String imageName = productObject.getString("image_name");

                                // Create a Product object and add it to the list
                                AlllabelResponse.LabelItem product = new AlllabelResponse.LabelItem();
                                product.setCategoryName(cat);
                                product.setProductName(pro);
                                product.setrFIDCode(rfidCode);
                                product.setItemCode(manufacturingCode);
                                product.setGrossWt(grossWt);
                                product.setTotalStoneWeight("0");
                                product.setNetWt(netWt);
                                product.setImages(imageName);


                                productList.add(product);
                            }


                            // Now productList contains all the extracted product data
                            // You can now use productList as needed in your app
                            Log.d("Product List Size", "Number of products: " + productList.size());
                        } else {
                            Log.e("Inventory Stock Error", "Acknowledgment failed");
//                            latch.countDown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        latch.countDown();
                        Log.e("Inventory Stock Error", "Failed to parse JSON: " + e.getMessage());
                    }finally {
                        latch.countDown(); // Ensure latch is counted down regardless of success or failure
                    }



//                    Log.e("checking data ", responseData);
                } else {
                    Log.e("Inventory Stock Error", "Error code: " + response.code());
                    latch.countDown();
                }
//                latch.countDown();
            }
        });*/


        List<Itemmodel> dmap = new ArrayList<>();

        new Thread(() -> {
            try {
                latch.await(); // Wait for both API calls to finish

                // Process data here
                HashMap<String, Itemmodel> nmap = new HashMap<>();

                //Log.e("check1allitemsat", "item check " + productList.get(0).toString());
                for (AlllabelResponse.LabelItem p : productList) {


                    if (p.gettIDNumber() == null || p.gettIDNumber().isEmpty()) {

                        if (p.getrFIDCode() != null && !p.getrFIDCode().isEmpty()) {
                            String[] barcodeArray = p.getrFIDCode().split(",");


                            for (String barcode : barcodeArray) {
                                barcode = barcode.trim();
                                String tid = findTidByBarcode(rfidList, barcode);
//                                Log.e("check1allitems", "" + tid+"  "+barcode);
                                if (tid != null) {

                                    if (ml.containsKey(tid)) {
                                        Itemmodel o = ml.get(tid);
                                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategoryName(), p.getProductName(), p.getPurityName(),
                                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                                p.gethUIDCode(), p.getImages(), "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrossWt(), "gross"),
                                                getdvalue(p.getTotalStoneWeight(), "tsw"), getdvalue(p.getNetWt(), "netwt"), getdvalue(p.getMakingPerGram(), "makingpergram"),
                                                getdvalue(p.getMakingPercentage(), "making%"), getdvalue(p.getMakingFixedAmt(), "mf"), getdvalue(p.getMakingFixedWastage(), "mfw"),
                                                getdvalue(p.getTotalStoneAmount(), "tsa"), getdvalue(p.getmRP(), "mrp"), getdvalue(p.getHallmarkAmount(), "hall"),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done",o.getProductCode(),o.getCounterId(),o.getCounterName(),o.getTotPcs(),o.getTotMPcs(),o.getCategoryId(),o.getProductId(),o.getDesignId(),o.getPurityId());

                                        item.setPcs(p.getPieces());
                                        item.setImageUrl(p.getImages());
                                        if(!o.getBranch().equalsIgnoreCase("home")){
                                            item.setBranch(o.getBranch());
                                        }

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
                                                "", 0, 0, 0, 0, getdvalue(p.getGrossWt(), "gross"),
                                                getdvalue(p.getTotalStoneWeight(), "tsw"), getdvalue(p.getNetWt(), "netwt"), getdvalue(p.getMakingPerGram(), "makingpergram"),
                                                getdvalue(p.getMakingPercentage(), "making%"), getdvalue(p.getMakingFixedAmt(), "mf"), getdvalue(p.getMakingFixedWastage(), "mfw"),
                                                getdvalue(p.getTotalStoneAmount(), "tsa"), getdvalue(p.getmRP(), "mrp"), getdvalue(p.getHallmarkAmount(), "hall"),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done",p.getProductCode(),String.valueOf(p.getCounterId()),p.getCounterName(),0,0,p.getCategoryId(),p.getProductId(),p.getDesignId(),p.getPurityId());
                                        item.setPcs(p.getPieces());
                                        item.setImageUrl(p.getImages());
//                                        if(!o.getBranch().equalsIgnoreCase("home")){
                                            item.setBranch("Home");
//                                        }
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }
                                    }

                                } else {
                                    //count here
                                    totalissueitem.set(totalissueitem.get() + 1);

                                    Issuemode is = new Issuemode();
                                    is.setIssueId(Issueid);
                                    is.setBarCode(barcode);
                                    is.setItemCode(p.getItemCode());
                                    issueitem.add(is);

                                }
                            }
                        }


                    } else {

                        String[] tidArray = p.gettIDNumber().split(",");
                        String[] barcodeArray = p.getrFIDCode().split(",");


                        for (int i = 0; i < tidArray.length; i++) {
                            String tid = tidArray[i].trim();
                            String barcode = barcodeArray[i].trim();


                            Log.e("check api process", "tid  " + barcode);
                            if (ml.containsKey(tid)) {
                                Itemmodel o = ml.get(tid);
                                Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                        o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategoryName(), p.getProductName(), p.getPurityName(),
                                        o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                        o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                        p.gethUIDCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrossWt(), "gross"),
                                        getdvalue(p.getTotalStoneWeight(), "tsw"), getdvalue(p.getNetWt(), "netwt"), getdvalue(p.getMakingPerGram(), "makingpergram"),
                                        getdvalue(p.getMakingPercentage(), "making%"), getdvalue(p.getMakingFixedAmt(), "mf"), getdvalue(p.getMakingFixedWastage(), "mfw"),
                                        getdvalue(p.getTotalStoneAmount(), "tsa"), getdvalue(p.getmRP(), "mrp"), getdvalue(p.getHallmarkAmount(), "hall"),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done",o.getProductCode(),o.getCounterId(),o.getCounterName(),o.getTotPcs(),o.getTotMPcs(),o.getCategoryId(),o.getProductId(),o.getDesignId(),o.getPurityId());
                                item.setPcs(p.getPieces());
                                item.setPartyCode(p.getImages());
                                item.setPcs(p.getPieces());
                                item.setImageUrl(p.getImages());
                                if(!o.getBranch().equalsIgnoreCase("home")){
                                    item.setBranch(o.getBranch());
                                }
                                if (item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {

                                    nmap.put(item.getTidValue(), item);
                                }

                            } else {

                                Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                        tid, tid, getbvalue(p.getBranchName()), p.getCategoryName(), p.getProductName(), p.getPurityName(),
                                        "", "", "", "", "",
                                        "", "", barcode, p.getItemCode(), p.getBoxName(), p.gethUIDCode(),
                                        "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                        "api add", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrossWt(), "gross"),
                                        getdvalue(p.getTotalStoneWeight(), "tsw"), getdvalue(p.getNetWt(), "netwt"), getdvalue(p.getMakingPerGram(), "makingpergram"),
                                        getdvalue(p.getMakingPercentage(), "making%"), getdvalue(p.getMakingFixedAmt(), "mf"), getdvalue(p.getMakingFixedWastage(), "mfw"),
                                        getdvalue(p.getTotalStoneAmount(), "tsa"), getdvalue(p.getmRP(), "mrp"), getdvalue(p.getHallmarkAmount(), "hall"),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done",p.getProductCode(),String.valueOf(p.getCounterId()),p.getCounterName(),0,0,p.getCategoryId(),p.getProductId(),p.getDesignId(),p.getPurityId());
//                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
//                                    nmap.put(item.getTidValue(), item);
//                                }
                                item.setPcs(p.getPieces());
                                item.setPartyCode(p.getImages());
                                item.setPcs(p.getPieces());
                                item.setImageUrl(p.getImages());

//                                if(!o.getBranch().equalsIgnoreCase("home")){
                                    item.setBranch("Home");
//                                }


                                if (item.getTidValue() != null && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                    nmap.put(item.getTidValue(), item);
                                    Log.e("checking all tidvalues", "" + item.toString());
                                }
                            }


                        }

                    }


                }

                Log.e("check2", "" + ml.size() + "  " + nmap.size() + "  " + dmap.size());
                for (String key : ml.keySet()) {
                    // Check if the key exists in umap
                    if (!nmap.containsKey(key)) {
                        // If the key does not exist in umap, add it to dmap
                        dmap.add(ml.get(key));
                    }
                }
                List<Itemmodel> itemlist = new ArrayList<>(nmap.values());




                new Handler(Looper.getMainLooper()).post(() -> {
                    //entryDatabase.saveAllItem(itemlist);
                    entryDatabase.makeentry(activity, itemlist, "excel", "product", app, issueitem, new SaveCallback() {

                        @Override
                        public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
                            deleteitems(entryDatabase, activity, dmap, app);


                           // if (totalissueitem.get() > 0) {
                              //  Toast.makeText(activity, "failed items " + totalissueitem.get(), Toast.LENGTH_SHORT).show();

                           // }
                            dialog.dismiss();

                        }

                        @Override
                        public void onSaveFailure(List<Itemmodel> failedItems) {
                            Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
                            deleteitems(entryDatabase, activity, dmap, app);
                            dialog.dismiss();
                        }
                    });

                });


                // Handle the updated map (e.g., update UI or database)
                // For example:
                // updateDatabase(nmap);

              /*  ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    try {
                        entryDatabase.saveAllItem(itemlist); // Heavy DB operation

                        // ✅ Step 3: Switch to UI to call makeentry


                    } catch (Exception e) {
                        handler.post(() -> {
                            Toast.makeText(activity, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing()) dialog.dismiss();
                        });
                        Log.e("DB_SAVE_ERROR", "Exception while saving", e);
                    }
                });*/

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    /*//this is all apis
    public void getproducts(HashMap<String, Itemmodel> ml, Context activity, String baseUrl, String rfidurl, EntryDatabase entryDatabase, MyApplication app) {

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading data");
        dialog.show();


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();


        AtomicInteger totalissueitem = new AtomicInteger();

        List<Rfidresponse.ItemModel> rfidList = new ArrayList<>();
        List<Productmodel> productList = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(2); // Count down for both API calls

        if(rfidurl == null || rfidurl.isEmpty()){
            latch.countDown();

        }
        else{
            Retrofit retrofit1 = new Retrofit.Builder()
                    .baseUrl(rfidurl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService1 = retrofit1.create(ApiService.class);
            Call<Rfidresponse> call1 = apiService1.getRfiddata();
            call1.enqueue(new Callback<Rfidresponse>() {
                @Override
                public void onResponse(Call<Rfidresponse> call, Response<Rfidresponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Rfidresponse apiResponse = response.body();
                        if ("Success".equals(apiResponse.getStatus())) {
                            rfidList.addAll(apiResponse.getData());
                        } else {
                            Toast.makeText(activity, "Failed to load RFID data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "RFID response was not successful", Toast.LENGTH_SHORT).show();
                    }
                    latch.countDown();
                }

                @Override
                public void onFailure(Call<Rfidresponse> call, Throwable t) {
                    Toast.makeText(activity, "Failed to load RFID data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    latch.countDown();
                }
            });
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ProductResponse> call2 = apiService.getAllProducts();
        call2.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    productList.addAll(response.body().getData());

                } else {
                    Toast.makeText(activity, "Product response was not successful", Toast.LENGTH_SHORT).show();
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity, "Failed to load product data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                latch.countDown();
            }
        });

        List<Itemmodel> dmap = new ArrayList<>();

        new Thread(() -> {
            try {
                latch.await(); // Wait for both API calls to finish

                // Process data here
                HashMap<String, Itemmodel> nmap = new HashMap<>();
                for (Productmodel p : productList) {

//                    if(p.getTid() == null || p.getTid().isEmpty()){
                        Log.e("check nirvan", "items "+p.toString());
                        if (p.getBarcodeNumber() != null && !p.getBarcodeNumber().isEmpty()){
                            String[] barcodeArray = p.getBarcodeNumber().split(",");

                            for (String barcode : barcodeArray) {
                                barcode = barcode.trim();
                                String tid = findTidByBarcode(rfidList, barcode);
                                if (tid != null) {

                                    if (ml.containsKey(tid)) {
                                        Itemmodel o = ml.get(tid);
                                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                                p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");

                                        item.setImageUrl(p.getImageurl());
                                        item.setPcs(p.getPcs());

                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }

                                    } else {

                                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                "", "", "", "", "",
                                                "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                                "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                "api add", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");

                                        item.setImageUrl(p.getImageurl());
                                        item.setPcs(p.getPcs());

                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }
                                    }

                                } else {
                                    //count here
                                    totalissueitem.set(totalissueitem.get() + 1);

                                }
                            }
                        }


//                    }
                    *//*else{

                        String[] tidArray = p.getTid().split(",");
                        String[] barcodeArray = p.getBarcodeNumber().split(",");
                        for (int i = 0; i < tidArray.length; i++) {
                            String tid = tidArray[i].trim();
                            String barcode = barcodeArray[i].trim();

                            if (ml.containsKey(tid)) {
                                Itemmodel o = ml.get(tid);
                                Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                        o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                        o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                        o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                        p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                        getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                        getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                        getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done");
                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                    nmap.put(item.getTidValue(), item);
                                }

                            } else {
                                Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                        tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                        "", "", "", "", "",
                                        "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                        "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                        "api add", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                        getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                        getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                        getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done");
                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                    nmap.put(item.getTidValue(), item);
                                }
                            }

                        }

                    }*//*


                }
                for (String key : ml.keySet()) {
                    // Check if the key exists in umap
                    if (!nmap.containsKey(key)) {
                        // If the key does not exist in umap, add it to dmap
                        dmap.add(ml.get(key));
                    }
                }
                List<Itemmodel> itemlist = new ArrayList<>(nmap.values());

                dialog.dismiss();
                new Handler(Looper.getMainLooper()).post(() -> {
                    entryDatabase.makeentry(activity, itemlist, "excel", "product", app, issueitem, new SaveCallback() {

                        @Override
                        public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
                            deleteitems(entryDatabase, activity, dmap, app);

                            Toast.makeText(activity, "failed items " + totalissueitem.get(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onSaveFailure(List<Itemmodel> failedItems) {
                            Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
                            deleteitems(entryDatabase, activity, dmap, app);
                        }
                    });

                });


                // Handle the updated map (e.g., update UI or database)
                // For example:
                // updateDatabase(nmap);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        *//*call1.enqueue(new Callback<Rfidresponse>() {
            @Override
            public void onResponse(Call<Rfidresponse> call, Response<Rfidresponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Rfidresponse apiResponse = response.body();
                    if ("Success".equals(apiResponse.getStatus())) {
                        rfidlist = apiResponse.getData();
                        // Update the database or UI as needed
                    } else {
                        Toast.makeText(activity, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, "Response was not successful", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Rfidresponse> call, Throwable t) {

            }
        });
        Call<ProductResponse> call = apiService.getAllProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.e("checkresponse", " "+response);
                    if (!response.body().getData().isEmpty()) {
                        HashMap<String, Itemmodel> nmap = new HashMap<>();
                        List<Itemmodel> dmap = new ArrayList<>();
                        List<Productmodel> pitem = response.body().getData();

                        for (Productmodel p : pitem) {
                            Log.d("customapiresponse", "  "+p);
                            if (p.getTid() != null && !p.getTid().isEmpty())
                            {

                                String[] tidArray = p.getTid().split(",");
                                String[] barcodeArray = p.getBarcodeNumber().split(",");
                                for (int i = 0; i < tidArray.length; i++) {
                                    String tid = tidArray[i].trim();
                                    String barcode = barcodeArray[i].trim();

                                    if (ml.containsKey(tid)) {
                                        Itemmodel o = ml.get(tid);
                                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                                p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }

                                    }
                                    else {
                                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                "", "", "", "", "",
                                                "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                                "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                "api add", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }
                                    }

                                }
                            }
                            else
                            {
                                String[] barcodeArray = p.getBarcodeNumber().split(",");
                                for (int i = 0; i < barcodeArray.length; i++) {
                                    String tid = get tidvalue from list
                                    String barcode = barcodeArray[i].trim();

                                    if (ml.containsKey(tid)) {
                                        Itemmodel o = ml.get(tid);
                                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                                p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }

                                    }
                                    else {
                                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                "", "", "", "", "",
                                                "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                                "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                "api add", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }
                                    }

                                }
                            }
                        }
                        for (String key : ml.keySet()) {
                            // Check if the key exists in umap
                            if (!nmap.containsKey(key)) {
                                // If the key does not exist in umap, add it to dmap
                                dmap.add(ml.get(key));
                            }
                        }
                        Log.d("check items", "  " + ml.size() + "  " + nmap.values().toString() + "  " + dmap.size());
                        List<Itemmodel> itemlist = new ArrayList<>(nmap.values());
                        entryDatabase.checkdatabase(activity);
//                        deleteitems(entryDatabase, activity, dmap, app);
                        entryDatabase.makeentry(activity, itemlist, "excel", "product", app, new SaveCallback() {

                            @Override
                            public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
                                deleteitems(entryDatabase, activity, dmap, app);


                            }

                            @Override
                            public void onSaveFailure(List<Itemmodel> failedItems) {
                                Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
                                deleteitems(entryDatabase, activity, dmap, app);
                            }
                        });
                    }

                    Toast.makeText(activity, "url verified successfully", Toast.LENGTH_SHORT).show();
//                    verified = true;
                } else {
                    Toast.makeText(activity, "failed to verify", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d("customapi", "  " + t.getMessage());
                dialog.dismiss();
                Toast.makeText(activity, "failed to verify", Toast.LENGTH_SHORT).show();
            }
        });*//*

    }

    */




/*
public void getproductscustom(HashMap<String, Itemmodel> ml, Context activity, String baseUrl, String rfidurl, EntryDatabase entryDatabase, MyApplication app) {

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading data");
        dialog.show();


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.72:98/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);





    Call<ItemCountResponse> call = apiService.getItemCount();


        Call<ProductResponse> call2 = apiService.getAllProducts();
        AtomicInteger totalissueitem = new AtomicInteger();

        List<Rfidresponse.ItemModel> rfidList = new ArrayList<>();
        List<Productmodel> productList = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(2); // Count down for both API calls

        if(rfidurl == null || rfidurl.isEmpty()){
            latch.countDown();

        }else{
            Retrofit retrofit1 = new Retrofit.Builder()
                    .baseUrl(rfidurl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService1 = retrofit1.create(ApiService.class);
            Call<Rfidresponse> call1 = apiService1.getRfiddata();
            call1.enqueue(new Callback<Rfidresponse>() {
                @Override
                public void onResponse(Call<Rfidresponse> call, Response<Rfidresponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Rfidresponse apiResponse = response.body();
                        if ("Success".equals(apiResponse.getStatus())) {
                            rfidList.addAll(apiResponse.getData());
                        } else {
                            Toast.makeText(activity, "Failed to load RFID data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "RFID response was not successful", Toast.LENGTH_SHORT).show();
                    }
                    latch.countDown();
                }

                @Override
                public void onFailure(Call<Rfidresponse> call, Throwable t) {
                    Toast.makeText(activity, "Failed to load RFID data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    latch.countDown();
                }
            });
        }



        call2.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    productList.addAll(response.body().getData());
                } else {
                    Toast.makeText(activity, "Product response was not successful", Toast.LENGTH_SHORT).show();
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity, "Failed to load product data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                latch.countDown();
            }
        });

        List<Itemmodel> dmap = new ArrayList<>();

        new Thread(() -> {
            try {
                latch.await(); // Wait for both API calls to finish

                // Process data here
                HashMap<String, Itemmodel> nmap = new HashMap<>();
                for (Productmodel p : productList) {

                    if(p.getTid() == null || p.getTid().isEmpty()){

                        if (p.getBarcodeNumber() != null || !p.getBarcodeNumber().isEmpty()){
                            String[] barcodeArray = p.getBarcodeNumber().split(",");

                            for (String barcode : barcodeArray) {
                                barcode = barcode.trim();
                                String tid = findTidByBarcode(rfidList, barcode);
                                if (tid != null) {

                                    if (ml.containsKey(tid)) {
                                        Itemmodel o = ml.get(tid);
                                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                                p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }

                                    } else {
                                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                "", "", "", "", "",
                                                "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                                "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                "api add", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }
                                    }

                                } else {
                                    //count here
                                    totalissueitem.set(totalissueitem.get() + 1);

                                }
                            }
                        }


                    }
                    else{

                        String[] tidArray = p.getTid().split(",");
                        String[] barcodeArray = p.getBarcodeNumber().split(",");
                        for (int i = 0; i < tidArray.length; i++) {
                            String tid = tidArray[i].trim();
                            String barcode = barcodeArray[i].trim();

                            if (ml.containsKey(tid)) {
                                Itemmodel o = ml.get(tid);
                                Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                        o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                        o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                        o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                        p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                        getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                        getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                        getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done");
                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                    nmap.put(item.getTidValue(), item);
                                }

                            } else {
                                Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                        tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                        "", "", "", "", "",
                                        "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                        "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                        "api add", "",
                                        "", "", "", "", "",
                                        "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                        getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                        getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                        getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0,
                                        0, "done", "done");
                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                    nmap.put(item.getTidValue(), item);
                                }
                            }

                        }

                    }


                }
                for (String key : ml.keySet()) {
                    // Check if the key exists in umap
                    if (!nmap.containsKey(key)) {
                        // If the key does not exist in umap, add it to dmap
                        dmap.add(ml.get(key));
                    }
                }
                List<Itemmodel> itemlist = new ArrayList<>(nmap.values());


                new Handler(Looper.getMainLooper()).post(() -> {
                    entryDatabase.makeentry(activity, itemlist, "excel", "product", app, new SaveCallback() {

                        @Override
                        public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
                            deleteitems(entryDatabase, activity, dmap, app);

                            Toast.makeText(activity, "failed items " + totalissueitem.get(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onSaveFailure(List<Itemmodel> failedItems) {
                            Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
                            deleteitems(entryDatabase, activity, dmap, app);
                        }
                    });

                });


                // Handle the updated map (e.g., update UI or database)
                // For example:
                // updateDatabase(nmap);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        */
/*call1.enqueue(new Callback<Rfidresponse>() {
            @Override
            public void onResponse(Call<Rfidresponse> call, Response<Rfidresponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Rfidresponse apiResponse = response.body();
                    if ("Success".equals(apiResponse.getStatus())) {
                        rfidlist = apiResponse.getData();
                        // Update the database or UI as needed
                    } else {
                        Toast.makeText(activity, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, "Response was not successful", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Rfidresponse> call, Throwable t) {

            }
        });
        Call<ProductResponse> call = apiService.getAllProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.e("checkresponse", " "+response);
                    if (!response.body().getData().isEmpty()) {
                        HashMap<String, Itemmodel> nmap = new HashMap<>();
                        List<Itemmodel> dmap = new ArrayList<>();
                        List<Productmodel> pitem = response.body().getData();

                        for (Productmodel p : pitem) {
                            Log.d("customapiresponse", "  "+p);
                            if (p.getTid() != null && !p.getTid().isEmpty())
                            {

                                String[] tidArray = p.getTid().split(",");
                                String[] barcodeArray = p.getBarcodeNumber().split(",");
                                for (int i = 0; i < tidArray.length; i++) {
                                    String tid = tidArray[i].trim();
                                    String barcode = barcodeArray[i].trim();

                                    if (ml.containsKey(tid)) {
                                        Itemmodel o = ml.get(tid);
                                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                                p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }

                                    }
                                    else {
                                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                "", "", "", "", "",
                                                "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                                "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                "api add", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }
                                    }

                                }
                            }
                            else
                            {
                                String[] barcodeArray = p.getBarcodeNumber().split(",");
                                for (int i = 0; i < barcodeArray.length; i++) {
                                    String tid = get tidvalue from list
                                    String barcode = barcodeArray[i].trim();

                                    if (ml.containsKey(tid)) {
                                        Itemmodel o = ml.get(tid);
                                        Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                o.getTidValue(), o.getEpcValue(), getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                o.getDiamondSize(), o.getDiamondCertificate(), barcode, p.getItemCode(), p.getBoxName(),
                                                p.getHuidCode(), "", "", o.getStatus(), o.getTagTransaction(), "api update", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }

                                    }
                                    else {
                                        Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                tid, "", getbvalue(p.getBranchName()), p.getCategory_Name(), p.getItemType(), p.getPurity(),
                                                "", "", "", "", "",
                                                "", "", barcode, p.getItemCode(), p.getBoxName(), p.getHuidCode(),
                                                "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                "api add", "",
                                                "", "", "", "", "",
                                                "", 0, 0, 0, 0, getdvalue(p.getGrosswt()),
                                                getdvalue(p.getStoneWeight()), getdvalue(p.getNetWt()), getdvalue(p.getMaking_per_gram()),
                                                getdvalue(p.getMaking_Percentage()), getdvalue(p.getMaking_Fixed_Amt()), getdvalue(p.getMaking_Fixed_Wastage()),
                                                getdvalue(p.getStoneAmount()), getdvalue(p.getMrp()), getdvalue(p.getHallmark_amt()),
                                                1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, 0, 0, 0, 0,
                                                0, "done", "done");
                                        if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                            nmap.put(item.getTidValue(), item);
                                        }
                                    }

                                }
                            }
                        }
                        for (String key : ml.keySet()) {
                            // Check if the key exists in umap
                            if (!nmap.containsKey(key)) {
                                // If the key does not exist in umap, add it to dmap
                                dmap.add(ml.get(key));
                            }
                        }
                        Log.d("check items", "  " + ml.size() + "  " + nmap.values().toString() + "  " + dmap.size());
                        List<Itemmodel> itemlist = new ArrayList<>(nmap.values());
                        entryDatabase.checkdatabase(activity);
//                        deleteitems(entryDatabase, activity, dmap, app);
                        entryDatabase.makeentry(activity, itemlist, "excel", "product", app, new SaveCallback() {

                            @Override
                            public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
                                deleteitems(entryDatabase, activity, dmap, app);


                            }

                            @Override
                            public void onSaveFailure(List<Itemmodel> failedItems) {
                                Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
                                deleteitems(entryDatabase, activity, dmap, app);
                            }
                        });
                    }

                    Toast.makeText(activity, "url verified successfully", Toast.LENGTH_SHORT).show();
//                    verified = true;
                } else {
                    Toast.makeText(activity, "failed to verify", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d("customapi", "  " + t.getMessage());
                dialog.dismiss();
                Toast.makeText(activity, "failed to verify", Toast.LENGTH_SHORT).show();
            }
        });*//*


    }
*/


    private String findTidByBarcode(List<Rfidresponse.ItemModel> rfidList, String barcode) {
        for (Rfidresponse.ItemModel item : rfidList) {
            if (item.getBarcodeNumber().equals(barcode)) {
                return item.getTid();
            }
        }
        return null;
    }

    private String getbvalue(String branchName) {
        if (branchName == null || branchName.isEmpty()) {
            return "Home";
        }
        return branchName;
    }

    private void deleteitems(EntryDatabase entryDatabase, Context activity, List<Itemmodel> dmap, MyApplication app) {
        entryDatabase.makeentry(activity, dmap, "delete", "product", app, issueitem, new SaveCallback() {

            @Override
            public void onSaveSuccess() {
                Toast.makeText(activity, "Item updated succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();


            }

            @Override
            public void onSaveFailure(List<Itemmodel> failedItems) {
                Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double getdvalue(String s, String from) {
        try {
            if (s == null || s.trim().isEmpty()) {
                return 0;
            }
            Log.e("check dvalue", "Parsing value: " + s + " from: " + from);
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            Log.e("getdvalue error", "Invalid number: '" + s + "' from: " + from);
            return 0;
        }
    }


    public void updateproduct(ArrayList<Itemmodel> ml, Context activity, String baseUrl) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        List<ProductUpdate> uplist = new ArrayList<>();
        Call<ProductUpdateResponse> call = apiService.updateProduct(uplist);
        call.enqueue(new Callback<ProductUpdateResponse>() {
            @Override
            public void onResponse(Call<ProductUpdateResponse> call, Response<ProductUpdateResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductUpdateResponse> call, Throwable t) {
                Toast.makeText(activity, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    List<String> imageurls = new ArrayList<>();
    List<File> destinationFiles = new ArrayList<>();


    public void sheetprocessdemo(HashMap<String, Itemmodel> ml, FragmentActivity activity, String sheeturl, EntryDatabase entryDatabase, MyApplication app, List<Rfidresponse.ItemModel> rfidList) {
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
                            Log.d("@@","rowData @@"+rowData);
                            String stonewt = "0";
                            // Extract relevant data
                            String branch = rowData.optJSONObject(9) != null ? rowData.getJSONObject(9).optString("v", "") : "";
                            String category = rowData.optJSONObject(10) != null ? rowData.getJSONObject(10).optString("v", "") : "";
                            String itemtype = rowData.optJSONObject(2) != null ? rowData.getJSONObject(2).optString("v", "") : "";
                            String purity = rowData.optJSONObject(3) != null ? rowData.getJSONObject(3).optString("v", "") : "";
                            String grosswt = rowData.optJSONObject(4) != null ? rowData.getJSONObject(4).optString("v", "") : "";
                            String netwt = rowData.optJSONObject(5) != null ? rowData.getJSONObject(5).optString("v", "") : "";
                            String itemcode = rowData.optJSONObject(1) != null ? rowData.getJSONObject(1).optString("v", "") : "";
                            String sbarcode = rowData.optJSONObject(0) != null ? rowData.getJSONObject(0).optString("v", "") : "";
                            String sbox = rowData.optJSONObject(7) != null ? rowData.getJSONObject(7).optString("v", "") : "";
                            String sstoneamt = rowData.optJSONObject(8) != null ? rowData.getJSONObject(8).optString("v", "") : "";
                            String simageurl = rowData.optJSONObject(11) != null ? rowData.getJSONObject(11).optString("v", "") : "";
                            String ssku = rowData.optJSONObject(6) != null ? rowData.getJSONObject(6).optString("v", "") : "";
                           // String productCode = rowData.optJSONObject(6) != null ? rowData.getJSONObject(6).optString("v", "") : "";


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

                            /*// Process item data and add to nmap or dmap...

                            String date = rowData.optJSONObject(0) != null ? rowData.getJSONObject(0).optString("v", "") : "";
                            String barcode = rowData.optJSONObject(1) != null ? rowData.getJSONObject(1).optString("v", "") : "";
                            String jobid = rowData.optJSONObject(2) != null ? rowData.getJSONObject(2).optString("v", "") : "";
                            String from = rowData.optJSONObject(3) != null ? rowData.getJSONObject(3).optString("v", "") : "";
                            String to = rowData.optJSONObject(4) != null ? rowData.getJSONObject(4).optString("v", "") : "";
                            String customername = rowData.optJSONObject(5) != null ? rowData.getJSONObject(5).optString("v", "") : "";
                            String purity = rowData.optJSONObject(6) != null ? rowData.getJSONObject(6).optString("v", "") : "";

                            String pcode = rowData.optJSONObject(7) != null ? rowData.getJSONObject(7).optString("v", "") : "";
                            String inpcs = rowData.optJSONObject(8) != null ? rowData.getJSONObject(8).optString("v", "") : "";
                            String inwt = rowData.optJSONObject(9) != null ? rowData.getJSONObject(9).optString("v", "") : "";
                            String outpcs = rowData.optJSONObject(10) != null ? rowData.getJSONObject(10).optString("v", "") : "";
                            String outwt = rowData.optJSONObject(11) != null ? rowData.getJSONObject(11).optString("v", "") : "";

                            if(!inpcs.isEmpty() && !inwt.isEmpty() && !outpcs.isEmpty() && !outwt.isEmpty()){

                                String ouq = jobid+"_"+from+"_"+to;

                                int pc = (int) Math.round(Double.parseDouble(inpcs));
                                int opcs = (int) Math.round(Double.parseDouble(outpcs));

                                if (barcode != null && !barcode.isEmpty()) {
                                    String tid = findTidByBarcode(rfidList, barcode.trim());

                                    if(tid != null && !tid.isEmpty()){
                                        if(ml.containsKey(tid)){
                                            Itemmodel o = ml.get(tid);
                                            String uq = o.getDiamondCertificate()+"_"+o.getDiamondClarity()+"_"+o.getDiamondColor();
                                            if(!ouq.equalsIgnoreCase( uq)){
                                                Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                        o.getTidValue(), o.getEpcValue(), "home", "Gold", jobid, purity,
                                                        o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                        o.getDiamondSize(), o.getDiamondCertificate(), barcode, pcode, "",
                                                        "", "", "", o.getStatus(), o.getTagTransaction(), "gsheet update", "",
                                                        "", customername, "", "", "",
                                                        "", pc, opcs, 0, 0, getdvalue(inwt, "g"),
                                                        getdvalue("0", "swt"), getdvalue(outwt, "n"), getdvalue("0", "swt"),
                                                        getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                        getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, "done", "done");
//                                                item.setImageUrl(simageurl);
                                                item.setDiamondClarity(from);
                                                item.setDiamondCertificate(jobid);
                                                item.setDiamondColor(to);
                                                item.setDiamondMetal(date);
                                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                    nmap.put(item.getTidValue(), item);
                                                }
                                            }
                                        }else{
                                            Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                    tid, tid, "Home", "Gold", jobid, purity,
                                                    "", "", "", "", "",
                                                    "", "", barcode, pcode, "", "",
                                                    "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                    "gsheet add", "",
                                                    "", "", "", "", "",
                                                    "", 0, 0, 0, 0, getdvalue(inwt, "g"),
                                                    getdvalue(stonewt, "swt"), getdvalue(outwt, "swt"), getdvalue("0", "swt"),
                                                    getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, "done", "done");
                                            item.setDiamondClarity(from);
                                            item.setDiamondCertificate(jobid);
                                            item.setDiamondColor(to);
                                            item.setDiamondMetal(date);
                                            if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                nmap.put(item.getTidValue(), item);
                                            }
                                        }
                                    }else {

                                    }


                                }


                            }*/


                            String[] barcodeArray = sbarcode.split(",");





                            for (String s : barcodeArray) {
//                                    String tid = tidArray[i].trim();
                                String barcode = s.trim();
                                if (barcode != null && !barcode.isEmpty()) {
                                    String
                                            tid = findTidByBarcode(rfidList, barcode.trim());

                                    Log.e("checkingvalues", " "+tid+"  "+barcode);

                                    if (tid != null && !tid.isEmpty()) {
                                        if (ml.containsKey(tid)) {
                                            Itemmodel o = ml.get(tid);
                                            Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                    o.getTidValue(), o.getEpcValue(), getbvalue(branch), category, itemtype, purity,
                                                    o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                    o.getDiamondSize(), o.getDiamondCertificate(), barcode, itemcode, sbox,
                                                    "", "", "", o.getStatus(), o.getTagTransaction(), "gsheet update", "",
                                                    "", "", "", "", "",
                                                    "", 0, 0, 0, 0, getdvalue(grosswt, "g"),
                                                    getdvalue(stonewt, "swt"), getdvalue(netwt, "n"), getdvalue("0", "swt"),
                                                    getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    getdvalue(sstoneamt, "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, "done", "done",o.getProductCode(),o.getCounterId(),o.getCounterName(),o.getTotPcs(),o.getTotMPcs(),o.getCategoryId(),o.getProductId(),o.getDesignId(),o.getPurityId());
                                            item.setImageUrl(simageurl);
                                            item.setPcs(o.getPcs());
                                            item.setDiamondClarity(ssku);
                                            if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                nmap.put(item.getTidValue(), item);
                                            }

                                        } else {
                                            Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                    tid, tid, getbvalue(branch), category, itemtype, purity,
                                                    "", "", "", "", "",
                                                    "", "", barcode, itemcode, sbox, "",
                                                    "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                    "gsheet add", "",
                                                    "", "", "", "", "",
                                                    "", 0, 0, 0, 0, getdvalue(grosswt, "g"),
                                                    getdvalue(stonewt, "swt"), getdvalue(netwt, "swt"), getdvalue("0", "swt"),
                                                    getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    getdvalue(sstoneamt, "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, "done", "done","","","",0,0,0,0,0,0);
                                            item.setImageUrl(simageurl);
                                            item.setPcs("");
                                            item.setDiamondClarity(ssku);
                                            if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                nmap.put(item.getTidValue(), item);
                                            }
                                        }
                                    } else {
                                        Log.e("checking tid", "" + barcode);
                                    }
                                }
                            }


                        }



                        for (String key : ml.keySet()) {
                            // Check if the key exists in umap
                            if (!nmap.containsKey(key)) {
                                // If the key does not exist in umap, add it to dmap
                                dmap.add(ml.get(key));
                            }
                        }


                        /*for (String key : ml.keySet()) {
                            // Check if the key exists in umap
                            if (!nmap.containsKey(key)) {
                                // If the key does not exist in umap, add it to dmap
                                dmap.add(ml.get(key));
                            }
                        }*/

                        List<Itemmodel> itemlist = new ArrayList<>(nmap.values());
                        Log.d("check items", "  " + ml.size() + "  " + nmap.values().toString() + "  " + dmap.size()+"  "+itemlist);
                        entryDatabase.checkdatabase(activity);
//                        deleteitems(entryDatabase, activity, dmap, app);
                        entryDatabase.makeentry(activity, itemlist, "productdemo", "product", app, issueitem, new SaveCallback() {

                            @Override
                            public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
//                                deleteitems(entryDatabase, activity, dmap, app);
                                dialog.dismiss();
//                                dialog.dismiss();
//                                if (!imageurls.isEmpty()) {
////                                    ProgressDialog dialog = new ProgressDialog(activity);
////                                    dialog.setMessage("Downloading images...");
////                                    dialog.setCancelable(false);
////                                    dialog.show();
//
//                                    if(!dialog.isShowing()){
//                                        dialog.setMessage("loading images");
//                                        dialog.show();
//                                        dialog.setCanceledOnTouchOutside(false);
//                                    }
//
//                                    downloadImagesConcurrently(activity, imageurls, destinationFiles, dialog);
//                                }

                            }

                            @Override
                            public void onSaveFailure(List<Itemmodel> failedItems) {
                                Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
//                                deleteitems(entryDatabase, activity, dmap, app);

                                dialog.dismiss();
                            }
                        });


                        // Database operations
                        dialog.dismiss();
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


    public void sheetprocess(HashMap<String, Itemmodel> ml, FragmentActivity activity, String sheeturl, EntryDatabase entryDatabase, MyApplication app, List<Rfidresponse.ItemModel> rfidList) {
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
                            Log.d("",""+rowData);
                            String stonewt = "0";
                            // Extract relevant data
                            String branch = rowData.optJSONObject(9) != null ? rowData.getJSONObject(9).optString("v", "") : "";
                            String category = rowData.optJSONObject(10) != null ? rowData.getJSONObject(10).optString("v", "") : "";
                            String itemtype = rowData.optJSONObject(2) != null ? rowData.getJSONObject(2).optString("v", "") : "";
                            String purity = rowData.optJSONObject(3) != null ? rowData.getJSONObject(3).optString("v", "") : "";
                            String grosswt = rowData.optJSONObject(4) != null ? rowData.getJSONObject(4).optString("v", "") : "";
                            String netwt = rowData.optJSONObject(5) != null ? rowData.getJSONObject(5).optString("v", "") : "";
                            String itemcode = rowData.optJSONObject(1) != null ? rowData.getJSONObject(1).optString("v", "") : "";
                            String sbarcode = rowData.optJSONObject(0) != null ? rowData.getJSONObject(0).optString("v", "") : "";
                            String sbox = rowData.optJSONObject(7) != null ? rowData.getJSONObject(7).optString("v", "") : "";
                            String sstoneamt = rowData.optJSONObject(8) != null ? rowData.getJSONObject(8).optString("v", "") : "";
                            String simageurl = rowData.optJSONObject(11) != null ? rowData.getJSONObject(11).optString("v", "") : "";
                            String ssku = rowData.optJSONObject(6) != null ? rowData.getJSONObject(6).optString("v", "") : "";
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

                            // Process item data and add to nmap or dmap...

                            String[] barcodeArray = sbarcode.split(",");


                            for (String s : barcodeArray) {
//                                    String tid = tidArray[i].trim();
                                String barcode = s.trim();
                                if (barcode != null && !barcode.isEmpty()) {
                                    String
                                            tid = findTidByBarcode(rfidList, barcode.trim());

                                    Log.e("checkingvalues", "  "+tid+"  "+barcode);

                                    if (tid != null && !tid.isEmpty()) {
                                        if (ml.containsKey(tid)) {
                                            Itemmodel o = ml.get(tid);
                                            Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                    o.getTidValue(), o.getEpcValue(), getbvalue(branch), category, itemtype, purity,
                                                    o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                    o.getDiamondSize(), o.getDiamondCertificate(), barcode, itemcode, sbox,
                                                    "", "", "", o.getStatus(), o.getTagTransaction(), "gsheet update", "",
                                                    "", "", "", "", "",
                                                    "", 0, 0, 0, 0, getdvalue(grosswt, "g"),
                                                    getdvalue(stonewt, "swt"), getdvalue(netwt, "n"), getdvalue("0", "swt"),
                                                    getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    getdvalue(sstoneamt, "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, "done", "done",o.getProductCode(),o.getCounterId(),o.getCounterName(),o.getTotPcs(),o.getTotMPcs(),o.getCategoryId(),o.getProductId(),o.getDesignId(),o.getPurityId());
                                            item.setImageUrl(simageurl);
                                            item.setPcs(o.getPcs());
                                            item.setDiamondClarity(ssku);
                                            if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                nmap.put(item.getTidValue(), item);
                                            }

                                        } else {
                                            Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                    tid, tid, getbvalue(branch), category, itemtype, purity,
                                                    "", "", "", "", "",
                                                    "", "", barcode, itemcode, sbox, "",
                                                    "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                    "gsheet add", "",
                                                    "", "", "", "", "",
                                                    "", 0, 0, 0, 0, getdvalue(grosswt, "g"),
                                                    getdvalue(stonewt, "swt"), getdvalue(netwt, "swt"), getdvalue("0", "swt"),
                                                    getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    getdvalue(sstoneamt, "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                    1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0,
                                                    0, "done", "done","","","",0,0,0,0,0,0);
                                            item.setImageUrl(simageurl);
                                            item.setDiamondClarity(ssku);
                                            if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                nmap.put(item.getTidValue(), item);
                                            }
                                        }
                                    } else {
                                        Log.e("checking tid", "" + barcode);
                                    }
                                }
                            }
                        }


                        for (String key : ml.keySet()) {
                            // Check if the key exists in umap
                            if (!nmap.containsKey(key)) {
                                // If the key does not exist in umap, add it to dmap
                                dmap.add(ml.get(key));
                            }
                        }
                        Log.d("check items", "  " + ml.size() + "  " + nmap.values().toString() + "  " + dmap.size());
                        List<Itemmodel> itemlist = new ArrayList<>(nmap.values());

                        entryDatabase.checkdatabase(activity);
//                        deleteitems(entryDatabase, activity, dmap, app);
                        entryDatabase.makeentry(activity, itemlist, "excel", "product", app, issueitem, new SaveCallback() {

                            @Override
                            public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
                                deleteitems(entryDatabase, activity, dmap, app);
                                dialog.dismiss();
//                                dialog.dismiss();
//                                if (!imageurls.isEmpty()) {
////                                    ProgressDialog dialog = new ProgressDialog(activity);
////                                    dialog.setMessage("Downloading images...");
////                                    dialog.setCancelable(false);
////                                    dialog.show();
//
//                                    if(!dialog.isShowing()){
//                                        dialog.setMessage("loading images");
//                                        dialog.show();
//                                        dialog.setCanceledOnTouchOutside(false);
//                                    }
//
//                                    downloadImagesConcurrently(activity, imageurls, destinationFiles, dialog);
//                                }

                            }

                            @Override
                            public void onSaveFailure(List<Itemmodel> failedItems) {
                                Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
                                deleteitems(entryDatabase, activity, dmap, app);

                                dialog.dismiss();
                            }
                        });


                        // Database operations
                        dialog.dismiss();
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

    private void downloadImagesConcurrently(Context context, List<String> imageUrls, List<File> destinationFiles, ProgressDialog dialog) {
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

    private void downloadImagesConcurrently1(Context context, List<String> imageUrls, List<File> destinationFiles, ProgressDialog dialog) {
        ExecutorService executor = Executors.newFixedThreadPool(4); // Control concurrency with a thread pool
        AtomicInteger remainingTasks = new AtomicInteger(imageUrls.size());

        for (int i = 0; i < imageUrls.size(); i++) {
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

                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.close();
                        inputStream.close();
                        Log.d("Download Image", "Image downloaded: " + destinationFile.getAbsolutePath());
                    } else {
                        Log.e("Download Image", "Failed to download image: " + imageUrl);
                    }

                    connection.disconnect();
                } catch (Exception e) {
                    Log.e("Download Image", "Error downloading image: " + e.getMessage(), e);
                }

                // Update progress and dismiss dialog when done
                if (remainingTasks.decrementAndGet() == 0) {
                    dialog.dismiss();
                    executor.shutdown();
                    Log.d("Download Image", "All images downloaded.");
                }
            });
        }
    }


    private void downloadImage(Context context, String imageUrl, File destinationFile) {
        try (InputStream in = new URL(imageUrl).openStream();
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.e("Download Image", "Error downloading file: " + destinationFile.getName(), e);
        }
    }

    private void downloadImage12(Context context, String imageUrl, File destinationFile) {
        new Thread(() -> {
            try {
                // Check if the file already exists and delete it
                if (destinationFile.exists()) {
                    boolean deleted = destinationFile.delete();
                    if (!deleted) {
                        Log.e("Download Image", "Failed to delete existing file: " + destinationFile.getAbsolutePath());
                        return;
                    }
                }

                // Open a connection to the URL
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Download Image", "Failed to download image: HTTP " + connection.getResponseCode());
                    return;
                }

                // Read from the input stream
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(destinationFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                // Close streams
                output.close();
                input.close();

                Log.d("Download Image", "Image downloaded: " + destinationFile.getAbsolutePath());
            } catch (Exception e) {
                Log.e("Download Image", "Error downloading image", e);
            }
        }).start();
    }

    private void downloadImage1(Context context, String imageUrl, File destinationFile) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Download Image", "Failed to download image: " + imageUrl);
                    return;
                }

                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(destinationFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                output.close();
                input.close();

                Log.d("Download Image", "Image downloaded: " + destinationFile.getAbsolutePath());
            } catch (Exception e) {
                Log.e("Download Image", "Error downloading image", e);
            }
        }).start();
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

    public void sheetprocess1(HashMap<String, Itemmodel> ml, FragmentActivity activity, String sheeturl, EntryDatabase entryDatabase, MyApplication app, List<Rfidresponse.ItemModel> rfidList) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("loading data");
        dialog.show();

        String url = "https://docs.google.com/spreadsheets/d/" + sheeturl + "/gviz/tq?tqx=out:json&sheet=Sheet2";
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Here you parse the response
//                        dialog.dismiss();
                        try {
                            HashMap<String, Itemmodel> nmap = new HashMap<>();
                            List<Itemmodel> dmap = new ArrayList<>();
                            // Extracting the JSON string from the JavaScript function call
                            String jsonString = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            JSONObject table = jsonObject.getJSONObject("table");
                            JSONArray rows = table.getJSONArray("rows");
                            for (int i1 = 0; i1 < rows.length(); i1++) {
                                JSONObject entryObj = rows.getJSONObject(i1);
                                JSONArray rowData = entryObj.getJSONArray("c");
//                                Log.e("checkrowdata", "" + rowData);

//                                String category = "Gold";//rowData.getJSONObject(2).getString("v");
                                String stonewt = "0";//rowData.getJSONObject(8).getString("v");

                                String branch = rowData.optJSONObject(9) != null ? rowData.getJSONObject(9).optString("v", "") : "";
                                String category = rowData.optJSONObject(10) != null ? rowData.getJSONObject(10).optString("v", "") : "";
                                String itemtype = rowData.optJSONObject(2) != null ? rowData.getJSONObject(2).optString("v", "") : "";
                                String purity = rowData.optJSONObject(3) != null ? rowData.getJSONObject(3).optString("v", "") : "";
                                String grosswt = rowData.optJSONObject(4) != null ? rowData.getJSONObject(4).optString("v", "") : "";
                                String netwt = rowData.optJSONObject(5) != null ? rowData.getJSONObject(5).optString("v", "") : "";
                                String itemcode = rowData.optJSONObject(1) != null ? rowData.getJSONObject(1).optString("v", "") : "";
                                String sbarcode = rowData.optJSONObject(0) != null ? rowData.getJSONObject(0).optString("v", "") : "";
                                String sbox = rowData.optJSONObject(7) != null ? rowData.getJSONObject(7).optString("v", "") : "";
                                String sstoneamt = rowData.optJSONObject(8) != null ? rowData.getJSONObject(8).optString("v", "") : "";
                                String simageurl = rowData.optJSONObject(11) != null ? rowData.getJSONObject(11).optString("v", "") : "";
                                String ssku = rowData.optJSONObject(6) != null ? rowData.getJSONObject(6).optString("v", "") : "";


//                                String[] tidArray = stid.split(",");
                                String[] barcodeArray = sbarcode.split(",");


                                for (String s : barcodeArray) {
//                                    String tid = tidArray[i].trim();
                                    String barcode = s.trim();
                                    if (barcode != null && !barcode.isEmpty()) {
                                        String
                                                tid = findTidByBarcode(rfidList, barcode.trim());

                                        if (tid != null && !tid.isEmpty()) {
                                            if (ml.containsKey(tid)) {
                                                Itemmodel o = ml.get(tid);
                                                Itemmodel item = new Itemmodel(o.getOperationTime(), o.getEntryDate(), o.getTransactionDate(), o.getRepaymentDate(),
                                                        o.getTidValue(), o.getEpcValue(), getbvalue(branch), category, itemtype, purity,
                                                        o.getDiamondMetal(), o.getDiamondColor(), o.getDiamondClarity(), o.getDiamondSetting(), o.getDiamondShape(),
                                                        o.getDiamondSize(), o.getDiamondCertificate(), barcode, itemcode, sbox,
                                                        "", "", "", o.getStatus(), o.getTagTransaction(), "gsheet update", "",
                                                        "", "", "", "", "",
                                                        "", 0, 0, 0, 0, getdvalue(grosswt, "g"),
                                                        getdvalue(stonewt, "swt"), getdvalue(netwt, "n"), getdvalue("0", "swt"),
                                                        getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                        getdvalue(sstoneamt, "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, "done", "done",o.getProductCode(),o.getCounterId(),o.getCounterName(),o.getTotPcs(),o.getTotMPcs(),o.getCategoryId(),o.getProductId(),o.getDesignId(),o.getPurityId());
                                                item.setImageUrl(simageurl);
                                                item.setDiamondClarity(ssku);
                                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                    nmap.put(item.getTidValue(), item);
                                                }

                                            } else {
                                                Itemmodel item = new Itemmodel(System.currentTimeMillis(), System.currentTimeMillis(), 0, 0,
                                                        tid, tid, getbvalue(branch), category, itemtype, purity,
                                                        "", "", "", "", "",
                                                        "", "", barcode, itemcode, sbox, "",
                                                        "", "", "Active", TransactionIDGenerator.generateTransactionNumber("A"),
                                                        "gsheet add", "",
                                                        "", "", "", "", "",
                                                        "", 0, 0, 0, 0, getdvalue(grosswt, "g"),
                                                        getdvalue(stonewt, "swt"), getdvalue(netwt, "swt"), getdvalue("0", "swt"),
                                                        getdvalue("0", "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                        getdvalue(sstoneamt, "swt"), getdvalue("0", "swt"), getdvalue("0", "swt"),
                                                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, 0, 0, 0, 0,
                                                        0, "done", "done","","","",0,0,0,0,0,0);
                                                item.setImageUrl(simageurl);
                                                item.setDiamondClarity(ssku);
                                                if (item.getTidValue().length() == 24 && item.getCategory() != null && !item.getCategory().isEmpty() && item.getProduct() != null && !item.getProduct().isEmpty()) {
                                                    nmap.put(item.getTidValue(), item);
                                                }
                                            }
                                        } else {
                                            Log.e("checking tid", "" + barcode);
                                        }
                                    }
                                }
//                                Log.e("check response", "First Name: " + tid + ", Last Name: " + category);
                            }
                            for (String key : ml.keySet()) {
                                // Check if the key exists in umap
                                if (!nmap.containsKey(key)) {
                                    // If the key does not exist in umap, add it to dmap
                                    dmap.add(ml.get(key));
                                }
                            }
                            Log.d("check items", "  " + ml.size() + "  " + nmap.values().toString() + "  " + dmap.size());
                            List<Itemmodel> itemlist = new ArrayList<>(nmap.values());

                            entryDatabase.checkdatabase(activity);
//                        deleteitems(entryDatabase, activity, dmap, app);
                            entryDatabase.makeentry(activity, itemlist, "excel", "product", app, issueitem, new SaveCallback() {

                                @Override
                                public void onSaveSuccess() {
//                                Toast.makeText(activity, "Item saved succesfully", Toast.LENGTH_SHORT).show();
//                                resetsstate();
                                    deleteitems(entryDatabase, activity, dmap, app);
                                    dialog.dismiss();

                                }

                                @Override
                                public void onSaveFailure(List<Itemmodel> failedItems) {
                                    Toast.makeText(activity, "Failed to save some items", Toast.LENGTH_SHORT).show();
                                    deleteitems(entryDatabase, activity, dmap, app);

                                    dialog.dismiss();
                                }
                            });
                        } catch (JSONException e) {
                            dialog.dismiss();
                            Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        dialog.dismiss();
                        Toast.makeText(activity, "failed to read data", Toast.LENGTH_SHORT).show();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Log.e("check error", errorMessage);
                        } else {
                            Log.e("check error", "Unknown error occurred.");
                        }
                    }
                });

        queue.add(stringRequest);
    }
}
