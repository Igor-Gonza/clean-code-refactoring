/*
 * @(#)UndoRedoManager.java 1.3.1  2006-04-12
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

package org.jhotdraw.undo;

import com.sun.istack.internal.logging.Logger;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;

/**
 * Same as javax.swing.UndoManager but provides actions for undo and
 * redo operations.
 *
 * @author Werner Randelshofer
 * @version 1.3.1 2006-04-12 Method setLocale added.
 * <br>1.3 2006-02-28 Support for PropertyChangeListener added.
 * <br>1.2.2 2006-02-25 Configure actions from resource bundle.
 * <br>1.2.1 2003-11-02 Adapted to changes in ResourceBundleUtil.
 * <br>1.2 2003-03-16 Texts and accelerator keys are now read from a
 * resource bundle.
 * <br>1.1.2 2003-03-12 Actions use now KeyStroke objects instead of
 * String objects as accelerator keys.
 * <br>1.1.1 2002-05-10 Method addEdit is now smarter when
 * determining whether an edit is significant.
 * <br>1.1 2002-04-08 Method hasSignificantEdits/clearChanged added.
 * <br>1.0 2001-10-09
 */
public class UndoRedoManager extends UndoManager {
  static final Logger logger = Logger.getLogger(UndoRedoManager.class);
  protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
  private static final boolean DEBUG = false;

  /**
   * The resource bundle used for internationalisation.
   */
  private static ResourceBundleUtil labels;
  /**
   * This flag is set to true when at
   * least one significant UndoableEdit
   * has been added to the manager since the
   * last call to discardAllEdits.
   */
  private boolean hasSignificantEdits = false;

  /**
   * This flag is set to true when an undo or redo
   * operation is in progress. The UndoRedoManager
   * ignores all incoming UndoableEdit events while
   * this flag is true.
   */
  private boolean undoOrRedoInProgress;

  /**
   * Sending this UndoableEdit event to the UndoRedoManager
   * disables the Undo and Redo functions of the manager.
   */
  public static final UndoableEdit DISCARD_ALL_EDITS = new AbstractUndoableEdit() {
    @Override
    public boolean canUndo() {
      return false;
    }

    @Override
    public boolean canRedo() {
      return false;
    }
  };

  /**
   * Undo Action for use in a menu bar.
   */
  private class UndoAction extends AbstractAction {
    public UndoAction() {
      labels.configureAction(this, "undo");
      setEnabled(false);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent evt) {
      try {
        undo();
      } catch (CannotUndoException e) {
        logger.info("UndoRedoManager - Cannot undo: " + e.getMessage());
      }
    }

  }

  /**
   * Redo Action for use in a menu bar.
   */
  private class RedoAction extends AbstractAction {
    public RedoAction() {
      labels.configureAction(this, "redo");
      setEnabled(false);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent evt) {
      try {
        redo();
      } catch (CannotRedoException e) {
        logger.info("UndoRedoManager - Cannot redo: " + e.getMessage());
      }
    }

  }

  /**
   * The undo action instance.
   */
  private final UndoAction undoAction;
  /**
   * The redo action instance.
   */
  private final RedoAction redoAction;

  public static ResourceBundleUtil getLabels() {
    if (labels == null) {
      labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.undo.Labels");
    }
    return labels;
  }

  /**
   * Creates new UndoRedoManager
   */
  public UndoRedoManager() {
    getLabels();
    undoAction = new UndoAction();
    redoAction = new RedoAction();
  }

  public void setLocale(Locale l) {
    labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.undo.Labels", l);
  }

  /**
   * Discards all edits.
   */
  @Override
  public void discardAllEdits() {
    super.discardAllEdits();
    updateActions();
    setHasSignificantEdits(false);
  }

  public void setHasSignificantEdits(boolean newValue) {
    boolean oldValue = hasSignificantEdits;
    hasSignificantEdits = newValue;
    firePropertyChange("hasSignificantEdits", oldValue, newValue);
  }

  /**
   * Returns true if at least one significant UndoableEdit
   * has been added since the last call to discardAllEdits.
   */
  public boolean hasSignificantEdits() {
    return hasSignificantEdits;
  }

  /**
   * If inProgress, inserts anEdit at indexOfNextAdd, and removes
   * any old edits that were at indexOfNextAdd or later. The die
   * method is called on each edit that is removed is sent, in the
   * reverse of the order the edits were added. Updates
   * indexOfNextAdd.
   *
   * <p>If not inProgress, acts as a CompoundEdit</p>
   *
   * <p>Regardless of inProgress, if undoOrRedoInProgress,
   * calls die on each edit that is sent.</p>
   *
   * @see CompoundEdit#end
   * @see CompoundEdit#addEdit
   */
  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (DEBUG)
      logger.info("UndoRedoManager.add: " + anEdit);
    if (undoOrRedoInProgress) {
      anEdit.die();
      return true;
    }
    boolean success = super.addEdit(anEdit);
    updateActions();
    if (success && anEdit.isSignificant() && editToBeUndone() == anEdit) {
      setHasSignificantEdits(true);
    }
    return success;
  }

  /**
   * Gets the undo action for use as an Undo menu item.
   */
  public Action getUndoAction() {
    return undoAction;
  }

  /**
   * Gets the redo action for use as a Redo menu item.
   */
  public Action getRedoAction() {
    return redoAction;
  }

  /**
   * Updates the properties of the UndoAction
   * and of the RedoAction.
   */
  private void updateActions() {
    String label;
    if (DEBUG)
      logger.info("UndoManager.updateActions " + editToBeUndone() + " canUndo=" + canUndo() + " canRedo=" + canRedo());
    if (canUndo()) {
      undoAction.setEnabled(true);
      label = getUndoPresentationName();
    } else {
      undoAction.setEnabled(false);
      label = labels.getString("undo");
    }
    undoAction.putValue(Action.NAME, label);
    undoAction.putValue(Action.SHORT_DESCRIPTION, label);

    if (canRedo()) {
      redoAction.setEnabled(true);
      label = getRedoPresentationName();
    } else {
      redoAction.setEnabled(false);
      label = labels.getString("redo");
    }
    redoAction.putValue(Action.NAME, label);
    redoAction.putValue(Action.SHORT_DESCRIPTION, label);
  }

  /**
   * Undoes the last edit event.
   * The UndoRedoManager ignores all incoming UndoableEdit events,
   * while undo is in progress.
   */
  @Override
  public void undo() throws CannotUndoException {
    undoOrRedoInProgress = true;
    try {
      super.undo();
    } finally {
      undoOrRedoInProgress = false;
      updateActions();
    }
  }

  /**
   * Redoes the last undone edit event.
   * The UndoRedoManager ignores all incoming UndoableEdit events,
   * while redo is in progress.
   */
  @Override
  public void redo() throws CannotUndoException {
    undoOrRedoInProgress = true;
    try {
      super.redo();
    } finally {
      undoOrRedoInProgress = false;
      updateActions();
    }
  }

  /**
   * Undoes or redoes the last edit event.
   * The UndoRedoManager ignores all incoming UndoableEdit events,
   * while undo or redo is in progress.
   */
  @Override
  public void undoOrRedo() throws CannotUndoException, CannotRedoException {
    undoOrRedoInProgress = true;
    try {
      super.undoOrRedo();
    } finally {
      undoOrRedoInProgress = false;
      updateActions();
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(propertyName, listener);
  }

  protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }
}
