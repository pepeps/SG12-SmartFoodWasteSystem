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
import io.grpc.stub.StreamObserver;

public class DemandServiceImplementation extends DemandPredictionServiceGrpc.DemandPredictionServiceImplBase {

    // =========================
    // CLIENT STREAMING
    // =========================
    @Override
    public StreamObserver<SalesRecord> uploadSalesData(StreamObserver<UploadSummary> responseObserver) {

        return new StreamObserver<>() {

            int count = 0;

            @Override
            public void onNext(SalesRecord record) {
                System.out.println("[CLIENT STREAM] Received sale: " + record.getSku());
                count++;
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error receiving sales data");
            }

            @Override
            public void onCompleted() {
                UploadSummary summary = UploadSummary.newBuilder()
                        .setAccepted(count)
                        .setRejected(0)
                        .setMessage("Processed " + count + " records")
                        .build();

                responseObserver.onNext(summary);
                responseObserver.onCompleted();
            }
        };
    }

    // =========================
    // UNARY
    // =========================
    @Override
    public void getDemandForecast(ForecastRequest request,
                                 StreamObserver<ForecastResponse> responseObserver) {

        System.out.println("[UNARY] Forecast request for " + request.getSku());

        ForecastResponse response = ForecastResponse.newBuilder()
                .setSku(request.getSku())
                .setConfidence(0.85)
                .addDays(DailyForecast.newBuilder().setDayOffset(1).setPredictedUnits(10))
                .addDays(DailyForecast.newBuilder().setDayOffset(2).setPredictedUnits(15))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}