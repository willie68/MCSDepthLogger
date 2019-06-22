/**
 * MCS Media Computer Software
 * <one line to give the program's name and a brief idea of what it does.>
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: JFilePicker.java
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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.mcs.depth.ConfigGUI;

/**
 * @author Willie
 * 
 */
public class JFilePicker extends JPanel {
  private String buttonLabel;

  private JTextField textField;
  private JButton button;

  private JFileChooser fileChooser;

  private int mode;
  public static final int MODE_OPEN = 1;
  public static final int MODE_SAVE = 2;

  public JFilePicker(String buttonLabel) {
    this.buttonLabel = buttonLabel;

    fileChooser = new JFileChooser();

    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

    // creates the GUI
    textField = new JTextField(30);
    button = new JButton(buttonLabel);

    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        buttonActionPerformed(evt);
      }
    });

    add(textField);
    add(button);
  }

  private void buttonActionPerformed(ActionEvent evt) {
    if (mode == MODE_OPEN) {
      if (!textField.getText().equals("")) {
        fileChooser.setSelectedFile(new File(textField.getText()));
      }
      if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
      }
    } else if (mode == MODE_SAVE) {
      if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
      }
    }
  }

  public void addFileTypeFilter(String extension, String description) {
    FileTypeFilter filter = new FileTypeFilter(extension, description);
    fileChooser.addChoosableFileFilter(filter);
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public String getSelectedFilePath() {
    return textField.getText();
  }

  public void setSelectedFilePath(String path) {
    textField.setText(path);
  }

  public JFileChooser getFileChooser() {
    return this.fileChooser;
  }

  public void setButtonIcon(String iconString) {
    button.setIcon(new ImageIcon(ConfigGUI.class.getResource("/de/mcs/depth/resources/" + iconString)));
  }

  public void setButtonText(String buttonText) {
    button.setText(buttonText);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#setEnabled(boolean)
   */
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    button.setEnabled(enabled);
    textField.setEnabled(enabled);
  }
}
