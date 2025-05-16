package com.example.billmanagerapp.model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

//客户数据类
@Entity
public class Customer {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String phone;

    public String address;
}
