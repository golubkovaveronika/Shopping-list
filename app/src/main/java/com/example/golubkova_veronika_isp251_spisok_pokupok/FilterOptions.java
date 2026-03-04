package com.example.golubkova_veronika_isp251_spisok_pokupok;

public class FilterOptions {
    public static FilterOptions INSTANCE = new FilterOptions();

    public String query = "";
    public String category = "Категория";
    public String store = "Магазин";
    public int minPrice = 0;
    public int maxPrice = 999999;
}