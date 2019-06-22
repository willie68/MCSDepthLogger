/**
 * 
 */
package de.mcs.depth.utils;

import java.io.File;
import java.util.List;

/**
 * @author w.klaas
 * 
 */
public class FileTool {

  /**
   * this methode will generate a user presentable string with the names of all
   * files in a comma seperated list.
   * 
   * @param files
   *          list of files
   * @return String comma seperated string with all filenames
   */
  public static String getFileNameString(List<File> files) {
    StringBuilder b = new StringBuilder();
    boolean first = true;
    for (File file : files) {
      if (first)
        first = false;
      else
        b.append(", ");
      b.append(file.getName());
    }
    return b.toString();
  }

}
