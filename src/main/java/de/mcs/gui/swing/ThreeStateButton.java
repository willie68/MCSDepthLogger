/**
 * MCS Media Computer Software
 * GUI Element: Three State Button
 * Copyright 2012 by Wilfried Klaas
 * Project: GUITest
 * File: ThreeStateButton.java
 * Created: 26.04.2012 Willie
 * EMail: W.Klaas@gmx.de
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * @author Willie
 * 
 */
public class ThreeStateButton extends JLabel {

  public interface StateChangeListener {

    void stateChanged(StateChangeEvent event);

  }

  public class StateChangeEvent {

    private JComponent source;
    private STATE state;
    private STATE oldState;

    public StateChangeEvent(JComponent source) {
      this.setSource(source);
    }

    public void setOldState(STATE state) {
      this.oldState = state;
    }

    public void setState(STATE state) {
      this.state = state;
    }

    /**
     * @return the source
     */
    public JComponent getSource() {
      return source;
    }

    /**
     * @param source
     *          the source to set
     */
    public void setSource(JComponent source) {
      this.source = source;
    }

    /**
     * @return the state
     */
    public STATE getState() {
      return state;
    }

    /**
     * @return the oldState
     */
    public STATE getOldState() {
      return oldState;
    }

  }

  public enum STATE {
    STATE_1, STATE_2, STATE_3
  }

  private ImageIcon icon1;
  private ImageIcon icon2;
  private ImageIcon icon3;
  private STATE state;
  private ArrayList<StateChangeListener> stateChangeListeners;
  private boolean enabled;

  protected ThreeStateButton() {
    super();
    state = STATE.STATE_1;
    initComponent();
  }

  public ThreeStateButton(ImageIcon icon1, ImageIcon icon2, ImageIcon icon3) {
    super();
    state = STATE.STATE_1;
    this.icon1 = icon1;
    this.icon2 = icon2;
    this.icon3 = icon3;
    initComponent();
  }

  private void initComponent() {
    enabled = true;
    state = STATE.STATE_1;
    setIcon();
    addMouseListener();
    stateChangeListeners = new ArrayList<StateChangeListener>();
  }

  private void addMouseListener() {
    addMouseListener(new MouseAdapter() {

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (enabled) {
          nextState();
        }
      }
    });
  }

  public void setState1Icon(ImageIcon icon) {
    icon1 = icon;
  }

  public void setState2Icon(ImageIcon icon) {
    icon2 = icon;
  }

  public void setState3Icon(ImageIcon icon) {
    icon3 = icon;
  }

  public STATE getState() {
    return state;
  }

  public void setState(STATE state) {
    this.state = state;
    setIcon();
  }

  private void setIcon() {
    if (state != null) {
      switch (state) {
      case STATE_1:
        if (icon1 != null) {
          super.setIcon(icon1);
        }
        break;
      case STATE_2:
        if (icon2 != null) {
          super.setIcon(icon2);
        }
        break;
      case STATE_3:
        if (icon3 != null) {
          super.setIcon(icon3);
        }
        break;
      default:
        if (icon1 != null) {
          super.setIcon(icon1);
        }
      }
    }
  }

  public void nextState() {
    StateChangeEvent event = new StateChangeEvent(this);
    event.setOldState(state);
    switch (state) {
    case STATE_1:
      state = STATE.STATE_2;
      break;
    case STATE_2:
      state = STATE.STATE_3;
      break;
    case STATE_3:
      state = STATE.STATE_1;
      break;
    default:
      state = STATE.STATE_1;
    }
    setIcon();
    event.setState(state);
    for (StateChangeListener listener : stateChangeListeners) {
      listener.stateChanged(event);
    }
  }

  public void addStateChangedListener(StateChangeListener stateChangeListener) {
    if (!stateChangeListeners.contains(stateChangeListener)) {
      stateChangeListeners.add(stateChangeListener);
    }
  }

  public void removeStateChangedListener(StateChangeListener stateChangeListener) {
    stateChangeListeners.remove(stateChangeListener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JLabel#setIcon(javax.swing.Icon)
   */
  @Override
  public void setIcon(Icon icon) {
    setIcon();
  }

  /**
   * @return the enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param enabled
   *          the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
