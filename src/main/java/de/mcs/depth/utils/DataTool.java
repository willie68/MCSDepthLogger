/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: DataTool.java
 * EMail: W.Klaas@gmx.de
 * Created: 29.04.2014 wklaa_000
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
package de.mcs.depth.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import de.mcs.depth.Constants.EXPORT_TYPES;
import de.mcs.depth.StatusAndProgress;
import de.mcs.depth.logger.LoggerFile;
import de.mcs.depth.logger.ProgressCallback;
import de.mcs.utils.Files;

/**
 * @author wklaa_000
 * 
 */
public class DataTool {

  private Logger logger = Logger.getLogger(DataTool.class);

  private static final String NLS_PREFIX = "main.gui.";

  private StatusAndProgress sap = null;

  private static DataTool instance = null;

  public static DataTool getInstance() {
    // TODO Auto-generated method stub
    return instance;
  }

  public DataTool(StatusAndProgress sap) {
    this.sap = sap;
    instance = this;
  }

  public void saveDataFile(File file, File oldFile) {
    sap.setProgress(0);
    sap.setStatusbar(NLS_PREFIX + "statusbar.analyse", NLS_PREFIX + "statusbar.analyse");
    sap.setWait(true);

    try {
      LoggerFile loggerFile = new LoggerFile(file).setProgressCallback(new ProgressCallback() {
        int pos = 0;

        @Override
        public void progress(long position, long max, String message) {
          final int newpos = (int) (position * 100 / max);
          if (newpos != pos) {
            sap.setProgress(newpos);
            pos = newpos;
            Thread.yield();
          }
        }
      }).parseFile();
      try {

        LoggingTool.showParseResults(logger, file, loggerFile);

        sap.setStatusbar(NLS_PREFIX + "statusbar.writing");
        sap.setProgress(100);

        Files.move(file, oldFile);
        logger.info("writing new file:" + file.getName());
        loggerFile.writeLoggerFile(file);

        sap.setStatusbar(NLS_PREFIX + "statusbar.ready");
        sap.setProgress(0);

        Thread.yield();
      } catch (IOException e) {
        logger.error(e);
      }

    } catch (IOException e) {
      logger.error(e);
    } finally {
      sap.setWait(false);
    }
  }

  public void exportNMEAData(Track track, File nmeaFile, EXPORT_TYPES exptype) {
    sap.setProgress(0);
    sap.setStatusbar(NLS_PREFIX + "statusbar.analyse", NLS_PREFIX + "statusbar.analyse");
    sap.setWait(true);

    try {
      if (exptype.equals(EXPORT_TYPES.NMEA)) {
        File mapFile = track.getMapFile();
        // wenn nicht vorhanden generieren
        if (mapFile != null && mapFile.exists()) {
          Files.fileCopy(mapFile, nmeaFile);
        }
      } else {
        List<File> dataFiles = track.getDataFiles();
        PrintWriter out = new PrintWriter(new FileWriter(nmeaFile));
        try {
          out.println("#Track: " + track.getName());
          out.println("#Comment: " + track.getComment());
          for (File file : dataFiles) {
            out.println("#File: " + file.getName());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
              out.println(readLine);
            }
            reader.close();
          }
        } finally {
          out.close();
        }
      }
      track.freeResources();
    } catch (ConfigurationException | IOException e) {
      logger.error(e);
    } finally {
      sap.setWait(false);
    }

  }
}
