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

import de.mcs.depth.logger.datagramms.NMEAConstants;
import de.mcs.depth.logger.datagramms.NMEADatagram;

/**
 * NMEA Depth below Keel
 * 
 * @author Willie
 * 
 */
public abstract class NMEADepthSentence extends NMEADatagram {

  private static double FEET2METERCONSTANT = 3.2808;
  private static double FEET2FATHOMSCONSTANT = 6.0;

  /**
   * @param timeStamp
   * @param channel
   * @param rawData
   * @param lineNumber
   */
  public NMEADepthSentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

  public double getDepth(NMEAConstants.DEPTH_UNIT unit) {
    String[] datafields = getDatafields();
    double depth = 0.0;
    if (datafields.length > 6) {
      String depthStr = datafields[1];
      if (!depthStr.trim().equals("")) {
        // Tiefenangabe in Fuss
        depth = Double.parseDouble(depthStr);
      }
      if (depth == 0.0) {
        // keine Tiefe, Angabe evt. in Meter
        depthStr = datafields[3];
        if (!depthStr.trim().equals("")) {
          depth = Double.parseDouble(depthStr);
          depth = depth * FEET2METERCONSTANT;
        }
      }
      if (depth == 0.0) {
        // immer noch keine Tiefe, Angabe evt. in Fathoms
        depthStr = datafields[5];
        if (!depthStr.trim().equals("")) {
          depth = Double.parseDouble(depthStr);
          depth = depth * FEET2FATHOMSCONSTANT;
        }
      }

      switch (unit) {
      case METER:
        return depth / FEET2METERCONSTANT;
      case FATHOMS:
        return depth / FEET2FATHOMSCONSTANT;
      default:
        return depth;
      }
    }

    return depth;
  }
}
