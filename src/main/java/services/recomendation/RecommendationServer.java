/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.recomendation;

/**
 *
 * @author joseperez
 */

import common.jmdns.JmDNSServiceRegister;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class RecommendationServer {

    public static void main(String[] args) {

        int port = 50053;

        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(new RecommendationServiceImplementation())
                    .build()
                    .start();

            System.out.println("***** Recommendation Server started on port " + port);

            // 🔥 DISCOVERY REGISTRATION
            JmDNSServiceRegister register = JmDNSServiceRegister.getInstance();
            register.registerService(
                    "_recommendation._tcp.local.",
                    "RecommendationService",
                    port
            );

            System.out.println("RecommendationService registered via JmDNS");

            server.awaitTermination();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
