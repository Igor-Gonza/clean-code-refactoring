/*
 * @(#)LoadRecentAction.java  1.0  June 15, 2006
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

package org.jhotdraw.app.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.util.Worker;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * LoadRecentAction.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 15, 2006, Created.
 */
@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class LoadRecentAction extends SaveBeforeAction {
  public final static String ID = "loadRecent";
  private final File file;

  /**
   * Creates a new instance.
   */
  public LoadRecentAction(Application app, File file) {
    super(app);
    this.file = file;
    putValue(Action.NAME, file.getName());
  }

  public void doIt(final Project project) {
    final Application app = getApplication();
    app.setEnabled(true);

    // If there is another project with we set the multiple open
    // id of our project to max(multiple open id) + 1.
    int multipleOpenId = 1;
    for (Project aProject : app.projects()) {
      if (aProject != project && aProject.getFile() != null && aProject.getFile().equals(file)) {
        multipleOpenId = Math.max(multipleOpenId, aProject.getMultipleOpenId() + 1);
      }
    }
    project.setMultipleOpenId(multipleOpenId);

    // Open the file
    project.execute(new Worker() {
      public Object construct() {
        try {
          project.read(file);
          return null;
        } catch (Throwable e) {
          return e;
        }
      }

      public void finished(Object value) {
        fileOpened(project, file, value);
      }
    });
  }

  protected void fileOpened(final Project project, File file, Object value) {
    final Application app = getApplication();
    if (value == null) {
      project.setFile(file);
      project.setEnabled(true);
      Frame w = (Frame) SwingUtilities.getWindowAncestor(project.getComponent());
      if (w != null) {
        w.setExtendedState(w.getExtendedState() & ~Frame.ICONIFIED);
        w.toFront();
      }
      project.getComponent().requestFocus();
      if (app != null) {
        app.setEnabled(true);
      }
    } else {
      if (value instanceof Throwable) {
        ((Throwable) value).printStackTrace();
      }
      JSheet.showMessageSheet(project.getComponent(), "<html>" + UIManager.getString("OptionPane.css") + "<b>Couldn't open the file \"" + file + "\".</b><br>" + value, JOptionPane.ERROR_MESSAGE, evt -> {
        // app.dispose(project);
      });
    }
  }
}
