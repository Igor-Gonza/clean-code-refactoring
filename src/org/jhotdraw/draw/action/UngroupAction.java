/*
 * @(#)UngroupAction.java  1.1  2006-07-12
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

import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.figures.CompositeFigure;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.figures.GroupFigure;
import org.jhotdraw.draw.views.DrawingView;
import org.jhotdraw.undo.CompositeEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;

/**
 * UngroupAction.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-07-12 Changed to support any CompositeFigure.
 * <br>1.0 24. November 2003  Created.
 */
public class UngroupAction extends AbstractSelectedAction {
  public static final String ID = "selectionUngroup";

  /**
   * Creates a new instance.
   */
  private final CompositeFigure prototype;

  /**
   * Creates a new instance.
   */
  public UngroupAction(DrawingEditor editor) {
    this(editor, new GroupFigure());
  }

  public UngroupAction(DrawingEditor editor, CompositeFigure prototype) {
    super(editor);
    this.prototype = prototype;
    labels.configureAction(this, ID);
  }

  @Override
  protected void updateEnabledState() {
    if (getView() != null) {
      setEnabled(canUngroup());
    } else {
      setEnabled(false);
    }
  }

  protected boolean canUngroup() {
    return getView().getSelectionCount() == 1 && getView().getSelectedFigures().iterator().next().getClass().equals(prototype.getClass());
  }

  public void actionPerformed(ActionEvent e) {
    if (canUngroup()) {
      final DrawingView view = getView();
      final CompositeFigure group = (CompositeFigure) getView().getSelectedFigures().iterator().next();
      final LinkedList<Figure> ungroupedFigures = new LinkedList<>();
      CompositeEdit edit = new CompositeEdit(labels.getString(ID)) {
        @Override
        public void redo() throws CannotRedoException {
          super.redo();
          ungroupFigures(view, group);
        }

        @Override
        public void undo() throws CannotUndoException {
          groupFigures(view, group, ungroupedFigures);
          super.undo();
        }
      };
      fireUndoableEditHappened(edit);
      ungroupedFigures.addAll(ungroupFigures(view, group));
      fireUndoableEditHappened(edit);
    }
  }

  public Collection<Figure> ungroupFigures(DrawingView view, CompositeFigure group) {
    LinkedList<Figure> figures = new LinkedList<>(group.getChildren());
    view.clearSelection();
    group.basicRemoveAllChildren();
    view.getDrawing().basicAddAll(figures);
    view.getDrawing().remove(group);
    view.addToSelection(figures);
    return figures;
  }

  public void groupFigures(DrawingView view, CompositeFigure group, Collection<Figure> figures) {
// XXX - This code is redundant with GroupAction
    Collection<Figure> sorted = view.getDrawing().sort(figures);
    view.getDrawing().basicRemoveAll(figures);
    view.clearSelection();
    view.getDrawing().add(group);
    group.willChange();
    for (Figure f : sorted) {
      group.basicAdd(f);
    }
    group.changed();
    view.addToSelection(group);
  }
}
