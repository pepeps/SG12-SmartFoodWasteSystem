/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author joseperez
 */

import common.logging.LogUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class OrchestratorGUI extends JFrame {

    private final JTextArea globalLogArea = new JTextArea();
    private final JTextArea inventoryArea = new JTextArea();
    private final JTextArea demandArea = new JTextArea();
    private final JTextArea recommendationArea = new JTextArea();

    private final JLabel inventoryStatus = new JLabel("Stopped");
    private final JLabel demandStatus = new JLabel("Stopped");
    private final JLabel recommendationStatus = new JLabel("Stopped");

    public OrchestratorGUI() {
        setTitle("Smart Food Waste System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        LogUtil.setLogArea(globalLogArea);

        add(buildTopControls(), BorderLayout.NORTH);
        add(buildCenterPanels(), BorderLayout.CENTER);
        add(buildBottomLogPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildTopControls() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 8, 8));// create panel frame

        JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serverPanel.setBorder(new TitledBorder("Server Controls"));

        JButton startInventoryServerBtn = new JButton("Start Inventory Server");
        JButton startDemandServerBtn = new JButton("Start Demand Server");
        JButton startRecommendationServerBtn = new JButton("Start Recommendation Server");

        serverPanel.add(startInventoryServerBtn);
        serverPanel.add(inventoryStatus);

        serverPanel.add(startDemandServerBtn);
        serverPanel.add(demandStatus);

        serverPanel.add(startRecommendationServerBtn);
        serverPanel.add(recommendationStatus);

        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPanel.setBorder(new TitledBorder("Client Controls"));
        
        //Create buttons to trigger services clients
        JButton runInventoryClientBtn = new JButton("Run Inventory Client");
        JButton runDemandClientBtn = new JButton("Run Demand Client");
        JButton runRecommendationClientBtn = new JButton("Run Recommendation Client");
        JButton runFullFlowBtn = new JButton("Run Full Flow");

        clientPanel.add(runInventoryClientBtn);
        clientPanel.add(runDemandClientBtn);
        clientPanel.add(runRecommendationClientBtn);
        clientPanel.add(runFullFlowBtn);

        startInventoryServerBtn.addActionListener(e -> runAsync(() -> {
            setStatus(inventoryStatus, "Starting...");
            appendPanel(inventoryArea, "Starting Inventory Server...");
            try {
                services.inventory.InventoryServer.main(new String[]{});
                setStatus(inventoryStatus, "Running");
            } catch (Exception ex) {
                setStatus(inventoryStatus, "Error");
                appendPanel(inventoryArea, "Inventory Server failed: " + ex.getMessage());
                LogUtil.error("Inventory Server failed", ex);
            }
        }));

        startDemandServerBtn.addActionListener(e -> runAsync(() -> {
            setStatus(demandStatus, "Starting...");
            appendPanel(demandArea, "Starting Demand Server...");
            try {
                services.demand.DemandServer.main(new String[]{});
                setStatus(demandStatus, "Running");
            } catch (Exception ex) {
                setStatus(demandStatus, "Error");
                appendPanel(demandArea, "Demand Server failed: " + ex.getMessage());
                LogUtil.error("Demand Server failed", ex);
            }
        }));

        startRecommendationServerBtn.addActionListener(e -> runAsync(() -> {
            setStatus(recommendationStatus, "Starting...");
            appendPanel(recommendationArea, "Starting Recommendation Server...");
            try {
                services.recomendation.RecommendationServer.main(new String[]{});
                setStatus(recommendationStatus, "Running");
            } catch (Exception ex) {
                setStatus(recommendationStatus, "Error");
                appendPanel(recommendationArea, "Recommendation Server failed: " + ex.getMessage());
                LogUtil.error("Recommendation Server failed", ex);
            }
        }));

        runInventoryClientBtn.addActionListener(e -> runAsync(() -> {
            appendPanel(inventoryArea, "Running Inventory Client...");
            try {
                services.inventory.InventoryClient.main(new String[]{});
                appendPanel(inventoryArea, "Inventory Client finished.");
            } catch (Exception ex) {
                appendPanel(inventoryArea, "Inventory Client failed: " + ex.getMessage());
                LogUtil.error("Inventory Client failed", ex);
            }
        }));

        runDemandClientBtn.addActionListener(e -> runAsync(() -> {
            appendPanel(demandArea, "Running Demand Client...");
            try {
                services.demand.DemandClient.main(new String[]{});
                appendPanel(demandArea, "Demand Client finished.");
            } catch (Exception ex) {
                appendPanel(demandArea, "Demand Client failed: " + ex.getMessage());
                LogUtil.error("Demand Client failed", ex);
            }
        }));

        runRecommendationClientBtn.addActionListener(e -> runAsync(() -> {
            appendPanel(recommendationArea, "Running Recommendation Client...");
            try {
                services.recomendation.RecommendationClient.main(new String[]{});
                appendPanel(recommendationArea, "Recommendation Client finished.");
            } catch (Exception ex) {
                appendPanel(recommendationArea, "Recommendation Client failed: " + ex.getMessage());
                LogUtil.error("Recommendation Client failed", ex);
            }
        }));

        
        runFullFlowBtn.addActionListener(e -> {

    LogUtil.info("🚀 Starting FULL SYSTEM FLOW...");

    new Thread(() -> {
        try {
            // 1. INVENTORY
         
            LogUtil.info("▶ Starting Inventory Client...");
            services.inventory.InventoryClient.main(new String[]{});

            Thread.sleep(1500); // allow service processing

       
            // 2. DEMAND
          
            LogUtil.info("▶ Starting Demand Client...");
            services.demand.DemandClient.main(new String[]{});

            Thread.sleep(1500);

           
            // 3. RECOMMENDATION
    
            LogUtil.info("▶ Starting Recommendation Client...");
            services.recomendation.RecommendationClient.main(new String[]{});

            LogUtil.info("✅ Full system flow completed");

        } catch (Exception ex) {
            LogUtil.error("❌ Error running full flow", ex);
        }
    }).start();
});

        panel.add(serverPanel);
        panel.add(clientPanel);

        return panel;
    }

    private JPanel buildCenterPanels() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        inventoryArea.setEditable(false);
        demandArea.setEditable(false);
        recommendationArea.setEditable(false);

        centerPanel.add(wrapPanel("Inventory Output", inventoryArea));
        centerPanel.add(wrapPanel("Demand Output", demandArea));
        centerPanel.add(wrapPanel("Recommendation Output", recommendationArea));

        return centerPanel;
    }

    private JPanel buildBottomLogPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new TitledBorder("Global Logs"));

        globalLogArea.setEditable(false);
        globalLogArea.setRows(10);

        JScrollPane scrollPane = new JScrollPane(globalLogArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        return bottomPanel;
    }

    private JPanel wrapPanel(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(title));

        JScrollPane scrollPane = new JScrollPane(area);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void appendPanel(JTextArea area, String message) {
        SwingUtilities.invokeLater(() -> {
            area.append(message + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        });
    }

    private void setStatus(JLabel label, String status) {
        SwingUtilities.invokeLater(() -> label.setText(status));
    }

    private void runAsync(Task task) {
        new Thread(() -> {
            try {
                task.run();
            } catch (Exception e) {
                LogUtil.error("Task execution failed", e);
            }
        }).start();
    }

    @FunctionalInterface
    interface Task {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OrchestratorGUI::new);
    }
}