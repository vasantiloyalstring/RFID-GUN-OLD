package com.loyalstring.fragments;

import static com.loyalstring.MainActivity.invf;
import static com.loyalstring.fsupporters.Pemissionscheck.STORAGE_PERMISSION_READWRITE_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loyalstring.Apis.ApiProcess;
import com.loyalstring.MainActivity;
import com.loyalstring.R;
import com.loyalstring.database.StorageClass;
import com.loyalstring.fsupporters.MyApplication;
import com.loyalstring.fsupporters.Pemissionscheck;
import com.loyalstring.interfaces.interfaces;
import com.loyalstring.modelclasses.Itemmodel;
import com.loyalstring.readersupport.KeyDwonFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Homefragment extends KeyDwonFragment implements interfaces.PermissionCallback {

    View view;
    CardView cproduct, cinventory, cbill, csearch, cstocktransfer
            , cstockhistory, cstockreport, csalereport, csettings, remap,stockTransfer, issue;

    private android.os.Handler handler;
    private Runnable runnable;

    MainActivity mainActivity;
    StorageClass storageClass;

    Pemissionscheck pcheck;
    Button testbtn;
    Button powercheck;
    private Fragment pendingFragment;
    MyApplication app;
    ApiProcess apiprocess;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_homefragment, container, false);

        mainActivity = (MainActivity) getActivity();
        apiprocess=new ApiProcess();
        ensurePermissions(getActivity());
        storageClass = new StorageClass(getActivity());
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            // Update ActionBar properties
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(storageClass.getBranch());
            // actionBar.setHomeAsUpIndicator(R.drawable.your_custom_icon); // Set a custom icon
        }
        mainActivity.toolpower.setVisibility(View.GONE);
     //   startAutoUpdate();

        pcheck = new Pemissionscheck(getActivity(), this);

        cproduct = view.findViewById(R.id.cproduct);
        cinventory = view.findViewById(R.id.cinventory);
        cbill = view.findViewById(R.id.cbill);
        csearch = view.findViewById(R.id.csearch);
        cstocktransfer = view.findViewById(R.id.cstocktransfer);
        cstockhistory = view.findViewById(R.id.cstockhistory);
        cstockreport = view.findViewById(R.id.cstockreport);
        csettings = view.findViewById(R.id.csettings);
        testbtn = view.findViewById(R.id.testbtn);
        remap = view.findViewById(R.id.remap);
        stockTransfer = view.findViewById(R.id.stock_transfer);
        issue = view.findViewById(R.id.issue);
        csalereport= view.findViewById(R.id.csalereport);

//        powercheck = view.findViewById(R.id.powercheck);


//        powercheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mainActivity.mReader.setPower(5)){
//                    Log.e("setpowerrr", " "+true);
//                }else{
//                    Log.e("setpowerrr", " "+false);
//                }
//            }
//        });




        cproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainActivity.mReader.setPower(getvalue(storageClass.getppower()))) {
                    Fragment h = new productfragment();
                    displayfragemnt(h);
                }else{
                    Toast.makeText(getActivity(), "failed to set power", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cinventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainActivity.mReader.setPower(getvalue(storageClass.getipower()))) {
                Fragment h = new Inventoryfragment();
                    invf = h;
                displayfragemnt(h);
                }else{
                    Toast.makeText(getActivity(), "failed to set power", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cbill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.mReader.setPower(getvalue(storageClass.gettpower()));
                Fragment h = new Billfragment();
//                Fragment h = new Bill1fragment();
                displayfragemnt(h);
            }
        });
        csearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.mReader.setPower(getvalue(storageClass.getspower()));
                Fragment h = new Searchfragment();
                displayfragemnt(h);
            }
        });
        /*cstocktransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment h = new Stocktransferfragment();
                displayfragemnt(h);
            }
        });
        cstockhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment h = new Stockhistoryfragment();
                displayfragemnt(h);
            }
        });*/
        cstockreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment h = new Stockreportfragment();
                displayfragemnt(h);
            }
        });
        csettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment h = new Settingsfragment();
                displayfragemnt(h);
            }
        });

        remap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment h = new remap();
                displayfragemnt(h);
            }
        });
        stockTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment h = new Stocktransferfragment();
                displayfragemnt(h);
            }
        });

        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment h = new Issuetrackerfragment();
                displayfragemnt(h);

            }
        });

        csalereport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment h = new Allreportfragment();
                displayfragemnt(h);
            }
        });

        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int randomNumber = random.nextInt(30) + 1;
//                Log.e("randomnumber", ""+randomNumber);

               if( !mainActivity.mReader.setPower(randomNumber)){
                   Log.e("setpower", "setted");
               }else{
                   Log.i("setpowerr", "failed");
               }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }*/
    private void startAutoUpdate() {
        handler = new android.os.Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                ArrayList<Itemmodel> ml = new ArrayList<>();

                if (app.getInventoryMap().size() > 0) {
                    for (Map.Entry<String, Itemmodel> entry : app.getInventoryMap().entrySet()) {
                        Itemmodel m = new Itemmodel(entry.getValue());
                        m.setCounterId("1");
                        m.setCounterName("name");
                        ml.add(m);
                    }
                }

                if (!ml.isEmpty() && storageClass.getBaseUrl() != null && !storageClass.getBaseUrl().isEmpty()) {
                    apiprocess.updateproduct(ml, getActivity(), storageClass.getBaseUrl());
                }

                handler.postDelayed(this, 120000); // <-- calls again after 15 sec
            }
        };

        handler.postDelayed(runnable, 120000); // first call
    }


    private void ensurePermissions(Activity activity) {
        String[] permissions = {
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
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

    private int getvalue(String power) {
        if(power == null || power.isEmpty() || power.equalsIgnoreCase("0")){
            return 5;
        }else{
            return Integer.parseInt(power);
        }
    }

    @Override
    public void onPermissionGranted(String s, Intent data) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentChanged(int menuItemId);
    }

    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    private void displayfragemnt(Fragment h) {
        if (pcheck.checkreadandwrite(requireContext())) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainfragment, h).addToBackStack(null).commit();
            mListener.onFragmentChanged(h.getId());
        } else {
            // ask the Activity to remember and request
            ((MainActivity) requireActivity()).requestPermissionAndNavigate(h);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_READWRITE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingFragment != null) {
                    displayfragemnt(pendingFragment);
                    pendingFragment = null;
                }
            } else {
                Toast.makeText(getActivity(), "Storage permission is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}