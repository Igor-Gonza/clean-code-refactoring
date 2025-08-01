/*
 * @(#)ChopEllipseConnector.java  2.1  2006-05-18
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

import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.geom.Geom;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * A ChopEllipseConnector locates a connection Point2D.Double by
 * chopping the connection at the ellipse defined by the
 * figure's display box.
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-05-18 Reworked.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
@SuppressWarnings("unused")
public class ChopEllipseConnector extends ChopBoxConnector {
  /**
   * Only used for DOMStorable input.
   */
  public ChopEllipseConnector() {
  }

  public ChopEllipseConnector(Figure owner) {
    super(owner);
  }

  private Color getStrokeColor(Figure f) {
    return (Color) f.getAttribute(STROKE_COLOR);
  }

  private float getStrokeWidth(Figure f) {
    Float w = (Float) f.getAttribute(STROKE_WIDTH);
    return (w == null) ? 1f : w;
  }

  // FIXME Deal with this odd switch
  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    target = getConnectorTarget(target);
    Rectangle2D.Double r = target.getBounds();
    if (getStrokeColor(target) != null) {
      double grow;
      switch (STROKE_PLACEMENT.get(target)) {
        case CENTER:
        default:
          grow = getStrokeTotalWidth(target) / 2d;
          break;
        case OUTSIDE:
          grow = getStrokeTotalWidth(target);
          break;
        case INSIDE:
          grow = 0f;
          break;
      }
      Geom.grow(r, grow, grow);
    }
    double angle = Geom.pointToAngle(r, from);
    return Geom.ovalAngleToPoint(r, angle);
  }
}
