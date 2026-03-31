/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.recomendation;

/**
 *
 * @author joseperez
 */

import common.logging.LogUtil;
import generated.sdg.recommendation.*;
import io.grpc.stub.StreamObserver;

public class RecommendationServiceImplementation extends WasteReductionRecommendationServiceGrpc.WasteReductionRecommendationServiceImplBase {

    // BIDIRECTIONAL STREAMING
    @Override
    public StreamObserver<OptimisationInput> liveOptimisation(StreamObserver<Recommendation> responseObserver) {

        LogUtil.info("RPC TYPE: BIDIRECTIONAL STREAMING → liveOptimisation started");

        // This observer receives messages from the client
        return new StreamObserver<>() {

            @Override
            public void onNext(OptimisationInput input) {

                // Called every time the client sends data
                LogUtil.info("Received optimisation input for store: " + input.getStoreId());

                // Simple logic: detect low stock, create a new message
                Recommendation recommendation = Recommendation.newBuilder()
                        .setSessionId(input.getSessionId())
                        .setStoreId(input.getStoreId())
                        .setSeverity(Severity.WARNING)
                        .setMessage("Low stock detected for some items")
                        .addSuggestedActions("Apply discount")
                        .addSuggestedActions("Promote bundle offer")
                        .setTimestampEpochMs(System.currentTimeMillis())
                        .build();

                // Send response immediately (real-time)
                responseObserver.onNext(recommendation);
            }

            @Override
            public void onError(Throwable t) {
                LogUtil.error("Error in bidirectional stream", t);
            }

            @Override
            public void onCompleted() {

                LogUtil.info("Client completed bidirectional stream");

                // Close response stream
                responseObserver.onCompleted();
            }
        };
    }

    // UNARY RPC (REPORT)
    @Override
    public void generateWasteReport(ReportRequest request,
                                   StreamObserver<ReportResponse> responseObserver) {

        LogUtil.info("RPC TYPE: UNARY → generateWasteReport called");

        ReportResponse response = ReportResponse.newBuilder()
                .setStoreId(request.getStoreId())
                .setSummary("Waste reduced by 20% this period")
                .setNearExpiryUnits(30)
                .setEstimatedWasteAvoidedUnits(15)
                .setGeneratedAtEpochMs(System.currentTimeMillis())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
