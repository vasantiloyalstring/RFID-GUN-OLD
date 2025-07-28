package com.loyalstring.interfaces;

import com.loyalstring.modelclasses.SyncRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface DynamicSyncService {
    @POST
    Call<Void> sendSyncData(@Url String fullUrl, @Body SyncRequest request);
}