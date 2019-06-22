package de.mcs;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {

  public static void main(String[] args) {
    try {
      File path = new File("data/tiles");
      String subpath = "18/41934/";

      File seaPath = new File(path, "sea");
      seaPath = new File(seaPath, subpath);

      File streetPath = new File(path, "street");
      streetPath = new File(streetPath, subpath);

      // load source images
      BufferedImage image = ImageIO.read(new File(streetPath, "101290.png"));
      BufferedImage overlay = ImageIO.read(new File(seaPath, "101290.png"));

      // create the new image, canvas size is the max. of both image sizes
      int w = Math.max(image.getWidth(), overlay.getWidth());
      int h = Math.max(image.getHeight(), overlay.getHeight());
      BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

      // paint both images, preserving the alpha channels
      Graphics g = combined.getGraphics();
      g.drawImage(image, 0, 0, null);
      g.drawImage(overlay, 0, 0, null);

      // Save as new image
      ImageIO.write(combined, "PNG", new File(path, "combined.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
