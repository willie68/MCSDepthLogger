/**
 * MCS Media Computer Software
 * Copyright (C) 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: Track.java
 * EMail: W.Klaas@gmx.de
 * Created: 01.02.2014 Willie
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import de.mcs.utils.Files;
import de.mcs.utils.FolderZipper;
import de.mcs.utils.ZipExtracter;

/**
 * @author Willie
 * 
 */
public class Track {

  private static final String TRACK_FILE = "track.nmea";
  private static final String DATA_FILES = "dataFiles";
  private static final String DATA_MD5 = "dataMD5";
  private static final String NAME = "name";
  private static final String COMMENT = "comment";
  private static final String DATA_FILE = "datafile.";
  private static final String DATA_FILE_MD5 = "datafile.md5.";
  private static final String MAP_FILE = "mapFile";
  private static final String UPLOADED = "uploaded";
  private static final String DELETE_SOURCE = "sourceDelete";
  private static File basepath;
  private File zipFile;
  private File tempFolder;
  private PropertiesConfiguration configuration;
  private boolean isDirty;
  private boolean isNew;
  private boolean compressed;

  class DataFile {
    String originalName;
    String trackName;
  }

  /**
   * @throws IOException
   * @throws ConfigurationException
   * 
   */
  public Track(File zipFile) throws IOException, ConfigurationException {
    this();
    initTrack(zipFile);
  }

  public Track() throws IOException {
    isNew = true;
    isDirty = false;
    compressed = true;
    configuration = new PropertiesConfiguration();
    tempFolder = Files.createTempDirectory(new File(Files.getTempPath()));
  }

  public void initTrack(File file) throws IOException, ConfigurationException {
    this.zipFile = file;
    if (zipFile.exists()) {
      expand();
      isNew = false;
    }
    File propertyFile = new File(tempFolder, "route.properties");
    if (configuration == null) {
      configuration = new PropertiesConfiguration(propertyFile);
    } else {
      configuration.setFile(propertyFile);
      if (propertyFile.exists()) {
        configuration.load();
      }
    }
    configuration.setAutoSave(true);
  }

  public void compress() throws IOException {
    if (tempFolder.exists()) {
      if (isDirty) {
        FolderZipper.zipFolder(tempFolder.getAbsolutePath(), zipFile.getAbsolutePath());
        Files.remove(tempFolder, true);
        isDirty = false;
        compressed = true;
      }
    }
  }

  public void expand() {
    if (!tempFolder.exists()) {
      tempFolder.mkdirs();
    }
    if ((zipFile != null) && zipFile.exists()) {
      try {
        ZipExtracter extracter = new ZipExtracter(zipFile, tempFolder);
        extracter.extractAll();
        compressed = false;
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void addDataFile(File file, String md5) throws IOException {
    String myMD5 = md5;
    if (isCompressed()) {
      expand();
    }
    if (myMD5 == null || myMD5.equals("")) {
      myMD5 = Files.computeMD5FromFile(file);
    }
    String[] md5Array = configuration.getStringArray(DATA_MD5);
    String[] stringArray = configuration.getStringArray(DATA_FILES);
    int i = 0;
    File newFile;
    String name;
    do {
      name = String.format("%04d_", i) + file.getName();
      newFile = new File(tempFolder, name);
      i++;
    } while (newFile.exists());

    if (!newFile.exists()) {
      if (!file.getParent().equalsIgnoreCase(tempFolder.getCanonicalPath())) {
        Files.fileCopy(file, newFile);
      }
    }

    configuration.setProperty(DATA_FILE + name, file.getName());
    configuration.setProperty(DATA_FILE_MD5 + name, myMD5);
    List<String> dataFiles = new ArrayList<>(Arrays.asList(stringArray));
    if (!dataFiles.contains(name)) {
      dataFiles.add(name);
      configuration.setProperty(DATA_FILES, dataFiles.toArray(new String[0]));
    }

    List<String> dataMD5 = new ArrayList<>(Arrays.asList(md5Array));
    if (!dataMD5.contains(myMD5)) {
      dataMD5.add(myMD5);
      configuration.setProperty(DATA_MD5, dataMD5.toArray(new String[0]));
    }
    setMapFile(null);
    isDirty = true;
  }

  public boolean isCompressed() {
    return compressed;
  }

  public boolean isExpanded() {
    return !compressed;
  }

  /**
   * Setting the name of the track.
   * 
   * @param name
   * @throws ConfigurationException
   * @throws IOException
   */
  public void setName(String name) throws ConfigurationException, IOException {
    String newName = name.replaceAll("\\\\", "/");
    if (isNew) {
      zipFile = new File(basepath, newName + ".zip");
      initTrack(zipFile);
    }
    if (newName.lastIndexOf("/") >= 0) {
      newName = newName.substring(newName.lastIndexOf("/") + 1);
    }
    String oldName = configuration.getString(NAME);
    if (oldName != null && oldName.equals(newName)) {
      return;
    }
    configuration.setProperty(NAME, newName);
    isDirty = true;
  }

  public static void setBasePath(String trackFolder) {
    basepath = new File(trackFolder);
  }

  public String getName() {
    return configuration.getString(NAME, "");
  }

  /**
   * Closing the track. Saving configuration, compressing all into the zip file
   * if necessary and removing the temp folder.
   * 
   * @throws IOException
   * @throws ConfigurationException
   */
  public void freeResources() throws IOException, ConfigurationException {
    if (isDirty) {
      configuration.save();
      compress();
    }
    Files.remove(tempFolder, true);
    tempFolder = null;
  }

  public void setComment(String text) {
    String oldText = configuration.getString(COMMENT);
    if (oldText != null && oldText.equals(text)) {
      return;
    }
    configuration.setProperty(COMMENT, text);
    isDirty = true;
  }

  public String getComment() {
    return configuration.getString(COMMENT, "");
  }

  public boolean hasDataFileMD5(String md5) {
    if (md5 != null) {
      String[] md5Array = configuration.getStringArray(DATA_MD5);
      for (String myMd5 : md5Array) {
        if ((myMd5 != null) && myMd5.equalsIgnoreCase(md5)) {
          return true;
        }
      }
    }
    return false;
  }

  public List<String> getDisplayFileNames() {
    String[] stringArray = configuration.getStringArray(DATA_FILES);
    List<String> dataFiles = new ArrayList<>();
    for (String filename : stringArray) {
      String displayName = configuration.getString(DATA_FILE + filename);
      dataFiles.add(String.format("%s (%s)", filename, displayName));
    }
    return dataFiles;
  }

  public List<File> getDataFiles() {
    String[] stringArray = configuration.getStringArray(DATA_FILES);
    List<File> dataFiles = new ArrayList<>();
    for (String filename : stringArray) {
      dataFiles.add(getFile(filename));
    }
    return dataFiles;
  }

  public void removeFile(String filename) {
    String[] stringArray = configuration.getStringArray(DATA_FILES);
    List<String> dataFiles = new ArrayList<>(Arrays.asList(stringArray));
    if (dataFiles.contains(filename)) {
      dataFiles.remove(dataFiles.indexOf(filename));
      File file = new File(tempFolder, filename);
      file.delete();
      configuration.setProperty(DATA_FILES, dataFiles.toArray(new String[0]));
      configuration.setProperty(DATA_FILE + filename, null);
      configuration.setProperty(DATA_FILE_MD5 + filename, null);
      setMapFile(null);
      isDirty = true;
    }
  }

  public void setDataFilesOrder(Object[] array) {
    List<String> list = new ArrayList<>();
    for (Object object : array) {
      String remove = object.toString();
      String filename = remove.substring(0, remove.indexOf("(") - 1).trim();
      list.add(filename);
    }
    configuration.setProperty(DATA_FILES, list.toArray(new String[0]));
    isDirty = true;
  }

  public File getFile(String filename) {
    return new File(tempFolder, filename);
  }

  public File getMapFile() {
    String mapFile = configuration.getString(MAP_FILE);
    if (mapFile == null) {
      return null;
    }
    return new File(tempFolder, mapFile);
  }

  public void setMapFile(File file) {
    File newFile = file;
    if (file == null) {
      configuration.setProperty(MAP_FILE, null);
      isDirty = true;
    } else {
      try {
        String parent = file.getParentFile().getCanonicalPath();
        String temp = tempFolder.getCanonicalPath();
        if (!parent.equalsIgnoreCase(temp)) {
          newFile = new File(tempFolder, TRACK_FILE);
          Files.fileCopy(file, newFile);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      configuration.setProperty(MAP_FILE, newFile.getName());
      isDirty = true;
    }
  }

  public File createNewMapFile() {
    if (isCompressed()) {
      expand();
    }
    File mapFile = new File(tempFolder, TRACK_FILE);
    return mapFile;
  }

  public boolean isUploaded() {
    return configuration.getBoolean(UPLOADED, false);
  }

  public void setUploaded(boolean b) {
    configuration.setProperty(UPLOADED, b);
    isDirty = true;
  }

  public boolean isDeleteSource() {
    return configuration.getBoolean(DELETE_SOURCE, false);
  }

  public void setDeleteSource(boolean delete) {
    configuration.setProperty(DELETE_SOURCE, delete);
  }
}
