package com.example.billmanagerapp.data.dp;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.billmanagerapp.model.ServiceItem;

import java.util.List;

@Dao
public interface ServiceItemDAO {
    @Insert
    void insert(ServiceItem serviceItem);


    @Delete
    void delete(ServiceItem serviceItem);

    @Query("SELECT * FROM service_items")
    List<ServiceItem> getAll();
}
