/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: ShowLoggerFile.java
 * EMail: W.Klaas@gmx.de
 * Created: 12.12.2013 Willie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package de.mcs.depth.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.DefaultDatagram.CHANNEL;
import de.mcs.utils.Files;

/**
 * @author Willie
 * 
 */
public class Convert2NMEAFile {

  public static void main(String[] args) {
    if (args.length > 0) {
      File logFile = new File(args[0]);

      if (logFile.exists()) {
        final File oldFile = new File(logFile.getParentFile(), logFile.getName() + ".old");
        if (oldFile.exists()) {
          System.out.println("file with extension \".old\" already exists.");
          showHelp();
          System.exit(1);
        }

        List<String> errorList = new ArrayList<>();
        try {
          System.out.println(String.format("starting File: %s.", logFile.getName()));
          LoggerFile loggerFile = new LoggerFile(logFile).setProgressCallback(new ProgressCallback() {
            int pos = 0;

            @Override
            public void progress(long position, long max, String message) {
              final int newpos = (int) (position * 50 / max);
              if (newpos != pos) {
                pos = newpos;
                System.out.print('.');
                Thread.yield();
              }
            }
          }).parseFile();

          errorList = loggerFile.getErrorList();
          System.out.println();

          int errors = errorList.size();
          long count = loggerFile.getCount();
          System.out.println(String.format("File: %s, found %4d errors (%2.1f%%) in %4d datagramms.", logFile.getName(),
              errors, errors * 100.0 / count, count));
          if (errors > 0) {
            for (String string : errorList) {
              System.out.println(string);
            }
          }
          System.out.println(String.format("Channelstats: Channel A: %d, Channel B: %d, Channel I: %d, no Channel: %d ",
              loggerFile.getChannelDataCount(CHANNEL.CHANNEL_A), loggerFile.getChannelDataCount(CHANNEL.CHANNEL_B),
              loggerFile.getChannelDataCount(CHANNEL.CHANNEL_I), loggerFile.getChannelDataCount(CHANNEL.NONE)));

          Files.move(logFile, oldFile);
          System.out.println("writing new file:" + logFile.getName());

          List<Datagram> datagrams = loggerFile.getDatagrams();
          Writer out = new BufferedWriter(new FileWriter(logFile));
          for (Datagram datagram : datagrams) {
            out.write(datagram.getRawData());
            out.write("\r\n");
          }
          out.close();
          System.out.println();
          System.out.println("ready.");
        } catch (IOException e) {
          e.printStackTrace();
          showHelp();
          System.exit(1);
        }
      } else {
        System.out.println(String.format("file \"%s\" not found.", logFile.toString()));
        showHelp();
        System.exit(1);
      }
    } else {
      showHelp();
      System.exit(1);
    }
  }

  private static void showHelp() {
    System.out.println("usage Convert2NMEAFile <file to process>");
    System.out.println("processing the given logger file. The loggerfile will renamed to <name>.old.");
    System.out.println("The result of the processing will be written to the original file. ");
  }
}
