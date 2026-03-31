/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.recomendation;

/**
 *
 * @author joseperez
 */

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class RecommendationServer {

    public static void main(String[] args) throws Exception {

        int port = 50053;

        Server server = ServerBuilder.forPort(port)
                .addService(new RecommendationServiceImplementation())
                .build()
                .start();

        System.out.println("Recommendation Service running on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Recommendation Server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
