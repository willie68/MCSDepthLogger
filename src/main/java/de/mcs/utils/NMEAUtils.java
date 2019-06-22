/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: NMEAUtils.java
 * EMail: W.Klaas@gmx.de
 * Created: 16.02.2014 wklaa_000
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
package de.mcs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mcs.depth.logger.ProgressCallback;
import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.DatagramFactory;

/**
 * @author wklaa_000
 * 
 */
public class NMEAUtils {
  private static Logger logger = Logger.getLogger(NMEAUtils.class);

  /**
   * @param nmeaFile
   * @param datagrams
   * @throws IOException
   */
  public static void writeNmeaFile(File nmeaFile, List<Datagram> datagrams) throws IOException {
    logger.info(String.format("writing nmea file %s", nmeaFile.getName()));
    Writer out = new BufferedWriter(new FileWriter(nmeaFile));
    for (Datagram datagram : datagrams) {
      out.write(datagram.getRawData());
      out.write("\r\n");
    }
    out.close();
  }

  public static List<Datagram> readNMEAData(File dataFile, ProgressCallback progressCallback)
      throws IOException, ParseException {
    BufferedReader reader = new BufferedReader(new FileReader(dataFile));
    String readLine;
    long length = dataFile.length();
    long pos = 0;
    int lineNumber = 0;

    List<Datagram> list = new ArrayList<>();

    while ((readLine = reader.readLine()) != null) {
      // System.out.println(readLine);
      pos += readLine.length() + 2;
      lineNumber++;
      if (progressCallback != null) {
        progressCallback.progress(pos, length, "");
      }

      Datagram datagram = DatagramFactory.buildDatagram(readLine, lineNumber);
      if (datagram != null) {
        list.add(datagram);
      }
    }
    reader.close();
    return list;
  }
}
