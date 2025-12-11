package com.loyalstring.apiresponse;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlllabelResponse {

    private List<LabelItem> items;

    // Getters and Setters for the list of items
    public List<LabelItem> getItems() {
        return items;
    }

    public void setItems(List<LabelItem> items) {
        this.items = items;
    }


    // Stone.java
    public class Stone1 {
        private int Id;
        private String StoneName;
        private String StoneWeight;
        private String StonePieces;
        private String StoneRate;
        private String StoneAmount;
        private String Description;
        private String ClientCode;
        private int LabelledStockId;
        private int CompanyId;
        private int CounterId;
        private int BranchId;
        private int EmployeeId;
        private String CreatedOn;
        private String LastUpdated;
        private String StoneLessPercent;
        private  String counterName;
        public String  designName;


        public String getDesignName() {
            return designName;
        }

        public void setDesignName(String designName) {
            this.designName = designName;
        }




        public String getCounterName() {
            return counterName;
        }

        public void setCounterName(String counterName) {
            this.counterName = counterName;
        }



        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getStoneName() {
            return StoneName;
        }

        public void setStoneName(String stoneName) {
            StoneName = stoneName;
        }

        public String getStoneWeight() {
            return StoneWeight;
        }

        public void setStoneWeight(String stoneWeight) {
            StoneWeight = stoneWeight;
        }

        public String getStonePieces() {
            return StonePieces;
        }

        public void setStonePieces(String stonePieces) {
            StonePieces = stonePieces;
        }

        public String getStoneRate() {
            return StoneRate;
        }

        public void setStoneRate(String stoneRate) {
            StoneRate = stoneRate;
        }

        public String getStoneAmount() {
            return StoneAmount;
        }

        public void setStoneAmount(String stoneAmount) {
            StoneAmount = stoneAmount;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getClientCode() {
            return ClientCode;
        }

        public void setClientCode(String clientCode) {
            ClientCode = clientCode;
        }

        public int getLabelledStockId() {
            return LabelledStockId;
        }

        public void setLabelledStockId(int labelledStockId) {
            LabelledStockId = labelledStockId;
        }

        public int getCompanyId() {
            return CompanyId;
        }

        public void setCompanyId(int companyId) {
            CompanyId = companyId;
        }

        public int getCounterId() {
            return CounterId;
        }

        public void setCounterId(int counterId) {
            CounterId = counterId;
        }

        public int getBranchId() {
            return BranchId;
        }

        public void setBranchId(int branchId) {
            BranchId = branchId;
        }

        public int getEmployeeId() {
            return EmployeeId;
        }

        public void setEmployeeId(int employeeId) {
            EmployeeId = employeeId;
        }

        public String getCreatedOn() {
            return CreatedOn;
        }

        public void setCreatedOn(String createdOn) {
            CreatedOn = createdOn;
        }

        public String getLastUpdated() {
            return LastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            LastUpdated = lastUpdated;
        }

        public String getStoneLessPercent() {
            return StoneLessPercent;
        }

        public void setStoneLessPercent(String stoneLessPercent) {
            StoneLessPercent = stoneLessPercent;
        }


        // Getters and Setters
    }

    // Item.java
    public static class LabelItem {
        @SerializedName("Id")
        public int id1;
        @SerializedName("SKUId")
        public int sKUId;
        @SerializedName("ProductTitle")
        public String productTitle;
        @SerializedName("ClipWeight")
        public String clipWeight;
        @SerializedName("ClipQuantity")
        public String clipQuantity;
        @SerializedName("ItemCode")
        public String itemCode;
        @SerializedName("HSNCode")
        public String hSNCode;
        @SerializedName("Description")
        public String description;
        @SerializedName("ProductCode")
        public String productCode;
        @SerializedName("MetalName")
        public String metalName;
        @SerializedName("CategoryId")
        public int categoryId;
        @SerializedName("ProductId")
        public int productId;
        @SerializedName("DesignId")
        public int designId;
        @SerializedName("PurityId")
        public int purityId;
        @SerializedName("Colour")
        public String colour;
        @SerializedName("Size")
        public String size;
        @SerializedName("WeightCategory")
        public String weightCategory;
        @SerializedName("GrossWt")
        public String grossWt;
        @SerializedName("NetWt")
        public String netWt;
        @SerializedName("CollectionName")
        public String collectionName;
        @SerializedName("OccassionName")
        public String occassionName;
        @SerializedName("Gender")
        public String gender;
        @SerializedName("MakingFixedAmt")
        public String makingFixedAmt;
        @SerializedName("MakingPerGram")
        public String makingPerGram;
        @SerializedName("MakingFixedWastage")
        public String makingFixedWastage;
        @SerializedName("MakingPercentage")
        public String makingPercentage;
        @SerializedName("TotalStoneWeight")
        public String totalStoneWeight;
        @SerializedName("TotalStoneAmount")
        public String totalStoneAmount;
        @SerializedName("TotalStonePieces")
        public String totalStonePieces;
        @SerializedName("TotalDiamondWeight")
        public String totalDiamondWeight;
        @SerializedName("TotalDiamondPieces")
        public String totalDiamondPieces;
        @SerializedName("TotalDiamondAmount")
        public String totalDiamondAmount;
        @SerializedName("Featured")
        public String featured;
        @SerializedName("Pieces")
        public String pieces;
        @SerializedName("HallmarkAmount")
        public String hallmarkAmount;
        @SerializedName("HUIDCode")
        public String hUIDCode;
        @SerializedName("MRP")
        public String mRP;
        @SerializedName("VendorId")
        public int vendorId;
        @SerializedName("VendorName")
        public String vendorName;
        @SerializedName("FirmName")
        public String firmName;
        @SerializedName("BoxId")
        public int boxId;
        @SerializedName("TIDNumber")
        public String tIDNumber;
        @SerializedName("RFIDCode")
        public String rFIDCode;
        @SerializedName("FinePercent")
        public String finePercent;
        @SerializedName("WastagePercent")
        public String wastagePercent;
        @SerializedName("Images")
        public String images;
        @SerializedName("BlackBeads")
        public String blackBeads;
        @SerializedName("Height")
        public String height;
        @SerializedName("Width")
        public String width;
        @SerializedName("OrderedItemId")
        public String orderedItemId;
        @SerializedName("CuttingGrossWt")
        public String cuttingGrossWt;
        @SerializedName("CuttingNetWt")
        public String cuttingNetWt;
        @SerializedName("MetalRate")
        public String metalRate;
        @SerializedName("LotNumber")
        public String lotNumber;
        @SerializedName("DeptId")
        public int deptId;
        @SerializedName("PurchaseCost")
        public String purchaseCost;
        @SerializedName("Margin")
        public String margin;
        @SerializedName("BranchName")
        public String branchName;
        @SerializedName("BoxName")
        public String boxName;
        @SerializedName("EstimatedDays")
        public String estimatedDays;
        @SerializedName("OfferPrice")
        public String offerPrice;
        @SerializedName("Rating")
        public String rating;
        @SerializedName("SKU")
        public String sKU;
        @SerializedName("Ranking")
        public String ranking;
        @SerializedName("CompanyId")
        public int companyId;
        @SerializedName("CounterId")
        public int counterId;
        @SerializedName("BranchId")
        public int branchId;
        @SerializedName("EmployeeId")
        public int employeeId;
        @SerializedName("Status")
        public String status;
        @SerializedName("ClientCode")
        public String clientCode;
        @SerializedName("UpdatedFrom")
        public String updatedFrom;
        public int count;
        @SerializedName("MetalId")
        public int metalId;
        @SerializedName("WarehouseId")
        public int warehouseId;
        @SerializedName("CreatedOn")
        public String createdOn;
        @SerializedName("LastUpdated")
        public String lastUpdated;
        @SerializedName("TaxId")
        public int taxId;
        @SerializedName("TaxPercentage")
        public String taxPercentage;
        @SerializedName("OtherWeight")
        public String otherWeight;
        @SerializedName("PouchWeight")
        public String pouchWeight;
        @SerializedName("CategoryName")
        public String categoryName;
        @SerializedName("PurityName")
        public String purityName;
        @SerializedName("TodaysRate")
        public String todaysRate;
        @SerializedName("ProductName")
        public String productName;
        @SerializedName("DesignName")
        public String designName;
        @SerializedName("DiamondSize")
        public String diamondSize;
        @SerializedName("DiamondWeight")
        public String diamondWeight;
        @SerializedName("DiamondPurchaseRate")
        public String diamondPurchaseRate;
        @SerializedName("DiamondSellRate")
        public String diamondSellRate;
        @SerializedName("DiamondClarity")
        public String diamondClarity;
        @SerializedName("DiamondColour")
        public String diamondColour;
        @SerializedName("DiamondShape")
        public String diamondShape;
        @SerializedName("DiamondCut")
        public String diamondCut;
        @SerializedName("DiamondSettingType")
        public String diamondSettingType;
        @SerializedName("DiamondCertificate")
        public String diamondCertificate;
        @SerializedName("DiamondPieces")
        public String diamondPieces;
        @SerializedName("DiamondPurchaseAmount")
        public String diamondPurchaseAmount;
        @SerializedName("DiamondSellAmount")
        public String diamondSellAmount;
        @SerializedName("DiamondDescription")
        public String diamondDescription;
        @SerializedName("TagWeight")
        public String tagWeight;
        @SerializedName("FindingWeight")
        public String findingWeight;
        @SerializedName("LanyardWeight")
        public String lanyardWeight;
        @SerializedName("PacketId")
        public int packetId;
        @SerializedName("PacketName")
        public String packetName;
        @SerializedName("Stones")
        public ArrayList<Stone> stones;
        @SerializedName("Diamonds")
        public ArrayList<Object> diamonds;
        //        RecordsCount
        @SerializedName("RecordsCount")
        public int recordsCount;

        public String getCounterName() {
            return counterName;
        }

        public void setCounterName(String counterName) {
            this.counterName = counterName;
        }

        @SerializedName("CounterName")
        public String counterName;


/*    @SerializedName("CategoryId")
        public int CategoryId;
        @SerializedName("ProductId")
        public int ProductId;
        @SerializedName("DesignId")
        public int DesignId;

        @SerializedName("PurityId")
        public int PurityId;
*/





        public int getRecordsCount() {
            return recordsCount;
        }

        public void setRecordsCount(int recordsCount) {
            this.recordsCount = recordsCount;
        }

        public LabelItem() {
        }

        public int getId1() {
            return id1;
        }

        public void setId1(int id1) {
            this.id1 = id1;
        }

        public int getsKUId() {
            return sKUId;
        }

        public void setsKUId(int sKUId) {
            this.sKUId = sKUId;
        }

        public String getProductTitle() {
            return productTitle;
        }

        public void setProductTitle(String productTitle) {
            this.productTitle = productTitle;
        }

        public String getClipWeight() {
            return clipWeight;
        }

        public void setClipWeight(String clipWeight) {
            this.clipWeight = clipWeight;
        }

        public String getClipQuantity() {
            return clipQuantity;
        }

        public void setClipQuantity(String clipQuantity) {
            this.clipQuantity = clipQuantity;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String gethSNCode() {
            return hSNCode;
        }

        public void sethSNCode(String hSNCode) {
            this.hSNCode = hSNCode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getMetalName() {
            return metalName;
        }

        public void setMetalName(String metalName) {
            this.metalName = metalName;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getDesignId() {
            return designId;
        }

        public void setDesignId(int designId) {
            this.designId = designId;
        }

        public int getPurityId() {
            return purityId;
        }

        public void setPurityId(int purityId) {
            this.purityId = purityId;
        }

        public String getColour() {
            return colour;
        }

        public void setColour(String colour) {
            this.colour = colour;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getWeightCategory() {
            return weightCategory;
        }

        public void setWeightCategory(String weightCategory) {
            this.weightCategory = weightCategory;
        }

        public String getGrossWt() {
            return grossWt;
        }

        public void setGrossWt(String grossWt) {
            this.grossWt = grossWt;
        }

        public String getNetWt() {
            return netWt;
        }

        public void setNetWt(String netWt) {
            this.netWt = netWt;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }

        public String getOccassionName() {
            return occassionName;
        }

        public void setOccassionName(String occassionName) {
            this.occassionName = occassionName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getMakingFixedAmt() {
            return makingFixedAmt;
        }

        public void setMakingFixedAmt(String makingFixedAmt) {
            this.makingFixedAmt = makingFixedAmt;
        }

        public String getMakingPerGram() {
            return makingPerGram;
        }

        public void setMakingPerGram(String makingPerGram) {
            this.makingPerGram = makingPerGram;
        }

        public String getMakingFixedWastage() {
            return makingFixedWastage;
        }

        public void setMakingFixedWastage(String makingFixedWastage) {
            this.makingFixedWastage = makingFixedWastage;
        }

        public String getMakingPercentage() {
            return makingPercentage;
        }

        public void setMakingPercentage(String makingPercentage) {
            this.makingPercentage = makingPercentage;
        }

        public String getTotalStoneWeight() {
            return totalStoneWeight;
        }

        public void setTotalStoneWeight(String totalStoneWeight) {
            this.totalStoneWeight = totalStoneWeight;
        }

        public String getTotalStoneAmount() {
            return totalStoneAmount;
        }

        public void setTotalStoneAmount(String totalStoneAmount) {
            this.totalStoneAmount = totalStoneAmount;
        }

        public String getTotalStonePieces() {
            return totalStonePieces;
        }

        public void setTotalStonePieces(String totalStonePieces) {
            this.totalStonePieces = totalStonePieces;
        }

        public String getTotalDiamondWeight() {
            return totalDiamondWeight;
        }

        public void setTotalDiamondWeight(String totalDiamondWeight) {
            this.totalDiamondWeight = totalDiamondWeight;
        }

        public String getTotalDiamondPieces() {
            return totalDiamondPieces;
        }

        public void setTotalDiamondPieces(String totalDiamondPieces) {
            this.totalDiamondPieces = totalDiamondPieces;
        }

        public String getTotalDiamondAmount() {
            return totalDiamondAmount;
        }

        public void setTotalDiamondAmount(String totalDiamondAmount) {
            this.totalDiamondAmount = totalDiamondAmount;
        }

        public String getFeatured() {
            return featured;
        }

        public void setFeatured(String featured) {
            this.featured = featured;
        }

        public String getPieces() {
            return pieces;
        }

        public void setPieces(String pieces) {
            this.pieces = pieces;
        }

        public String getHallmarkAmount() {
            return hallmarkAmount;
        }

        public void setHallmarkAmount(String hallmarkAmount) {
            this.hallmarkAmount = hallmarkAmount;
        }

        public String gethUIDCode() {
            return hUIDCode;
        }

        public void sethUIDCode(String hUIDCode) {
            this.hUIDCode = hUIDCode;
        }

        public String getmRP() {
            return mRP;
        }

        public void setmRP(String mRP) {
            this.mRP = mRP;
        }

        public int getVendorId() {
            return vendorId;
        }

        public void setVendorId(int vendorId) {
            this.vendorId = vendorId;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getFirmName() {
            return firmName;
        }

        public void setFirmName(String firmName) {
            this.firmName = firmName;
        }

        public int getBoxId() {
            return boxId;
        }

        public void setBoxId(int boxId) {
            this.boxId = boxId;
        }

        public String gettIDNumber() {
            return tIDNumber;
        }

        public void settIDNumber(String tIDNumber) {
            this.tIDNumber = tIDNumber;
        }

        public String getrFIDCode() {
            return rFIDCode;
        }

        public void setrFIDCode(String rFIDCode) {
            this.rFIDCode = rFIDCode;
        }

        public String getFinePercent() {
            return finePercent;
        }

        public void setFinePercent(String finePercent) {
            this.finePercent = finePercent;
        }

        public String getWastagePercent() {
            return wastagePercent;
        }

        public void setWastagePercent(String wastagePercent) {
            this.wastagePercent = wastagePercent;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public String getBlackBeads() {
            return blackBeads;
        }

        public void setBlackBeads(String blackBeads) {
            this.blackBeads = blackBeads;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getOrderedItemId() {
            return orderedItemId;
        }

        public void setOrderedItemId(String orderedItemId) {
            this.orderedItemId = orderedItemId;
        }

        public String getCuttingGrossWt() {
            return cuttingGrossWt;
        }

        public void setCuttingGrossWt(String cuttingGrossWt) {
            this.cuttingGrossWt = cuttingGrossWt;
        }

        public String getCuttingNetWt() {
            return cuttingNetWt;
        }

        public void setCuttingNetWt(String cuttingNetWt) {
            this.cuttingNetWt = cuttingNetWt;
        }

        public String getMetalRate() {
            return metalRate;
        }

        public void setMetalRate(String metalRate) {
            this.metalRate = metalRate;
        }

        public String getLotNumber() {
            return lotNumber;
        }

        public void setLotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
        }

        public int getDeptId() {
            return deptId;
        }

        public void setDeptId(int deptId) {
            this.deptId = deptId;
        }

        public String getPurchaseCost() {
            return purchaseCost;
        }

        public void setPurchaseCost(String purchaseCost) {
            this.purchaseCost = purchaseCost;
        }

        public String getMargin() {
            return margin;
        }

        public void setMargin(String margin) {
            this.margin = margin;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getBoxName() {
            return boxName;
        }

        public void setBoxName(String boxName) {
            this.boxName = boxName;
        }

        public String getEstimatedDays() {
            return estimatedDays;
        }

        public void setEstimatedDays(String estimatedDays) {
            this.estimatedDays = estimatedDays;
        }

        public String getOfferPrice() {
            return offerPrice;
        }

        public void setOfferPrice(String offerPrice) {
            this.offerPrice = offerPrice;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getsKU() {
            return sKU;
        }

        public void setsKU(String sKU) {
            this.sKU = sKU;
        }

        public String getRanking() {
            return ranking;
        }

        public void setRanking(String ranking) {
            this.ranking = ranking;
        }

        public int getCompanyId() {
            return companyId;
        }

        public void setCompanyId(int companyId) {
            this.companyId = companyId;
        }

        public int getCounterId() {
            return counterId;
        }



        public void setCounterId(int counterId) {
            this.counterId = counterId;
        }



        public int getBranchId() {
            return branchId;
        }

        public void setBranchId(int branchId) {
            this.branchId = branchId;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getClientCode() {
            return clientCode;
        }

        public void setClientCode(String clientCode) {
            this.clientCode = clientCode;
        }

        public String getUpdatedFrom() {
            return updatedFrom;
        }

        public void setUpdatedFrom(String updatedFrom) {
            this.updatedFrom = updatedFrom;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getMetalId() {
            return metalId;
        }

        public void setMetalId(int metalId) {
            this.metalId = metalId;
        }

        public int getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(int warehouseId) {
            this.warehouseId = warehouseId;
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

        public int getTaxId() {
            return taxId;
        }

        public void setTaxId(int taxId) {
            this.taxId = taxId;
        }

        public String getTaxPercentage() {
            return taxPercentage;
        }

        public void setTaxPercentage(String taxPercentage) {
            this.taxPercentage = taxPercentage;
        }

        public String getOtherWeight() {
            return otherWeight;
        }

        public void setOtherWeight(String otherWeight) {
            this.otherWeight = otherWeight;
        }

        public String getPouchWeight() {
            return pouchWeight;
        }

        public void setPouchWeight(String pouchWeight) {
            this.pouchWeight = pouchWeight;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getPurityName() {
            return purityName;
        }

        public void setPurityName(String purityName) {
            this.purityName = purityName;
        }

        public String getTodaysRate() {
            return todaysRate;
        }

        public void setTodaysRate(String todaysRate) {
            this.todaysRate = todaysRate;
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

        public String getDiamondSize() {
            return diamondSize;
        }

        public void setDiamondSize(String diamondSize) {
            this.diamondSize = diamondSize;
        }

        public String getDiamondWeight() {
            return diamondWeight;
        }

        public void setDiamondWeight(String diamondWeight) {
            this.diamondWeight = diamondWeight;
        }

        public String getDiamondPurchaseRate() {
            return diamondPurchaseRate;
        }

        public void setDiamondPurchaseRate(String diamondPurchaseRate) {
            this.diamondPurchaseRate = diamondPurchaseRate;
        }

        public String getDiamondSellRate() {
            return diamondSellRate;
        }

        public void setDiamondSellRate(String diamondSellRate) {
            this.diamondSellRate = diamondSellRate;
        }

        public String getDiamondClarity() {
            return diamondClarity;
        }

        public void setDiamondClarity(String diamondClarity) {
            this.diamondClarity = diamondClarity;
        }

        public String getDiamondColour() {
            return diamondColour;
        }

        public void setDiamondColour(String diamondColour) {
            this.diamondColour = diamondColour;
        }

        public String getDiamondShape() {
            return diamondShape;
        }

        public void setDiamondShape(String diamondShape) {
            this.diamondShape = diamondShape;
        }

        public String getDiamondCut() {
            return diamondCut;
        }

        public void setDiamondCut(String diamondCut) {
            this.diamondCut = diamondCut;
        }

        public String getDiamondSettingType() {
            return diamondSettingType;
        }

        public void setDiamondSettingType(String diamondSettingType) {
            this.diamondSettingType = diamondSettingType;
        }

        public String getDiamondCertificate() {
            return diamondCertificate;
        }

        public void setDiamondCertificate(String diamondCertificate) {
            this.diamondCertificate = diamondCertificate;
        }

        public String getDiamondPieces() {
            return diamondPieces;
        }

        public void setDiamondPieces(String diamondPieces) {
            this.diamondPieces = diamondPieces;
        }

        public String getDiamondPurchaseAmount() {
            return diamondPurchaseAmount;
        }

        public void setDiamondPurchaseAmount(String diamondPurchaseAmount) {
            this.diamondPurchaseAmount = diamondPurchaseAmount;
        }

        public String getDiamondSellAmount() {
            return diamondSellAmount;
        }

        public void setDiamondSellAmount(String diamondSellAmount) {
            this.diamondSellAmount = diamondSellAmount;
        }

        public String getDiamondDescription() {
            return diamondDescription;
        }

        public void setDiamondDescription(String diamondDescription) {
            this.diamondDescription = diamondDescription;
        }

        public String getTagWeight() {
            return tagWeight;
        }

        public void setTagWeight(String tagWeight) {
            this.tagWeight = tagWeight;
        }

        public String getFindingWeight() {
            return findingWeight;
        }

        public void setFindingWeight(String findingWeight) {
            this.findingWeight = findingWeight;
        }

        public String getLanyardWeight() {
            return lanyardWeight;
        }

        public void setLanyardWeight(String lanyardWeight) {
            this.lanyardWeight = lanyardWeight;
        }

        public int getPacketId() {
            return packetId;
        }

        public void setPacketId(int packetId) {
            this.packetId = packetId;
        }

        public String getPacketName() {
            return packetName;
        }

        public void setPacketName(String packetName) {
            this.packetName = packetName;
        }

        public ArrayList<Stone> getStones() {
            return stones;
        }

        public void setStones(ArrayList<Stone> stones) {
            this.stones = stones;
        }

        public ArrayList<Object> getDiamonds() {
            return diamonds;
        }

        public void setDiamonds(ArrayList<Object> diamonds) {
            this.diamonds = diamonds;
        }


        @Override
        public String toString() {
            return "LabelItem{" +
                    "id1=" + id1 +
                    ", sKUId=" + sKUId +
                    ", productTitle='" + productTitle + '\'' +
                    ", clipWeight='" + clipWeight + '\'' +
                    ", clipQuantity='" + clipQuantity + '\'' +
                    ", itemCode='" + itemCode + '\'' +
                    ", hSNCode='" + hSNCode + '\'' +
                    ", description='" + description + '\'' +
                    ", productCode='" + productCode + '\'' +
                    ", metalName='" + metalName + '\'' +
                    ", categoryId=" + categoryId +
                    ", productId=" + productId +
                    ", designId=" + designId +
                    ", purityId=" + purityId +
                    ", colour='" + colour + '\'' +
                    ", size='" + size + '\'' +
                    ", weightCategory='" + weightCategory + '\'' +
                    ", grossWt='" + grossWt + '\'' +
                    ", netWt='" + netWt + '\'' +
                    ", collectionName='" + collectionName + '\'' +
                    ", occassionName='" + occassionName + '\'' +
                    ", gender='" + gender + '\'' +
                    ", makingFixedAmt='" + makingFixedAmt + '\'' +
                    ", makingPerGram='" + makingPerGram + '\'' +
                    ", makingFixedWastage='" + makingFixedWastage + '\'' +
                    ", makingPercentage='" + makingPercentage + '\'' +
                    ", totalStoneWeight='" + totalStoneWeight + '\'' +
                    ", totalStoneAmount='" + totalStoneAmount + '\'' +
                    ", totalStonePieces='" + totalStonePieces + '\'' +
                    ", totalDiamondWeight='" + totalDiamondWeight + '\'' +
                    ", totalDiamondPieces='" + totalDiamondPieces + '\'' +
                    ", totalDiamondAmount='" + totalDiamondAmount + '\'' +
                    ", featured='" + featured + '\'' +
                    ", pieces='" + pieces + '\'' +
                    ", hallmarkAmount='" + hallmarkAmount + '\'' +
                    ", hUIDCode='" + hUIDCode + '\'' +
                    ", mRP='" + mRP + '\'' +
                    ", vendorId=" + vendorId +
                    ", vendorName='" + vendorName + '\'' +
                    ", firmName='" + firmName + '\'' +
                    ", boxId=" + boxId +
                    ", tIDNumber='" + tIDNumber + '\'' +
                    ", rFIDCode='" + rFIDCode + '\'' +
                    ", finePercent='" + finePercent + '\'' +
                    ", wastagePercent='" + wastagePercent + '\'' +
                    ", images='" + images + '\'' +
                    ", blackBeads='" + blackBeads + '\'' +
                    ", height='" + height + '\'' +
                    ", width='" + width + '\'' +
                    ", orderedItemId='" + orderedItemId + '\'' +
                    ", cuttingGrossWt='" + cuttingGrossWt + '\'' +
                    ", cuttingNetWt='" + cuttingNetWt + '\'' +
                    ", metalRate='" + metalRate + '\'' +
                    ", lotNumber='" + lotNumber + '\'' +
                    ", deptId=" + deptId +
                    ", purchaseCost='" + purchaseCost + '\'' +
                    ", margin='" + margin + '\'' +
                    ", branchName='" + branchName + '\'' +
                    ", boxName='" + boxName + '\'' +
                    ", estimatedDays='" + estimatedDays + '\'' +
                    ", offerPrice='" + offerPrice + '\'' +
                    ", rating='" + rating + '\'' +
                    ", sKU='" + sKU + '\'' +
                    ", ranking='" + ranking + '\'' +
                    ", companyId=" + companyId +
                    ", counterId=" + counterId +
                    ", branchId=" + branchId +
                    ", employeeId=" + employeeId +
                    ", status='" + status + '\'' +
                    ", clientCode='" + clientCode + '\'' +
                    ", updatedFrom='" + updatedFrom + '\'' +
                    ", count=" + count +
                    ", metalId=" + metalId +
                    ", warehouseId=" + warehouseId +
                    ", createdOn='" + createdOn + '\'' +
                    ", lastUpdated='" + lastUpdated + '\'' +
                    ", taxId=" + taxId +
                    ", taxPercentage='" + taxPercentage + '\'' +
                    ", otherWeight='" + otherWeight + '\'' +
                    ", pouchWeight='" + pouchWeight + '\'' +
                    ", categoryName='" + categoryName + '\'' +
                    ", purityName='" + purityName + '\'' +
                    ", todaysRate='" + todaysRate + '\'' +
                    ", productName='" + productName + '\'' +
                    ", designName='" + designName + '\'' +
                    ", diamondSize='" + diamondSize + '\'' +
                    ", diamondWeight='" + diamondWeight + '\'' +
                    ", diamondPurchaseRate='" + diamondPurchaseRate + '\'' +
                    ", diamondSellRate='" + diamondSellRate + '\'' +
                    ", diamondClarity='" + diamondClarity + '\'' +
                    ", diamondColour='" + diamondColour + '\'' +
                    ", diamondShape='" + diamondShape + '\'' +
                    ", diamondCut='" + diamondCut + '\'' +
                    ", diamondSettingType='" + diamondSettingType + '\'' +
                    ", diamondCertificate='" + diamondCertificate + '\'' +
                    ", diamondPieces='" + diamondPieces + '\'' +
                    ", diamondPurchaseAmount='" + diamondPurchaseAmount + '\'' +
                    ", diamondSellAmount='" + diamondSellAmount + '\'' +
                    ", diamondDescription='" + diamondDescription + '\'' +
                    ", tagWeight='" + tagWeight + '\'' +
                    ", findingWeight='" + findingWeight + '\'' +
                    ", lanyardWeight='" + lanyardWeight + '\'' +
                    ", packetId=" + packetId +
                    ", packetName='" + packetName + '\'' +
                    ", stones=" + stones +
                    ", diamonds=" + diamonds +
                    ", recordsCount=" + recordsCount +
                    '}';
        }
    }

    public class Stone {
        @SerializedName("Id")
        public int id;
        @SerializedName("StoneName")
        public String stoneName;
        @SerializedName("StoneWeight")
        public String stoneWeight;
        @SerializedName("StonePieces")
        public String stonePieces;
        @SerializedName("StoneRate")
        public String stoneRate;
        @SerializedName("StoneAmount")
        public String stoneAmount;
        @SerializedName("Description")
        public String description;
        @SerializedName("ClientCode")
        public String clientCode;
        @SerializedName("LabelledStockId")
        public int labelledStockId;
        @SerializedName("CompanyId")
        public int companyId;
        @SerializedName("CounterId")
        public int counterId;
        @SerializedName("BranchId")
        public int branchId;
        @SerializedName("EmployeeId")
        public int employeeId;
        @SerializedName("CreatedOn")
        public String createdOn;
        @SerializedName("LastUpdated")
        public String lastUpdated;
        @SerializedName("StoneLessPercent")
        public String stoneLessPercent;
    }


}
