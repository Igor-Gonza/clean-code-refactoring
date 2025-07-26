/*
 * @(#)LineFigure.java  2.0  2006-02-27
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

package org.jhotdraw.draw.figures;

import org.jhotdraw.draw.handlers.BezierNodeHandle;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.geom.BezierPath;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

/**
 * LineFigure.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-02-27 Support point editing at handle level 0.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class LineFigure extends BezierFigure {

  /**
   * Creates a new instance.
   */
  public LineFigure() {
    basicAddNode(new BezierPath.Node(new Point2D.Double(0, 0)));
    basicAddNode(new BezierPath.Node(new Point2D.Double(0, 0)));
  }

  // DRAWING
  // SHAPE AND BOUNDS
  // ATTRIBUTES
  // EDITING
  // TODO Wonder why this exists
  public Collection<Handle> createHandles(int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<>();
    switch (detailLevel) {
      case 0:
        for (int i = 0, n = path.size(); i < n; i++) {
          handles.add(new BezierNodeHandle(this, i));
        }
        break;
    }
    return handles;
  }

  // CONNECTING
  // COMPOSITE FIGURES
  // CLONING
  // EVENT HANDLING
  public boolean canConnect() {
    return false;
  }

  /**
   * Handles a mouse click.
   */
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    if (evt.getClickCount() == 2 && view.getHandleDetailLevel() == 0) {
      willChange();
      final int index = basicSplitSegment(p, (float) (5f / view.getScaleFactor()));
      if (index != -1) {
        final BezierPath.Node newNode = getNode(index);
        fireUndoableEditHappened(new AbstractUndoableEdit() {
          public void redo() throws CannotRedoException {
            super.redo();
            willChange();
            basicAddNode(index, newNode);
            changed();
          }

          public void undo() throws CannotUndoException {
            super.undo();
            willChange();
            basicRemoveNode(index);
            changed();
          }

        });
        changed();
        return true;
      }
    }
    return false;
  }
}
