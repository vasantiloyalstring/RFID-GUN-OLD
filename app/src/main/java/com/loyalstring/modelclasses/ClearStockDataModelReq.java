package com.loyalstring.modelclasses;

public class ClearStockDataModelReq {
    private String ClientCode;
    private String DeviceId;

    public ClearStockDataModelReq() { }

    public ClearStockDataModelReq(String clientCode, String deviceId) {
        this.ClientCode = clientCode;
        this.DeviceId = deviceId;
    }

    public String getClientCode() {
        return ClientCode;
    }

    public void setClientCode(String clientCode) {
        ClientCode = clientCode;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

}
