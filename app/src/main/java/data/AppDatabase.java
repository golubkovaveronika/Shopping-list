package com.example.golubkova_veronika_isp251_spisok_pokupok.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.Item;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.ItemDao;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ItemDao itemDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "shopping_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}