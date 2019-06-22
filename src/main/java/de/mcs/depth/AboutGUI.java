/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: AboutGUI.java
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import de.mcs.CommonInformation;

public class AboutGUI extends javax.swing.JDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Auto-generated main method to display this JDialog
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame();
        frame.setSize(239, 173);
        AboutGUI inst = new AboutGUI(frame);
        inst.setModal(true);
        inst.setVisible(true);
        System.exit(0);
      }
    });
  }

  private JTextPane jTCopyright;
  private JButton jBOK;
  private JPanel jPanel2;
  private JPanel jPanel1;
  private JTextPane jTApplication;
  private JLabel jLabel1;

  public AboutGUI(JFrame frame) {
    super(frame);
    initGUI(frame);
  }

  private void initGUI(JFrame frame) {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    try {
      {
        this.addWindowListener(new WindowAdapter() {

          @Override
          public void windowOpened(WindowEvent e) {
            setLabels();
          }
        });
      }
      setTitle(getTranslatedString("aboutGUI.title"));
      int screenHeight = frame.getHeight();
      int screenWidth = frame.getWidth();

      setSize(400, 300);
      setLocation((screenWidth / 2) - (getWidth() / 2) + frame.getX(),
          (screenHeight / 2) - (getHeight() / 2) + frame.getY());
      {
        jTCopyright = new JTextPane();
        getContentPane().add(jTCopyright, BorderLayout.CENTER);

        jTCopyright.setName("copyright");
        jTCopyright.setOpaque(false);
        jTCopyright.setEditable(false);
        jTCopyright.setPreferredSize(new java.awt.Dimension(392, 194));
        jTCopyright.setContentType("text/html");
      }
      {
        jPanel1 = new JPanel();
        BorderLayout jPanel1Layout = new BorderLayout();
        jPanel1.setLayout(jPanel1Layout);
        getContentPane().add(jPanel1, BorderLayout.NORTH);
        jPanel1.setPreferredSize(new java.awt.Dimension(392, 80));
        {
          jLabel1 = new JLabel();
          jPanel1.add(jLabel1, BorderLayout.WEST);
          jLabel1.setName("logoImage");
          jLabel1.setPreferredSize(new java.awt.Dimension(107, 80));
          jLabel1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
              if (e.getButton() == MouseEvent.BUTTON1) {
                try {
                  String url = CommonInformation.APPLICATION_URL;
                  if (url == null) {
                    url = "http://www.rcarduino.de";
                  }
                  openUrl(url);
                } catch (IOException | URISyntaxException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
                }
              }
            }
          });
        }
        {
          jTApplication = new JTextPane();
          jPanel1.add(jTApplication, BorderLayout.CENTER);
          jTApplication.setName("application");
          jTApplication.setEditable(false);
        }
      }
      {
        jPanel2 = new JPanel();
        FlowLayout jPanel2Layout = new FlowLayout();
        getContentPane().add(jPanel2, BorderLayout.SOUTH);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2.setPreferredSize(new java.awt.Dimension(392, 33));
        {
          jBOK = new JButton();
          jPanel2.add(jBOK);
          jBOK.setName("jBOK");
          jBOK.setAction(getAppActionMap().get("ok"));
          jBOK.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));
        }
      }

      Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setLabels() {
    String versionText = MessageFormat.format(jTCopyright.getText(), CommonInformation.COPYRIGHT);
    jTCopyright.setText(versionText);
    versionText = MessageFormat.format(jTApplication.getText(), CommonInformation.APPLICATION_NAME,
        CommonInformation.RELEASE_VERSION_BUILD, CommonInformation.VERSION_STRING);
    jTApplication.setText(versionText);
  }

  @Action
  public void ok() {
    this.setVisible(false);
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

  public void openUrl(String url) throws IOException, URISyntaxException {
    if (java.awt.Desktop.isDesktopSupported()) {
      java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

      if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
        java.net.URI uri = new java.net.URI(url);
        desktop.browse(uri);
      }
    }
  }

}
