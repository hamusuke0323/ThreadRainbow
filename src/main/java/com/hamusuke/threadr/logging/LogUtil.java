package com.hamusuke.threadr.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class LogUtil {
    public static void setLogLevel(Level level) {
        var ctx = (LoggerContext) LogManager.getContext(false);
        var config = ctx.getConfiguration();
        var loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }
}
