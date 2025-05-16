package com.example.billmanagerapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.model.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private OnItemLongClickListener longClickListener;

    private OnCustomerClickListener clickListener;

    public interface OnCustomerClickListener{
        void onCustomerClick(Customer customer);
    }

    public void setClickListener(OnCustomerClickListener listener){
        this.clickListener = listener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(Customer customer);
    }

    public void setLongClickListener(OnItemLongClickListener listener){
        this.longClickListener = listener;
    }

    public CustomerAdapter(List<Customer> customers){
        this.customerList = customers;
    }

    @NonNull
    @Override
    public CustomerAdapter.CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.textViewName.setText("客户名：" + customer.name);
        holder.textViewPhone.setText("电话：" + customer.phone);
        holder.textViewAddress.setText("地址：" + customer.address);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null){
                clickListener.onCustomerClick(customer);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(customer);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPhone, textViewAddress;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
        }
    }
}
