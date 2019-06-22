/**
 * MCS Media Computer Software
 * Copyright (C) 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: TrackPanel.java
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

package de.mcs.depth;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import de.mcs.depth.Constants.EXPORT_TYPES;
import de.mcs.depth.logger.DefaultSentenceFilter;
import de.mcs.depth.logger.LoggerFile;
import de.mcs.depth.logger.ProgressCallback;
import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.utils.DataTool;
import de.mcs.depth.utils.LoggerFileFilter;
import de.mcs.depth.utils.LoggingTool;
import de.mcs.depth.utils.NMEAFileFilter;
import de.mcs.depth.utils.SimpleCallback;
import de.mcs.depth.utils.Track;
import de.mcs.depth.utils.TrackTreeModel;
import de.mcs.depth.utils.TrackTreeModel.MyFileData;
import de.mcs.depth.utils.UploadTool;
import de.mcs.utils.NMEAUtils;

/**
 * @author Willie
 * 
 */
public class TrackPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  class PopupTrigger extends MouseAdapter {
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()) {
        int x = e.getX();
        int y = e.getY();
        TreePath path = trackTree.getPathForLocation(x, y);
        trackTree.setSelectionPath(path);
        popupMenu.show(trackTree, x, y);
      }
    }
  }

  private JTree trackTree;
  private JPopupMenu popupMenu;
  private JFrame mainFrame;
  private ArrayList<SimpleCallback<File>> listeners;
  private MainGUI mainGUI;
  private Logger logger = Logger.getLogger(this.getClass());

  /**
   * 
   */
  public TrackPanel(JFrame mainFrame, MainGUI mainGUI) {
    this.mainFrame = mainFrame;
    this.mainGUI = mainGUI;
    setLayout(new BorderLayout(0, 0));

    add(getToolBar(), BorderLayout.NORTH);

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane);

    trackTree = new JTree(getTrackTreeModel());
    scrollPane.setViewportView(trackTree);
    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
    trackTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
        MyFileData node = (MyFileData) trackTree.getLastSelectedPathComponent();
        checkActions();
        selectionChange(node);
      }
    });
    trackTree.setRootVisible(true);
    trackTree.addMouseListener(new PopupTrigger());

    popupMenu = new JPopupMenu();
    popupMenu.add(getAppActionMap().get("refresh"));
    popupMenu.addSeparator();
    popupMenu.add(getAppActionMap().get("addTrack"));
    popupMenu.add(getAppActionMap().get("delTrack"));
    popupMenu.add(getAppActionMap().get("editTrack"));
    popupMenu.addSeparator();
    popupMenu.add(getAppActionMap().get("exportTrack"));
    popupMenu.add(getAppActionMap().get("uploadTrack"));
    popupMenu.add(getAppActionMap().get("showTrackMap"));

    checkActions();
  }

  private JToolBar getToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);

    JLabel label = new JLabel();
    label.setName("trackLabel");
    toolBar.add(label);

    toolBar.add(new JSeparator(JSeparator.VERTICAL));

    toolBar.add(getAppActionMap().get("refresh"));
    toolBar.add(getAppActionMap().get("addTrack"));
    toolBar.add(getAppActionMap().get("delTrack"));
    toolBar.add(getAppActionMap().get("editTrack"));

    toolBar.add(new JSeparator(JSeparator.VERTICAL));

    toolBar.add(getAppActionMap().get("exportTrack"));
    toolBar.add(getAppActionMap().get("uploadTrack"));
    toolBar.add(getAppActionMap().get("showTrackMap"));

    return toolBar;
  }

  private void checkActions() {
    MyFileData node = (MyFileData) trackTree.getLastSelectedPathComponent();
    javax.swing.Action delAction = getAppActionMap().get("delTrack");
    javax.swing.Action addAction = getAppActionMap().get("addTrack");
    javax.swing.Action editAction = getAppActionMap().get("editTrack");
    javax.swing.Action showMapAction = getAppActionMap().get("showTrackMap");
    javax.swing.Action uploadAction = getAppActionMap().get("uploadTrack");
    javax.swing.Action exportAction = getAppActionMap().get("exportTrack");

    if (node != null) {
      delAction.setEnabled(node.isFile());
      addAction.setEnabled(!node.isFile());
      editAction.setEnabled(node.isFile());
      showMapAction.setEnabled(node.isFile());
      uploadAction.setEnabled(node.isFile());
      exportAction.setEnabled(node.isFile());
    } else {
      delAction.setEnabled(false);
      addAction.setEnabled(true);
      editAction.setEnabled(false);
      showMapAction.setEnabled(false);
      uploadAction.setEnabled(false);
      exportAction.setEnabled(false);
    }
  }

  @Action
  public void refresh() {
    TreePath selectionPath = trackTree.getSelectionPath();
    trackTree.setModel(getTrackTreeModel());
    trackTree.setSelectionPath(selectionPath);
  }

  @Action
  public void addTrack() {
    Track newTrack;
    try {
      newTrack = new Track();
      EditTrackDialog gui = new EditTrackDialog(mainFrame, newTrack);
      MyFileData node = (MyFileData) trackTree.getLastSelectedPathComponent();
      if ((node != null) && node.isDirectory()) {
        String path = node.getNodePath();
        gui.setPath(path);
      }
      gui.setModal(true);
      gui.setVisible(true);
      refresh();
    } catch (IOException e) {
      logger.error(e);
    }
  }

  @Action
  public void delTrack() {
    MyFileData node = (MyFileData) trackTree.getLastSelectedPathComponent();
    if (node != null) {
      if (node.isFile()) {
        int result = JOptionPane.showConfirmDialog(this, getTranslatedString("ask.delete.track.text", node.getName()),
            getTranslatedString("ask.delete.track.title"), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          node.delete();
          refresh();
        }
      }
    }
  }

  @Action
  public void editTrack() {
    setWaitCursor(true);
    Track track = getSelectedTrack();
    setWaitCursor(false);
    if (track != null) {
      EditTrackDialog gui = new EditTrackDialog(mainFrame, track);
      MyFileData node = (MyFileData) trackTree.getLastSelectedPathComponent();
      if ((node != null) && node.isDirectory()) {
        String path = node.getNodePath();
        gui.setPath(path);
        System.out.println(path);
      }
      gui.setModal(true);
      gui.setVisible(true);
      refresh();
    }
  }

  @Action
  public void showTrackMap() {
    System.out.println("showTrackMap");
    // aktuellen Track auspacken
    Track track = getSelectedTrack();
    if (track != null) {
      // die Gesamtnmeadatei suchen (Steht in den Properties)
      File mapFile = getMapFile(track);
      if (mapFile != null) {
        // datendatei mit dem MapGUI aufrufen
        MapGUI.showNmeaFile(mapFile, null);
      }
      try {
        track.freeResources();
      } catch (ConfigurationException | IOException e) {
        logger.error(e);
      }
    }
  }

  private File getMapFile(Track track) {
    File mapFile = track.getMapFile();
    // wenn nicht vorhanden generieren
    if (mapFile == null || !mapFile.exists()) {
      List<Datagram> datagramms = new ArrayList<>();
      // die einzeln Datendateien laden und sortieren
      List<String> files = track.getDisplayFileNames();
      for (String string : files) {

        String filename = string.substring(0, string.indexOf("(") - 1).trim();
        File dataFile = track.getFile(filename);
        try {
          LoggerFile loggerFile = new LoggerFile(dataFile).setProgressCallback(new ProgressCallback() {
            public void progress(long position, long max, String message) {
              final int newpos = (int) (position * 100 / max);
              mainGUI.setProgress(newpos);
              Thread.yield();
            }
          }).setSentenceFilter(DefaultSentenceFilter.KNOWN_SENTENCEFILTER).parseFile();
          LoggingTool.showParseResults(logger, dataFile, loggerFile);

          List<Datagram> myDatagrams = loggerFile.getDatagrams();
          datagramms.addAll(myDatagrams);

        } catch (Exception e) {
          logger.error(e);
        }
      }
      // alles in eine Datendatei speichern
      mapFile = track.createNewMapFile();
      try {
        NMEAUtils.writeNmeaFile(mapFile, datagramms);
        track.setMapFile(mapFile);
      } catch (IOException e) {
        logger.error(e);
        mapFile = null;
        track.setMapFile(mapFile);
      }
      mainGUI.setProgress(0);
    }
    return mapFile;
  }

  @Action
  public void exportTrack() {
    final Track track = getSelectedTrack();
    if (track != null) {
      JFileChooser fileChooser = new JFileChooser();
      FileFilter nmeaFileFilter = new NMEAFileFilter(getTranslatedString("export.file.filterName"));
      fileChooser.addChoosableFileFilter(nmeaFileFilter);
      FileFilter loggerFileFilter = new LoggerFileFilter(getTranslatedString("export.file.loggerFilterName"));
      fileChooser.addChoosableFileFilter(loggerFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(true);
      fileChooser.setFileFilter(nmeaFileFilter);
      int result = fileChooser.showSaveDialog(mainGUI.getMainFrame());

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

        final EXPORT_TYPES exptype = exporttype;

        if (exporttype.equals(EXPORT_TYPES.NMEA)) {
          // File mapFile = getMapFile(track);
        }
        File selectedFile = fileChooser.getSelectedFile();
        String filename = selectedFile.getName();
        if (!filename.endsWith(ext)) {
          filename = filename + ext;
        }

        final File nmeaFile = new File(selectedFile.getParentFile(), filename);

        new Thread(new Runnable() {

          @Override
          public void run() {
            DataTool.getInstance().exportNMEAData(track, nmeaFile, exptype);
          }
        }, "export").start();
      }
    }
  }

  @Action
  public void uploadTrack() {
    final Track track = getSelectedTrack();
    final String uploadURL = ProgramConfig.getInstance().getUploadURL();
    if ((uploadURL != null && !uploadURL.equals("")) && (track != null)) {
      int result = 0;
      if (track.isUploaded()) {
        result = JOptionPane.showConfirmDialog(this,
            getTranslatedString("ask.upload.track.already.text", track.getName()),
            getTranslatedString("ask.upload.track.already.title"), JOptionPane.OK_CANCEL_OPTION);
      } else {
        result = JOptionPane.showConfirmDialog(this, getTranslatedString("ask.upload.track.text", track.getName()),
            getTranslatedString("ask.upload.track.title"), JOptionPane.OK_CANCEL_OPTION);
      }
      if (result == JOptionPane.OK_OPTION) {
        final List<File> files = track.getDataFiles();

        Thread thread = new Thread(new Runnable() {

          @Override
          public void run() {
            boolean result = UploadTool.getInstance().uploadDataFile(files, uploadURL, null);
            if (result) {
              track.setUploaded(true);
            }
            try {
              track.freeResources();
            } catch (ConfigurationException | IOException e) {
              logger.error(e);
            }
          }
        }, "Uploadfile");
        thread.start();

      }
    }
  }

  private ApplicationActionMap getAppActionMap() {
    return Application.getInstance().getContext().getActionMap(this);
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

  /**
   * @return
   */
  private TreeModel getTrackTreeModel() {
    Track.setBasePath(ProgramConfig.getInstance().getTrackFolder());
    File trackFile = new File(ProgramConfig.getInstance().getTrackFolder());
    return new TrackTreeModel(trackFile);
  }

  public boolean isTrackSelected() {
    MyFileData node = (MyFileData) trackTree.getLastSelectedPathComponent();
    if (node != null) {
      if (node.isFile()) {
        return true;
      }
    }
    return false;
  }

  public void addSelectionListener(SimpleCallback<File> listener) {
    if (listeners == null) {
      listeners = new ArrayList<>();
    }
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeSelectionListener(SimpleCallback<File> listener) {
    if (listeners != null) {
      if (listeners.contains(listener)) {
        listeners.remove(listener);
      }
    }
  }

  private void selectionChange(MyFileData file) {
    for (SimpleCallback<File> listener : listeners) {
      listener.callback(file);
    }
  }

  public MyFileData getSelectedNode() {
    MyFileData node = null;
    if (isTrackSelected()) {
      node = (MyFileData) trackTree.getLastSelectedPathComponent();
    }
    return node;
  }

  public Track getSelectedTrack() {
    if (isTrackSelected()) {
      MyFileData node = (MyFileData) trackTree.getLastSelectedPathComponent();
      try {
        Track track = new Track(node);
        return track;
      } catch (ConfigurationException | IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private void setWaitCursor(boolean wait) {
    if (wait) {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    } else {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

}
