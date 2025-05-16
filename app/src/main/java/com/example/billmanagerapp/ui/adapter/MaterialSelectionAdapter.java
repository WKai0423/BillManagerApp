package com.example.billmanagerapp.ui.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmanagerapp.R;
import com.example.billmanagerapp.model.MaterialItem;

import java.util.List;

public class MaterialSelectionAdapter extends RecyclerView.Adapter<MaterialSelectionAdapter.MaterialViewHolder> {

    private final List<MaterialItem> materialItemList;

    public MaterialSelectionAdapter(List<MaterialItem> materialItemList){
        this.materialItemList = materialItemList;
    }

    public List<MaterialItem> getSelectedMaterials(){
        return materialItemList;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material_input, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        MaterialItem item = materialItemList.get(position);
        holder.editName.setText(item.name);
        holder.editPrice.setText(String.valueOf(item.price));

        holder.buttonDelete.setOnClickListener(v -> {
            materialItemList.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });

        holder.editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                item.name = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        holder.editPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    item.price = Double.parseDouble(charSequence.toString());
                }catch (Exception e){
                    item.price = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return materialItemList.size();
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder{
        EditText editName, editPrice;
        ImageButton buttonDelete;

        public MaterialViewHolder(@NonNull View itemView){
            super(itemView);
            editName = itemView.findViewById(R.id.editMaterialName);
            editPrice = itemView.findViewById(R.id.editMaterialPrice);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteMaterial);
        }
    }
}
