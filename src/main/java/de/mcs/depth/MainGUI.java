/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: MainGUI.java
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mcs.CommonInformation;
import de.mcs.MCSWebVersion;
import de.mcs.depth.Constants.BOOTLOADERVERSION;
import de.mcs.depth.Constants.EXPORT_TYPES;
import de.mcs.depth.logger.DefaultSentenceFilter;
import de.mcs.depth.logger.DepthLoggerConfig;
import de.mcs.depth.logger.DepthLoggerConfig.Channel;
import de.mcs.depth.logger.LoggerFile;
import de.mcs.depth.logger.ProgressCallback;
import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMStartSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEARMCSentence;
import de.mcs.depth.utils.AdaptTimeTool;
import de.mcs.depth.utils.DataTool;
import de.mcs.depth.utils.DiscImagerTool;
import de.mcs.depth.utils.DriveInfo;
import de.mcs.depth.utils.DriveInfo.DRIVE_TYPES;
import de.mcs.depth.utils.DriveInfo.FILE_SYSTEM_TYPES;
import de.mcs.depth.utils.ExportTool;
import de.mcs.depth.utils.FileTableModel;
import de.mcs.depth.utils.FileTool;
import de.mcs.depth.utils.FirmwareVersion;
import de.mcs.depth.utils.GPXFileFilter;
import de.mcs.depth.utils.HTMLBuilder;
import de.mcs.depth.utils.ITranslator;
import de.mcs.depth.utils.LoggerFileFilter;
import de.mcs.depth.utils.LoggingTool;
import de.mcs.depth.utils.NMEAFileFilter;
import de.mcs.depth.utils.ShowMapTool;
import de.mcs.depth.utils.SimpleCallback;
import de.mcs.depth.utils.Track;
import de.mcs.depth.utils.TrackTreeModel.MyFileData;
import de.mcs.depth.utils.UploadTool;
import de.mcs.utils.Files;
import de.mcs.utils.StartProcess;
import de.mcs.utils.StreamHelper;
import de.mcs.utils.Version;
import de.mcs.utils.ZipExtracter;
import de.mcs.utils.caches.FileCache;

/**
 * 
 */
public class MainGUI extends SingleFrameApplication implements StatusAndProgress, ITranslator {
  private static final String HEX_OSMFIRMW = "OSMFIRMW.HEX";
  private static final String NLS_PREFIX = "main.gui.";
  private JPanel topPanel;
  private JComboBox<DriveInfo> driveLetters;
  private JLabel sdInfoLabel;
  private List<File> dataFiles;
  private JLabel statusLabel;
  private JProgressBar pbar;
  private JLabel fileInfoLabel;
  private JCheckBox autoAnalyse;
  private boolean autoanalyse = false;
  private Logger logger = Logger.getLogger(this.getClass());
  private DepthLoggerConfig loggerConfig;
  private Version mainVersion;
  private Object analyseLock = new Object();
  private JDialog waitPanel;
  private JLabel progressLabel;
  private JXTable fileTable;
  private FileTableModel tableModel;
  private Map<String, DriveInfo> drives;

  private File appDir;
  private File toolsDir;

  private MCSWebVersion firmwareWebVersion;
  private MCSWebVersion softwareWebVersion;
  private FileCache fileCache;
  private MapGUI mapGui;
  private JSplitPane routeSplitter;
  private TrackPanel trackPanel;
  private JScrollPane scrollPane;

  public static void main(String[] args) {
    System.setProperty("java.net.useSystemProxies", "true");
    System.setProperty("java.util.PropertyResourceBundle.encoding", "ISO-8859-1");
    // System.getProperties().list(System.out);
    try {
      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception e) {
      e.printStackTrace();
    }

    launch(MainGUI.class, args);
  }

  // ----- user actions, buttons, menus, etc. -----
  @Action
  public void viewFile() {
    final File file = getSelectedFile();
    if (file != null) {
      if (Desktop.isDesktopSupported()) {
        try {
          Desktop.getDesktop().open(file);
        } catch (IOException e) {
          logger.error(e);
        }
      }
    }
  }

  @Action
  public void saveFile() {
    final File file = getSelectedFile();
    if (file != null) {
      final File oldFile = new File(file.getParentFile(), file.getName() + ".old");
      if (oldFile.exists()) {
        setStatusbar(NLS_PREFIX + "statusbar.cantmove.short", NLS_PREFIX + "statusbar.cantmove.long");
        setProgress(0);
        Thread.yield();
        return;
      }

      Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
          synchronized (analyseLock) {
            DataTool.getInstance().saveDataFile(file, oldFile);
          }
        }

      }, "Save file");
      thread.start();
    }
  }

  @Action
  public void exitApp() {
    fileCache.clear();
    ProgramConfig.getInstance().setTrackDivider(routeSplitter.getDividerLocation());
    ProgramConfig.getInstance().saveMainFrameData(getMainFrame().getBounds());
    getMainFrame().setVisible(false);
    logger.info(String.format("stopping %s", CommonInformation.APPLICATION_NAME));
    exit();
  }

  @Action
  public void restoreBackup() {
    DriveInfo drive = (DriveInfo) driveLetters.getSelectedItem();
    RestoreBackupGUI gui = new RestoreBackupGUI(getMainFrame(), appDir, drive, toolsDir);
    gui.setModal(true);
    gui.setVisible(true);
  }

  @Action
  public void newTrack() {
    try {
      setStatusbar(NLS_PREFIX + "statusbar.adddatafile");

      Track track = new Track();
      List<File> selectedValuesList = getSelectedFiles();
      if (selectedValuesList != null && selectedValuesList.size() > 0) {
        for (int i = 0; i < selectedValuesList.size(); i++) {

          File file = selectedValuesList.get(i);
          try {
            track.addDataFile(file, null);
            setProgress(i * 100 / selectedValuesList.size());
          } catch (IOException e) {
            logger.error(e);
          }
        }
      }
      setProgress(0);

      EditTrackDialog gui = new EditTrackDialog(this.getMainFrame(), track);
      MyFileData node = trackPanel.getSelectedNode();
      if ((node != null) && node.isDirectory()) {
        String path = node.getNodePath();
        gui.setPath(path);
      }
      gui.setModal(true);
      gui.setVisible(true);

      if (gui.isApplied()) {
        if (track.isDeleteSource()) {
          int result = JOptionPane.NO_OPTION;
          result = JOptionPane.showConfirmDialog(this.getMainFrame(),
              getTranslatedString("main.gui.track.ask.delete.files.text"),
              getTranslatedString("main.gui.track.ask.delete.files.title"), JOptionPane.YES_NO_OPTION);
          if (result == JOptionPane.YES_OPTION) {
            StringBuilder filenames = new StringBuilder();
            boolean first = true;
            for (File file : selectedValuesList) {
              if (!first) {
                filenames.append(", ");
              }
              filenames.append(file.getName());
              file.delete();
              if (first) {
                first = false;
              }
            }
            logger.info(String.format("files deleted (%s)", filenames.toString()));
            DriveInfo drive = (DriveInfo) driveLetters.getSelectedItem();
            analyseSDCarddrive(drive);
          }
        }
      }

      logger.debug("compressing");
      track.freeResources();

      resetStatusbar();

      trackPanel.refresh();
    } catch (IOException e) {
      logger.error(e);
    } catch (ConfigurationException e) {
      logger.error(e);
    }
  }

  private File getSelectedFile() {
    if (fileTable.getSelectedRow() > -1) {
      return (File) fileTable.getValueAt(fileTable.getSelectedRow(), 0);
    }
    return null;
  }

  private List<File> getSelectedFiles() {
    int[] selectedRows = fileTable.getSelectedRows();
    List<File> list = new ArrayList<>();
    for (int i : selectedRows) {
      list.add((File) fileTable.getValueAt(i, 0));
    }
    return list;
  }

  @Action
  public void addTrack() {
    if (trackPanel.isTrackSelected()) {
      List<File> selectedValuesList = getSelectedFiles();// fileList.getSelectedValuesList();
      Track track = trackPanel.getSelectedTrack();
      List<File> alreadyAdded = new ArrayList<>();
      Map<File, String> md5s = new HashMap<>();

      showWait(true);
      Thread.yield();
      for (File file : selectedValuesList) {
        logger.debug("building track information");
        logger.debug("building md5 hash");
        String md5 = Files.computeMD5FromFile(file);
        md5s.put(file, md5);
        if (track.hasDataFileMD5(md5)) {
          alreadyAdded.add(file);
        }
      }
      showWait(false);
      Thread.yield();

      int result = JOptionPane.OK_OPTION;
      if (alreadyAdded.size() > 0) {
        result = JOptionPane.showConfirmDialog(
            this.getMainFrame(), getTranslatedString("main.gui.addtrack.md5.text",
                FileTool.getFileNameString(alreadyAdded), track.getName()),
            getTranslatedString("main.gui.addtrack.md5.title"), JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.NO_OPTION) {
          for (File file : alreadyAdded) {
            if (selectedValuesList.contains(file)) {
              selectedValuesList.remove(file);
            }
          }
          result = JOptionPane.OK_OPTION;
        }
      } else {
        result = JOptionPane.showConfirmDialog(
            this.getMainFrame(), getTranslatedString("main.gui.addtrack.ask.text",
                FileTool.getFileNameString(selectedValuesList), track.getName()),
            getTranslatedString("main.gui.addtrack.ask.title"), JOptionPane.OK_CANCEL_OPTION);
      }
      try {
        showWait(true);
        try {
          if (result == JOptionPane.OK_OPTION) {
            statusLabel.setText(getTranslatedString(NLS_PREFIX + "statusbar.adddatafile"));
            Thread.yield();
            for (File file : selectedValuesList) {
              logger.debug(String.format("adding data file \"%s\"", file.getName()));
              track.addDataFile(file, md5s.get(file));
            }
            if (track.isDeleteSource()) {
              result = JOptionPane.NO_OPTION;
              result = JOptionPane.showConfirmDialog(this.getMainFrame(),
                  getTranslatedString("main.gui.track.ask.delete.files.text"),
                  getTranslatedString("main.gui.track.ask.delete.files.title"), JOptionPane.YES_NO_OPTION);
              if (result == JOptionPane.YES_OPTION) {
                StringBuilder filenames = new StringBuilder();
                boolean first = true;
                for (File file : selectedValuesList) {
                  if (!first) {
                    filenames.append(", ");
                  }
                  filenames.append(file.getName());
                  file.delete();
                  if (first) {
                    first = false;
                  }
                }
                logger.info(String.format("files deleted (%s)", filenames.toString()));
                DriveInfo drive = (DriveInfo) driveLetters.getSelectedItem();
                analyseSDCarddrive(drive);
              }

            }
          }
          logger.debug("compressing");
          track.freeResources();
        } finally {
          showWait(false);
          statusLabel.setText(getTranslatedString(NLS_PREFIX + "statusbar.ready"));
          Thread.yield();
        }
      } catch (Exception e) {
        logger.error(e);
        JOptionPane.showMessageDialog(this.getMainFrame(),
            getTranslatedString("main.gui.addtrack.error.text", e.getLocalizedMessage()),
            getTranslatedString("main.gui.addtrack.error.title"), JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @Action
  public void about() {
    AboutGUI gui = new AboutGUI(getMainFrame());
    gui.setModal(true);
    gui.setVisible(true);
  }

  @Action
  public void help() {
    JHelp helpViewer = null;
    try {
      ClassLoader cl = this.getClass().getClassLoader();
      URL url = HelpSet.findHelpSet(cl, "jhelpset.hs");
      helpViewer = new JHelp(new HelpSet(cl, url));

      helpViewer.setCurrentID("introduction");

      JFrame frame = new JFrame();
      frame.setTitle(getTranslatedString(NLS_PREFIX + "help.windowtitle"));
      frame.setSize(800, 600);
      frame.getContentPane().add(helpViewer);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setVisible(true);
    } catch (Exception e) {
      logger.error("API Help Set not found");
    }
  }

  @Action
  public void refreshDrives() {
    buildDriveList();
    checkActions();
  }

  @Action
  public void analyseFile() {
    fullAnalyseDataFile(true);
  }

  @Action
  public void showMap() {
    final File sdFile = getSelectedFile();// fileList.getSelectedValue();
    if (sdFile != null) {
      fileCache.addFile(sdFile);
      final File dataFile = fileCache.getFile(sdFile);
      new Thread(new Runnable() {
        public void run() {
          convertAndShowMap(dataFile);
        }
      }, "showMap").start();
    }
  }

  @Action
  public void adaptDateTime() {
    int result = JOptionPane.showConfirmDialog(this.getMainFrame(), getTranslatedString("main.gui.adapttime.ask.text"),
        getTranslatedString("main.gui.adapttime.ask.title"), JOptionPane.YES_NO_CANCEL_OPTION);
    if (result != JOptionPane.CANCEL_OPTION) {
      final boolean allFiles = result == JOptionPane.YES_OPTION;
      new Thread(new Runnable() {
        public void run() {
          showWait(true);

          setStatusbar(NLS_PREFIX + "statusbar.adapttimestamp", null);
          Thread.yield();

          AdaptTimeTool.newAdaptTimeTool().setStatusAndProgress(MainGUI.this).setProcessAllFiles(allFiles)
              .setDataFiles(dataFiles).setFileCache(fileCache).doWork();

          fileCache.clear();

          resetStatusbar();
          showWait(false);
          Thread.yield();
        }
      }, "adaptTime").start();
    }
  }

  @Action
  public void importPath() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(getTranslatedString(NLS_PREFIX + "openPath.import"));
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setSelectedFile(ProgramConfig.getInstance().getImportPath());
    if (chooser.showOpenDialog(getMainFrame()) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      ProgramConfig.getInstance().setImportPath(file);
      DriveInfo info = new DriveInfo("local", file.getName(), file);
      info.setDriveType(DRIVE_TYPES.LOCAL);
      info.setFileSystem(FILE_SYSTEM_TYPES.UNKNOWN);
      analyseSDCarddrive(info);
    }
  }

  @Action
  public void export() {
    final List<File> sdFiles = getSelectedFiles(); // fileList.getSelectedValuesList();
    if ((sdFiles != null) && (sdFiles.size() > 0)) {
      // zunächst alle Dateien in den Cache übernehmen.
      for (File sdFile : sdFiles) {
        fileCache.addFile(sdFile);
      }

      JFileChooser fileChooser = new JFileChooser();
      FileFilter nmeaFileFilter = new NMEAFileFilter(getTranslatedString("export.file.filterName"));
      fileChooser.addChoosableFileFilter(nmeaFileFilter);
      FileFilter loggerFileFilter = new LoggerFileFilter(getTranslatedString("export.file.loggerFilterName"));
      fileChooser.addChoosableFileFilter(loggerFileFilter);
      FileFilter gpxFileFilter = new GPXFileFilter(getTranslatedString("export.file.gpxFilterName"));
      fileChooser.addChoosableFileFilter(gpxFileFilter);

      fileChooser.setAcceptAllFileFilterUsed(true);
      fileChooser.setFileFilter(nmeaFileFilter);
      int result = fileChooser.showSaveDialog(getMainFrame());

      if (result == JFileChooser.APPROVE_OPTION) {
        // caching file
        FileFilter fileFilter = fileChooser.getFileFilter();
        String ext = "";
        EXPORT_TYPES exporttype = EXPORT_TYPES.MCSLOGGER;
        if (fileFilter instanceof NMEAFileFilter) {
          ext = ".nmea";
          exporttype = EXPORT_TYPES.NMEA;
        }
        if (fileFilter instanceof LoggerFileFilter) {
          ext = ".dat";
          exporttype = EXPORT_TYPES.MCSLOGGER;
        }
        if (fileFilter instanceof GPXFileFilter) {
          ext = ".gpx";
          exporttype = EXPORT_TYPES.GPX;
        }

        final EXPORT_TYPES exptype = exporttype;

        File selectedFile = fileChooser.getSelectedFile();
        String filename = selectedFile.getName();
        if (!filename.endsWith(ext)) {
          filename = filename + ext;
        }

        final File exportFile = new File(selectedFile.getParentFile(), filename);

        if (exportFile.exists()) {
          result = JOptionPane.showConfirmDialog(this.getMainFrame(),
              getTranslatedString("main.gui.export.ask.nmeafile.overwrite.text"),
              getTranslatedString("main.gui.export.ask.nmeafile.overwrite.title"), JOptionPane.YES_NO_CANCEL_OPTION);
          if (result == JOptionPane.YES_OPTION) {
            exportFile.delete();
          } else if (result == JOptionPane.CANCEL_OPTION) {
            return;
          }
        }

        new Thread(new Runnable() {
          @Override
          public void run() {
            setWait(true);
            setProgress(0);
            setStatusbar(NLS_PREFIX + "statusbar.export", null);

            ExportTool exporter = new ExportTool(MainGUI.this, exptype);
            try {
              exporter.start(exportFile);
              for (File dataFile : sdFiles) {
                exporter.exportData(dataFile, exportFile);
              }
              exporter.stop();
            } catch (IOException e) {
              logger.error(e);
              JOptionPane.showMessageDialog(MainGUI.this.getMainFrame(),
                  getTranslatedString("main.gui.update.error.firmware.text"),
                  getTranslatedString("main.gui.update.error.firmware.title"), JOptionPane.ERROR_MESSAGE);
            }

            resetStatusbar();
            setWait(false);
          }
        }, "export").start();
      }
    }
  }

  @Action
  public void upload() {
    final List<File> files = getSelectedFiles(); // fileList.getSelectedValuesList();
    final String uploadURL = ProgramConfig.getInstance().getUploadURL();
    if ((uploadURL != null && !uploadURL.equals("")) && (files.size() > 0)) {

      int result = JOptionPane.showConfirmDialog(this.getMainFrame(), getTranslatedString("main.gui.upload.ask.text"),
          getTranslatedString("main.gui.upload.ask.title"), JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        Thread thread = new Thread(new Runnable() {

          @Override
          public void run() {
            UploadTool.getInstance().uploadDataFile(files, uploadURL, loggerConfig);
          }
        }, "Uploadfile");
        thread.start();
      }
    }
  }

  @Action
  public void config() {
    if (loggerConfig != null) {
      if (loggerConfig.isProcessedFile()) {
        loggerConfig.readConfigFile();
      }
    }
    ConfigGUI gui = new ConfigGUI(getMainFrame());
    gui.setModal(true);
    gui.setLoggerConfig(loggerConfig);
    gui.setVisible(true);
    if (gui.isApplied()) {
      setStatusbar(getTranslatedString("main.gui.statusbar.config.saved.text"),
          getTranslatedString("main.gui.statusbar.config.saved.tooltip"));
    }
  }

  /**
   * update the logger firmware
   */
  @Action
  public void update() {
    boolean error = false;
    DriveInfo drive = (DriveInfo) driveLetters.getSelectedItem();
    BOOTLOADERVERSION bootloaderVersion = ProgramConfig.getInstance().getBootloaderVersion();
    if (SystemUtils.IS_OS_WINDOWS && (drive != null)) {
      try {
        int result = JOptionPane.NO_OPTION;
        if (bootloaderVersion.equals(BOOTLOADERVERSION.FAT16)) {
          // nur bei bootloader Version 1 muss ein FAT Image erzeugt werden.
          boolean fileSystemOK = drive.getFileSystem().equals(FILE_SYSTEM_TYPES.FAT);
          if (!fileSystemOK) {
            result = JOptionPane.showConfirmDialog(this.getMainFrame(),
                getTranslatedString("main.gui.update.ask.overwrite.text"),
                getTranslatedString("main.gui.update.ask.overwrite.title"), JOptionPane.YES_NO_CANCEL_OPTION);
          }
          if (result == JOptionPane.CANCEL_OPTION) {
            return;
          }

          if (result == JOptionPane.YES_OPTION) {
            // image sichern
            backupImage(drive);
          }
          if (!error && !fileSystemOK) {
            // neues image aufspielen
            if (!restoreFATImage(drive)) {
              setStatusbar(getTranslatedString("main.gui.statusbar.ready"),
                  getTranslatedString("main.gui.statusbar.ready"));
              return;
            }
          }
        }

        // firmware downloaden
        result = JOptionPane.OK_OPTION;
        FirmwareVersion firmwareVersion = getActualFirmware();

        try {
          updateActualFirmware(firmwareVersion);
        } catch (IOException e) {
          if (firmwareVersion.exists()) {
            result = JOptionPane.showConfirmDialog(this.getMainFrame(),
                getTranslatedString("main.gui.firmware.update.error.download.use.text"),
                getTranslatedString("main.gui.firmware.update.error.download.use.title"), JOptionPane.OK_CANCEL_OPTION);
          } else {
            result = JOptionPane.CANCEL_OPTION;
            JOptionPane.showMessageDialog(this.getMainFrame(),
                getTranslatedString("main.gui.update.error.firmware.download.text"),
                getTranslatedString("main.gui.update.error.firmware.download.title"), JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        if (result == JOptionPane.OK_OPTION) {
          copyFirmwareToDrive(firmwareVersion, drive);
        }
      } catch (IOException e) {
        logger.error(e);
        JOptionPane.showMessageDialog(this.getMainFrame(), getTranslatedString("main.gui.update.error.firmware.text"),
            getTranslatedString("main.gui.update.error.firmware.title"), JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @Action
  public void settings() {
    SettingsGUI gui = new SettingsGUI(getMainFrame());
    gui.setModal(true);
    gui.setVisible(true);
  }

  @Action
  public void autoAnalyse() {
  }

  /**
   * Software update
   */
  @Action
  public void checkUpdate() {
    if (mainVersion == null) {
      mainVersion = new Version(CommonInformation.VERSION_STRING);
    }
    try {
      boolean checkUpdate = softwareWebVersion.checkUpdate(mainVersion);
      if (checkUpdate) {
        final String userReadbleString = getTranslatedString("main.gui.statusbar.update.long",
            softwareWebVersion.getLastVersion().toString(), mainVersion.toString());
        logger.info(userReadbleString);
        setStatusbar(getTranslatedString("main.gui.statusbar.update.short"), userReadbleString);
      }
    } catch (IOException e) {
      logger.error("checking software update.", e);
      setStatusbar(getTranslatedString("main.gui.statusbar.error"),
          getTranslatedString("main.gui.statusbar.update.error"));
    }
  }

  /**
   * @param dataFile
   */
  public void convertAndShowMap(final File dataFile) {
    try {
      showWait(true);
      setProgress(0);
      setStatusbar(NLS_PREFIX + "statusbar.export", null);

      try {
        LoggerFile loggerFile = new LoggerFile(dataFile).setProgressCallback(new ProgressCallback() {
          public void progress(long position, long max, String message) {
            final int newpos = (int) (position * 100 / max);
            setProgress(newpos);
            Thread.yield();
          }
        }).setSentenceFilter(DefaultSentenceFilter.GPSECHOSENTENCEFILTER).parseFile();
        LoggingTool.showParseResults(logger, dataFile, loggerFile);

        List<Datagram> datagrams = loggerFile.getDatagrams();

        String showMapIn = ProgramConfig.getInstance().getShowMapIn();
        mapGui = null;
        if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.BROWSER.name())) {
          ShowMapTool.showInBrowser(dataFile, datagrams);
        } else if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.PROGRAM.name())) {
          ShowMapTool.showInExternalProgram(dataFile, datagrams);
        } else if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.INTERNAL.name())) {
          setStatusbar(NLS_PREFIX + "statusbar.prepare.map", null);
          mapGui = new MapGUI();
          mapGui.setData(datagrams);
          mapGui.setVisible(true);
        }

      } finally {
        resetStatusbar();
        showWait(false);
        SwingUtilities.invokeLater(new Runnable() {

          public void run() {
            if (mapGui != null) {
              mapGui.toFront();
            }
          }
        });
      }
    } catch (

    IOException e)

    {
      e.printStackTrace();
    } catch (

    URISyntaxException e)

    {
      e.printStackTrace();
    }

  }

  private void checkActions() {
    boolean isFileSelected = false;
    final File sdFile = getSelectedFile(); // fileList.getSelectedValue();
    if (sdFile != null) {
      isFileSelected = true;
    }

    boolean isDriveSelected = false;
    DriveInfo drive = (DriveInfo) driveLetters.getSelectedItem();
    if (drive != null) {
      isDriveSelected = true;
    }

    boolean isTrackSelected = trackPanel.isTrackSelected();

    javax.swing.Action action = actions.get("export");
    action.setEnabled(isFileSelected);
    action = actions.get("analyseFile");
    action.setEnabled(isFileSelected);
    action = actions.get("saveFile");
    action.setEnabled(isFileSelected);
    action = actions.get("showMap");
    action.setEnabled(isFileSelected);
    action = actions.get("upload");
    action.setEnabled(isFileSelected);
    action = actions.get("viewFile");
    action.setEnabled(isFileSelected);
    action = actions.get("newTrack");
    action.setEnabled(isDriveSelected);
    action = actions.get("addTrack");
    action.setEnabled(isFileSelected);

    action = actions.get("update");
    action.setEnabled(isDriveSelected);
    action = actions.get("restoreBackup");
    action.setEnabled(isDriveSelected);
    action = actions.get("config");
    action.setEnabled(isDriveSelected);

    action = actions.get("addTrack");
    action.setEnabled(isTrackSelected && isFileSelected);
  }

  /**
   * @param firmwareFile
   * @param drive
   */
  private void copyFirmwareToDrive(FirmwareVersion firmwareVersion, DriveInfo drive) {
    File outputFile = new File(drive.getFile(), HEX_OSMFIRMW);
    FirmwareVersion output = new FirmwareVersion(outputFile);

    if (output.getVersion().lesserThan(firmwareVersion.getVersion())) {
      int result = JOptionPane.OK_OPTION;
      if (output.exists()) {
        result = JOptionPane.showConfirmDialog(this.getMainFrame(),
            getTranslatedString("main.gui.update.ask.firmware.overwrite.text"),
            getTranslatedString("main.gui.update.ask.firmware.overwrite.title"), JOptionPane.OK_CANCEL_OPTION);
      }
      if (result == JOptionPane.OK_OPTION) {
        // kopieren
        logger.info("do update");
        try {
          firmwareVersion.writeFirmware(drive.getFile());
          setStatusbar(getTranslatedString("main.gui.statusbar.update.success.firmware"),
              getTranslatedString("main.gui.statusbar.update.success.firmware"));
          JOptionPane.showMessageDialog(this.getMainFrame(),
              getTranslatedString("main.gui.statusbar.update.success.firmware"));
        } catch (IOException e) {
          logger.error("error writing firmware to sd card.", e);
          JOptionPane.showMessageDialog(this.getMainFrame(),
              getTranslatedString("main.gui.update.error.firmware.sdwriting.text"),
              getTranslatedString("main.gui.update.error.firmware.sdwriting.title"), JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      setStatusbar(getTranslatedString("main.gui.statusbar.firmware.update.notneeded"),
          getTranslatedString("main.gui.statusbar.firmware.update.notneeded"));
    }

  }

  private FirmwareVersion getActualFirmware() {
    // ist die Firmware schon herrunter geladen worden?
    File firmwareFile = new File(appDir, HEX_OSMFIRMW);
    FirmwareVersion firmware = new FirmwareVersion(firmwareFile);
    // testen, ob das die aktuelle Firmware ist?
    return firmware;
  }

  private void updateActualFirmware(FirmwareVersion firmware) throws IOException {
    if (!firmware.exists() || firmwareWebVersion.checkUpdate(firmware.getVersion())) {
      // aktuelle Version herrunter laden.
      Version webVersion = firmwareWebVersion.getVersion();
      downloadFirmware(firmware.getFile());
      firmware.setVersion(webVersion);
    }
  }

  private void downloadFirmware(File outputFile) {
    URL url;
    HttpURLConnection conn;
    try {
      url = new URL(CommonInformation.FIRMWARE_DOWNLOAD_URL);
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      java.io.OutputStream out = new FileOutputStream(outputFile);
      StreamHelper.copyStream(conn.getInputStream(), out);
      out.close();
    } catch (MalformedURLException e) {
      logger.error(e);
    } catch (IOException e) {
      logger.error(e);
    }
  }

  /**
   * @param drive
   * @param filepath
   * @throws IOException
   */
  private boolean restoreFATImage(DriveInfo drive) throws IOException {
    logger.info("the sd card will be written with the FAT image.");
    setStatusbar(getTranslatedString("main.gui.statusbar.restorefat.short"),
        getTranslatedString("main.gui.statusbar.restorefat.long"));

    if (drive != null) {
      File fatImage = new File(appDir, "fat.img");
      File fatImageZip = new File(toolsDir, "fat.zip");
      if (!fatImage.exists()) {
        if (!fatImageZip.exists()) {
          JOptionPane.showMessageDialog(this.getMainFrame(), getTranslatedString("main.gui.update.error.fatimage.text"),
              getTranslatedString("main.gui.update.error.fatimage.title"), JOptionPane.ERROR_MESSAGE);
          return false;
        }
        ZipExtracter extracter = new ZipExtracter(fatImageZip, appDir);
        extracter.extractAll();
        extracter.close();

        if (!fatImage.exists()) {
          JOptionPane.showMessageDialog(this.getMainFrame(), getTranslatedString("main.gui.update.error.fatimage.text"),
              getTranslatedString("main.gui.update.error.fatimage.title"), JOptionPane.ERROR_MESSAGE);
          return false;
        }
      }

      DiscImagerTool.newDiscImagerTool().setToolsDir(toolsDir).setAppDir(appDir).writeDiscImage(drive, fatImage);

      File hexFile = new File(drive.getFile(), "OSMFIRMW.HEX");
      hexFile.delete();
    }
    return true;
  }

  /**
   * @param drive
   * @param filepath
   * @throws IOException
   */
  private void backupImage(DriveInfo drive) throws IOException {
    logger.info("the sd card will be backuped.");
    setStatusbar(getTranslatedString("main.gui.statusbar.backup.short"),
        getTranslatedString("main.gui.statusbar.backup.long"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmm");
    if (drive != null) {
      int i = 0;
      File backupImageFile = null;
      do {
        String filename = String.format("backup_%s_%d.img", dateFormat.format(new Date()), (int) i);
        backupImageFile = new File(appDir, filename);
        i++;
      } while (backupImageFile.exists());

      DiscImagerTool.newDiscImagerTool().setAppDir(appDir).setToolsDir(toolsDir).readDiscImage(drive, backupImageFile);
    }
  }

  private void checkCache() {
    String cachePath = ProgramConfig.getInstance().getCacheFolder();
    File cacheDir = new File(cachePath);
    if (!cacheDir.exists()) {
      cacheDir.mkdirs();
    } else {
      File[] files = Files.getFiles(cacheDir, true);
      long thiryDaysBefore = new Date().getTime();
      thiryDaysBefore = thiryDaysBefore - (1000L * 60L * 60L * 24L * 30L);

      for (File file : files) {
        if (file.lastModified() < thiryDaysBefore) {
          logger.debug("deleting file from cache");
          file.delete();
        }
      }
    }
  }

  @Override
  protected void startup() {
    drives = new HashMap<>();
    logger.info(String.format("starting %s %s, Version %s", CommonInformation.APPLICATION_NAME,
        CommonInformation.RELEASE_VERSION_BUILD, CommonInformation.VERSION_STRING));
    mainVersion = new Version(CommonInformation.VERSION_STRING);

    softwareWebVersion = new MCSWebVersion(CommonInformation.UPDATE_URL);
    firmwareWebVersion = new MCSWebVersion(CommonInformation.FIRMWARE_UPDATE_URL);

    appDir = new File(Files.getAppData(), "MCSDepthLogger");
    appDir.mkdirs();

    File cachePath = new File(appDir, "cache");
    fileCache = new FileCache(cachePath);

    toolsDir = new File(".\\tools\\");
    if (!toolsDir.exists()) {
      logger.error("tools not installed.");
    }

    createContent();

    afterStartup();
  }

  /**
   * @wbp.parser.entryPoint
   */
  private void createContent() {
    actions = Application.getInstance().getContext().getActionMap(this);

    topPanel = new JPanel();
    BorderLayout panelLayout = new BorderLayout();
    topPanel.setLayout(panelLayout);
    topPanel.setPreferredSize(new java.awt.Dimension(500, 300));

    FormLayout layout = new FormLayout("4dlu, pref, pref",
        "top:p,4dlu,top:p,top:p,top:p,top:p,top:p,top:p,top:p,top:p");
    PanelBuilder builder = new PanelBuilder(layout);

    int x = 1;
    driveLetters = new JComboBox<DriveInfo>();
    builder.addLabel(getTranslatedString("main.gui.driveletter"), CC.rc(x++, 2));
    x++;
    builder.add(driveLetters, CC.rc(++x, 2, CellConstraints.FILL, CellConstraints.FILL));
    driveLetters.setBounds(0, 0, 80, 38);
    driveLetters.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        DriveInfo drive = (DriveInfo) e.getItem();
        if (drive != null) {
          analyseSDCarddrive(drive);
        }
      }
    });

    JButton refreshBtn = new JButton();
    refreshBtn.setAction(actions.get("refreshDrives"));
    refreshBtn.setName("refreshDrives");
    refreshBtn.setFocusable(false);
    builder.add(refreshBtn, CC.rc(x, 3, CellConstraints.CENTER, CellConstraints.LEFT));

    builder.addLabel(getTranslatedString("main.gui.info"), CC.rc(++x, 2));
    sdInfoLabel = new JLabel();
    builder.add(sdInfoLabel, CC.rc(++x, 2));

    builder.addLabel(getTranslatedString("main.gui.file.info"), CC.rc(++x, 2));
    fileInfoLabel = new JLabel();
    builder.add(fileInfoLabel, CC.rc(++x, 2));

    final JButton analyseBtn = new JButton();
    analyseBtn.setAction(actions.get("analyseFile"));
    analyseBtn.setName("analyseFile");
    analyseBtn.setFocusable(false);
    builder.add(analyseBtn, CC.rc(++x, 2));

    autoAnalyse = new JCheckBox();
    autoAnalyse.setAction(actions.get("autoAnalyse"));
    autoAnalyse.setName("autoAnalyse");
    autoAnalyse.setEnabled(!autoanalyse);
    autoAnalyse.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        autoanalyse = e.getStateChange() == ItemEvent.SELECTED;
        analyseBtn.setEnabled(!autoanalyse);
      }
    });
    builder.add(autoAnalyse, CC.rc(++x, 2));

    topPanel.add(builder.getPanel(), BorderLayout.WEST);

    JPopupMenu listMenu = new JPopupMenu();
    listMenu.add(actions.get("viewFile"));
    listMenu.add(actions.get("saveFile"));
    listMenu.add(actions.get("showMap"));
    listMenu.add(actions.get("export"));
    listMenu.add(actions.get("addTrack"));
    listMenu.add(actions.get("adaptDateTime"));

    routeSplitter = new JSplitPane();
    topPanel.add(routeSplitter, BorderLayout.CENTER);
    routeSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
    routeSplitter.setDividerLocation(ProgramConfig.getInstance().getTrackDivider());

    JPanel filePanel = new JPanel(new BorderLayout());
    routeSplitter.setLeftComponent(filePanel);

    JLabel fileLabel = new JLabel();
    fileLabel.setName("fileLabel");
    filePanel.add(fileLabel, BorderLayout.NORTH);

    tableModel = new FileTableModel();
    fileTable = new JXTable(tableModel);
    fileTable.setFillsViewportHeight(true);

    scrollPane = new JScrollPane();
    scrollPane.setViewportView(fileTable);

    filePanel.add(scrollPane, BorderLayout.CENTER);
    fileTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    fileTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          checkActions();
          fullAnalyseDataFile(autoanalyse);
        }
      }
    });
    fileTable.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
          showMap();
        }
      }
    });
    fileTable.setComponentPopupMenu(listMenu);

    fileTable.setAutoResizeMode(JXTable.AUTO_RESIZE_NEXT_COLUMN);
    fileTable.setEditable(false);

    trackPanel = new TrackPanel(this.getMainFrame(), this);
    trackPanel.addSelectionListener(new SimpleCallback<File>() {

      @Override
      public void callback(File t) {
        checkActions();
      }
    });

    routeSplitter.setRightComponent(trackPanel);

    topPanel.add(getToolbar(), BorderLayout.NORTH);
    getMainFrame().setJMenuBar(getMainMenu());
    getMainFrame().setBounds(ProgramConfig.getInstance().getMainFrameData());

    JXStatusBar statusBar = new JXStatusBar();
    statusLabel = new JLabel(getTranslatedString(NLS_PREFIX + "statusbar.ready"));
    JXStatusBar.Constraint c1 = new JXStatusBar.Constraint();
    c1.setFixedWidth(200);
    statusBar.add(statusLabel, c1);
    JXStatusBar.Constraint c2 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
    pbar = new JProgressBar();
    pbar.setMinimum(0);
    pbar.setMaximum(100);
    statusBar.add(pbar, c2); // Fill with no inserts - will use remaining space

    progressLabel = new JLabel();
    JXStatusBar.Constraint c3 = new JXStatusBar.Constraint();
    c3.setFixedWidth(120);
    statusBar.add(progressLabel, c3);

    topPanel.add(statusBar, BorderLayout.SOUTH);

    show(topPanel);
  }

  private void afterStartup() {
    new UploadTool(this);
    new DataTool(this);
    Thread buildThread = new Thread(new Runnable() {
      public void run() {
        buildDriveList();
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            checkActions();
            MainGUI.this.getMainFrame().doLayout();
          }
        });
      }
    }, "buildThread");
    buildThread.start();

    Thread updateThread = new Thread(new Runnable() {
      public void run() {
        checkUpdate();
      }
    }, "updateThread");
    updateThread.start();

    Thread cacheThread = new Thread(new Runnable() {
      public void run() {
        checkCache();
      }
    }, "checkCache");
    cacheThread.setDaemon(true);
    cacheThread.start();
  }

  protected void analyseSDCarddrive(DriveInfo drive) {
    loggerConfig = null;
    StringBuilder builder = new StringBuilder();
    builder.append("<html><body>");

    // some normal information about this volumen
    String displayName = drive.toString();
    builder.append(getTranslatedString(NLS_PREFIX + "name", displayName));
    builder.append("<br/>");

    long totalSpace = drive.getFile().getTotalSpace();
    builder.append(getTranslatedString(NLS_PREFIX + "totalspace", Files.readableFileSize(totalSpace)));
    builder.append("<br/>");

    long usableSpace = drive.getFile().getUsableSpace();
    builder.append(getTranslatedString(NLS_PREFIX + "usablespace", Files.readableFileSize(usableSpace)));
    builder.append("<br/>");

    builder.append(getTranslatedString(NLS_PREFIX + "filesystem", drive.getFileSystem()));
    builder.append("<br/>");

    // searching for special logger depending fileList.
    File oseamlogConfig = new File(drive.getFile(), "oseamlog.cnf");
    loggerConfig = new DepthLoggerConfig(oseamlogConfig);
    if (loggerConfig.isLoggerConfig()) {
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "config"));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "vesselid", loggerConfig.getVesselid()));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "seatalk", loggerConfig.isSeatalkActive()));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "channel_a", loggerConfig.getBaudString(Channel.Channel_A)));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "channel_b", loggerConfig.getBaudString(Channel.Channel_B)));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "outputGyro", loggerConfig.isWriteGyroData()));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "outputSupply", loggerConfig.isWriteBoardSupply()));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "firmwareVersion", loggerConfig.getLoggerVersion().toString()));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "bootloaderVersion", loggerConfig.getBootloaderVersion(),
          loggerConfig.getBootloaderCRC()));
      builder.append("<br/>");
      builder.append(getTranslatedString(NLS_PREFIX + "normVoltage", loggerConfig.getNormVoltage()));
      builder.append("<br/>");
    }

    builder.append("<br/>");
    String[] dataFilesStr = drive.getFile().list(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".dat") && name.toLowerCase().startsWith("data");
      }
    });

    if (dataFiles == null) {
      dataFiles = new ArrayList<>();
    }
    dataFiles.clear();

    tableModel.clear();
    if ((dataFilesStr != null) && (dataFilesStr.length > 0)) {
      Arrays.sort(dataFilesStr);

      for (String dataFile : dataFilesStr) {
        File myFile = new File(drive.getFile(), dataFile);
        dataFiles.add(myFile);
        tableModel.addFile(myFile);
      }
    }
    fileTable.doLayout();
    scrollPane.doLayout();
    builder.append(getTranslatedString(NLS_PREFIX + "dataCount", dataFiles.size()));
    builder.append("<br/>");
    builder.append("</body></html>");
    sdInfoLabel.setText(builder.toString());

    checkFileDates();
  }

  private void checkFileDates() {
    AdaptTimeTool adaptTimeTool = AdaptTimeTool.newAdaptTimeTool();
    boolean isAdaptableFile = false;
    for (File file : dataFiles) {
      isAdaptableFile = isAdaptableFile || adaptTimeTool.mustBeProcessed(file);
    }

    if (isAdaptableFile) {
      int result = JOptionPane.CANCEL_OPTION;
      if (!ProgramConfig.getInstance().isAutoAdaptUnAsk()) {
        if (ProgramConfig.getInstance().isAutoAdapt()) {
          result = JOptionPane.YES_OPTION;
        }
      }
      if (ProgramConfig.getInstance().isAutoAdaptUnAsk()) {
        result = JOptionPane.showConfirmDialog(this.getMainFrame(),
            getTranslatedString("main.gui.adapttime.ask.isadaptable.text"),
            getTranslatedString("main.gui.adapttime.ask.isadaptable.title"), JOptionPane.YES_NO_CANCEL_OPTION);
      }
      if (result != JOptionPane.CANCEL_OPTION) {
        if (result == JOptionPane.YES_OPTION) {
          ProgramConfig.getInstance().setAutoAdapt(true);
        } else {
          ProgramConfig.getInstance().setAutoAdapt(false);
        }
        Thread autoAdaptThread = new Thread(new Runnable() {

          @Override
          public void run() {
            showWait(true);

            setStatusbar(NLS_PREFIX + "statusbar.adapttimestamp", null);
            Thread.yield();

            AdaptTimeTool.newAdaptTimeTool().setStatusAndProgress(MainGUI.this).setProcessAllFiles(false)
                .setDataFiles(dataFiles).doWork();

            fileCache.clear();

            resetStatusbar();
            showWait(false);
            Thread.yield();
          }
        });
        autoAdaptThread.start();
      }
    }
  }

  protected void analyseDataFile(File sdFile, boolean fullAnalyse) {
    if (sdFile != null) {
      fileCache.addFile(sdFile);
      final File dataFile = fileCache.getFile(sdFile);

      HTMLBuilder htmlBuilder = new HTMLBuilder(this);
      htmlBuilder.addFileData(dataFile);

      if (fullAnalyse) {
        SwingUtilities.invokeLater(new Runnable() {

          public void run() {
            pbar.setValue(0);
            statusLabel.setText(getTranslatedString(NLS_PREFIX + "statusbar.analyse"));
            showWait(true);
          }
        });
        try {
          synchronized (analyseLock) {
            LoggerFile loggerFile = new LoggerFile(dataFile);
            loggerFile.setProgressCallback(new ProgressCallback() {
              public void progress(long position, long max, String message) {
                final int newpos = (int) (position * 100 / max);
                setProgress(newpos);
                Thread.yield();
              }
            });
            List<String> errorList = loggerFile.checkFile();
            int errors = errorList.size();
            long count = loggerFile.getCount();
            if (errors > 0) {
              logger.info(String.format("found errors in file %s", dataFile.getName()));
              for (String string : errorList) {
                logger.info(String.format("errors in line %s", string));
              }
            }

            List<Datagram> datagrams = loggerFile.getDatagrams();
            Version version = new Version();
            Map<String, Integer> datagramTypes = new HashMap<String, Integer>();
            Date lastModified = new Date(0);
            Date firstTimeStamp = null;

            for (Datagram datagram : datagrams) {
              String dtgName = datagram.getClass().getSimpleName();
              if (!datagramTypes.containsKey(dtgName)) {
                datagramTypes.put(dtgName, new Integer(0));
              }
              Integer dtgCount = datagramTypes.get(dtgName);
              dtgCount++;
              datagramTypes.put(dtgName, dtgCount);

              if (datagram instanceof NMEAOSMStartSentence) {
                version = ((NMEAOSMStartSentence) datagram).getVersion();
              }
              if (datagram instanceof NMEARMCSentence) {
                NMEARMCSentence rmcDatagram = (NMEARMCSentence) datagram;
                if (rmcDatagram != null) {
                  if (firstTimeStamp == null) {
                    firstTimeStamp = rmcDatagram.getDateTime();
                  }
                  lastModified = rmcDatagram.getDateTime();
                }
              }
            }
            if (firstTimeStamp == null) {
              firstTimeStamp = new Date(0);
            }

            List<String> keys = new ArrayList<String>(datagramTypes.keySet());
            Collections.sort(keys);

            setProgress(100);

            htmlBuilder.addDatagramCount(count).setErrorCount(errors).setVersion(version).setTimestamps(lastModified,
                firstTimeStamp);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          SwingUtilities.invokeLater(new Runnable() {

            public void run() {
              pbar.setValue(0);
              showWait(false);
            }
          });
        }
      }
      htmlBuilder.closeHTML();

      resetStatusbar();

      SwingUtilities.invokeLater(new Runnable() {

        public void run() {
          fileInfoLabel.setText(htmlBuilder.toString());
        }

      });
    }
  }

  private void fullAnalyseDataFile(final boolean full) {
    final File file = getSelectedFile();
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        analyseDataFile(file, full);
      }
    }, "Analyse file");
    thread.start();
  }

  private void buildDriveList() {
    FileSystemView fsv = FileSystemView.getFileSystemView();
    driveLetters.removeAllItems();
    try {
      fileCache.clear();

      StringBuilder build = new StringBuilder();
      build.append("@echo off\r\n");
      build.append("cd \"");
      build.append(toolsDir.getCanonicalPath());
      build.append("\"\r\n");
      File logFile = new File(appDir, "log.txt");
      if (logFile.exists()) {
        logFile.delete();
      }

      build.append("MCSfsInfo.exe >\"");
      build.append(logFile.getCanonicalPath());
      build.append("\"\r\n");

      File infoCommand = new File(appDir, "info.cmd");
      Files.writeStringToFile(infoCommand, build.toString());
      List<String> command = new ArrayList<>();
      command.add("cmd.exe");
      command.add("/c");
      command.add(infoCommand.getCanonicalPath());
      StartProcess.startJava(command, true, ".");

      if (logFile.exists()) {
        try {
          List<String> readFileToList = Files.readFileToList(logFile);
          for (String line : readFileToList) {
            String[] local = line.split(",");
            if (local.length > 0) {
              String drive = local[0];
              File file = new File(drive);
              // String displayName = fsv.getSystemDisplayName(file);
              String type = local[1].toUpperCase();
              DRIVE_TYPES driveType = DRIVE_TYPES.UNKNOWN;
              try {
                driveType = DRIVE_TYPES.valueOf(type);
              } catch (IllegalArgumentException e) {
              }

              FILE_SYSTEM_TYPES fileSystemType = FILE_SYSTEM_TYPES.UNKNOWN;
              try {
                fileSystemType = FILE_SYSTEM_TYPES.valueOf(local[3].toUpperCase());
              } catch (Exception e) {
              }

              boolean isFloppy = fsv.isFloppyDrive(file);
              boolean canRead = file.canRead();
              boolean canWrite = file.canWrite();
              DriveInfo driveInfo = new DriveInfo(drive, fsv.getSystemDisplayName(file), file);
              driveInfo.setDriveType(driveType);
              driveInfo.setFileSystem(fileSystemType);
              drives.put(driveInfo.getName(), driveInfo);
              if (canRead && canWrite && !isFloppy && (type.toLowerCase().contains("removable"))) {
                driveLetters.addItem(driveInfo);
              }
            }
          }

          for (String key : drives.keySet()) {
            logger.info(String.format("found drive %s with filesystem %s", drives.get(key).getName(),
                drives.get(key).getFileSystem()));
          }
        } catch (IOException e) {
          logger.error("error building drive list.", e);
        }
        logFile.delete();
      }
    } catch (IOException e) {
      logger.error("error building drive list.", e);
    }
  }

  private JPanel getToolbar() {
    JPanel toolBarPanel = new JPanel();
    BorderLayout jPanel1Layout = new BorderLayout();
    toolBarPanel.setLayout(jPanel1Layout);
    JToolBar toolBar = new JToolBar();
    toolBarPanel.add(toolBar, BorderLayout.CENTER);
    toolBar.setRollover(true);

    toolBar.add(actions.get("upload"));
    toolBar.add(actions.get("config"));
    toolBar.add(actions.get("update"));
    toolBar.add(actions.get("restoreBackup"));
    toolBar.add(actions.get("showMap"));
    toolBar.addSeparator();

    toolBar.add(actions.get("adaptDateTime"));
    toolBar.add(actions.get("importPath"));
    toolBar.add(actions.get("export"));
    toolBar.addSeparator();
    toolBar.add(actions.get("newTrack"));
    toolBar.add(actions.get("addTrack"));
    toolBar.add(actions.get("settings"));

    toolBar.add(new JSeparator(JSeparator.VERTICAL));

    toolBar.add(actions.get("exitApp"));
    toolBar.add(actions.get("about"));
    toolBar.add(actions.get("help"));

    toolBarPanel.add(new JSeparator(), BorderLayout.SOUTH);
    return toolBarPanel;
  }

  private JMenuBar getMainMenu() {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu();
    menuBar.add(fileMenu);
    fileMenu.setName("fileMenu");

    fileMenu.add(new JMenuItem(actions.get("upload")));
    fileMenu.add(new JMenuItem(actions.get("config")));
    fileMenu.add(new JMenuItem(actions.get("update")));
    fileMenu.add(new JMenuItem(actions.get("importPath")));
    fileMenu.addSeparator();
    fileMenu.add(new JMenuItem(actions.get("settings")));
    fileMenu.add(new JMenuItem(actions.get("restoreBackup")));
    fileMenu.addSeparator();
    fileMenu.add(new JMenuItem(actions.get("exitApp")));

    JMenu trackMenu = new JMenu();
    menuBar.add(trackMenu);
    trackMenu.setName("trackMenu");
    trackMenu.add(new JMenuItem(actions.get("addTrack")));

    JMenu mapMenu = new JMenu();
    menuBar.add(mapMenu);
    mapMenu.setName("mapMenu");
    mapMenu.add(new JMenuItem(actions.get("showMap")));
    mapMenu.add(new JMenuItem(actions.get("export")));

    JMenu helpMenu = new JMenu();
    menuBar.add(helpMenu);
    helpMenu.setName("helpMenu");
    helpMenu.add(new JMenuItem(actions.get("help")));
    helpMenu.add(new JMenuItem(actions.get("about")));

    return menuBar;
  }

  public String getTranslatedString(final String key, Object... object) {
    if (key == null) {
      return null;
    }
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

  /**
   * 
   */
  private void showWait(boolean wait) {
    if (waitPanel == null) {
      waitPanel = new JDialog(getMainFrame());
      // waitPanel.setModal(true);
      waitPanel.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      waitPanel.setAlwaysOnTop(true);

      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout());

      JLabel label = new JLabel();
      label.setName("main.gui.wait.icon");
      label.setText(getTranslatedString(NLS_PREFIX + "wait.wait"));

      String urlStr = getTranslatedString(NLS_PREFIX + "wait.icon");
      java.net.URL imgURL = getClass().getResource(urlStr);
      ImageIcon image = new ImageIcon(imgURL);
      label.setIcon(image);

      panel.add(label, BorderLayout.CENTER);
      waitPanel.getContentPane().add(panel);
      waitPanel.pack();
    }
    setWaitCursor(wait);
    if (wait) {
      getMainFrame().setEnabled(false);
      int x, y;
      Rectangle bounds = topPanel.getBounds();
      x = (bounds.width / 2) - (waitPanel.getWidth() / 2) + bounds.x;
      y = (bounds.height / 2) - (waitPanel.getHeight() / 2) + bounds.y;
      waitPanel.setLocation(x, y);
      waitPanel.setTitle(getTranslatedString(NLS_PREFIX + "wait.title"));
      waitPanel.setVisible(true);
    } else {
      getMainFrame().setEnabled(true);
      waitPanel.setVisible(false);
    }
  }

  private void setWaitCursor(boolean wait) {
    if (wait) {
      getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    } else {
      getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  // ----- Interface StatusAndProgress -----
  @Override
  public void setStatusbar(final String text) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        statusLabel.setText(getTranslatedString(text));
      }
    });
  }

  @Override
  public void setStatusbar(final String text, final String tooltip) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        String translatedText = getTranslatedString(text);
        statusLabel.setText(translatedText);
        String tooltiptext = getTranslatedString(tooltip);
        if (tooltiptext == null) {
          tooltiptext = translatedText;
        }
        statusLabel.setToolTipText(tooltiptext);
      }
    });
  }

  int oldPos = 0;
  private ApplicationActionMap actions;

  @Override
  public void setProgress(final int value) {
    if (oldPos != value) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          pbar.setValue(value);
        }
      });
      oldPos = value;
    }
  }

  @Override
  public void setProgressLabel(final String text, final String hint) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        progressLabel.setText(text);
        progressLabel.setToolTipText(hint);
      }
    });
  }

  @Override
  public void setWait(final boolean wait) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        showWait(wait);
      }
    });
  }

  @Override
  public void resetStatusbar() {
    setProgressLabel("", "");
    setProgress(0);
    setStatusbar(NLS_PREFIX + "statusbar.ready", null);
  }
}
