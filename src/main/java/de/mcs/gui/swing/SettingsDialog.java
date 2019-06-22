/**
 * MCS Media Computer Software
 * GUI Element: Settings dialog
 * Copyright (C) 2012 by Wilfried Klaas
 * Project: SmallArchiveSwingClient
 * File: SettingsDialog.java
 * EMail: W.Klaas@gmx.de
 * Created: 14.03.2012 Willie
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

/**
 * @author Willie
 * 
 */
public interface SettingsDialog {

  String getTitle();

  boolean doApply();

  void doCancel();

  int getDialogHeight();

  int getDialogWidth();

  String getInfoText();

  void setCallback(DialogCallback callback);

}
