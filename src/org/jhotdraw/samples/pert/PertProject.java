/*
 * @(#)DrawProject.java  1.1  2006-06-10
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
 *
 */
package org.jhotdraw.samples.pert;

import org.jhotdraw.app.AbstractProject;
import org.jhotdraw.app.action.RedoAction;
import org.jhotdraw.app.action.UndoAction;
import org.jhotdraw.draw.action.ToolBarButtonFactory;
import org.jhotdraw.draw.constrainers.GridConstrainer;
import org.jhotdraw.draw.drawings.DefaultDrawing;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.views.DefaultDrawingView;
import org.jhotdraw.gui.PlacardScrollPaneLayout;
import org.jhotdraw.io.ExtensionFileFilter;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.xml.NanoXMLLiteDOMInput;
import org.jhotdraw.xml.NanoXMLLiteDOMOutput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.prefs.Preferences;

/**
 * A Pert drawing project.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-06-10 Extended to support DefaultDrawApplicationModel.
 * <br>1.0 2006-02-07 Created.
 */
public class PertProject extends AbstractProject {

  /**
   * Each DrawProject uses its own undo redo manager.
   * This allows for undoing and redoing actions per project.
   */
  private UndoRedoManager undo;

  /**
   * Depending on the type of an application, there may be one editor per
   * project, or a single shared editor for all projects.
   */
  private org.jhotdraw.draw.editors.DrawingEditor editor;

  private org.jhotdraw.draw.constrainers.GridConstrainer visibleConstrainer = new org.jhotdraw.draw.constrainers.GridConstrainer(10, 10);
  private GridConstrainer invisibleConstrainer = new org.jhotdraw.draw.constrainers.GridConstrainer(1, 1);
  private Preferences prefs;

  /**
   * Creates a new Project.
   */
  public PertProject() {
  }

  /**
   * Initializes the project.
   */
  public void init() {
    super.init();

    initComponents();

    JPanel zoomButtonPanel = new JPanel(new BorderLayout());
    scrollPane.setLayout(new PlacardScrollPaneLayout());
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

    setEditor(new org.jhotdraw.draw.editors.DefaultDrawingEditor());
    view.setDOMFactory(new PertFactory());
    undo = new UndoRedoManager();
    view.setDrawing(new org.jhotdraw.draw.drawings.DefaultDrawing());
    view.getDrawing().addUndoableEditListener(undo);
    initActions();
    undo.addPropertyChangeListener(evt -> setHasUnsavedChanges(undo.hasSignificantEdits()));

    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");

    JPanel placardPanel = new JPanel(new BorderLayout());
    javax.swing.AbstractButton pButton;
    pButton = ToolBarButtonFactory.createZoomButton(view);
    pButton.putClientProperty("Quaqua.Button.style", "placard");
    pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
    pButton.setFont(UIManager.getFont("SmallSystemFont"));
    placardPanel.add(pButton, BorderLayout.WEST);
    pButton = ToolBarButtonFactory.createToggleGridButton(view);
    pButton.putClientProperty("Quaqua.Button.style", "placard");
    pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
    pButton.setFont(UIManager.getFont("SmallSystemFont"));
    labels.configureToolBarButton(pButton, "alignGridSmall");
    placardPanel.add(pButton, BorderLayout.EAST);
    scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);

    prefs = Preferences.userNodeForPackage(getClass());
    setGridVisible(prefs.getBoolean("project.gridVisible", false));
    setScaleFactor(prefs.getDouble("project.scaleFactor", 1d));
  }

  public org.jhotdraw.draw.editors.DrawingEditor getEditor() {
    return editor;
  }

  public void setEditor(DrawingEditor newValue) {
    org.jhotdraw.draw.editors.DrawingEditor oldValue = editor;
    if (oldValue != null) {
      oldValue.remove(view);
    }
    editor = newValue;
    if (newValue != null) {
      newValue.add(view);
    }
  }

  public void setGridVisible(boolean newValue) {
    boolean oldValue = isGridVisible();
    org.jhotdraw.draw.constrainers.Constrainer c = (newValue) ? visibleConstrainer : invisibleConstrainer;
    view.setConstrainer(c);

    firePropertyChange("gridVisible", oldValue, newValue);
    prefs.putBoolean("project.gridVisible", newValue);
  }

  public boolean isGridVisible() {
    return view.getConstrainer() == visibleConstrainer;
  }

  public double getScaleFactor() {
    return view.getScaleFactor();
  }

  public void setScaleFactor(double newValue) {
    double oldValue = getScaleFactor();
    view.setScaleFactor(newValue);

    firePropertyChange("scaleFactor", oldValue, newValue);
    prefs.putDouble("project.scaleFactor", newValue);
  }

  /**
   * Initializes project specific actions.
   */
  private void initActions() {
    putAction(UndoAction.ID, undo.getUndoAction());
    putAction(RedoAction.ID, undo.getRedoAction());
  }

  protected void setHasUnsavedChanges(boolean newValue) {
    super.setHasUnsavedChanges(newValue);
    undo.setHasSignificantEdits(newValue);
  }

  /**
   * Writes the project to the specified file.
   */
  public void write(File f) throws IOException {
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(Files.newOutputStream(f.toPath()));
      NanoXMLLiteDOMOutput domo = new NanoXMLLiteDOMOutput(view.getDOMFactory());
      domo.openElement("PertDiagram");
      domo.writeObject(view.getDrawing());
      domo.closeElement();
      domo.save(out);
    } finally {
      if (out != null) try {
        out.close();
      } catch (IOException ignored) {
      }
      //if (out != null) out.close();
    }
  }

  /**
   * Reads the project from the specified file.
   */
  public void read(File f) throws IOException {
    InputStream in = null;
    try {
      in = new BufferedInputStream(Files.newInputStream(f.toPath()));
      NanoXMLLiteDOMInput domInput = new NanoXMLLiteDOMInput(view.getDOMFactory(), in);
      domInput.openElement("PertDiagram");
      final org.jhotdraw.draw.drawings.Drawing drawing = (org.jhotdraw.draw.drawings.Drawing) domInput.readObject();
      domInput.closeElement();
      SwingUtilities.invokeAndWait(() -> {
        view.getDrawing().removeUndoableEditListener(undo);
        view.setDrawing(drawing);
        view.getDrawing().addUndoableEditListener(undo);
        undo.discardAllEdits();
      });
    } catch (InterruptedException | InvocationTargetException e) {
      InternalError error = new InternalError();
      e.initCause(e);
      throw error;
    } finally {
      if (in != null) try {
        in.close();
      } catch (IOException ignored) {
      }
    }
  }

  /**
   * Sets a drawing editor for the project.
   */
  public void setDrawingEditor(org.jhotdraw.draw.editors.DrawingEditor newValue) {
    if (editor != null) {
      editor.remove(view);
    }
    editor = newValue;
    if (editor != null) {
      editor.add(view);
    }
  }

  /**
   * Gets the drawing editor of the project.
   */
  public org.jhotdraw.draw.editors.DrawingEditor getDrawingEditor() {
    return editor;
  }

  /**
   * Clears the project.
   */
  public void clear() {
    view.setDrawing(new DefaultDrawing());
    undo.discardAllEdits();
  }

  @Override
  protected JFileChooser createOpenChooser() {
    JFileChooser c = super.createOpenChooser();
    c.addChoosableFileFilter(new ExtensionFileFilter("Pert Diagram", "xml"));
    return c;
  }

  @Override
  protected JFileChooser createSaveChooser() {
    JFileChooser c = super.createSaveChooser();
    c.addChoosableFileFilter(new ExtensionFileFilter("Pert Diagram", "xml"));
    return c;
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    scrollPane = new javax.swing.JScrollPane();
    view = new DefaultDrawingView();

    setLayout(new java.awt.BorderLayout());

    scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setViewportView(view);

    add(scrollPane, java.awt.BorderLayout.CENTER);

  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane scrollPane;
  private DefaultDrawingView view;
  // End of variables declaration//GEN-END:variables

}
