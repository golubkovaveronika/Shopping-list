package com.example.golubkova_veronika_isp251_spisok_pokupok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.AppDatabase;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.Item;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ItemAdapter adapter;
    private AppDatabase db;
    private TextView tvNoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        db = AppDatabase.getDatabase(this);
        rv = findViewById(R.id.recycler_view);
        tvNoItems = findViewById(R.id.tv_no_items);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter();
        rv.setAdapter(adapter);

        loadShoppingList();

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            AppDatabase localDb = db;
            new Thread(() -> {
                localDb.itemDao().clearBoughtItems();
                runOnUiThread(this::loadShoppingList);
            }).start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            loadShoppingList();
        }
    }

    public void loadShoppingList() {
        AppDatabase localDb = db;
        new Thread(() -> {
            List<Item> items = localDb.itemDao().getShoppingList();
            final List<Item> safeItems = (items == null) ? new ArrayList<>() : items;

            runOnUiThread(() -> {
                adapter.setItems(safeItems, this);
                tvNoItems.setVisibility(safeItems.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }

    public void removeItem(Item item) {
        runOnUiThread(() -> {
            int pos = adapter.getItems().indexOf(item);
            if (pos >= 0) {
                adapter.getItems().remove(pos);
                adapter.notifyItemRemoved(pos);
                tvNoItems.setVisibility(adapter.getItems().isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadShoppingList();
    }
}