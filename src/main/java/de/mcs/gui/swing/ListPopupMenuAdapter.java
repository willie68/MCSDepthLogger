/**
 * MCS Media Computer Software
 * GUI Element: Popupmenu adapter
 * Copyright 2012 by Wilfried Klaas
 * Project: SmallArchiveSwingClient
 * File: ListPopupMenuAdapter.java
 * EMail: W.Klaas@gmx.de
 * Created: 14.04.2012 Willie
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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * @author Willie
 * 
 */
public class ListPopupMenuAdapter implements PopupMenuListener {
  private void maybeUpdateSelection(PopupMenuEvent e) {
    final AWTEvent awtEvent = EventQueue.getCurrentEvent();
    final MouseEvent me;
    if (!(awtEvent instanceof MouseEvent) || !(me = (MouseEvent) awtEvent).isPopupTrigger()) {
      return;
    }
    final JPopupMenu menu = (JPopupMenu) e.getSource();
    final Component invoker = menu.getInvoker();

    if (!(invoker instanceof JList)) {
      return;
    }
    final JList list = (JList) invoker;
    final Point p = me.getPoint();
    final int row = list.locationToIndex(p);
    if (row == -1) {
      return;
    }

    // Ab hier wie bei Windows eventuell MAC und Linux anpassen
    // int[] indices = list.getSelectedIndices();
    // int rowSelected = Arrays.binarySearch(indices, row);
    // if (rowSelected < 0) {
    list.clearSelection();
    list.setSelectedIndex(row);
    // }
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    maybeUpdateSelection(e);
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    maybeUpdateSelection(e);
  }

  public void popupMenuCanceled(PopupMenuEvent e) {
    maybeUpdateSelection(e);
  }
}
