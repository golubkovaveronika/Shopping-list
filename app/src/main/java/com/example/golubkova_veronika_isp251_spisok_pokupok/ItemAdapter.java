package com.example.golubkova_veronika_isp251_spisok_pokupok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.AppDatabase;
import com.example.golubkova_veronika_isp251_spisok_pokupok.data.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> items = new ArrayList<>();
    private Context context;

    public void setItems(List<Item> items, Context context) {
        this.items = items != null ? items : new ArrayList<>();
        this.context = context;
        notifyDataSetChanged();
    }

    public List<Item> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(items.get(position), context);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, store, category, price;
        ImageView ivFavorite, ivEdit, ivDelete, ivAdd;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_name);
            store = itemView.findViewById(R.id.text_view_store);
            category = itemView.findViewById(R.id.text_view_category);
            price = itemView.findViewById(R.id.text_view_price);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            ivAdd = itemView.findViewById(R.id.iv_add);
        }

        void bind(Item item, Context context) {
            if (item == null) {
                name.setText("");
                store.setText("");
                category.setText("");
                price.setText("0 ₽");
                ivFavorite.setImageResource(R.drawable.ic_favorite_empty);
                ivAdd.setImageResource(R.drawable.ic_plus_small);
                return;
            }

            name.setText(item.name != null ? item.name : "");
            store.setText(item.store != null ? item.store : "");
            category.setText(item.category != null ? item.category : "");
            price.setText(item.priceValue + " ₽");

            // Избранное — мгновенное обновление
            ivFavorite.setImageResource(item.isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_empty);
            ivFavorite.setOnClickListener(v -> {
                item.isFavorite = !item.isFavorite;
                new Thread(() -> {
                    AppDatabase.getDatabase(context).itemDao().update(item);
                    // Обновляем избранное, если оно открыто
                    if (FavoritesActivity.instance != null) {
                        FavoritesActivity.instance.loadFavorites();
                    }
                }).start();
                ivFavorite.setImageResource(item.isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_empty);
            });

            // Список покупок — как сердечко
            if (item.isBought) {
                ivAdd.setImageResource(R.drawable.ic_minus);
            } else {
                ivAdd.setImageResource(R.drawable.ic_plus_small);
            }
            ivAdd.setOnClickListener(v -> {
                item.isBought = !item.isBought;
                new Thread(() -> {
                    AppDatabase.getDatabase(context).itemDao().update(item);
                }).start();
                if (item.isBought) {
                    ivAdd.setImageResource(R.drawable.ic_minus);
                } else {
                    ivAdd.setImageResource(R.drawable.ic_plus_small);
                }
                // если в списке покупок и убрали — удаляет из списка
                if (context instanceof ShoppingListActivity && !item.isBought) {
                    ((ShoppingListActivity) context).removeItem(item);
                }
            });

            // редактирование только по карандашику
            ivEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("ITEM_ID", item.id);
                ((Activity) context).startActivityForResult(intent, 100);
            });

            // удаление
            ivDelete.setOnClickListener(v -> {
                new Thread(() -> {
                    AppDatabase.getDatabase(context).itemDao().delete(item);
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(() -> {
                            if (context instanceof SearchActivity) {
                                ((SearchActivity) context).applyFilters();
                            } else if (context instanceof ShoppingListActivity) {
                                ((ShoppingListActivity) context).removeItem(item);
                            } else if (context instanceof FavoritesActivity) {
                                ((FavoritesActivity) context).loadFavorites();
                            }
                        });
                    }
                }).start();
            });
        }
    }
}