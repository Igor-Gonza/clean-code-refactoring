/*
 * @(#)NullHandle.java  1.0  2003-12-01
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

import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.locators.Locator;
import org.jhotdraw.draw.locators.RelativeLocator;

import java.awt.*;
import java.util.Collection;

/**
 * A handle that doesn't change the owned figure. Its only purpose is
 * to show feedback that a figure is selected.
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class NullHandle extends LocatorHandle {

  public NullHandle(Figure owner, Locator locator) {
    super(owner, locator);
  }

  public Cursor getCursor() {
    return Cursor.getDefaultCursor();
  }

  public void trackStart(Point anchor, int modifiersEx) {
    // TODO document why this method is empty
  }

  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    // TODO document why this method is empty
  }

  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    // TODO document why this method is empty
  }

  /**
   * Creates handles for each lead of a
   * figure and adds them to the provided collection.
   */
  public static void addLeadHandles(Figure f, Collection<Handle> handles) {
    handles.add(new NullHandle(f, new RelativeLocator(0f, 0f)));
    handles.add(new NullHandle(f, new RelativeLocator(0f, 1f)));
    handles.add(new NullHandle(f, new RelativeLocator(1f, 0f)));
    handles.add(new NullHandle(f, new RelativeLocator(1f, 1f)));
  }

  /**
   * Draws this handle.
   * Null Handles are drawn as unfilled rectangles.
   */
  public void draw(Graphics2D g) {
    Rectangle r = getBounds();

    g.setColor(Color.white);
    g.drawRect(r.x + 1, r.y + 1, r.width - 3, r.height - 3);

    g.setStroke(new BasicStroke());
    g.setColor(Color.black);
    g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
  }

}
