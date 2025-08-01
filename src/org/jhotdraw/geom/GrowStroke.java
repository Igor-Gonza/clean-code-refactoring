/*
 * @(#)GrowStroke.java  1.0  June 9, 2006
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

package org.jhotdraw.geom;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * GrowStroke can be used to grow/shrink a figure by a specified line width.
 * This only works with closed convex paths having edges in clockwise direction.
 * <p>
 * Note: Although this is a Stroke object, it does not actually create a stroked
 * shape, but one that can be used for filling.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 9, 2006, Created.
 */
public class GrowStroke extends DoubleStroke {
  private float grow;

  public GrowStroke(float grow, float miterLimit) {
    super(grow * 2, 1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, miterLimit, null, 0f);
    this.grow = grow;
  }

  public Shape createStrokedShape(Shape s) {
    BezierPath bp = new BezierPath();
    GeneralPath left = new GeneralPath();
    GeneralPath right = new GeneralPath();

    double[] coordinates = new double[6];
    // FIXME - We only do a flattened path
    for (PathIterator i = s.getPathIterator(null, 0.1d); !i.isDone(); i.next()) {
      int type = i.currentSegment(coordinates);

      switch (type) {
        case PathIterator.SEG_MOVETO:
          if (!bp.isEmpty()) {
            traceStroke(bp, left, right);
          }
          bp.clear();
          bp.moveTo(coordinates[0], coordinates[1]);
          break;
        case PathIterator.SEG_LINETO:
          if (coordinates[0] != bp.get(bp.size() - 1).x[0] || coordinates[1] != bp.get(bp.size() - 1).y[0]) {
            bp.lineTo(coordinates[0], coordinates[1]);
          }
          break;
        case PathIterator.SEG_QUADTO:
          bp.quadTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
          break;
        case PathIterator.SEG_CUBICTO:
          bp.curveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4], coordinates[5]);
          break;
        case PathIterator.SEG_CLOSE:
          bp.setClosed(true);
          break;
      }
    }
    if (bp.size() > 1) {
      traceStroke(bp, left, right);
    }

    if (left.getBounds2D().contains(right.getBounds2D())) {
      return (grow > 0) ? left : right;
    } else {
      return (grow > 0) ? right : left;
    }
  }

}