/*
 * @(#)FontSizeLocator.java  1.0  10. March 2004
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

package org.jhotdraw.draw.locators;

import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.figures.TextHolder;

import java.awt.geom.Point2D;

import static org.jhotdraw.draw.AttributeKeys.FONT_SIZE;

/**
 * FontSizeLocator.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 10. March 2004  Created.
 */
public class FontSizeLocator implements Locator {

  /**
   * Creates a new instance.
   */
  public FontSizeLocator() {
  }

  /**
   * Locates a position on the provided figure.
   *
   * @return a Point2D.Double on the figure.
   */
  public Point2D.Double locate(Figure owner) {
    Point2D.Double p = owner.getStartPoint();
    p.y += FONT_SIZE.get(owner);
    if (owner instanceof TextHolder) {
      p.y += ((TextHolder) owner).getInsets().top;
    }
    return p;
  }

  public Point2D.Double locate(Figure owner, Figure dependent) {
    return locate(owner);
  }
}
