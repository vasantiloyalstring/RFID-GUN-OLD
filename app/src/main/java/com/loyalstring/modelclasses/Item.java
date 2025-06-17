package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("ItemCode")
    @Expose
    private String itemCode;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("GrossWeight")
    @Expose
    private Integer grossWeight;
    @SerializedName("NetWeight")
    @Expose
    private Integer netWeight;
    @SerializedName("Quantity")
    @Expose
    private Integer quantity;
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

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

}
