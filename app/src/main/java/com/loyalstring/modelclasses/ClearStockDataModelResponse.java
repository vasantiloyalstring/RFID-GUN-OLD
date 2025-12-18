package com.loyalstring.modelclasses;

public class ClearStockDataModelResponse {
    private boolean success;
    private int deletedRecords;

    public ClearStockDataModelResponse() {}

    public ClearStockDataModelResponse(boolean success, int deletedRecords) {
        this.success = success;
        this.deletedRecords = deletedRecords;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getDeletedRecords() {
        return deletedRecords;
    }

    public void setDeletedRecords(int deletedRecords) {
        this.deletedRecords = deletedRecords;
    }
}
