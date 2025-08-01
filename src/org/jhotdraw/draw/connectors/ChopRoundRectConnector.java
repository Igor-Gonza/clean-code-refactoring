/*
 * @(#)ChopRoundRectConnector.java  2.0  2006-01-14
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

package org.jhotdraw.draw.connectors;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.figures.RoundRectangleFigure;
import org.jhotdraw.geom.Geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.jhotdraw.draw.AttributeKeys.STROKE_PLACEMENT;

/**
 * A ChopRoundRectConnector locates a connection Point2D.Double by
 * chopping the connection at the horizontal and vertica lines defined by the
 * display box of a RoundRectangleFigure.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0  2004-03-02  Derived from class ShortestDistanceConnector of
 * JHotDraw 6.0b1.
 */
public class ChopRoundRectConnector extends ChopBoxConnector {

  /**
   * Only used for DOMStorable input.
   */
  public ChopRoundRectConnector() {
  }

  public ChopRoundRectConnector(Figure owner) {
    super(owner);
  }

  // FIXME Deal with this odd switch
  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    target = getConnectorTarget(target);
    RoundRectangleFigure rrf = (RoundRectangleFigure) target;
    Rectangle2D.Double outer = rrf.getBounds();

    double grow;
    switch (STROKE_PLACEMENT.get(target)) {
      case CENTER:
      default:
        grow = AttributeKeys.getStrokeTotalWidth(target) / 2d;
        break;
      case OUTSIDE:
        grow = AttributeKeys.getStrokeTotalWidth(target);
        break;
      case INSIDE:
        grow = 0;
        break;
    }
    Geom.grow(outer, grow, grow);

    Rectangle2D.Double inner = (Rectangle2D.Double) outer.clone();
    double gw = -(rrf.getArcWidth() + grow * 2) / 2;
    double gh = -(rrf.getArcHeight() + grow * 2) / 2;
    inner.x -= gw;
    inner.y -= gh;
    inner.width += gw * 2;
    inner.height += gh * 2;

    double angle = Geom.pointToAngle(outer, from);
    Point2D.Double p = Geom.angleToPoint(outer, Geom.pointToAngle(outer, from));

    if (p.x == outer.x || p.x == outer.x + outer.width) {
      p.y = Math.min(Math.max(p.y, inner.y), inner.y + inner.height);
    } else {
      p.x = Math.min(Math.max(p.x, inner.x), inner.x + inner.width);
    }
    return p;
  }
}
