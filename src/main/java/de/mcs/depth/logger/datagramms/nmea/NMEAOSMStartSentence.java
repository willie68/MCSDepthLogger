/**
 * 
 */
package de.mcs.depth.logger.datagramms.nmea;

import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.NMEADatagram;
import de.mcs.utils.Version;

/**
 * @author w.klaas
 * 
 */
public class NMEAOSMStartSentence extends NMEADatagram implements Datagram {

  public NMEAOSMStartSentence(long timeStamp, String talkerId, CHANNEL channel, String rawData, int lineNumber) {
    super(timeStamp, talkerId, channel, rawData, lineNumber);
  }

  public Version getVersion() {
    String[] datafields = getDatafields();
    String versionString = datafields[2].trim();
    if (versionString.startsWith("v") || versionString.startsWith("V")) {
      versionString = versionString.substring(1).trim();
    }
    try {
      return new Version(versionString);
    } catch (Exception e) {
      return new Version();
    }
  }
}
