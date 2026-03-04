package com.example.golubkova_veronika_isp251_spisok_pokupok;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.AppDatabase;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.Item;

public class ItemDetailsActivity extends AppCompatActivity {
    private EditText etName, etStore, etCategory, etPrice;
    private Button btnSave, btnCancel;
    private AppDatabase db;
    private Item item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        db = AppDatabase.getDatabase(this);
        etName = findViewById(R.id.et_name);
        etStore = findViewById(R.id.et_store);
        etCategory = findViewById(R.id.et_category);
        etPrice = findViewById(R.id.et_price);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        int itemId = getIntent().getIntExtra("ITEM_ID", -1);
        if (itemId != -1) {
            final AppDatabase localDb = db;
            new Thread(() -> {
                item = localDb.itemDao().getItemById(itemId);
                if (item != null) {
                    runOnUiThread(() -> {
                        etName.setText(item.name);
                        etStore.setText(item.store);
                        etCategory.setText(item.category);
                        etPrice.setText(String.valueOf(item.priceValue));
                    });
                }
            }).start();
        }

        btnSave.setOnClickListener(v -> save());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void save() {
        final String n = etName.getText().toString().trim();
        final String s = etStore.getText().toString().trim();
        final String c = etCategory.getText().toString().trim();
        final String pStr = etPrice.getText().toString().trim();

        if (n.isEmpty()) {
            Toast.makeText(this, "Укажите название товара", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pStr.isEmpty()) {
            Toast.makeText(this, "Укажите цену", Toast.LENGTH_SHORT).show();
            return;
        }

        int parsedPrice = 0;
        try {
            String digitsOnly = pStr.replaceAll("[^\\d]", "");
            if (digitsOnly.isEmpty()) {
                Toast.makeText(this, "Цена должна содержать цифры", Toast.LENGTH_SHORT).show();
                return;
            }
            parsedPrice = Integer.parseInt(digitsOnly);
        } catch (Exception e) {
            Toast.makeText(this, "Некорректная цена", Toast.LENGTH_SHORT).show();
            return;
        }
        final int priceValue = parsedPrice;

        final AppDatabase localDb = db;
        final Item localItem = this.item;

        new Thread(() -> {
            if (localItem == null) {
                Item newItem = new Item(n, s, c, priceValue);
                localDb.itemDao().insert(newItem);
            } else {
                Item updatedItem = new Item();
                updatedItem.id = localItem.id;
                updatedItem.name = n;
                updatedItem.store = s;
                updatedItem.category = c;
                updatedItem.priceValue = priceValue;
                updatedItem.isFavorite = localItem.isFavorite;
                updatedItem.isBought = localItem.isBought;
                localDb.itemDao().update(updatedItem);
            }

            runOnUiThread(() -> {
                Toast.makeText(ItemDetailsActivity.this, "Сохранено!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            });
        }).start();
    }
}