package com.example.golubkova_veronika_isp251_spisok_pokupok.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.Item;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM items ORDER BY isBought ASC, name COLLATE NOCASE")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY name COLLATE NOCASE")
    List<Item> getFavorites();

    @Query("SELECT * FROM items WHERE " +
            "(:nameQuery IS NULL OR name LIKE :nameQuery) AND " +
            "(:categoryFilter IS NULL OR category = :categoryFilter) AND " +
            "(:storeFilter IS NULL OR store = :storeFilter) AND " +
            "(priceValue >= :minPrice) AND " +
            "(priceValue <= :maxPrice)")
    List<Item> searchWithFilters(String nameQuery, String categoryFilter, String storeFilter,
                                 int minPrice, int maxPrice);

    @Query("SELECT DISTINCT category FROM items ORDER BY category")
    List<String> getAllCategories();

    @Query("SELECT DISTINCT store FROM items ORDER BY store")
    List<String> getAllStores();

    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    // Очистка списка покупок снимает флаг
    @Query("UPDATE items SET isBought = 0 WHERE isBought = 1")
    void clearBoughtItems();

    @Query("SELECT * FROM items WHERE isBought = 1 ORDER BY name COLLATE NOCASE")
    List<Item> getShoppingList();

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(int id);
}