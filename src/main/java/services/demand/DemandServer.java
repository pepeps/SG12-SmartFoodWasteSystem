/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.demand;

/**
 *
 * @author joseperez
 */


import common.jmdns.JmDNSServiceRegister;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class DemandServer {

    public static void main(String[] args) {

        int port = 50052;

        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(new DemandServiceImplementation())
                    .build()
                    .start();

            System.out.println("***** Demand Server started on port " + port);

            // Register with JmDNS
            JmDNSServiceRegister register = JmDNSServiceRegister.getInstance();
            register.registerService(
                    "_demand._tcp.local.",
                    "DemandService",
                    port
            );

            server.awaitTermination();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}