package com.loyalstring.interfaces;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.apiresponse.SkuResponse;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.modelclasses.StockVerificationFilterModelResponse;
import com.loyalstring.modelclasses.StockVerificationResponseNew;

import java.util.List;
import java.util.Map;

public class interfaces {

    public interface RetrieveDataListener1 {
        void onRetrieveData(Map<String, List<Itemmodel>>  resultList);
    }

    public interface PermissionCallback {
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
        void onPermissionGranted(String excelopen, Intent data);
    }


    public interface ItemUpdateListener {
        void onItemUpdated(Itemmodel updatedItem, double gwt, double swt, double nwt);
    }

    public interface Imagedownload{
        void onSaveSuccess(int scount);

        void onSaveFailure(int fcount);
    }


    public interface Fetchbills{
        void onFetched(List<Itemmodel> items);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }


    public interface OnSkusFetchedListener {
        void onSkusFetched(List<SkuResponse> skus);
    }

    public interface OnRFIDFetched {
        void onSuccess(List<Rfidresponse.ItemModel> result);
        void onError(Exception e);
    }


    public interface FetchAllRFIDData{
        void onSuccess(List<ScannedDataToService> result);
        void onError(Exception e);
    }


    public interface FetchAllVerificxationData{
        void onSuccess(StockVerificationFilterModelResponse result);
        void onError(Exception e);
    }

    public interface FetchAllVerificxationDataNew{
        void onSuccess(StockVerificationResponseNew result);
        void onError(Exception e);
    }



}
