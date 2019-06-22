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
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.application.Application;

import de.mcs.depth.Constants.MEASURE_TYPE;
import de.mcs.gui.swing.JFilePicker;

/**
 * @author Willie
 * 
 */
public class CommonSettingsPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JFilePicker jfpRouteStore;
  private JComboBox<MeasureItem> jcbMeasureType;
  private JCheckBox chAutoAdapt;

  class MeasureItem {

    private MEASURE_TYPE type;
    private String displayName;

    public MeasureItem(String displayName, MEASURE_TYPE type) {
      this.type = type;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }

    public MEASURE_TYPE getType() {
      return type;
    }
  }

  /**
   * Create the panel.
   */
  public CommonSettingsPanel() {
    setBounds(new Rectangle(0, 4, 4, 0));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 114, 130, 80, 130 };
    gridBagLayout.rowHeights = new int[] { 30, 0, 30, 0, 30, 0, 30 };
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JLabel lblRouteStore = new JLabel();
    lblRouteStore.setName("lblRouteStore");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblRouteStore, gbc_lblNewLabel);

    jfpRouteStore = new JFilePicker("");
    jfpRouteStore.setMode(JFilePicker.MODE_OPEN);
    jfpRouteStore.setName("jfpRouteStore");
    jfpRouteStore.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    // filePicker.setButtonIcon((String) null);
    GridBagConstraints gbc_filePicker = new GridBagConstraints();
    gbc_filePicker.gridx = 1;
    gbc_filePicker.gridwidth = 3;
    gbc_filePicker.insets = new Insets(0, 0, 5, 0);
    gbc_filePicker.fill = GridBagConstraints.HORIZONTAL;
    gbc_filePicker.gridy = 0;
    add(jfpRouteStore, gbc_filePicker);

    JLabel lblMeasureIn = new JLabel();
    lblMeasureIn.setName("lblMeasureIn");
    GridBagConstraints gbc_lblMeasureIn = new GridBagConstraints();
    gbc_lblMeasureIn.gridx = 0;
    gbc_lblMeasureIn.insets = new Insets(0, 0, 5, 5);
    gbc_lblMeasureIn.anchor = GridBagConstraints.EAST;
    gbc_lblMeasureIn.gridy = 1;
    add(lblMeasureIn, gbc_lblMeasureIn);

    jcbMeasureType = new JComboBox<>();
    GridBagConstraints gbc_comboBox = new GridBagConstraints();
    gbc_comboBox.gridx = 1;
    gbc_comboBox.gridwidth = 3;
    gbc_comboBox.insets = new Insets(0, 0, 5, 0);
    gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_comboBox.gridy = 1;
    add(jcbMeasureType, gbc_comboBox);

    for (MEASURE_TYPE type : MEASURE_TYPE.values()) {
      jcbMeasureType.addItem(new MeasureItem(getTranslatedString("measureType." + type.name()), type));
    }

    // {
    // JLabel lblAutoAdapt = new JLabel(getTranslatedString("autoAdapt.label"));
    // GridBagConstraints gbc_lblAutoAdapt = new GridBagConstraints();
    // gbc_lblAutoAdapt.anchor = GridBagConstraints.EAST;
    // gbc_lblAutoAdapt.insets = new Insets(0, 0, 5, 5);
    // gbc_lblAutoAdapt.gridx = 0;
    // gbc_lblAutoAdapt.gridy = 2;
    // add(lblAutoAdapt, gbc_lblAutoAdapt);
    // }
    {
      chAutoAdapt = new JCheckBox(getTranslatedString("autoAdapt.label"));
      GridBagConstraints gbc_chAutoAdapt = new GridBagConstraints();
      gbc_chAutoAdapt.anchor = GridBagConstraints.WEST;
      gbc_chAutoAdapt.insets = new Insets(0, 0, 5, 5);
      gbc_chAutoAdapt.gridx = 1;
      gbc_chAutoAdapt.gridy = 2;
      add(chAutoAdapt, gbc_chAutoAdapt);
    }

    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);

  }

  // private ApplicationActionMap getAppActionMap() {
  // return Application.getInstance().getContext().getActionMap(this);
  // }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

  public String getShowMapProgram() {
    return jfpRouteStore.getSelectedFilePath();
  }

  public void setShowMapProgram(String showMapProgram) {
    jfpRouteStore.setSelectedFilePath(showMapProgram);
  }

  /**
   * @param routeFolder
   */
  public void setRouteFolder(String routeFolder) {
    jfpRouteStore.setSelectedFilePath(routeFolder);
    jfpRouteStore.getFileChooser().setSelectedFile(new File(routeFolder));
  }

  /**
   * @return
   */
  public String getRouteFolder() {
    return jfpRouteStore.getSelectedFilePath();
  }

  public String getMeasureSystem() {
    return ((MeasureItem) jcbMeasureType.getSelectedItem()).getType().name();
  }

  public void setMeasureSystem(String measureSystem) {
    for (int i = 0; i < jcbMeasureType.getItemCount(); i++) {
      MeasureItem item = jcbMeasureType.getItemAt(i);
      if (item.getType().name().equals(measureSystem)) {
        jcbMeasureType.setSelectedIndex(i);
      }
    }

  }

  public void setAutoAdapt(boolean autoAdapt) {
    chAutoAdapt.setSelected(autoAdapt);
  }

  public boolean isAutoAdapt() {
    return chAutoAdapt.isSelected();
  }

}
