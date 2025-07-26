/*
 * @(#)DrawFigureFactory.java  1.0  February 17, 2004
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

package org.jhotdraw.samples.draw;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connectors.ChopBezierConnector;
import org.jhotdraw.draw.connectors.ChopBoxConnector;
import org.jhotdraw.draw.connectors.ChopDiamondConnector;
import org.jhotdraw.draw.connectors.ChopEllipseConnector;
import org.jhotdraw.draw.connectors.ChopRoundRectConnector;
import org.jhotdraw.draw.connectors.ChopTriangleConnector;
import org.jhotdraw.draw.figures.BezierFigure;
import org.jhotdraw.draw.figures.DiamondFigure;
import org.jhotdraw.draw.figures.GroupFigure;
import org.jhotdraw.draw.figures.RectangleFigure;
import org.jhotdraw.xml.DefaultDOMFactory;

/**
 * DrawFigureFactory.
 *
 * @author Werner Randelshofer
 * @version 1.0 February 17, 2004, Created.
 */
public class DrawFigureFactory extends DefaultDOMFactory {
  private static final Object[][] classTagArray = {
          {DefaultDrawing.class, "drawing"},
          {QuadTreeDrawing.class, "drawing"},
          {DiamondFigure.class, "diamond"},
          {org.jhotdraw.draw.figures.TriangleFigure.class, "triangle"},
          {org.jhotdraw.draw.figures.BezierFigure.class, "bezier"},
          {RectangleFigure.class, "r"},
          {org.jhotdraw.draw.figures.RoundRectangleFigure.class, "rr"},
          {org.jhotdraw.draw.figures.LineFigure.class, "l"},
          {BezierFigure.class, "b"},
          {org.jhotdraw.draw.figures.LineConnectionFigure.class, "lnk"},
          {EllipseFigure.class, "e"},
          {org.jhotdraw.draw.figures.TextFigure.class, "t"},
          {org.jhotdraw.draw.figures.TextAreaFigure.class, "ta"},
          {GroupFigure.class, "g"},

          {ArrowTip.class, "arrowTip"},
          {ChopBoxConnector.class, "rConnector"},
          {ChopEllipseConnector.class, "ellipseConnector"},
          {ChopRoundRectConnector.class, "rrConnector"},
          {ChopTriangleConnector.class, "triangleConnector"},
          {ChopDiamondConnector.class, "diamondConnector"},
          {ChopBezierConnector.class, "bezierConnector"},

          {ElbowLiner.class, "elbowLiner"},
  };
  private static final Object[][] enumTagArray = {
          {AttributeKeys.StrokePlacement.class, "strokePlacement"},
          {AttributeKeys.StrokeType.class, "strokeType"},
          {AttributeKeys.Underfill.class, "underfill"},
          {AttributeKeys.Orientation.class, "orientation"},
  };

  /**
   * Creates a new instance.
   */
  public DrawFigureFactory() {
    for (Object[] o : classTagArray) {
      addStorableClass((String) o[1], (Class<?>) o[0]);
    }
    for (Object[] o : enumTagArray) {
      addEnumClass((String) o[1], (Class<?>) o[0]);
    }
  }
}
