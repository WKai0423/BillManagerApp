package com.example.billmanagerapp.model;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "material_items")
public class MaterialItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double price;

    @Ignore
    public int quantity;// 👈 不存数据库，只是临时字段

    public MaterialItem(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
