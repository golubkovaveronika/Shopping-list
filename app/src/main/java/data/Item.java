package com.example.golubkova_veronika_isp251_spisok_pokupok.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String store;
    public String category;
    public int priceValue;
    public boolean isFavorite;
    public boolean isBought;

    public Item() {
        this.isFavorite = false;
        this.isBought = false;
    }

    public Item(String name, String store, String category, int priceValue) {
        this.name = name;
        this.store = store;
        this.category = category;
        this.priceValue = priceValue;
        this.isFavorite = false;
        this.isBought = false;
    }
}