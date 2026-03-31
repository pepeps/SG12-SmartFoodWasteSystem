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

       
        // 1. CREATE gRPC CHANNEL
        
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051) // Inventory server port
                .usePlaintext() // No SSL for local development
                .build();

       
        // 2. CREATE BLOCKING STUB
      
        // Blocking stub is used for unary and server streaming
        InventoryMonitoringServiceGrpc.InventoryMonitoringServiceBlockingStub stub =
                InventoryMonitoringServiceGrpc.newBlockingStub(channel);

        
        // 3. UNARY RPC CALL
      
        System.out.println("\n=================================");
        System.out.println("UNARY RPC → getInventoryStatus");
        System.out.println("One request → One response");
        System.out.println("=================================");

        // Build request
        InventoryRequest request = InventoryRequest.newBuilder()
                .setStoreId("STORE-1")
                .build();

        // Call unary RPC
        InventoryStatus response = stub.getInventoryStatus(request);

        // Print response
        response.getItemsList().forEach(item ->
                System.out.println("[UNARY RESPONSE] " + item.getSku() +
                        " -> " + item.getQuantityUnits())
        );

        
        // 4. SERVER STREAMING RPC
        
        System.out.println("\n=================================");
        System.out.println("SERVER STREAMING RPC → streamExpiryAlerts");
        System.out.println("One request → Multiple responses");
        System.out.println("=================================");

        // Build streaming request
        ExpiryAlertRequest alertRequest = ExpiryAlertRequest.newBuilder()
                .setHorizonDays(2)
                .build();

        // Call streaming RPC
        stub.streamExpiryAlerts(alertRequest)
                .forEachRemaining(alert ->
                        System.out.println("[STREAM] " + alert.getSku()
                                + " expires in " + alert.getDaysToExpiry() + " days")
                );

        // 5. SHUTDOWN CHANNEL
        
        channel.shutdown();
    }
}