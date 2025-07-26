/*
 * @(#)FloatingPaletteHandler.java  1.1  2006-06-11
 *
 * Copyright (c) 2005-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 */

package org.jhotdraw.app;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Hides all registered floating palettes, if none of the registered project
 * windows has focus anymore.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-06-11 Palettes can now be any subclass of java.awt.Window.
 * <br>1.0 October 9, 2005, Created.
 */
@SuppressWarnings("unused")
public class OSXPaletteHandler {
  private final HashSet<Window> palettes = new HashSet<>();
  private final HashMap<Window, Project> windows = new HashMap<>();
  private static OSXPaletteHandler instance;
  private final javax.swing.Timer timer;
  private final DefaultOSXApplication app;
  private final WindowFocusListener focusHandler = new WindowFocusListener() {
    /**
     * Invoked when the Window is set to be the focused Window, which means
     * that the Window, or one of its subcomponents, will receive keyboard
     * events.
     */
    public void windowGainedFocus(WindowEvent e) {
      timer.stop();
      if (windows.containsKey(e.getWindow())) {
        app.setCurrentProject(windows.get(e.getWindow()));
        showPalettes();
      }
    }

    private void showPalettes() {
      for (Window palette : palettes) {
        if (!palette.isVisible()) {
          palette.setVisible(true);
        }
      }
    }

    /**
     * Invoked when the Window is no longer the focused Window, which means
     * that keyboard events will no longer be delivered to the Window or any of
     * its subcomponents.
     */
    public void windowLostFocus(WindowEvent e) {
      timer.restart();
    }
  };

  /**
   * Creates a new instance.
   */
  public OSXPaletteHandler(DefaultOSXApplication app) {
    this.app = app;
    timer = new javax.swing.Timer(60, evt -> maybeHidePalettes());
    timer.setRepeats(false);
  }

  public void add(Window window, Project project) {
    window.addWindowFocusListener(focusHandler);
    windows.put(window, project);
  }

  public void remove(Window window, Project project) {
    windows.remove(window);
    window.removeWindowFocusListener(focusHandler);
  }

  public void addPalette(Window palette) {
    palette.addWindowFocusListener(focusHandler);
    palettes.add(palette);
  }

  public void removePalette(Window palette) {
    palettes.remove(palette);
    palette.removeWindowFocusListener(focusHandler);
  }

  public Set<Window> getPalettes() {
    return Collections.unmodifiableSet(palettes);
  }

  private void showPalettes() {
    for (Window palette : palettes) {
      if (!palette.isVisible()) {
        palette.setVisible(true);
      }
    }
  }

  private boolean isFocused(Window w) {
    if (w.isFocused()) return true;
    Window[] ownedWindows = w.getOwnedWindows();
    for (Window ownedWindow : ownedWindows) {
      if (isFocused(ownedWindow)) {
        return true;
      }
    }
    return false;
  }

  private void maybeHidePalettes() {
    boolean hasFocus = false;
    for (Window window : windows.keySet()) {
      if (isFocused(window)) {
        hasFocus = true;
        break;
      }
    }
    if (!hasFocus && !windows.isEmpty()) {
      for (Window palette : palettes) {
        if (isFocused(palette)) {
          hasFocus = true;
          break;
        }
      }
    }
    if (!hasFocus) {
      for (Window palette : palettes) {
        palette.setVisible(false);
      }
    }
  }
}
