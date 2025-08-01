/*
 * @(#)TransformEdit.java  2.0  2006-01-14
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

import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * TransformEdit.
 * <p>
 * FIXME - When we do group moves or moves of a composite figure we fail to
 * coalesce the TransformEdit events. This may exhaust memory!
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
@SuppressWarnings("CallToPrintStackTrace")
public class TransformEdit extends AbstractUndoableEdit {
  /**
   * Implementation note: Owner has package access, because it is accessed
   * by CompositeMoveEdit.
   */
  private final Collection<Figure> figures;
  private final AffineTransform tx;

  public TransformEdit(Figure figure, AffineTransform tx) {
    figures = new LinkedList<>();
    figures.add(figure);
    this.tx = (AffineTransform) tx.clone();
  }

  public TransformEdit(Collection<Figure> figures, AffineTransform tx) {
    this.figures = figures;
    this.tx = (AffineTransform) tx.clone();
  }

  @Override
  public String getPresentationName() {
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    return labels.getString("transformFigure");
  }

  @Override
  public boolean addEdit(UndoableEdit anEdit) {
    if (anEdit instanceof TransformEdit) {
      TransformEdit that = (TransformEdit) anEdit;
      if (that.figures == this.figures) {
        this.tx.concatenate(that.tx);
        that.die();
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean replaceEdit(UndoableEdit anEdit) {
    if (anEdit instanceof TransformEdit) {
      TransformEdit that = (TransformEdit) anEdit;
      if (that.figures == this.figures) {
        this.tx.preConcatenate(that.tx);
        that.die();
        return true;
      }
    }
    return false;
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    for (Figure f : figures) {
      f.willChange();
      f.basicTransform(tx);
      f.changed();

    }
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    try {
      AffineTransform inverse = tx.createInverse();
      for (Figure f : figures) {
        f.willChange();
        f.basicTransform(inverse);
        f.changed();
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return getClass().getName() + '@' + hashCode() + " tx:" + tx;
  }
}
