package com.example.billmanagerapp.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.model.Invoice;
import com.example.billmanagerapp.ui.adapter.InvoiceAdapter;

import java.util.ArrayList;
import java.util.List;

//查看界面
public class InvoiceListActivity extends AppCompatActivity {

    private RecyclerView view; // 用于显示所有发票列表的 RecyclerView

    private List<Invoice> invoiceList = new ArrayList<>();
    private List<Invoice> filteredList = new ArrayList<>();
    private InvoiceAdapter adapter;


    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        SearchView searchView = findViewById(R.id.searchView);
        view = findViewById(R.id.recyclerViewInvoices);
        view.setLayoutManager(new LinearLayoutManager(this));// 设置纵向列表布局

        Button buttonClearAll = findViewById(R.id.buttonClearAll);


        new Thread(() -> {
            invoiceList = DatabaseInstance.getDatabase(this).invoiceDao().getAllInvoice();
            filteredList = new ArrayList<>(invoiceList);
            runOnUiThread(() -> {
                // 创建适配器并传入数据
                adapter = new InvoiceAdapter(filteredList);

                //点击跳转生成PDF
                adapter.setClickListener(invoice -> {
                    Intent intent = new Intent(this, InvoiceDetailActivity.class);
                    intent.putExtra("invoice_id", invoice.id);
                    startActivity(intent);
                });

                //长按删除
                adapter.setLongClickListener(invoice -> new AlertDialog.Builder(this).setTitle("是否要删除").setMessage("真的要删除吗？？删了就无法回复了哦~").setPositiveButton("确定", (dialog, which) -> new Thread(() -> {
                    DatabaseInstance.getDatabase(this).invoiceDao().deleteInvoice(invoice);

                    runOnUiThread(() -> {
                        invoiceList.remove(invoice);
                        filteredList.remove(invoice);
                        adapter.notifyDataSetChanged();
                    });
                }).start()).setNegativeButton("取消", null).show());
                view.setAdapter(adapter);// 把适配器绑定到 RecyclerView 上
            });

            new Thread(() -> {
                invoiceList = DatabaseInstance.getDatabase(this).invoiceDao().getAllInvoice();
                filteredList.clear();
                filteredList.addAll(invoiceList);

                runOnUiThread(() -> adapter.notifyDataSetChanged());
            });


            runOnUiThread(() -> {
                //搜索功能
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        filteredList.clear();
                        for (Invoice invoice : invoiceList) {
                            if (invoice.customerName.toLowerCase().contains(s.toLowerCase()) || invoice.invoiceNumber.toLowerCase().contains(s.toLowerCase())) {
                                filteredList.add(invoice);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                });
            });


            //清空按钮
            buttonClearAll.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("是否要删除？");
                builder.setMessage("真的要删除吗？？删了就无法回复了哦~");
                builder.setPositiveButton("确定", (dialog, which) -> {
                    new Thread(() -> {
                        DatabaseInstance.getDatabase(this).invoiceDao().deleteAllInvoices();

                        runOnUiThread(() -> {
                            if (invoiceList != null && adapter != null) {
                                invoiceList.clear();
                                filteredList.clear();
                                adapter.notifyDataSetChanged();
                                finish();
                            }
                        });
                    }).start();
                });
                builder.setNegativeButton("取消", null);
                builder.show();

            });

        }).start();
    }
}