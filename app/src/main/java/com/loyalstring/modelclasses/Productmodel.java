package com.loyalstring.modelclasses;

import com.google.gson.annotations.SerializedName;

public class Productmodel {

    @SerializedName("sku")
    private String sku;

    @SerializedName("product_Name")
    private String productName;

    @SerializedName("label")
    private String label;

    @SerializedName("hsn_code")
    private String hsnCode;

    @SerializedName("description")
    private String description;

    @SerializedName("minQuantity")
    private int minQuantity;

    @SerializedName("images")
    private String images;

    @SerializedName("minWeight")
    private double minWeight;

    @SerializedName("purityId")
    private int purityId;

    @SerializedName("tblPurity")
    private Purity tblPurity;

    @SerializedName("ItemCode")
    private String itemCode;


    //new values
    @SerializedName("product_No")
    private String product_No;
    @SerializedName("category_id")
    private String category_id;
    @SerializedName("tblMaterialCategory")
    private Materialcategory materialcategory;
    @SerializedName("material")
    private String material;
    @SerializedName("gm")
    private String gm;
    @SerializedName("size")
    private String size;
    @SerializedName("GrossWt")
    private String grosswt;
    @SerializedName("Purity")
    private String purity;
    @SerializedName("collection")
    private String collection;
    @SerializedName("occasion")
    private String occasion;
    @SerializedName("gender")
    private String gender;
    @SerializedName("product_type")
    private String product_type;
    @SerializedName("making_Percentage")
    private String making_Percentage;
    @SerializedName("making_Fixed_Amt")
    private String making_Fixed_Amt;
    @SerializedName("making_Fixed_Wastage")
    private String making_Fixed_Wastage;
    @SerializedName("making_per_gram")
    private String making_per_gram;
    @SerializedName("StoneWeight")
    private String stoneWeight;
    @SerializedName("stoneAmount")
    private String stoneAmount;
    @SerializedName("featured")
    private String featured;
    @SerializedName("ItemType")
    private String itemType;
    @SerializedName("Category_Name")
    private String category_Name;
    @SerializedName("pieces")
    private String pieces;
    @SerializedName("huidCode")
    private String huidCode;
    @SerializedName("NetWt")
    private String netWt;
    @SerializedName("product_Code")
    private String product_Code;
    @SerializedName("mrp")
    private String mrp;
    @SerializedName("quantity")
    private String quantity;
    @SerializedName("productTypeId")
    private String productTypeId;
    @SerializedName("tblProductType")
    private String tblProductType;
    @SerializedName("collectionId")
    private String collectionId;
    @SerializedName("tblCollection")
    private String tblCollection;
    @SerializedName("partyTypeId")
    private String partyTypeId;
    @SerializedName("party_Details")
    private Partydetails partydetails;
    @SerializedName("boxId")
    private String boxId;
    @SerializedName("tblBox")
    private Tblbox tblbox;
    @SerializedName("onlineStatus")
    private String onlineStatus;
    @SerializedName("tid")
    private String tid;
    @SerializedName("BarcodeNumber")
    private String barcodeNumber;
    @SerializedName("diamondWeight")
    private String diamondWeight;
    @SerializedName("diamondPeaces")
    private String diamondPeaces;
    @SerializedName("diamondRate")
    private String diamondRate;
    @SerializedName("diamondAmount")
    private String diamondAmount;
    @SerializedName("colour")
    private String colour;
    @SerializedName("clarity")
    private String clarity;
    @SerializedName("settingType")
    private String settingType;
    @SerializedName("shape")
    private String shape;
    @SerializedName("diamondSize")
    private String diamondSize;
    @SerializedName("certificate")
    private String certificate;
    @SerializedName("count")
    private String count;
    @SerializedName("branchName")
    private String branchName;
    @SerializedName("boxName")
    private String boxName;
    @SerializedName("hallmark")
    private String hallmark;
    @SerializedName("hallmark_amt")
    private String hallmark_amt;
    @SerializedName("stoneWeight1")
    private String stoneWeight1;
    @SerializedName("stoneAmount1")
    private String stoneAmount1;
    @SerializedName("stoneWeight2")
    private String stoneWeight2;
    @SerializedName("stoneAmount2")
    private String stoneAmount2;
    @SerializedName("stoneWeight3")
    private String stoneWeight3;
    @SerializedName("stoneAmount3")
    private String stoneAmount3;
    @SerializedName("stoneWeight4")
    private String stoneWeight4;
    @SerializedName("stoneAmount4")
    private String stoneAmount4;
    @SerializedName("stoneName1")
    private String stoneName1;
    @SerializedName("stoneName2")
    private String stoneName2;
    @SerializedName("stoneName3")
    private String stoneName3;
    @SerializedName("stoneName4")
    private String stoneName4;
    @SerializedName("finePlusWastage")
    private String finePlusWastage;
    @SerializedName("cuttingGrossWt")
    private String cuttingGrossWt;
    @SerializedName("cuttingNetWt")
    private String cuttingNetWt;
    @SerializedName("inwardNo")
    private String inwardNo;
    @SerializedName("id")
    private String id;
    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("lastUpdated")
    private String lastUpdated;

    @SerializedName("statusType")
    private String statusType;


    @SerializedName("imageurl")
    private String imageurl;

    @SerializedName("pcs")
    private String pcs;
    @SerializedName("DesignName")
    private String designName;

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPcs() {
        return pcs;
    }

    public void setPcs(String pcs) {
        this.pcs = pcs;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public double getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(double minWeight) {
        this.minWeight = minWeight;
    }

    public int getPurityId() {
        return purityId;
    }

    public void setPurityId(int purityId) {
        this.purityId = purityId;
    }

    public Purity getTblPurity() {
        return tblPurity;
    }

    public void setTblPurity(Purity tblPurity) {
        this.tblPurity = tblPurity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getProduct_No() {
        return product_No;
    }

    public void setProduct_No(String product_No) {
        this.product_No = product_No;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public Materialcategory getMaterialcategory() {
        return materialcategory;
    }

    public void setMaterialcategory(Materialcategory materialcategory) {
        this.materialcategory = materialcategory;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getGm() {
        return gm;
    }

    public void setGm(String gm) {
        this.gm = gm;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGrosswt() {
        return grosswt;
    }

    public void setGrosswt(String grosswt) {
        this.grosswt = grosswt;
    }

    public String getPurity() {
        return purity;
    }

    public void setPurity(String purity) {
        this.purity = purity;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getMaking_Percentage() {
        return making_Percentage;
    }

    public void setMaking_Percentage(String making_Percentage) {
        this.making_Percentage = making_Percentage;
    }

    public String getMaking_Fixed_Amt() {
        return making_Fixed_Amt;
    }

    public void setMaking_Fixed_Amt(String making_Fixed_Amt) {
        this.making_Fixed_Amt = making_Fixed_Amt;
    }

    public String getMaking_Fixed_Wastage() {
        return making_Fixed_Wastage;
    }

    public void setMaking_Fixed_Wastage(String making_Fixed_Wastage) {
        this.making_Fixed_Wastage = making_Fixed_Wastage;
    }

    public String getMaking_per_gram() {
        return making_per_gram;
    }

    public void setMaking_per_gram(String making_per_gram) {
        this.making_per_gram = making_per_gram;
    }

    public String getStoneWeight() {
        return stoneWeight;
    }

    public void setStoneWeight(String stoneWeight) {
        this.stoneWeight = stoneWeight;
    }

    public String getStoneAmount() {
        return stoneAmount;
    }

    public void setStoneAmount(String stoneAmount) {
        this.stoneAmount = stoneAmount;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCategory_Name() {
        return category_Name;
    }

    public void setCategory_Name(String category_Name) {
        this.category_Name = category_Name;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getHuidCode() {
        return huidCode;
    }

    public void setHuidCode(String huidCode) {
        this.huidCode = huidCode;
    }

    public String getNetWt() {
        return netWt;
    }

    public void setNetWt(String netWt) {
        this.netWt = netWt;
    }

    public String getProduct_Code() {
        return product_Code;
    }

    public void setProduct_Code(String product_Code) {
        this.product_Code = product_Code;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getTblProductType() {
        return tblProductType;
    }

    public void setTblProductType(String tblProductType) {
        this.tblProductType = tblProductType;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getTblCollection() {
        return tblCollection;
    }

    public void setTblCollection(String tblCollection) {
        this.tblCollection = tblCollection;
    }

    public String getPartyTypeId() {
        return partyTypeId;
    }

    public void setPartyTypeId(String partyTypeId) {
        this.partyTypeId = partyTypeId;
    }

    public Partydetails getPartydetails() {
        return partydetails;
    }

    public void setPartydetails(Partydetails partydetails) {
        this.partydetails = partydetails;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public Tblbox getTblbox() {
        return tblbox;
    }

    public void setTblbox(Tblbox tblbox) {
        this.tblbox = tblbox;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getBarcodeNumber() {
        return barcodeNumber;
    }

    public void setBarcodeNumber(String barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
    }

    public String getDiamondWeight() {
        return diamondWeight;
    }

    public void setDiamondWeight(String diamondWeight) {
        this.diamondWeight = diamondWeight;
    }

    public String getDiamondPeaces() {
        return diamondPeaces;
    }

    public void setDiamondPeaces(String diamondPeaces) {
        this.diamondPeaces = diamondPeaces;
    }

    public String getDiamondRate() {
        return diamondRate;
    }

    public void setDiamondRate(String diamondRate) {
        this.diamondRate = diamondRate;
    }

    public String getDiamondAmount() {
        return diamondAmount;
    }

    public void setDiamondAmount(String diamondAmount) {
        this.diamondAmount = diamondAmount;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getClarity() {
        return clarity;
    }

    public void setClarity(String clarity) {
        this.clarity = clarity;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getDiamondSize() {
        return diamondSize;
    }

    public void setDiamondSize(String diamondSize) {
        this.diamondSize = diamondSize;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
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

    public String getHallmark() {
        return hallmark;
    }

    public void setHallmark(String hallmark) {
        this.hallmark = hallmark;
    }

    public String getHallmark_amt() {
        return hallmark_amt;
    }

    public void setHallmark_amt(String hallmark_amt) {
        this.hallmark_amt = hallmark_amt;
    }

    public String getStoneWeight1() {
        return stoneWeight1;
    }

    public void setStoneWeight1(String stoneWeight1) {
        this.stoneWeight1 = stoneWeight1;
    }

    public String getStoneAmount1() {
        return stoneAmount1;
    }

    public void setStoneAmount1(String stoneAmount1) {
        this.stoneAmount1 = stoneAmount1;
    }

    public String getStoneWeight2() {
        return stoneWeight2;
    }

    public void setStoneWeight2(String stoneWeight2) {
        this.stoneWeight2 = stoneWeight2;
    }

    public String getStoneAmount2() {
        return stoneAmount2;
    }

    public void setStoneAmount2(String stoneAmount2) {
        this.stoneAmount2 = stoneAmount2;
    }

    public String getStoneWeight3() {
        return stoneWeight3;
    }

    public void setStoneWeight3(String stoneWeight3) {
        this.stoneWeight3 = stoneWeight3;
    }

    public String getStoneAmount3() {
        return stoneAmount3;
    }

    public void setStoneAmount3(String stoneAmount3) {
        this.stoneAmount3 = stoneAmount3;
    }

    public String getStoneWeight4() {
        return stoneWeight4;
    }

    public void setStoneWeight4(String stoneWeight4) {
        this.stoneWeight4 = stoneWeight4;
    }

    public String getStoneAmount4() {
        return stoneAmount4;
    }

    public void setStoneAmount4(String stoneAmount4) {
        this.stoneAmount4 = stoneAmount4;
    }

    public String getStoneName1() {
        return stoneName1;
    }

    public void setStoneName1(String stoneName1) {
        this.stoneName1 = stoneName1;
    }

    public String getStoneName2() {
        return stoneName2;
    }

    public void setStoneName2(String stoneName2) {
        this.stoneName2 = stoneName2;
    }

    public String getStoneName3() {
        return stoneName3;
    }

    public void setStoneName3(String stoneName3) {
        this.stoneName3 = stoneName3;
    }

    public String getStoneName4() {
        return stoneName4;
    }

    public void setStoneName4(String stoneName4) {
        this.stoneName4 = stoneName4;
    }

    public String getFinePlusWastage() {
        return finePlusWastage;
    }

    public void setFinePlusWastage(String finePlusWastage) {
        this.finePlusWastage = finePlusWastage;
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

    public String getInwardNo() {
        return inwardNo;
    }

    public void setInwardNo(String inwardNo) {
        this.inwardNo = inwardNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    @Override
    public String toString() {
        return "Productmodel{" +
                "sku='" + sku + '\'' +
                ", productName='" + productName + '\'' +
                ", label='" + label + '\'' +
                ", hsnCode='" + hsnCode + '\'' +
                ", description='" + description + '\'' +
                ", minQuantity=" + minQuantity +
                ", images='" + images + '\'' +
                ", minWeight=" + minWeight +
                ", purityId=" + purityId +
                ", tblPurity=" + tblPurity +
                ", itemCode='" + itemCode + '\'' +
                ", product_No='" + product_No + '\'' +
                ", category_id='" + category_id + '\'' +
                ", materialcategory=" + materialcategory +
                ", material='" + material + '\'' +
                ", gm='" + gm + '\'' +
                ", size='" + size + '\'' +
                ", grosswt='" + grosswt + '\'' +
                ", purity='" + purity + '\'' +
                ", collection='" + collection + '\'' +
                ", occasion='" + occasion + '\'' +
                ", gender='" + gender + '\'' +
                ", product_type='" + product_type + '\'' +
                ", making_Percentage='" + making_Percentage + '\'' +
                ", making_Fixed_Amt='" + making_Fixed_Amt + '\'' +
                ", making_Fixed_Wastage='" + making_Fixed_Wastage + '\'' +
                ", making_per_gram='" + making_per_gram + '\'' +
                ", stoneWeight='" + stoneWeight + '\'' +
                ", stoneAmount='" + stoneAmount + '\'' +
                ", featured='" + featured + '\'' +
                ", itemType='" + itemType + '\'' +
                ", category_Name='" + category_Name + '\'' +
                ", pieces='" + pieces + '\'' +
                ", huidCode='" + huidCode + '\'' +
                ", netWt='" + netWt + '\'' +
                ", product_Code='" + product_Code + '\'' +
                ", mrp='" + mrp + '\'' +
                ", quantity='" + quantity + '\'' +
                ", productTypeId='" + productTypeId + '\'' +
                ", tblProductType='" + tblProductType + '\'' +
                ", collectionId='" + collectionId + '\'' +
                ", tblCollection='" + tblCollection + '\'' +
                ", partyTypeId='" + partyTypeId + '\'' +
                ", partydetails=" + partydetails +
                ", boxId='" + boxId + '\'' +
                ", tblbox=" + tblbox +
                ", onlineStatus='" + onlineStatus + '\'' +
                ", tid='" + tid + '\'' +
                ", barcodeNumber='" + barcodeNumber + '\'' +
                ", diamondWeight='" + diamondWeight + '\'' +
                ", diamondPeaces='" + diamondPeaces + '\'' +
                ", diamondRate='" + diamondRate + '\'' +
                ", diamondAmount='" + diamondAmount + '\'' +
                ", colour='" + colour + '\'' +
                ", clarity='" + clarity + '\'' +
                ", settingType='" + settingType + '\'' +
                ", shape='" + shape + '\'' +
                ", diamondSize='" + diamondSize + '\'' +
                ", certificate='" + certificate + '\'' +
                ", count='" + count + '\'' +
                ", branchName='" + branchName + '\'' +
                ", boxName='" + boxName + '\'' +
                ", hallmark='" + hallmark + '\'' +
                ", hallmark_amt='" + hallmark_amt + '\'' +
                ", stoneWeight1='" + stoneWeight1 + '\'' +
                ", stoneAmount1='" + stoneAmount1 + '\'' +
                ", stoneWeight2='" + stoneWeight2 + '\'' +
                ", stoneAmount2='" + stoneAmount2 + '\'' +
                ", stoneWeight3='" + stoneWeight3 + '\'' +
                ", stoneAmount3='" + stoneAmount3 + '\'' +
                ", stoneWeight4='" + stoneWeight4 + '\'' +
                ", stoneAmount4='" + stoneAmount4 + '\'' +
                ", stoneName1='" + stoneName1 + '\'' +
                ", stoneName2='" + stoneName2 + '\'' +
                ", stoneName3='" + stoneName3 + '\'' +
                ", stoneName4='" + stoneName4 + '\'' +
                ", finePlusWastage='" + finePlusWastage + '\'' +
                ", cuttingGrossWt='" + cuttingGrossWt + '\'' +
                ", cuttingNetWt='" + cuttingNetWt + '\'' +
                ", inwardNo='" + inwardNo + '\'' +
                ", id='" + id + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", statusType='" + statusType + '\'' +
                '}';
    }


    // Define getters and setters for other fields
}

class Tblbox {
    @SerializedName("metalName")
    private String metalName;
    @SerializedName("boxName")
    private String boxName;
    @SerializedName("emptyWeight")
    private String emptyWeight;
    @SerializedName("productName")
    private String productName;
    @SerializedName("onlineStatus")
    private String onlineStatus;
    @SerializedName("id")
    private String id;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("lastUpdated")
    private String lastUpdated;
    @SerializedName("statusType")
    private String statusType;

    public String getMetalName() {
        return metalName;
    }

    public void setMetalName(String metalName) {
        this.metalName = metalName;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getEmptyWeight() {
        return emptyWeight;
    }

    public void setEmptyWeight(String emptyWeight) {
        this.emptyWeight = emptyWeight;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }
}


class Partydetails {

    @SerializedName("supplier_code")
    private String supplier_code;
    @SerializedName("supplierType")
    private String supplierType;
    @SerializedName("supplier_name")
    private String supplier_name;
    @SerializedName("party_pan_no")
    private String party_pan_no;
    @SerializedName("party_adhar_no")
    private String party_adhar_no;
    @SerializedName("contact_no")
    private String contact_no;
    @SerializedName("email_id")
    private String email_id;
    @SerializedName("address")
    private String address;
    @SerializedName("state")
    private String state;
    @SerializedName("city")
    private String city;
    @SerializedName("firm_name")
    private String firm_name;
    @SerializedName("firm_details")
    private String firm_details;
    @SerializedName("gst_no")
    private String gst_no;
    @SerializedName("central_gst_no")
    private String central_gst_no;
    @SerializedName("onlineStatus")
    private String onlineStatus;
    @SerializedName("advanceAmt")
    private String advanceAmt;
    @SerializedName("balanceAmt")
    private String balanceAmt;
    @SerializedName("fineSilver")
    private String fineSilver;
    @SerializedName("fineGold")
    private String fineGold;
    @SerializedName("inwardNo")
    private String inwardNo;
    @SerializedName("inwardGold")
    private String inwardGold;
    @SerializedName("inwardSilver")
    private String inwardSilver;
    @SerializedName("id")
    private String id;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("lastUpdated")
    private String lastUpdated;
    @SerializedName("statusType")
    private String statusType;


    public String getSupplier_code() {
        return supplier_code;
    }

    public void setSupplier_code(String supplier_code) {
        this.supplier_code = supplier_code;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getParty_pan_no() {
        return party_pan_no;
    }

    public void setParty_pan_no(String party_pan_no) {
        this.party_pan_no = party_pan_no;
    }

    public String getParty_adhar_no() {
        return party_adhar_no;
    }

    public void setParty_adhar_no(String party_adhar_no) {
        this.party_adhar_no = party_adhar_no;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }

    public String getFirm_details() {
        return firm_details;
    }

    public void setFirm_details(String firm_details) {
        this.firm_details = firm_details;
    }

    public String getGst_no() {
        return gst_no;
    }

    public void setGst_no(String gst_no) {
        this.gst_no = gst_no;
    }

    public String getCentral_gst_no() {
        return central_gst_no;
    }

    public void setCentral_gst_no(String central_gst_no) {
        this.central_gst_no = central_gst_no;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getAdvanceAmt() {
        return advanceAmt;
    }

    public void setAdvanceAmt(String advanceAmt) {
        this.advanceAmt = advanceAmt;
    }

    public String getBalanceAmt() {
        return balanceAmt;
    }

    public void setBalanceAmt(String balanceAmt) {
        this.balanceAmt = balanceAmt;
    }

    public String getFineSilver() {
        return fineSilver;
    }

    public void setFineSilver(String fineSilver) {
        this.fineSilver = fineSilver;
    }

    public String getFineGold() {
        return fineGold;
    }

    public void setFineGold(String fineGold) {
        this.fineGold = fineGold;
    }

    public String getInwardNo() {
        return inwardNo;
    }

    public void setInwardNo(String inwardNo) {
        this.inwardNo = inwardNo;
    }

    public String getInwardGold() {
        return inwardGold;
    }

    public void setInwardGold(String inwardGold) {
        this.inwardGold = inwardGold;
    }

    public String getInwardSilver() {
        return inwardSilver;
    }

    public void setInwardSilver(String inwardSilver) {
        this.inwardSilver = inwardSilver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }
}

class Materialcategory {
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("entryby_Staff_id")
    private String entryby_Staff_id;
    @SerializedName("tblStaff")
    private String tblStaff;
    @SerializedName("label")
    private String label;
    @SerializedName("itemType")
    private String itemType;
    @SerializedName("material")
    private String material;
    @SerializedName("parentsCategory")
    private String parentsCategory;
    @SerializedName("slug")
    private String slug;
    @SerializedName("hsnCode")
    private String hsnCode;
    @SerializedName("shortCode")
    private String shortCode;
    @SerializedName("onlineStatus")
    private String onlineStatus;
    @SerializedName("id")
    private String id;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("lastUpdated")
    private String lastUpdated;
    @SerializedName("statusType")
    private String statusType;


}

class Purity {
    @SerializedName("purity")
    private String purity;

    @SerializedName("category")
    private String category;

    @SerializedName("label")
    private String label;

    @SerializedName("todaysRate")
    private String todaysRate;

    @SerializedName("onlineStatus")
    private String onlineStatus;

    @SerializedName("finePercentage")
    private String finePercentage;

    @SerializedName("id")
    private int id;

    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("lastUpdated")
    private String lastUpdated;

    @SerializedName("statusType")
    private boolean statusType;

    public String getPurity() {
        return purity;
    }

    public void setPurity(String purity) {
        this.purity = purity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTodaysRate() {
        return todaysRate;
    }

    public void setTodaysRate(String todaysRate) {
        this.todaysRate = todaysRate;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getFinePercentage() {
        return finePercentage;
    }

    public void setFinePercentage(String finePercentage) {
        this.finePercentage = finePercentage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isStatusType() {
        return statusType;
    }

    public void setStatusType(boolean statusType) {
        this.statusType = statusType;
    }

    // Define getters and setters
}