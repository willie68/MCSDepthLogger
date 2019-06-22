/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: RouteConverterUtils.java
 * EMail: W.Klaas@gmx.de
 * Created: 16.02.2014 wklaa_000
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
package de.mcs.utils;

import static slash.common.io.Files.createTargetFiles;
import static slash.navigation.base.NavigationFormatParser.getNumberOfFilesToWriteFor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import slash.navigation.base.BaseNavigationFormat;
import slash.navigation.base.MultipleRoutesFormat;
import slash.navigation.base.NavigationFormat;
import slash.navigation.base.NavigationFormatParser;
import slash.navigation.base.NavigationFormats;
import slash.navigation.base.ParserResult;

/**
 * @author wklaa_000
 * 
 */
public class RouteConverterUtils {
  private static Logger logger = Logger.getLogger(RouteConverterUtils.class);

  @SuppressWarnings("rawtypes")
  public static void convert(File source, File target) throws IOException {
    BaseNavigationFormat format = findFormat("WebPageFormat");
    NavigationFormatParser parser = new NavigationFormatParser();
    ParserResult result = parser.read(source);
    if (!result.isSuccessful()) {
      logger.error("Could not read source '" + source.getAbsolutePath() + "'");
    } else {

      if (format.isSupportsMultipleRoutes()) {
        parser.write(result.getAllRoutes(), (MultipleRoutesFormat) format, target);
      } else {
        int fileCount = getNumberOfFilesToWriteFor(result.getTheRoute(), format, false);
        File[] targets = createTargetFiles(target, fileCount, format.getExtension(), format.getMaximumFileNameLength());
        for (File t : targets) {
          if (t.exists()) {
            logger.error("Target '" + t.getAbsolutePath() + "' already exists; stopping.");
          }
        }
        parser.write(result.getTheRoute(), format, false, false, null, targets);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private static BaseNavigationFormat findFormat(String formatName) {
    List<NavigationFormat> formats = NavigationFormats.getWriteFormats();
    for (NavigationFormat format : formats)
      if (formatName.equals(format.getClass().getSimpleName()))
        return (BaseNavigationFormat) format;
    return null;
  }

}
