package com.example.golubkova_veronika_isp251_spisok_pokupok;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tv_datetime);
        updateClock(tv);

        findViewById(R.id.btn_go_to_list).setOnClickListener(v ->
                startActivity(new Intent(this, ShoppingListActivity.class)));
        findViewById(R.id.btn_favorites).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.btn_search).setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }

    private void updateClock(TextView tv) {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                runOnUiThread(() -> tv.setText(DateFormat.format("dd.MM.yyyy HH:mm", System.currentTimeMillis()).toString()));
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
        }).start();
    }
}