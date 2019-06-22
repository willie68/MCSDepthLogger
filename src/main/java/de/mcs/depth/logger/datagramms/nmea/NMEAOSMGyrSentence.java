/**
 * 
 */
package de.mcs.depth.logger.datagramms.nmea;

import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.NMEADatagram;

/**
 * @author w.klaas
 * 
 */
public class NMEAOSMGyrSentence extends NMEADatagram implements Datagram {

  public NMEAOSMGyrSentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

  public int getGyroX() {
    String[] datafields = getDatafields();
    int result = 0;
    try {
      if (datafields.length > 1) {
        String value = datafields[1].trim();
        result = Integer.parseInt(value);
      }
    } catch (Exception e) {

    }
    return result;
  }

  public int getGyroY() {
    String[] datafields = getDatafields();
    int result = 0;
    try {
      if (datafields.length > 2) {
        String value = datafields[2].trim();
        result = Integer.parseInt(value);
      }
    } catch (Exception e) {

    }
    return result;
  }

  public int getGyroZ() {
    String[] datafields = getDatafields();
    int result = 0;
    try {
      if (datafields.length > 3) {
        String value = datafields[3].trim();
        result = Integer.parseInt(value);
      }
    } catch (Exception e) {

    }
    return result;
  }

  public double normGyro(int value) {
    return value / 16768.0 * 250.0;
  }

}
