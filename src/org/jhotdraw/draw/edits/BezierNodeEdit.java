/*
 * @(#)BezierNodeEdit.java 1.0  2006-08-24
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

import org.jhotdraw.draw.figures.BezierFigure;
import org.jhotdraw.geom.BezierPath;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * BezierNodeEdit.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-06-24
 */
public class BezierNodeEdit extends AbstractUndoableEdit {
  private final BezierFigure owner;
  private final int index;
  private BezierPath.Node oldValue;
  private BezierPath.Node newValue;

  public BezierNodeEdit(BezierFigure owner, int index, BezierPath.Node oldValue, BezierPath.Node newValue) {
    this.owner = owner;
    this.index = index;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  // FIXME German (holy moly!)
  @Override
  public String getPresentationName() {
    return "Punkt verschieben";
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    owner.willChange();
    owner.basicSetNode(index, newValue);
    owner.changed();
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    owner.willChange();
    owner.basicSetNode(index, oldValue);
    owner.changed();
  }

  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (anEdit instanceof BezierNodeEdit) {
      BezierNodeEdit that = (BezierNodeEdit) anEdit;
      if (that.owner == this.owner && that.index == this.index) {
        this.newValue = that.newValue;
        that.die();
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean replaceEdit(UndoableEdit anEdit) {
    if (anEdit instanceof BezierNodeEdit) {
      BezierNodeEdit that = (BezierNodeEdit) anEdit;
      if (that.owner == this.owner && that.index == this.index) {
        that.oldValue = this.oldValue;
        this.die();
        return true;
      }
    }
    return false;
  }
}