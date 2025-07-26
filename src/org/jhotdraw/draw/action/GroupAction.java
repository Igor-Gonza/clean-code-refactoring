/*
 * @(#)GroupAction.java  1.1  2006-07-12
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
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.figures.CompositeFigure;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.figures.GroupFigure;
import org.jhotdraw.undo.CompositeEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.util.Collection;
import java.util.LinkedList;

/**
 * GroupAction.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-07-12 Changed to support any CompositeFigure.
 * <br>1.0.1 2006-07-09 Fixed enabled state.
 * <br>1.0 24. November 2003  Created.
 */
public class GroupAction extends AbstractSelectedAction {
  public final static String ID = "selectionGroup";
  private final org.jhotdraw.draw.figures.CompositeFigure prototype;

  /**
   * Creates a new instance.
   */
  public GroupAction(org.jhotdraw.draw.editors.DrawingEditor editor) {
    this(editor, new GroupFigure());
  }

  public GroupAction(DrawingEditor editor, org.jhotdraw.draw.figures.CompositeFigure prototype) {
    super(editor);
    this.prototype = prototype;
    labels.configureAction(this, ID);
  }

  @Override
  protected void updateEnabledState() {
    if (getView() != null) {
      setEnabled(canGroup());
    } else {
      setEnabled(false);
    }
  }

  protected boolean canGroup() {
    return getView().getSelectionCount() > 1;
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    if (canGroup()) {
      final DrawingView view = getView();
      final LinkedList<org.jhotdraw.draw.figures.Figure> ungroupedFigures = new LinkedList<>(view.getSelectedFigures());
      final org.jhotdraw.draw.figures.CompositeFigure group = (CompositeFigure) prototype.clone();
      CompositeEdit edit = new CompositeEdit(labels.getString("selectionGroup")) {
        public void redo() throws CannotRedoException {
          super.redo();
          groupFigures(view, group, ungroupedFigures);
        }

        public void undo() throws CannotUndoException {
          ungroupFigures(view, group);
          super.undo();
        }

        public boolean addEdit(UndoableEdit anEdit) {
          return super.addEdit(anEdit);
        }
      };
      fireUndoableEditHappened(edit);
      groupFigures(view, group, ungroupedFigures);
      fireUndoableEditHappened(edit);
    }
  }

  public Collection<org.jhotdraw.draw.figures.Figure> ungroupFigures(DrawingView view, org.jhotdraw.draw.figures.CompositeFigure group) {
// XXX - This code is redundant with UngroupAction
    LinkedList<org.jhotdraw.draw.figures.Figure> figures = new LinkedList<>(group.getChildren());
    view.clearSelection();
    group.basicRemoveAllChildren();
    view.getDrawing().basicAddAll(figures);
    view.getDrawing().remove(group);
    view.addToSelection(figures);
    return figures;
  }

  public void groupFigures(DrawingView view, org.jhotdraw.draw.figures.CompositeFigure group, Collection<org.jhotdraw.draw.figures.Figure> figures) {
    Collection<Figure> sorted = view.getDrawing().sort(figures);
    view.getDrawing().basicRemoveAll(figures);
    view.clearSelection();
    view.getDrawing().add(group);
    group.willChange();
    for (org.jhotdraw.draw.figures.Figure f : sorted) {
      group.basicAdd(f);
    }
    group.changed();
    view.addToSelection(group);
  }
}
