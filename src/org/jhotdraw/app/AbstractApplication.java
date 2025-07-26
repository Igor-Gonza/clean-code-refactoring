/*
 * @(#)AbstractApplication.java  1.1  2006-05-01
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

package org.jhotdraw.app;

import org.jhotdraw.beans.AbstractBean;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.prefs.Preferences;

/**
 * AbstractApplication.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-05-01 System.exit(0) explicitly in method stop().
 * <br>1.0 October 4, 2005, Created.
 */
@SuppressWarnings("unused")
public abstract class AbstractApplication extends AbstractBean implements Application {
  private final LinkedList<Project> projects = new LinkedList<>();
  private Collection<Project> unmodifiableDocuments;
  private boolean isEnabled = true;
  protected ResourceBundleUtil labels;
  private ApplicationModel model;
  private final LinkedList<File> recentFiles = new LinkedList<>();
  private final static int MAX_RECENT_FILES_COUNT = 10;
  private Preferences prefs;

  /**
   * Creates a new instance.
   */
  public AbstractApplication() {
  }

  public void init() {
    prefs = Preferences.userNodeForPackage((getModel() == null) ? getClass() : getModel().getClass());

    int count = prefs.getInt("recentFileCount", 0);
    for (int i = 0; i < count; i++) {
      String path = prefs.get("recentFile." + i, null);
      if (path != null) {
        recentFiles.add(new File(path));
      }
    }

    if (model != null) {
      model.initApplication(this);
    }
  }

  public void start() {
    Project p = createProject();
    add(p);
    show(p);
  }

  public final Project createProject() {
    Project p = basicCreateProject();
    p.init();
    if (getModel() != null) {
      getModel().initProject(this, p);
    }
    initProjectActions(p);
    return p;
  }

  public void setModel(ApplicationModel newValue) {
    ApplicationModel oldValue = model;
    model = newValue;
    firePropertyChange("model", oldValue, newValue);
  }

  public ApplicationModel getModel() {
    return model;
  }

  protected Project basicCreateProject() {
    return model.createProject();
  }

  public String getName() {
    return model.getName();
  }

  public String getVersion() {
    return model.getVersion();
  }

  public String getCopyright() {
    return model.getCopyright();
  }

  protected abstract void initProjectActions(Project p);

  public void stop() {
    for (Project p : new LinkedList<>(projects())) {
      dispose(p);
    }
    System.exit(0);
  }

  public void remove(Project p) {
    hide(p);
    int oldCount = projects.size();
    projects.remove(p);
    p.setApplication(null);
    firePropertyChange("projectCount", oldCount, projects.size());
  }

  public void add(Project p) {
    if (p.getApplication() != this) {
      int oldCount = projects.size();
      projects.add(p);
      p.setApplication(this);
      firePropertyChange("projectCount", oldCount, projects.size());
    }
  }

  public void dispose(Project p) {
    remove(p);
    p.dispose();
  }

  public Collection<Project> projects() {
    if (unmodifiableDocuments == null) {
      unmodifiableDocuments = Collections.unmodifiableCollection(projects);
    }
    return unmodifiableDocuments;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(boolean newValue) {
    boolean oldValue = isEnabled;
    isEnabled = newValue;
    firePropertyChange("enabled", oldValue, newValue);
  }

  public Container createContainer() {
    return new JFrame();
  }

  public void launch(String[] args) {
    configure(args);
    SwingUtilities.invokeLater(() -> {
      init();
      start();
    });
  }

  protected void initLabels() {
    labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
  }

  public void configure(String[] args) {
  }

  public java.util.List<File> recentFiles() {
    return Collections.unmodifiableList(recentFiles);
  }

  public void clearRecentFiles() {
    java.util.List<File> oldValue = new ArrayList<>(recentFiles);
    recentFiles.clear();
    prefs.putInt("recentFileCount", 0);
    firePropertyChange("recentFiles", Collections.unmodifiableList(oldValue), Collections.unmodifiableList(recentFiles));
  }

  public void addRecentFile(File file) {
    java.util.List<File> oldValue = new ArrayList<>(recentFiles);
    recentFiles.remove(file);
    recentFiles.addFirst(file);
    if (recentFiles.size() > MAX_RECENT_FILES_COUNT) {
      recentFiles.removeLast();
    }

    prefs.putInt("recentFileCount", recentFiles.size());
    int i = 0;
    for (File f : recentFiles) {
      prefs.put("recentFile." + i, f.getPath());
      i++;
    }

    firePropertyChange("recentFiles", oldValue, 0);
    firePropertyChange("recentFiles", Collections.unmodifiableList(oldValue), Collections.unmodifiableList(recentFiles));
  }
}
