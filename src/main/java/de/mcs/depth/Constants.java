/**
 * 
 */
package de.mcs.depth;

/**
 * @author w.klaas
 * 
 */
public class Constants {

  public static enum SHOW_IN_TYPE {
    INTERNAL, BROWSER, PROGRAM
  };

  public static enum BOOTLOADERVERSION {
    FAT16, FAT32
  }

  public static enum SERIES {
    SPEED, DEPTH, ACC, GYR, VCC
  }

  public static enum MEASURE_TYPE {
    NAUTICAL, IMPERIAL, METRICAL
  };

  public static enum EXPORT_TYPES {
    NMEA, MCSLOGGER, GPX
  }

}
