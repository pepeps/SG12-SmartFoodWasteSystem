/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.demand;

/**
 *
 * @author joseperez
 */

import common.jmdns.JmDNSServiceDiscovery;
import generated.sdg.demand.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.jmdns.ServiceInfo;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DemandClient {

    public static void main(String[] args) throws Exception {
        int port;
        

// 1. use discovery object to find Demand service
        JmDNSServiceDiscovery discovery = new JmDNSServiceDiscovery();

        ServiceInfo serviceInfo =
                discovery.discoverService(
                        "_demand._tcp.local.",
                        "DemandService",
                        5000
                );

       if (serviceInfo == null) {
            System.out.println("⚠ JmDNS failed → fallback to localhost:50051");
            port = 50052;
        } else {
            port = serviceInfo.getPort();
            System.out.println("✅ Service found via JmDNS at port: " + port);
        }

       
        System.out.println("✅ Demand Service found on port: " + port);

        // =========================
        // 2. CHANNEL
        // =========================
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", port)
                .usePlaintext()
                .build();

        // =========================
        // 3. CLIENT STREAMING
        // =========================
        System.out.println("\n=== CLIENT STREAMING: uploadSalesData ===");

        DemandPredictionServiceGrpc.DemandPredictionServiceStub asyncStub =
                DemandPredictionServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<UploadSummary> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(UploadSummary summary) {
                System.out.println("[SUMMARY] " + summary.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("❌ Error: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("✅ Upload completed");
                latch.countDown();
            }
        };

        StreamObserver<SalesRecord> requestObserver =
                asyncStub.uploadSalesData(responseObserver);

        for (int i = 1; i <= 3; i++) {

            SalesRecord record = SalesRecord.newBuilder()
                    .setStoreId("STORE-1")
                    .setSku("ITEM-" + i)
                    .setUnitsSold(5)
                    .setSoldAtEpochMs(System.currentTimeMillis())
                    .build();

            System.out.println("[SEND] " + record.getSku());
            requestObserver.onNext(record);

            Thread.sleep(300);
        }

        requestObserver.onCompleted();
        latch.await(5, TimeUnit.SECONDS);

        // =========================
        // 4. UNARY
        // =========================
        System.out.println("\n=== UNARY: getDemandForecast ===");

        DemandPredictionServiceGrpc.DemandPredictionServiceBlockingStub blockingStub =
                DemandPredictionServiceGrpc.newBlockingStub(channel);

        ForecastResponse forecast =
                blockingStub.getDemandForecast(
                        ForecastRequest.newBuilder()
                                .setStoreId("STORE-1")
                                .setSku("ITEM-1")
                                .setForecastDays(3)
                                .build()
                );

        System.out.println("[FORECAST] SKU: " + forecast.getSku());

        forecast.getDaysList().forEach(day ->
                System.out.println("Day " + day.getDayOffset()
                        + " → " + day.getPredictedUnits())
        );

        // =========================
        // 5. CLEANUP
        // =========================
        channel.shutdown();
        discovery.close();
    }
}