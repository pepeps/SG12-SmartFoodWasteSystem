/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joseperez
 */
package common.logging;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    private static JTextArea globalLogArea;

    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("HH:mm:ss");

    public static void setLogArea(JTextArea area) {
        globalLogArea = area;
    }

    public static void info(String message) {
        log("INFO", message, null);
    }

    public static void warn(String message) {
        log("WARN", message, null);
    }

    public static void error(String message, Throwable t) {
        log("ERROR", message, t);
    }

    private static void log(String level, String message, Throwable t) {
        String timestamp = sdf.format(new Date());
        String formatted = String.format("[%s] [%s] %s", timestamp, level, message);

        if ("ERROR".equals(level)) {
            System.err.println(formatted);
        } else {
            System.out.println(formatted);
        }

        JTextArea localArea = globalLogArea;
        if (localArea != null) {
            SwingUtilities.invokeLater(() -> {
                localArea.append(formatted + "\n");
                localArea.setCaretPosition(localArea.getDocument().getLength());
            });
        }

        if (t != null) {
            t.printStackTrace();
            if (localArea != null) {
                SwingUtilities.invokeLater(() -> {
                    localArea.append("Exception: " + t.getMessage() + "\n");
                    localArea.setCaretPosition(localArea.getDocument().getLength());
                });
            }
        }
    }
}