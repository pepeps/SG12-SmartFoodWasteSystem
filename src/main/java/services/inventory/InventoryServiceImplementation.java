/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.inventory;

/**
 *
 * @author joseperez
 */

import common.logging.LogUtil;
import generated.sdg.inventory.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InventoryServiceImplementation extends InventoryMonitoringServiceGrpc.InventoryMonitoringServiceImplBase {

    private final List<InventoryItem> inventory = new ArrayList<>();

    public InventoryServiceImplementation() {

        inventory.add(InventoryItem.newBuilder()
                .setSku("MILK-1L")
                .setName("Milk")
                .setQuantityUnits(20)
                .setExpiryEpochMs(Instant.now().plusSeconds(86400 * 2).toEpochMilli())
                .build());

        inventory.add(InventoryItem.newBuilder()
                .setSku("BREAD")
                .setName("Bread")
                .setQuantityUnits(15)
                .setExpiryEpochMs(Instant.now().plusSeconds(86400).toEpochMilli())
                .build());
    }

    // UNARY RPC
    
    @Override
    public void getInventoryStatus(InventoryRequest request,
                                   StreamObserver<InventoryStatus> responseObserver) {

        LogUtil.logger.info("Inventory request for store: {}", request.getStoreId());

        try {
            InventoryStatus response = InventoryStatus.newBuilder()
                    .setStoreId(request.getStoreId())
                    .addAllItems(inventory)
                    .setGeneratedAtEpochMs(System.currentTimeMillis())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LogUtil.logger.error("Error fetching inventory", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to fetch inventory")
                            .asRuntimeException()
            );
        }
    }

    // SERVER STREAMING RPC
    
    @Override
    public void streamExpiryAlerts(ExpiryAlertRequest request,
                                   StreamObserver<ExpiryAlert> responseObserver) {

        LogUtil.logger.info("Streaming expiry alerts...");

        try {
            for (InventoryItem item : inventory) {

                long daysLeft = (item.getExpiryEpochMs() - System.currentTimeMillis())
                        / (1000 * 60 * 60 * 24);

                if (daysLeft <= request.getHorizonDays()) {

                    ExpiryAlert alert = ExpiryAlert.newBuilder()
                            .setSku(item.getSku())
                            .setDaysToExpiry((int) daysLeft)
                            .setCurrentQuantityUnits(item.getQuantityUnits())
                            .build();

                    responseObserver.onNext(alert);

                    Thread.sleep(1000); // simula streaming real
                }
            }

            responseObserver.onCompleted();

        } catch (Exception e) {
            LogUtil.logger.error("Error in streaming alerts", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Streaming error")
                            .asRuntimeException()
            );
        }
    }
}