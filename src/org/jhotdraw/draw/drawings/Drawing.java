/*
 * @(#)Drawing.java  2.0  2006-01-14
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

package org.jhotdraw.draw.drawings;

import org.jhotdraw.draw.DrawingListener;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.xml.DOMStorable;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Drawing is a container for figures.
 * <p>
 * Drawing sends out DrawingChanged events to DrawingChangeListeners
 * whenever a part of its area was invalidated.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Drawing extends Serializable, DOMStorable {
  /**
   * Removes all figures from the drawing.
   */
   void clear();

  /**
   * Adds a figure to the drawing.
   * The drawing sends an <code>addNotify</code> message to the figure
   * after it has been added.
   *
   * @param figure to be added to the drawing
   */
   void add(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Adds a collection of figures to the drawing.
   * The drawing sends an <code>addNotify</code>  message to each figure
   * after it has been added.
   *
   * @param figures to be added to the drawing
   */
   void addAll(Collection<org.jhotdraw.draw.figures.Figure> figures);

  /**
   * Removes a figure from the drawing.
   * The drawing sends a <code>removeNotify</code>  message to the figure
   * before it is removed.
   *
   * @param figure that is part of the drawing and should be removed
   */
   void remove(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Removes the specified figures from the drawing.
   * The drawing sends a <code>removeNotify</code>  message to each figure
   * before it is removed.
   *
   * @param figures A collection of figures which are part of the drawing
   *                and should be removed
   */
   void removeAll(Collection<org.jhotdraw.draw.figures.Figure> figures);

  /**
   * Removes a figure temporarily from the drawing.
   * The drawing sends no </code>removeNotify</code> message to the figure.
   *
   * @param figure that is part of the drawing and should be removed
   * @see #basicAdd(org.jhotdraw.draw.figures.Figure)
   */
   void basicRemove(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Removes the specified figures temporarily from the drawing.
   * The drawing sends no </code>removeNotify</code> message to the figures.
   *
   * @param figures A collection of figures which are part of the drawing
   *                and should be removed
   * @see #basicAddAll(Collection)
   */
   void basicRemoveAll(Collection<org.jhotdraw.draw.figures.Figure> figures);

  /**
   * Reinserts a figure which was temporarily removed using basicRemove.
   * The drawing sends no <code>addNotify</code> message to the figure.
   *
   * @param figure that is part of the drawing and should be removed
   * @see #basicRemove(org.jhotdraw.draw.figures.Figure)
   */
   void basicAdd(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Reinserts a figure which was temporarily removed using basicRemove.
   * The drawing sends no <code>addNotify</code> message to the figure.
   *
   * @param figure that is part of the drawing and should be removed
   * @see #basicRemove(org.jhotdraw.draw.figures.Figure)
   */
   void basicAdd(int index, org.jhotdraw.draw.figures.Figure figure);

  /**
   * Reinserts the specified figures which were temporarily basicRemoved from
   * the drawing.
   * The drawing sends no <code>addNotify</code> message to the figures.
   *
   * @param figures A collection of figures which are part of the drawing
   *                and should be reinserted.
   * @see #basicRemoveAll(Collection)
   */
   void basicAddAll(Collection<Figure> figures);

  /**
   * Draws all the figures from back to front.
   */
  void draw(Graphics2D g);

  /*
   * Draws only the figures in the supplied set.
   * /
   * void draw(Graphics2D g, ArrayList figures);
   */

  /**
   * Returns all figures that lie within or intersect the specified
   * bounds. The figures are returned in Z-order from back to front.
   */
   Collection<org.jhotdraw.draw.figures.Figure> findFigures(Rectangle2D.Double bounds);

  /**
   * Returns all figures that lie within the specified
   * bounds. The figures are returned in Z-order from back to front.
   */
   Collection<org.jhotdraw.draw.figures.Figure> findFiguresWithin(Rectangle2D.Double bounds);

  /**
   * Returns the figures of the drawing.
   *
   * @return A Collection of Figure's.
   */
   Collection<org.jhotdraw.draw.figures.Figure> getFigures();

  /**
   * Returns the number of figures in this drawing.
   */
   int getFigureCount();

  /**
   * Finds a top level Figure. Use this call for hit detection that
   * should not descend into the figure's children.
   */
  org.jhotdraw.draw.figures.Figure findFigure(Point2D.Double p);

  /**
   * Finds a top level Figure. Use this call for hit detection that
   * should not descend into the figure's children.
   */
  org.jhotdraw.draw.figures.Figure findFigureExcept(Point2D.Double p, org.jhotdraw.draw.figures.Figure ignore);

  /**
   * Finds a top level Figure. Use this call for hit detection that
   * should not descend into the figure's children.
   */
  org.jhotdraw.draw.figures.Figure findFigureExcept(Point2D.Double p, Collection<org.jhotdraw.draw.figures.Figure> ignore);

  /**
   * Returns true if this drawing contains the specified figure.
   */
  boolean contains(org.jhotdraw.draw.figures.Figure f);

  /**
   * Returns a list of the figures in Z-Order from front to back.
   */
   List<org.jhotdraw.draw.figures.Figure> getFiguresFrontToBack();

  /**
   * Finds a figure but descends into a figure's
   * children. Use this method to implement <i>click-through</i>
   * hit detection, that is, you want to detect the innermost
   * figure containing the given point.
   */
   org.jhotdraw.draw.figures.Figure findFigureInside(Point2D.Double p);

  /**
   * Sends a figure to the back of the drawing.
   *
   * @param figure that is part of the drawing
   */
   void sendToBack(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Brings a figure to the front.
   *
   * @param figure that is part of the drawing
   */
   void bringToFront(org.jhotdraw.draw.figures.Figure figure);

  /**
   * Returns a copy of the provided collection which is sorted
   * in z order from back to front.
   */
   Collection<org.jhotdraw.draw.figures.Figure> sort(Collection<org.jhotdraw.draw.figures.Figure> figures);

  /**
   * Adds a listener for this drawing.
   */
  void addDrawingListener(DrawingListener listener);

  /**
   * Removes a listener from this drawing.
   */
  void removeDrawingListener(DrawingListener listener);

  /**
   * Adds a listener for undoable edit events.
   */
   void addUndoableEditListener(UndoableEditListener l);

  /**
   * Removes a listener for undoable edit events.
   */
   void removeUndoableEditListener(UndoableEditListener l);

  /**
   * Notify all listenerList that have registered interest for
   * notification on this event type.
   */
   void fireUndoableEditHappened(UndoableEdit edit);

  /**
   * Returns the font render context used to do text layout and text drawing.
   */
   FontRenderContext getFontRenderContext();

  /**
   * Sets the font render context used to do text layout and text drawing.
   */
   void setFontRenderContext(FontRenderContext frc);

  /**
   * Returns the lock object on which all threads acting in Figures in this
   * drawing synchronize to prevent race conditions.
   */
   Object getLock();
}

