package com.example.billmanagerapp.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.model.MaterialItem;

import java.util.List;

/*
* 材料选择
*
* */
public class MaterialItemAdapter extends RecyclerView.Adapter<MaterialItemAdapter.MaterialViewHolder> {

    private final List<MaterialItem> materialItems;

    public MaterialItemAdapter(List<MaterialItem> materialItems) {
        this.materialItems = materialItems;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material_selection, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        MaterialItem item = materialItems.get(position);
        if (item.name == null) item.name = "";
        int qty = item.quantity <= 0 ? 1 : item.quantity;

        holder.textMaterialName.setText(item.name);
        holder.textQuantity.setText(String.valueOf(item.quantity));
        double total = item.price * qty;
        holder.textTotalPrice.setText(String.format("RM %.2f", total));

        holder.btnAdd.setOnClickListener(view -> {
            item.quantity++;
            notifyItemChanged(position);
        });

        holder.btnRemove.setOnClickListener(view -> {
            if (item.quantity >= 1){
                item.quantity--;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialItems.size();
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder{
        TextView textMaterialName, textQuantity, textTotalPrice;
        ImageButton btnAdd, btnRemove;
        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            textMaterialName = itemView.findViewById(R.id.textMaterialName);
            textQuantity = itemView.findViewById(R.id.textMaterialQuantity);
            textTotalPrice = itemView.findViewById(R.id.textMaterialTotal);
            btnAdd = itemView.findViewById(R.id.buttonAddMaterial);
            btnRemove = itemView.findViewById(R.id.buttonRemoveMaterial);
        }
    }
}
