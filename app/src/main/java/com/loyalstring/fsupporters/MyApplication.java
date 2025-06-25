package com.loyalstring.fsupporters;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MyApplication extends Application {
    private HashMap<String, Itemmodel> inventoryMap = new HashMap<>();
    private HashMap<String, Itemmodel> finvetorymap = new HashMap<>();
    private TreeMap<String, List<Itemmodel>> skureport;

    private TreeMap<String, List<Itemmodel>> salereport = new TreeMap<>();

    private int totalcount = 0;


    public void setInventoryMap(HashMap<String, Itemmodel> inventoryMap) {
        this.inventoryMap = inventoryMap;
    }

    public HashMap<String, Itemmodel> getInventoryMap() {

        finvetorymap.clear();
        Log.d("checkingsv", "" + inventoryMap.size());

        // Create a copy to avoid concurrent modification issues
        HashMap<String, Itemmodel> inventoryCopy = new HashMap<>(inventoryMap);

        for (Map.Entry<String, Itemmodel> entry : inventoryCopy.entrySet()) {
            finvetorymap.put(entry.getKey(), new Itemmodel(entry.getValue()));
        }

        return finvetorymap;

//        finvetorymap.clear();
//        Log.d("checkingsv", ""+inventoryMap.size());
//        for (Map.Entry<String, Itemmodel> entry : inventoryMap.entrySet()) {
//            finvetorymap.put(entry.getKey(), new Itemmodel(entry.getValue()));
//
//        }
//        return finvetorymap;
    }


    /*public void putitem(Itemmodel item) {
//        Log.d("eitemset ","  "+item);
        if(inventoryMap.containsKey(item.getTidValue())){
            inventoryMap.replace(item.getTidValue(), item);
        }else{
            inventoryMap.put(item.getTidValue(), item);
            totalcount = totalcount+1;
        }

    }*/

    public void putitem(Itemmodel item) {
        // Use compute to handle both insertion and updating atomically
        inventoryMap.compute(item.getTidValue(), (key, existingItem) -> {
            if (existingItem == null) {
                // If the item doesn't exist, increment the count
//                totalcount.increment();
                totalcount = totalcount+1;
            }
            // Return the new item (this will replace the existing item if it exists)
            return item;
        });
    }

    public void cleardata(Context mContext) {
        inventoryMap.clear();
        totalcount = 0;
    }

    /*public Itemmodel checkitem(String tidv, EntryDatabase entryDatabase, Context context) {
        Log.d("eiteminv", ""+inventoryMap.size()+"  "+inventoryMap.values().toString());
        if (inventoryMap.containsKey(tidv)) {
            return inventoryMap.get(tidv); // Item found in memory
        } else if (totalcount > 0 && inventoryMap.size() <= totalcount) {
            // Check in the database only if the item is not found in memory and total count is valid
            return entryDatabase.getitembytid(tidv, context);
        } else {
            return null; // Item not found in memory or database
        }
    }*/

    public Itemmodel checkitem(String tidv, EntryDatabase entryDatabase, Context context) {
        Itemmodel item = inventoryMap.get(tidv);
        if (item == null && totalcount > 0 && inventoryMap.size() <= totalcount) {
            item = entryDatabase.getitembytid(tidv, context);
            if (item != null) {
//                inventoryMap.put(tidv, item);
                putitem(item);
            }
        }
        return item;
    }


    public void setcount(int count) {
        totalcount = count;
    }

    public boolean isCountMatch() {
        return (totalcount == inventoryMap.size());
    }

    public void removeite(Itemmodel item) {
        inventoryMap.remove(item.getTidValue());
        totalcount = totalcount-1;
    }

    public TreeMap<String, List<Itemmodel>> getSkureport() {
        return skureport;
    }

    public void setSkureport(TreeMap<String, List<Itemmodel>> skureport) {
        this.skureport = skureport;
    }

    public HashMap<String, Itemmodel> getFinvetorymap() {
        return finvetorymap;
    }

    public void setFinvetorymap(HashMap<String, Itemmodel> finvetorymap) {
        this.finvetorymap = finvetorymap;
    }

    public TreeMap<String, List<Itemmodel>> getSalereport() {
        return salereport;
    }

    public void setSalereport(TreeMap<String, List<Itemmodel>> salereport) {
        Log.e("MyApplication", "Setting salereport with size: " + salereport.size());

        this.salereport = salereport;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    // Method to clear all items from the list
    public void clearItems() {
        inventoryMap.clear();  // Clears the list of all items
    }

}