package com.example.billmanagerapp.data.dp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.billmanagerapp.model.Customer;

import java.util.List;

//客户类跟数据库的接口
@Dao
public interface CustomerDAO {
   @Insert
   void insert(Customer customer);

    @Delete
    void delete(Customer customer);

    @Query("SELECT * FROM Customer")
    List<Customer> getAllCustomers();

    @Query("DELETE FROM Customer")
    void deleteAllCustomers();
}
