package com.example.billmanagerapp.data.dp;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.billmanagerapp.model.MaterialItem;

import java.util.List;

@Dao
public interface MaterialItemDAO {
    @Insert
    void insert(MaterialItem item);

    @Query("SELECT * FROM material_items")
    List<MaterialItem> getAll();
}
