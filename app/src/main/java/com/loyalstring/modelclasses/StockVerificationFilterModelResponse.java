package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockVerificationFilterModelResponse {
    @SerializedName("StockVerificationData")
    @Expose
    private List<StockVerificationDatum> stockVerificationData;
    @SerializedName("MatchQuantityData")
    @Expose
    private List<MatchQuantityDatum> matchQuantityData;
    @SerializedName("Message")
    @Expose
    private Object message;
    @SerializedName("TotalGrossWeight")
    @Expose
    private Integer totalGrossWeight;
    @SerializedName("TotalNetWeight")
    @Expose
    private Integer totalNetWeight;
    @SerializedName("TotalQuantity")
    @Expose
    private Integer totalQuantity;
    @SerializedName("TotalMatchQty")
    @Expose
    private Integer totalMatchQty;

    public List<StockVerificationDatum> getStockVerificationData() {
        return stockVerificationData;
    }

    public void setStockVerificationData(List<StockVerificationDatum> stockVerificationData) {
        this.stockVerificationData = stockVerificationData;
    }

    public List<MatchQuantityDatum> getMatchQuantityData() {
        return matchQuantityData;
    }

    public void setMatchQuantityData(List<MatchQuantityDatum> matchQuantityData) {
        this.matchQuantityData = matchQuantityData;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Integer getTotalGrossWeight() {
        return totalGrossWeight;
    }

    public void setTotalGrossWeight(Integer totalGrossWeight) {
        this.totalGrossWeight = totalGrossWeight;
    }

    public Integer getTotalNetWeight() {
        return totalNetWeight;
    }

    public void setTotalNetWeight(Integer totalNetWeight) {
        this.totalNetWeight = totalNetWeight;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getTotalMatchQty() {
        return totalMatchQty;
    }

    public void setTotalMatchQty(Integer totalMatchQty) {
        this.totalMatchQty = totalMatchQty;
    }

}
