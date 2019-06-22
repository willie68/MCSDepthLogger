package de.mcs;

/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: CommonInformation.java
 * EMail: W.Klaas@gmx.de
 * Created: 12.12.2013 Willie
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * Automatische Versionsmanagment. Diese Klasse liest die ben&ouml;tigten
 * Informationen aus der version.properties und erzeugt die entsprechenden
 * Versionsstrings.
 * 
 * @author Wilfried Klaas
 */
public class CommonInformation {

  /** The version information. */
  public static Properties versionInformation = new Properties();
  static {
    InputStream input = ClassLoader.getSystemResourceAsStream("de/mcs/version.properties");
    try {
      versionInformation.load(input);
    } catch (IOException e) {
    }
  }

  /** release verion of this server. */
  public static final String APPLICATION_NAME = versionInformation.getProperty("application.name");

  /** release verion of this server. */
  public static final String RELEASE_VERSION = versionInformation.getProperty("release.version");

  /** release verion of this server. */
  public static final String VERSION_STRING = versionInformation.getProperty("version.string");

  /** build date automatically set by build script. */
  public static final String BUILD_DATE = versionInformation.getProperty("build.date");

  /** build number automatically set by build script. */
  public static final String BUILD_NUM = versionInformation.getProperty("build.num");

  /** build number automatically set by build script. */
  public static final String BUILD_YEAR = versionInformation.getProperty("build.year");

  /** vendor automatically set by build script. */
  public static final String RELEASE_VENDOR = "MCS Media Computer Software";

  /** version string. */
  public static final String RELEASE_VERSION_BUILD = RELEASE_VERSION + " / Build " + BUILD_NUM + " from " + BUILD_DATE;

  /** version string. */
  public static final String APPLICATION_URL = versionInformation.getProperty("application.url");
  public static final String UPDATE_ID = versionInformation.getProperty("application.update.id");
  public static final String UPDATE_URL = versionInformation.getProperty("application.update.url");

  /** copyright string. */
  public static final String COPYRIGHT = "<html><body><br/>Copyright (c) " + BUILD_YEAR + " by " + RELEASE_VENDOR
      + "<br/>All rights reserved!<br/><br/>" + "Java and all Java-based trademarks and logos are trademarks<br/>"
      + "or registered trademarks of Sun Microsystems, Inc. in the United States and other countries.</body></html>";

  public static final String FIRMWARE_UPDATE_URL = versionInformation.getProperty("firmware.update.url");
  public static final String FIRMWARE_UPDATE_ID = versionInformation.getProperty("firmware.update.id");
  public static final String FIRMWARE_DOWNLOAD_URL = versionInformation.getProperty("firmware.download.url");

}
