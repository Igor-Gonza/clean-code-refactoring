/*
 * @(#)DrawApplicationModel.java  1.0  June 10, 2006
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

package org.jhotdraw.samples.draw;

import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.figures.AttributedFigure;
import org.jhotdraw.draw.figures.ConnectionFigure;
import org.jhotdraw.draw.figures.LineConnectionFigure;
import org.jhotdraw.draw.figures.RectangleFigure;
import org.jhotdraw.draw.figures.RoundRectangleFigure;
import org.jhotdraw.draw.liners.ElbowLiner;
import org.jhotdraw.draw.tools.ConnectionTool;
import org.jhotdraw.util.*;

import java.util.*;
import javax.swing.*;

import org.jhotdraw.app.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;

import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * DrawApplicationModel.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 10, 2006, Created.
 */
public class DrawApplicationModel extends DefaultApplicationModel {
  /**
   * This editor is shared by all projects.
   */
  private org.jhotdraw.draw.editors.DefaultDrawingEditor sharedEditor;

  /**
   * Creates a new instance.
   */
  public DrawApplicationModel() {
  }

  public org.jhotdraw.draw.editors.DefaultDrawingEditor getSharedEditor() {
    if (sharedEditor == null) {
      sharedEditor = new org.jhotdraw.draw.editors.DefaultDrawingEditor();
    }
    return sharedEditor;
  }

  @Override
  public void initProject(Application a, Project p) {
    if (a.isSharingToolsAmongProjects()) {
      ((DrawProject) p).setEditor(getSharedEditor());
    }
  }

  /**
   * Creates toolbars for the application.
   * This class always returns an empty list. Subclasses may return other
   * values.
   */
  @Override
  public List<JToolBar> createToolBars(Application a, Project pr) {
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
    DrawProject p = (DrawProject) pr;

    org.jhotdraw.draw.editors.DrawingEditor editor;
    if (p == null) {
      editor = getSharedEditor();
    } else {
      editor = p.getEditor();
    }

    LinkedList<JToolBar> list = new LinkedList<>();
    JToolBar tb;
    tb = new JToolBar();
    addCreationButtonsTo(tb, editor);
    tb.setName(labels.getString("drawToolBarTitle"));
    list.add(tb);
    tb = new JToolBar();
    ToolBarButtonFactory.addAttributesButtonsTo(tb, editor);
    tb.setName(labels.getString("attributesToolBarTitle"));
    list.add(tb);
    tb = new JToolBar();
    ToolBarButtonFactory.addAlignmentButtonsTo(tb, editor);
    tb.setName(labels.getString("alignmentToolBarTitle"));
    list.add(tb);
    return list;
  }

  private void addCreationButtonsTo(JToolBar tb, DrawingEditor editor) {
    addDefaultCreationButtonsTo(tb, editor, ToolBarButtonFactory.createDrawingActions(editor), ToolBarButtonFactory.createSelectionActions(editor));
  }

  public void addDefaultCreationButtonsTo(JToolBar tb, final org.jhotdraw.draw.editors.DrawingEditor editor, Collection<Action> drawingActions, Collection<Action> selectionActions) {
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");

    ToolBarButtonFactory.addSelectionToolTo(tb, editor, drawingActions, selectionActions);
    tb.addSeparator();

    org.jhotdraw.draw.figures.AttributedFigure af;
    org.jhotdraw.draw.tools.CreationTool ct;
    org.jhotdraw.draw.tools.ConnectionTool cnt;
    ConnectionFigure lc;

    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.CreationTool(new RectangleFigure()), "createRectangle", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.CreationTool(new RoundRectangleFigure()), "createRoundRectangle", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.CreationTool(new EllipseFigure()), "createEllipse", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.CreationTool(new org.jhotdraw.draw.figures.DiamondFigure()), "createDiamond", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.CreationTool(new org.jhotdraw.draw.figures.TriangleFigure()), "createTriangle", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.CreationTool(new org.jhotdraw.draw.figures.LineFigure()), "createLine", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, ct = new org.jhotdraw.draw.tools.CreationTool(new org.jhotdraw.draw.figures.LineFigure()), "createArrow", labels);
    af = (AttributedFigure) ct.getPrototype();
    af.setAttribute(END_DECORATION, new ArrowTip(0.35, 12, 11.3));
    ToolBarButtonFactory.addToolTo(tb, editor, new ConnectionTool(new LineConnectionFigure()), "createLineConnection", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, cnt = new org.jhotdraw.draw.tools.ConnectionTool(new org.jhotdraw.draw.figures.LineConnectionFigure()), "createElbowConnection", labels);
    lc = cnt.getPrototype();
    lc.setLiner(new ElbowLiner());
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.BezierTool(new org.jhotdraw.draw.figures.BezierFigure()), "createScribble", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.BezierTool(new org.jhotdraw.draw.figures.BezierFigure(true)), "createPolygon", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.TextTool(new org.jhotdraw.draw.figures.TextFigure()), "createText", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.TextAreaTool(new org.jhotdraw.draw.figures.TextAreaFigure()), "createTextArea", labels);
  }
}
