package com.example.billmanagerapp.model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "service_items")
public class ServiceItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double price;

    public ServiceItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceItem item = (ServiceItem) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
