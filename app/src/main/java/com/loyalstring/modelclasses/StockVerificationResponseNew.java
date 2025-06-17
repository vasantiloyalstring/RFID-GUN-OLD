package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockVerificationResponseNew {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("MATCH")
    @Expose
    private List<Match> match;
    @SerializedName("UNMATCH")
    @Expose
    private List<Unmatch> unmatch;
    @SerializedName("TotalMatchGrossWeight")
    @Expose
    private Integer totalMatchGrossWeight;
    @SerializedName("TotalMatchNetWeight")
    @Expose
    private Integer totalMatchNetWeight;
    @SerializedName("TotalUnmatchGrossWeight")
    @Expose
    private Integer totalUnmatchGrossWeight;
    @SerializedName("TotalUnmatchNetWeight")
    @Expose
    private Integer totalUnmatchNetWeight;
    @SerializedName("TotalQuantity")
    @Expose
    private Integer totalQuantity;
    @SerializedName("TotalGrossWeight")
    @Expose
    private Integer totalGrossWeight;
    @SerializedName("TotalNetWeight")
    @Expose
    private Integer totalNetWeight;
    @SerializedName("TotalMatchQty")
    @Expose
    private Integer totalMatchQty;
    @SerializedName("TotalUnmatchQty")
    @Expose
    private Integer totalUnmatchQty;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Match> getMatch() {
        return match;
    }

    public void setMatch(List<Match> match) {
        this.match = match;
    }

    public List<Unmatch> getUnmatch() {
        return unmatch;
    }

    public void setUnmatch(List<Unmatch> unmatch) {
        this.unmatch = unmatch;
    }

    public Integer getTotalMatchGrossWeight() {
        return totalMatchGrossWeight;
    }

    public void setTotalMatchGrossWeight(Integer totalMatchGrossWeight) {
        this.totalMatchGrossWeight = totalMatchGrossWeight;
    }

    public Integer getTotalMatchNetWeight() {
        return totalMatchNetWeight;
    }

    public void setTotalMatchNetWeight(Integer totalMatchNetWeight) {
        this.totalMatchNetWeight = totalMatchNetWeight;
    }

    public Integer getTotalUnmatchGrossWeight() {
        return totalUnmatchGrossWeight;
    }

    public void setTotalUnmatchGrossWeight(Integer totalUnmatchGrossWeight) {
        this.totalUnmatchGrossWeight = totalUnmatchGrossWeight;
    }

    public Integer getTotalUnmatchNetWeight() {
        return totalUnmatchNetWeight;
    }

    public void setTotalUnmatchNetWeight(Integer totalUnmatchNetWeight) {
        this.totalUnmatchNetWeight = totalUnmatchNetWeight;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
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

    public Integer getTotalMatchQty() {
        return totalMatchQty;
    }

    public void setTotalMatchQty(Integer totalMatchQty) {
        this.totalMatchQty = totalMatchQty;
    }

    public Integer getTotalUnmatchQty() {
        return totalUnmatchQty;
    }

    public void setTotalUnmatchQty(Integer totalUnmatchQty) {
        this.totalUnmatchQty = totalUnmatchQty;
    }

}