/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: DriveInfo.java
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

/**
 * @author w.klaas
 * 
 */
public class DriveInfo {
  public static enum DRIVE_TYPES {
    UNKNOWN, LOCAL, CD_DVD, REMOVABLE, NETWORK, RAM
  }

  public static enum FILE_SYSTEM_TYPES {
    UNKNOWN, FAT, FAT32, NTFS, EXTFAT, CDFS
  }

  private String displayName;
  private File file;
  private String name;
  private DRIVE_TYPES driveType;
  private FILE_SYSTEM_TYPES fileSystem;

  public DriveInfo(String name, String systemDisplayName, File file) {
    this.name = name;
    this.displayName = systemDisplayName;
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public String toString() {
    return displayName;
  }

  public String getName() {
    return name;
  }

  public void setDriveType(DRIVE_TYPES driveType) {
    this.driveType = driveType;
  }

  public DRIVE_TYPES getDriveTypes() {
    return driveType;
  }

  public void setFileSystem(FILE_SYSTEM_TYPES string) {
    this.fileSystem = string;
  }

  public FILE_SYSTEM_TYPES getFileSystem() {
    return fileSystem;
  }
}
