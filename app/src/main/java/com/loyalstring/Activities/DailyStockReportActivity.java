package com.loyalstring.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loyalstring.Adapters.CommonStockAdapter;
import com.loyalstring.R;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.modelclasses.Itemmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DailyStockReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommonStockAdapter adapter;
    private EntryDatabase entryDatabase;
    private List<Itemmodel> allItems;

    private final Stack<LevelState> levelStack = new Stack<>();
    private Map<String, List<Itemmodel>> groupedMap;
    private TextView tvSelectedDate;

    static class LevelState {
        CommonStockAdapter.LevelType level;
        List<Object> displayList;
        Map<String, List<Itemmodel>> groupedData;

        LevelState(CommonStockAdapter.LevelType level, List<Object> displayList, Map<String, List<Itemmodel>> groupedData) {
            this.level = level;
            this.displayList = displayList;
            this.groupedData = groupedData;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_stock_report);
        entryDatabase = new EntryDatabase(this);
        recyclerView = findViewById(R.id.recyclerViewStock);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvSelectedDate = findViewById(R.id.tv_selected_date);

        // After updating date in DB...
        allItems = entryDatabase.getAllItems();
        if (allItems == null || allItems.isEmpty()) {
            Toast.makeText(this, "No stock data found", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<Itemmodel>> dateMap = CommonStockAdapter.groupBy(allItems, "date");
        List<Object> dateList = new ArrayList<>(dateMap.keySet());


        adapter = new CommonStockAdapter(
                this,
                dateList,
                CommonStockAdapter.LevelType.DATE,
                dateMap,
                (level, value) -> {
                    switch (level) {
                        case DATE:
                            String selectedDate = (String) value;
                            tvSelectedDate.setVisibility(View.VISIBLE);
                            tvSelectedDate.setText("Date: " + selectedDate);
                            List<Itemmodel> itemsForDate = dateMap.get(selectedDate);
                            showCounters(itemsForDate);
                            break;
                        case COUNTER:
                            String selectedCounter = (String) value;
                            List<Itemmodel> itemsForCounter = groupedMap.get(selectedCounter);
                            showCategories(itemsForCounter);
                            break;
                        case CATEGORY:
                            String selectedCategory = (String) value;
                            List<Itemmodel> itemsForCategory = groupedMap.get(selectedCategory);
                            showProducts(itemsForCategory);
                            break;
                    }
                }
        );


        recyclerView.setAdapter(adapter);

        // ✅ 4. Now it's safe to call this
        adapter.updateGroupedData(dateMap);
    }

    private void showDates(List<Itemmodel> items) {
        Map<String, List<Itemmodel>> dateMap = CommonStockAdapter.groupBy(items, "date");
        List<Object> dateList = new ArrayList<>(dateMap.keySet());

        adapter = new CommonStockAdapter(this, dateList, CommonStockAdapter.LevelType.DATE, dateMap, (level, value) -> {
            String selectedDate = (String) value;
            List<Itemmodel> itemsForDate = dateMap.get(selectedDate);
            levelStack.push(new LevelState(CommonStockAdapter.LevelType.DATE, dateList, dateMap));
            showCounters(itemsForDate);
        });

        recyclerView.setAdapter(adapter);
    }

    private void showCounters(List<Itemmodel> itemsForDate) {
        levelStack.push(new LevelState(CommonStockAdapter.LevelType.DATE, new ArrayList<>(adapter.getDisplayList()), adapter.getGroupedData()));

        groupedMap = CommonStockAdapter.groupBy(itemsForDate, "counter");
        List<Object> counterList = new ArrayList<>(groupedMap.keySet());

        adapter.updateGroupedData(groupedMap);
        adapter.updateList(counterList, CommonStockAdapter.LevelType.COUNTER, groupedMap);
    }

    private void showCategories(List<Itemmodel> itemsForCounter) {
        levelStack.push(new LevelState(CommonStockAdapter.LevelType.COUNTER, new ArrayList<>(adapter.getDisplayList()), adapter.getGroupedData()));

        groupedMap = CommonStockAdapter.groupBy(itemsForCounter, "category");
        List<Object> categoryList = new ArrayList<>(groupedMap.keySet());

        adapter.updateGroupedData(groupedMap);
        adapter.updateList(categoryList, CommonStockAdapter.LevelType.CATEGORY, groupedMap);
    }

    private void showProducts(List<Itemmodel> itemsForCategory) {
        // ✅ Push current category state before navigating to products
        levelStack.push(new LevelState(adapter.getCurrentLevel(), adapter.getDisplayList(), adapter.getGroupedData()));

        adapter.updateGroupedData(null); // No grouped data at product level
        adapter.updateList(new ArrayList<>(itemsForCategory), CommonStockAdapter.LevelType.PRODUCT, null);
    }

    @Override
    public void onBackPressed() {
        if (!levelStack.isEmpty()) {
            LevelState previous = levelStack.pop();

            // Restore groupedMap in case of counter/category click
            groupedMap = previous.groupedData;

            // Restore adapter data
            adapter.updateList(previous.displayList, previous.level, previous.groupedData);

            // Update selected date visibility
            if (previous.level == CommonStockAdapter.LevelType.DATE) {
                tvSelectedDate.setVisibility(View.GONE);
            } else if (previous.level == CommonStockAdapter.LevelType.COUNTER) {
                // Re-show the selected date
                // Try to extract a date from any one item in groupedMap
                if (groupedMap != null && !groupedMap.isEmpty()) {
                    List<Itemmodel> anyList = groupedMap.values().iterator().next();
                    if (!anyList.isEmpty()) {
                        long entryDate = anyList.get(0).getEntryDate();
                        String dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH).format(new java.util.Date(entryDate));
                        tvSelectedDate.setVisibility(View.VISIBLE);
                        tvSelectedDate.setText("Date: " + dateStr);
                    }
                }
            }

        } else {
            super.onBackPressed();
        }
    }

}
