package com.example.billmanagerapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.model.Customer;

//客户管理界面
public class AddCustomerActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_customer);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();

            //判断是否为空
            if (name.isEmpty()) {
                Toast.makeText(this, "请输入客户姓名", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建客户对象
            Customer customer = new Customer();
            customer.name = name;
            customer.phone = phone;
            customer.address = address;

            // 存入数据库
            new Thread(() -> {
                DatabaseInstance.getDatabase(this).customerDAO().insert(customer);
                runOnUiThread(() -> {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish(); // 添加完成后关闭页面
                });
            }).start();
        });

    }
}