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
public class NMEAOSMStopSentence extends NMEADatagram implements Datagram {

  public NMEAOSMStopSentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

}
