/**
 * MCS Media Computer Software
 * <one line to give the program's name and a brief idea of what it does.>
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: FileTypeFilter.java
 * EMail: W.Klaas@gmx.de
 * Created: 24.12.2013 Willie
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
package de.mcs.gui.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Willie
 * 
 */
public class FileTypeFilter extends FileFilter {

  private String extension;
  private String description;

  public FileTypeFilter(String extension, String description) {
    this.extension = extension;
    this.description = description;
  }

  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    return file.getName().toLowerCase().endsWith(extension);
  }

  public String getDescription() {
    return description + String.format(" (*%s)", extension);
  }

}
