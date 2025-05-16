package com.example.billmanagerapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.model.Invoice;
import com.example.billmanagerapp.model.MaterialItem;
import com.example.billmanagerapp.model.ServiceItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.List;

public class InvoiceDetailActivity extends AppCompatActivity {

    private TextView textInvoiceDate;
    private TextView textCustomerName;
    private TextView textCustomerPhone;
    private TextView textCustomerAddress;
    private TextView textMaterialCost;
    private TextView textTotalCost;
    private TextView textNote;
    private LinearLayout layoutServiceItems;
    private Invoice invoice;
    private File exportedPdfFile;
    private TextView textInvoiceNumber;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        textInvoiceNumber = findViewById(R.id.textInvoiceNumber);
        textInvoiceDate = findViewById(R.id.textInvoiceDate);
        textCustomerName = findViewById(R.id.textCustomerName);
        textCustomerPhone = findViewById(R.id.textCustomerPhone);
        textCustomerAddress = findViewById(R.id.textCustomerAddress);
        //textMaterialCost = findViewById(R.id.textMaterialCost);
        textTotalCost = findViewById(R.id.textTotalCost);
        textNote = findViewById(R.id.textNote);
        layoutServiceItems = findViewById(R.id.layoutServiceItems);

        Button buttonPDF = findViewById(R.id.buttonExportPDF);
        Button buttonShare = findViewById(R.id.buttonShareInvoice);
        Button buttonEdit = findViewById(R.id.btn_edit);

        //获取传递过来的发票对象（通过 Intent）
        int invoiceId = getIntent().getIntExtra("invoice_id", -1);
        if (invoiceId == -1) {
            finish();
            return;
        }

        new Thread(() -> {
            invoice = DatabaseInstance.getDatabase(this).invoiceDao().getInvoiceById(invoiceId);

            runOnUiThread(() -> {
                if (invoice != null) {
                    showInvoiceDetails(invoice);
                }
            });
        }).start();

        // 点击按钮功能（稍后添加）
        buttonPDF.setOnClickListener(v -> createStyledPdf());
        buttonShare.setOnClickListener(v -> sharePdf());
        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvoiceActivity.class);

            new Thread(() -> {
                invoice = DatabaseInstance.getDatabase(this).invoiceDao().getInvoiceById(invoiceId);

                runOnUiThread(() -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("invoice_id", invoice.id);
                    bundle.putString("invoice_number", invoice.invoiceNumber);
                    bundle.putString("invoice_date", invoice.date);
                    bundle.putString("invoice_name", invoice.customerName);
                    bundle.putString("invoice_phone", invoice.phone);
                    bundle.putString("invoice_address", invoice.address);
                    bundle.putString("invoice_service", invoice.service);
                    bundle.putDouble("invoice_mc", invoice.materialCost);
                    bundle.putString("invoice_note", invoice.note);

                    intent.putExtras(bundle);
                    startActivity(intent);
                });
            }).start();
        });

    }

    private void showInvoiceDetails(Invoice invoice) {
        textInvoiceNumber.setText(invoice.invoiceNumber);
        textInvoiceDate.setText("日期：" + invoice.date);
        textCustomerName.setText("客户：" + invoice.customerName);
        textCustomerPhone.setText("电话：" + invoice.phone);
        textCustomerAddress.setText("地址：" + invoice.address);
        textNote.setText("备注：" + (invoice.note == null ? "无" : invoice.note));

        //从 service 字段解析服务项目（例如存的是JSON字符串）
        Gson gson = new Gson();
        layoutServiceItems.removeAllViews();
        double total = 0;

        // 1. 显示服务项目（带字母）
        try {
            Type listType = new TypeToken<List<ServiceItem>>() {
            }.getType();
            List<ServiceItem> serviceList = gson.fromJson(invoice.service, listType);

            char label = 'A';
            for (ServiceItem item : serviceList) {
                TextView text = new TextView(this);
                text.setText(label + "." + item.name + " - RM " + String.format("%.2f", item.price));
                text.setTextSize(16f);
                layoutServiceItems.addView(text);

                total += item.price;
                label++;
            }
        } catch (Exception e) {
            // 如果不是JSON格式
            TextView text = new TextView(this);
            text.setText(invoice.service);
            text.setTextSize(16f);
            layoutServiceItems.addView(text);

            total += invoice.materialCost;
            e.printStackTrace();
        }

        // 2. 显示材料明细（材料JSON 字段）
        try {
            if (invoice.materialJson != null && !invoice.materialJson.isEmpty()) {
                Type materialType = new TypeToken<List<MaterialItem>>() {
                }.getType();
                List<MaterialItem> materials = gson.fromJson(invoice.materialJson, materialType);

                for (MaterialItem item : materials) {
                    if (item.quantity > 0) {
                        TextView text = new TextView(this);
                        double subtotal = item.price * item.quantity;
                        text.setText(item.quantity + "x" + item.name + " - RM " + String.format("%.2f", subtotal));
                        text.setTextSize(16f);
                        layoutServiceItems.addView(text);

                        total += subtotal;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. 设置总价
        textTotalCost.setText("总计：RM" + String.format("%.2f", total));
    }

    private void createStyledPdf() {
        PdfDocument document = new PdfDocument();
        int pageWidth = 595;  // A4 width
        int pageHeight = 842; // A4 height

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18f);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(14f);
        normalPaint.setColor(Color.BLACK);
        normalPaint.setTextAlign(Paint.Align.LEFT); // 默认左对齐

        Paint rightAlignPaint = new Paint(normalPaint);
        rightAlignPaint.setTextAlign(Paint.Align.RIGHT); // 用于金额右对齐

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1f);
        linePaint.setColor(Color.BLACK);

        int marginLeft = 40;
        int y = 50;

        // 公司信息

        canvas.drawText("YUN CHUAN HARDWARE TRADING", marginLeft, y, normalPaint);
        y += 18;
        canvas.drawText("(JR0051859-D)", marginLeft, y, normalPaint);
        y += 18;
        canvas.drawText("38, Jalan Gemilang, Taman Bukit Flora, 83000 Batu Pahat, Johor.", marginLeft, y, normalPaint);
        y += 18;
        canvas.drawText("H/P: 012-7507505", marginLeft, y, normalPaint);
        y += 25;
        canvas.drawText("Invoice", pageWidth / 2 - 30, y, titlePaint);
        y += 30;

        // 发票编号（右上角）
        if (invoice.invoiceNumber != null) {
            Paint redPaint = new Paint();
            redPaint.setColor(Color.RED);
            redPaint.setTextSize(16f);
            canvas.drawText("No. " + invoice.invoiceNumber, pageWidth - 200, y - 15, redPaint);
        }

        // 客户和日期
        canvas.drawText("M/S: " + invoice.customerName, marginLeft, y, normalPaint);
        canvas.drawText("Date: " + invoice.date, pageWidth - 200, y, normalPaint);
        y += 20;
        canvas.drawText(invoice.address, marginLeft, y, normalPaint);
        y += 25;

        // 表头行位置准备
        int tableStartY = y;
        int rowHeight = 30;
        int colQty = marginLeft;
        int colDesc = colQty + 60;
        int colPrice = colDesc + 300;
        int colAmount = colPrice + 80;

        // 表头
        canvas.drawLine(marginLeft, tableStartY, pageWidth - marginLeft, tableStartY, linePaint);
        canvas.drawText("Qty", colQty + 10, tableStartY + 20, normalPaint);
        canvas.drawText("Particulars", colDesc + 10, tableStartY + 20, normalPaint);
        canvas.drawText("Price", colPrice + 10, tableStartY + 20, normalPaint);
        canvas.drawText("Amount", colAmount + 10, tableStartY + 20, normalPaint);

        y = tableStartY + rowHeight;
        canvas.drawLine(marginLeft, y, pageWidth - marginLeft, y, linePaint);

        // 画服务项目
        Gson gson = new Gson();
        double total = 0;
        try {
            Type listType = new TypeToken<List<ServiceItem>>() {
            }.getType();
            List<ServiceItem> services = gson.fromJson(invoice.service, listType);

            char serviceLabel = 'A';
            for (ServiceItem item : services) {
                canvas.drawLine(marginLeft, y, pageWidth - marginLeft, y, linePaint); // row line
                canvas.drawText(String.valueOf(serviceLabel), colQty + 10, y + 20, normalPaint);
                canvas.drawText(item.name, colDesc + 10, y + 20, normalPaint);
                canvas.drawText(String.format("%.2f", item.price), colPrice + 70, y + 20, rightAlignPaint);
                canvas.drawText(String.format("%.2f", item.price), colAmount + 70, y + 20, rightAlignPaint);
                y += rowHeight;
                total += item.price;
                serviceLabel++;
            }
        } catch (Exception e) {
            canvas.drawText(invoice.service, colDesc + 10, y + 20, normalPaint);
            y += rowHeight;
        }

        // 材料费
        if (invoice.materialJson != null && !invoice.materialJson.isEmpty()) {
            Type materialListType = new TypeToken<List<MaterialItem>>() {
            }.getType();
            List<MaterialItem> materialItems = gson.fromJson(invoice.materialJson, materialListType);

            for (MaterialItem item : materialItems) {
                if (item.quantity > 0) {
                    canvas.drawLine(marginLeft, y, pageWidth - marginLeft, y, linePaint);
                    canvas.drawText(String.valueOf(item.quantity), colQty + 10, y + 20, normalPaint);// 数量
                    canvas.drawText(item.name, colDesc + 10, y + 20, normalPaint);
                    canvas.drawText(String.format("%.2f", item.price), colPrice + 70, y + 20, rightAlignPaint);
                    canvas.drawText(String.format("%.2f", item.price * item.quantity), colAmount + 70, y + 20, rightAlignPaint);
                    total += item.price * item.quantity;
                    y += rowHeight;
                }
            }
        }

        // 表格底线
        canvas.drawLine(marginLeft, y, pageWidth - marginLeft, y, linePaint);

        // TOTAL 金额
        y += 20;
        normalPaint.setFakeBoldText(true);
        canvas.drawText("TOTAL RM", colPrice + 10, y, normalPaint);
        canvas.drawText(String.format("%.2f", total), colAmount + 70, y, rightAlignPaint);
        normalPaint.setFakeBoldText(false);

        // 备注
        y += 40;
        canvas.drawText("Note: " + (invoice.note == null || invoice.note.isEmpty() ? "None" : invoice.note), marginLeft, y, normalPaint);

        // 签名栏
        int sigY = pageHeight - 70;
        canvas.drawLine(marginLeft, sigY, marginLeft + 100, sigY, linePaint);
        canvas.drawLine(pageWidth - 150, sigY, pageWidth - 50, sigY, linePaint);

        canvas.drawText("Issued by", marginLeft, sigY + 15, normalPaint);
        canvas.drawText("Received by", pageWidth - 150, sigY + 15, normalPaint);

        // 完成页面
        document.finishPage(page);

        // 保存到文件
        File file = new File(getExternalFilesDir(null), "invoice_" + System.currentTimeMillis() + ".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            exportedPdfFile = file;
            Toast.makeText(this, "PDF 已保存：" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void sharePdf() {
        if (exportedPdfFile == null || !exportedPdfFile.exists()) {
            createStyledPdf();
        }

        // 延迟一点点时间再执行分享（确保 PDF 写入完成）
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (exportedPdfFile != null && exportedPdfFile.exists()) {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", exportedPdfFile);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, "分享发票 PDF"));
            } else {
                Toast.makeText(this, "PDF 未生成，请重试", Toast.LENGTH_SHORT).show();
            }
        }, 300);

    }
}