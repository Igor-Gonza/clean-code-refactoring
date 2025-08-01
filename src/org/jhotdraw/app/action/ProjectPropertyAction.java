/*
 * @(#)ProjectPropertyAction.java  1.0  June 18, 2006
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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

/**
 * ProjectPropertyAction.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 18, 2006, Created.
 */
public class ProjectPropertyAction extends AbstractProjectAction {
  private final String propertyName;
  private final Class<?>[] parameterClass;
  private final Object propertyValue;
  private final String setterName;
  private final String getterName;

  private final PropertyChangeListener projectListener = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent evt) {
      if (Objects.equals(evt.getPropertyName(), propertyName)) { // Strings get interned
        updateSelectedState();
      }
    }
  };

  /**
   * Creates a new instance.
   */
  public ProjectPropertyAction(Application app, String propertyName, Object propertyValue) {
    this(app, propertyName, propertyValue.getClass(), propertyValue);
  }

  public ProjectPropertyAction(Application app, String propertyName, Class<?> propertyClass, Object propertyValue) {
    super(app);
    this.propertyName = propertyName;
    this.parameterClass = new Class<?>[]{propertyClass};
    this.propertyValue = propertyValue;
    setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    getterName = ((propertyClass == Boolean.TYPE || propertyClass == Boolean.class) ? "is" : "get") + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    updateSelectedState();
  }

  public void actionPerformed(ActionEvent evt) {
    Project p = getCurrentProject();
    try {
      p.getClass().getMethod(setterName, parameterClass).invoke(p, propertyValue);
    } catch (Throwable e) {
      throw new InternalError("Method invocation failed", e);
    }
  }

  protected void installProjectListeners(Project p) {
    super.installProjectListeners(p);
    p.addPropertyChangeListener(projectListener);
    updateSelectedState();
  }

  /**
   * Installs listeners on the project object.
   */
  protected void uninstallProjectListeners(Project p) {
    super.uninstallProjectListeners(p);
    p.removePropertyChangeListener(projectListener);
  }

  private void updateSelectedState() {
    boolean isSelected = false;
    Project p = getCurrentProject();
    if (p != null) {
      try {
        Object value = p.getClass().getMethod(getterName, (Class<?>[]) null).invoke(p);
        isSelected = Objects.equals(value, propertyValue);
      } catch (Throwable e) {
        throw new InternalError("Method invocation failed", e);
      }
    }
    putValue(Actions.SELECTED_KEY, isSelected);
  }
}
