package com.example.billmanagerapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.model.Customer;
import com.example.billmanagerapp.ui.adapter.CustomerAdapter;

import java.util.List;

public class CustomerPickerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_picker);

        recyclerView = findViewById(R.id.recyclerViewCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<Customer> customerList = DatabaseInstance.getDatabase(this).customerDAO().getAllCustomers();
            runOnUiThread(() ->{
                adapter = new CustomerAdapter(customerList);
                adapter.setClickListener(customer -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("customer_id", customer.id);
                    resultIntent.putExtra("customer_name", customer.name);
                    resultIntent.putExtra("customer_phone", customer.phone);
                    resultIntent.putExtra("customer_address", customer.address);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}