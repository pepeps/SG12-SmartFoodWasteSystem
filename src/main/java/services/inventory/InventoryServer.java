/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.inventory;

/**
 *
 * @author joseperez
 */

import common.jmdns.JmDNSServiceRegister;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class InventoryServer {

    public static void main(String[] args) {

        int port = 50051;

        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(new InventoryServiceImplementation())
                    .build()
                    .start();

            System.out.println("***** Inventory Server started on port " + port);

           
            JmDNSServiceRegister register = JmDNSServiceRegister.getInstance();
            register.registerService(
                    "_inventory._tcp.local.",   // TYPE
                    "InventoryService",         // NAME
                    port
            );

            server.awaitTermination();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
