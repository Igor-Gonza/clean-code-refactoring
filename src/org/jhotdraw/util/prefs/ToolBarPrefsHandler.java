/*
 * @(#)ToolBarPrefsHandler.java  1.1  2006-06-15
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.util.prefs;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;

/**
 * ToolBarPrefsHandler.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-06-15 Code added to work with ToolBarUI's that don't extend
 * BasicToolBarUI.
 * <br>1.0 2. April 2004  Created.
 */
public class ToolBarPrefsHandler implements ComponentListener, AncestorListener {
  private final JToolBar toolbar;
  private final String prefsPrefix;
  private final Preferences prefs;
  private boolean firstTimeShown;

  public ToolBarPrefsHandler(JToolBar toolbar, String prefsPrefix, Preferences prefs) {
    this.toolbar = toolbar;
    this.prefsPrefix = prefsPrefix;
    this.prefs = prefs;

    String constraint = prefs.get(prefsPrefix + ".constraint", BorderLayout.NORTH);
    int orientation = (constraint.equals(BorderLayout.NORTH) || constraint.equals(BorderLayout.SOUTH)) ? JToolBar.HORIZONTAL : JToolBar.VERTICAL;
    toolbar.setOrientation(orientation);
    toolbar.getParent().add(constraint, toolbar);
    toolbar.setVisible(prefs.getBoolean(prefsPrefix + ".visible", true));
//        if (prefs.getBoolean(prefsPrefix+".isFloating", false)) {
//            makeToolBarFloat();
//        }
    toolbar.addComponentListener(this);
    toolbar.addAncestorListener(this);
  }

  /*
   * XXX - This does not work
  private void makeToolBarFloat() {
      BasicToolBarUI ui = (BasicToolBarUI) toolbar.getUI();
      Window window = SwingUtilities.getWindowAncestor(toolbar);
      System.out.println("Window Ancestor:"+window+" instanceof Frame:"+(window instanceof Frame));
      ui.setFloating(true, new Point(
      prefs.getInt(prefsPrefix+".floatingX", 0),
      prefs.getInt(prefsPrefix+".floatingY", 0)
      ));
      window = SwingUtilities.getWindowAncestor(toolbar);
      window.setLocation(
      prefs.getInt(prefsPrefix+".floatingX", 0),
      prefs.getInt(prefsPrefix+".floatingY", 0)
      );
      window.toFront();
  }*/
  public void componentHidden(ComponentEvent e) {
    prefs.putBoolean(prefsPrefix + ".visible", false);
  }

  public void componentMoved(ComponentEvent e) {
    locationChanged();
  }

  private void locationChanged() {
    // FIXME : use reflection to get hold of method 'isFloating'.
    if (toolbar.getUI() instanceof BasicToolBarUI) {
      BasicToolBarUI ui = (BasicToolBarUI) toolbar.getUI();
      boolean floating = ui.isFloating();
      prefs.putBoolean(prefsPrefix + ".isFloating", floating);
      if (floating) {
        Window window = SwingUtilities.getWindowAncestor(toolbar);
        prefs.putInt(prefsPrefix + ".floatingX", window.getX());
        prefs.putInt(prefsPrefix + ".floatingY", window.getY());
      } else if (toolbar.getParent() != null) {
        int x = toolbar.getX();
        int y = toolbar.getY();
        Insets insets = toolbar.getParent().getInsets();
        String constraint;
        if (x == insets.left && y == insets.top) {
          constraint = (toolbar.getOrientation() == SwingConstants.HORIZONTAL) ? BorderLayout.NORTH : BorderLayout.WEST;
        } else {
          constraint = (toolbar.getOrientation() == SwingConstants.HORIZONTAL) ? BorderLayout.SOUTH : BorderLayout.EAST;
        }
        prefs.put(prefsPrefix + ".constraint", constraint);
      }
    } else {
      if (toolbar.getParent() != null) {
        int x = toolbar.getX();
        int y = toolbar.getY();
        Insets insets = toolbar.getParent().getInsets();
        String constraint;
        if (x == insets.left && y == insets.top) {
          constraint = (toolbar.getOrientation() == SwingConstants.HORIZONTAL) ? BorderLayout.NORTH : BorderLayout.WEST;
        } else {
          constraint = (toolbar.getOrientation() == SwingConstants.HORIZONTAL) ? BorderLayout.SOUTH : BorderLayout.EAST;
        }
        prefs.put(prefsPrefix + ".constraint", constraint);
      }
    }
  }

  public void componentResized(ComponentEvent e) {
    locationChanged();
  }

  public void componentShown(ComponentEvent e) {
    prefs.putBoolean(prefsPrefix + ".visible", true);
  }

  public void ancestorAdded(AncestorEvent event) {
    locationChanged();
  }

  public void ancestorMoved(AncestorEvent event) {
    if (toolbar.getUI() instanceof BasicToolBarUI && ((BasicToolBarUI) toolbar.getUI()).isFloating()) {
        locationChanged();
      }

  }

  public void ancestorRemoved(AncestorEvent event) {
    if (toolbar.getUI() instanceof BasicToolBarUI && ((BasicToolBarUI) toolbar.getUI()).isFloating()) {
        locationChanged();
      }

  }
}
