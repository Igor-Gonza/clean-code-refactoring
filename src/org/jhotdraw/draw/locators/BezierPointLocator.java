/*
 * @(#)BezierPointLocator.java  2.1  2006-06-08
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

import org.jhotdraw.draw.figures.BezierFigure;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.geom.Point2D;

/**
 * BezierPointLocator.
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-07-08 Added support for DOMStorable.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
@SuppressWarnings("unused")
public class BezierPointLocator extends AbstractLocator {
  private int index;
  private int coordinates;

  public BezierPointLocator(int index) {
    this.index = index;
    this.coordinates = 0;
  }

  public BezierPointLocator(int index, int coordinates) {
    this.index = index;
    this.coordinates = index;
  }

  public Point2D.Double locate(Figure owner) {
    BezierFigure plf = (BezierFigure) owner;
    if (index < plf.getNodeCount()) {
      return plf.getPoint(index, coordinates);
    }
    return new Point2D.Double(0, 0);
  }

  public void write(DOMOutput out) {
    out.addAttribute("index", index, 0);
    out.addAttribute("coordinates", coordinates, 0);
  }

  public void read(DOMInput in) {
    index = in.getAttribute("index", 0);
    coordinates = in.getAttribute("coordinates", 0);
  }
}
