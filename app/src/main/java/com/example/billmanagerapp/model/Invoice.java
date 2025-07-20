package com.example.billmanagerapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**

 开单的实体类

 * */
@Entity
public class Invoice {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String invoiceNumber;

    public String date;
    public String customerName;
    public String phone;
    public String address;
    public String service;
    public double materialCost;
    public String materialJson;
    public String note;

}
