package com.loyalstring.Apis;

import android.util.Log;

import com.google.gson.Gson;
import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.apiresponse.ClientCodeRequest;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.apiresponse.SkuResponse;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.ClearStockDataModelReq;
import com.loyalstring.modelclasses.ClearStockDataModelResponse;
import com.loyalstring.modelclasses.ScanSessionResponse;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.modelclasses.Item;
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

/*    public void stockVarificationDataDataNew(StockVerificationRequestData stockVerificationRequestData, interfaces.FetchAllVerificxationDataNew fetchAllRFIDData) {
        // Create a defensive copy of the list to prevent concurrent modifications
        new Thread(() -> {
            try {
                Call<ScanSessionResponse> call = apiService.stockVarificationNew(stockVerificationRequestData);
                Response<ScanSessionResponse> response = call.execute();

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
            interfaces.FetchAllVerificxationDataNew fetchAllRFIDData) {

        new Thread(() -> {
            try {

                // 1️⃣ Full item list
                List<Item> fullList = new ArrayList<>(stockVerificationRequestData.getItems());

                if (fullList == null || fullList.isEmpty()) {
                    fetchAllRFIDData.onError(new Exception("Item list is empty"));
                    return;
                }

                // 2️⃣ Create safe batches
                int batchSize = 500;
                List<List<Item>> batches = new ArrayList<>();

                for (int i = 0; i < fullList.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, fullList.size());

                    // ❗ Copy into NEW ArrayList (IMPORTANT)
                    List<Item> batchCopy = new ArrayList<>(fullList.subList(i, end));

                    batches.add(batchCopy);
                }

                ScanSessionResponse lastResponse = null;

                // 3️⃣ Upload each batch one-by-one
                for (int i = 0; i < batches.size(); i++) {

                    List<Item> singleBatch = batches.get(i);

                    // Prepare request for this batch
                    StockVerificationRequestData batchRequest =
                            new StockVerificationRequestData(
                                    stockVerificationRequestData.getClientCode(),
                                    singleBatch
                            );

                    // Call API
                    Call<ScanSessionResponse> call = apiService.stockVarificationNew(batchRequest);
                    Response<ScanSessionResponse> response = call.execute();

                    if (!response.isSuccessful() || response.body() == null) {
                        String errorMsg = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Unknown error";

                        fetchAllRFIDData.onError(
                                new Exception("Batch " + (i + 1) + " failed: " + errorMsg)
                        );

                        return;
                    }

                    lastResponse = response.body();
                }

                // 4️⃣ All batches successful
                fetchAllRFIDData.onSuccess(lastResponse);

            } catch (Exception e) {
                fetchAllRFIDData.onError(e);
            }
        }).start();
    }

    public void clearStockDataNew(
            ClearStockDataModelReq req,
            interfaces.FetchClearStockData callback
    ) {
        new Thread(() -> {
            try {
                Call<ClearStockDataModelResponse> call = apiService.clearStockData(req);
                Response<ClearStockDataModelResponse> response = call.execute();

                if (!response.isSuccessful() || response.body() == null) {
                    String errorMsg = response.errorBody() != null
                            ? response.errorBody().string()
                            : "Unknown error";

                    callback.onError(new Exception("Clear API failed: " + errorMsg));
                    return;
                }

                callback.onSuccess(response.body());

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }



}
