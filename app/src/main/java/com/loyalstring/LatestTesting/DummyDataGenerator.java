package com.loyalstring.LatestTesting;

import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DummyDataGenerator {
    private static final String[] CATEGORIES = {"Electronics", "Toys", "Food"};
    private static final String[] PRODUCTS = {"Product A", "Product B", "Product C", "Product D", "Product E"};
    private static final String[] BOXES = {"Box 1", "Box 2", "Box 3", "Box 4", "Box 5"};
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";



    public static void main(String[] args) {
        List<Itemmodel> items = generateDummyData(1_000_000); // Generate 1 million items

        // Optionally print the first 10 items to verify
        for (int i = 0; i < 10; i++) {
            Itemmodel item = items.get(i);
            System.out.println("ID: " +  ", Category: " + item.getCategory() +
                    ", Product: " + item.getProduct() + ", Box: " + item.getBox());
        }
    }

    public static List<Itemmodel> generateDummyData(int numberOfItems) {
        List<Itemmodel> items = new ArrayList<>();
        Random random = new Random();

        for (long i = 1; i <= numberOfItems; i++) {
            String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
            String product = PRODUCTS[random.nextInt(PRODUCTS.length)];
            String box = BOXES[random.nextInt(BOXES.length)];


            String epcValue = generateRandomAlphaNumeric(24);
            String tidValue = generateRandomAlphaNumeric(24);

            Itemmodel item = new Itemmodel();
            item.setCategory(category);
            item.setProduct(product);
            item.setDesignName(category);
            item.setBox(box);
            item.setCounterId("1");
            item.setCounterName("name");
            item.setEpcValue(epcValue);
            item.setTidValue(tidValue);
            item.setBarCode("123");
            item.setGrossWt(12);
            item.setNetWt(12);
            item.setItemCode("Item1");

            items.add(item);

//            items.add(new Itemmodel(i, category, product, box));
        }

        return items;
    }

    private static String generateRandomAlphaNumeric(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(index));
        }
        return builder.toString();
    }

}
