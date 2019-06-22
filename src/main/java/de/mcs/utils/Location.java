/**
 * 
 */
package de.mcs.utils;

import java.io.Serializable;

/**
 * @author w.klaas
 * 
 */
public class Location implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -1050404609188783151L;
  private double longitude;
  private double latitude;

  // create and initialize a point with given name and
  // (latitude, longitude) specified in degrees
  public Location(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  // return string representation of this point
  public String toString() {
    return getLatitude() + ", " + longitude;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }
}
