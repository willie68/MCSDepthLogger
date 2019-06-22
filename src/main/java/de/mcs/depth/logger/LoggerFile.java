/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: LoggerFile.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.DatagramFactory;
import de.mcs.depth.logger.datagramms.DefaultDatagram.CHANNEL;
import de.mcs.depth.logger.datagramms.NMEADatagram;
import de.mcs.utils.caches.FileCache;

public class LoggerFile {

  // private Logger logger = Logger.getLogger(LoggerFile.class);
  private File loggerFile;

  private List<Datagram> list = null;

  private ProgressCallback progressCallback;

  private Comparator<Datagram> comparator;

  private int channelADataCount;

  private int channelBDataCount;

  private int channelIDataCount;

  private int channelNDataCount;

  private SentenceFilter sentenceFilter;

  private List<String> errorList;

  private FileCache fileCache;

  public LoggerFile(File loggerFile) throws IOException {
    this.loggerFile = loggerFile;
    list = new ArrayList<>();
  }

  public LoggerFile parseFile() throws IOException {
    try {
      long pos = 0;
      int lineNumber = 0;
      setErrorList(new ArrayList<>());

      FileReader fileReader = new FileReader(loggerFile);
      long length = loggerFile.length();
      BufferedReader reader = new BufferedReader(fileReader);
      String readLine = null;
      List<Datagram> list_a = new ArrayList<>();
      List<Datagram> list_b = new ArrayList<>();
      List<Datagram> list_i = new ArrayList<>();
      List<Datagram> list_n = new ArrayList<>();

      while ((readLine = reader.readLine()) != null) {
        // System.out.println(readLine);
        pos += readLine.length() + 2;
        lineNumber++;
        if (progressCallback != null) {
          progressCallback.progress(pos, length, "");
        }

        try {
          Datagram datagram = DatagramFactory.buildDatagram(readLine, lineNumber);
          if (datagram != null) {
            switch (datagram.getChannel()) {
            case CHANNEL_A:
              list_a.add(datagram);
              break;
            case CHANNEL_B:
              list_b.add(datagram);
              break;
            case CHANNEL_I:
              list_i.add(datagram);
              break;
            default:
              list_n.add(datagram);
              getErrorList().add(String.format("Line %d: no channel.", lineNumber));
              break;
            }
          }
        } catch (ParseException e) {
          getErrorList().add(String.format("Line %d: parsing exception: %s", lineNumber, e.getMessage()));
        }
      }
      reader.close();
      fileReader.close();

      // listen sortieren

      Collections.sort(list_a, getDatagramSortComp());
      Collections.sort(list_b, getDatagramSortComp());
      Collections.sort(list_i, getDatagramSortComp());
      Collections.sort(list_n, getDatagramSortComp());

      // evt. rawdatas zusammenfassen (bei NMEA Zeilen > 80 Zeichen
      mergeNMEAData(list_a);
      mergeNMEAData(list_b);

      channelADataCount = list_a.size();
      channelBDataCount = list_b.size();
      channelIDataCount = list_i.size();
      channelNDataCount = list_n.size();
      // listen zusammen führen
      list.clear();
      filterSentence(list_a, list);
      list_a.clear();
      filterSentence(list_b, list);
      list_b.clear();
      filterSentence(list_n, list);
      list_n.clear();
      filterSentence(list_i, list);
      list_i.clear();
      Collections.sort(list, getDatagramSortComp());
      for (Datagram datagram : list) {
        if (datagram instanceof NMEADatagram) {
          NMEADatagram nmeaDatagram = (NMEADatagram) datagram;
          if (nmeaDatagram.isError()) {
            getErrorList().add(String.format("Line %d: NMEA crc error: found crc %h, calculate crc %h, %s",
                datagram.getLineNumber(), ((NMEADatagram) datagram).getFoundCrc(), ((NMEADatagram) datagram).getCrc(),
                datagram.getRawData()));
          }
        } else {
          getErrorList()
              .add(String.format("Line %d: not NMEA Datagram: %s", datagram.getLineNumber(), datagram.getRawData()));
        }

      }
    } finally {
    }
    // und fertig
    return this;
  }

  private void filterSentence(List<Datagram> srcList, List<Datagram> list) {
    if (sentenceFilter != null) {
      for (Datagram datagram : srcList) {
        if (datagram instanceof NMEADatagram) {
          NMEADatagram nmeaSentence = (NMEADatagram) datagram;
          if (sentenceFilter.filterSentence(nmeaSentence.getNmemonic())) {
            list.add(datagram);
          }
        }
      }
    } else {
      list.addAll(srcList);
    }
  }

  private void mergeNMEAData(List<Datagram> list_a) {
    for (Iterator<Datagram> iterator = list_a.iterator(); iterator.hasNext();) {
      Datagram datagram = iterator.next();
      if (datagram instanceof NMEADatagram) {
        NMEADatagram nmeaDatagram = (NMEADatagram) datagram;
        if (!nmeaDatagram.isFullDatagram()) {
          datagram = (Datagram) iterator.next();
          if (!(datagram instanceof NMEADatagram)) {
            nmeaDatagram.addRawdata(datagram.getRawData());
            iterator.remove();
          }
        }
      }
    }
  }

  private Comparator<Datagram> getDatagramSortComp() {
    if (comparator == null) {
      comparator = new Comparator<Datagram>() {

        @Override
        public int compare(Datagram o1, Datagram o2) {
          long stampo1 = o1.getTimeStamp();
          long stampo2 = o2.getTimeStamp();
          return Long.compare(stampo1, stampo2);
        }
      };
    }
    return comparator;
  }

  public List<String> checkFile() throws IOException {
    parseFile();
    return errorList;
  }

  public List<Datagram> getDatagrams() {
    return Collections.unmodifiableList(list);
  }

  /**
   * @return the progressCallback
   */
  public ProgressCallback getProgressCallback() {
    return progressCallback;
  }

  /**
   * @param progressCallback
   *          the progressCallback to set
   * @return
   */
  public LoggerFile setProgressCallback(ProgressCallback progressCallback) {
    this.progressCallback = progressCallback;
    return this;
  }

  public int getCount() {
    return list.size();
  }

  public void writeLoggerFile(File newFile) throws IOException {
    Writer writer = new BufferedWriter(new FileWriter(newFile));
    if (list != null) {
      int i = 0;
      int count = list.size();
      for (Datagram datagram : list) {
        writer.write(datagram.getNMEAString());
        writer.write("\r\n");
        i++;
        if (progressCallback != null) {
          progressCallback.progress(i, count, "");
        }
      }
    }
    writer.close();
  }

  public int getChannelDataCount(CHANNEL channelA) {
    switch (channelA) {
    case CHANNEL_A:
      return channelADataCount;
    case CHANNEL_B:
      return channelBDataCount;
    case CHANNEL_I:
      return channelIDataCount;
    case NONE:
      return channelNDataCount;
    default:
      break;
    }
    return 0;
  }

  public LoggerFile setSentenceFilter(SentenceFilter sentenceFilter) {
    this.sentenceFilter = sentenceFilter;
    return this;
  }

  public LoggerFile removeSentenceFilter() {
    this.sentenceFilter = null;
    return this;
  }

  /**
   * @return the errorList
   */
  public List<String> getErrorList() {
    return errorList;
  }

  /**
   * @param errorList
   *          the errorList to set
   */
  public void setErrorList(List<String> errorList) {
    this.errorList = errorList;
  }
}
