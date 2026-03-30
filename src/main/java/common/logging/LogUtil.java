/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joseperez
 */


package common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    // Logger class to add logs along the app.
    public static final Logger logger = LoggerFactory.getLogger("SDG12-System");


    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }
}