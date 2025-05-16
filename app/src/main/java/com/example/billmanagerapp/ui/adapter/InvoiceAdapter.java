package com.example.billmanagerapp.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.model.Invoice;
import com.example.billmanagerapp.ui.InvoiceListActivity;

import java.util.List;

//查看账单的格式
// 适配器类：用于把发票列表数据显示到 RecyclerView 上
public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private List<Invoice> invoiceList;// 所有发票数据的列表
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    // 构造函数，传入发票列表
    public InvoiceAdapter(List<Invoice> invoices){
        this.invoiceList = invoices;
    }

    public interface OnItemClickListener{
        void onItemClick(Invoice invoice);
    }

    public void setClickListener(OnItemClickListener listener){
        this.clickListener = listener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(Invoice invoice);
    }

    public void setLongClickListener(OnItemLongClickListener listener){
        this.longClickListener = listener;
    }

    // 创建每一行的视图（绑定布局）
    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 把 XML 布局 invoice_item 加载成一个 View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item, parent, false);
        return new InvoiceViewHolder(view);// 返回 ViewHolder 对象
    }


    // 把发票数据绑定到每一行的控件上
    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);// 拿到当前位置的发票数据
        // 把数据填到 item 的文本框里
        holder.textViewNumber.setText(invoice.invoiceNumber);
        holder.textViewDate.setText("日期：" + invoice.date);
        holder.textViewName.setText("客户：" + invoice.customerName);
        holder.textViewAddress.setText("地址：" + invoice.address);
        holder.textViewService.setText(invoice.service);
        holder.textViewMaterial.setText("材料费：RM" + invoice.materialCost);

        //点按事件
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null){
                clickListener.onItemClick(invoice);
            }
        });

        //添加长按事件
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null){
                longClickListener.onItemLongClick(invoice);
            }
            return true;
        });
    }

    // 总共有几条数据（用来控制 RecyclerView 有几行）
    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    // 这个类代表每一行的“视图”，里面有 TextView 控件
    public static class InvoiceViewHolder extends RecyclerView.ViewHolder{

        TextView textViewDate, textViewName, textViewAddress, textViewService, textViewMaterial, textViewNumber;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            // 绑定这一行里要显示的 TextView 控件
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewService = itemView.findViewById(R.id.textViewService);
            textViewMaterial = itemView.findViewById(R.id.textViewMaterial);

        }
    }
}
