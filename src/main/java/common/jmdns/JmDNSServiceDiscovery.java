/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author joseperez
 */
package common.jmdns;

import javax.jmdns.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class JmDNSServiceDiscovery {

    private JmDNS jmdns;

    public ServiceInfo discoverService(String type, String name, long timeout) {

        final ServiceInfo[] found = {null};
        CountDownLatch latch = new CountDownLatch(1);

        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());

            System.out.println("Client running on: " + InetAddress.getLocalHost());

            jmdns.addServiceListener(type, new ServiceListener() {

                @Override
                public void serviceAdded(ServiceEvent event) {
                    System.out.println("Service added: " + event.getName());
                    jmdns.requestServiceInfo(event.getType(), event.getName(), 2000);
                }

                @Override
                public void serviceRemoved(ServiceEvent event) {}

                @Override
                public void serviceResolved(ServiceEvent event) {
                    System.out.println("Service resolved: " + event.getInfo());

                    if (event.getName().equals(name)) {
                        found[0] = event.getInfo();
                        latch.countDown();
                    }
                }
            });

            latch.await(timeout, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return found[0];
    }

    public void close() {
        try {
            if (jmdns != null) {
                jmdns.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}