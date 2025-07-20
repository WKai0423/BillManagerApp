package com.example.billmanagerapp.ui;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.billmanagerapp.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonIntent = findViewById(R.id.btn_intent);
        Button buttonCustomer = findViewById(R.id.buttonCustomer);
        Button buttonCustomerList = findViewById(R.id.buttonCustomerList);
        Button buttonServiceList = findViewById(R.id.buttonServiceList);

        buttonSave.setOnClickListener(this);
        buttonIntent.setOnClickListener(this);
        buttonCustomer.setOnClickListener(this);
        buttonCustomerList.setOnClickListener(this);
        buttonServiceList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSave){
            Intent intent = new Intent(this, InvoiceActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_intent) {
            Intent intent = new Intent(this, InvoiceListActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonCustomer) {
            Intent intent = new Intent(this, AddCustomerActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonCustomerList) {
            Intent intent = new Intent(this, CustomerListActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonServiceList) {
            Intent intent = new Intent(this, ServiceItemManagerActivity.class);
            startActivity(intent);
        }
    }
}