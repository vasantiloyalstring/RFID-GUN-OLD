// Imports remain the same
package com.loyalstring.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loyalstring.Adapters.ScannedDataAdapter;
import com.loyalstring.Apis.ApiManager;
import com.loyalstring.Apis.RetrofitClient;
import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.apiresponse.AlllabelResponse;
import com.loyalstring.fsupporters.Globalcomponents;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.DynamicSyncService;
import com.loyalstring.interfaces.ScanDataCallback;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.modelclasses.SyncRequest;
import com.loyalstring.network.NetworkUtils;
import com.loyalstring.readersupport.KeyDwonFragment;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.barcode.BarcodeFactory;
import com.rscja.deviceapi.entity.BarcodeEntity;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.*;

import okhttp3.OkHttpClient;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class Stocktransferfragment extends KeyDwonFragment {

    private MainActivity mainActivity;
    private Handler handler;
    private TextView singletext, tvTotalItems;
    private EditText editBoxName;
    private LinearLayout layoutScan, layoutSync, singlereset;
    private RecyclerView recyclerView;
    private ImageView singleimage;
    private Button btnScanBox;
    private ScannedDataAdapter adapter;

    private SharedPreferencesManager sharedPreferencesManager;
    private ApiService apiService;
    private ApiManager apiManager;
    private NetworkUtils networkUtils;
    public BarcodeDecoder barcodeDecoder = BarcodeFactory.getInstance().getBarcodeDecoder();
    public KeyDwonFragment currentFragment=null;

    private final List<ScannedDataToService> scannedList = new ArrayList<>();
    private final List<String> scannedEpcList = new ArrayList<>();
    private final List<AlllabelResponse.LabelItem> labelledStockList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocktransferfragment, container, false);

        mainActivity = (MainActivity) getActivity();
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        apiManager = new ApiManager(apiService);
        mainActivity.currentFragment = Stocktransferfragment.this;


        layoutScan = view.findViewById(R.id.lay_scan_products);
        layoutSync = view.findViewById(R.id.layout_sync);
        singlereset = view.findViewById(R.id.singlereset);
        singleimage = view.findViewById(R.id.singleimage);
        singletext = view.findViewById(R.id.singletext);
        tvTotalItems = view.findViewById(R.id.tv_total_items);


        btnScanBox = view.findViewById(R.id.btn_scan_box);
        editBoxName = view.findViewById(R.id.ed_boxName);
        recyclerView = view.findViewById(R.id.recyclerView_scanneddata);

        adapter = new ScannedDataAdapter(scannedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Stock Transfer");
        }

        fetchLabelledStockListFromApi();

        handler = new Handler(msg -> {
            UHFTAGInfo tagInfo = (UHFTAGInfo) msg.obj;
            if (tagInfo != null) {
                String epc = tagInfo.getEPC();
                if (!scannedEpcList.contains(epc)) {
                    scannedEpcList.add(epc);
                    mainActivity.playSound(1);
                    ScannedDataToService item = findProductByEPC(epc);
                    if (item != null) {
                        requireActivity().runOnUiThread(() -> {
                            scannedList.add(item);
                            adapter.notifyDataSetChanged();
                            tvTotalItems.setText("Total items: " + scannedList.size());
                        });
                    }
                }
            }
            return true;
        });

        layoutScan.setOnClickListener(v -> startOrStopScan());
        layoutSync.setOnClickListener(view1 -> syncToServer(scannedList, success -> {

            requireActivity().runOnUiThread(() -> {
                if (success) {
                    startOrStopScan();
                    Toast.makeText(getContext(), "Stock transfer successful", Toast.LENGTH_SHORT).show();
                    editBoxName.setText("");
                    scannedList.clear();
                    scannedEpcList.clear();
                    labelledStockList.clear();
                    adapter.notifyDataSetChanged();
                    tvTotalItems.setText("Total items: 0");
                    fetchLabelledStockListFromApi();
                } else {
                    Toast.makeText(getContext(), "Stock transfer failed", Toast.LENGTH_SHORT).show();
                }
            });
        }));
        btnScanBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.barcodeDecoder.startScan();

                mainActivity.barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
                    @Override
                    public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                        if (barcodeEntity != null && barcodeEntity.getBarcodeData() != null){
                            Log.e("BARCODE", barcodeEntity.getBarcodeData());
                            if(!mainActivity.mReader.isInventorying()){
                                editBoxName.setText(barcodeEntity.getBarcodeData());

                            }

                        }

                    }
                });
            }
        });

        singlereset.setOnClickListener(v -> {
            stopScanner();
            editBoxName.setText("");
            scannedList.clear();
            scannedEpcList.clear();
            labelledStockList.clear();
            adapter.notifyDataSetChanged();
            tvTotalItems.setText("Total items: 0");

            fetchLabelledStockListFromApi();
        });

        return view;
    }

    private void fetchLabelledStockListFromApi() {
        Clients clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();
        String clientCode = clients.getClientCode();

        if (clientCode != null && !clientCode.isEmpty()) {
            apiManager.fetchAllLabeledStock(clientCode, new com.loyalstring.interfaces.interfaces.ApiCallback<List<AlllabelResponse.LabelItem>>() {
                @Override
                public void onSuccess(List<AlllabelResponse.LabelItem> result) {
                    requireActivity().runOnUiThread(() -> {
                        labelledStockList.clear();
                        labelledStockList.addAll(result);
                    });
                }

                @Override
                public void onError(Exception e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to fetch Labeled Stock", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        }
    }

    private void startOrStopScan() {
        if (mainActivity == null || mainActivity.mReader == null) return;

        mainActivity.mReader.setPower(mainActivity.mReader.getPower());

        if (mainActivity.mReader.startInventoryTag()) {
            editBoxName.setEnabled(false);
            singletext.setText("Stop Scan");
            singleimage.setImageResource(R.drawable.ic_cancelblack);
            new TagThread().start();
        } else {
            stopScanner();
            editBoxName.setEnabled(true);

            singletext.setText("Scan");
            singleimage.setImageResource(R.drawable.ic_scanblack);
        }
    }

    private void stopScanner() {
        if (mainActivity != null && mainActivity.mReader != null) {
            mainActivity.mReader.stopInventory();
        }
    }

    private class TagThread extends Thread {
        @Override
        public void run() {
            while (mainActivity != null && mainActivity.mReader != null && mainActivity.mReader.isInventorying()) {
                UHFTAGInfo tagInfo = mainActivity.mReader.readTagFromBuffer();
                if (tagInfo != null) {
                    Message msg = handler.obtainMessage();
                    msg.obj = tagInfo;
                    handler.sendMessage(msg);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            requireActivity().runOnUiThread(() -> {
                singletext.setText("Scan");
                singleimage.setImageResource(R.drawable.ic_scanblack);
            });
        }
    }

    @SuppressLint("HardwareIds")
    private ScannedDataToService findProductByEPC(String epc) {
        for (AlllabelResponse.LabelItem labelItem : labelledStockList) {
            String itemCode = labelItem.getItemCode();
            if (itemCode != null && !itemCode.isEmpty()) {
                String hexCode = convertToHex(itemCode);
                if (epc.equalsIgnoreCase(hexCode)) {
                    ScannedDataToService item = new ScannedDataToService();
                    item.setTIDValue(epc);
                    item.setRFIDCode(hexCode);
                    item.setItemCode(itemCode);

                    String androidId = Settings.Secure.getString(
                            getActivity().getContentResolver(),
                            Settings.Secure.ANDROID_ID
                    );
                    Clients clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();
                    String clientCode = clients.getClientCode();

                    LocalDateTime currentDateTime = LocalDateTime.now();
                    String formatted = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

                    item.setDeviceId(androidId);
                    item.setCreatedOn(formatted);
                    item.setLastUpdated(formatted);
                    item.setStatusType(true);
                    item.setId(0);
                    item.setClientCode(clientCode);

                    return item;
                }
            }
        }
        return null;
    }

    private void syncToServer(List<ScannedDataToService> scannedList, ScanDataCallback callback) {
        String url = sharedPreferencesManager.getStockTransferUrl();
        if (url.isEmpty()) {
            Toast.makeText(getContext(), "Please set Stock Transfer URL first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editBoxName.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please scan box name first", Toast.LENGTH_SHORT).show();
            return;
        }

        List<SyncRequest.ProductEntry> items = new ArrayList<>();
        for (ScannedDataToService item : scannedList) {
            items.add(new SyncRequest.ProductEntry(item.getRFIDCode(), item.getItemCode()));
        }

        SyncRequest request = new SyncRequest("success", editBoxName.getText().toString(), items);
        OkHttpClient client = getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummy.url/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        if (!url.endsWith("/")) url += "/";
        DynamicSyncService service = retrofit.create(DynamicSyncService.class);
        Call<Void> call = service.sendSyncData(url, request);

        Log.e("SYNC_URL", url);
        Log.e("SYNC_REQ", new GsonBuilder().setPrettyPrinting().create().toJson(request));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                callback.onResult(true);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SYNC_FAILED", t.getMessage());
                callback.onResult(false);
            }
        });
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToHex(String input) {
        StringBuilder hexBuilder = new StringBuilder();
        for (char ch : input.toCharArray()) {
            hexBuilder.append(String.format("%02X", (int) ch));
        }
        while (hexBuilder.length() % 4 != 0) {
            hexBuilder.insert(0, "00");
        }
        return hexBuilder.toString();
    }

    @Override
    public void myOnKeyDwon(String process) {
       // super.myOnKeyDwon(process);
        Log.e("PROCESS :",process);
        if (process.equalsIgnoreCase("scan")) {
            layoutScan.performClick();
        } else {
            btnScanBox.performClick();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanner();
    }


}
