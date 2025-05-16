package com.example.billmanagerapp.data.dp;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.billmanagerapp.model.Invoice;

import java.util.List;

/**
  
 实体类与数据库的接口

 增删改查方法都可以在这里做

 * */

@Dao
public interface InvoiceDAO {
    @Insert
    void insert(Invoice invoice);

    @Query("SELECT * FROM Invoice")
    List<Invoice> getAllInvoice();

    @Update
    void update(Invoice invoice);

    @Delete
    void deleteInvoice(Invoice invoice);

    @Query("DELETE FROM invoice")
    void deleteAllInvoices();

    @Query("SELECT * FROM invoice WHERE id = :invoiceId LIMIT 1")
    Invoice getInvoiceById(int invoiceId);

    @Query("SELECT invoiceNumber FROM invoice ORDER BY id DESC LIMIT 1")
    String getLastInvoiceNumber();
}
