/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: DepthLoggerConfig.java
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
package de.mcs.depth.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.mcs.utils.Files;
import de.mcs.utils.Version;

/**
 * @author w.klaas
 * 
 */
public class DepthLoggerConfig {

  private Logger logger = Logger.getLogger(getClass());

  public enum Channel {
    Channel_A, Channel_B
  }

  public final static String[] BAUDRATES = { "inactive", "1200", "2400", "4800", "9600", "19200" };

  private File file;
  // this is the from logger processed file
  private boolean processedFile;
  // Seatalk is active for this logger
  private boolean seatalkActive;
  // baudrate for channel a
  //
  private int baud_a;
  // baudrate for channel b
  private int baud_b;
  // outputformats
  private int outputs;

  private String versionStr;

  private boolean writeBoardSupply;

  private boolean writeGyroData;

  private int vesselid;

  private File configFile;

  private int normvoltage;

  private int bootloaderversion;

  private int bootloadercrc;
  private int autoAdapt;

  public DepthLoggerConfig(File file) {
    this.file = file;
    this.setSeatalkActive(false);
    this.baud_a = 3;
    this.baud_b = 3;
    this.outputs = 2;
    this.vesselid = -1;
    this.normvoltage = -1;
    this.bootloaderversion = -1;
    this.bootloadercrc = -1;
    if (isLoggerConfig()) {
      parseLoggerConfig(file);
    }
  }

  private void parseLoggerConfig(File file) {
    try {
      this.setSeatalkActive(false);
      boolean isConfigFile = false;
      if (file.getName().equalsIgnoreCase("config.dat")) {
        isConfigFile = true;
      }
      List<String> fileContent = Files.readFileToList(file);
      int index = 0;
      for (String line : fileContent) {
        switch (index) {
        case 0:
          String myLine = line;
          if (myLine.startsWith("s")) {
            this.setSeatalkActive(true);
            myLine = myLine.substring(1);
          }
          baud_a = Integer.parseInt(myLine);
          break;
        case 1:
          baud_b = Integer.parseInt(line);
          break;
        case 2:
          outputs = Integer.parseInt(line);
          break;
        case 3:
          if (!isConfigFile) {
            versionStr = line;
            break;
          }
        case 4:
          try {
            vesselid = Integer.parseInt(line, 16);
          } catch (Exception e) {
            logger.error("Error parsing vesselid: " + line);
            vesselid = -1;
          }
          break;
        case 5:
          // norm voltage
          try {
            normvoltage = Integer.parseInt(line);
          } catch (Exception e) {
            logger.error("Error parsing normal voltage: " + line);
            normvoltage = -1;
          }
          break;
        case 6:
          // bootloader version
          try {
            bootloaderversion = Integer.parseInt(line);
          } catch (Exception e) {
            logger.error("Error parsing bootloader version: " + line);
            bootloaderversion = -1;
          }
          break;
        case 7:
          // bootloader crc
          try {
            bootloadercrc = Integer.parseInt(line, 16);
          } catch (Exception e) {
            logger.error("Error parsing bootloader crc: " + line);
            bootloadercrc = -1;
          }
          break;
        default:
          break;
        }
        index++;
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    checkParameter();
  }

  private void checkParameter() {
    if ((baud_a < 0) || (baud_a > 5)) {
      baud_a = 3;
    }
    if ((baud_b < 0) || (baud_b > 3)) {
      baud_b = 3;
    }
    setWriteBoardSupply((outputs & 0x01) > 0);
    setWriteGyroData((outputs & 0x02) > 0);
  }

  public boolean isLoggerConfig() {
    if (file.getName().equalsIgnoreCase("config.dat")) {
      processedFile = false;
    } else {
      processedFile = true;
    }

    return file.exists();
  }

  public String getBaudString(Channel channel) {
    switch (channel) {
    case Channel_A:
      return BAUDRATES[baud_a];
    case Channel_B:
      return BAUDRATES[baud_b];
    default:
    }
    return null;
  }

  /**
   * @return the writeGyroData
   */
  public boolean isWriteGyroData() {
    return writeGyroData;
  }

  /**
   * @param writeGyroData
   *          the writeGyroData to set
   */
  public void setWriteGyroData(boolean writeGyroData) {
    this.writeGyroData = writeGyroData;
  }

  /**
   * @return the writeBoardSupply
   */
  public boolean isWriteBoardSupply() {
    return writeBoardSupply;
  }

  /**
   * @param writeBoardSupply
   *          the writeBoardSupply to set
   */
  public void setWriteBoardSupply(boolean writeBoardSupply) {
    this.writeBoardSupply = writeBoardSupply;
  }

  public Version getLoggerVersion() {
    Version version = new Version();
    if (versionStr != null) {
      versionStr = versionStr.trim();
      if (versionStr.startsWith("V")) {
        versionStr = versionStr.substring(1).trim();
      }
      version.parseString(versionStr);
    }
    return version;
  }

  public void writeConfig() throws IOException {
    writeConfig(getConfigFile());
  }

  public void writeConfig(File file) throws IOException {
    List<String> list = new ArrayList<>();
    String line = "";
    if (isSeatalkActive()) {
      line = "s";
    }
    line = line + Integer.toString(baud_a);
    list.add(line);
    list.add(Integer.toString(baud_b));
    outputs = 0;
    if (isWriteBoardSupply()) {
      outputs += 1;
    }
    if (isWriteGyroData()) {
      outputs += 2;
    }
    list.add(Integer.toString(outputs));
    if (vesselid > -1) {
      list.add(String.format("%08X", vesselid));
    }
    Files.writeListToFile(file, list);
  }

  /**
   * @return the seatalkActive
   */
  public boolean isSeatalkActive() {
    return seatalkActive;
  }

  /**
   * @param seatalkActive
   *          the seatalkActive to set
   */
  public void setSeatalkActive(boolean seatalkActive) {
    this.seatalkActive = seatalkActive;
  }

  /**
   * @return the vesselid
   */
  public int getVesselid() {
    return vesselid;
  }

  /**
   * @param vesselid
   *          the vesselid to set
   */
  public void setVesselid(int vesselid) {
    this.vesselid = vesselid;
  }

  public int getBaudRate(Channel channel) {
    int baudValue = 0;
    switch (channel) {
    case Channel_A:
      baudValue = baud_a;
      break;
    case Channel_B:
      baudValue = baud_b;
      break;
    default:
      break;
    }
    if (baudValue != 0) {
      baudValue = Integer.parseInt(BAUDRATES[baudValue]);
    }
    return baudValue;
  }

  public void setBaudRate(Channel channel, int baudrate) {
    int baud = 0;
    switch (baudrate) {
    case 1200:
      baud = 1;
      break;
    case 2400:
      baud = 2;
      break;
    case 4800:
      baud = 3;
      break;
    case 9600:
      baud = 4;
      break;
    case 19200:
      baud = 5;
      break;
    }
    if (channel.equals(Channel.Channel_A)) {
      baud_a = baud;
    } else if (channel.equals(Channel.Channel_B)) {
      baud_b = baud;
    }
  }

  public File getLoggerFile() {
    return file;
  }

  public File getConfigFile() {
    configFile = new File(file.getParent(), "config.dat");
    return configFile;
  }

  public boolean isProcessedFile() {
    return processedFile;
  }

  public void readConfigFile() {
    getConfigFile();
    if (configFile.exists()) {
      parseLoggerConfig(configFile);
    }
  }

  public void writeDescriptiveConfig(File file) throws IOException {
    Properties props = new Properties();
    props.setProperty("seatalkActive", Boolean.toString(isSeatalkActive()));
    props.setProperty("baud_a", BAUDRATES[baud_a]);
    props.setProperty("baud_b", BAUDRATES[baud_b]);
    props.setProperty("gyroWrite", Boolean.toString(isWriteGyroData()));
    props.setProperty("supplyWrite", Boolean.toString(isWriteBoardSupply()));
    props.setProperty("openseamap.vesselID", Integer.toString(vesselid));
    FileWriter writer = new FileWriter(file);
    props.store(writer, "");
    writer.close();
  }

  public int getBootloaderCRC() {
    return bootloadercrc;
  }

  public int getBootloaderVersion() {
    return bootloaderversion;
  }

  public int getNormVoltage() {
    return normvoltage;
  }

}
