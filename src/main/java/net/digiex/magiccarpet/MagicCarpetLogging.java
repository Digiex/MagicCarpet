package net.digiex.magiccarpet;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MagicCarpetLogging {

    private final Logger logger = Logger.getLogger("Minecraft");

    public void info(String s) {
        logger.log(Level.INFO, "[FeaturePack] " + s);
    }

    public void severe(String s) {
        logger.log(Level.SEVERE, "[FeaturePack] " + s);
    }

    public void warning(String s) {
        logger.log(Level.WARNING, "[FeaturePack] " + s);
    }
}