package com.example.billmanagerapp.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.data.dp.ServiceItemDAO;
import com.example.billmanagerapp.model.ServiceItem;
import com.example.billmanagerapp.ui.adapter.ServiceItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class ServiceItemManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceItemAdapter adapter;
    private List<ServiceItem> serviceItemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_item_manager);

        recyclerView = findViewById(R.id.recyclerViewService);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceItemAdapter(this, serviceItemList);
        recyclerView.setAdapter(adapter);

        new Thread(() -> {
            ServiceItemDAO dao = DatabaseInstance.getDatabase(this).serviceItemDAO();

            if (dao.getAll().isEmpty()){
                dao.insert(new ServiceItem("更换水龙头", 30.0));
                dao.insert(new ServiceItem("安装马桶", 120.0));
                dao.insert(new ServiceItem("水管维修", 50.0));
            }

            //刷新列表
            runOnUiThread(this::loadServiceItems);
        }).start();


        // 加载服务项目列表
        loadServiceItems();

        // 添加服务项目
        findViewById(R.id.buttonAddService).setOnClickListener(view -> showAddDialog());
    }

    public void loadServiceItems() {
        new Thread(() -> {
            List<ServiceItem> list = DatabaseInstance.getDatabase(this).serviceItemDAO().getAll();
            runOnUiThread(() -> adapter.updateList(list));
        }).start();
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        EditText editName = dialogView.findViewById(R.id.editServiceName);
        EditText editPrice = dialogView.findViewById(R.id.editServicePrice);

        new AlertDialog.Builder(this)
                .setTitle("添加服务项目")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String name = editName.getText().toString().trim();
                    String priceStr = editPrice.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "名称和价格不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "价格格式错误", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ServiceItem item = new ServiceItem(name, price);
                    new Thread(() -> {
                        DatabaseInstance.getDatabase(this).serviceItemDAO().insert(item);
                        loadServiceItems(); // 重新加载
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
