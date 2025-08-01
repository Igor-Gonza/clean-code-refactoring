/*
 * @(#)LineDecoration.java  2.0  2006-01-14
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

package org.jhotdraw.draw.linedecorations;

import org.jhotdraw.draw.figures.Figure;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Decorate the start or end Point2D.Double of a line or poly line figure.
 * LineDecoration is the base class for the different line decorations.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface LineDecoration extends Cloneable, Serializable {

  /**
   * Draws the decoration in the direction specified by the two Points.
   */
  void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2);

  /**
   * Returns the radius of the decorator.
   * This is used to crop the end of the line, to prevent it from being
   * drawn it over the decorator.
   */
  double getDecorationRadius(Figure f);

  /**
   * Returns the drawing bounds of the decorator.
   */
  Rectangle2D.Double getDrawBounds(Figure f, Point2D.Double p1, Point2D.Double p2);
}
