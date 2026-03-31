/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.recomendation;

/**
 *
 * @author joseperez
 */

import generated.sdg.recommendation.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class RecommendationClient {

    public static void main(String[] args) throws InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50053)
                .usePlaintext()
                .build();

        WasteReductionRecommendationServiceGrpc.WasteReductionRecommendationServiceStub stub =
                WasteReductionRecommendationServiceGrpc.newStub(channel);

        System.out.println("\n=================================");
        System.out.println("BIDIRECTIONAL STREAMING RPC → liveOptimisation");
        System.out.println("Client ⇄ Server (real-time communication)");
        System.out.println("=================================");

        // RESPONSE OBSERVER (SERVER → CLIENT)
        StreamObserver<Recommendation> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(Recommendation rec) {

                System.out.println("[SERVER RESPONSE]");
                System.out.println("Message: " + rec.getMessage());
                System.out.println("Severity: " + rec.getSeverity());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed");
            }
        };

        // REQUEST OBSERVER (CLIENT → SERVER)
        StreamObserver<OptimisationInput> requestObserver =
                stub.liveOptimisation(responseObserver);

        // SEND MULTIPLE MESSAGES
        for (int i = 0; i < 3; i++) {

            OptimisationInput input = OptimisationInput.newBuilder()
                    .setSessionId("SESSION-1")
                    .setStoreId("STORE-1")
                    .setTimestampEpochMs(System.currentTimeMillis())
                    .build();

            System.out.println("Sending optimisation input " + i);

            requestObserver.onNext(input);

            Thread.sleep(1000);
        }

        // Finish stream
        requestObserver.onCompleted();

        Thread.sleep(2000);

        channel.shutdown();
    }
}
