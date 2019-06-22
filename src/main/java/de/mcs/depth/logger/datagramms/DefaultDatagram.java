/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: DefaultDatagram.java
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

import java.sql.Date;

/**
 * @author Willie
 * 
 */
public class DefaultDatagram implements Datagram {

  public enum CHANNEL {
    CHANNEL_A, CHANNEL_B, CHANNEL_I, NONE
  };

  private long timeStamp;
  private String rawData;
  private CHANNEL channel;
  private int lineNumber;

  public DefaultDatagram(long timeStamp, CHANNEL channel, String rawData, int lineNumber) {
    this.timeStamp = timeStamp;
    this.rawData = rawData.trim();
    this.channel = channel;
    this.lineNumber = lineNumber;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.depth.logger.datagramms.Datagram#getTimeStamp()
   */
  @Override
  public long getTimeStamp() {
    return timeStamp;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.depth.logger.datagramms.Datagram#getRawLine()
   */
  @Override
  public String getRawData() {
    return rawData;
  }

  @Override
  public String toString() {
    return new Date(getTimeStamp()) + ";" + getChannel() + ";" + getRawData();
  }

  public CHANNEL getChannel() {
    return channel;
  }

  public void addRawdata(String rawData) {
    this.rawData = this.rawData + rawData;
    analyseDatagram();
  }

  @Override
  public void analyseDatagram() {
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public String getNMEAString() {
    StringBuilder builder = new StringBuilder();
    builder.append(DatagramConstants.getTimeStampFormat().format(new Date(getTimeStamp())));
    builder.append(";");
    switch (getChannel()) {
    case CHANNEL_A:
      builder.append("A");
      break;
    case CHANNEL_B:
      builder.append("B");
      break;
    case CHANNEL_I:
      builder.append("I");
      break;
    default:
      break;
    }
    builder.append(";");
    builder.append(getRawData());
    return builder.toString();
  }
}
