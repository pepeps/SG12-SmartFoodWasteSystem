/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.inventory;

/**
 *
 * @author joseperez
 */

import generated.sdg.inventory.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class InventoryClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        InventoryMonitoringServiceGrpc.InventoryMonitoringServiceBlockingStub stub =
                InventoryMonitoringServiceGrpc.newBlockingStub(channel);

        // UNARY
        InventoryRequest request = InventoryRequest.newBuilder()
                .setStoreId("STORE-1")
                .build();

        InventoryStatus response = stub.getInventoryStatus(request);

        System.out.println("Inventory:");
        response.getItemsList().forEach(item ->
                System.out.println(item.getSku() + " -> " + item.getQuantityUnits())
        );

        // STREAMING
        System.out.println("\nExpiry Alerts:");

        ExpiryAlertRequest alertRequest = ExpiryAlertRequest.newBuilder()
                .setHorizonDays(2)
                .build();

        stub.streamExpiryAlerts(alertRequest)
                .forEachRemaining(alert ->
                        System.out.println(alert.getSku() + " expires in " + alert.getDaysToExpiry() + " days")
                );

        channel.shutdown();
    }
}
