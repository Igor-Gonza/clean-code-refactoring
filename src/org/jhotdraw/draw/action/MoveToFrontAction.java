/*
 * @(#)ToFrontAction.java  1.0  24. November 2003
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

import org.jhotdraw.draw.drawings.Drawing;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.views.DrawingView;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * ToFrontAction.
 *
 * @author Werner Randelshofer
 * @version 1.0 24. November 2003  Created.
 */
public class MoveToFrontAction extends AbstractSelectedAction {
  private final ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());

  /**
   * Creates a new instance.
   */
  public MoveToFrontAction(DrawingEditor editor) {
    super(editor);
    labels.configureAction(this, "moveToFront");
  }

  public void actionPerformed(ActionEvent e) {
    final DrawingView view = getView();
    final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());
    bringToFront(view, figures);
    fireUndoableEditHappened(new AbstractUndoableEdit() {
      @Override
      public String getPresentationName() {
        return labels.getString("moveToFront");
      }

      @Override
      public void redo() throws CannotRedoException {
        super.redo();
        MoveToFrontAction.bringToFront(view, figures);
      }

      @Override
      public void undo() throws CannotUndoException {
        super.undo();
        MoveToBackAction.sendToBack(view, figures);
      }
    });
  }

  public static void bringToFront(DrawingView view, Collection<Figure> figures) {
    Drawing drawing = view.getDrawing();
    for (Figure figure : drawing.sort(figures)) {
      drawing.bringToFront(figure);
    }
  }
}
