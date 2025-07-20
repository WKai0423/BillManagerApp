package com.example.billmanagerapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.model.ServiceItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceSelectionAdapter extends RecyclerView.Adapter<ServiceSelectionAdapter.ViewHolder> {

    private final List<ServiceItem> serviceItems;
    private final Map<ServiceItem, Integer> quantitMap = new HashMap<>();

    public ServiceSelectionAdapter(List<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
        for (ServiceItem item : serviceItems) {
            quantitMap.put(item, 0);//初始化为0
        }
    }

    public Map<ServiceItem, Integer> getSelectedItemsWithQuantity(){
        return quantitMap;
    }

    @NonNull
    @Override
    public ServiceSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceSelectionAdapter.ViewHolder holder, int position) {
        ServiceItem item = serviceItems.get(position);
        holder.textName.setText(item.name + "- RM" + item.price);

        int qty = quantitMap.getOrDefault(item, 0);
        holder.textQuantity.setText(String.valueOf(qty));

        holder.buttonAdd.setOnClickListener(v -> {
            quantitMap.put(item, qty + 1);
            notifyItemChanged(position);
        });

        holder.buttonMinus.setOnClickListener(v -> {
            if (qty > 0){
                quantitMap.put(item, qty - 1);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textName, textQuantity;
        Button buttonAdd, buttonMinus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textServiceName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            buttonAdd = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
        }
    }
}
