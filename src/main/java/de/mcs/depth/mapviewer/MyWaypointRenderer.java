/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: MyWaypointRenderer.java
 * EMail: W.Klaas@gmx.de
 * Created: 13.03.2014 wklaa_000
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
package de.mcs.depth.mapviewer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.Waypoint;

/**
 * @author wklaa_000
 * 
 */
public class MyWaypointRenderer extends DefaultWaypointRenderer {
  private static final Log log = LogFactory.getLog(MyWaypointRenderer.class);
  private BufferedImage imgStart;
  private BufferedImage imgEnd;
  private BufferedImage img;

  public MyWaypointRenderer() {
    try {
      imgStart = ImageIO.read(getClass().getResource("resources/pin7.png"));
      imgEnd = ImageIO.read(getClass().getResource("resources/pin8.png"));
      img = ImageIO.read(getClass().getResource("resources/standard_waypoint.png"));
    } catch (Exception ex) {
      log.warn("couldn't read standard_waypoint.png", ex);
    }
  }

  @Override
  public void paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint w) {
    Point2D point = map.getTileFactory().geoToPixel(w.getPosition(), map.getZoom());
    BufferedImage myImg;
    myImg = img;
    int x = (int) point.getX();
    if (w instanceof StartWaypoint) {
      x = (int) point.getX() - myImg.getWidth() / 2;
      myImg = imgStart;
    }
    if (w instanceof EndWaypoint) {
      x = (int) point.getX() - myImg.getWidth() / 2;
      myImg = imgEnd;
    }
    if (myImg == null) {
      return;
    }

    int y = (int) point.getY() - myImg.getHeight();

    g.drawImage(myImg, x, y, null);
  }
}
