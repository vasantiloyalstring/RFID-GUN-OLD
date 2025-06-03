package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockVerificationFilterModel  {

    @SerializedName("StockVerificationFilter")
    @Expose
    private StockVerificationFilter stockVerificationFilter;
    @SerializedName("MatchQuantityRequest")
    @Expose
    private MatchQuantityRequest matchQuantityRequest;

    public StockVerificationFilter getStockVerificationFilter() {
        return stockVerificationFilter;
    }

    public void setStockVerificationFilter(StockVerificationFilter stockVerificationFilter) {
        this.stockVerificationFilter = stockVerificationFilter;
    }

    public MatchQuantityRequest getMatchQuantityRequest() {
        return matchQuantityRequest;
    }

    public void setMatchQuantityRequest(MatchQuantityRequest matchQuantityRequest) {
        this.matchQuantityRequest = matchQuantityRequest;
    }
}
