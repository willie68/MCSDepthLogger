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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.jdesktop.application.Application;

import de.mcs.depth.Constants.SHOW_IN_TYPE;
import de.mcs.gui.swing.JFilePicker;

/**
 * @author Willie
 * 
 */
public class MapSettingsPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JFilePicker jfpProgram;
  private JComboBox<ShowInItem> jcbShowMapType;
  private JSpinner jsMaxPoints;

  class ShowInItem {

    private SHOW_IN_TYPE type;
    private String displayName;

    public ShowInItem(String displayName, SHOW_IN_TYPE type) {
      this.type = type;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }

    public SHOW_IN_TYPE getType() {
      return type;
    }
  }

  /**
   * Create the panel.
   */
  public MapSettingsPanel() {
    setBounds(new Rectangle(0, 4, 4, 0));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 114, 130, 80, 130 };
    gridBagLayout.rowHeights = new int[] { 30, 0, 30, 0, 30, 0, 30 };
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JLabel lblShowMapIn = new JLabel();
    lblShowMapIn.setName("lblShowMapIn");
    GridBagConstraints gbc_lblKartendarstellungIm = new GridBagConstraints();
    gbc_lblKartendarstellungIm.gridx = 0;
    gbc_lblKartendarstellungIm.insets = new Insets(0, 0, 5, 5);
    gbc_lblKartendarstellungIm.anchor = GridBagConstraints.EAST;
    gbc_lblKartendarstellungIm.gridy = 0;
    add(lblShowMapIn, gbc_lblKartendarstellungIm);

    jcbShowMapType = new JComboBox<>();
    GridBagConstraints gbc_comboBox = new GridBagConstraints();
    gbc_comboBox.gridx = 1;
    gbc_comboBox.gridwidth = 3;
    gbc_comboBox.insets = new Insets(0, 0, 5, 0);
    gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_comboBox.gridy = 0;
    add(jcbShowMapType, gbc_comboBox);
    jcbShowMapType.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        checkType();
      }
    });

    JLabel lblExtProgram = new JLabel();
    lblExtProgram.setName("lblExtProgram");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 1;
    add(lblExtProgram, gbc_lblNewLabel);

    jfpProgram = new JFilePicker("");
    jfpProgram.setMode(JFilePicker.MODE_OPEN);
    jfpProgram.setName("jfpProgram");
    // filePicker.setButtonIcon((String) null);
    GridBagConstraints gbc_filePicker = new GridBagConstraints();
    gbc_filePicker.gridx = 1;
    gbc_filePicker.gridwidth = 3;
    gbc_filePicker.insets = new Insets(0, 0, 5, 0);
    gbc_filePicker.fill = GridBagConstraints.HORIZONTAL;
    gbc_filePicker.gridy = 1;
    add(jfpProgram, gbc_filePicker);

    JLabel lblLblmaxpoints = new JLabel("lblMaxPoints");
    lblLblmaxpoints.setName("lblLblmaxpoints");
    GridBagConstraints gbc_lblLblmaxpoints = new GridBagConstraints();
    gbc_lblLblmaxpoints.anchor = GridBagConstraints.EAST;
    gbc_lblLblmaxpoints.insets = new Insets(0, 0, 5, 5);
    gbc_lblLblmaxpoints.gridx = 0;
    gbc_lblLblmaxpoints.gridy = 2;
    add(lblLblmaxpoints, gbc_lblLblmaxpoints);

    jsMaxPoints = new JSpinner();
    jsMaxPoints.setPreferredSize(new Dimension(120, 22));
    jsMaxPoints.setMinimumSize(new Dimension(120, 22));
    GridBagConstraints gbc_spinner = new GridBagConstraints();
    gbc_spinner.anchor = GridBagConstraints.WEST;
    gbc_spinner.insets = new Insets(0, 0, 5, 5);
    gbc_spinner.gridx = 1;
    gbc_spinner.gridy = 2;
    add(jsMaxPoints, gbc_spinner);

    for (SHOW_IN_TYPE type : SHOW_IN_TYPE.values()) {
      jcbShowMapType.addItem(new ShowInItem(getTranslatedString("showInType." + type.name()), type));
    }

    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);

  }

  private void checkType() {
    Object selectedItem = jcbShowMapType.getSelectedItem();
    if ((selectedItem != null) && (selectedItem instanceof ShowInItem)) {
      ShowInItem item = (ShowInItem) selectedItem;
      switch (item.type) {
      case BROWSER:
      case INTERNAL:
        jfpProgram.setEnabled(false);
        break;
      case PROGRAM:
        jfpProgram.setEnabled(true);
        break;
      default:
        break;
      }
    }
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

  public String getShowMapIn() {
    return ((ShowInItem) jcbShowMapType.getSelectedItem()).getType().name();
  }

  public void setShowMapIn(String showMapIn) {
    for (int i = 0; i < jcbShowMapType.getItemCount(); i++) {
      ShowInItem item = jcbShowMapType.getItemAt(i);
      if (item.getType().name().equals(showMapIn)) {
        jcbShowMapType.setSelectedIndex(i);
      }
    }
  }

  public String getShowMapProgram() {
    return jfpProgram.getSelectedFilePath();
  }

  public void setShowMapProgram(String showMapProgram) {
    jfpProgram.setSelectedFilePath(showMapProgram);
  }

  public int getMaxMapPoints() {
    return (Integer) jsMaxPoints.getValue();
  }

  public void setMaxMapPoints(int maxMapPoints) {
    jsMaxPoints.setValue(maxMapPoints);
  }
}
