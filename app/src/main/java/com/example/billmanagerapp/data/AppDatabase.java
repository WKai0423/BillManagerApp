package com.example.billmanagerapp.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.billmanagerapp.data.dp.CustomerDAO;
import com.example.billmanagerapp.data.dp.InvoiceDAO;
import com.example.billmanagerapp.data.dp.MaterialItemDAO;
import com.example.billmanagerapp.data.dp.ServiceItemDAO;
import com.example.billmanagerapp.model.Customer;
import com.example.billmanagerapp.model.Invoice;
import com.example.billmanagerapp.model.MaterialItem;
import com.example.billmanagerapp.model.ServiceItem;

/**

 数据库创建

 * */

@Database(entities = {Invoice.class, Customer.class, ServiceItem.class, MaterialItem.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InvoiceDAO invoiceDao();

    public abstract CustomerDAO customerDAO();

    public abstract ServiceItemDAO serviceItemDAO();

    public abstract MaterialItemDAO materialItemDAO();

}
