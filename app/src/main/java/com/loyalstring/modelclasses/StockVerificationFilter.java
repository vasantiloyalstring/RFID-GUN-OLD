package com.loyalstring.modelclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockVerificationFilter {

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
    @SerializedName("GrossWeight")
    @Expose
    private String grossWeight;
    @SerializedName("NetWeight")
    @Expose
    private String netWeight;
    @SerializedName("Quantity")
    @Expose
    private String quantity;
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

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}
