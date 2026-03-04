package com.example.golubkova_veronika_isp251_spisok_pokupok;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.AppDatabase;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.Item;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    public static FavoritesActivity instance = null;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private AppDatabase db;
    private TextView tvNoFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        instance = this;

        db = AppDatabase.getDatabase(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter();
        recyclerView.setAdapter(adapter);
        tvNoFavorites = findViewById(R.id.tv_no_favorites);

        loadFavorites();
    }

    public void loadFavorites() {
        new Thread(() -> {
            List<Item> items = db.itemDao().getFavorites();
            runOnUiThread(() -> {
                adapter.setItems(items, this);
                tvNoFavorites.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}