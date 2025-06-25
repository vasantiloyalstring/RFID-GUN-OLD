package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Match {
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
    private Integer counterId;
    @SerializedName("CategoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("ProductId")
    @Expose
    private Integer productId;
    @SerializedName("DesignId")
    @Expose
    private Integer designId;
    @SerializedName("PurityId")
    @Expose
    private Integer purityId;
    @SerializedName("CompanyId")
    @Expose
    private Integer companyId;
    @SerializedName("BranchId")
    @Expose
    private Integer branchId;
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
    private double grossWeight;
    @SerializedName("NetWeight")
    @Expose
    private double netWeight;
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

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getDesignId() {
        return designId;
    }

    public void setDesignId(Integer designId) {
        this.designId = designId;
    }

    public Integer getPurityId() {
        return purityId;
    }

    public void setPurityId(Integer purityId) {
        this.purityId = purityId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
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

    public double getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(double grossWeight) {
        this.grossWeight = grossWeight;
    }

    public double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(double netWeight) {
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