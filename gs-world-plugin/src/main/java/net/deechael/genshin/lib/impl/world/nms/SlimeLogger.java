package net.deechael.genshin.lib.impl.world.nms;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SlimeLogger {

    private static final Logger LOGGER = Logger.getLogger("Gs World");

    public static boolean DEBUG = false;

    public static void debug(String message) {
        if (DEBUG) {
            LOGGER.log(Level.WARNING, message);
        }
    }

}
