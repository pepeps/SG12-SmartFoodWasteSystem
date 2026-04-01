/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common.jmdns;

/**
 *
 * @author joseperez
 */

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

public class JmDNSServiceRegister {

    private static JmDNS jmdns;
    private static JmDNSServiceRegister instance;

    private JmDNSServiceRegister() throws IOException {
        jmdns = JmDNS.create(InetAddress.getLocalHost());
        System.out.println("Register running on: " + InetAddress.getLocalHost());
    }

    public static JmDNSServiceRegister getInstance() throws IOException {
        if (instance == null) {
            instance = new JmDNSServiceRegister();
        }
        return instance;
    }

    public void registerService(String type, String name, int port) throws IOException {

        ServiceInfo serviceInfo = ServiceInfo.create(type, name, port, "gRPC service");

        jmdns.registerService(serviceInfo);

        System.out.println("Registered Service: " + serviceInfo);
    }
}