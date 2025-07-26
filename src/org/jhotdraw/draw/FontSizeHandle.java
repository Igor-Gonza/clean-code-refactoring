/*
 * @(#)FontSizeHandle.java  2.0  2006-01-14
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

package org.jhotdraw.draw;

import org.jhotdraw.draw.figures.TextHolder;
import org.jhotdraw.draw.locators.FontSizeLocator;
import org.jhotdraw.draw.locators.Locator;
import org.jhotdraw.undo.CompositeEdit;

import java.awt.*;

/**
 * FontSizeHandle.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class FontSizeHandle extends LocatorHandle {
  private float oldSize;
  private CompositeEdit edit;

  /**
   * Creates a new instance.
   */
  public FontSizeHandle(org.jhotdraw.draw.figures.TextHolder owner) {
    super(owner, new FontSizeLocator());
  }

  public FontSizeHandle(org.jhotdraw.draw.figures.TextHolder owner, Locator locator) {
    super(owner, locator);
  }

  /**
   * Draws this handle.
   */
  public void draw(Graphics2D g) {
    drawDiamond(g, Color.yellow, Color.black);
  }

  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
  }

  protected Rectangle basicGetBounds() {
    Rectangle r = new Rectangle(getLocation());
    r.grow(getHandleSize() / 2 + 1, getHandleSize() / 2 + 1);
    return r;
  }

  public void trackStart(Point anchor, int modifiersEx) {
    view.getDrawing().fireUndoableEditHappened(edit = new CompositeEdit("Schriftgrösse"));
    org.jhotdraw.draw.figures.TextHolder textOwner = (org.jhotdraw.draw.figures.TextHolder) getOwner();
    oldSize = textOwner.getFontSize();
  }

  public void trackStep(Point anchor, Point lead, int modifiersEx) {
    org.jhotdraw.draw.figures.TextHolder textOwner = (TextHolder) getOwner();

    float newSize = (float) Math.max(1, oldSize + view.viewToDrawing(new Point(0, lead.y - anchor.y)).y);
    textOwner.setFontSize(newSize);
  }

  public void trackEnd(Point anchor, Point lead, int modifiersEx) {
    view.getDrawing().fireUndoableEditHappened(edit);
  }
}
