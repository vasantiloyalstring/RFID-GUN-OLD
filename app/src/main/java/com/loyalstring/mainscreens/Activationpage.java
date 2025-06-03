package com.loyalstring.mainscreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.datatransport.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loyalstring.Apis.ActivationResponse;
import com.loyalstring.LatestApis.AuthRepository;
import com.loyalstring.LatestApis.LoginResponse;
import com.loyalstring.LatestCallBacks.ActivationCallback;
import com.loyalstring.LatestDatabase.LoginDbHelper;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.database.StorageClass;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.network.NetworkUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activationpage extends AppCompatActivity {
    private ApiService apiService;
    String sname, semail, saddress, sphone, sserial, sbuiltnumber, devicestate;
    String phone;
    ProgressDialog progressDialog;
    Button activatebtn;
    NetworkUtils networkUtils;
    StorageClass storageClass;
    EditText ephone;
    private AuthRepository authRepository;

    LoginDbHelper loginDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activationpage);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        authRepository = new AuthRepository();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Activating Device...");
        progressDialog.setCanceledOnTouchOutside(false);
        networkUtils = new NetworkUtils(this);
        storageClass = new StorageClass(this);
        loginDbHelper = new LoginDbHelper(this);


        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        EditText passwordBox = findViewById(R.id.passwordBox);

        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordBox.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.g_visible); // Change to visible icon
                } else {
                    passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.g_invisible); // Change to hidden icon
                }
                passwordBox.setSelection(passwordBox.length()); // Move cursor to end
            }
        });

        Button loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(this::performLogin);

    }

    private void performLogin(View view) {
        EditText emailBox = findViewById(R.id.emailBox);
        EditText passwordBox = findViewById(R.id.passwordBox);

        String username = emailBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Validate email and password
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!networkUtils.isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Perform login
        authRepository.login(username, password, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Dismiss the progress dialog
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful login

                    LoginResponse loginResponse = response.body();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.body());
                    Log.e("authservice ", "login "+response.body());
                    // Log the JSON string
                    Log.d("Full JSON Response", ""+loginResponse);

                    if(loginResponse.getEmployee() == null){
                        Toast.makeText(Activationpage.this, "Please check Username and Password", Toast.LENGTH_SHORT).show();
                    return;
                    }
                    if(loginResponse.getEmployee().getClients() == null){
                        Toast.makeText(Activationpage.this, "Please Check Username and Passwordzzz", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Log.e("check response ", "check " + response.body().getEmployee().toString());

                    String rfid = loginResponse.getEmployee().getClients().getRfidType();

                    if (rfid == null || rfid.isEmpty()) {
                        Toast.makeText(Activationpage.this, "rfid not enabled ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Save data to SharedPreferences using the LoginResponse object
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(Activationpage.this);

                    loginDbHelper.ClientOnboarding(loginResponse, new ActivationCallback() {
                        @Override
                        public void onSaveSuccess() {
                            storageClass.setActivationStatus(true, "", "", "", "", "");
                            sharedPreferencesManager.saveLoginData(loginResponse);

                            Intent go = new Intent(Activationpage.this, Loginpage.class);
                            startActivity(go);
                            finish();
                            // You can navigate to the next activity here
                            Toast.makeText(Activationpage.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String error) {
                            Toast.makeText(Activationpage.this, "Login faield " + error, Toast.LENGTH_SHORT).show();

                        }
                    });


                } else {
                    // Handle login failure
                    Toast.makeText(Activationpage.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Dismiss the progress dialog
                progressDialog.dismiss();
                // Handle error
                Toast.makeText(Activationpage.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkgun(String sbuilt, String ph) {

//        String deviceModel = activationResponse.getDeviceModel();
        String mobileNo = "9999999999";
        String adate = "03-09-2024";
        String ddate = "03-09-2024";
//        String DeviceBuildNo = activationResponse.getDeviceBuildNo();
        String serial = "";
        String dstate = "activated";

        if (dstate == null || dstate.equalsIgnoreCase("locked") || mobileNo == null || mobileNo.isEmpty() || adate == null || adate.isEmpty() || ddate == null || ddate.isEmpty() || serial == null || serial.isEmpty()) {

            progressDialog.dismiss();
            Toast.makeText(Activationpage.this, "no data found...", Toast.LENGTH_SHORT).show();

//                                return;
        }

        storageClass.setActivationStatus(true, adate, ddate, "1", mobileNo, serial);


        Intent go = new Intent(Activationpage.this, Loginpage.class);
        startActivity(go);
        finish();

        // Continue extracting other fields as needed
        progressDialog.dismiss();
        Toast.makeText(Activationpage.this, "activated", Toast.LENGTH_SHORT).show();

    }

    private void checkgun1(String sbuilt, String ph) {
//        try {
        if (sbuiltnumber == null || sbuiltnumber.isEmpty()) {
            Toast.makeText(this, "something went wrong activation...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        Log.d("checkbuilt", " " + sbuiltnumber + "  " + ph);
        try {
            // Make API call
//            LoginRequest loginRequest = new LoginRequest(sbuiltnumber,ph );

            String baseUrl = "https://raniwalajewellers.loyalstring.co.in/";
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

// Prepare the JSON request body
            String json = "{\"DeviceBuildNo\":\"" + sbuiltnumber + "\",\"MobileNo\":\"" + ph + "\"}";

//            String json = "{\"DeviceBuildNo\":\"c72e_MT6763_V1.3_EU_GIT88fca5d3b8_202008131511\",\"MobileNo\":\"8806588921\"}";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);


            // Make the API call
            Call<ActivationResponse> call = apiService.login(requestBody);
            call.enqueue(new Callback<ActivationResponse>() {
                @Override
                public void onResponse(Call<ActivationResponse> call, Response<ActivationResponse> response) {
                    if (response.isSuccessful()) {
                        // Handle successful login response
                        ActivationResponse activationResponse = response.body();
                        Log.d("check response ", "" + response);
                        Log.d("check device", "Response body: " + activationResponse.toString());
                        if (activationResponse != null) {
                            String deviceModel = activationResponse.getDeviceModel();
                            String mobileNo = activationResponse.getMobileNo();
                            String adate = activationResponse.getDeviceActivationDate();
                            String ddate = activationResponse.getDeviceDeactivationDate();
                            String DeviceBuildNo = activationResponse.getDeviceBuildNo();
                            String serial = activationResponse.getDeviceSerialNo();
                            String dstate = activationResponse.getDeviceStatus();

                            if (dstate == null || dstate.equalsIgnoreCase("locked") || mobileNo == null || mobileNo.isEmpty() || adate == null || adate.isEmpty() || ddate == null || ddate.isEmpty() || serial == null || serial.isEmpty()) {

                                progressDialog.dismiss();
                                Toast.makeText(Activationpage.this, "no data found...", Toast.LENGTH_SHORT).show();

//                                return;
                            }

                            storageClass.setActivationStatus(true, adate, ddate, "1", mobileNo, serial);


                            Intent go = new Intent(Activationpage.this, Loginpage.class);
                            startActivity(go);
                            finish();

                            // Continue extracting other fields as needed
                            progressDialog.dismiss();
                            Toast.makeText(Activationpage.this, "activated", Toast.LENGTH_SHORT).show();
                            // Proceed with your logic
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Activationpage.this, "something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle unsuccessful login response
                        progressDialog.dismiss();
                        Toast.makeText(Activationpage.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ActivationResponse> call, Throwable t) {
                    // Handle login failure
                    progressDialog.dismiss();
                    Toast.makeText(Activationpage.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.e("checkacerror", "  " + t.getMessage());

                }
            });

        } catch (
                Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }

    }

}