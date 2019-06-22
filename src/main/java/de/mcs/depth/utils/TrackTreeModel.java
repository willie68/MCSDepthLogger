/**
 * MCS Media Computer Software
 * Copyright (C) 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: TrackTreeModel.java
 * EMail: W.Klaas@gmx.de
 * Created: 01.02.2014 Willie
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

package de.mcs.depth.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.mcs.utils.Files;

/**
 * @author Willie
 * 
 */
public class TrackTreeModel implements TreeModel {

  public class MyFileData extends File {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String nodePath;

    /**
     * @param pathname
     */
    public MyFileData(String pathname, String nodePath) {
      super(pathname);
      this.nodePath = nodePath;
    }

    /**
     * @param pathname
     */
    public MyFileData(File file, String pathname, String nodePath) {
      super(file, pathname);
      this.nodePath = nodePath;
    }

    /**
     * @param pathname
     */
    public MyFileData(File file, String nodePath) {
      super(file.getAbsolutePath());
      this.nodePath = nodePath;
    }

    public String[] fileList() {
      return super.list(new FilenameFilter() {

        @Override
        public boolean accept(File file, String name) {
          if (name.startsWith(".")) {
            return false;
          }
          File localFile = new File(file, name);
          if (localFile.isHidden()) {
            return false;
          }
          if (localFile.isDirectory()) {
            return true;
          }
          if (name.toLowerCase().endsWith(".zip")) {
            return true;
          }
          return false;
        }
      });
    }

    @Override
    public String toString() {
      return Files.extractName(this);
    }

    public String getNodePath() {
      return nodePath;
    }
  }

  private MyFileData root; // The root identifier

  private Vector<TreeModelListener> listeners; // Declare the listeners vector

  public TrackTreeModel(File root) {
    this.root = new MyFileData(root, "");
    listeners = new Vector<>();
  }

  public Object getRoot() {
    return (root);
  }

  public Object getChild(Object parent, int index) {
    MyFileData directory = (MyFileData) parent;
    String[] directoryMembers = directory.fileList();
    String name = directoryMembers[index];
    return (new MyFileData(directory, name, directory.nodePath + "/" + name));
  }

  public int getChildCount(Object parent) {
    MyFileData fileSystemMember = (MyFileData) parent;
    if (fileSystemMember.isDirectory()) {
      String[] directoryMembers = fileSystemMember.fileList();
      return directoryMembers.length;
    } else {
      return 0;
    }
  }

  public int getIndexOfChild(Object parent, Object child) {
    MyFileData directory = (MyFileData) parent;
    MyFileData directoryMember = (MyFileData) child;
    String[] directoryMemberNames = directory.fileList();
    int result = -1;

    for (int i = 0; i < directoryMemberNames.length; ++i) {
      if (directoryMember.getName().equals(directoryMemberNames[i])) {
        result = i;
        break;
      }
    }

    return result;
  }

  public boolean isLeaf(Object node) {
    return ((MyFileData) node).isFile();
  }

  public void addTreeModelListener(TreeModelListener l) {
    if (l != null && !listeners.contains(l)) {
      listeners.addElement(l);
    }
  }

  public void removeTreeModelListener(TreeModelListener l) {
    if (l != null) {
      listeners.removeElement(l);
    }
  }

  public void valueForPathChanged(TreePath path, Object newValue) {
    // Does Nothing!
  }

  public void fireTreeNodesInserted(TreeModelEvent e) {
    Enumeration<TreeModelListener> listenerCount = listeners.elements();
    while (listenerCount.hasMoreElements()) {
      TreeModelListener listener = listenerCount.nextElement();
      listener.treeNodesInserted(e);
    }
  }

  public void fireTreeNodesRemoved(TreeModelEvent e) {
    Enumeration<TreeModelListener> listenerCount = listeners.elements();
    while (listenerCount.hasMoreElements()) {
      TreeModelListener listener = listenerCount.nextElement();
      listener.treeNodesRemoved(e);
    }

  }

  public void fireTreeNodesChanged(TreeModelEvent e) {
    Enumeration<TreeModelListener> listenerCount = listeners.elements();
    while (listenerCount.hasMoreElements()) {
      TreeModelListener listener = listenerCount.nextElement();
      listener.treeNodesChanged(e);
    }

  }

  public void fireTreeStructureChanged(TreeModelEvent e) {
    Enumeration<TreeModelListener> listenerCount = listeners.elements();
    while (listenerCount.hasMoreElements()) {
      TreeModelListener listener = listenerCount.nextElement();
      listener.treeStructureChanged(e);
    }

  }
}
