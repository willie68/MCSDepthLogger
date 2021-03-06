/**
 * 
 */
package de.mcs.depth.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import de.mcs.utils.Files;

/**
 * @author w.klaas
 * 
 */
public class LoggerFileFilter extends FileFilter {

  private String description;

  public LoggerFileFilter(String description) {
    this.description = description;
  }

  // Accept all directories and all gif, jpg, tiff, or png files.
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }

    String extension = Files.getExtension(f);
    if (extension != null) {
      if (extension.equalsIgnoreCase(".dat")) {
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  // The description of this filter
  public String getDescription() {
    return description;
  }
}
