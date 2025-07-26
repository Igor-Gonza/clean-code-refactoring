/*
 * @(#)TextHolder.java  1.0  19. November 2003
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

import org.jhotdraw.geom.Insets2DDouble;

import java.awt.*;

/**
 * TextHolder.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface TextHolder extends Figure {
  boolean isEditable();

  Font getFont();

  Color getTextColor();

  Color getFillColor();

  TextHolder getLabelFor();

  /**
   * Gets the number of characters used to expand tabs.
   */
  int getTabSize();

  String getText();

  void setText(String text);

  /**
   * Gets the number of columns to be overlaid when the figure is edited.
   */
  int getTextColumns();

  void setFontSize(float size);

  float getFontSize();

  Insets2DDouble getInsets();
}
