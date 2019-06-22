/**
 * 
 */
package de.mcs.depth.logger.datagramms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author w.klaas
 * 
 */
public class DateTimeUtils {

  private static SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HHmmss");
  private static SimpleDateFormat longTimeFormat = new SimpleDateFormat("HHmmss.SSS");
  private static SimpleDateFormat shortDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  private static SimpleDateFormat longDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
  public static SimpleDateFormat shortEuroDateFormat = new SimpleDateFormat("ddMMyy");

  public static Date parseTime(String time) {
    try {
      if (time.length() == 6) {
        return shortTimeFormat.parse(time);
      } else {
        return longTimeFormat.parse(time);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Date parseDateTime(String time) {
    try {
      if (time.length() == 14) {
        return shortDateTimeFormat.parse(time);
      } else {
        return longDateTimeFormat.parse(time);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param date
   * @return
   */
  public static Date parseShortEuroDate(String date) {
    try {
      return shortEuroDateFormat.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
}
