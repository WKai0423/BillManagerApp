package com.example.billmanagerapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.data.DatabaseInstance;
import com.example.billmanagerapp.model.ServiceItem;
import com.example.billmanagerapp.ui.ServiceItemManagerActivity;

import java.util.List;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ViewHolder> {

    private final Context context;
    private List<ServiceItem> itemList;

    public ServiceItemAdapter(Context context, List<ServiceItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void updateList(List<ServiceItem> newList){
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceItem item = itemList.get(position);
        holder.textName.setText(item.name);
        holder.textPrice.setText("RM" + item.price);

        holder.itemView.setOnLongClickListener(v -> {
            new Thread(() -> {
                DatabaseInstance.getDatabase(context).serviceItemDAO().delete(item);
                ((android.app.Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "已删除：" + item.name, Toast.LENGTH_SHORT).show();

                    // 主动调用 ServiceItemManagerActivity 的 loadServiceItems()
                    if (context instanceof ServiceItemManagerActivity) {
                        ((ServiceItemManagerActivity) context).loadServiceItems();
                    }
                });
            }).start();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textServiceName);
            textPrice = itemView.findViewById(R.id.textServicePrice);
        }
    }
}
