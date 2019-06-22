/**
 * MCS Media Computer Software
 * Copyright (C) 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: MapSettingsPanel.java
 * EMail: W.Klaas@gmx.de
 * Created: 23.01.2014 Willie
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

package de.mcs.depth;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jdesktop.application.Application;

import de.mcs.depth.Constants.BOOTLOADERVERSION;

/**
 * @author Willie
 * 
 */
public class LoggerSettingsPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JRadioButton versionFAT32;
  private JRadioButton versionFAT;

  /**
   * Create the panel.
   */
  public LoggerSettingsPanel() {
    setBounds(new Rectangle(0, 4, 4, 0));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 114, 130, 80, 130 };
    gridBagLayout.rowHeights = new int[] { 30, 0, 30, 0, 30, 0, 30 };
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JLabel lblBootloaderVersion = new JLabel();
    lblBootloaderVersion.setName("lblBootloaderVersion");
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 0, 5, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(lblBootloaderVersion, gbc);

    ButtonGroup groupVersion = new ButtonGroup();
    versionFAT = new JRadioButton();
    versionFAT.setName("versionFAT");
    groupVersion.add(versionFAT);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 0, 5, 5);
    gbc.gridx = 1;
    gbc.gridy = 0;
    add(versionFAT, gbc);

    versionFAT32 = new JRadioButton();
    versionFAT32.setName("versionFAT32");
    groupVersion.add(versionFAT32);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 0, 5, 5);
    gbc.gridx = 2;
    gbc.gridy = 0;
    add(versionFAT32, gbc);

    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);

  }

  public void setBootloaderVersion(BOOTLOADERVERSION version) {
    switch (version) {
    case FAT32:
      versionFAT32.setSelected(true);
      break;
    case FAT16:
    default:
      versionFAT.setSelected(true);
      break;
    }
  }

  public BOOTLOADERVERSION getBootloaderVersion() {
    if (versionFAT32.isSelected()) {
      return BOOTLOADERVERSION.FAT32;
    }
    return BOOTLOADERVERSION.FAT16;
  }
}
