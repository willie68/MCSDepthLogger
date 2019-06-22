/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: ConfigGUI.java
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
package de.mcs.depth;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import de.mcs.depth.logger.DepthLoggerConfig;
import de.mcs.depth.logger.DepthLoggerConfig.Channel;

public class ConfigGUI extends JDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  enum BAUDRATES {
    B_1200, B_2400, B_4800, B_9600, B_19200, B_INACTIVE
  }

  private static int BaudRateNameToRate(BAUDRATES bautrateName) {
    switch (bautrateName) {
    case B_1200:
      return 1200;
    case B_2400:
      return 2400;
    case B_4800:
      return 4800;
    case B_9600:
      return 9600;
    case B_19200:
      return 19200;
    default:
      return 0;
    }
  }

  private static BAUDRATES BaudRateToName(int bautrate) {
    switch (bautrate) {
    case 1200:
      return BAUDRATES.B_1200;
    case 2400:
      return BAUDRATES.B_2400;
    case 4800:
      return BAUDRATES.B_4800;
    case 9600:
      return BAUDRATES.B_9600;
    case 19200:
      return BAUDRATES.B_19200;
    default:
      return BAUDRATES.B_INACTIVE;
    }
  }

  public class BaudModel {
    private String displayName;
    private BAUDRATES baudrate;
    private boolean bDefault;

    private BaudModel(String display, BAUDRATES baudrate, boolean bDefault) {
      this.displayName = getTranslatedString(display);
      this.bDefault = bDefault;
      if (bDefault) {
        this.displayName = this.displayName + " *";
      }
      this.baudrate = baudrate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return displayName;
    }
  }

  private Logger logger = Logger.getLogger(this.getClass());

  private final JPanel contentPanel = new JPanel();
  private JTextField vesselIDField;
  private JComboBox<BaudModel> cbBaudB;
  private JComboBox<BaudModel> cbBaudA;
  private JCheckBox chckbxSeatalkActive;
  private JCheckBox chckbxWriteGyrodata;
  private JCheckBox chckbxWriteBoardSupply;

  private BaudModel[] channelA = { new BaudModel("config.gui.baudrate.inactive", BAUDRATES.B_1200, false),
      new BaudModel("config.gui.baudrate.1200", BAUDRATES.B_1200, false),
      new BaudModel("config.gui.baudrate.2400", BAUDRATES.B_2400, false),
      new BaudModel("config.gui.baudrate.4800", BAUDRATES.B_4800, true),
      new BaudModel("config.gui.baudrate.9600", BAUDRATES.B_9600, false),
      new BaudModel("config.gui.baudrate.19200", BAUDRATES.B_19200, false) };

  private BaudModel[] channelB = { new BaudModel("config.gui.baudrate.inactive", BAUDRATES.B_1200, false),
      new BaudModel("config.gui.baudrate.1200", BAUDRATES.B_1200, false),
      new BaudModel("config.gui.baudrate.2400", BAUDRATES.B_2400, false),
      new BaudModel("config.gui.baudrate.4800", BAUDRATES.B_4800, true) };
  private DepthLoggerConfig loggerConfig;
  private boolean applied;

  private JFrame frame;

  public ConfigGUI(JFrame frame) {
    super(frame);
    this.frame = frame;
    createContent();
  }

  private void createContent() {
    setModal(true);
    setSize(550, 430);
    if (frame != null) {
      int screenHeight = frame.getHeight();
      int screenWidth = frame.getWidth();

      setLocation((screenWidth / 2) - (getWidth() / 2) + frame.getX(),
          (screenHeight / 2) - (getHeight() / 2) + frame.getY());
    } else {
      setLocation(100, 100);
    }
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JPanel logoPanel = new JPanel();
      contentPanel.add(logoPanel, BorderLayout.NORTH);
      logoPanel.setLayout(new BorderLayout(0, 0));
      logoPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
      {
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setIcon(new ImageIcon(ConfigGUI.class.getResource("/de/mcs/depth/resources/ico/logo.png")));
        logoPanel.add(lblNewLabel, BorderLayout.WEST);
      }
      {
        JTextArea textPane = new JTextArea();
        textPane.setFont(new Font("Tahoma", textPane.getFont().getStyle(), textPane.getFont().getSize()));
        textPane.setBackground(SystemColor.control);
        textPane.setLineWrap(true);
        textPane.setWrapStyleWord(true);
        textPane.setName("description");
        textPane.setEditable(false);
        logoPanel.add(textPane, BorderLayout.CENTER);
      }
    }
    {
      JPanel panel = new JPanel();
      panel.setName("");
      contentPanel.add(panel, BorderLayout.CENTER);
      GridBagLayout gbl_panel = new GridBagLayout();
      gbl_panel.columnWidths = new int[] { 120, 56, 0 };
      gbl_panel.rowHeights = new int[] { 16, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
      gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
      panel.setLayout(gbl_panel);
      JLabel label = new JLabel("");
      label.setName("config.gui.baud_a");

      GridBagConstraints gbc_label = new GridBagConstraints();
      gbc_label.gridx = 0;
      gbc_label.gridy = 1;
      gbc_label.insets = new Insets(0, 0, 5, 5);
      gbc_label.anchor = GridBagConstraints.NORTHEAST;
      panel.add(label, gbc_label);
      {
        cbBaudA = new JComboBox<>();
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.gridx = 1;
        gbc_comboBox.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridy = 1;
        panel.add(cbBaudA, gbc_comboBox);
      }
      {
        JLabel lblChannelASeatalk = new JLabel("");
        lblChannelASeatalk.setHorizontalAlignment(SwingConstants.LEADING);
        lblChannelASeatalk.setName("config.gui.sealtalk");
        GridBagConstraints gbc_lblChannelASeatalk = new GridBagConstraints();
        gbc_lblChannelASeatalk.gridx = 0;
        gbc_lblChannelASeatalk.anchor = GridBagConstraints.EAST;
        gbc_lblChannelASeatalk.insets = new Insets(0, 0, 5, 5);
        gbc_lblChannelASeatalk.gridy = 2;
        panel.add(lblChannelASeatalk, gbc_lblChannelASeatalk);
      }
      {
        chckbxSeatalkActive = new JCheckBox("");
        chckbxSeatalkActive.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            if (chckbxSeatalkActive.isSelected()) {
              cbBaudA.setEnabled(false);
            } else {
              cbBaudA.setEnabled(true);
            }
          }
        });
        chckbxSeatalkActive.setName("cbSeatalk");
        chckbxSeatalkActive.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_chckbxSeatalkActive = new GridBagConstraints();
        gbc_chckbxSeatalkActive.gridx = 1;
        gbc_chckbxSeatalkActive.anchor = GridBagConstraints.WEST;
        gbc_chckbxSeatalkActive.insets = new Insets(0, 0, 5, 0);
        gbc_chckbxSeatalkActive.gridy = 2;
        panel.add(chckbxSeatalkActive, gbc_chckbxSeatalkActive);
      }
      {
        JLabel lblChannelBBaud = new JLabel("");
        lblChannelBBaud.setName("config.gui.baud_b");
        GridBagConstraints gbc_lblChannelBBaud = new GridBagConstraints();
        gbc_lblChannelBBaud.gridx = 0;
        gbc_lblChannelBBaud.anchor = GridBagConstraints.EAST;
        gbc_lblChannelBBaud.insets = new Insets(0, 0, 5, 5);
        gbc_lblChannelBBaud.gridy = 3;
        panel.add(lblChannelBBaud, gbc_lblChannelBBaud);
      }
      {
        cbBaudB = new JComboBox<>();
        GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
        gbc_comboBox_1.gridx = 1;
        gbc_comboBox_1.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_1.gridy = 3;
        panel.add(cbBaudB, gbc_comboBox_1);
      }
      {
        JLabel lblFeatures = new JLabel("Features");
        lblFeatures.setName("config.gui.feature");
        GridBagConstraints gbc_lblFeatures = new GridBagConstraints();
        gbc_lblFeatures.gridx = 0;
        gbc_lblFeatures.anchor = GridBagConstraints.EAST;
        gbc_lblFeatures.insets = new Insets(0, 0, 5, 5);
        gbc_lblFeatures.gridy = 4;
        panel.add(lblFeatures, gbc_lblFeatures);
      }
      {
        chckbxWriteGyrodata = new JCheckBox("");
        chckbxWriteGyrodata.setName("cbGyro");
        chckbxWriteGyrodata.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_chckbxWriteGyrodata = new GridBagConstraints();
        gbc_chckbxWriteGyrodata.gridx = 1;
        gbc_chckbxWriteGyrodata.anchor = GridBagConstraints.WEST;
        gbc_chckbxWriteGyrodata.insets = new Insets(0, 0, 5, 0);
        gbc_chckbxWriteGyrodata.gridy = 4;
        panel.add(chckbxWriteGyrodata, gbc_chckbxWriteGyrodata);
      }
      {
        chckbxWriteBoardSupply = new JCheckBox("");
        chckbxWriteBoardSupply.setName("cbSupply");
        chckbxWriteBoardSupply.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_chckbxWriteBoardSupply = new GridBagConstraints();
        gbc_chckbxWriteBoardSupply.gridy = 5;
        gbc_chckbxWriteBoardSupply.gridx = 1;
        gbc_chckbxWriteBoardSupply.anchor = GridBagConstraints.WEST;
        gbc_chckbxWriteBoardSupply.insets = new Insets(0, 0, 5, 0);
        panel.add(chckbxWriteBoardSupply, gbc_chckbxWriteBoardSupply);
      }
      {
        JLabel lblVesselId = new JLabel("");
        lblVesselId.setName("config.gui.vesselid");
        GridBagConstraints gbc_lblVesselId = new GridBagConstraints();
        gbc_lblVesselId.gridx = 0;
        gbc_lblVesselId.gridy = 6;
        gbc_lblVesselId.anchor = GridBagConstraints.EAST;
        gbc_lblVesselId.insets = new Insets(0, 0, 5, 5);
        panel.add(lblVesselId, gbc_lblVesselId);
      }
      {
        vesselIDField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.gridx = 1;
        gbc_textField.insets = new Insets(0, 0, 5, 0);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridy = 6;
        panel.add(vesselIDField, gbc_textField);
        vesselIDField.setColumns(10);
      }
      {
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Tahoma", textArea.getFont().getStyle(), textArea.getFont().getSize() - 2));
        textArea.setBackground(SystemColor.control);
        textArea.setRows(0);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setName("comment");
        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.insets = new Insets(0, 0, 5, 0);
        gbc_textArea.fill = GridBagConstraints.BOTH;
        gbc_textArea.gridx = 1;
        gbc_textArea.gridy = 7;
        panel.add(textArea, gbc_textArea);
      }
    }
    {
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPanel, BorderLayout.SOUTH);
      buttonPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
      {
        JButton okButton = new JButton("OK");
        okButton.setName("OK");
        // okButton.setActionCommand("OK");
        okButton.setAction(getAppActionMap().get("ok"));
        buttonPanel.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setName("Cancel");
        // cancelButton.setActionCommand("Cancel");
        cancelButton.setAction(getAppActionMap().get("cancel"));
        buttonPanel.add(cancelButton);
      }
    }

    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
    fillInDefault();
  }

  private void fillInDefault() {
    cbBaudA.removeAllItems();
    BaudModel dModel = null;
    for (BaudModel model : channelA) {
      cbBaudA.addItem(model);
      if (model.bDefault) {
        dModel = model;
      }
    }
    cbBaudA.setSelectedItem(dModel);

    dModel = null;
    cbBaudB.removeAllItems();
    for (BaudModel model : channelB) {
      cbBaudB.addItem(model);
      if (model.bDefault) {
        dModel = model;
      }
    }
    cbBaudB.setSelectedItem(dModel);

    chckbxSeatalkActive.setSelected(false);
    chckbxWriteGyrodata.setSelected(true);
    chckbxWriteBoardSupply.setSelected(false);
  }

  @Action
  public void ok() {
    if (loggerConfig != null) {
      try {
        boolean doWrite = true;
        File file = loggerConfig.getConfigFile();
        if (file.exists()) {
          int result = JOptionPane.showConfirmDialog(this, getTranslatedString("config.overwrite.config.text"),
              getTranslatedString("config.overwrite.config.title"), JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.CANCEL_OPTION) {
            doWrite = false;
          }
        }
        if (doWrite) {
          int value = 0;
          if ((vesselIDField.getText() != null) && !vesselIDField.equals("")) {
            value = Integer.parseInt(vesselIDField.getText());
          }
          loggerConfig.setVesselid(value);
          loggerConfig.setSeatalkActive(chckbxSeatalkActive.isSelected());
          loggerConfig.setWriteBoardSupply(chckbxWriteBoardSupply.isSelected());
          loggerConfig.setWriteGyroData(chckbxWriteGyrodata.isSelected());

          BaudModel model = (BaudModel) cbBaudA.getSelectedItem();
          loggerConfig.setBaudRate(Channel.Channel_A, BaudRateNameToRate(model.baudrate));
          model = (BaudModel) cbBaudB.getSelectedItem();
          loggerConfig.setBaudRate(Channel.Channel_B, BaudRateNameToRate(model.baudrate));

          try {
            loggerConfig.writeConfig();
            this.setVisible(false);
            applied = true;
          } catch (Exception e) {
            JOptionPane.showMessageDialog(this, getTranslatedString("config.vesselid.error.text", e.getMessage()),
                getTranslatedString("config.vesselid.error.title"), JOptionPane.ERROR_MESSAGE);
            logger.error(getTranslatedString("config.vesselid.error.text", e.getMessage()), e);
            applied = false;
          }
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, getTranslatedString("config.vesselid.illegal.text"),
            getTranslatedString("config.vesselid.illegal.title"), JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @Action
  public void cancel() {
    this.setVisible(false);
    applied = false;
  }

  /**
   * Returns the action map used by this application. Actions defined using the
   * Action annotation are returned by this method
   */
  private ApplicationActionMap getAppActionMap() {
    return Application.getInstance().getContext().getActionMap(this);
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

  public void setLoggerConfig(DepthLoggerConfig loggerConfig) {
    this.loggerConfig = loggerConfig;
    if ((loggerConfig != null) && loggerConfig.isLoggerConfig()) {
      int baudRate = loggerConfig.getBaudRate(Channel.Channel_A);
      BAUDRATES baudRateName = BaudRateToName(baudRate);
      for (int i = 0; i < cbBaudA.getItemCount(); i++) {
        BaudModel model = cbBaudA.getItemAt(i);
        if (model.baudrate.equals(baudRateName)) {
          cbBaudA.setSelectedItem(model);
        }
      }
      baudRate = loggerConfig.getBaudRate(Channel.Channel_B);
      baudRateName = BaudRateToName(baudRate);
      for (int i = 0; i < cbBaudB.getItemCount(); i++) {
        BaudModel model = cbBaudB.getItemAt(i);
        if (model.baudrate.equals(baudRateName)) {
          cbBaudB.setSelectedItem(model);
        }
      }

      chckbxSeatalkActive.setSelected(loggerConfig.isSeatalkActive());
      cbBaudA.setEnabled(!chckbxSeatalkActive.isSelected());
      chckbxWriteBoardSupply.setSelected(loggerConfig.isWriteBoardSupply());
      chckbxWriteGyrodata.setSelected(loggerConfig.isWriteGyroData());

      vesselIDField.setText(String.format("%d", loggerConfig.getVesselid()));
    }
  }

  public boolean isApplied() {
    return applied;
  }
}
