/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: MCSWebVersion.java
 * EMail: W.Klaas@gmx.de
 * Created: 12.12.2013 Willie
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
package de.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import de.mcs.utils.Version;

/**
 * @author w.klaas
 * 
 */
public class MCSWebVersion {
  private Version webVersion;
  private String urlStr;

  class WebVersion {
    String name;
    int count;
    String version;
  }

  public MCSWebVersion(String updateUrl) {
    this.urlStr = updateUrl;
  }

  public boolean checkUpdate(Version version) throws IOException {
    getVersion();
    return webVersion.greaterThan(version);
  }

  public Version getLastVersion() {
    return webVersion;
  }

  public Version getVersion() throws IOException {
    URL url;
    HttpURLConnection conn;
    BufferedReader rd;
    String line;
    String result = "";
    url = new URL(urlStr);
    conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    while ((line = rd.readLine()) != null) {
      result += line;
    }
    rd.close();

    Gson gson = new Gson();
    WebVersion webJsonVersion = gson.fromJson(result, WebVersion.class);
    if (webJsonVersion != null) {
      webVersion = new Version(webJsonVersion.version);
    } else {
      webVersion = new Version("0.0.0.0");
    }
    return webVersion;
  }
}
