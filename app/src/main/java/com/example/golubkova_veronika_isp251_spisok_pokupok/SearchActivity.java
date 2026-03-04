package com.example.golubkova_veronika_isp251_spisok_pokupok;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.AppDatabase;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.Item;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ItemAdapter adapter;
    private AppDatabase db;
    private EditText etSearch;
    private Button btnFilter;
    private TextView tvNoResults;
    private TextView tvNoItems;
    private static FilterOptions filters = FilterOptions.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = AppDatabase.getDatabase(this);
        rv = findViewById(R.id.recycler_view);
        tvNoResults = findViewById(R.id.tv_no_results);
        tvNoItems = findViewById(R.id.tv_no_items);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter();
        rv.setAdapter(adapter);
        etSearch = findViewById(R.id.et_search);
        btnFilter = findViewById(R.id.btn_filter);

        applyFilters();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filters.query = s.toString().trim();
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnFilter.setOnClickListener(v -> showFilterDialog());

        findViewById(R.id.fab_add).setOnClickListener(v ->
                startActivityForResult(new Intent(this, ItemDetailsActivity.class), 100));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            applyFilters();
        }
    }

    public void applyFilters() {
        AppDatabase localDb = db;
        String query = filters.query.isEmpty() ? null : "%" + filters.query + "%";
        String category = filters.category.equals("Категория") ? null : filters.category;
        String store = filters.store.equals("Магазин") ? null : filters.store;
        int minPrice = filters.minPrice;
        int maxPrice = filters.maxPrice;

        new Thread(() -> {
            List<Item> items = localDb.itemDao().searchWithFilters(query, category, store, minPrice, maxPrice);
            List<Item> allItems = localDb.itemDao().getAllItems();
            boolean hasAnyItems = allItems != null && !allItems.isEmpty();

            runOnUiThread(() -> {
                adapter.setItems(items != null ? items : new ArrayList<>(), this);

                if (hasAnyItems && (items == null || items.isEmpty())) {
                    rv.setVisibility(View.GONE);
                    tvNoResults.setVisibility(View.VISIBLE);
                    tvNoItems.setVisibility(View.GONE);
                } else if (!hasAnyItems) {
                    rv.setVisibility(View.GONE);
                    tvNoResults.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                } else {
                    rv.setVisibility(View.VISIBLE);
                    tvNoResults.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    private void showFilterDialog() {
        AppDatabase localDb = db;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        Spinner spCategory = dialogView.findViewById(R.id.spinner_category);
        Spinner spStore = dialogView.findViewById(R.id.spinner_store);
        EditText etMinPrice = dialogView.findViewById(R.id.et_min_price);
        EditText etMaxPrice = dialogView.findViewById(R.id.et_max_price);

        etMinPrice.setText(String.valueOf(filters.minPrice));
        etMaxPrice.setText(String.valueOf(filters.maxPrice));

        new Thread(() -> {
            List<String> categories = new ArrayList<>();
            categories.add("Категория");
            categories.addAll(localDb.itemDao().getAllCategories());

            List<String> stores = new ArrayList<>();
            stores.add("Магазин");
            stores.addAll(localDb.itemDao().getAllStores());

            runOnUiThread(() -> {
                ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, categories);
                catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(catAdapter);

                ArrayAdapter<String> storeAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, stores);
                storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spStore.setAdapter(storeAdapter);

                int catPos = findPosition(spCategory, filters.category);
                int storePos = findPosition(spStore, filters.store);
                spCategory.setSelection(catPos);
                spStore.setSelection(storePos);
            });
        }).start();

        builder.setPositiveButton("Применить", (dialog, which) -> {
            filters.category = spCategory.getSelectedItem().toString();
            filters.store = spStore.getSelectedItem().toString();

            String minStr = etMinPrice.getText().toString().trim();
            String maxStr = etMaxPrice.getText().toString().trim();

            filters.minPrice = parsePrice(minStr, 0);
            filters.maxPrice = parsePrice(maxStr, 999999);

            applyFilters();
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private int findPosition(Spinner spinner, String target) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(target)) {
                return i;
            }
        }
        return 0;
    }

    // хелп для парсинга цены
    private int parsePrice(String str, int defaultValue) {
        if (str.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}