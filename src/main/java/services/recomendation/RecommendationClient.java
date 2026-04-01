/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.recomendation;

/**
 *
 * @author joseperez
 */

import common.jmdns.JmDNSServiceDiscovery;
import generated.sdg.recommendation.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.jmdns.ServiceInfo;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RecommendationClient {

    public static void main(String[] args) throws Exception {
        int port;
        // =========================
        // 1. DISCOVERY (YOUR CLASS)
        // =========================
        JmDNSServiceDiscovery discovery = new JmDNSServiceDiscovery();

        ServiceInfo serviceInfo =
                discovery.discoverService(
                        "_recommendation._tcp.local.",
                        "RecommendationService",
                        5000
                );

        if (serviceInfo == null) {
            System.out.println("⚠ JmDNS failed → fallback to localhost:50051");
            port = 50053;
        } else {
            port = serviceInfo.getPort();
            System.out.println("✅ Service found via JmDNS at port: " + port);
        }

        // IMPORTANT → close JmDNS after discovery
        System.out.println("✅ Recommendation Service found on port: " + port);

        // =========================
        // 2. CHANNEL
        // =========================
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", port)
                .usePlaintext()
                .build();

        // =========================
        // 3. BIDI STREAMING
        // =========================
        System.out.println("\n=== BIDI STREAM: liveOptimisation ===");

        WasteReductionRecommendationServiceGrpc.WasteReductionRecommendationServiceStub asyncStub =
                WasteReductionRecommendationServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Recommendation> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(Recommendation rec) {
                System.out.println("[RECOMMENDATION] " + rec.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("❌ Error: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("✅ Stream completed");
                latch.countDown();
            }
        };

        StreamObserver<OptimisationInput> requestObserver =
                asyncStub.liveOptimisation(responseObserver);

        for (int i = 1; i <= 3; i++) {

            OptimisationInput input = OptimisationInput.newBuilder()
                    .setSessionId("SESSION-1")
                    .setStoreId("STORE-1")
                    .setTimestampEpochMs(System.currentTimeMillis())
                    .build();

            System.out.println("[SEND] Optimisation batch " + i);
            requestObserver.onNext(input);

            Thread.sleep(500);
        }

        requestObserver.onCompleted();
        latch.await(5, TimeUnit.SECONDS);

        // =========================
        // 4. UNARY
        // =========================
        System.out.println("\n=== UNARY: generateWasteReport ===");

        WasteReductionRecommendationServiceGrpc.WasteReductionRecommendationServiceBlockingStub blockingStub =
                WasteReductionRecommendationServiceGrpc.newBlockingStub(channel);

        ReportResponse report =
                blockingStub.generateWasteReport(
                        ReportRequest.newBuilder()
                                .setStoreId("STORE-1")
                                .setLastNDays(7)
                                .build()
                );

        System.out.println("[REPORT] " + report.getSummary());

        // =========================
        // 5. CLEANUP
        // =========================
        channel.shutdown();
        discovery.close();
    }
}