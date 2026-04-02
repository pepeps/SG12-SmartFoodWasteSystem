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
import common.logging.LogUtil;
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

        // 1. DISCOVERY
        JmDNSServiceDiscovery discovery = new JmDNSServiceDiscovery();

        ServiceInfo serviceInfo =
                discovery.discoverService(
                        "_demand._tcp.local.",
                        "DemandService",
                        5000
                );

        if (serviceInfo == null) {
            String msg = "⚠ JmDNS failed → fallback to localhost:50052";
            System.out.println(msg);
            port = 50052;
        } else {
            port = serviceInfo.getPort();
            String msg = "✅ Service found via JmDNS at port: " + port;
            System.out.println(msg);
            LogUtil.info(msg);
        }

        String msgFound = "✅ Demand Service found on port: " + port;
        System.out.println(msgFound);
        LogUtil.info(msgFound);

        // 2. CHANNEL
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", port)
                .usePlaintext()
                .build();

        // 3. CLIENT STREAMING
        
        String streamingMsg = "\n=== CLIENT STREAMING: uploadSalesData ===";
        System.out.println(streamingMsg);
        LogUtil.info(streamingMsg);

        DemandPredictionServiceGrpc.DemandPredictionServiceStub asyncStub =
                DemandPredictionServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<UploadSummary> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(UploadSummary summary) {
                String msg = "[SUMMARY] " + summary.getMessage();
                System.out.println(msg);
                LogUtil.info(msg);
            }

            @Override
            public void onError(Throwable t) {
                String msg = "❌ Error: " + t.getMessage();
                System.out.println(msg);
                LogUtil.error(msg, t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                String msg = "✅ Upload completed";
                System.out.println(msg);
                LogUtil.info(msg);
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

            String sendMsg = "[SEND] " + record.getSku();
            System.out.println(sendMsg);
            LogUtil.info(sendMsg);

            requestObserver.onNext(record);

            Thread.sleep(300);
        }

        String closeMsg = "📤 Closing stream...";
        System.out.println(closeMsg);
        LogUtil.info(closeMsg);

        requestObserver.onCompleted();
        latch.await(5, TimeUnit.SECONDS);

        // 4. UNARY
        String unaryMsg = "\n=== UNARY: getDemandForecast ===";
        System.out.println(unaryMsg);
        LogUtil.info(unaryMsg);

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

        String forecastMsg = "[FORECAST] SKU: " + forecast.getSku();
        System.out.println(forecastMsg);
        LogUtil.info(forecastMsg);

        forecast.getDaysList().forEach(day -> {
            String dayMsg = "Day " + day.getDayOffset()
                    + " → " + day.getPredictedUnits();

            System.out.println(dayMsg);
            LogUtil.info(dayMsg);
        });

        
        // 5. CLEANUP
       
        channel.shutdown();
        discovery.close();

        String doneMsg = "✅ Demand client finished";
        System.out.println(doneMsg);
        LogUtil.info(doneMsg);
    }
}    