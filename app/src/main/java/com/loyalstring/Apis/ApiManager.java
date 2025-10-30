package com.loyalstring.Apis;

import android.util.Log;

import com.google.gson.Gson;
import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.apiresponse.ClientCodeRequest;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.apiresponse.SkuResponse;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.ScanSessionResponse;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.modelclasses.StockVerificationFilterModel;
import com.loyalstring.modelclasses.StockVerificationFilterModelResponse;
import com.loyalstring.modelclasses.StockVerificationRequestData;
import com.loyalstring.modelclasses.StockVerificationResponseNew;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {


    private ApiService apiService;

    public ApiManager(ApiService apiService) {
        this.apiService = apiService;
    }

    public void fetchAllSKU(String clientCode, interfaces.ApiCallback<List<SkuResponse>> callback) {
        new Thread(() -> {
            try {
                Call<List<SkuResponse>> call = apiService.getAllSKU(new ClientCodeRequest(clientCode));
                Response<List<SkuResponse>> response = call.execute();
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error fetching SKU: " + response.errorBody().string()));
                }
            } catch (IOException e) {
                callback.onError(e);
            }
        }).start();
    }

    public void fetchAllLabeledStock(String clientCode, interfaces.ApiCallback<List<AlllabelResponse.LabelItem>> callback) {
        new Thread(() -> {
            try {
                Call<List<AlllabelResponse.LabelItem>> call = apiService.getAlllableproducts(new ClientCodeRequest(clientCode));
                Response<List<AlllabelResponse.LabelItem>> response = call.execute();
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error fetching Labeled Stock: " + response.errorBody().string()));
                }
            } catch (IOException e) {
                callback.onError(e);
            }
        }).start();
    }


    public void fetchallrfid(String clientcode, interfaces.OnRFIDFetched onRFIDFetched) {
        new Thread(() -> {
            try {
                ClientCodeRequest clientCodeRequest = new ClientCodeRequest(clientcode);
                Call<List<Rfidresponse.ItemModel>> call = apiService.getRfiddata(clientCodeRequest);
                Response<List<Rfidresponse.ItemModel>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    // Convert the list to JSON or any other format if required for onSuccess
                    onRFIDFetched.onSuccess(response.body());
                } else {
                    onRFIDFetched.onError(new Exception("Error fetching Labeled Stock: " + response.errorBody().string()));
                }
            } catch (IOException e) {
                onRFIDFetched.onError(e);
            }
        }).start();
    }

    public void addAllScannedData(List<ScannedDataToService> scannedDataToService, interfaces.FetchAllRFIDData fetchAllRFIDData) {
        // Create a defensive copy of the list to prevent concurrent modifications
        List<ScannedDataToService> safeCopy = new ArrayList<>(scannedDataToService);

        new Thread(() -> {
            try {
                Call<List<ScannedDataToService>> call = apiService.AddAllScannedData(safeCopy);
                Response<List<ScannedDataToService>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    fetchAllRFIDData.onSuccess(response.body());
                } else {
                    String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                    fetchAllRFIDData.onError(new Exception("Error fetching Labeled Stock: " + errorMsg));
                }
            } catch (IOException e) {
                fetchAllRFIDData.onError(e);
            }
        }).start();
    }

    /* old stock varification*/
/*    public void stockVarificationDataDataNew(StockVerificationRequestData stockVerificationRequestData, interfaces.FetchAllVerificxationDataNew fetchAllRFIDData) {
        // Create a defensive copy of the list to prevent concurrent modifications
        new Thread(() -> {
            try {
                Call<StockVerificationResponseNew> call = apiService.stockVarificationNew(stockVerificationRequestData);
                Response<StockVerificationResponseNew> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    fetchAllRFIDData.onSuccess(response.body());
                } else {
                    String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                    fetchAllRFIDData.onError(new Exception("Error fetching Labeled Stock: " + errorMsg));
                }
            } catch (IOException e) {
                fetchAllRFIDData.onError(e);
            }
        }).start();
    }*/

    public void stockVarificationDataDataNew(
            StockVerificationRequestData stockVerificationRequestData,
            interfaces.FetchAllVerificxationDataNew fetchAllRFIDData
    ) {
        //Call<ScanSessionResponse> call = apiService.stockVarificationNew(stockVerificationRequestData);

      /*  call.enqueue(new Callback<ScanSessionResponse>() {
            @Override
            public void onResponse(Call<ScanSessionResponse> call, Response<ScanSessionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("@@","data found");
                    fetchAllRFIDData.onSuccess(response.body());
                } else {
                    try {
                        Log.d("@@","not data found");
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        fetchAllRFIDData.onError(new Exception("Error fetching Labeled Stock: " + errorMsg));
                    } catch (IOException e) {
                        Log.d("@@","not data found111");
                        fetchAllRFIDData.onError(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ScanSessionResponse> call, Throwable t) {
                Log.e("@@ NETWORK FAIL", "Exception: " + t.getClass().getSimpleName() + " | Message: " + t.getMessage());
                if (t instanceof SocketTimeoutException) {
                    Log.e("NETWORK", "READ TIMEOUT — server took too long to send data");
                } else if (t instanceof ConnectException) {
                    Log.e("NETWORK", "CONNECT TIMEOUT — server didn’t accept the connection");
                } else {
                    Log.e("NETWORK", "OTHER ERROR: " + t);
                }
                fetchAllRFIDData.onError(new Exception("Network call failed: " + t.getMessage(), t));
            }
        });*/

        Gson gson = new Gson();

// Convert to JSON once
        String json = gson.toJson(stockVerificationRequestData);

// Create a streaming request body
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json; charset=utf-8")
        );

// Now make the call
        Call<ResponseBody> call = apiService.stockVarificationNew(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try (Reader reader = new InputStreamReader(response.body().byteStream())) {
                        ScanSessionResponse parsed = gson.fromJson(reader, ScanSessionResponse.class);
                        fetchAllRFIDData.onSuccess(parsed);
                    } catch (Exception e) {
                        fetchAllRFIDData.onError(e);
                    }
                } else {
                    fetchAllRFIDData.onError(new Exception("HTTP " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("NETWORK FAIL", t.toString());
                fetchAllRFIDData.onError(new Exception("Network call failed: " + t.getMessage(), t));
            }
        });

    }



}
