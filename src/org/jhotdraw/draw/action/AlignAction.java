/*
 * @(#)AlignAction.java  2.0  2006-01-15
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

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.edits.TransformEdit;
import org.jhotdraw.undo.CompositeEdit;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * Aligns the selected figures.
 * <p>
 * XXX - Fire edit events
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-15 Changed to support double precision coordinates.
 * <br>1.0 17. Marz 2004  Created.
 */
public abstract class AlignAction extends AbstractSelectedAction {

  /**
   * Creates a new instance.
   */
  public AlignAction(DrawingEditor editor) {
    super(editor);
  }

  public void updateEnabledState() {
    if (getView() != null) {
      setEnabled(getView().isEnabled() && getView().getSelectionCount() > 1);
    } else {
      setEnabled(false);
    }
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    CompositeEdit edit = new CompositeEdit(labels.getString("align"));
    fireUndoableEditHappened(edit);
    alignFigures(getView().getSelectedFigures(), getSelectionBounds());
    fireUndoableEditHappened(edit);
  }

  protected abstract void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds);

  /**
   * Returns the bounds of the selected figures.
   */
  protected Rectangle2D.Double getSelectionBounds() {
    Rectangle2D.Double bounds = null;
    for (Figure f : getView().getSelectedFigures()) {
      if (bounds == null) {
        bounds = f.getBounds();
      } else {
        bounds.add(f.getBounds());
      }
    }
    return bounds;
  }

  public static class North extends AlignAction {
    public North(DrawingEditor editor) {
      super(editor);
      labels.configureAction(this, "alignNorth");
    }

    protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
      double y = selectionBounds.y;
      for (Figure f : getView().getSelectedFigures()) {
        f.willChange();
        Rectangle2D.Double b = f.getBounds();
        AffineTransform tx = new AffineTransform();
        tx.translate(0, y - b.y);
        f.basicTransform(tx);
        f.changed();
        fireUndoableEditHappened(new TransformEdit(f, tx));
      }
    }
  }

  public static class East extends AlignAction {
    public East(DrawingEditor editor) {
      super(editor);
      labels.configureAction(this, "alignEast");
    }

    protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
      double x = selectionBounds.x + selectionBounds.width;
      for (Figure f : getView().getSelectedFigures()) {
        f.willChange();
        Rectangle2D.Double b = f.getBounds();
        AffineTransform tx = new AffineTransform();
        tx.translate(x - b.x - b.width, 0);
        f.basicTransform(tx);
        f.changed();
        fireUndoableEditHappened(new TransformEdit(f, tx));
      }
    }
  }

  public static class West extends AlignAction {
    public West(DrawingEditor editor) {
      super(editor);
      labels.configureAction(this, "alignWest");
    }

    protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
      double x = selectionBounds.x;
      for (Figure f : getView().getSelectedFigures()) {
        f.willChange();
        Rectangle2D.Double b = f.getBounds();
        AffineTransform tx = new AffineTransform();
        tx.translate(x - b.x, 0);
        f.basicTransform(tx);
        f.changed();
        fireUndoableEditHappened(new TransformEdit(f, tx));
      }
    }
  }

  public static class South extends AlignAction {
    public South(DrawingEditor editor) {
      super(editor);
      labels.configureAction(this, "alignSouth");
    }

    protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
      double y = selectionBounds.y + selectionBounds.height;
      for (Figure f : getView().getSelectedFigures()) {
        f.willChange();
        Rectangle2D.Double b = f.getBounds();
        AffineTransform tx = new AffineTransform();
        tx.translate(0, y - b.y - b.height);
        f.basicTransform(tx);
        f.changed();
        fireUndoableEditHappened(new TransformEdit(f, tx));
      }
    }
  }

  public static class Vertical extends AlignAction {
    public Vertical(DrawingEditor editor) {
      super(editor);
      labels.configureAction(this, "alignVertical");
    }

    protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
      double y = selectionBounds.y + selectionBounds.height / 2;
      for (Figure f : getView().getSelectedFigures()) {
        f.willChange();
        Rectangle2D.Double b = f.getBounds();
        AffineTransform tx = new AffineTransform();
        tx.translate(0, y - b.y - b.height / 2);
        f.basicTransform(tx);
        f.changed();
        fireUndoableEditHappened(new TransformEdit(f, tx));
      }
    }
  }

  public static class Horizontal extends AlignAction {
    public Horizontal(DrawingEditor editor) {
      super(editor);
      labels.configureAction(this, "alignHorizontal");
    }

    protected void alignFigures(Collection<Figure> selectedFigures, Rectangle2D.Double selectionBounds) {
      double x = selectionBounds.x + selectionBounds.width / 2;
      for (Figure f : getView().getSelectedFigures()) {
        f.willChange();
        Rectangle2D.Double b = f.getBounds();
        AffineTransform tx = new AffineTransform();
        tx.translate(x - b.x - b.width / 2, 0);
        f.basicTransform(tx);
        f.changed();
        fireUndoableEditHappened(new TransformEdit(f, tx));
      }
    }
  }
}