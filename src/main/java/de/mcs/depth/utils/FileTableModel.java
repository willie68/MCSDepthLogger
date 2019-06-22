package de.mcs.depth.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jdesktop.application.Application;

import de.mcs.utils.StringUtils;

public class FileTableModel extends AbstractTableModel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected List<File> filenames;

  private String[] columnNames = new String[] { "name", "size", "last modified" }; // ,
                                                                                   // "directory?",
                                                                                   // "readable?",
                                                                                   // "writable?"
                                                                                   // };

  @SuppressWarnings("rawtypes")
  protected Class[] columnClasses = new Class[] { File.class, Long.class, Date.class }; // ,
                                                                                        // Boolean.class,
                                                                                        // Boolean.class,
                                                                                        // Boolean.class
                                                                                        // };

  public FileTableModel() {
    this.filenames = new ArrayList<>();
    columnNames = StringUtils.csvStringToArray(getTranslatedString("main.gui.filetable.columnnames"));
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public int getRowCount() {
    return filenames.size();
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Class getColumnClass(int col) {
    return columnClasses[col];
  }

  // The method that must actually return the value of each cell.
  public Object getValueAt(int row, int col) {
    File f = filenames.get(row);
    switch (col) {
    case 0:
      return f;
    case 1:
      return new Long(f.length());
    case 2:
      return new Date(f.lastModified());
    case 3:
      return f;
    default:
      return null;
    }
  }

  public void addFile(File file) {
    filenames.add(file);
    fireTableDataChanged();
  }

  public void addFiles(List<File> files) {
    filenames.addAll(files);
    fireTableDataChanged();
  }

  public void clear() {
    filenames.clear();
    fireTableDataChanged();
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }
}
