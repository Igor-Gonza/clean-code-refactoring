/*
 * @(#)CompositeTransformEdit.java  1.0  2006-01-21
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

package org.jhotdraw.draw.edits;

import com.sun.istack.internal.logging.Logger;
import org.jhotdraw.draw.figures.AbstractFigure;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * CompositeTransformEdit.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-01-21 Created.
 */
public class CompositeTransformEdit extends AbstractUndoableEdit {

  static final Logger logger = Logger.getLogger(CompositeTransformEdit.class);

  private final AbstractFigure owner;
  private final AffineTransform tx;
  /**
   * True if this edit has never received <code>end</code>.
   */
  boolean inProgress;

  public CompositeTransformEdit(AbstractFigure owner, AffineTransform tx) {
    this.owner = owner;
    this.tx = (AffineTransform) tx.clone();
    inProgress = true;
  }

  // FIXME German (holy moly!)
  @Override
  public String getPresentationName() {
    return "Figur transformieren";
  }

  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (anEdit == this) {
      end();
      return true;
    } else {
      if (!inProgress) {
        return false;
      } else {
        anEdit.die();
        return true;
      }
    }
  }

  @Override
  public boolean replaceEdit(UndoableEdit anEdit) {
    // TODO See how this might work (or not)
//    if (anEdit instanceof CompositeTransformEdit) {
//      CompositeTransformEdit that = (CompositeTransformEdit) anEdit;
//      if (that.owner == this.owner) {
//        this.tx.concatenate(that.tx);
//        that.die();
//        return true;
//      }
//    }
    return false;
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    owner.willChange();
    owner.basicTransform(tx);
    owner.changed();
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    owner.willChange();
    try {
      owner.basicTransform(tx.createInverse());
    } catch (NoninvertibleTransformException ex) {
      logger.info("CompositeTransformEdit - undo: " + ex.getMessage());
    }
    owner.changed();
  }

  /**
   * Returns true if this edit is in progress--that is, it has not
   * received end. This generally means that edits are still being
   * added to it.
   *
   * @see #end
   */
  public boolean isInProgress() {
    return inProgress;
  }

  /**
   * Sets <code>inProgress</code> to false.
   *
   * @see #canUndo
   * @see #canRedo
   */
  public void end() {
    inProgress = false;
  }

  /**
   * Returns false if <code>isInProgress</code> or if super
   * returns false.
   *
   * @see #isInProgress
   */
  @Override
  public boolean canUndo() {
    return !isInProgress() && super.canUndo();
  }

  /**
   * Returns false if <code>isInProgress</code> or if super
   * returns false.
   *
   * @see #isInProgress
   */
  @Override
  public boolean canRedo() {
    return !isInProgress() && super.canRedo();
  }
}
