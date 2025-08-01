/*
 * @(#)ListFigure.java  1.1  2006-07-08
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

import org.jhotdraw.draw.layouters.VerticalLayouter;
import org.jhotdraw.geom.Insets2DDouble;

/**
 * A ListFigure consists of a list of Figures and a RectangleFigure.
 *
 * @author Werner Randelshofer
 * @version 1.2 2006-07-08 Method setInsets() removed, because it is
 * redundant with the setter/getter in LAYOUT_INSETS.
 * <br>1.0 30. January 2004  Created.
 */
public class ListFigure extends GraphicalCompositeFigure {

  public ListFigure() {
    this(null);
  }

  public ListFigure(Figure presentationFigure) {
    super(presentationFigure);
    setLayouter(new VerticalLayouter());
    LAYOUT_INSETS.set(this, new Insets2DDouble(4, 8, 4, 8));
  }
}
