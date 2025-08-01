/*
 * @(#)DrawingPanel.java  1.0  11. March 2004
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

import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.draw.drawings.Drawing;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.figures.AttributedFigure;
import org.jhotdraw.draw.figures.BezierFigure;
import org.jhotdraw.draw.figures.ConnectionFigure;
import org.jhotdraw.draw.figures.EllipseFigure;
import org.jhotdraw.draw.figures.RectangleFigure;
import org.jhotdraw.draw.figures.RoundRectangleFigure;
import org.jhotdraw.draw.linedecorations.ArrowTip;
import org.jhotdraw.draw.liners.ElbowLiner;
import org.jhotdraw.draw.tools.BezierTool;
import org.jhotdraw.draw.views.DrawingView;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import java.util.Collection;

import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;

/**
 * DrawingPanel.
 *
 * @author Werner Randelshofer
 * @version 1.0 11. März 2004  Created.
 */
public class DrawingPanel extends JPanel {
  private final UndoRedoManager undoManager;
  private org.jhotdraw.draw.drawings.Drawing drawing;
  private final org.jhotdraw.draw.editors.DrawingEditor editor;

  /**
   * Creates new instance.
   */
  public DrawingPanel() {
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
    initComponents();
    undoManager = new UndoRedoManager();
    editor = new org.jhotdraw.draw.editors.DefaultDrawingEditor();
    editor.add(view);

    addCreationButtonsTo(creationToolbar, editor);
    ToolBarButtonFactory.addAttributesButtonsTo(attributesToolbar, editor);

    JPopupButton pb = new JPopupButton();
    pb.setItemFont(UIManager.getFont("MenuItem.font"));
    labels.configureToolBarButton(pb, "actions");
    pb.add(new DuplicateAction());
    pb.addSeparator();
    pb.add(new GroupAction(editor));
    pb.add(new UngroupAction(editor));
    pb.addSeparator();
    pb.add(new MoveToFrontAction(editor));
    pb.add(new MoveToBackAction(editor));
    pb.addSeparator();
    pb.add(new CutAction());
    pb.add(new CopyAction());
    pb.add(new PasteAction());
    pb.add(new SelectAllAction());
    pb.add(new SelectSameAction(editor));
    pb.addSeparator();
    pb.add(undoManager.getUndoAction());
    pb.add(undoManager.getRedoAction());
    pb.addSeparator();
    pb.add(new ToggleGridAction(editor));

    JMenu m = new JMenu(labels.getString("zoom"));
    JRadioButtonMenuItem rbmi;
    ButtonGroup group = new ButtonGroup();
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.1, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.25, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.5, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 0.75, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 1.0, null)));
    rbmi.setSelected(true);
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 1.25, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 1.5, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 2, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 3, null)));
    group.add(rbmi);
    m.add(rbmi = new JRadioButtonMenuItem(new ZoomAction(editor, 4, null)));
    group.add(rbmi);
    pb.add(m);
    pb.setFocusable(false);
    creationToolbar.addSeparator();
    creationToolbar.add(pb);

    org.jhotdraw.draw.drawings.DefaultDrawing drawing = new org.jhotdraw.draw.drawings.DefaultDrawing();
    view.setDrawing(drawing);
    drawing.addUndoableEditListener(undoManager);
  }

  public void setDrawing(Drawing d) {
    undoManager.discardAllEdits();
    view.getDrawing().removeUndoableEditListener(undoManager);
    view.setDrawing(d);
    d.addUndoableEditListener(undoManager);
  }

  public org.jhotdraw.draw.drawings.Drawing getDrawing() {
    return view.getDrawing();
  }

  public DrawingView getView() {
    return view;
  }

  public org.jhotdraw.draw.editors.DrawingEditor getEditor() {
    return editor;
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    toolButtonGroup = new javax.swing.ButtonGroup();
    scrollPane = new javax.swing.JScrollPane();
    view = new org.jhotdraw.draw.views.DefaultDrawingView();
    jPanel1 = new javax.swing.JPanel();
    creationToolbar = new javax.swing.JToolBar();
    attributesToolbar = new javax.swing.JToolBar();

    setLayout(new java.awt.BorderLayout());

    scrollPane.setViewportView(view);

    add(scrollPane, java.awt.BorderLayout.CENTER);

    jPanel1.setLayout(new java.awt.GridBagLayout());

    creationToolbar.setFloatable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel1.add(creationToolbar, gridBagConstraints);

    attributesToolbar.setFloatable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel1.add(attributesToolbar, gridBagConstraints);

    add(jPanel1, java.awt.BorderLayout.SOUTH);

  }// </editor-fold>//GEN-END:initComponents

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
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.ConnectionTool(new org.jhotdraw.draw.figures.LineConnectionFigure()), "createLineConnection", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, cnt = new org.jhotdraw.draw.tools.ConnectionTool(new org.jhotdraw.draw.figures.LineConnectionFigure()), "createElbowConnection", labels);
    lc = cnt.getPrototype();
    lc.setLiner(new ElbowLiner());
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.BezierTool(new org.jhotdraw.draw.figures.BezierFigure()), "createScribble", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure(true)), "createPolygon", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.TextTool(new org.jhotdraw.draw.figures.TextFigure()), "createText", labels);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.TextAreaTool(new org.jhotdraw.draw.figures.TextAreaFigure()), "createTextArea", labels);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JToolBar attributesToolbar;
  private javax.swing.JToolBar creationToolbar;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane scrollPane;
  private javax.swing.ButtonGroup toolButtonGroup;
  private org.jhotdraw.draw.views.DefaultDrawingView view;
  // End of variables declaration//GEN-END:variables

}
