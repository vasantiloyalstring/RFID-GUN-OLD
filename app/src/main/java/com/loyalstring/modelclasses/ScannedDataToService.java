package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScannedDataToService {
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("CreatedOn")
    @Expose
    private String createdOn;
    @SerializedName("LastUpdated")
    @Expose
    private String lastUpdated;
    @SerializedName("StatusType")
    @Expose
    private Boolean statusType;
    @SerializedName("ClientCode")
    @Expose
    private String clientCode;
    @SerializedName("DeviceId")
    @Expose
    private String deviceId;
    @SerializedName("TIDValue")
    @Expose
    private String tIDValue;
    @SerializedName("RFIDCode")
    @Expose
    private String rFIDCode;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    @SerializedName("ItemCode")
    @Expose
    private String itemCode;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getStatusType() {
        return statusType;
    }

    public void setStatusType(Boolean statusType) {
        this.statusType = statusType;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTIDValue() {
        return tIDValue;
    }

    public void setTIDValue(String tIDValue) {
        this.tIDValue = tIDValue;
    }

    public String getRFIDCode() {
        return rFIDCode;
    }

    public void setRFIDCode(String rFIDCode) {
        this.rFIDCode = rFIDCode;
    }

}
