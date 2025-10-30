package com.loyalstring;

import static com.loyalstring.fsupporters.Pemissionscheck.STORAGE_PERMISSION_READWRITE_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.loyalstring.Apis.ApiManager;
import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestBackground.SyncWorker;
import com.loyalstring.LatestStorage.SharedPreferencesManager;
import com.loyalstring.apiresponse.SkuResponse;
import com.loyalstring.database.StorageClass;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.fragments.Billfragment;
import com.loyalstring.fragments.DailyStockreportfragment;
import com.loyalstring.fragments.Homefragment;
import com.loyalstring.fragments.Inventoryfragment;
import com.loyalstring.fragments.Searchfragment;
import com.loyalstring.fragments.Settingsfragment;
import com.loyalstring.fragments.Stockreportfragment;
import com.loyalstring.fragments.productfragment;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.fsupporters.Pemissionscheck;
import com.loyalstring.interfaces.ApiService;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.mainscreens.Loginpage;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.readersupport.BaseTabFragmentActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseTabFragmentActivity implements NavigationView.OnNavigationItemSelectedListener, Homefragment.OnFragmentInteractionListener, interfaces.PermissionCallback {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    public static NavigationView navigationView;

    private Toolbar toolbar;
    public TextView toolpower;
    StorageClass storageClass;
    Pemissionscheck pemissionscheck;
    private Fragment pendingFragment;
//    BarcodeDecoder barcodeDecoder= BarcodeFactory.getInstance().getBarcodeDecoder();

    public static Fragment invf;
    public static boolean Isearching = false;
    public static DecimalFormat decimalFormat = new DecimalFormat("#.###");

    EntryDatabase entryDatabase;
    MyApplication myapp;
    HashMap<String, Itemmodel> totalitems = new HashMap<>();

    SharedPreferencesManager sharedPreferencesManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ensurePermissions(this);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        storageClass = new StorageClass(MainActivity.this);

        sharedPreferencesManager = new SharedPreferencesManager(MainActivity.this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Home");
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_invisible); // Set the hamburger icon
        }
        toolpower = findViewById(R.id.toolbarpower);
        myapp = (MyApplication) this.getApplicationContext();

//        storageClass.setppower("5");
//        storageClass.setipower("30");
//        storageClass.settpower("30");
//        storageClass.setspower("5");
//        storageClass.setstpower("5");
//        storageClass.setshpower("5");

//        FirebaseCrashlytics.getInstance().setUserId("12345")
        Fragment h = new Homefragment();
        displayfragemnt(h);
        setSupportActionBar(toolbar);
        pemissionscheck = new Pemissionscheck(MainActivity.this, this);

//        FirebaseCrashlytics.getInstance().setUserId(getsvalue(storageClass.getSerial()));


        // Set up the action bar
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
//        drawerToggle.setDrawerIndicatorEnabled(false);
//        Drawable icon = getResources().getDrawable(R.drawable.ic_menu); // Replace with your custom icon
//        drawerToggle.setHomeAsUpIndicator(icon);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        entryDatabase = new EntryDatabase(this);
        entryDatabase.checkdatabase(this);

        Clients client = sharedPreferencesManager.readLoginData().getEmployee().getClients();
        FirebaseCrashlytics.getInstance().setUserId(client.getClientCode());


//        String a = null;
//        int b = Integer.parseInt(a);

//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        Button crashButton = new Button(this);
//        crashButton.setText("Crash App");
//        crashButton.setOnClickListener(v -> {
//            throw new RuntimeException("Test Crash: This is a forced crash for Firebase Crashlytics testing.");
//        });
//
//        // Add the button to the layout
//        layout.addView(crashButton);
//
//        // Set the layout as the content view
//        setContentView(layout);


        client = sharedPreferencesManager.readLoginData().getEmployee().getClients();
        Log.e("check type ", "cc"+client.toString());
        if(client.getRfidType().contains("Web") && client.getClientCode()!= null && !client.getClientCode().isEmpty()){
            if (!myapp.isCountMatch()) {
                Clients finalClient = client;
                Runnable onCountMatched = new Runnable() {
                    @Override
                    public void run() {
                        // Dismiss the progress dialog
                        //if(finalClient.getRfidType().contains("websingle")) {
                        try {
                            totalitems = myapp.getInventoryMap();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                       // }
                        // OneTimeWorkRequest syncWorkRequest = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
                        //WorkManager.getInstance(MainActivity.this).enqueue(syncWorkRequest);

                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Check for count match
                        while (!myapp.isCountMatch()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        MainActivity.this.runOnUiThread(onCountMatched);
                    }
                }).start();
            } else {
                Clients finalClient = client;
               // if(finalClient.getRfidType().contains("websingle")) {


try {
    totalitems = myapp.getInventoryMap();
}catch ( Exception e)
{

}
                //}
                // OneTimeWorkRequest syncWorkRequest = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
                // WorkManager.getInstance(MainActivity.this).enqueue(syncWorkRequest);
            }
        }


//        List<Itemmodel> ditem = DummyDataGenerator.generateDummyData(200000);
//        Toast.makeText(this, "itemsize "+ditem.size(), Toast.LENGTH_SHORT).show();
//        ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("adding");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//        entryDatabase.AutoSync(ditem, dialog, myapp, new SaveCallback(){
//            @Override
//            public void onSaveSuccess() {
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onSaveFailure(List<Itemmodel> failedItems) {
//                dialog.dismiss();
//            }
//        });

        initSound();
        initUHF();
        new InitTask1().execute();


















//        fetchapis();


    }

    private void ensurePermissions(Activity activity) {
        String[] permissions = {
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        List<String> req = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                req.add(p);
            }
        }
        if (!req.isEmpty()) {
            ActivityCompat.requestPermissions(activity, req.toArray(new String[0]), 1001);
        }
    }

    private String getsvalue(String serial) {
        if (serial == null || serial.isEmpty()) {
            return "failed id";
        }
        return serial.trim();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.itemhome) {
            fragment = getSupportFragmentManager().findFragmentByTag("Homefragment");
            if (fragment == null) {
                fragment = new Homefragment();
                // Add HomeFragment to back stack only when it's initially added
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainfragment, fragment, "Homefragment")
                        .addToBackStack(null)
                        .commit();
            }
        } else if (id == R.id.itemproduct) {
            fragment = new productfragment();
        } else if (id == R.id.iteminventory) {
            invf = fragment;
            fragment = new Inventoryfragment();
        } else if (id == R.id.itemsearch) {
            fragment = new Searchfragment();
        } else if (id == R.id.itembill) {
            fragment = new Billfragment();
        } else if (id == R.id.itemsvr) {
            fragment = new Stockreportfragment();
        }
      /*  else if (id == R.id.itemDailyStock) {
            fragment = new DailyStockreportfragment();
        }*/
//        else if (id == R.id.itemstocktransfer) {
//            fragment = new Stocktransferfragment();
//        }
//        else if (id == R.id.itemstockhistory) {
//            fragment = new Stockhistoryfragment();
//        }
        else if (id == R.id.itemsettings) {
            fragment = new Settingsfragment();
        } else if (id == R.id.nav_Logout) {
            storageClass.setLoggedInStatus(false);
            Intent go = new Intent(MainActivity.this, Loginpage.class);
            startActivity(go);
            finish();
        }

        if (fragment != null) {
            if (pemissionscheck.checkreadandwrite(MainActivity.this)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainfragment, fragment, fragment.getClass().getSimpleName())
                        .addToBackStack(null)
                        .commit();
                drawerLayout.closeDrawer(GravityCompat.START);   // <-- ensure close
                return true;
            } else {
                pendingFragment = fragment;
                pemissionscheck.requestreadwrite(MainActivity.this);
                drawerLayout.closeDrawer(GravityCompat.START);   // <-- close even when requesting
                return true;                                     // mark handled so drawer doesn’t linger
            }
        }
        return false;
        }


        private void displayfragemnt(Fragment h) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainfragment, h)
                .commit();
    }



    @Override
    public void onFragmentChanged(int menuItemId) {
        navigationView.setCheckedItem(menuItemId);
    }


    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;
    HashMap<Integer, Integer> soundStreamIds = new HashMap<>();


    private void initSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.sixty, 1));
        soundMap.put(3, soundPool.load(this, R.raw.seventy, 1));
        soundMap.put(4, soundPool.load(this, R.raw.fourty, 1));
        soundMap.put(5, soundPool.load(this, R.raw.found1, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }

    private void releaseSoundPool() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public void playSound(int id) {
        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolume / audioMaxVolume;
        try {
            int streamId = soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, // 右声道音量
                    1, // 优先级，0为最低
                    0, // 循环次数，0不循环，-1永远循环
                    1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );
            soundStreamIds.put(id, streamId); // Store the stream ID
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSound(int id) {
        if (soundStreamIds.containsKey(id)) {
            int streamId = soundStreamIds.get(id);
            soundPool.stop(streamId); // Stop the sound using the stored stream ID
            soundStreamIds.remove(id); // Remove the stream ID from the map
        }
    }


    public static int binarySearch(List<String> array, String src) {
        int left = 0;
        int right = array.size() - 1;
        // 这里必须是 <=
        while (left <= right) {
            if (compareString(array.get(left), src)) {
                return left;
            } else if (left != right) {
                if (compareString(array.get(right), src))
                    return right;
            }
            left++;
            right--;
        }
        return -1;
    }

    static boolean compareString(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        } else if (str1.hashCode() != str2.hashCode()) {
            return false;
        } else {
            char[] value1 = str1.toCharArray();
            char[] value2 = str2.toCharArray();
            int size = value1.length;
            for (int k = 0; k < size; k++) {
                if (value1[k] != value2[k]) {
                    return false;
                }
            }
            return true;
        }
    }

  /*  @Override
    public void onBackPressed() {
        if (mReader.isInventorying()) {
            mReader.stopInventory();
        }
        if (Isearching) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment1 = fragmentManager.findFragmentById(R.id.mainfragment);

            if (currentFragment1 != null && currentFragment1 instanceof Searchfragment) {
                // Show the InventoryFragment
                getSupportFragmentManager().beginTransaction().show(invf).commit();
                Isearching = false;
            }

        } else {
            if (currentFragment != null) {
                currentFragment = null;
            }
        }
        *//*if(currentFragment != null){
            currentFragment = null;
        }*//*
        super.onBackPressed();
    }*/


    @Override
    public void onBackPressed() {
        if (mReader.isInventorying()) {
            mReader.stopInventory();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainfragment);

        if (Isearching) {
            Fragment invf = fragmentManager.findFragmentByTag("INVENTORY_FRAGMENT_TAG");

            if (currentFragment instanceof Searchfragment) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (invf != null && invf.isAdded()) {
                    transaction.hide(currentFragment);
                    transaction.show(invf);
                    transaction.commit();
                    Isearching = false;
                    return; // Handled, do not call super
                } else {
                    // Create and add InventoryFragment if missing
                    invf = new Homefragment();
                    transaction.replace(R.id.mainfragment, invf, "INVENTORY_FRAGMENT_TAG");
                    transaction.commit();
                    Isearching = false;
                    return; // Handled
                }
            }
        }

        // If current fragment is HomeFragment or InventoryFragment, finish the activity
        if (currentFragment instanceof Homefragment) {
            finish(); // Exit the activity
        } else {
            super.onBackPressed(); // Default back behavior
        }
    }




    @Override
    public void onPermissionGranted(String excelopen, Intent data) {

    }


    private void fetchapis() {

        ApiManager apiManager;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dev.loyalstring.co.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Initialize ApiManager
        apiManager = new ApiManager(apiService);

        // Fetch SKU data
        apiManager.fetchAllSKU("LS000026", new interfaces.ApiCallback<List<SkuResponse>>() {
            @Override
            public void onSuccess(List<SkuResponse> result) {
                runOnUiThread(() -> {
                    // Handle the SKU data, update UI
                    // Example: display the SKU data in a RecyclerView
                    Toast.makeText(MainActivity.this, "loaded", Toast.LENGTH_SHORT).show();
                    entryDatabase.addskus(result, MainActivity.this);


                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Failed to fetch SKU data", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Fetch Labeled Stock data
    }

    public void requestPermissionAndNavigate(Fragment target) {
        if (pemissionscheck.checkreadandwrite(this)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainfragment, target).addToBackStack(null).commit();
        } else {
            pendingFragment = target; // keep it here (Activity)
            pemissionscheck.requestreadwrite(this); // request FROM Activity
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_READWRITE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted → re-call displayfragemnt() with saved fragment
                if (pendingFragment != null) {
                    displayfragemnt(pendingFragment);
                    pendingFragment = null; // reset
                }
            } else {
                Toast.makeText(this, "Storage permission is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}