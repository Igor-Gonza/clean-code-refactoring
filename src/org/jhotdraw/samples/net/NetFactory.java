/*
 * @(#)PertFactory.java  1.0  2006-01-18
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

package org.jhotdraw.samples.net;

import org.jhotdraw.draw.connectors.ChopBoxConnector;
import org.jhotdraw.draw.connectors.LocatorConnector;
import org.jhotdraw.draw.drawings.DefaultDrawing;
import org.jhotdraw.draw.figures.GroupFigure;
import org.jhotdraw.draw.figures.LineConnectionFigure;
import org.jhotdraw.draw.figures.TextAreaFigure;
import org.jhotdraw.draw.linedecorations.ArrowTip;
import org.jhotdraw.draw.locators.RelativeLocator;
import org.jhotdraw.geom.Insets2DDouble;
import org.jhotdraw.samples.net.figures.NodeFigure;
import org.jhotdraw.xml.DefaultDOMFactory;

/**
 * PertFactory.
 *
 * @author Werner Randelshofer
 * @version 2006-01-18 Created.
 */
public class NetFactory extends DefaultDOMFactory {
  private final static Object[][] classTagArray = {
          {DefaultDrawing.class, "NetDiagram"},
          {NodeFigure.class, "node"},
          {LineConnectionFigure.class, "link"},
          {GroupFigure.class, "g"},
          {org.jhotdraw.draw.figures.GroupFigure.class, "g"},
          {TextAreaFigure.class, "ta"},

          {LocatorConnector.class, "locConnect"},
          {ChopBoxConnector.class, "rectConnect"},
          {ArrowTip.class, "arrowTip"},
          {Insets2DDouble.class, "insets"},
          {RelativeLocator.class, "relativeLoc"},
  };

  /**
   * Creates a new instance.
   */
  public NetFactory() {
    for (Object[] o : classTagArray) {
      addStorableClass((String) o[1], (Class) o[0]);
    }
  }
}
