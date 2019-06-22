/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: DatagramConstants.java
 * EMail: W.Klaas@gmx.de
 * Created: 08.04.2014 wklaa_000
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
package de.mcs.depth.logger.datagramms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author wklaa_000
 * 
 */
public class DatagramConstants {
  private final static SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss.SSS");

  public static DateFormat getTimeStampFormat() {
    return dateformat;
  }
}
