package com.loyalstring.modelclasses;

import java.util.List;

public class SyncRequest {
    private String status;
    private String box;
    private List<ProductEntry> products;

    public SyncRequest(String status, String box, List<ProductEntry> products) {
        this.status = status;
        this.box = box;
        this.products = products;
    }

    public static class ProductEntry {
        private String rfid;
        private String product;

        public ProductEntry(String rfid, String product) {
            this.rfid = rfid;
            this.product = product;
        }

        // Getters if needed
    }
}
