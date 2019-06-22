/**
 * MCS Media Computer Software
 * Copyright 2016 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: Exporter.java
 * EMail: W.Klaas@gmx.de
 * Created: 26.04.2016 wklaa_000
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.mcs.CommonInformation;
import de.mcs.depth.Constants.EXPORT_TYPES;
import de.mcs.depth.StatusAndProgress;
import de.mcs.depth.logger.LoggerFile;
import de.mcs.depth.logger.ProgressCallback;
import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.nmea.NMEARMCSentence;
import de.mcs.utils.Files;

/**
 * @author wklaa_000
 *
 */
public class ExportTool {
  private Logger logger = Logger.getLogger(this.getClass());
  private StatusAndProgress sap;

  private Writer out;

  private Document doc;

  private Namespace namespace;

  private DateFormat df;

  private EXPORT_TYPES exportType;

  private Element trk;

  public ExportTool(StatusAndProgress sap, EXPORT_TYPES exptype) {
    this.sap = sap;
    this.exportType = exptype;
  }

  /**
   * @param sdFile
   * @param nmeaFile
   * @param exptype
   * @param dataFile
   */
  public void exportData(final File sdFile, File nmeaFile) {
    try {
      final File dataFile = new File(Files.getTempPath(), sdFile.getName());
      Files.fileCopy(sdFile, dataFile);

      LoggerFile loggerFile = new LoggerFile(dataFile).setProgressCallback(new ProgressCallback() {
        int pos = 0;

        @Override
        public void progress(long position, long max, String message) {
          final int newpos = (int) (position * 100 / max);
          if (newpos != pos) {
            pos = newpos;
            sap.setProgress(pos);
          }
        }
      }).parseFile();

      loggerFile.setProgressCallback(null);
      LoggingTool.showParseResults(logger, dataFile, loggerFile);

      logger.info("writing new file:" + nmeaFile.getCanonicalPath());

      List<Datagram> datagrams = loggerFile.getDatagrams();
      switch (exportType) {
      case GPX:
        writeOutGPX(out, datagrams);
        break;
      case NMEA:
        writeOutNMEA(out, datagrams);
        break;
      case MCSLOGGER:
        writeOutDAT(out, datagrams);
        break;
      default:
        writeOutNMEA(out, datagrams);
        break;
      }

      Thread.yield();
    } catch (Error e) {
      logger.error(e);
    } catch (Throwable e) {
      logger.error(e);
    }
  }

  private void writeOutGPX(Writer out, List<Datagram> datagrams) throws IOException {
    Element trkseg = new Element("trkseg", namespace);
    trk.addContent(trkseg);
    for (Datagram datagram : datagrams) {
      if (datagram instanceof NMEARMCSentence) {
        NMEARMCSentence gprmc = (NMEARMCSentence) datagram;
        if (gprmc.getLocation() != null) {
          Element trkpt = new Element("trkpt", namespace);
          trkpt.setAttribute("lat", Double.toString(gprmc.getLocation().getLatitude()));
          trkpt.setAttribute("lon", Double.toString(gprmc.getLocation().getLongitude()));
          if (gprmc.getDateTime() != null) {
            trkpt.addContent(new Element("time", namespace).setText(df.format(gprmc.getDateTime())));
          }
          trkpt.addContent(new Element("speed", namespace).setText(Long.toString(Math.round(gprmc.getSpeed()))));
          trkseg.addContent(trkpt);
        }
      }
    }
  }

  private void writeOutDAT(Writer out, List<Datagram> datagrams) throws IOException {
    for (Datagram datagram : datagrams) {
      out.write(datagram.getNMEAString());
      out.write("\r\n");
    }
  }

  private void writeOutNMEA(Writer out, List<Datagram> datagrams) throws IOException {
    for (Datagram datagram : datagrams) {
      out.write(datagram.getRawData());
      out.write("\r\n");
    }
  }

  public void start(File exportFile) throws IOException {
    out = new BufferedWriter(new FileWriter(exportFile, true));

    TimeZone tz = TimeZone.getTimeZone("UTC");
    df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    df.setTimeZone(tz);

    if (exportType.equals(EXPORT_TYPES.GPX)) {
      Element gpx = new Element("gpx");
      gpx.setAttribute("version", "1.0");
      gpx.setAttribute("creator", String.format("%s %s - %s", CommonInformation.APPLICATION_NAME,
          CommonInformation.RELEASE_VERSION, CommonInformation.APPLICATION_URL));

      namespace = Namespace.getNamespace("http://www.topografix.com/GPX/1/0");
      gpx.setNamespace(namespace);

      Namespace XSI = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      gpx.addNamespaceDeclaration(XSI);

      Namespace topografix = Namespace.getNamespace("topografix",
          "http://www.topografix.com/GPX/Private/TopoGrafix/0/1");
      gpx.addNamespaceDeclaration(topografix);

      gpx.setAttribute("schemaLocation",
          "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.topografix.com/GPX/Private/TopoGrafix/0/1 http://www.topografix.com/GPX/Private/TopoGrafix/0/1/topografix.xsd",
          XSI);

      doc = new Document(gpx);

      Element time = new Element("time", namespace).setText(df.format(new Date()));
      doc.getRootElement().addContent(time);

      trk = new Element("trk", namespace);
      doc.getRootElement().addContent(trk);
    }
  }

  public void stop() throws IOException {
    if (exportType.equals(EXPORT_TYPES.GPX)) {

      XMLOutputter xmlOutput = new XMLOutputter();
      // display nice nice
      xmlOutput.setFormat(Format.getPrettyFormat());
      xmlOutput.output(doc, out);
    }
    out.close();
  }

}
