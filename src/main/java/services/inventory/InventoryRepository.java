package services.inventory;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joseperez
 */


import generated.sdg.inventory.InventoryItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {

    private final List<InventoryItem> inventory = new ArrayList<>();

    public InventoryRepository() {
        seedData();
    }

    private void seedData() {

        inventory.add(InventoryItem.newBuilder()
                .setSku("MILK-021")
                .setName("Milk")
                .setQuantityUnits(20)
                .setExpiryEpochMs(Instant.now().plusSeconds(86400 * 2).toEpochMilli())
                .build());

        inventory.add(InventoryItem.newBuilder()
                .setSku("BREAD-001")
                .setName("Bread")
                .setQuantityUnits(15)
                .setExpiryEpochMs(Instant.now().plusSeconds(86400).toEpochMilli())
                .build());
        
        inventory.add(InventoryItem.newBuilder()
                .setSku("VG-001")
                .setName("Orange")
                .setQuantityUnits(65)
                .setExpiryEpochMs(Instant.now().plusSeconds(86400).toEpochMilli())
                .build());
    }

    public List<InventoryItem> getAll() {
        return inventory;
    }
}
