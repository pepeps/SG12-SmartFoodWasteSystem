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

public class RecommendationServiceImplementation
        extends WasteReductionRecommendationServiceGrpc.WasteReductionRecommendationServiceImplBase {

    // =========================
    // BIDIRECTIONAL STREAMING
    // =========================
    @Override
    public StreamObserver<OptimisationInput> liveOptimisation(
            StreamObserver<Recommendation> responseObserver) {

        LogUtil.info("RPC TYPE: BIDIRECTIONAL STREAMING → liveOptimisation started");

        return new StreamObserver<OptimisationInput>() {

            @Override
            public void onNext(OptimisationInput input) {

                LogUtil.info("Received optimisation input for store: " + input.getStoreId());

                // Simple business logic example
                // (You can later replace this with real logic)
                Recommendation recommendation = Recommendation.newBuilder()
                        .setSessionId(input.getSessionId())
                        .setStoreId(input.getStoreId())
                        .setSeverity(Severity.WARNING)   // ✅ ENUM (correct)
                        .setMessage("Low stock or expiry risk detected")
                        .addSuggestedActions("Apply discount to near-expiry items")
                        .addSuggestedActions("Create bundle promotions")
                        .setTimestampEpochMs(System.currentTimeMillis())
                        .build();

                // Real-time response (key concept of BIDI streaming)
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

    // =========================
    // UNARY RPC
    // =========================
    @Override
    public void generateWasteReport(
            ReportRequest request,
            StreamObserver<ReportResponse> responseObserver) {

        LogUtil.info("RPC TYPE: UNARY → generateWasteReport called for store: "
                + request.getStoreId());

        // Simple mock report logic
        ReportResponse response = ReportResponse.newBuilder()
                .setStoreId(request.getStoreId())
                .setSummary("Waste reduced by 20% in the last period")
                .setNearExpiryUnits(30)
                .setEstimatedWasteAvoidedUnits(15)
                .setGeneratedAtEpochMs(System.currentTimeMillis())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}