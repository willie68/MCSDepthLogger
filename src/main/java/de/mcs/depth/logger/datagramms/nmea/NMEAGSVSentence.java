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
public class NMEAGSVSentence extends NMEADatagram {

  public class SatelliteInfo {
    private int number;
    private int elevation;
    private int azimuth;
    private int snr;

    /**
     * 
     */
    public SatelliteInfo(int number, int elevation, int azimuth, int snr) {
      this.number = number;
      this.elevation = elevation;
      this.azimuth = azimuth;
      this.snr = snr;
    }

    /**
     * @return the snr
     */
    public int getSnr() {
      return snr;
    }

    /**
     * @return the azimuth
     */
    public int getAzimuth() {
      return azimuth;
    }

    /**
     * @return the elevation
     */
    public int getElevation() {
      return elevation;
    }

    /**
     * @return the number
     */
    public int getNumber() {
      return number;
    }
  }

  /**
   * @param timeStamp
   * @param channel
   * @param rawData
   * @param lineNumber
   */
  public NMEAGSVSentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

  public int getTotalMessageNumber() {
    String[] dataFields = getDatafields();
    return Integer.parseInt(dataFields[1]);
  }

  public int getMessageNumber() {
    String[] dataFields = getDatafields();
    return Integer.parseInt(dataFields[2]);
  }

  public int getSatellitesInView() {
    String[] dataFields = getDatafields();
    return Integer.parseInt(dataFields[3]);
  }

  public int getSatelliteInfoCount() {
    String[] dataFields = getDatafields();
    return (dataFields.length - 4) / 4;
  }

  public SatelliteInfo getSatelliteInfo(int number) {
    String[] dataFields = getDatafields();
    int index = number * 4 + 4;
    if ((index + 3) > dataFields.length) {
      return null;
    }
    return new SatelliteInfo(Integer.parseInt(dataFields[index++]), Integer.parseInt(dataFields[index++]),
        Integer.parseInt(dataFields[index++]), Integer.parseInt(dataFields[index++]));
  }
}
