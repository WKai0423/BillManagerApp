package com.example.billmanagerapp.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.data.dp.ServiceItemDAO;
import com.example.billmanagerapp.model.Customer;
import com.example.billmanagerapp.model.Invoice;
import com.example.billmanagerapp.model.MaterialItem;
import com.example.billmanagerapp.model.ServiceItem;
import com.example.billmanagerapp.ui.adapter.MaterialItemAdapter;
import com.example.billmanagerapp.ui.adapter.MaterialSelectionAdapter;
import com.example.billmanagerapp.ui.adapter.ServiceSelectionAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextDate, editTextName, editTextPhone, editTextAddress,
            editTextNote;
    private TextView editTextMaterialCost;

    private Customer selectedCustomer;
    private TextView tvsCustomer;

    private List<ServiceItem> allServiceItems = new ArrayList<>();

    private List<ServiceItem> selectedServiceItems = new ArrayList<>();

    private List<MaterialItem> selectedMaterialItems = new ArrayList<>();
    private TextView textSelectedServices;

    private Invoice invoice;
    private String originalDate;
    private String originalName;
    private String originalPhone;
    private String originalAddress;
    private int originalInvoiceId;
    private String invoiceNumber;

    @SuppressLint({"DefaultLocale", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        editTextDate = findViewById(R.id.editTextDate);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextMaterialCost = findViewById(R.id.editTextMaterialCost);
        editTextNote = findViewById(R.id.editTextNote);

        tvsCustomer = findViewById(R.id.textViewSelectedCustomer);


        // 编辑或另外存账单
        Intent intent1 = getIntent();
        if (intent1 != null && intent1.getExtras() != null) {
            Bundle bundle = intent1.getExtras();

            originalInvoiceId = bundle.getInt("invoice_id");
            invoiceNumber = bundle.getString("invoice_number");

            originalDate = bundle.getString("invoice_date");
            originalName = bundle.getString("invoice_name");
            originalPhone = bundle.getString("invoice_phone");
            originalAddress = bundle.getString("invoice_address");

            //设置字段
            editTextDate.setText(originalDate);
            Log.d("InvoiceDebug", "收到日期: " + bundle.getString("invoice_date"));
            editTextName.setText(originalName);
            editTextPhone.setText(originalPhone);
            editTextAddress.setText(originalAddress);
            editTextMaterialCost.setText(String.valueOf(bundle.getDouble("invoice_mc")));
            editTextNote.setText(bundle.getString("invoice_note"));

            // 解析服务 JSON 字符串
            String serviceJson = bundle.getString("invoice_service");
            if (!serviceJson.isEmpty()) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<ServiceItem>>() {
                }.getType();
                selectedServiceItems = gson.fromJson(serviceJson, type);

                updateSelectedServicesText();

            }
        }

        //注册并接受数据，自动填入客户信息
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                selectedCustomer = new Customer();
                selectedCustomer.id = intent.getIntExtra("customer_id", -1);
                selectedCustomer.name = intent.getStringExtra("customer_name");
                selectedCustomer.phone = intent.getStringExtra("customer_phone");
                selectedCustomer.address = intent.getStringExtra("customer_address");

                tvsCustomer.setText("客户：" + selectedCustomer.name);
                editTextName.setText(selectedCustomer.name);
                editTextPhone.setText(selectedCustomer.phone);
                editTextAddress.setText(selectedCustomer.address);

                editTextName.setEnabled(false);
                editTextPhone.setEnabled(false);
                editTextAddress.setEnabled(false);

            }
        });
        //转去CustomerPickerActivity为了选择客户信息
        tvsCustomer.setOnClickListener(view -> {
            Intent intent = new Intent(this, CustomerPickerActivity.class);
            launcher.launch(intent);
        });

        // 添加材料的按钮
        Button buttonAddMaterial = findViewById(R.id.buttonAddMaterial);
        buttonAddMaterial.setOnClickListener(view -> showMaterialInputDialog());
        // 弹出对话框选择材料
        editTextMaterialCost.setOnClickListener(view -> showMaterialDialog());

        Button buttonSelectServices = findViewById(R.id.buttonSelectServices);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonIntent = findViewById(R.id.buttonIntent);


        //日期对话框
        editTextDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(InvoiceActivity.this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String dateStr = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                        editTextDate.setText(dateStr);
                    }, year, month, day);
            datePickerDialog.show();
        });

        if (intent1 == null || intent1.getExtras() == null) {
            // 自动写入当前日期到 editTextDate
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String todayStr = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
            editTextDate.setText(todayStr);
        }


        buttonSave.setOnClickListener(this);
        buttonIntent.setOnClickListener(this);
        buttonSelectServices.setOnClickListener(this);


    }

    // 添加材料的对话框
    @SuppressLint("MissingInflatedId")
    private void showMaterialInputDialog() {
        List<MaterialItem> inputList = new ArrayList<>();
        inputList.add(new MaterialItem("", 0));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_material_input, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerInputMaterials);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialSelectionAdapter adapter = new MaterialSelectionAdapter(inputList);
        recyclerView.setAdapter(adapter);

        builder.setTitle("新增材料").setView(view).setPositiveButton("保存", (dialog, which) -> {
            List<MaterialItem> newItems = adapter.getSelectedMaterials();
            new Thread(() -> {
                for (MaterialItem item : newItems) {
                    if (!item.name.isEmpty()) {
                        DatabaseInstance.getDatabase(this).materialItemDAO().insert(item);
                    }
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "材料已保存", Toast.LENGTH_SHORT).show();
                });
            }).start();
        }).setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // 等待界面绘制完成后再弹出键盘
        recyclerView.postDelayed(() -> {
            RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(0);
            if (vh != null && vh.itemView != null) {
                View nameEdit = vh.itemView.findViewById(R.id.editMaterialName);
                if (nameEdit != null) {
                    nameEdit.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(nameEdit, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }, 300);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.buttonSave) {
            save();
        } else if (view.getId() == R.id.buttonSelectServices) {
            showServiceSelectionDialog();
        } else if (view.getId() == R.id.buttonIntent) {
            Intent intent = new Intent(this, InvoiceListActivity.class);
            startActivity(intent);
            finish();
        }

    }

    //选项按钮
    private void showServiceSelectionDialog() {
        new Thread(() -> {

            ServiceItemDAO dao = DatabaseInstance.getDatabase(this).serviceItemDAO();
            allServiceItems = DatabaseInstance.getDatabase(this).serviceItemDAO().getAll();


            //初始化
            if (dao.getAll().isEmpty()){
                dao.insert(new ServiceItem("更换水龙头", 30.0));
                dao.insert(new ServiceItem("安装马桶", 120.0));
                dao.insert(new ServiceItem("水管维修", 50.0));
            }

            runOnUiThread(() -> {

                //1.  创建 RecyclerView 和 Adapter
                RecyclerView recyclerView = new RecyclerView(this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                ServiceSelectionAdapter adapter = new ServiceSelectionAdapter(allServiceItems);
                recyclerView.setAdapter(adapter);

                // 2. 创建 Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择服务项目");
                builder.setView(recyclerView);


                builder.setPositiveButton("确定", (dialog, which) -> {
                    Map<ServiceItem, Integer> selectedMap = adapter.getSelectedItemsWithQuantity();

                    selectedServiceItems.clear();// 先清空
                    for (Map.Entry<ServiceItem, Integer> entry : selectedMap.entrySet()) {
                        ServiceItem item = entry.getKey();
                        int qty = entry.getValue();
                        if (qty > 0) {
                            ServiceItem newItem = new ServiceItem(item.name, item.price * qty);
                            newItem.id = item.id;// 保持ID不变
                            selectedServiceItems.add(newItem);

                        }
                    }

                    updateSelectedServicesText();
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            });
        }).start();
    }

    // 选择材料
    @SuppressLint("MissingInflatedId")
    private void showMaterialDialog() {
        new Thread(() -> {
            List<MaterialItem> allMaterials = DatabaseInstance.getDatabase(this).materialItemDAO().getAll();


            for (MaterialItem item : allMaterials) {
                item.quantity = 0;
            }

            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.dialog_material_list, null);
                RecyclerView recyclerView = view.findViewById(R.id.recyclerMaterialList);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                MaterialItemAdapter adapter = new MaterialItemAdapter(allMaterials);
                recyclerView.setAdapter(adapter);

                builder.setTitle("选择材料").setView(view).setPositiveButton("确定", (dialog, which) -> {
                    selectedMaterialItems.clear();
                    selectedMaterialItems.addAll(allMaterials);

                    double total = 0;
                    for (MaterialItem item : selectedMaterialItems) {
                        total += item.price * item.quantity;
                    }

                    editTextMaterialCost.setText(String.format("%.2f", total));
                    Toast.makeText(this, "材料已选择，总价 RM " + total, Toast.LENGTH_SHORT).show();
                }).setNegativeButton("取消", null).show();
            });
        }).start();
    }

    //显示已选的服务和总价
    private void updateSelectedServicesText() {
        //服务名
        StringBuilder names = new StringBuilder();
        //总价
        double total = 0;
        for (ServiceItem item : selectedServiceItems) {
            names.append(item.name).append(" (RM").append(String.format("%.2f", item.price)).append("), ");
            total += item.price;
        }
        if (names.length() > 0) {
            names.setLength(names.length() - 2);//去掉末尾的逗号和空格
        }

        textSelectedServices = findViewById(R.id.textSelectedServices);
        textSelectedServices.setText("已选服务：" + names + "\n服务费：RM" + String.format("%.2f", total));
    }

    private void save() {
        String date = editTextDate.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String note = editTextNote.getText().toString().trim();


        //防空数值
        if (date.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "请输入日期和客户姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (textSelectedServices == null) {
            Toast.makeText(this, "请选择服务项目", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ 使用 JSON 存储服务列表

        Gson gson = new Gson();
        String serviceJson = gson.toJson(selectedServiceItems);
        String materialJson = new Gson().toJson(selectedMaterialItems);

        boolean isKeyInfoSame = originalDate != null && originalDate.equals(date)
                && originalName != null && originalName.equals(name)
                && originalPhone != null && originalPhone.equals(phone)
                && originalAddress != null && originalAddress.equals(address);

        invoice = new Invoice();
        invoice.date = date;
        invoice.customerName = name;
        invoice.phone = phone;
        invoice.address = address;
        invoice.service = serviceJson;
        invoice.materialJson = materialJson;
        invoice.note = note;

        if (isKeyInfoSame) {
            invoice.id = originalInvoiceId;
            invoice.invoiceNumber = invoiceNumber;

            new Thread(() -> {
                DatabaseInstance.getDatabase(this).invoiceDao().update(invoice);
                runOnUiThread(() -> {
                    Toast.makeText(this, "发票已更新", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, InvoiceListActivity.class));
                    finish();
                });
            }).start();

        } else {

            new Thread(() -> {
                // 1. 获取下一个发票编号
                String lastNumber = DatabaseInstance.getDatabase(this).invoiceDao().getLastInvoiceNumber();
                int nextNumber = 1;
                if (lastNumber != null && lastNumber.startsWith("INV")) {
                    try {
                        String numberPart = lastNumber.substring(3);
                        nextNumber = Integer.parseInt(numberPart) + 1;
                    } catch (Exception ignored) {}
                }
                invoice.invoiceNumber = String.format("INV%04d", nextNumber);

                // 2. 检查客户是否存在
                List<Customer> customerList = DatabaseInstance.getDatabase(this).customerDAO().getAllCustomers();
                boolean customerExists = false;
                for (Customer customer : customerList) {
                    if (name.equals(customer.name) && phone.equals(customer.phone) && address.equals(customer.address)) {
                        customerExists = true;
                        break;
                    }
                }
                if (!customerExists) {
                    Customer cus = new Customer();
                    cus.name = name;
                    cus.phone = phone;
                    cus.address = address;
                    DatabaseInstance.getDatabase(this).customerDAO().insert(cus);
                }

                // 3. 插入发票
                DatabaseInstance.getDatabase(this).invoiceDao().insert(invoice);

                // 4. 回到主线程清空输入等
                runOnUiThread(() -> {
                    Toast.makeText(this, "账单已保存", Toast.LENGTH_SHORT).show();
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    String todayStr = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
                    editTextDate.setText(todayStr);
                    tvsCustomer.setText("选择客户");
                    editTextName.setText("");
                    editTextPhone.setText("");
                    editTextAddress.setText("");
                    editTextMaterialCost.setText("");
                    editTextNote.setText("");
                    textSelectedServices.setText("未选择服务");

                    Intent intent = new Intent(this, InvoiceListActivity.class);
                    startActivity(intent);
                    finish();
                });
            }).start();

        }


    }

}
