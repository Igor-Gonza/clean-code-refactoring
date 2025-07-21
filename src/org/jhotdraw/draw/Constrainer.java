/*
 * @(#)Constrainer.java  2.1  2006-07-03
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

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Interface to constrain a Point2D.Double. This can be used to implement
 * different kinds of grids.
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-07-03 Method isVisible() added.
 * <br>2.0 2006-01-17 Changed to support double precision coordinates.
 * <br>1.0 2004-03-14  Created.
 */
public interface Constrainer {
  /**
   * Constrains the given point.
   * This method changes the point which is passed as a parameter.
   *
   * @return constrained point.
   */
  Point2D.Double constrainPoint(Point2D.Double p);

  /**
   * Returns true if the Constrainer grid is visible.
   */
  boolean isVisible();

  /**
   * Draws the constrainer grid for the specified drawing view.
   */
  void draw(Graphics2D g, DrawingView view);
}
