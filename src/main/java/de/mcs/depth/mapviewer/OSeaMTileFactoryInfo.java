package de.mcs.depth.mapviewer;

import org.jxmapviewer.viewer.TileFactoryInfo;

/**
 * Uses OpenStreetMap
 * 
 * @author Martin Dummer
 */
public class OSeaMTileFactoryInfo extends TileFactoryInfo {
  private static final int max = 19;
  private String template;

  /**
   * Default constructor
   */
  public OSeaMTileFactoryInfo() {
    // super("OpenSeaMap", 1, max - 2, max, 256, true, true,
    // "http://wkla.dyndns.org/tileserver", null, "x", "y", "z");
    // super("OpenSeaMap", 1, max - 2, max, 256, true, true,
    // "http://127.0.0.1/tileserver", null, "x", "y", "z");
    //
    // super("OpenSeaMap", 1, max - 2, max, 256, true, true,
    // "http://osm1.wtnet.de/tiles/base", null, "x", "y", "z");
    super("OpenSeaMap", 1, max - 2, max, 256, true, true, "http://t1.openseamap.org/tiles/base", null, "x", "y", "z");
    template = "%s/%d/%d/%d.png"; // this.baseURL + "/" + zoom + "/" + x + "/" +
                                  // y + ".png";
    // String url = this.baseURL + "/osm1.wtnet.de/tiles/base" + "/" + zoom +
    // "/" + x + "/" + y + ".png";
    // url = this.baseURL + "/cache.php?url=osm1.wtnet.de/tiles/base&zoom=" +
    // zoom + "&x=" + x + "&y=" + y + ".png";
  }

  @Override
  public String getTileUrl(int x, int y, int zoom) {
    zoom = max - zoom;
    String url = String.format(template, this.baseURL, zoom, x, y);
    // System.out.println(url);
    return url;
  }
}
