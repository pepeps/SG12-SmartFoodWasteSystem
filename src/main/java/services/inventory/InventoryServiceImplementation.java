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

import java.util.List;

public class InventoryServiceImplementation extends InventoryMonitoringServiceGrpc.InventoryMonitoringServiceImplBase {

    private final InventoryRepository repository = new InventoryRepository();

   // Server Streaming
    
    @Override
    public void getInventoryStatus(InventoryRequest request,
                                   StreamObserver<InventoryStatus> responseObserver) {

        if (request.getStoreId() == null || request.getStoreId().isEmpty()) {

            LogUtil.warn("Invalid request: storeId is empty");

            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("storeId is required")
                            .asRuntimeException()
            );
            return;
        }

        LogUtil.info("Fetching inventory for store: " + request.getStoreId());

        try {
            List<InventoryItem> items = repository.getAll();

            InventoryStatus response = InventoryStatus.newBuilder()
                    .setStoreId(request.getStoreId())
                    .addAllItems(items)
                    .setGeneratedAtEpochMs(System.currentTimeMillis())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LogUtil.error("Error fetching inventory", e);

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

        LogUtil.info("Streaming expiry alerts...");

        try {
            List<InventoryItem> items = repository.getAll();

            for (InventoryItem item : items) {

                long daysLeft = Math.max(0,
                        (item.getExpiryEpochMs() - System.currentTimeMillis())
                                / (1000 * 60 * 60 * 24)
                );

                if (daysLeft <= request.getHorizonDays()) {

                    ExpiryAlert alert = ExpiryAlert.newBuilder()
                            .setSku(item.getSku())
                            .setDaysToExpiry((int) daysLeft)
                            .setCurrentQuantityUnits(item.getQuantityUnits())
                            .build();

                    LogUtil.info("Sending alert for SKU: " + item.getSku());

                    responseObserver.onNext(alert);

                    Thread.sleep(800); // Simulate streaming
                }
            }

            responseObserver.onCompleted();

        } catch (Exception e) {
            LogUtil.error("Error in streaming alerts", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Streaming error")
                            .asRuntimeException()
            );
        }
    }
}