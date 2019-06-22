/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: FirmwareVersion.java
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
package de.mcs.depth.utils;

import java.io.File;
import java.io.IOException;

import de.mcs.mcu.utils.Hex2Bin;
import de.mcs.utils.Files;
import de.mcs.utils.Version;

/**
 * @author w.klaas
 * 
 */
public class FirmwareVersion {

  private File file;
  private File newBootloaderFile;
  private Version version;
  private File firmwareVersionFile;

  public FirmwareVersion(File firmwareFile) {
    this.file = firmwareFile;
    firmwareVersionFile = Files.changeExtension(file, ".VER");
    this.version = new Version("0.0.0");
    if (firmwareFile.exists() && firmwareVersionFile.exists()) {
      String versionString = Files.readFileToString(firmwareVersionFile);
      version = new Version(versionString.trim());
    }
    newBootloaderFile = new File(firmwareFile.getParent(), String.format("OSMFW%03d.BIN", version.getRelease()));
  }

  public boolean exists() {
    return file.exists();
  }

  public Version getVersion() {
    return version;
  }

  public void writeFirmware(File destinationPath) throws IOException {
    if (exists()) {
      Files.fileCopy(file, destinationPath);
      File destVersionFile = new File(destinationPath, firmwareVersionFile.getName());
      Files.writeStringToFile(destVersionFile, version.toString());
      writeBootloaderFile();
      Files.fileCopy(newBootloaderFile, destinationPath);
    }
  }

  public void setVersion(Version webVersion) throws IOException {
    if (webVersion == null) {
      this.version = new Version("0.0.0.0");
    } else {
      this.version = webVersion;
    }

    String versionString = "";
    if (version != null) {
      versionString = version.toString();
    }
    Files.writeStringToFile(firmwareVersionFile, versionString);
  }

  public File getFile() {
    return file;
  }

  public void writeBootloaderFile() {
    if (file.exists() && !newBootloaderFile.exists()) {
      Hex2Bin.convert(file, newBootloaderFile);
    }
  }

  @Override
  public String toString() {
    return file + "," + version.toString();
  }

}
