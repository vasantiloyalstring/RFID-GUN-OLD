package com.loyalstring.LatestStorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.loyalstring.LatestApis.LoginApiSupport.Clients;
import com.loyalstring.LatestApis.LoginApiSupport.Employee;
import com.loyalstring.LatestApis.LoginResponse;
import com.loyalstring.LatestSettings.SyncSettings;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "MyAppPreferences";

    private SharedPreferences sharedPreferences;
    private String key_rfidtype = "RfidType";
    private static final String KEY_STOCK_TRANSFER_URL = "stock_transfer_url";
    private static final String KEY_DEVICE_ID= "device_id";


    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Method to save login data using keys from LoginResponse
    public void saveLoginData(LoginResponse loginResponse) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String, Object> loginData = new HashMap<>();

        try {
            // Save LoginResponse fields
            for (Field field : LoginResponse.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(loginResponse);
                if (value != null) loginData.put(field.getName(), value);
            }

            // Save Employee fields with "Employee_" prefix
            Employee employee = loginResponse.getEmployee();
            if (employee != null) {
                for (Field field : Employee.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(employee);
                    if (value != null) loginData.put("Employee_" + field.getName(), value);
                }

                // Save Client fields with "Client_" prefix
                Clients clients = employee.getClients();
                if (clients != null) {
                    for (Field field : Clients.class.getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(clients);
                        if (value != null) loginData.put("Client_" + field.getName(), value);
                    }
                }
            }

            // Commit all login data to SharedPreferences
            for (Map.Entry<String, Object> entry : loginData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    editor.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                }
                // Add checks for other data types as needed
            }

            editor.apply();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public LoginResponse readLoginData() {
        LoginResponse loginResponse = new LoginResponse();

        try {
            // Load LoginResponse fields
            for (Field field : LoginResponse.class.getDeclaredFields()) {
                field.setAccessible(true);
                String key = field.getName();
                if (field.getType().equals(String.class)) {
                    field.set(loginResponse, sharedPreferences.getString(key, ""));
                } else if (field.getType().equals(int.class)) {
                    field.set(loginResponse, sharedPreferences.getInt(key, 0));
                }
            }

            // Initialize and load Employee fields with "Employee_" prefix
            Employee employee = new Employee();
            for (Field field : Employee.class.getDeclaredFields()) {
                field.setAccessible(true);
                String key = "Employee_" + field.getName();
                if (field.getType().equals(String.class)) {
                    field.set(employee, sharedPreferences.getString(key, ""));
                } else if (field.getType().equals(int.class)) {
                    field.set(employee, sharedPreferences.getInt(key, 0));
                }
            }
            loginResponse.setEmployee(employee);

            // Initialize and load Clients fields with "Client_" prefix
            Clients clients = new Clients();
            for (Field field : Clients.class.getDeclaredFields()) {
                field.setAccessible(true);
                String key = "Client_" + field.getName();
                if (field.getType().equals(String.class)) {
                    field.set(clients, sharedPreferences.getString(key, ""));
                } else if (field.getType().equals(int.class)) {
                    field.set(clients, sharedPreferences.getInt(key, 0));
                }
            }
            employee.setClients(clients);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return loginResponse;
    }

    // Method to retrieve token
    public LoginResponse readLoginData1() {
        LoginResponse loginResponse = new LoginResponse();

        // Load data for LoginResponse fields
        Field[] responseFields = LoginResponse.class.getDeclaredFields();
        for (Field field : responseFields) {
            field.setAccessible(true);
            String key = field.getName();
            try {
                if (field.getType().equals(String.class)) {
                    field.set(loginResponse, sharedPreferences.getString(key, ""));
                } else if (field.getType().equals(int.class)) {
                    field.set(loginResponse, sharedPreferences.getInt(key, 0));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Load data for Employee fields
//        LoginResponse.Employee employee = loginResponse.new Employee();
        Employee employee = loginResponse.getEmployee();
        Field[] employeeFields = Employee.class.getDeclaredFields();
        for (Field field : employeeFields) {
            field.setAccessible(true);
            String key = "Employee_" + field.getName();
            try {
                if (field.getType().equals(String.class)) {
                    field.set(employee, sharedPreferences.getString(key, ""));
                } else if (field.getType().equals(int.class)) {
                    field.set(employee, sharedPreferences.getInt(key, 0));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        loginResponse.setEmployee(employee);

        // Load data for Clients fields within Employee
        Clients clients = employee.getClients();


        Field[] clientFields = Clients.class.getDeclaredFields();
        for (Field field : clientFields) {
            field.setAccessible(true);
            String key = "Client_" + field.getName();
            try {
                if (field.getType().equals(String.class)) {
                    field.set(clients, sharedPreferences.getString(key, ""));
                } else if (field.getType().equals(int.class)) {
                    field.set(clients, sharedPreferences.getInt(key, 0));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        employee.setClients(clients);

        return loginResponse;
    }

    public void saveSyncSettings(SyncSettings syncSettings) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get all fields from SyncSettings class
        Field[] fields = SyncSettings.class.getDeclaredFields();

        for (Field field : fields) {
            // Ensure the field is accessible
            field.setAccessible(true);

            try {
                // Only process boolean fields
                if (field.getType() == boolean.class) {
                    String fieldName = field.getName();
                    boolean fieldValue = field.getBoolean(syncSettings);

                    // Save each boolean field in SharedPreferences
                    editor.putBoolean("sync_" + fieldName, fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        editor.apply();
    }
    public SyncSettings loadSyncSettings() {
        SyncSettings syncSettings = new SyncSettings();
        syncSettings.setProduct(sharedPreferences.getBoolean("sync_product", false));
        syncSettings.setInventory(sharedPreferences.getBoolean("sync_inventory", false));
        syncSettings.setBills(sharedPreferences.getBoolean("sync_bills", false));
        syncSettings.setReports(sharedPreferences.getBoolean("sync_reports", false));
        return syncSettings;
    }
    // Method to clear saved data

    public void  saverfid(String type){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key_rfidtype, type);
        editor.apply();
    }
    public void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveStockTransferUrl(String url) {
        sharedPreferences.edit().putString(KEY_STOCK_TRANSFER_URL, url).apply();
    }

    public String getStockTransferUrl() {
        return sharedPreferences.getString(KEY_STOCK_TRANSFER_URL, "");
    }

    public void saveDeviceId(String id) {
        sharedPreferences.edit().putString(KEY_DEVICE_ID, id).apply();
    }

    public String getDeviceId() {
        return sharedPreferences.getString(KEY_DEVICE_ID, "");
    }
}
