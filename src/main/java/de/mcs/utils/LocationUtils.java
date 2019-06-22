/**
 * 
 */
package de.mcs.utils;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * @author w.klaas
 * 
 */
public class LocationUtils {
  static double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

  // return distance between this location and that location
  // measured in statute miles
  public static double distanceTo(Location source, Location destination) {
    double lat1 = Math.toRadians(source.getLatitude());
    double lon1 = Math.toRadians(source.getLongitude());
    double lat2 = Math.toRadians(destination.getLatitude());
    double lon2 = Math.toRadians(destination.getLongitude());

    // great circle distance in radians, using law of cosines formula
    double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

    // each degree on a great circle of Earth is 60 nautical miles
    double nauticalMiles = 60 * Math.toDegrees(angle);
    double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    return statuteMiles;
  }

  // test client
  public static void main(String[] args) {
    Location loc1 = new Location(40.366633, 74.640832);
    Location loc2 = new Location(42.443087, 76.488707);
    double distance = distanceTo(loc1, loc2);
    System.out.printf("%6.3f miles from\n", distance);
    System.out.println(loc1 + " to " + loc2);
  }

  public static GeoPosition convertLocation2GeoPosition(Location location) {
    return new GeoPosition(location.getLatitude(), location.getLongitude());
  }
}
