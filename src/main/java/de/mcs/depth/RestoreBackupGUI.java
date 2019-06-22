/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: RestoreBackupGUI.java
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
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import de.mcs.depth.utils.DiscImagerTool;
import de.mcs.depth.utils.DriveInfo;
import de.mcs.utils.Files;
import de.mcs.utils.StartProcess;

public class RestoreBackupGUI extends javax.swing.JDialog {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * The methods in this class allow the JTable component to get and display
   * data about the files in a specified directly. It represents a table with 6
   * columns: file name, size, modification date, plus three columns for flags:
   * directory, readable, writable
   **/
  class FileTableModel extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // protected File dir;
    protected File[] filenames;

    protected String[] columnNames = new String[] { "name", "size", "last modified" };

    @SuppressWarnings("rawtypes")
    protected Class[] columnClasses = new Class[] { String.class, Long.class, Date.class };

    // This table model works for any one given directory
    public FileTableModel(File[] files) {
      // this.dir = dir;
      this.filenames = files; // Store a list of files in the directory
    }

    // These are easy methods.
    public int getColumnCount() {
      return 3;
    } // A constant for this model

    public int getRowCount() {
      return filenames.length;
    } // # of files in dir

    // Information about each column.
    public String getColumnName(int col) {
      return columnNames[col];
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class getColumnClass(int col) {
      return columnClasses[col];
    }

    // The method that must actually return the value of each cell.
    public Object getValueAt(int row, int col) {
      File f = filenames[row];
      switch (col) {
      case 0:
        return f.getName();
      case 1:
        return new Long(f.length());
      case 2:
        return new Date(f.lastModified());
      // case 3:
      // return f.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
      // case 4:
      // return f.canRead() ? Boolean.TRUE : Boolean.FALSE;
      // case 5:
      // return f.canWrite() ? Boolean.TRUE : Boolean.FALSE;
      default:
        return null;
      }
    }

    public void setFiles(File[] images) {
      filenames = images;
    }

    public File getFile(int row) {
      return filenames[row];
    }
  }

  private JTable table;
  private File appDir;
  private FileTableModel myTableModel;
  private DriveInfo drive;
  private Logger logger = Logger.getLogger(this.getClass());
  private File toolsDir;

  public RestoreBackupGUI(JFrame frame, File appDir, DriveInfo drive, File toolsDir) {
    super(frame);
    this.appDir = appDir;
    this.drive = drive;
    this.toolsDir = toolsDir;
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout(0, 0));

    setTitle(getTranslatedString("restoreBackupGUI.title"));
    int screenHeight = frame.getHeight();
    int screenWidth = frame.getWidth();

    setSize(600, 400);
    setLocation((screenWidth / 2) - (getWidth() / 2) + frame.getX(),
        (screenHeight / 2) - (getHeight() / 2) + frame.getY());

    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.SOUTH);
    panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

    JButton jbDelete = new JButton();
    jbDelete.setName("jbDelete");
    jbDelete.setAction(getAppActionMap().get("deleteImage"));
    panel.add(jbDelete);

    JButton jbRestore = new JButton();
    jbRestore.setName("jbRestore");
    jbRestore.setAction(getAppActionMap().get("restoreImage"));
    panel.add(jbRestore);

    JButton jbExplorer = new JButton();
    jbExplorer.setName("jbExplorer");
    jbExplorer.setAction(getAppActionMap().get("startExplorer"));
    panel.add(jbExplorer);

    JButton jbClose = new JButton();
    jbClose.setName("jbClose");
    jbClose.setAction(getAppActionMap().get("close"));

    panel.add(jbClose);

    JScrollPane scrollPane = new JScrollPane();
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    table = new JTable();
    myTableModel = new FileTableModel(new File[0]);

    table.setModel(myTableModel);
    scrollPane.setViewportView(table);

    initGUI();
    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
  }

  private void initGUI() {
    try {
      updateTable();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateTable() {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        File[] images = appDir.listFiles(new FilenameFilter() {

          @Override
          public boolean accept(File dir, String name) {
            return name.startsWith("backup_") && name.endsWith(".img");
          }
        });
        if (images != null) {
          myTableModel.setFiles(images);
          myTableModel.fireTableDataChanged();
        }
      }
    });
  }

  @Action
  public void close() {
    this.setVisible(false);
  }

  @Action
  public void startExplorer() {
    try {
      List<String> command = new ArrayList<>();
      command.add("explorer.exe");
      command.add(appDir.getCanonicalPath());
      StartProcess.startJava(command, false, ".");
    } catch (IOException e) {
      logger.error("error starting explorer.", e);
    }
  }

  @Action
  public void deleteImage() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow >= 0) {
      File file = myTableModel.getFile(selectedRow);
      int result = JOptionPane.showConfirmDialog(this, getTranslatedString("ask.delete.image.text", file.getName()),
          getTranslatedString("ask.delete.image.title"), JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        file.delete();
        updateTable();
      }
    }
  }

  @Action
  public void restoreImage() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow >= 0) {
      File image = myTableModel.getFile(selectedRow);
      int result = JOptionPane.CANCEL_OPTION;
      try {
        result = JOptionPane.showConfirmDialog(this,
            getTranslatedString("ask.restore.image.text", image.getName(), Files.getDriveLetter(drive.getFile())),
            getTranslatedString("ask.restore.image.title"), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          logger.info("the sd card will be written with the FAT image.");

          if (drive != null) {
            DiscImagerTool.newDiscImagerTool().setAppDir(appDir).setToolsDir(toolsDir).writeDiscImage(drive, image);
          }
          updateTable();
        }
      } catch (HeadlessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
  }

  /**
   * Returns the action map used by this application. Actions defined using the
   * Action annotation are returned by this method
   */
  private ApplicationActionMap getAppActionMap() {
    return Application.getInstance().getContext().getActionMap(this);
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

}
