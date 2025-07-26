/*
 * @(#)DrawingView.java  3.1  2006-03-15
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

package org.jhotdraw.draw.views;

import org.jhotdraw.draw.FigureSelectionListener;
import org.jhotdraw.draw.constrainers.Constrainer;
import org.jhotdraw.draw.drawings.Drawing;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.draw.tools.Tool;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Set;

/**
 * DrawingView renders a Drawing and listens to its changes.
 * It receives user input and forwards it to registered listeners.
 *
 * @author Werner Randelshofer
 * @version 3.1 2006-03-15 Support for enabled state added.
 * <br>3.0 2006-02-20 Changed to share a single DrawingEditor by multiple
 * views.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface DrawingView {
  /**
   * Gets the tools.
   */
  Set<Tool> getTools();

  /**
   * Gets the drawing.
   */
  Drawing getDrawing();

  /**
   * Sets and installs another drawing in the view.
   */
  void setDrawing(org.jhotdraw.draw.drawings.Drawing d);

  /**
   * Sets the cursor of the DrawingView
   */
  void setCursor(Cursor c);

  /**
   * Test whether a given figure is selected.
   */
  boolean isFigureSelected(Figure checkFigure);

  /**
   * Adds a figure to the current selection.
   */
  void addToSelection(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Adds a collection of figures to the current selection.
   */
  void addToSelection(Collection<org.jhotdraw.draw.figures.Figure> figures);

  /**
   * Removes a figure from the selection.
   */
  void removeFromSelection(org.jhotdraw.draw.figures.Figure figure);

  /**
   * If a figure isn't selected it is added to the selection.
   * Otherwise ,it is removed from the selection.
   */
  void toggleSelection(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Clears the current selection.
   */
  void clearSelection();

  /**
   * Selects all figures.
   */
  void selectAll();

  /**
   * Gets the current selection as a FigureSelection. A FigureSelection
   * can be cut, copied, pasted.
   */
  Collection<org.jhotdraw.draw.figures.Figure> getSelectedFigures();

  /**
   * Gets the number of selected figures.
   */
  int getSelectionCount();

  /**
   * Finds a handle at the given coordinates.
   *
   * @return A handle, null if no handle is found.
   */
  org.jhotdraw.draw.handlers.Handle findHandle(Point p);

  /**
   * Gets compatible handles.
   *
   * @return A collection containing the handle and all compatible handles.
   */
  Collection<Handle> getCompatibleHandles(org.jhotdraw.draw.handlers.Handle handle);

  /**
   * Finds a figure at the given point.
   *
   * @return A figure, null if no figure is found.
   */
  org.jhotdraw.draw.figures.Figure findFigure(Point p);

  /**
   * Returns all figures that lie within or intersect the specified
   * bounds. The figures are returned in Z-order from back to front.
   */
  Collection<org.jhotdraw.draw.figures.Figure> findFigures(Rectangle r);

  /**
   * Returns all figures that lie within the specified
   * bounds. The figures are returned in Z-order from back to front.
   */
  Collection<org.jhotdraw.draw.figures.Figure> findFiguresWithin(Rectangle r);

  /**
   * Informs the view that it has been added to the specified editor.
   * The view must draw the tool of the editor, if getActiveView() of the
   * editor returns the view.
   */
  void addNotify(org.jhotdraw.draw.editors.DrawingEditor editor);

  /**
   * Informs the view that it has been removed from the specified editor.
   * The view must not draw the tool from the editor anymore.
   */
  void removeNotify(DrawingEditor editor);

  void addMouseListener(MouseListener l);

  void removeMouseListener(MouseListener l);

  void addKeyListener(KeyListener l);

  void removeKeyListener(KeyListener l);

  void addMouseMotionListener(MouseMotionListener l);

  void removeMouseMotionListener(MouseMotionListener l);

  /**
   * Add a listener for selection changes in this DrawingView.
   *
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  void addFigureSelectionListener(FigureSelectionListener fsl);

  /**
   * Remove a listener for selection changes in this DrawingView.
   *
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  void removeFigureSelectionListener(FigureSelectionListener fsl);

  void requestFocus();

  /**
   * Converts drawing coordinates to view coordinates.
   */
  Point drawingToView(Point2D.Double p);

  /**
   * Converts view coordinates to drawing coordinates.
   */
  Point2D.Double viewToDrawing(Point p);

  /**
   * Converts drawing coordinates to view coordinates.
   */
  Rectangle drawingToView(Rectangle2D.Double p);

  /**
   * Converts view coordinates to drawing coordinates.
   */
  Rectangle2D.Double viewToDrawing(Rectangle p);

  /**
   * Sets the editor's constrainer.
   */
  void setConstrainer(org.jhotdraw.draw.constrainers.Constrainer constrainer);

  /**
   * Gets the editor's constrainer.
   */
  Constrainer getConstrainer();

  /**
   * Returns the container of the drawing view.
   */
  Container getContainer();

  /**
   * Gets a transform which can be used to convert
   * drawing coordinates to view coordinates.
   */
  AffineTransform getDrawingToViewTransform();

  /**
   * Gets the scale factor of the drawing view.
   */
  double getScaleFactor();

  /**
   * Sets the scale factor of the drawing view.
   * This is a bound property.
   */
  void setScaleFactor(double newValue);

  /**
   * The detail level of the handles.
   */
  void setHandleDetailLevel(int newValue);

  /**
   * Returns the detail level of the handles.
   */
  int getHandleDetailLevel();

  /**
   * Sets the enabled state of the drawing view.
   * This is a bound property.
   */
  void setEnabled(boolean newValue);

  /**
   * Gets the enabled state of the drawing view.
   */
  boolean isEnabled();

  void addPropertyChangeListener(PropertyChangeListener listener);

  void removePropertyChangeListener(PropertyChangeListener listener);
}
