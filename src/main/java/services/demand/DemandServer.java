/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.demand;

/**
 *
 * @author joseperez
 */

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class DemandServer {

    public static void main(String[] args) throws Exception {

        int port = 50052;

        Server server = ServerBuilder.forPort(port)
                .addService(new DemandServiceImplementation())
                .build()
                .start();

        System.out.println("Demand Service running on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Demand Server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}