/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: MeasureConverter.java
 * EMail: W.Klaas@gmx.de
 * Created: 05.04.2014 wklaa_000
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
package de.mcs.depth.utils;

import de.mcs.depth.Constants.MEASURE_TYPE;

/**
 * @author wklaa_000
 * 
 */
public class MeasureConverter {

  public static double convertSpeed(MEASURE_TYPE input, MEASURE_TYPE output, double value) {
    switch (input) {
    case IMPERIAL:
      return convertSpeedImperial(output, value);
    case METRICAL:
      return convertSpeedMetrical(output, value);
    case NAUTICAL:
      return convertSpeedNautical(output, value);
    default:
      return 0.0;
    }
  }

  /**
   * Konvertiert nautische kn in die entsprechende Einheit
   * 
   * @param output
   * @param value
   * @return
   */
  public static double convertSpeedNautical(MEASURE_TYPE output, double value) {
    switch (output) {
    case IMPERIAL:
      return kn2mph(value);
    case METRICAL:
      return kn2kmh(value);
    case NAUTICAL:
      return value;
    default:
      return 0.0;
    }
  }

  public static double kn2kmh(double value) {
    return value * 1.852;
  }

  public static double kn2mph(double value) {
    return value * 1.15078;
  }

  /**
   * Konvertiert metrische kmh in die entsprechende Einheit
   * 
   * @param output
   * @param value
   * @return
   */
  public static double convertSpeedMetrical(MEASURE_TYPE output, double value) {
    switch (output) {
    case IMPERIAL:
      return kmh2mph(value);
    case METRICAL:
      return value;
    case NAUTICAL:
      return kmh2kn(value);
    default:
      return 0.0;
    }
  }

  private static double kmh2mph(double value) {
    return value * 0.62137;
  }

  private static double kmh2kn(double value) {
    return value * 0.53996;
  }

  /**
   * Konvertiert imperiale mph in die entsprechende Einheit
   * 
   * @param output
   * @param value
   * @return
   */
  public static double convertSpeedImperial(MEASURE_TYPE output, double value) {
    switch (output) {
    case IMPERIAL:
      return value;
    case METRICAL:
      return mph2kmh(value);
    case NAUTICAL:
      return mph2kn(value);
    default:
      return 0.0;
    }
  }

  private static double mph2kn(double value) {
    return value * 0.86898;
  }

  private static double mph2kmh(double value) {
    return value * 1.6093;
  }

}
