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
    private double totalMatchGrossWeight;
    @SerializedName("TotalMatchNetWeight")
    @Expose
    private double totalMatchNetWeight;
    @SerializedName("TotalUnmatchGrossWeight")
    @Expose
    private double totalUnmatchGrossWeight;
    @SerializedName("TotalUnmatchNetWeight")
    @Expose
    private double totalUnmatchNetWeight;
    @SerializedName("TotalQuantity")
    @Expose
    private Integer totalQuantity;
    @SerializedName("TotalGrossWeight")
    @Expose
    private double totalGrossWeight;
    @SerializedName("TotalNetWeight")
    @Expose
    private double totalNetWeight;
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

    public double getTotalMatchGrossWeight() {
        return totalMatchGrossWeight;
    }

    public void setTotalMatchGrossWeight(double totalMatchGrossWeight) {
        this.totalMatchGrossWeight = totalMatchGrossWeight;
    }

    public double getTotalMatchNetWeight() {
        return totalMatchNetWeight;
    }

    public void setTotalMatchNetWeight(double totalMatchNetWeight) {
        this.totalMatchNetWeight = totalMatchNetWeight;
    }

    public double getTotalUnmatchGrossWeight() {
        return totalUnmatchGrossWeight;
    }

    public void setTotalUnmatchGrossWeight(double totalUnmatchGrossWeight) {
        this.totalUnmatchGrossWeight = totalUnmatchGrossWeight;
    }

    public double getTotalUnmatchNetWeight() {
        return totalUnmatchNetWeight;
    }

    public void setTotalUnmatchNetWeight(double totalUnmatchNetWeight) {
        this.totalUnmatchNetWeight = totalUnmatchNetWeight;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalGrossWeight() {
        return totalGrossWeight;
    }

    public void setTotalGrossWeight(Integer totalGrossWeight) {
        this.totalGrossWeight = totalGrossWeight;
    }

    public double getTotalNetWeight() {
        return totalNetWeight;
    }

    public void setTotalNetWeight(double totalNetWeight) {
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