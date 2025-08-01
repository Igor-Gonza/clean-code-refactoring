/*
 * @(#)Handle.java  2.0  2006-01-14
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

package org.jhotdraw.draw.handlers;

import org.jhotdraw.draw.views.DrawingView;
import org.jhotdraw.draw.listeners.HandleListener;
import org.jhotdraw.draw.figures.Figure;

import java.awt.*;
import java.awt.event.KeyListener;
import java.util.Collection;

/**
 * Handles are used to change a figure by direct manipulation.
 * Handles know their owning figure, and they provide methods to
 * locate the handle on the figure and to track changes.
 * <p>
 * Handles are used for user interaction. Unlike figures, a handle works with
 * the user interface coordinates of the DrawingView. The user interface
 * coordinates are expressed in integer pixels.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface Handle extends KeyListener {
  /**
   * Returns the owner of this handle.
   */
   Figure getOwner();

  /**
   * Sets the view of the handle.
   */
   void setView(DrawingView view);

  /**
   * Adds a listener for this handle.
   */
   void addHandleListener(HandleListener l);

  /**
   * Removes a listener for this handle.
   */
  void removeHandleListener(HandleListener l);

  /**
   * Returns the bounding box of the handle.
   */
  Rectangle getBounds();

  /**
   * Returns the draw bounds of the handle.
   */
  Rectangle getDrawBounds();

  /**
   * Tests if a point is contained in the handle.
   */
   boolean contains(Point p);

  /**
   * Draws this handle.
   */
   void draw(Graphics2D g);

  /**
   * Invalidates the handle. This method informs its listeners
   * that its current display box is invalid and should be
   * refreshed.
   */
   void invalidate();

  /**
   * Disposes the resources acquired by the handler.
   */
   void dispose();

  /**
   * Returns a cursor for the handle.
   */
   Cursor getCursor();

  /**
   * Returns true, if this handle is combinable with the specified handle.
   * This method is used to determine, if multiple handles need to be tracked,
   * when more than one figure is selected.
   */
   boolean isCombinableWith(Handle handle);

  /**
   * Tracks the start of the interaction. The default implementation
   * does nothing.
   *
   * @param anchor the position where the interaction started
   */
   void trackStart(Point anchor, int modifiersEx);

  /**
   * Tracks a step of the interaction.
   *
   * @param anchor the position where the interaction started
   * @param lead   the current position
   */
   void trackStep(Point anchor, Point lead, int modifiersEx);

  /**
   * Tracks the end of the interaction.
   *
   * @param anchor the position where the interaction started
   * @param lead   the current position
   */
   void trackEnd(Point anchor, Point lead, int modifiersEx);

  /**
   * Tracks a double click.
   */
   void trackDoubleClick(Point p, int modifiersEx);

  /**
   * This method is invoked by the drawing view, when its transform
   * has changed. This means, that DrawingView.viewToDrawing and
   * DrawingView.drawingToView will return different values than they
   * did before.
   */
   void viewTransformChanged();

  /**
   * Creates secondary handles.
   */
   Collection<Handle> createSecondaryHandles();
}
