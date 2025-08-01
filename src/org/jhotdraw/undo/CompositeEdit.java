/*
 * @(#)CompositeModel.java 1.1  2006-06-20
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

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * This is basically the same as javax.swing.undo.CompoundEdit but
 * it has a slightly different behaviour:
 * The compound edit ends, when it is added to itself. This way it
 * can be fired two times to an UndoManager: The first time, when
 * a sequence of compoundable edits starts, end the last time, when
 * the sequence is over.
 * <p>
 * For example:
 * <pre>
 * // fire CompositeEdit at start of sequence
 * CompositeEdit ce = new CompositeEdit();
 * fireUndoableEditEvent(ce);
 *
 * ...fire edits which shall compounded here...
 *
 * // fire CompositeEdit at end of sequence again, to end it.
 * fireUndoableEditEvent(ce);
 * </pre>
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-06-20 Method setSignificant added.
 * <br>1.0 2001-01-01 Created.
 */
public class CompositeEdit extends CompoundEdit {
  private String presentationName;
  private boolean isSignificant;
  private boolean isVerbose;

  public void setVerbose(boolean b) {
    isVerbose = b;
  }

  /**
   * Creates new CompositeEdit.
   * Which uses CompoundEdit.getPresentationName.
   *
   * @see javax.swing.undo.CompoundEdit#getPresentationName()
   */
  public CompositeEdit() {
    isSignificant = true;
  }

  /**
   * Creates new CompositeEdit.
   * Which uses CompoundEdit.getPresentationName.
   *
   * @see javax.swing.undo.CompoundEdit#getPresentationName()
   */
  public CompositeEdit(boolean isSignificant) {
    this.isSignificant = isSignificant;
  }

  /**
   * Creates new CompositeEdit.
   * Which uses the given presentation name.
   * If the presentation name is null, then CompoundEdit.getPresentationName
   * is used.
   *
   * @see javax.swing.undo.CompoundEdit#getPresentationName()
   */
  public CompositeEdit(String presentationName) {
    this.presentationName = presentationName;
    isSignificant = true;
  }

  /**
   * Creates new CompositeEdit.
   * Which uses the given presentation name.
   * If the presentation name is null, then CompoundEdit.getPresentationName
   * is used.
   *
   * @see javax.swing.undo.CompoundEdit#getPresentationName()
   */
  public CompositeEdit(String presentationName, boolean isSignificant) {
    this.presentationName = presentationName;
    this.isSignificant = isSignificant;
  }

  /**
   * Returns the presentation name.
   * If the presentation name is null, then CompoundEdit.getPresentationName
   * is returned.
   *
   * @see javax.swing.undo.CompoundEdit#getPresentationName()
   */
  @Override
  public String getPresentationName() {
    return (presentationName != null) ? presentationName : super.getPresentationName();
  }

  /**
   * Returns the undo presentation name.
   * If the presentation name is null, then CompoundEdit.getUndoPresentationName
   * is returned.
   *
   * @see javax.swing.undo.CompoundEdit#getUndoPresentationName()
   */
  @Override
  public String getUndoPresentationName() {
    return ((presentationName != null) ? UndoRedoManager.getLabels().getString("undo") + " " + presentationName : super.getUndoPresentationName());
  }

  /**
   * Returns the redo presentation name.
   * If the presentation name is null, then CompoundEdit.getRedoPresentationName
   * is returned.
   *
   * @see javax.swing.undo.CompoundEdit#getRedoPresentationName()
   */
  @Override
  public String getRedoPresentationName() {
    return ((presentationName != null) ? UndoRedoManager.getLabels().getString("redo") + " " + presentationName : super.getRedoPresentationName());
  }

  /**
   * If this edit is inProgress, accepts anEdit and returns
   * true.
   *
   * <p>The last edit added to this CompositeEdit is given a
   * chance to addEdit(anEdit). If it refuses (returns false), anEdit is
   * given a chance to replaceEdit the last edit. If anEdit returns
   * false here, it is added to edits.</p>
   *
   * <p>If the CompositeEdit is added to itself, then method end()
   * is called, and true is returned.</p>
   */
  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (anEdit == this) {
      end();
      return true;
    } else if (isInProgress() && (anEdit instanceof CompositeEdit)) {
      return true;
    } else {
      return super.addEdit(anEdit);
    }
  }

  /**
   * Returns false if this edit is insignificant - for example one
   * that maintains the user's selection, but does not change
   * any model state.
   */
  @Override
  public boolean isSignificant() {
    return isSignificant && super.isSignificant();
  }

  public void setSignificant(boolean newValue) {
    isSignificant = newValue;
  }
}
