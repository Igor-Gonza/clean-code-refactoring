/*
 * @(#)SaveAction.java  1.2.1  2006-07-25
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

package org.jhotdraw.app.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.Project;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.io.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.Worker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * SaveAction.
 *
 * @author Werner Randelshofer
 * @version 1.2.1 2006-07-25 Add saved file to recent file list of application.
 * <br>1.2 2006-05-19 Make filename acceptable by ExtensionFileFilter.
 * <br>1.1 2006-02-23 Support multiple open id.
 * <br>1.0 28. September 2005 Created.
 */
@SuppressWarnings("unused")
public class SaveAction extends AbstractProjectAction {
  public final static String ID = "save";
  private final boolean saveAs;
  private Component oldFocusOwner;

  /**
   * Creates a new instance.
   */
  public SaveAction(Application app) {
    this(app, false);
  }

  /**
   * Creates a new instance.
   */
  public SaveAction(Application app, boolean saveAs) {
    super(app);
    this.saveAs = saveAs;
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
    labels.configureAction(this, ID);
  }


  public void actionPerformed(ActionEvent evt) {
    final Project project = getCurrentProject();
    if (project.isEnabled()) {
      oldFocusOwner = SwingUtilities.getWindowAncestor(project.getComponent()).getFocusOwner();
      project.setEnabled(false);

      File saveToFile;
      if (!saveAs && project.getFile() != null) {
        saveToFile(project, project.getFile());
      } else {
        JFileChooser fileChooser = project.getSaveChooser();

        JSheet.showSaveSheet(fileChooser, project.getComponent(), evt1 -> {
          if (evt1.getOption() == JFileChooser.APPROVE_OPTION) {
            final File file;
            if (evt1.getFileChooser().getFileFilter() instanceof ExtensionFileFilter) {
              file = ((ExtensionFileFilter) evt1.getFileChooser().getFileFilter()).makeAcceptable(evt1.getFileChooser().getSelectedFile());
            } else {
              file = evt1.getFileChooser().getSelectedFile();
            }
            saveToFile(project, file);
          } else {
            project.setEnabled(true);
            if (oldFocusOwner != null) {
              oldFocusOwner.requestFocus();
            }
          }
        });
      }
    }
  }

  protected void saveToFile(final Project project, final File file) {
    project.execute(new Worker() {
      public Object construct() {
        try {
          project.write(file);
          return null;
        } catch (IOException e) {
          return e;
        }
      }

      public void finished(Object value) {
        fileSaved(project, file, value);
      }
    });
  }

  /**
   * XXX - Change type of value to Throwable
   *
   * @param value is either null for success or a Throwable on failure.
   */
  protected void fileSaved(final Project project, File file, Object value) {
    if (value == null) {
      project.setFile(file);
      project.markChangesAsSaved();
      int multiOpenId = 1;
      for (Project p : project.getApplication().projects()) {
        if (p != project && p.getFile() != null && p.getFile().equals(file)) {
          multiOpenId = Math.max(multiOpenId, p.getMultipleOpenId() + 1);
        }
      }
      getApplication().addRecentFile(file);
      project.setMultipleOpenId(multiOpenId);
    } else {
      JSheet.showMessageSheet(project.getComponent(), "<html>" + UIManager.getString("OptionPane.css") + "<b>Couldn't save to the file \"" + file + "\".<p>" + "Reason: " + value, JOptionPane.ERROR_MESSAGE);
    }
    project.setEnabled(true);
    SwingUtilities.getWindowAncestor(project.getComponent()).toFront();
    if (oldFocusOwner != null) {
      oldFocusOwner.requestFocus();
    }
  }
}