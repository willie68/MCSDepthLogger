/**
 * MCS Media Computer Software
 * Copyright 2016 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: AdaptTime.java
 * EMail: W.Klaas@gmx.de
 * Created: 26.04.2016 wklaa_000
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.mcs.depth.StatusAndProgress;
import de.mcs.depth.logger.DefaultSentenceFilter;
import de.mcs.depth.logger.LoggerFile;
import de.mcs.depth.logger.ProgressCallback;
import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.nmea.NMEARMCSentence;
import de.mcs.utils.caches.FileCache;

/**
 * @author wklaa_000
 *
 */
public class AdaptTimeTool {

  private static Date nullDate = new GregorianCalendar(2000, 1, 2, 0, 0, 0).getTime();
  private Logger logger = Logger.getLogger(this.getClass());
  private StatusAndProgress sap;
  private boolean processAllFiles;
  private List<File> dataFiles;
  private FileCache fileCache;

  public static AdaptTimeTool newAdaptTimeTool() {
    return new AdaptTimeTool();
  }

  public AdaptTimeTool() {
    this.processAllFiles = false;
  }

  public AdaptTimeTool setStatusAndProgress(StatusAndProgress sap) {
    this.sap = sap;
    return this;
  }

  public AdaptTimeTool setProcessAllFiles(boolean allFiles) {
    this.processAllFiles = allFiles;
    return this;
  }

  public AdaptTimeTool setDataFiles(List<File> dataFiles) {
    this.dataFiles = dataFiles;
    return this;
  }

  public void doWork() {
    if ((dataFiles != null) && (dataFiles.size() > 0)) {
      // Bestimmen der Dateien mit falschen Datum/Uhrzeit
      int i = 0;
      for (File file : dataFiles) {
        Thread.yield();
        i++;
        int pos = (int) Math.round(i / ((double) dataFiles.size()) * 100.0);
        sap.setProgress(pos);
        try {
          if (mustBeProcessed(file) || processAllFiles) {
            fileCache.addFile(file);
            logger.info(String.format("processing file: %s", file.getName()));
            LoggerFile loggerFile = new LoggerFile(fileCache.getFile(file));
            loggerFile.setSentenceFilter(DefaultSentenceFilter.RMC_SENTENCEFILTER);
            // Dateien einlesen und aus der letzten GPS Zeit die Zeit berechnen
            loggerFile.setProgressCallback(new ProgressCallback() {
              int oldPos = 0;

              @Override
              public void progress(long position, long max, String message) {
                int pos = (int) Math.round(position / ((double) max) * 100.0);
                if (pos != oldPos) {
                  String name = file.getName();
                  name = name.substring(0, name.lastIndexOf("."));
                  sap.setProgressLabel(String.format("%s: %d%%", name, pos), message);
                  if (StringUtils.isNotEmpty(message)) {
                    sap.setStatusbar(message);
                  }
                  Thread.yield();
                  oldPos = pos;
                }
              }
            });
            loggerFile.parseFile();
            List<Datagram> datagrams = loggerFile.getDatagrams();
            if (datagrams.size() > 0) {
              Datagram datagram = datagrams.get(datagrams.size() - 1);
              if (datagram instanceof NMEARMCSentence) {
                NMEARMCSentence rmcDatagram = (NMEARMCSentence) datagram;
                Date lastModified = rmcDatagram.getDateTime();
                file.setLastModified(lastModified.getTime());
                logger.info(
                    String.format("setting file \"%s\" to timestamp \"%s\"", file.getName(), lastModified.toString()));
              }
            }
          }
        } catch (IOException e) {
          logger.error(e);
        }
      }
    }
  }

  public boolean mustBeProcessed(File file) {
    Date lastModified = new Date(file.lastModified());
    return nullDate.after(lastModified);
  }

  public AdaptTimeTool setFileCache(FileCache fileCache) {
    this.fileCache = fileCache;
    return this;
  }
}
