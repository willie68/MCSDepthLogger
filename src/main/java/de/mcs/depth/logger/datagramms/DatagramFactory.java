/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: DatagramFactory.java
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
package de.mcs.depth.logger.datagramms;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;

import de.mcs.depth.logger.datagramms.DefaultDatagram.CHANNEL;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMAccSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMGyrSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMSeaTalkSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMStartSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMStopSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMVccSentence;

/**
 * @author Willie
 * 
 */
public class DatagramFactory {

  private static Logger logger = Logger.getLogger(DatagramFactory.class);

  public static long parseTimeStamp(String timestampStr) throws ParseException {
    Date timestamp = DatagramConstants.getTimeStampFormat().parse(timestampStr);
    return timestamp.getTime();
  }

  public static Datagram buildDatagram(String line, int lineNumber) throws ParseException {
    String rawData = line;
    CHANNEL channel = CHANNEL.NONE;
    long timeStamp = lineNumber;
    if (line.indexOf(';') > 0) {
      String timestampStr = line.substring(0, line.indexOf(';'));
      timeStamp = parseTimeStamp(timestampStr);
      rawData = line.substring(line.indexOf(';') + 1).trim();
      if (rawData.indexOf(';') > 0) {
        String channelStr = rawData.substring(0, rawData.indexOf(';'));
        channelStr = channelStr.trim();
        if (channelStr.equalsIgnoreCase("A")) {
          channel = CHANNEL.CHANNEL_A;
        } else if (channelStr.equalsIgnoreCase("B")) {
          channel = CHANNEL.CHANNEL_B;
        } else if (channelStr.equalsIgnoreCase("I")) {
          channel = CHANNEL.CHANNEL_I;
        }
        rawData = rawData.substring(rawData.indexOf(';') + 1).trim();
      }
    }
    if (rawData.startsWith("$") || rawData.startsWith("!")) {
      String nmemonic = rawData;
      if (rawData.indexOf(",") >= 0) {
        nmemonic = rawData.substring(0, rawData.indexOf(",")).toUpperCase();
      }
      if (nmemonic.startsWith("$P")) {
        String manufacturer = nmemonic.substring(2, 5);
        String sentenceId = nmemonic.substring(5);
        if (manufacturer.equalsIgnoreCase("OSM")) {
          switch (sentenceId) {
          case "ST":
            return new NMEAOSMStartSentence(timeStamp, manufacturer, channel, rawData, lineNumber);
          case "SO":
            return new NMEAOSMStopSentence(timeStamp, manufacturer, channel, rawData, lineNumber);
          case "ACC":
            return new NMEAOSMAccSentence(timeStamp, manufacturer, channel, rawData, lineNumber);
          case "GYR":
            return new NMEAOSMGyrSentence(timeStamp, manufacturer, channel, rawData, lineNumber);
          case "VCC":
            return new NMEAOSMVccSentence(timeStamp, manufacturer, channel, rawData, lineNumber);
          case "SK":
            return new NMEAOSMSeaTalkSentence(timeStamp, manufacturer, channel, rawData, lineNumber);
          default:
            break;
          }
        }
      } else {
        if (nmemonic.length() > 3) {
          String talkerId = nmemonic.substring(1, 3).toLowerCase();
          if (nmemonic.length() > 2) {
            String sentenceId = nmemonic.substring(3);
            String className = String.format("de.mcs.depth.logger.datagramms.nmea.NMEA%sSentence", sentenceId);
            try {
              Class<?> sentenceClass = Class.forName(className);
              Constructor<?> constructor = sentenceClass.getDeclaredConstructor(long.class, String.class, CHANNEL.class,
                  String.class, int.class);
              return (NMEADatagram) constructor.newInstance(timeStamp, talkerId, channel, rawData, lineNumber);
            } catch (Exception e) {
              logger.debug(String.format("unknown sentence id:%s", sentenceId));
              return new NMEADatagram(timeStamp, talkerId, channel, rawData, lineNumber);
            }
          }
        }
      }
      return new NMEADatagram(timeStamp, "", channel, rawData, lineNumber);
    }
    DefaultDatagram datagram = new DefaultDatagram(timeStamp, channel, rawData, lineNumber);
    return datagram;
    // return null;
  }
}
