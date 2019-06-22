package de.mcs.gui.swing;

/**
 * MCS Media Computer Software
 * GUI Element: JCheckedListbox
 * Copyright (C) 2012 by Wilfried Klaas
 * Project: SmallArchiveSwingClient
 * File: JCheckBoxList.java
 * EMail: W.Klaas@gmx.de
 * Created: 14.03.2012 Willie
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JCheckBoxList<E> extends JList<E> implements ListSelectionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 5512033188146740593L;
  static Color listForeground, listBackground, listSelectionForeground, listSelectionBackground;
  static {
    UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
    listForeground = uid.getColor("List.foreground");
    listBackground = uid.getColor("List.background");
    listSelectionForeground = uid.getColor("List.selectionForeground");
    listSelectionBackground = uid.getColor("List.selectionBackground");
  }

  HashSet<Integer> selectionCache = new HashSet<>();
  int toggleIndex = -1;
  boolean toggleWasSelected;

  public JCheckBoxList() {
    super();
    setCellRenderer(new CheckBoxListCellRenderer<E>());
    addListSelectionListener(this);
  }

  public JCheckBoxList(DefaultListModel<E> listModel) {
    this();
    setModel(listModel);
  }

  @Override
  public void clearSelection() {
    super.clearSelection();
    selectionCache.clear();
  }

  // ListSelectionListener implementation
  public void valueChanged(ListSelectionEvent lse) {
    if (!lse.getValueIsAdjusting()) {
      removeListSelectionListener(this);

      // remember everything selected as a result of this action
      HashSet<Integer> newSelections = new HashSet<>();
      int size = getModel().getSize();
      for (int i = 0; i < size; i++) {
        if (getSelectionModel().isSelectedIndex(i)) {
          newSelections.add(new Integer(i));
        }
      }

      // turn on everything that was previously selected
      Iterator<Integer> it = selectionCache.iterator();
      while (it.hasNext()) {
        int index = it.next().intValue();
        getSelectionModel().addSelectionInterval(index, index);
      }

      // add or remove the delta
      it = newSelections.iterator();
      while (it.hasNext()) {
        Integer nextInt = (Integer) it.next();
        int index = nextInt.intValue();
        if (selectionCache.contains(nextInt))
          getSelectionModel().removeSelectionInterval(index, index);
        else
          getSelectionModel().addSelectionInterval(index, index);
      }

      // save selections for next time
      selectionCache.clear();
      for (int i = 0; i < size; i++) {
        if (getSelectionModel().isSelectedIndex(i)) {
          selectionCache.add(new Integer(i));
        }
      }

      addListSelectionListener(this);

    }
  }

  class CheckBoxListCellRenderer<E> extends JComponent implements ListCellRenderer<E> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    DefaultListCellRenderer defaultComp;
    JCheckBox checkbox;

    public CheckBoxListCellRenderer() {
      setLayout(new BorderLayout());
      defaultComp = new DefaultListCellRenderer();
      checkbox = new JCheckBox();
      add(checkbox, BorderLayout.WEST);
      add(defaultComp, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
        boolean cellHasFocus) {
      defaultComp.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      /*
       * checkbox.setSelected (isSelected); checkbox.setForeground (isSelected ?
       * listSelectionForeground : listForeground); checkbox.setBackground
       * (isSelected ? listSelectionBackground : listBackground);
       */
      checkbox.setSelected(isSelected);
      Component[] comps = getComponents();
      for (int i = 0; i < comps.length; i++) {
        comps[i].setForeground(listForeground);
        comps[i].setBackground(listBackground);
      }
      return this;
    }

  }
}

/*
 * Swing Hacks Tips and Tools for Killer GUIs By Joshua Marinacci, Chris Adamson
 * First Edition June 2005 Series: Hacks ISBN: 0-596-00907-0
 * http://www.oreilly.com/catalog/swinghks/
 */
