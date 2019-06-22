package de.mcs.depth;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jdesktop.application.Application;

import de.mcs.gui.swing.JFilePicker;
import de.mcs.utils.StringUtils;

public class UploadSettingsPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public static final String KEY_STRING = "";
  public static final byte[] KEY = new byte[] { 'm', 'c', 's', 'F', 'd', '2', 'a', 'r', 'E', '2', 'K', 'n', 'B', 'T',
      'M', 'p' };

  class MyInputVerifier extends InputVerifier implements ActionListener {
    @Override
    public boolean verify(JComponent input) {
      char[] password = passwordField.getPassword();
      char[] rpassword = rPasswordField.getPassword();
      return Arrays.equals(password, rpassword);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

  };

  private JTextField urlField;
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JCheckBox chckbxInternalProcessing;
  private JFilePicker batchFile;
  private JPasswordField rPasswordField;

  /**
   * Create the panel.
   */
  public UploadSettingsPanel() {
    GridBagLayout gbl_uploadPanel = new GridBagLayout();
    gbl_uploadPanel.columnWidths = new int[] { 114, 130, 80, 130 };
    gbl_uploadPanel.rowHeights = new int[] { 30, 0, 30, 0, 30, 0, 30 };
    gbl_uploadPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0 };
    gbl_uploadPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gbl_uploadPanel);
    {
      JLabel lblUploadurl = new JLabel(getTranslatedString("uploadURL.label"));
      GridBagConstraints gbc_lblUploadurl = new GridBagConstraints();
      gbc_lblUploadurl.insets = new Insets(0, 0, 5, 5);
      gbc_lblUploadurl.anchor = GridBagConstraints.EAST;
      gbc_lblUploadurl.gridx = 0;
      gbc_lblUploadurl.gridy = 0;
      add(lblUploadurl, gbc_lblUploadurl);
    }
    {
      urlField = new JTextField();
      GridBagConstraints gbc_textField = new GridBagConstraints();
      gbc_textField.gridx = 1;
      gbc_textField.gridwidth = 3;
      gbc_textField.insets = new Insets(0, 0, 5, 0);
      gbc_textField.fill = GridBagConstraints.HORIZONTAL;
      gbc_textField.gridy = 0;
      add(urlField, gbc_textField);
      urlField.setColumns(10);
    }
    {
      JLabel lblUsername = new JLabel(getTranslatedString("username.label"));
      GridBagConstraints gbc_lblUsername = new GridBagConstraints();
      gbc_lblUsername.anchor = GridBagConstraints.EAST;
      gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
      gbc_lblUsername.gridx = 0;
      gbc_lblUsername.gridy = 1;
      add(lblUsername, gbc_lblUsername);
    }
    {
      usernameField = new JTextField();
      GridBagConstraints gbc_textField_1 = new GridBagConstraints();
      gbc_textField_1.gridx = 1;
      gbc_textField_1.gridwidth = 3;
      gbc_textField_1.insets = new Insets(0, 0, 5, 0);
      gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
      gbc_textField_1.gridy = 1;
      add(usernameField, gbc_textField_1);
      usernameField.setColumns(10);
    }
    {
      JLabel lblPasswort = new JLabel(getTranslatedString("password.label"));
      GridBagConstraints gbc_lblPasswort = new GridBagConstraints();
      gbc_lblPasswort.anchor = GridBagConstraints.EAST;
      gbc_lblPasswort.insets = new Insets(0, 0, 5, 5);
      gbc_lblPasswort.gridx = 0;
      gbc_lblPasswort.gridy = 2;
      add(lblPasswort, gbc_lblPasswort);
    }
    {
      passwordField = new JPasswordField();
      GridBagConstraints gbc_passwordField = new GridBagConstraints();
      gbc_passwordField.insets = new Insets(0, 0, 5, 5);
      gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
      gbc_passwordField.gridx = 1;
      gbc_passwordField.gridy = 2;
      add(passwordField, gbc_passwordField);
      passwordField.addKeyListener(new KeyAdapter() {

        @Override
        public void keyTyped(KeyEvent e) {
          super.keyTyped(e);
          checkPasswords();
        }
      });
      passwordField.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          checkPasswords();
        }
      });
    }
    {
      JLabel lblRetype = new JLabel(getTranslatedString("password.retype.label"));
      GridBagConstraints gbc_lblRetype = new GridBagConstraints();
      gbc_lblRetype.insets = new Insets(0, 0, 5, 5);
      gbc_lblRetype.fill = GridBagConstraints.EAST;
      gbc_lblRetype.gridx = 2;
      gbc_lblRetype.gridy = 2;
      add(lblRetype, gbc_lblRetype);
    }
    {
      rPasswordField = new JPasswordField();
      GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
      gbc_passwordField_1.gridx = 3;
      gbc_passwordField_1.insets = new Insets(0, 0, 5, 0);
      gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
      gbc_passwordField_1.gridy = 2;
      add(rPasswordField, gbc_passwordField_1);
      rPasswordField.addKeyListener(new KeyAdapter() {

        @Override
        public void keyTyped(KeyEvent e) {
          super.keyTyped(e);
          checkPasswords();
        }
      });
      rPasswordField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          checkPasswords();
        }
      });
    }
    {
      JLabel lblBatch = new JLabel(getTranslatedString("batch.label"));
      GridBagConstraints gbc_lblBatch = new GridBagConstraints();
      gbc_lblBatch.anchor = GridBagConstraints.EAST;
      gbc_lblBatch.insets = new Insets(0, 0, 5, 5);
      gbc_lblBatch.gridx = 0;
      gbc_lblBatch.gridy = 3;
      add(lblBatch, gbc_lblBatch);
    }
    {
      batchFile = new JFilePicker(getTranslatedString("batch.button.label"));
      batchFile.setName("batchFile");
      batchFile.setMode(JFilePicker.MODE_OPEN);
      batchFile.setButtonIcon(getTranslatedString("filechooser.icon"));
      GridBagConstraints gbc_textField = new GridBagConstraints();
      gbc_textField.gridx = 1;
      gbc_textField.gridwidth = 3;
      gbc_textField.insets = new Insets(0, 0, 5, 0);
      gbc_textField.fill = GridBagConstraints.HORIZONTAL;
      gbc_textField.gridy = 3;
      add(batchFile, gbc_textField);
    }
    {
      chckbxInternalProcessing = new JCheckBox(getTranslatedString("internalProcessing.label"));
      GridBagConstraints gbc_chckbxInterneNachbearbeitung = new GridBagConstraints();
      gbc_chckbxInterneNachbearbeitung.anchor = GridBagConstraints.WEST;
      gbc_chckbxInterneNachbearbeitung.insets = new Insets(0, 0, 5, 5);
      gbc_chckbxInterneNachbearbeitung.gridx = 1;
      gbc_chckbxInterneNachbearbeitung.gridy = 4;
      add(chckbxInternalProcessing, gbc_chckbxInterneNachbearbeitung);
    }

    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

  private void checkPasswords() {
    char[] password = passwordField.getPassword();
    char[] rpassword = rPasswordField.getPassword();
    if (Arrays.equals(password, rpassword)) {
      passwordField.setForeground(Color.BLACK);
      rPasswordField.setForeground(Color.BLACK);
    } else {
      passwordField.setForeground(Color.RED);
      rPasswordField.setForeground(Color.RED);
    }
  }

  public String getPassword() {
    return StringUtils.encrypt(new String(passwordField.getPassword()), KEY);
  }

  public String getUploadUrl() {
    return urlField.getText();
  }

  public String getUsername() {
    return usernameField.getText();
  }

  public String getBatchFile() {
    return batchFile.getSelectedFilePath();
  }

  public boolean getInternalProcessing() {
    return chckbxInternalProcessing.isSelected();
  }

  public void setUploadUrl(String uploadURL) {
    urlField.setText(uploadURL);
  }

  public void setUsername(String username) {
    usernameField.setText(username);
  }

  public void setPassword(String password) {
    if (password != null && !password.equals("")) {
      passwordField.setText(StringUtils.decrypt(password, KEY));
      rPasswordField.setText(StringUtils.decrypt(password, KEY));
    }
  }

  public void setBatchFile(String batch) {
    batchFile.setSelectedFilePath(batch);
  }

  public void setInternalProcessing(boolean internalProcessing) {
    chckbxInternalProcessing.setSelected(internalProcessing);
  }
}
