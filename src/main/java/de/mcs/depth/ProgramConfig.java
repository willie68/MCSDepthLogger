/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: ProgramConfig.java
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
package de.mcs.depth;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;

import de.mcs.depth.Constants.BOOTLOADERVERSION;
import de.mcs.depth.Constants.MEASURE_TYPE;
import de.mcs.depth.mapviewer.TileServerInfo;
import de.mcs.utils.Files;
import de.mcs.utils.StringUtils;
import de.mcs.utils.Version;

public class ProgramConfig {

  private PropertiesConfiguration config;

  private static final int AUTO_ADAPT_TRUE = 1;
  private static final int AUTO_ADAPT_FALSE = 0;
  private static final int AUTO_ADAPT_UNAKS = -1;

  private static final String PREFIX = "de.mcs.depth.logger.";
  private static final String CONFIG_PREFIX = PREFIX + "session.";
  private static final String MAIN_WIDTH = CONFIG_PREFIX + "mainwidth";
  private static final String MAIN_HEIGHT = CONFIG_PREFIX + "mainheight";
  private static final String MAIN_TOP = CONFIG_PREFIX + "maintop";
  private static final String MAIN_LEFT = CONFIG_PREFIX + "mainleft";
  private static final String WIDTH = ".width";
  private static final String HEIGHT = ".height";
  private static final String TOP = ".top";
  private static final String LEFT = ".left";
  private static final String FIRMWARE_PREFIX = CONFIG_PREFIX + "firmware.";
  private static final String FIRMWARE_VERSION = FIRMWARE_PREFIX + "version";
  private static final String UPLOAD_URL = CONFIG_PREFIX + "uploadUrl";
  private static final String UPLOAD_USERNAME = CONFIG_PREFIX + "uploadUser";
  private static final String UPLOAD_PASSWORD = CONFIG_PREFIX + "uploadPassword";
  private static final String UPLOAD_BATCH = CONFIG_PREFIX + "uploadBatch";
  private static final String INTERNAL_PROCESSING = CONFIG_PREFIX + "internalProcessing";
  private static final String AUTO_ADAPT = CONFIG_PREFIX + "autoAdapt";

  private static final String SHOW_MAP_IN = CONFIG_PREFIX + "showMapIn";
  private static final String SHOW_MAP_PRG = CONFIG_PREFIX + "showMapPrg";
  private static final String MAX_MAP_POINTS = CONFIG_PREFIX + "maxMapPoints";
  private static final String MAP_HEIGHT = CONFIG_PREFIX + "mapHeight";
  private static final String TRACK_FOLDER = CONFIG_PREFIX + "trackFolder";
  private static final String TRACK_DIVIDER = CONFIG_PREFIX + "trackDivider";
  private static final String MAP_PROVIDER = CONFIG_PREFIX + "mapProvider";
  private static final String PREFIX_TILESERVER = CONFIG_PREFIX + "tileServer";
  private static final String BOOTLOADER_VERSION = CONFIG_PREFIX + "bootloaderVersion";
  private static final String CACHE_FOLDER = CONFIG_PREFIX + "cacheFolder";
  private static final String IMPORT_PATH = CONFIG_PREFIX + "importPath";

  private static final String MEASURE_SYSTEM = CONFIG_PREFIX + "measureSystem";

  private static ProgramConfig instance = new ProgramConfig();

  private Logger log = Logger.getLogger(getClass());

  public static ProgramConfig getInstance() {
    return instance;
  }

  public ProgramConfig() {
    this(null);
  }

  public ProgramConfig(File configFile) {
    System.setProperty("java.net.useSystemProxies", "true");
    if (configFile == null) {
      File path = Application.getInstance().getContext().getLocalStorage().getDirectory();
      configFile = new File(path, "client.config");
    }
    openConfigFile(configFile);
  }

  private void openConfigFile(File configFile) {
    try {
      config = new PropertiesConfiguration(configFile);
    } catch (ConfigurationException e) {
      log.error(e);
    }
    config.setAutoSave(true);
  }

  public void saveMainFrameData(Rectangle bounds) {
    config.setProperty(MAIN_WIDTH, (int) bounds.width);
    config.setProperty(MAIN_HEIGHT, (int) bounds.height);
    config.setProperty(MAIN_TOP, (int) bounds.y);
    config.setProperty(MAIN_LEFT, (int) bounds.x);
  }

  public Rectangle getMainFrameData() {
    Rectangle bounds = new Rectangle();
    int x = config.getInt(MAIN_LEFT, 100);
    int y = config.getInt(MAIN_TOP, 100);
    int width = config.getInt(MAIN_WIDTH, 800);
    int height = config.getInt(MAIN_HEIGHT, 600);
    bounds.setBounds(x, y, width, height);
    return bounds;
  }

  public Rectangle getFrameData(String key) {
    String nlsKeyPrefix = CONFIG_PREFIX + key;
    Rectangle bounds = new Rectangle();
    int x = config.getInt(nlsKeyPrefix + LEFT, 100);
    int y = config.getInt(nlsKeyPrefix + TOP, 100);
    int width = config.getInt(nlsKeyPrefix + WIDTH, 800);
    int height = config.getInt(nlsKeyPrefix + HEIGHT, 600);
    bounds.setBounds(x, y, width, height);
    return bounds;
  }

  public void saveFrameData(String key, Rectangle bounds) {
    String nlsKeyPrefix = CONFIG_PREFIX + key;
    config.setProperty(nlsKeyPrefix + WIDTH, (int) bounds.width);
    config.setProperty(nlsKeyPrefix + HEIGHT, (int) bounds.height);
    config.setProperty(nlsKeyPrefix + TOP, (int) bounds.y);
    config.setProperty(nlsKeyPrefix + LEFT, (int) bounds.x);
  }

  public Version getFirmwareVersion() {
    Version version = new Version();
    String versionStr = config.getString(FIRMWARE_VERSION);
    if (versionStr != null) {
      version.parseString(versionStr);
    }
    return version;
  }

  public void setUploadURL(String text) {
    config.setProperty(UPLOAD_URL, text);
  }

  public void setUploadUsername(String text) {
    config.setProperty(UPLOAD_USERNAME, text);
  }

  public void setUploadPassword(String encrypt) {
    config.setProperty(UPLOAD_PASSWORD, encrypt);
  }

  public String getUploadURL() {
    return config.getString(UPLOAD_URL, "");
  }

  public String getUploadUsername() {
    return config.getString(UPLOAD_USERNAME, "");
  }

  public String getUploadPassword() {
    return config.getString(UPLOAD_PASSWORD, "");
  }

  public void setUploadBatch(String selectedFilePath) {
    config.setProperty(UPLOAD_BATCH, selectedFilePath);
  }

  public String getUploadBatch() {
    return config.getString(UPLOAD_BATCH, "");
  }

  public void setInternalProcessing(boolean selected) {
    config.setProperty(INTERNAL_PROCESSING, selected);
  }

  public boolean isInternalProcessing() {
    return config.getBoolean(INTERNAL_PROCESSING, true);
  }

  public void setShowMapIn(String showMapIn) {
    config.setProperty(SHOW_MAP_IN, showMapIn);
  }

  public String getShowMapIn() {
    return config.getString(SHOW_MAP_IN, Constants.SHOW_IN_TYPE.BROWSER.name());
  }

  public void setShowMapProgram(String showMapProgram) {
    config.setProperty(SHOW_MAP_PRG, showMapProgram);
  }

  public String getShowMapProgram() {
    return config.getString(SHOW_MAP_PRG, "");
  }

  public void setMaxMapPoints(long maxMapPoints) {
    config.setProperty(MAX_MAP_POINTS, maxMapPoints);
  }

  public int getMaxMapPoints() {
    return config.getInt(MAX_MAP_POINTS, 1000);
  }

  /**
   * @param height2
   */
  public void setMapHeight(int height) {
    config.setProperty(MAP_HEIGHT, height);
  }

  public int getMapHeight() {
    return config.getInt(MAP_HEIGHT, 400);
  }

  /**
   * @return
   */
  public String getCacheFolder() {
    return config.getString(CACHE_FOLDER, new File(Files.getAppData(), "MCSDepthLogger/cache").getAbsolutePath());
  }

  /**
  *
  */
  public void setCacheFolder(String folder) {
    config.setProperty(CACHE_FOLDER, folder);
  }

  /**
   * @return
   */
  public String getTrackFolder() {
    return config.getString(TRACK_FOLDER, new File(Files.getAppData(), "MCSDepthLogger").getAbsolutePath());
  }

  /**
   *
   */
  public void setTrackFolder(String folder) {
    config.setProperty(TRACK_FOLDER, folder);
  }

  /**
   * @param dividerLocation
   */
  public void setTrackDivider(int dividerLocation) {
    config.setProperty(TRACK_DIVIDER, dividerLocation);
  }

  /**
   * @param dividerLocation
   */
  public int getTrackDivider() {
    return config.getInt(TRACK_DIVIDER, 200);
  }

  public void setMapProvider(String mapProvider) {
    config.setProperty(MAP_PROVIDER, mapProvider);
  }

  public String getMapProvider() {
    return config.getString(MAP_PROVIDER);
  }

  public List<TileServerInfo> getTileServerInfos() {
    List<TileServerInfo> list = new ArrayList<>();
    for (@SuppressWarnings("unchecked")
    Iterator<String> iterator = config.getKeys(PREFIX_TILESERVER); iterator.hasNext();) {
      String key = iterator.next();
      String[] strings = config.getStringArray(key);
      String server = StringUtils.arrayToCSVString(strings, ',', '\'');
      TileServerInfo tileServerInfo = new TileServerInfo(server);
      list.add(tileServerInfo);
    }
    return list;
  }

  public void setBootloaderVersion(BOOTLOADERVERSION version) {
    config.setProperty(BOOTLOADER_VERSION, version.name());
  }

  public BOOTLOADERVERSION getBootloaderVersion() {
    String versionName = config.getString(BOOTLOADER_VERSION);
    BOOTLOADERVERSION version = BOOTLOADERVERSION.FAT16;
    if (versionName != null) {
      version = BOOTLOADERVERSION.valueOf(versionName);
    }
    return version;
  }

  public void setMeasureSystem(String measureSystem) {
    config.setProperty(MEASURE_SYSTEM, measureSystem);
  }

  public MEASURE_TYPE getMeasureSystem() {
    String name = config.getString(MEASURE_SYSTEM, MEASURE_TYPE.METRICAL.name());
    try {
      return MEASURE_TYPE.valueOf(name);
    } catch (Exception e) {
      return MEASURE_TYPE.METRICAL;
    }
  }

  public File getImportPath() {
    try {
      String filename = config.getString(IMPORT_PATH);
      if (filename != null) {
        return new File(filename);
      }
    } catch (Exception e) {
      log.error(e);
    }
    return null;
  }

  public void setImportPath(File file) {
    config.setProperty(IMPORT_PATH, file.getAbsolutePath());
  }

  public void setAutoAdapt(boolean value) {
    if (value) {
      config.setProperty(AUTO_ADAPT, AUTO_ADAPT_TRUE);
    } else {
      config.setProperty(AUTO_ADAPT, AUTO_ADAPT_FALSE);
    }
  }

  public boolean isAutoAdapt() {
    int value = config.getInt(AUTO_ADAPT, AUTO_ADAPT_UNAKS);
    return value == AUTO_ADAPT_TRUE;
  }

  public boolean isAutoAdaptUnAsk() {
    int value = config.getInt(AUTO_ADAPT, AUTO_ADAPT_UNAKS);
    return value == AUTO_ADAPT_UNAKS;
  }
}
