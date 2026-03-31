/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.demand;

/**
 *
 * @author joseperez
 */

import generated.sdg.demand.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class DemandClient {

    public static void main(String[] args) throws InterruptedException {

       
        // 1. CREATE gRPC CHANNEL
     
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

      
        // Client streaming requires an asynchronous stub
        DemandPredictionServiceGrpc.DemandPredictionServiceStub stub =
                DemandPredictionServiceGrpc.newStub(channel);

        System.out.println("\n=================================");
        System.out.println("CLIENT STREAMING RPC → uploadSalesData");
        System.out.println("Multiple requests → One response");
        System.out.println("=================================");

      
        // This handles the server's response (only once)
        StreamObserver<UploadSummary> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(UploadSummary summary) {
                System.out.println("\n=== SERVER RESPONSE ===");
                System.out.println("Accepted: " + summary.getAccepted());
                System.out.println("Rejected: " + summary.getRejected());
                System.out.println("Message: " + summary.getMessage());
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


    
        // This is used to send data to the server
        StreamObserver<SalesRecord> requestObserver =
                stub.uploadSalesData(responseObserver);

      
    // Simulate sending multiple messages
       
        for (int i = 0; i < 5; i++) {

            SalesRecord record = SalesRecord.newBuilder()
                    .setStoreId("STORE-1")
                    .setSku("MILK-1L")
                    .setUnitsSold(5 + i)
                    .setSoldAtEpochMs(System.currentTimeMillis())
                    .build();

            System.out.println("Sending record " + i);

            // Send each message to the server
            requestObserver.onNext(record);

            // Simulate delay between messages
            Thread.sleep(500);
        }
    
        // 6. COMPLETE STREAM
        requestObserver.onCompleted();

        // 7. WAIT FOR RESPONSE
        Thread.sleep(2000);

        // 8. SHUTDOWN CHANNEL
        channel.shutdown();
    }
}