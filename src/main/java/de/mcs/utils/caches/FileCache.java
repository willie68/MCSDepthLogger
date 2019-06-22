/**
 * 
 */
package de.mcs.utils.caches;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.mcs.utils.Files;

/**
 * @author w.klaas
 * 
 */
public class FileCache {

  private File folder;

  private Map<String, File> map = new HashMap<>();

  public FileCache(File cachePath) {
    this.folder = cachePath;
    folder.mkdirs();
  }

  public boolean exists(String name) {
    return map.containsKey(name);
  }

  public void addFile(File sourceFile) {
    String name = sourceFile.getName();
    File file = map.get(name);
    if (!exists(name) || (file != null) || (!file.exists())) {
      File dataFile = new File(folder, name);
      try {
        Files.fileCopy(sourceFile, dataFile);
        map.put(name, dataFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public File getFile(File file) {
    if (exists(file.getName())) {
      return map.get(file.getName());
    }
    return file;
  }

  public void clear() {
    try {
      Files.remove(folder, true);
      folder.mkdirs();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
