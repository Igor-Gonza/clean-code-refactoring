/*
 * @(#)LocatorHandle.java  2.0  2006-01-14
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

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A LocatorHandle implements a Handle by delegating the location requests to
 * a Locator object.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 * @see Locator
 */
public abstract class LocatorHandle extends AbstractHandle {
  private final Locator locator;

  /**
   * Initializes the LocatorHandle with the given Locator.
   */
  public LocatorHandle(Figure owner, Locator l) {
    super(owner);
    locator = l;
  }

  public Point2D.Double getLocationOnDrawing() {
    return locator.locate(getOwner());
  }

  public Point getLocation() {
    return view.drawingToView(locator.locate(getOwner()));
  }

  protected Rectangle basicGetBounds() {
    Rectangle r = new Rectangle(getLocation());
    r.grow(getHandleSize() / 2, getHandleSize() / 2);
    return r;
  }
}
