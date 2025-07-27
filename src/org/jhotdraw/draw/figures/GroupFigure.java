/*
 * @(#)GroupFigure.java  1.0  24. November 2003
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

package org.jhotdraw.draw.figures;

import java.awt.geom.*;

import org.jhotdraw.geom.*;

/**
 * A Figure that groups a collection of figures.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class GroupFigure extends AbstractCompositeFigure {

  public GroupFigure() {
    // TODO document why this constructor is empty
  }

  /**
   * This is a default implementation that chops the point at the rectangle
   * returned by getBounds() of the figure.
   * <p>
   * Figures which have a non-rectangular shape need to override this method.
   * <p>
   * FIXME Invoke chop on each child and return the closest point.
   */
  public Point2D.Double chop(Point2D.Double from) {
    Rectangle2D.Double r = getBounds();
    return Geom.angleToPoint(r, Geom.pointToAngle(r, from));
  }
}
