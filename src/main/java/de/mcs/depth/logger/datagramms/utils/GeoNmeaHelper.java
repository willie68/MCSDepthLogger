package de.mcs.depth.logger.datagramms.utils;

public class GeoNmeaHelper {

  /**
   * @param latitudeStr
   * @return
   */
  public static double getLatitude(String latitudeStr, String latitudeMod) {
    int pointPos = latitudeStr.indexOf(".");

    String gradStr = latitudeStr.substring(0, pointPos - 2);
    String minutenStr = latitudeStr.substring(pointPos - 2);
    double minuten = Double.parseDouble(minutenStr);
    int grad = Integer.parseInt(gradStr);
    double latitude = grad + (minuten / 60.0);
    if (latitudeMod.equalsIgnoreCase("W") || latitudeMod.equalsIgnoreCase("S")) {
      latitude = -1 * latitude;
    }
    return latitude;
  }

}
