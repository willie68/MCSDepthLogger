package de.mcs.depth.utils;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import de.mcs.depth.logger.LoggerFile;
import de.mcs.depth.logger.datagramms.DefaultDatagram.CHANNEL;

public class LoggingTool {

  public static void showParseResults(Logger logger, final File dataFile, LoggerFile loggerFile) {
    List<String> errorList = loggerFile.getErrorList();

    int errors = errorList.size();
    long count = loggerFile.getCount();
    logger.info(String.format("File: %s, found %4d errors (%2.1f%%) in %4d datagramms.", dataFile.getName(), errors,
        errors * 100.0 / count, count));
    logger.info(String.format("Channelstats: Channel A: %d, Channel B: %d, Channel I: %d, no Channel: %d ",
        loggerFile.getChannelDataCount(CHANNEL.CHANNEL_A), loggerFile.getChannelDataCount(CHANNEL.CHANNEL_B),
        loggerFile.getChannelDataCount(CHANNEL.CHANNEL_I), loggerFile.getChannelDataCount(CHANNEL.NONE)));
    if (errors > 0) {
      for (String string : errorList) {
        logger.info(String.format("errors in line %s", string));
      }
    }

  }

}
