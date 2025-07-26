/*
 * @(#)ChopBezierConnector.java  2.0  2006-01-14
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

import org.jhotdraw.draw.figures.BezierFigure;
import org.jhotdraw.draw.figures.Figure;

import java.awt.geom.Point2D;

/**
 * ChopBezierConnector.
 * <p>
 * XXX - This connector does not take the stroke width of the polygon into
 * account.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 5. März 2004  Created.
 */
public class ChopBezierConnector extends ChopBoxConnector {

  /**
   * Creates a new instance.
   */
  public ChopBezierConnector() {
  }

  public ChopBezierConnector(BezierFigure owner) {
    super(owner);
  }

  protected Point2D.Double chop(Figure target, Point2D.Double from) {
    BezierFigure bf = (BezierFigure) getConnectorTarget(target);
    return bf.chop(from);
  }
}
