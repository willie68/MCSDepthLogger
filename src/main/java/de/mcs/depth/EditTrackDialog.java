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
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import de.mcs.depth.logger.ProgressCallback;
import de.mcs.depth.utils.Track;

public class EditTrackDialog extends JDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Logger logger = Logger.getLogger(this.getClass());

  private final JPanel contentPanel = new JPanel();

  private boolean applied;

  private JFrame frame;
  private JTextField jtfName;
  private JLabel lblTrackName;
  private JLabel lblComment;
  private JLabel lblDataFiles;
  private JList<String> listField;

  private Track track;

  private JTextArea jtaComment;

  private DefaultListModel<String> listModel;

  private JCheckBox jcbDeleteFile;

  public EditTrackDialog(JFrame frame, Track track) {
    super(frame);
    this.frame = frame;
    this.track = track;
    createContent();
  }

  private void createContent() {
    setModal(true);
    setSize(550, 450);
    if (frame != null) {
      int screenHeight = frame.getHeight();
      int screenWidth = frame.getWidth();

      setLocation((screenWidth / 2) - (getWidth() / 2) + frame.getX(),
          (screenHeight / 2) - (getHeight() / 2) + frame.getY());
    } else {
      setLocation(100, 100);
    }
    String string = getTranslatedString("editTrackDialog.title");
    this.setTitle(string);
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
      JPanel formPanel = new JPanel();
      formPanel.setName("jlbTrackName");
      JScrollPane jScrollPane = new JScrollPane(formPanel);
      jScrollPane.setBorder(null);
      contentPanel.add(jScrollPane, BorderLayout.CENTER);
      GridBagLayout gbl_formPanel = new GridBagLayout();
      gbl_formPanel.columnWidths = new int[] { 120, 56, 0 };
      gbl_formPanel.rowHeights = new int[] { 0, 90, 90, 0, 0 };
      gbl_formPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
      gbl_formPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
      formPanel.setLayout(gbl_formPanel);
      {
        lblTrackName = new JLabel("Name");
        lblTrackName.setName("lblTrackName");
        GridBagConstraints gbc_jlbTrackName = new GridBagConstraints();
        gbc_jlbTrackName.insets = new Insets(0, 0, 5, 5);
        gbc_jlbTrackName.anchor = GridBagConstraints.NORTHEAST;
        gbc_jlbTrackName.gridx = 0;
        gbc_jlbTrackName.gridy = 0;
        formPanel.add(lblTrackName, gbc_jlbTrackName);
      }
      {
        jtfName = new JTextField();
        lblTrackName.setLabelFor(jtfName);
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 5, 0);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        formPanel.add(jtfName, gbc_textField);
        jtfName.setColumns(10);
      }
      {
        lblComment = new JLabel("Comment");
        lblComment.setName("lblComment");
        GridBagConstraints gbc_lblComment = new GridBagConstraints();
        gbc_lblComment.anchor = GridBagConstraints.NORTHEAST;
        gbc_lblComment.insets = new Insets(0, 0, 5, 5);
        gbc_lblComment.gridx = 0;
        gbc_lblComment.gridy = 1;
        formPanel.add(lblComment, gbc_lblComment);
      }
      {
        jtaComment = new JTextArea();
        jtaComment.setFont(jtfName.getFont());
        jtaComment.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        GridBagConstraints gbc_jtaComment = new GridBagConstraints();
        gbc_jtaComment.insets = new Insets(0, 0, 5, 0);
        gbc_jtaComment.fill = GridBagConstraints.BOTH;
        gbc_jtaComment.gridx = 1;
        gbc_jtaComment.gridy = 1;
        formPanel.add(jtaComment, gbc_jtaComment);
        lblComment.setLabelFor(jtaComment);
      }
      {
        lblDataFiles = new JLabel("Data files");
        lblDataFiles.setName("lblDataFiles");
        GridBagConstraints gbc_jlbDataFiles = new GridBagConstraints();
        gbc_jlbDataFiles.insets = new Insets(0, 0, 5, 5);
        gbc_jlbDataFiles.anchor = GridBagConstraints.NORTHEAST;
        gbc_jlbDataFiles.gridx = 0;
        gbc_jlbDataFiles.gridy = 2;
        formPanel.add(lblDataFiles, gbc_jlbDataFiles);
      }
      lblDataFiles.setLabelFor(listField);
      {
        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 1;
        gbc_panel.gridy = 2;
        formPanel.add(panel, gbc_panel);
        panel.setLayout(new BorderLayout(0, 0));
        {
          panel.add(getToolBar(), BorderLayout.NORTH);
        }
        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane);
        scrollPane.setBorder(null);
        listField = new JList<String>();
        scrollPane.setViewportView(listField);
        listField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listField.setVisibleRowCount(4);
        listField.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
      }
      {
        listModel = new DefaultListModel<>();
        listField.setModel(listModel);
      }
      {
        jcbDeleteFile = new JCheckBox("delete file after insert");
        GridBagConstraints gbc_chckbxDeleteFileAfter = new GridBagConstraints();
        gbc_chckbxDeleteFileAfter.anchor = GridBagConstraints.WEST;
        gbc_chckbxDeleteFileAfter.gridx = 1;
        gbc_chckbxDeleteFileAfter.gridy = 3;
        formPanel.add(jcbDeleteFile, gbc_chckbxDeleteFileAfter);
        jcbDeleteFile.setName("jcbDeleteFile");
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

  private JToolBar getToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);

    JLabel label = new JLabel();
    label.setName("trackLabel");
    toolBar.add(label);

    toolBar.add(getAppActionMap().get("delFile"));
    toolBar.add(getAppActionMap().get("fileUp"));
    toolBar.add(getAppActionMap().get("fileDown"));

    toolBar.addSeparator();

    toolBar.add(getAppActionMap().get("showMap"));

    return toolBar;
  }

  /**
   * 
   */
  private void fillInDefault() {
    jtfName.setText(track.getName());
    jtaComment.setText(track.getComment());

    listModel.clear();
    List<String> files = track.getDisplayFileNames();
    for (String string : files) {
      listModel.addElement(string);
    }
    jcbDeleteFile.setSelected(track.isDeleteSource());
  }

  @Action
  public void ok() {
    setWaitCursor(true);
    try {
      track.setName(jtfName.getText());
      track.setComment(jtaComment.getText());
      track.setDeleteSource(jcbDeleteFile.isSelected());
      track.compress();
      track.freeResources();
      setWaitCursor(false);
      this.setVisible(false);
      applied = true;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, getTranslatedString("config.vesselid.error.text", e.getMessage()),
          getTranslatedString("config.vesselid.error.title"), JOptionPane.ERROR_MESSAGE);
      logger.error(getTranslatedString("config.vesselid.error.text", e.getMessage()), e);
      applied = false;
    } finally {
      if (this.isVisible()) {
        setWaitCursor(false);
      }
    }
  }

  @Action
  public void cancel() {
    this.setVisible(false);
    applied = false;
  }

  @Action
  public void delFile() {
    int selectedIndex = listField.getSelectedIndex();
    if (selectedIndex >= 0) {
      int result = JOptionPane.showConfirmDialog(this, getTranslatedString("ask.delete.file.text"),
          getTranslatedString("ask.delete.file.title"), JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        String remove = listModel.remove(selectedIndex);
        String filename = remove.substring(0, remove.indexOf("(") - 1).trim();
        track.removeFile(filename);
      }
    }
    // TODO hier muss noch die Datei entfernt werden.
  }

  @Action
  public void fileUp() {
    int selectedIndex = listField.getSelectedIndex();
    if (selectedIndex > 0) {
      String remove = listModel.remove(selectedIndex);
      listModel.add(selectedIndex - 1, remove);
      listField.setSelectedIndex(selectedIndex - 1);
      track.setDataFilesOrder(listModel.toArray());
    }
  }

  @Action
  public void fileDown() {
    int selectedIndex = listField.getSelectedIndex();
    if (selectedIndex < listModel.size() - 1) {
      String remove = listModel.remove(selectedIndex);
      listModel.add(selectedIndex + 1, remove);
      listField.setSelectedIndex(selectedIndex + 1);
      track.setDataFilesOrder(listModel.toArray());
    }
  }

  @Action
  public void showMap() {
    int selectedIndex = listField.getSelectedIndex();
    if (selectedIndex >= 0) {
      String remove = listModel.get(selectedIndex);
      String filename = remove.substring(0, remove.indexOf("(") - 1).trim();
      final File dataFile = track.getFile(filename);

      new Thread(new Runnable() {

        @Override
        public void run() {
          MapGUI.showDataFile(dataFile, new ProgressCallback() {

            @Override
            public void progress(long position, long max, String message) {

            }
          });
        }
      }, "showMap").start();
    }
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

  public boolean isApplied() {
    return applied;
  }

  public void setPath(String path) {
    jtfName.setText(path + jtfName.getText());
  }

  private void setWaitCursor(boolean wait) {
    if (wait) {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    } else {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

}
