package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {
    @SerializedName("ItemCode")
    @Expose
    private String itemCode;
    @SerializedName("ClientCode")
    @Expose
    private String clientCode;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("CounterId")
    @Expose
    private Object counterId;
    @SerializedName("CategoryId")
    @Expose
    private Object categoryId;
    @SerializedName("ProductId")
    @Expose
    private Object productId;
    @SerializedName("DesignId")
    @Expose
    private Object designId;
    @SerializedName("PurityId")
    @Expose
    private Object purityId;
    @SerializedName("CompanyId")
    @Expose
    private Object companyId;
    @SerializedName("BranchId")
    @Expose
    private Object branchId;
    @SerializedName("CounterName")
    @Expose
    private String counterName;
    @SerializedName("CategoryName")
    @Expose
    private String categoryName;
    @SerializedName("ProductName")
    @Expose
    private String productName;
    @SerializedName("DesignName")
    @Expose
    private String designName;
    @SerializedName("PurityName")
    @Expose
    private String purityName;
    @SerializedName("CompanyName")
    @Expose
    private String companyName;
    @SerializedName("BranchName")
    @Expose
    private String branchName;
    @SerializedName("GrossWeight")
    @Expose
    private Integer grossWeight;
    @SerializedName("NetWeight")
    @Expose
    private Integer netWeight;
    @SerializedName("Quantity")
    @Expose
    private Integer quantity;
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

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getCounterId() {
        return counterId;
    }

    public void setCounterId(Object counterId) {
        this.counterId = counterId;
    }

    public Object getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Object categoryId) {
        this.categoryId = categoryId;
    }

    public Object getProductId() {
        return productId;
    }

    public void setProductId(Object productId) {
        this.productId = productId;
    }

    public Object getDesignId() {
        return designId;
    }

    public void setDesignId(Object designId) {
        this.designId = designId;
    }

    public Object getPurityId() {
        return purityId;
    }

    public void setPurityId(Object purityId) {
        this.purityId = purityId;
    }

    public Object getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Object companyId) {
        this.companyId = companyId;
    }

    public Object getBranchId() {
        return branchId;
    }

    public void setBranchId(Object branchId) {
        this.branchId = branchId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getPurityName() {
        return purityName;
    }

    public void setPurityName(String purityName) {
        this.purityName = purityName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Integer grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Integer getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Integer netWeight) {
        this.netWeight = netWeight;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

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

}

