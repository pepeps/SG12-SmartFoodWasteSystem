/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.inventory;

/**
 *
 * @author joseperez
 */

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class InventoryServer {

    public static void main(String[] args) throws Exception {

        int port = 50051;

        Server server = ServerBuilder.forPort(port)
                .addService(new InventoryServiceImplementation())
                .build()
                .start();

        System.out.println("Inventory Service running on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Inventory Server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
