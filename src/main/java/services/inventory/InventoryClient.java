/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.inventory;

/**
 *
 * @author joseperez
 */

import common.jmdns.JmDNSServiceDiscovery;
import generated.sdg.inventory.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.jmdns.ServiceInfo;

public class InventoryClient {

    public static void main(String[] args) {

        int port;

        // =========================
        // 1. SERVICE DISCOVERY (JmDNS)
        // =========================
        JmDNSServiceDiscovery discovery = new JmDNSServiceDiscovery();

        ServiceInfo serviceInfo = discovery.discoverService(
                "_inventory._tcp.local.",   // MUST match server
                "InventoryService",         // MUST match server
                5000                        // timeout (ms)
        );

        if (serviceInfo == null) {
            System.out.println("⚠ JmDNS failed → fallback to localhost:50051");
            port = 50051;
        } else {
            port = serviceInfo.getPort();
            System.out.println("✅ Service found via JmDNS at port: " + port);
        }

        // IMPORTANT → close JmDNS after discovery
        discovery.close();

        // =========================
        // 2. CREATE gRPC CHANNEL
        // =========================
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", port)
                .usePlaintext()
                .build();

        // =========================
        // 3. CREATE BLOCKING STUB
        // =========================
        InventoryMonitoringServiceGrpc.InventoryMonitoringServiceBlockingStub stub =
                InventoryMonitoringServiceGrpc.newBlockingStub(channel);

        // =========================
        // 4. UNARY RPC
        // =========================
        System.out.println("\n=================================");
        System.out.println("UNARY RPC → getInventoryStatus");
        System.out.println("One request → One response");
        System.out.println("=================================");

        try {
            InventoryRequest request = InventoryRequest.newBuilder()
                    .setStoreId("STORE-1")
                    .build();

            InventoryStatus response = stub.getInventoryStatus(request);

            response.getItemsList().forEach(item ->
                    System.out.println("[UNARY RESPONSE] "
                            + item.getSku() + " → "
                            + item.getQuantityUnits())
            );

        } catch (Exception e) {
            System.out.println("Error in UNARY call: " + e.getMessage());
        }

        // =========================
        // 5. SERVER STREAMING RPC
        // =========================
        System.out.println("\n=================================");
        System.out.println("SERVER STREAMING RPC → streamExpiryAlerts");
        System.out.println("One request → Multiple responses");
        System.out.println("=================================");

        try {
            ExpiryAlertRequest alertRequest = ExpiryAlertRequest.newBuilder()
                    .setHorizonDays(2)
                    .build();

            stub.streamExpiryAlerts(alertRequest)
                    .forEachRemaining(alert ->
                            System.out.println("[STREAM] "
                                    + alert.getSku()
                                    + " expires in "
                                    + alert.getDaysToExpiry() + " days")
                    );

        } catch (Exception e) {
            System.out.println("Error in STREAM call: " + e.getMessage());
        }

        // =========================
        // 6. SHUTDOWN
        // =========================
        channel.shutdown();

        System.out.println("\nClient finished execution.");
    }
}