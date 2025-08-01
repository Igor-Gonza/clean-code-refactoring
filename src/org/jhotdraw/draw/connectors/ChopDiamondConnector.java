/*
 * @(#)ChopDiamondConnector.java  1.0  27. März 2006
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

import java.awt.geom.*;

import static org.jhotdraw.draw.AttributeKeys.*;

import org.jhotdraw.draw.figures.DiamondFigure;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.geom.*;

/**
 * A ChopDiamondConnector locates connection points by choping the
 * connection between the centers of the two figures at the edge of
 * a diamond figure.
 *
 * @author Werner Randelshofer
 * @version 1.0 27. März 2006 Created.
 */
public class ChopDiamondConnector extends ChopBoxConnector {

  public ChopDiamondConnector() {
    // only used for Storable implementation
  }

  public ChopDiamondConnector(Figure owner) {
    super(owner);
  }

  /**
   * Return an appropriate connection point on the edge of a diamond figure
   */
  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    target = getConnectorTarget(target);
    Rectangle2D.Double r = target.getBounds();

    if (DiamondFigure.IS_QUADRATIC.get(target)) {
      double side = Math.max(r.width, r.height);
      r.x -= (side - r.width) / 2;
      r.y -= (side - r.height) / 2;
      r.width = r.height = side;
    }
    double growX;
    double growY;
    // FIXME - This code is wrong. Copy correct code from DiamondFigure.
    switch (STROKE_PLACEMENT.get(target)) {
      case INSIDE: {
        growX = growY = 0f;
        break;
      }
      case OUTSIDE: {
        double lineLength = Math.sqrt(r.width * r.width + r.height * r.height);
        double scale = getStrokeTotalWidth(target) * 2d / lineLength;
        growX = scale * r.height;
        growY = scale * r.width;
        //growY = getStrokeTotalWidth() * SQRT2;
        break;
      }
      case CENTER:
      default:
        double lineLength = Math.sqrt(r.width * r.width + r.height * r.height);
        double scale = getStrokeTotalWidth(target) / lineLength;
        growX = scale * r.height;
        growY = scale * r.width;
        //growX = growY = getStrokeTotalWidth() / 2d * SQRT2;
        break;
    }
    Geom.grow(r, growX, growY);

    // Center point
    Point2D.Double c1 = new Point2D.Double(r.x + r.width / 2, r.y + (r.height / 2));
    Point2D.Double p2 = new Point2D.Double(r.x + r.width / 2, r.y + r.height);
    Point2D.Double p4 = new Point2D.Double(r.x + r.width / 2, r.y);

    // If overlapping, just return the opposite corners
    if (r.contains(from)) {
      if (from.y > r.y && from.y < (r.y + r.height / 2)) {
        return p2;
      } else {
        return p4;
      }
    }

    // Calculate angle to determine quadrant
    double ang = Geom.pointToAngle(r, from);

    // Determine line points
    Point2D.Double p1 = new Point2D.Double(r.x + r.width, r.y + (r.height / 2));
    Point2D.Double p3 = new Point2D.Double(r.x, r.y + (r.height / 2));
    Point2D.Double rp = null; // This will be returned

    // Get the intersection with edges
    if (ang > 0 && ang < 1.57) {
      rp = Geom.intersect(p1.x, p1.y, p2.x, p2.y, c1.x, c1.y, from.x, from.y);
    } else if (ang > 1.575 && ang < 3.14) {
      rp = Geom.intersect(p2.x, p2.y, p3.x, p3.y, c1.x, c1.y, from.x, from.y);
    } else if (ang > -3.14 && ang < -1.575) {
      rp = Geom.intersect(p3.x, p3.y, p4.x, p4.y, c1.x, c1.y, from.x, from.y);
    } else if (ang > -1.57 && ang < 0) {
      rp = Geom.intersect(p4.x, p4.y, p1.x, p1.y, c1.x, c1.y, from.x, from.y);
    }

    // No proper edge found, we should send one of four corners
    if (rp == null) {
      rp = Geom.angleToPoint(r, ang);
    }

    return rp;
  }
}
