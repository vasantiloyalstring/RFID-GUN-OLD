package com.loyalstring.Apis;

import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.apiresponse.ClientCodeRequest;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.apiresponse.SkuResponse;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.modelclasses.StockVerificationFilterModel;
import com.loyalstring.modelclasses.StockVerificationFilterModelResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
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

    /*stock varification*/
    public void stockVarificationDataData(StockVerificationFilterModel stockVerificationFilterModel, interfaces.FetchAllVerificxationData fetchAllRFIDData) {
        // Create a defensive copy of the list to prevent concurrent modifications
        new Thread(() -> {
            try {
                Call<StockVerificationFilterModelResponse> call = apiService.stockVarification(stockVerificationFilterModel);
                Response<StockVerificationFilterModelResponse> response = call.execute();

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




}
