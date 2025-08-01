/*
 * @(#)PertApplicationModel.java  1.0  2006-06-18
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

package org.jhotdraw.samples.pert;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.Project;
import org.jhotdraw.app.action.Actions;
import org.jhotdraw.app.action.ExportAction;
import org.jhotdraw.app.action.ProjectPropertyAction;
import org.jhotdraw.app.action.ToggleProjectPropertyAction;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.ToolBarButtonFactory;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.figures.TextAreaFigure;
import org.jhotdraw.draw.tools.TextAreaTool;
import org.jhotdraw.draw.tools.Tool;
import org.jhotdraw.samples.pert.figures.DependencyFigure;
import org.jhotdraw.samples.pert.figures.TaskFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * PertApplicationModel.
 *
 * @author Werner Randelshofer.
 * @version 1.0 2006-06-18 Created.
 */
public class PertApplicationModel extends DefaultApplicationModel {
  private final static double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};

  private static class ToolButtonListener implements ItemListener {
    private Tool tool;
    private org.jhotdraw.draw.editors.DrawingEditor editor;

    public ToolButtonListener(org.jhotdraw.draw.tools.Tool t, org.jhotdraw.draw.editors.DrawingEditor editor) {
      this.tool = t;
      this.editor = editor;
    }

    public void itemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == ItemEvent.SELECTED) {
        editor.setTool(tool);
      }
    }
  }

  /**
   * This editor is shared by all projects.
   */
  private org.jhotdraw.draw.editors.DefaultDrawingEditor sharedEditor;

  private HashMap<String, Action> actions;

  /**
   * Creates a new instance.
   */
  public PertApplicationModel() {
  }

  public void initApplication(Application a) {
    ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.pert.Labels");
    AbstractAction aa;

    putAction(ExportAction.ID, new ExportAction(a));
    putAction("toggleGrid", aa = new ToggleProjectPropertyAction(a, "gridVisible"));
    drawLabels.configureAction(aa, "alignGrid");
    for (double sf : scaleFactors) {
      putAction((int) (sf * 100) + "%", aa = new ProjectPropertyAction(a, "scaleFactor", Double.TYPE, sf));
      aa.putValue(Action.NAME, (int) (sf * 100) + " %");

    }
  }

  public org.jhotdraw.draw.editors.DefaultDrawingEditor getSharedEditor() {
    if (sharedEditor == null) {
      sharedEditor = new org.jhotdraw.draw.editors.DefaultDrawingEditor();
    }
    return sharedEditor;
  }

  public void initProject(Application a, Project p) {
    if (a.isSharingToolsAmongProjects()) {
      ((PertProject) p).setDrawingEditor(getSharedEditor());
    }
  }

  private void addCreationButtonsTo(JToolBar tb, final DrawingEditor editor) {
    // AttributeKeys for the entity sets
    HashMap<AttributeKey, Object> attributes;

    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.pert.Labels");
    ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");

    ToolBarButtonFactory.addSelectionToolTo(tb, editor);
    tb.addSeparator();

    attributes = new HashMap<>();
    attributes.put(AttributeKeys.FILL_COLOR, Color.white);
    attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
    attributes.put(AttributeKeys.TEXT_COLOR, Color.black);
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.CreationTool(new TaskFigure(), attributes), "createTask", labels);

    attributes = new HashMap<>();
    attributes.put(AttributeKeys.STROKE_COLOR, new Color(0x000099));
    ToolBarButtonFactory.addToolTo(tb, editor, new org.jhotdraw.draw.tools.ConnectionTool(new DependencyFigure(), attributes), "createDependency", labels);
    tb.addSeparator();
    ToolBarButtonFactory.addToolTo(tb, editor, new TextAreaTool(new TextAreaFigure()), "createTextArea", drawLabels);

  }

  /**
   * Creates toolbars for the application.
   * This class always returns an empty list. Subclasses may return other
   * values.
   */
  public java.util.List<JToolBar> createToolBars(Application a, Project pr) {
    ResourceBundleUtil drawLabels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.pert.Labels");
    PertProject p = (PertProject) pr;

    org.jhotdraw.draw.editors.DrawingEditor editor;
    if (p == null) {
      editor = getSharedEditor();
    } else {
      editor = p.getDrawingEditor();
    }

    LinkedList<JToolBar> list = new LinkedList<>();
    JToolBar tb;
    tb = new JToolBar();
    addCreationButtonsTo(tb, editor);
    tb.setName(drawLabels.getString("drawToolBarTitle"));
    list.add(tb);
    tb = new JToolBar();
    ToolBarButtonFactory.addAttributesButtonsTo(tb, editor);
    tb.setName(drawLabels.getString("attributesToolBarTitle"));
    list.add(tb);
    tb = new JToolBar();
    ToolBarButtonFactory.addAlignmentButtonsTo(tb, editor);
    tb.setName(drawLabels.getString("alignmentToolBarTitle"));
    list.add(tb);
    return list;
  }

  public java.util.List<JMenu> createMenus(Application a, Project pr) {
    // FIXME - Add code for unconfiguring the menus!! We leak memory!
    PertProject p = (PertProject) pr;
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");

    //  JMenuBar mb = new JMenuBar();
    LinkedList<JMenu> mb = new LinkedList<>();
    JMenu m, m2;
    JMenuItem mi;
    JRadioButtonMenuItem rbmi;
    JCheckBoxMenuItem cbmi;
    ButtonGroup group;

    m = new JMenu();
    labels.configureMenu(m, "view");
    cbmi = new JCheckBoxMenuItem(getAction("toggleGrid"));
    Actions.configureJCheckBoxMenuItem(cbmi, getAction("toggleGrid"));
    m.add(cbmi);
    m2 = new JMenu("Zoom");
    for (double sf : scaleFactors) {
      String id = (int) (sf * 100) + "%";
      cbmi = new JCheckBoxMenuItem(getAction(id));
      Actions.configureJCheckBoxMenuItem(cbmi, getAction(id));
      m2.add(cbmi);
    }
    m.add(m2);
    mb.add(m);

    return mb;
  }
}
