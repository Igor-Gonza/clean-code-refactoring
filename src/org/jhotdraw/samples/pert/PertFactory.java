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

package org.jhotdraw.samples.pert;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connectors.ChopBoxConnector;
import org.jhotdraw.draw.drawings.DefaultDrawing;
import org.jhotdraw.draw.figures.ListFigure;
import org.jhotdraw.draw.figures.TextFigure;
import org.jhotdraw.samples.pert.figures.DependencyFigure;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.samples.pert.figures.TaskFigure;
import org.jhotdraw.xml.DefaultDOMFactory;

/**
 * PertFactory.
 *
 * @author Werner Randelshofer
 * @version 2006-01-18 Created.
 */
public class PertFactory extends DefaultDOMFactory {
  private final static Object[][] classTagArray = {
          {DefaultDrawing.class, "PertDiagram"},
          {TaskFigure.class, "task"},
          {DependencyFigure.class, "dep"},
          {ListFigure.class, "list"},
          {TextFigure.class, "text"},
          {org.jhotdraw.draw.figures.GroupFigure.class, "g"},
          {org.jhotdraw.draw.figures.TextAreaFigure.class, "ta"},
          {SeparatorLineFigure.class, "separator"},

          {ChopBoxConnector.class, "rectConnector"},
          {ArrowTip.class, "arrowTip"}
  };

  /**
   * Creates a new instance.
   */
  public PertFactory() {
    for (Object[] o : classTagArray) {
      addStorableClass((String) o[1], (Class) o[0]);
    }
  }
}
