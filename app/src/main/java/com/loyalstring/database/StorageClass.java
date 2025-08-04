package com.loyalstring.database;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageClass {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_LOGIN_STATUS = "isLoggedIn";

    private static final String KEY_BRANCH = "branch";
    private static final String KEY_RESTORE = "backuprestore";
    private static final String KEY_PRODUCTPOWER = "productpower";
    private static final String KEY_INVENTORYPOWER = "inventorypower";
    private static final String KEY_SEARCHPOWER = "searchpower";
    private static final String KEY_STOCKTRA = "stocktransfer";
    private static final String KEY_TRANSACTIONPOWER = "transactionpower";
    private static final String KEY_STOCKTRANSFERPOWER = "stocktransferpower";
    private static final String KEY_STOCKHISTORYPOWER = "stockhistorypower";
    private static final String KEY_SBRANCH = "branch";
    private static final String KEY_SERIAL = "serial";
    private static final String KEY_ACTIVATION = "isActivated";
    private static final String KEY_ACTIVATIONDATE = "activationdate";
    private static final String KEY_EXPIRYDATE = "expirydate";
    private static final String KEY_VALIDITY = "validity";
    private static final String KEY_PHONE = "validity";

    //logindetails
    private static final String KEY_LUSERNAME = "loginusername";
    private static final String KEY_LBRANCH = "loginbranch";
    private static final String KEY_LPHONE = "loginphone";
    private static final String BASE_URL = "baseurl";
    private static final String RFID_URL = "rfidurl";
    private static final String SHEET_URL = "sheeturl";


    private SharedPreferences sharedPreferences;

    public StorageClass(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedInStatus(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_LOGIN_STATUS, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGIN_STATUS, false);
    }

    //activation page code
    public void setActivationStatus(boolean isActivated, String adate, String edate, String validity, String phone, String serial) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ACTIVATION, isActivated);
        editor.putString(KEY_ACTIVATIONDATE, adate);
        editor.putString(KEY_EXPIRYDATE, edate);
        editor.putString(KEY_VALIDITY, validity);
        editor.putString(KEY_SERIAL, serial);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    public void  updateactivation(boolean isActivated){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ACTIVATION, isActivated);
        editor.apply();
    }

    public void setlogindetails(String uname, String phone, String branch) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LUSERNAME, uname);
        editor.putString(KEY_LPHONE, phone);
        editor.putString(KEY_LBRANCH, branch);
        editor.apply();
    }

    public String lusername() {
        return sharedPreferences.getString(KEY_LUSERNAME, "");
    }

    public String lphone() {
        return sharedPreferences.getString(KEY_LPHONE, "");
    }

    public String lbranch() {
        return sharedPreferences.getString(KEY_LBRANCH, "");
    }



    public boolean isActivated() {
        return sharedPreferences.getBoolean(KEY_ACTIVATION, false); // Default value can be changed as needed
    }

    public String getActivationDate() {
        return sharedPreferences.getString(KEY_ACTIVATIONDATE, "");
    }

    public String getExpiryDate() {
        return sharedPreferences.getString(KEY_EXPIRYDATE, "");
    }

    public String getValidity() {
        return sharedPreferences.getString(KEY_VALIDITY, "");
    }

    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, "");
    }

    public String getSerial() {
        return sharedPreferences.getString(KEY_SERIAL, "");
    }


    public void setBackuprestore(boolean isActivated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_RESTORE, isActivated);
        editor.apply();
    }

    public boolean isBackuprestore() {
        return sharedPreferences.getBoolean(KEY_RESTORE, false);
    }


    public void setBranch(String branch) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BRANCH, branch);
        editor.apply();
    }

    public String getBranch() {
        return sharedPreferences.getString(KEY_BRANCH, "");
    }


    public void setppower(String power) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PRODUCTPOWER, power);
        editor.apply();
    }

    public String getppower() {
        return sharedPreferences.getString(KEY_PRODUCTPOWER, "");
    }

    public void setipower(String power) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_INVENTORYPOWER, power);
        editor.apply();
    }

    public String getipower() {
        return sharedPreferences.getString(KEY_INVENTORYPOWER, "");
    }

    public void setspower(String power) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SEARCHPOWER, power);
        editor.apply();
    }

    public String getspower() {
        return sharedPreferences.getString(KEY_SEARCHPOWER, "");
    }



    public void settpower(String power) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TRANSACTIONPOWER, power);
        editor.apply();
    }

    public String gettpower() {
        return sharedPreferences.getString(KEY_TRANSACTIONPOWER, "");
    }

    public void setstpower(String power) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STOCKTRANSFERPOWER, power);
        editor.apply();
    }

    public String getstpower() {
        return sharedPreferences.getString(KEY_STOCKTRANSFERPOWER, "");
    }

    public void setshpower(String power) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STOCKHISTORYPOWER, power);
        editor.apply();
    }

    public String getshpower() {
        return sharedPreferences.getString(KEY_STOCKHISTORYPOWER, "");
    }

    public void setstapower(String power) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STOCKTRA, power);
        editor.apply();
    }

    public String getstapower() {
        return sharedPreferences.getString(KEY_STOCKTRA, "");
    }



    public void setbaseurl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BASE_URL, url);
        editor.apply();
    }
    public String getBaseUrl(){
        return sharedPreferences.getString(BASE_URL, "");
    }


    public void setrfidurl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RFID_URL, url);
        editor.apply();
    }
    public String getrfidUrl(){
        return sharedPreferences.getString(RFID_URL, "");
    }




 public void setsheeturl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHEET_URL, url);
        editor.apply();
    }
    public String getSheeturl(){
        return sharedPreferences.getString(SHEET_URL, "");
    }
}
