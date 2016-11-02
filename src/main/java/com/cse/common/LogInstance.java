package com.cse.common;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by bullet on 16. 10. 25.
 */
public class LogInstance {
    private static final String PRODUCTION_LOG = "PRODUCTION_LOGGER";
    private static final String DEBUG_LOG = "DEBUG_LOOGER";
    public static final boolean isDebug = true;

    public static Logger getLogger(){
        if(isDebug)
            return LogManager.getLogger(DEBUG_LOG);
        else
            return LogManager.getLogger(PRODUCTION_LOG);
    }
}
