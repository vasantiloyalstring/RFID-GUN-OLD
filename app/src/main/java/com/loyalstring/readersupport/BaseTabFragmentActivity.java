package com.loyalstring.readersupport;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.loyalstring.MainActivity;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.barcode.BarcodeFactory;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.BarcodeEntity;
import com.rscja.utility.StringUtility;
import com.loyalstring.readersupport.KeyDwonFragment;

public class BaseTabFragmentActivity extends AppCompatActivity{
//        FragmentActivity {
    public RFIDWithUHFUART mReader;
    public KeyDwonFragment currentFragment=null;
    public BarcodeDecoder barcodeDecoder = BarcodeFactory.getInstance().getBarcodeDecoder();
    public int TidLen=6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {

            toastMessage(ex.getMessage());

            return;
        }

        if (mReader != null) {
            new InitTask().execute();
        }
    }

    public class InitTask1 extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            open();
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!BaseTabFragmentActivity.this.isFinishing() && mypDialog != null && mypDialog.isShowing()) {
                mypDialog.dismiss();
            }
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(BaseTabFragmentActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.setCancelable(false);
            mypDialog.show();
        }
    }
    private void open(){
        barcodeDecoder.open(this);

            /*TODO
            BarcodeUtility.getInstance().setPrefix(this,"");
            BarcodeUtility.getInstance().setSuffix(this,"");
            BarcodeUtility.getInstance().enablePlaySuccessSound(this,true); //success Sound
            BarcodeUtility.getInstance().enableVibrate(this,true);//vibrate
            BarcodeUtility.getInstance().enableEnter(this,true);//addition enter

            BarcodeUtility.getInstance().enableContinuousScan(this,true);//Continuous scanning


            BarcodeUtility.getInstance().setContinuousScanIntervalTime(this,100);//Unit: milliseconds
            BarcodeUtility.getInstance().setContinuousScanTimeOut(this,9999);//Unit: milliseconds
            */

        barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
            @Override
            public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                if(barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS){
//                    tvData.setText("data:"+barcodeEntity.getBarcodeData());
                }else{
                    Toast.makeText(BaseTabFragmentActivity.this, "failed to load barcodescanner", Toast.LENGTH_SHORT).show();
//                    tvData.setText("fail");
                }
            }
        });
    }

    private void close(){
        barcodeDecoder.close();
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 293 || keyCode == 280 || keyCode == 139) {
            if (event.getRepeatCount() == 0) {
                if (currentFragment != null) {
                    if (keyCode == 139) {
                        currentFragment.myOnKeyDwon("barcode");
                    }else{
                        currentFragment.myOnKeyDwon("scan");
                    }
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub

            return mReader.init(getApplicationContext());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();
            if (!result) {
                Toast.makeText(BaseTabFragmentActivity.this, "init fail", Toast.LENGTH_SHORT).show();
            }
//            new InitTask1().execute();
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(BaseTabFragmentActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
//            new InitTask1().execute();
        }
    }

    /*public class InitTask1 extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
//            open();
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(BaseTabFragmentActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.setCancelable(false);
            mypDialog.show();
        }
    }*/


    public boolean vailHexInput(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        if (str.length() % 2 == 0) {
            return StringUtility.isHexNumberRex(str);
        }
        return false;
    }


}
