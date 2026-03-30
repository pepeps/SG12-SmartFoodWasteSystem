/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.demand;

/**
 *
 * @author joseperez
 */

import common.logging.LogUtil;
import generated.sdg.demand.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class DemandServiceImplementation extends DemandPredictionServiceGrpc.DemandPredictionServiceImplBase {

  
    // CLIENT STREAMING RPC
    @Override
    public StreamObserver<SalesRecord> uploadSalesData(StreamObserver<UploadSummary> responseObserver) {

        // Log the start of a client streaming RPC
        LogUtil.info("RPC TYPE: CLIENT STREAMING → uploadSalesData started");

        // Return a StreamObserver to receive data from the client
        return new StreamObserver<SalesRecord>() {

            // Tracking messages in memory
            List<SalesRecord> records = new ArrayList<>();

            int accepted = 0;
            int rejected = 0;

            @Override
            public void onNext(SalesRecord record) {

                // This method is called every time the client sends a message

                if (record.getSku().isEmpty()) {
                    // Reject invalid records
                    rejected++;
                    LogUtil.warn("Rejected record: missing SKU");
                    return;
                }

                // Accept valid record
                records.add(record);
                accepted++;

                LogUtil.info("Received sales record → SKU: " + record.getSku());
            }

            @Override
            public void onError(Throwable t) {
                // Called if client terminates with an error
                LogUtil.error("Error in client streaming", t);
            }

            @Override
            public void onCompleted() {

                // Called when client finishes sending all messages
                LogUtil.info("Client finished sending data");

                // Build response summary
                UploadSummary summary = UploadSummary.newBuilder()
                        .setAccepted(accepted)
                        .setRejected(rejected)
                        .setProcessedAtEpochMs(System.currentTimeMillis())
                        .setMessage("Processed " + accepted + " records")
                        .build();

                // Send ONE response back to the client
                responseObserver.onNext(summary);

                // Close the response stream
                responseObserver.onCompleted();
            }
        };
    }

    // UNARY RPC
    @Override
    public void getDemandForecast(ForecastRequest request,
                                 StreamObserver<ForecastResponse> responseObserver) {

        // Log unary call
        LogUtil.info("RPC TYPE: UNARY → getDemandForecast called");

        try {

            // Simulated forecast logic
            List<DailyForecast> forecastList = new ArrayList<>();

            for (int i = 1; i <= request.getForecastDays(); i++) {

                forecastList.add(DailyForecast.newBuilder()
                        .setDayOffset(i)
                        .setPredictedUnits(10 + i * 2) // simple prediction model
                        .build());
            }

            ForecastResponse response = ForecastResponse.newBuilder()
                    .setStoreId(request.getStoreId())
                    .setSku(request.getSku())
                    .addAllDays(forecastList)
                    .setConfidence(0.85)
                    .setGeneratedAtEpochMs(System.currentTimeMillis())
                    .build();

            // Send response
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            LogUtil.error("Error generating forecast", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Forecast error")
                            .asRuntimeException()
            );
        }
    }
}