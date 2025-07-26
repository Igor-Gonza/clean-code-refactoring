/*
 * @(#)AbstractSelectedAction.java  3.1.1  2006-07-09
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

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.drawings.Drawing;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.events.FigureSelectionEvent;
import org.jhotdraw.draw.views.DrawingView;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

/**
 * Abstract super class for actions which act on the selected figures of a drawing
 * editor. If no figures are selected, the action is disabled.
 *
 * @author Werner Randelshofer
 * @version 3.1.1. 2006-07-09 Fixed enabled state.
 * <br>3.1 2006-03-15 Support for enabled state of view added.
 * <br>3.0 2006-02-24 Changed to support multiple views.
 * <br>2.0 2006-02-14 Updated to work with multiple views.
 * <br>1.0 2003-12-01 Created.
 */
public abstract class AbstractSelectedAction extends AbstractAction {
  private DrawingEditor editor;
  protected ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
  private final PropertyChangeListener propertyChangeHandler = evt -> {
    if (evt.getPropertyName().equals("enabled")) {
      updateEnabledState();
    }
  };

  private class EventHandler implements PropertyChangeListener, FigureSelectionListener {
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("focusedView")) {
        if (evt.getOldValue() != null) {
          org.jhotdraw.draw.views.DrawingView view = ((DrawingView) evt.getOldValue());
          view.removeFigureSelectionListener(this);
          view.removePropertyChangeListener(propertyChangeHandler);
        }
        if (evt.getNewValue() != null) {
          org.jhotdraw.draw.views.DrawingView view = ((org.jhotdraw.draw.views.DrawingView) evt.getNewValue());
          view.addFigureSelectionListener(this);
          view.addPropertyChangeListener(propertyChangeHandler);
        }
        updateEnabledState();
      }
    }

    public void selectionChanged(FigureSelectionEvent evt) {
      updateEnabledState();

    }
  }

  private final EventHandler eventHandler = new EventHandler();

  /**
   * Creates an action which acts on the selected figures on the current view
   * of the specified editor.
   */
  public AbstractSelectedAction(org.jhotdraw.draw.editors.DrawingEditor editor) {
    setEditor(editor);
    updateEnabledState();
  }

  protected void updateEnabledState() {
    if (getView() != null) {
      setEnabled(getView().isEnabled() && getView().getSelectionCount() > 0);
    } else {
      setEnabled(false);
    }
  }

  public void dispose() {
    if (this.editor != null) {
      this.editor.removePropertyChangeListener(eventHandler);
      if (this.editor.getView() != null) {
        this.editor.getView().removeFigureSelectionListener(eventHandler);
      }
    }
    this.editor = null;
  }

  public void setEditor(org.jhotdraw.draw.editors.DrawingEditor editor) {
    if (this.editor != null) {
      this.editor.removePropertyChangeListener(eventHandler);
      if (getView() != null) {
        getView().removeFigureSelectionListener(eventHandler);
      }
    }
    this.editor = editor;
    if (this.editor != null) {
      this.editor.addPropertyChangeListener(eventHandler);
    }
  }

  public org.jhotdraw.draw.editors.DrawingEditor getEditor() {
    return editor;
  }

  protected org.jhotdraw.draw.views.DrawingView getView() {
    return (editor == null) ? null : editor.getFocusedView();
  }

  protected Drawing getDrawing() {
    return (getView() == null) ? null : getView().getDrawing();
  }

  protected void fireUndoableEditHappened(UndoableEdit edit) {
    getDrawing().fireUndoableEditHappened(edit);
  }

}
