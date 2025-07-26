/*
 * @(#)CombinePathsAction.java  1.0  2006-07-12
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

package org.jhotdraw.samples.svg.action;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.GroupAction;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.figures.CompositeFigure;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.samples.svg.figures.SVGPath;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * CombinePathsAction.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-07-12 Created.
 */
public class CombineAction extends GroupAction {
  public final static String ID = "selectionCombine";

  /**
   * Creates a new instance.
   */
  public CombineAction(DrawingEditor editor) {
    super(editor, new SVGPath());

    labels = ResourceBundleUtil.getLAFBundle(
            "org.jhotdraw.samples.svg.Labels",
            Locale.getDefault()
    );
    labels.configureAction(this, ID);
  }

  @Override
  protected boolean canGroup() {
    boolean canCombine = getView().getSelectionCount() > 1;
    if (canCombine) {
      for (org.jhotdraw.draw.figures.Figure f : getView().getSelectedFigures()) {
        if (!(f instanceof SVGPath)) {
          canCombine = false;
          break;
        }
      }
    }
    return canCombine;
  }

  public Collection<Figure> ungroupFigures(DrawingView view, org.jhotdraw.draw.figures.CompositeFigure group) {
    LinkedList<org.jhotdraw.draw.figures.Figure> figures = new LinkedList<>(group.getChildren());
    view.clearSelection();
    group.basicRemoveAllChildren();
    LinkedList<org.jhotdraw.draw.figures.Figure> paths = new LinkedList<>();
    for (org.jhotdraw.draw.figures.Figure f : figures) {
      SVGPath path = new SVGPath();
      path.removeAllChildren();
      for (Map.Entry<AttributeKey, Object> entry : group.getAttributes().entrySet()) {
        path.basicSetAttribute(entry.getKey(), entry.getValue());
      }
      path.add(f);
      view.getDrawing().basicAdd(path);
      paths.add(path);
    }
    view.getDrawing().remove(group);
    view.addToSelection(paths);
    return figures;
  }

  public void groupFigures(DrawingView view, CompositeFigure group, Collection<org.jhotdraw.draw.figures.Figure> figures) {
    Collection<org.jhotdraw.draw.figures.Figure> sorted = view.getDrawing().sort(figures);
    view.getDrawing().basicRemoveAll(figures);
    view.clearSelection();
    view.getDrawing().add(group);
    group.willChange();
    group.removeAllChildren();
    for (Map.Entry<AttributeKey, Object> entry : figures.iterator().next().getAttributes().entrySet()) {
      group.basicSetAttribute(entry.getKey(), entry.getValue());
    }
    for (org.jhotdraw.draw.figures.Figure f : sorted) {
      SVGPath path = (SVGPath) f;
      for (org.jhotdraw.draw.figures.Figure child : path.getChildren()) {
        group.basicAdd(child);
      }
    }
    group.changed();
    view.addToSelection(group);
  }
}
