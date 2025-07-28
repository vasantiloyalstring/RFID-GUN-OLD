package com.loyalstring.fragments;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
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
import com.loyalstring.apiresponse.Rfidresponse;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.DynamicSyncService;
import com.loyalstring.interfaces.ScanDataCallback;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.ScannedDataToService;
import com.loyalstring.modelclasses.SyncRequest;
import com.loyalstring.network.NetworkUtils;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.barcode.BarcodeFactory;
import com.rscja.deviceapi.entity.BarcodeEntity;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Stocktransferfragment extends Fragment {

    private MainActivity mainActivity;
    private Handler handler;
    private TextView singletext;
    private EditText editBoxName;
    private Button btnScanBox;
    private LinearLayout layoutScan,layoutSync,singlereset;
    private RecyclerView recyclerView;
    private ImageView singleimage;
    private ScannedDataAdapter adapter;

    private SharedPreferencesManager sharedPreferencesManager;
    private ApiService apiService;
    private ApiManager apiManager;
    private NetworkUtils networkUtils;
    public BarcodeDecoder barcodeDecoder = BarcodeFactory.getInstance().getBarcodeDecoder();

    private final List<ScannedDataToService> scannedList = new ArrayList<>();
    private final List<String> scannedEpcList = new ArrayList<>();
    private final List<Rfidresponse.ItemModel> rfidList = new ArrayList<>();
    private final List<AlllabelResponse.LabelItem> labelledStockList = new ArrayList<>();
    private final List<Pair<String, String>> itemCodeToRfidMap = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocktransferfragment, container, false);

        mainActivity = (MainActivity) getActivity();
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        apiManager = new ApiManager(apiService);
        networkUtils = new NetworkUtils(getActivity());

        layoutSync = view.findViewById(R.id.layout_sync);
        layoutScan = view.findViewById(R.id.lay_scan_products);
        singlereset = view.findViewById(R.id.singlereset);
        singleimage = view.findViewById(R.id.singleimage);
        singletext = view.findViewById(R.id.singletext);

        btnScanBox = view.findViewById(R.id.btn_scan_box);
        editBoxName = view.findViewById(R.id.ed_boxName);
        recyclerView = view.findViewById(R.id.recyclerView_scanneddata);
        recyclerView = view.findViewById(R.id.recyclerView_scanneddata);

        adapter = new ScannedDataAdapter(scannedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Stock Transfer");

            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }

        fetchRFIDListFromApi();
        fetchLabelledStockListFromApi();

        handler = new Handler(msg -> {
            UHFTAGInfo tagInfo = (UHFTAGInfo) msg.obj;
            if (tagInfo != null) {
                String epc = tagInfo.getEPC();
                if (!scannedEpcList.contains(epc)) {
                    scannedEpcList.add(epc);
                    Log.d("SCAN", "EPC Collected: " + epc);
                    mainActivity.playSound(1);
                }
            }
            return true;
        });

        layoutScan.setOnClickListener(v -> startOrStopScan());

        layoutSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addScanDatatoWeb(scannedList, success -> {
                    if (success) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Stock transfer successful", Toast.LENGTH_SHORT).show();
                            scannedList.clear();
                            adapter.notifyDataSetChanged();
                        });
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Stock transfer failed", Toast.LENGTH_SHORT).show()
                        );
                    }
                });

            }
        });
        btnScanBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.barcodeDecoder.startScan();

                mainActivity.barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
                    @Override
                    public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                        if (barcodeEntity != null){
                            Log.e("BARCODE", barcodeEntity.getBarcodeData());
                            editBoxName.setText(barcodeEntity.getBarcodeData());

                        }

                    }
                });
            }
        });

        singlereset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rfidList.clear();
                labelledStockList.clear();
                scannedList.clear();
            }
        });

        return view;
    }


    private void fetchAllBranches(){


    }

    private void fetchRFIDListFromApi() {
        Clients clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();
        String clientCode = clients.getClientCode();

        if (clientCode != null && !clientCode.isEmpty()) {
            apiManager.fetchallrfid(clientCode, new interfaces.OnRFIDFetched() {
                @Override
                public void onSuccess(List<Rfidresponse.ItemModel> result) {
                    rfidList.clear();
                    rfidList.addAll(result);
                    tryBuildItemCodeRfidMapping();
                }

                @Override
                public void onError(Exception e) {
                    Log.e("API", "Failed to fetch RFID list", e);
                }
            });
        }
    }

    private void fetchLabelledStockListFromApi() {
        Clients clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();
        String clientCode = clients.getClientCode();

        if (clientCode != null && !clientCode.isEmpty()) {
            apiManager.fetchAllLabeledStock(clientCode, new interfaces.ApiCallback<List<AlllabelResponse.LabelItem>>() {
                @Override
                public void onSuccess(List<AlllabelResponse.LabelItem> result) {
                    requireActivity().runOnUiThread(() -> {
                        labelledStockList.clear();
                        labelledStockList.addAll(result);
                        tryBuildItemCodeRfidMapping();
                    });
                }

                @Override
                public void onError(Exception e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to fetch Labeled Stock", LENGTH_SHORT).show()
                    );
                }
            });
        }
    }

    private void startOrStopScan() {
        if (mainActivity == null || mainActivity.mReader == null) return;

        mainActivity.mReader.setPower(30);
        if (mainActivity.mReader.startInventoryTag()) {
            singletext.setText("Stop Scan");
            singleimage.setImageResource(R.drawable.ic_cancelblack);
            new TagThread().start();
        } else {
            stopScanner();
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
                singletext.setText("Scan Box");
             //   singletext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_scanblack, 0, 0, 0);
                processScannedEpcs();
            });
        }
    }

    private void processScannedEpcs() {
        scannedList.clear();
        for (String epc : scannedEpcList) {
            ScannedDataToService item = findProductByEPC(epc);
            if (item != null) {
                scannedList.add(item);
            } else {
                Log.w("RFID", "Unmapped EPC: " + epc);
            }
        }
        adapter.notifyDataSetChanged();
        scannedEpcList.clear();
    }

    @SuppressLint("HardwareIds")
    private ScannedDataToService findProductByEPC(String epc) {


        for (Rfidresponse.ItemModel rfidItem : rfidList) {
            if (epc.equalsIgnoreCase(rfidItem.getTid())) {
                String barcode = rfidItem.getBarcodeNumber();
                for (Pair<String, String> map : itemCodeToRfidMap) {
                    if (map.second.equalsIgnoreCase(barcode)) {
                        String itemCode = map.first;
                        ScannedDataToService item = new ScannedDataToService();
                        item.setTIDValue(epc);
                        item.setRFIDCode(barcode);
                        item.setItemCode(itemCode);
                        Clients clients = sharedPreferencesManager.readLoginData().getEmployee().getClients();
                        String clientCode = clients.getClientCode();
                        String androidId="";
                        Log.e("check body client code", "  " + clientCode);
                        if (clientCode != null || !clientCode.isEmpty()) {
                            androidId = Settings.Secure.getString(
                                    getActivity().getContentResolver(),
                                    Settings.Secure.ANDROID_ID
                            );
                        }
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
        }
        return null;
    }

    private void tryBuildItemCodeRfidMapping() {
        if (!rfidList.isEmpty() && !labelledStockList.isEmpty()) {
            itemCodeToRfidMap.clear();
            for (AlllabelResponse.LabelItem labelItem : labelledStockList) {
                String itemCode = labelItem.getItemCode();
                String rfid = labelItem.getrFIDCode();
                if (itemCode != null && rfid != null) {
                    itemCodeToRfidMap.add(new Pair<>(itemCode, rfid));
                }
            }
        }
    }

    private void addScanDatatoWeb(List<ScannedDataToService> scannedList, ScanDataCallback callback) {
        if (networkUtils.isNetworkAvailable()) {
            apiManager.addAllScannedData(scannedList, new interfaces.FetchAllRFIDData() {
                @Override
                public void onSuccess(List<ScannedDataToService> result) {
                    boolean isSuccess = result != null && !result.isEmpty();
                    Log.e("RfidListCheck", "Rfid Scanned data: " + result.size());
                    callback.onResult(isSuccess);
                }

                @Override
                public void onError(Exception e) {
                    callback.onResult(false);
                }
            });
        } else {
            callback.onResult(false);
        }
    }


    private void syncToServer(List<ScannedDataToService> scannedList) {
        String url = sharedPreferencesManager.getStockTransferUrl();
        if (url.isEmpty()) {
            Toast.makeText(getContext(), "Please set Stock Transfer URL first.", LENGTH_SHORT).show();
            return;
        }

        List<SyncRequest.ProductEntry> items = new ArrayList<>();
        for (ScannedDataToService item : scannedList) {
            items.add(new SyncRequest.ProductEntry(item.getRFIDCode(), item.getItemCode()));
        }

        SyncRequest request = new SyncRequest("success", editBoxName.getText().toString(), items);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummy.url/")  // unused dummy base
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String fullUrl = sharedPreferencesManager.getStockTransferUrl(); // e.g., https://sapphirejewelryny.com/RFID
        if (!fullUrl.endsWith("/")) fullUrl += "/";


        DynamicSyncService service = retrofit.create(DynamicSyncService.class);
        Call<Void> call = service.sendSyncData(url, request);

        Log.e("URL",url);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonRequest = gson.toJson(request);
        Log.e("REQUEST_JSON", jsonRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getContext(), "Synced successfully", LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Sync failed: " + t.getMessage(), LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanner();
    }
}
