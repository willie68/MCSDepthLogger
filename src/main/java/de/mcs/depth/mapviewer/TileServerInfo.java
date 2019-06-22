/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: TileServerInfo.java
 * EMail: W.Klaas@gmx.de
 * Created: 20.02.2014 wklaa_000
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.mcs.utils.StringUtils;

/**
 * @author wklaa_000
 * 
 */
public class TileServerInfo {

  private static final String NAME = "name";
  private static final String BASEURL = "baseUrl";
  private static final String MAXZOOM = "maxZoom";
  private Properties props;

  public TileServerInfo() {
    props = new Properties();
  }

  public TileServerInfo(String name, String baseUrl, int maxZoom) {
    this();
    props.setProperty(NAME, name);
    props.setProperty(BASEURL, baseUrl);
    props.setProperty(MAXZOOM, Integer.toString(maxZoom));
  }

  public TileServerInfo(String line) {
    this();
    Map<String, String> csvStringToPropMap = StringUtils.csvStringToPropMap(line, ',', '"', '=');
    for (String key : csvStringToPropMap.keySet()) {
      props.setProperty(key, csvStringToPropMap.get(key));
    }
  }

  @Override
  public String toString() {
    Map<String, String> map = new HashMap<>();
    Set<String> names = props.stringPropertyNames();
    for (String key : names) {
      map.put(key, props.getProperty(key));
    }
    return StringUtils.propMapToCsvString(map, ',', '"', '=');
  }

  public String getName() {
    return props.getProperty(NAME);
  }

  public String getBaseUrl() {
    return props.getProperty(BASEURL);
  }

  public int getMaxZoom() {
    return Integer.parseInt(props.getProperty(MAXZOOM, "0"));
  }
}
