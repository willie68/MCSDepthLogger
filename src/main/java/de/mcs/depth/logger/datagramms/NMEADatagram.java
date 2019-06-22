/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: NMEADatagram.java
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

/**
 * @author Willie
 * 
 */
public class NMEADatagram extends DefaultDatagram implements Datagram {

  public enum CRC_STATE {
    TRUE, FALSE, NONE
  };

  private boolean error;
  private CRC_STATE crcState;
  private String nmemonic;
  private int foundCrc;
  private int crc;
  private String talkerId;

  public NMEADatagram(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, channel, rawData, lineNumber);
    this.talkerId = talkerId;
    error = false;
    analyseDatagram();
  }

  @Override
  public void analyseDatagram() {
    String data = getRawData();
    if (!data.startsWith("$") && !data.startsWith("!")) {
      this.error = true;
    } else {
      this.crcState = CRC_STATE.NONE;
      if (data.indexOf("*") >= 0) {
        String dataString = data.substring(1, data.indexOf("*"));
        String crcString = data.substring(data.indexOf("*") + 1);
        if (crcString.length() > 2) {
          error = true;
          return;
        }
        try {
          foundCrc = Integer.parseInt(crcString, 16);
          crc = 0;
          for (int i = 0; i < dataString.length(); i++) {
            char c = dataString.charAt(i);
            crc ^= c;
          }
          if (crc == getFoundCrc()) {
            this.crcState = CRC_STATE.TRUE;
          } else {
            this.crcState = CRC_STATE.FALSE;
            this.error = true;
          }
        } catch (Exception e) {
          this.crcState = CRC_STATE.FALSE;
        }
        data = dataString;
      }
      if (data.indexOf(",") >= 0) {
        this.nmemonic = data.substring(0, data.indexOf(","));
      } else {
        this.nmemonic = data;
      }
    }
  }

  public CRC_STATE getCrcState() {
    return crcState;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    if (error) {
      b.append("E;");
    }
    b.append("CRC=");
    b.append(getCrcState().toString());
    b.append(";");
    b.append("NME=");
    b.append(getNmemonic());
    b.append(";");
    b.append(super.toString());
    return b.toString();
  }

  public String getNmemonic() {
    return nmemonic;
  }

  public boolean isError() {
    return error;
  }

  public boolean isFullDatagram() {
    boolean fullDataGram = true;
    String rawData = getRawData();
    if (rawData.length() >= 80) {
      if (crcState.equals(CRC_STATE.NONE)) {
        return false;
      }
    }
    return fullDataGram;
  }

  public int getFoundCrc() {
    return foundCrc;
  }

  public int getCrc() {
    return crc;
  }

  public String[] getDatafields() {
    String data = getRawData();
    if (data.indexOf("*") >= 0) {
      data = data.substring(1, data.indexOf("*"));
    }
    return data.split(",");
  }

  public String getTalkerID() {
    return talkerId;
  }
}
