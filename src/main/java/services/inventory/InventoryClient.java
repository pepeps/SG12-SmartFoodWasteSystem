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

        // =========================
        // UNARY
        // =========================
        System.out.println("\n=================================");
        System.out.println("UNARY RPC → getInventoryStatus");
        System.out.println("One request → One response");
        System.out.println("=================================");

        InventoryRequest request = InventoryRequest.newBuilder()
                .setStoreId("STORE-1")
                .build();

        InventoryStatus response = stub.getInventoryStatus(request);

        response.getItemsList().forEach(item ->
                System.out.println("[UNARY RESPONSE] " + item.getSku() + " -> " + item.getQuantityUnits())
        );

        // =========================
        // SERVER STREAMING
        // =========================
        System.out.println("\n=================================");
        System.out.println("SERVER STREAMING RPC → streamExpiryAlerts");
        System.out.println("One request → Multiple responses");
        System.out.println("=================================");

        ExpiryAlertRequest alertRequest = ExpiryAlertRequest.newBuilder()
                .setHorizonDays(2)
                .build();

        stub.streamExpiryAlerts(alertRequest)
                .forEachRemaining(alert ->
                        System.out.println("[STREAM] " + alert.getSku()
                                + " expires in " + alert.getDaysToExpiry() + " days")
                );

        channel.shutdown();
    }
}