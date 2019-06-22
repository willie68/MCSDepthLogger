/**
 * MCS Media Computer Software
 * Copyright (C) 2013 by Wilfried Klaas
 * Project: Depth Logger
 * File: ErrorDialog.java
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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ErrorDialog extends javax.swing.JDialog {

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
        ErrorDialog inst = new ErrorDialog(frame);
        inst.setVisible(true);
      }
    });
  }

  public ErrorDialog(JFrame frame) {
    super(frame);
    initGUI();
  }

  private void initGUI() {
    try {
      setSize(400, 300);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
