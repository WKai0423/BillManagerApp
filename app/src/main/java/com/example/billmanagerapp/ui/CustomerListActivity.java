package com.example.billmanagerapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.model.Customer;
import com.example.billmanagerapp.ui.adapter.CustomerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomerListActivity extends AppCompatActivity {

    private List<Customer> customerList = new ArrayList<>();
    private List<Customer> filteredList = new ArrayList<>();

    private CustomerAdapter adapter;
    private RecyclerView recyclerView;

    @SuppressLint({"MissingInflatedId", "LocalSuppress", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        SearchView searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerViewCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Button buttonClearAll = findViewById(R.id.buttonClearAllCustomers);

        // 读取数据
        new Thread(() -> {
            customerList = DatabaseInstance.getDatabase(this).customerDAO().getAllCustomers();
            filteredList = new ArrayList<>(customerList);
            runOnUiThread(() -> {
                adapter = new CustomerAdapter(filteredList);
                adapter.setLongClickListener(customer -> {
                    new AlertDialog.Builder(this)
                            .setTitle("确认删除？")
                            .setMessage("删除客户后无法恢复，是否继续？")
                            .setPositiveButton("删除", (dialog, which) -> {
                                new Thread(() -> {
                                    DatabaseInstance.getDatabase(this).customerDAO().delete(customer);
                                    runOnUiThread(() -> {
                                        if (customerList.contains(customer)){
                                            customerList.remove(customer);
                                            filteredList.remove(customer);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }).start();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                });
                recyclerView.setAdapter(adapter);
            });

            runOnUiThread(() -> {
                //搜索实现
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        filteredList.clear();
                        for (Customer customer : customerList) {
                            if (customer.name.toLowerCase().contains(s.toLowerCase()) || customer.phone.toLowerCase().contains(s.toLowerCase())){
                                filteredList.add(customer);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                });
            });


            // 清空全部
            buttonClearAll.setOnClickListener(v -> {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("确认删除？");
                builder.setMessage("删除客户后无法恢复，是否继续？");
                builder.setPositiveButton("确定", (dialog, which) -> {
                    new Thread(() -> {
                        DatabaseInstance.getDatabase(this).customerDAO().deleteAllCustomers();
                        runOnUiThread(() -> {
                            customerList.clear();
                            filteredList.clear();
                            adapter.notifyDataSetChanged();
                            finish();
                        });
                    }).start();
                });

                builder.setNegativeButton("取消", null);
                builder.show();

            });
        }).start();
    }
}