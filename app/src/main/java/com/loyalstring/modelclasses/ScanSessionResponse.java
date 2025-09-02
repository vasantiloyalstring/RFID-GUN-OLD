package com.loyalstring.modelclasses;

import java.util.List;

public class ScanSessionResponse {
    public String Message;
    public String ScanBatchId;
    public List<StockItemModel> MATCH;
    public List<StockItemModel> UNMATCH;
    public TotalsModel Totals;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getScanBatchId() {
        return ScanBatchId;
    }

    public void setScanBatchId(String scanBatchId) {
        ScanBatchId = scanBatchId;
    }

    public List<StockItemModel> getMATCH() {
        return MATCH;
    }

    public void setMATCH(List<StockItemModel> MATCH) {
        this.MATCH = MATCH;
    }

    public List<StockItemModel> getUNMATCH() {
        return UNMATCH;
    }

    public void setUNMATCH(List<StockItemModel> UNMATCH) {
        this.UNMATCH = UNMATCH;
    }

    public TotalsModel getTotals() {
        return Totals;
    }

    public void setTotals(TotalsModel totals) {
        Totals = totals;
    }
}
