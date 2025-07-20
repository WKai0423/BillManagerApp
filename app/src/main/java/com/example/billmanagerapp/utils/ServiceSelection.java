package com.example.billmanagerapp.utils;

import com.example.billmanagerapp.model.ServiceItem;

public class ServiceSelection {
    public ServiceSelection item;
    public int quantity;

    public ServiceSelection(ServiceSelection item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}
