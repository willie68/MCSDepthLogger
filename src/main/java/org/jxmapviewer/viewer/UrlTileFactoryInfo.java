package org.jxmapviewer.viewer;

/**
 * Uses OpenStreetMap
 * 
 * @author Martin Dummer
 */
public class UrlTileFactoryInfo extends TileFactoryInfo {

  /**
   * Default constructor
   */
  public UrlTileFactoryInfo(String name, int maxZoom, String url) {
    super(name, 1, maxZoom - 2, maxZoom, 256, true, true, url, null, "x", "y", "z");
  }

  @Override
  public String getTileUrl(int x, int y, int zoom) {
    zoom = getMaximumZoomLevel() - zoom + 2;
    String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
    return url;
  }
}
