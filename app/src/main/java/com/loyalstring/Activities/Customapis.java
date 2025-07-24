package com.loyalstring.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.apiresponse.ClientCodeRequest;
import com.loyalstring.apiresponse.ProductResponse;
import com.loyalstring.database.StorageClass;
import com.loyalstring.databinding.ActivityCustomapisBinding;
import com.loyalstring.interfaces.ApiService;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Customapis extends AppCompatActivity {

    ActivityCustomapisBinding b;
    boolean verified = false;
    boolean rfidverified = false;
    String url, rfidurl;

    StorageClass storageClass;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_customapis);
        b = ActivityCustomapisBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        storageClass = new StorageClass(this);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Verifying please wait...");
        progressBar.setCanceledOnTouchOutside(false);
        String baseUrl =
                "https://goldstringwebapp.loyalstring.co.in/";
        // "http://43.241.147.162:9991/WS/";
//                "https://satyanarayanajewellers.loyalstring.co.in/" ;//"https://heerajewellers.loyalstring.co.in/";
        //storageClass.getBaseUrl();
        // "https://madanjewellers.loyalstring.co.in/";
        // "https://heerajewellers.loyalstring.co.in/";
        // "https://heerajewellers.loyalstring.co.in/";
        // https://testing.loyalstring.co.in/";
        // "https://thashnajewellery.loyalstring.co.in/";//
        b.apiurl.setText(baseUrl);

        b.rfidurl.setText
                ("https://goldstringwebapp.loyalstring.co.in/");
        //("https://navrangjewellers.loyalstring.co.in/");


        b.verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = b.apiurl.getText().toString().trim();

                if(url.isEmpty()){
                    Toast.makeText(Customapis.this, "please enter base url", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.show();
                //
                // only for navarang
                if (url.equalsIgnoreCase("https://goldstringwebapp.loyalstring.co.in/")) {
                    verifyurl(url);
                } else  {
                    try {
                        verifyurlold(url);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(Customapis.this, "Please Enter Proper URL", Toast.LENGTH_SHORT).show();
                        progressBar.dismiss();
                    }
                }


            }
        });
        b.seturl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verified){
                    storageClass.setbaseurl(url);
                    Toast.makeText(Customapis.this, "api successfull", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Customapis.this, "please verify api before setting", Toast.LENGTH_SHORT).show();
                }

            }
        });



        b.rfidverifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rfidurl = b.rfidurl.getText().toString().trim();

                if(rfidurl.isEmpty()){

                    Toast.makeText(Customapis.this, "please enter base url", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.show();

                verifyurl1(rfidurl);





            }
        });

        b.rfidseturl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(rfidverified){
                    storageClass.setrfidurl(rfidurl);
                    Toast.makeText(Customapis.this, "api successfull", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Customapis.this, "please verify api before setting", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void verifyurl1(String rfidurl) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(rfidurl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
//        ClientCodeRequest clientCodeRequest = new ClientCodeRequest("LS000008");
//        Call<Rfidresponse> call = apiService.getRfiddata(clientCodeRequest);
//        call.enqueue(new Callback<Rfidresponse>() {
//            @Override
//            public void onResponse(Call<Rfidresponse> call, Response<Rfidresponse> response) {
//                if(response.body().getData() == null){
//                    progressBar.dismiss();
//                    Toast.makeText(Customapis.this, "failed to verify api", Toast.LENGTH_SHORT).show();
//                }
//                if(response.isSuccessful()){
//                    progressBar.dismiss();
//                    Toast.makeText(Customapis.this, "url verified successfully", Toast.LENGTH_SHORT).show();
//                    rfidverified = true;
//                }else{
//                    progressBar.dismiss();
//                    Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Rfidresponse> call, Throwable t) {
//                progressBar.dismiss();
//                Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
//            }
//        });
    }



    private void verifyurl(String url) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        ClientCodeRequest clientCodeRequest = new ClientCodeRequest("LS000095");

        Call<List<AlllabelResponse.LabelItem>> call = apiService.getAlllableproducts(clientCodeRequest);
        call.enqueue(new Callback<List<AlllabelResponse.LabelItem>>() {
            @Override
            public void onResponse(Call<List<AlllabelResponse.LabelItem>> call, Response<List<AlllabelResponse.LabelItem>> response) {
                if(response.body() == null){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify api", Toast.LENGTH_SHORT).show();
                }
                Log.e("customapiresponse", "  "+response);
                if(response.isSuccessful()){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "url verified successfully", Toast.LENGTH_SHORT).show();
                    verified = true;
                }else{
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, " failed to verify", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AlllabelResponse.LabelItem>> call, Throwable t) {
                Log.d("customapi", "  "+t.getMessage());
                progressBar.dismiss();
                Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
            }
        });
        /*call.enqueue(new Callback<AlllabelResponse>() {
            @Override
            public void onResponse(Call<AlllabelResponse> call, Response<AlllabelResponse> response) {

                if(response.body() == null){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify api", Toast.LENGTH_SHORT).show();
                }
                Log.e("customapiresponse", "  "+response);
//                if(response.isSuccessful()){
//                    progressBar.dismiss();
//                    Toast.makeText(Customapis.this, "url verified successfully", Toast.LENGTH_SHORT).show();
                    verified = true;
//                }else{
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, " verify done", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(Call<AlllabelResponse> call, Throwable t) {
                Log.d("customapi", "  "+t.getMessage());
                progressBar.dismiss();
                Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
            }
        });*/
        /*call.enqueue(new Callback<AlllabelResponse>() {
            @Override
            public void onResponse(Call<AlllabelResponse> call, Response<AlllabelResponse> response) {
                Log.d("customapiresponse", "  "+response);
                if(response.body() == null){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify api", Toast.LENGTH_SHORT).show();
                }
                if(response.isSuccessful()){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "url verified successfully", Toast.LENGTH_SHORT).show();
                    verified = true;
                }else{
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AlllabelResponse> call, Throwable t) {
                Log.d("customapi", "  "+t.getMessage());
                progressBar.dismiss();
                Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
            }
        });*/


        /*call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                Log.d("customapiresponse", "  "+response.body());
                if(response.body().getData() == null){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify api", Toast.LENGTH_SHORT).show();
                }
                if(response.isSuccessful()){

                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "url verified successfully", Toast.LENGTH_SHORT).show();
                    verified = true;
                }else{
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d("customapi", "  "+t.getMessage());
                progressBar.dismiss();
                Toast.makeText(Customapis.this, "failed to verify", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    private void verifyurlold(String url) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<ProductResponse> call = apiService.getAllProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                Log.d("customapiresponse", "  "+response.body());
                if(response.body().getData() == null){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify api", Toast.LENGTH_SHORT).show();
                }
                if(response.isSuccessful()){
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "url verified successfully", Toast.LENGTH_SHORT).show();
                    verified = true;
                }else{
                    progressBar.dismiss();
                    Toast.makeText(Customapis.this, "failed to verify  "+response, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d("customapi", "  "+t.getMessage());
                progressBar.dismiss();
                Toast.makeText(Customapis.this, "failed to verify  "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}