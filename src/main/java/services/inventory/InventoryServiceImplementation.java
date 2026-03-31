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

    // Repository that simulates a data source (in-memory)
    private final InventoryRepository repository = new InventoryRepository();

    
    // UNARY RPC

    @Override
    public void getInventoryStatus(InventoryRequest request,
                                   StreamObserver<InventoryStatus> responseObserver) {

        // Log the RPC type
        LogUtil.info("RPC TYPE: UNARY → getInventoryStatus called");

        // Validate input
        if (request.getStoreId() == null || request.getStoreId().isEmpty()) {

            LogUtil.warn("Invalid request: storeId is empty");

            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("storeId is required")
                            .asRuntimeException()
            );
            return;
        }

        try {
            // Fetch all inventory items
            List<InventoryItem> items = repository.getAll();

            // Build response (single response → unary)
            InventoryStatus response = InventoryStatus.newBuilder()
                    .setStoreId(request.getStoreId())
                    .addAllItems(items)
                    .setGeneratedAtEpochMs(System.currentTimeMillis())
                    .build();

            // Send response to client
            responseObserver.onNext(response);

            // Complete the call
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

        // Log streaming start
        LogUtil.info("RPC TYPE: SERVER STREAMING → streamExpiryAlerts started");

        try {
            List<InventoryItem> items = repository.getAll();

            // Iterate through inventory items
            for (InventoryItem item : items) {

                // Calculate days remaining until expiry
                long daysLeft = Math.max(0,
                        (item.getExpiryEpochMs() - System.currentTimeMillis())
                                / (1000 * 60 * 60 * 24)
                );

                // Only send alerts within requested horizon
                if (daysLeft <= request.getHorizonDays()) {

                    ExpiryAlert alert = ExpiryAlert.newBuilder()
                            .setSku(item.getSku())
                            .setDaysToExpiry((int) daysLeft)
                            .setCurrentQuantityUnits(item.getQuantityUnits())
                            .build();

                    // Log each streaming event
                    LogUtil.info("STREAM EVENT → Sending alert for SKU: " + item.getSku());

                    // Send each message independently (streaming behavior)
                    responseObserver.onNext(alert);

                    // Simulate real-time streaming delay
                    Thread.sleep(800);
                }
            }

            // Complete stream
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