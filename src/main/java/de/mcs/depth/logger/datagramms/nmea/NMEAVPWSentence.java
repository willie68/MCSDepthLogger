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

import de.mcs.depth.logger.datagramms.NMEADatagram;

/**
 * @author Willie
 * 
 */
public class NMEAVPWSentence extends NMEADatagram {

  /**
   * @param timeStamp
   * @param channel
   * @param rawData
   * @param lineNumber
   */
  public NMEAVPWSentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

}
