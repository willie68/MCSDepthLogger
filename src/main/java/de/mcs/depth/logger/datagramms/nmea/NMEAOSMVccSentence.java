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
public class NMEAOSMVccSentence extends NMEADatagram implements Datagram {

  public NMEAOSMVccSentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

  public int getVoltage() {
    String[] datafields = getDatafields();
    int voltageValue = 0;
    try {
      if (datafields.length > 1) {
        String value = datafields[1].trim();
        voltageValue = Integer.parseInt(value);
      }
    } catch (Exception e) {

    }
    return voltageValue;
  }
}
