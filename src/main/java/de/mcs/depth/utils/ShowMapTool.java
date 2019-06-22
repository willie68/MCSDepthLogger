/**
 * MCS Media Computer Software
 * Copyright 2016 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: RouteConverterTool.java
 * EMail: W.Klaas@gmx.de
 * Created: 27.04.2016 wklaa_000
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.mcs.depth.ProgramConfig;
import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.utils.Files;
import de.mcs.utils.NMEAUtils;
import de.mcs.utils.RouteConverterUtils;
import de.mcs.utils.StartProcess;
import de.mcs.utils.URLUtilities;

/**
 * @author wklaa_000
 *
 */
public class ShowMapTool {

  public static void showInBrowser(File dataFile, List<Datagram> datagrams) throws IOException, URISyntaxException {
    File nmeaFile = Files.changeExtension(dataFile, ".nmea");
    NMEAUtils.writeNmeaFile(nmeaFile, datagrams);

    File htmlFile = Files.changeExtension(dataFile, ".html");
    if (htmlFile.exists()) {
      htmlFile.delete();
    }
    RouteConverterUtils.convert(nmeaFile, htmlFile);
    if (htmlFile.exists()) {
      URLUtilities.openUrl(htmlFile.toURI().toURL().toString());
    } else {
      throw new IOException("no data found");
    }
  }

  public static void showInExternalProgram(File dataFile, List<Datagram> datagrams) throws IOException {
    File nmeaFile = Files.changeExtension(dataFile, ".nmea");
    NMEAUtils.writeNmeaFile(nmeaFile, datagrams);

    String showMapProgram = ProgramConfig.getInstance().getShowMapProgram();
    List<String> command = new ArrayList<>();
    command.add(showMapProgram);
    command.add(nmeaFile.getCanonicalPath());
    StartProcess.startJava(command, false, ".");
  }

}
