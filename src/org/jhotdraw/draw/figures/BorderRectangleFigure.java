/*
 * @(#)BorderRectangle2D.DoubleFigure.java  1.0  8. April 2004
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

package org.jhotdraw.draw.figures;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * BorderRectangle2D.DoubleFigure.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 8. April 2004  Created.
 */
public class BorderRectangleFigure extends RectangleFigure {
  protected Border border;
  protected static final JComponent borderComponent = new JPanel();

  public BorderRectangleFigure(Border border) {
    this.border = border;
  }

  public void drawFigure(Graphics2D g) {
    Rectangle bounds = getBounds().getBounds();
    border.paintBorder(borderComponent, g, bounds.x, bounds.y, bounds.width, bounds.height);
  }
}
