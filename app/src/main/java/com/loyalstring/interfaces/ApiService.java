package com.loyalstring.interfaces;


import androidx.annotation.RawRes;

import com.loyalstring.Apis.ActivationResponse;
import com.loyalstring.Billmodels.Customersmodel;
import com.loyalstring.Billmodels.CustomersmodelItem;
import com.loyalstring.Billmodels.Onboardmodel;
import com.loyalstring.LatestApis.BillSupport.BillRequest;
import com.loyalstring.LatestApis.ProductRequestPayload;
import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.apiresponse.ClientCodeRequest;
import com.loyalstring.apiresponse.ProductResponse;
import com.loyalstring.apiresponse.ProductUpdate;
import com.loyalstring.apiresponse.ProductUpdateResponse;
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.apiresponse.SkuResponse;
import com.loyalstring.modelclasses.Productmodel;
import com.loyalstring.modelclasses.RequestBodyBill;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.modelclasses.StockVerificationFilterModel;
import com.loyalstring.modelclasses.StockVerificationFilterModelResponse;
import com.loyalstring.modelclasses.StockVerificationRequestData;
import com.loyalstring.modelclasses.StockVerificationResponseNew;
import com.loyalstring.modelclasses.jjjresponse;


import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {


    @POST("api/Device/RFIDDeviceLogin")
    Call<ActivationResponse> login(@Body RequestBody requestBody);
    @POST("api/ClientOnboarding/GetAllCustomer")
    Call<List<CustomersmodelItem>> getAllCustomers(@Body Onboardmodel request);

//    @GET("api/ProductMaster/GetfetchAllProduct")
//    Call<ProductResponse> getAllProducts();


    @GET("api/ProductMaster/FetchAllProducts")
    Call<ProductResponse> getAllProducts();

//    @GET("api/ProductMaster/GetAllRFID")
//    Call<Rfidresponse> getRfiddata();

    @POST("api/ProductMaster/GetAllRFID")
    Call<List<Rfidresponse.ItemModel>> getRfiddata(@Body ClientCodeRequest clientCodeRequest);


    @Headers("Content-Type: application/json")
    @POST("api/ProductMaster/UpdateGunProduct")
    Call<ProductUpdateResponse> updateProduct(@Body List<ProductUpdate> productUpdate);

    @POST("api/ProductMaster/GetAllLabeledStock")
    Call<List<AlllabelResponse.LabelItem>> getAlllableproducts(@Body ClientCodeRequest clientCodeRequest);

    @POST("api/ProductMaster/GetAllSKU")
    Call<List<SkuResponse>> getAllSKU(@Body ClientCodeRequest clientCodeRequest);

    @POST("callback/StockSell")
    Call<RequestBodyBill> uploadBill(@Body RequestBodyBill requestBody);


    @Multipart
    @POST("callback/StockSell")
    Call<jjjresponse> uploadBill(
            @Part("voucher_id") RequestBody voucherId,
            @Part("rfid_value") RequestBody rfidValue,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("user_id") RequestBody userId
    );


    @POST("ProductMaster/GetBatchedLabelledStock")
    Call<List<AlllabelResponse.LabelItem>> getBatchedLabelledStock(@Body ProductRequestPayload payload);


    @POST("Invoice/UpdateBarcodeItemStatus")
    Call<List<AlllabelResponse.LabelItem>> updateBarcodeItemStatus(@Body List<BillRequest> requestItems);

    @POST("api/RFIDDevice/AddRFID")
    Call<List<ScannedDataToService>> AddAllScannedData(@Body List<ScannedDataToService> scannedDataToService);


    @POST("api/ProductMaster/GetCombinedSV")
    Call<StockVerificationFilterModelResponse> stockVarification(@Body StockVerificationFilterModel stockVerificationFilterModel);


    /*stock verification new api*/
    @POST("api/ProductMaster/AddStockVerification")
    Call<StockVerificationResponseNew> stockVarificationNew(@Body StockVerificationRequestData stockVerificationRequestData);


}
