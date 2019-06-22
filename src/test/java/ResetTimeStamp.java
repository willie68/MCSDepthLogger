import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 */

/**
 * @author w.klaas
 *
 */
public class ResetTimeStamp {

  /**
   * @param args
   */
  public static void main(String[] args) {
    File folder = new File("F:");
    File[] list = folder.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".dat");
      }
    });
    Date nullDate = new GregorianCalendar(2000, 1, 1, 0, 0, 0).getTime();

    for (File file : list) {
      System.out.println(file);
      file.setLastModified(nullDate.getTime());
    }
  }

}
