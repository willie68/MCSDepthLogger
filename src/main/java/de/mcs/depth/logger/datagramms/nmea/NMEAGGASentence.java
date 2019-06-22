/**
 * MCS Media Computer Software
 * Copyright (C) 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: NMEAGSVSentence.java
 * EMail: W.Klaas@gmx.de
 * Created: 28.01.2014 Willie
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

package de.mcs.depth.logger.datagramms.nmea;

import java.util.Date;

import de.mcs.depth.logger.datagramms.NMEADatagram;
import de.mcs.depth.logger.datagramms.utils.DateTimeUtils;
import de.mcs.depth.logger.datagramms.utils.GeoNmeaHelper;
import de.mcs.utils.Location;

/**
 * @author Willie
 * 
 */
public class NMEAGGASentence extends NMEADatagram {

  /**
   * @param timeStamp
   * @param channel
   * @param rawData
   * @param lineNumber
   */
  public NMEAGGASentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

  public Date getTime() {
    String[] datafields = getDatafields();
    String time = datafields[1];
    return DateTimeUtils.parseTime(time);
  }

  public Location getLocation() {
    String[] datafields = getDatafields();
    if (datafields.length > 6) {

      String latitudeStr = datafields[2];
      String latitudeMod = datafields[3];

      if ((latitudeStr != null) && !latitudeStr.equals("") && (latitudeMod != null) && !latitudeMod.equals("")) {

        double latitude = GeoNmeaHelper.getLatitude(latitudeStr, latitudeMod);

        String longitudeStr = datafields[4];
        String longitudeMod = datafields[5];

        if ((longitudeStr != null) && !longitudeStr.equals("") && (longitudeMod != null) && !longitudeMod.equals("")) {

          double longitude = GeoNmeaHelper.getLatitude(longitudeStr, longitudeMod);

          return new Location(latitude, longitude);
        }
      }
    }
    return null;
  }

}
