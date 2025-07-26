/*
 * @(#)ElbowLiner.java  1.0  2006-03-28
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

package org.jhotdraw.draw.liners;

import org.jhotdraw.draw.connectors.Connector;
import org.jhotdraw.draw.figures.ConnectionFigure;
import org.jhotdraw.draw.figures.LineConnectionFigure;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import org.jhotdraw.xml.DOMStorable;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * A Liner that constrains a connection to orthogonal lines.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-03-28 Created.
 */
public class ElbowLiner implements Liner, DOMStorable {

  /**
   * Creates a new instance.
   */
  public ElbowLiner() {
  }

  public Collection<Handle> createHandles(BezierPath path) {
    return null;
  }

  public void lineout(ConnectionFigure figure) {
    BezierPath path = ((LineConnectionFigure) figure).getBezierPath();
    org.jhotdraw.draw.connectors.Connector start = figure.getStartConnector();
    Connector end = figure.getEndConnector();
    if (start == null || end == null || path == null) {
      return;
    }

    Point2D.Double sp = start.findStart(figure);
    Point2D.Double ep = end.findEnd(figure);

    path.clear();
    path.add(new BezierPath.Node(sp.x, sp.y));

    if (sp.x == ep.x || sp.y == ep.y) {
      path.add(new BezierPath.Node(ep.x, ep.y));
    } else {
      Rectangle2D.Double sb = start.getBounds();
      sb.x += 5d;
      sb.y += 5d;
      sb.width -= 10d;
      sb.height -= 10d;
      Rectangle2D.Double eb = end.getBounds();
      eb.x += 5d;
      eb.y += 5d;
      eb.width -= 10d;
      eb.height -= 10d;

      int sOutCode = sb.outcode(sp);
      if (sOutCode == 0) {
        sOutCode = Geom.outcode(sb, eb);
      }
      int eOutCode = eb.outcode(ep);
      if (eOutCode == 0) {
        eOutCode = Geom.outcode(eb, sb);
      }

      if ((sOutCode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0 && (eOutCode & (Geom.OUT_TOP | Geom.OUT_BOTTOM)) != 0) {
        path.add(new BezierPath.Node(sp.x, (sp.y + ep.y) / 2));
        path.add(new BezierPath.Node(ep.x, (sp.y + ep.y) / 2));
      } else if ((sOutCode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0 && (eOutCode & (Geom.OUT_LEFT | Geom.OUT_RIGHT)) != 0) {
        path.add(new BezierPath.Node((sp.x + ep.x) / 2, sp.y));
        path.add(new BezierPath.Node((sp.x + ep.x) / 2, ep.y));
      }

      path.add(new BezierPath.Node(ep.x, ep.y));
    }

    path.invalidatePath();
  }

  public void read(DOMInput in) {
  }

  public void write(DOMOutput out) {
  }

  public Liner clone() {
    try {
      return (Liner) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new InternalError(ex.getMessage(), ex);
    }
  }
}
