/*
 * @(#)Liner.java  1.0  2006-01-20
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

import org.jhotdraw.draw.figures.ConnectionFigure;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.geom.BezierPath;

import java.io.Serializable;
import java.util.Collection;

/**
 * A Liner encapsulates an algorithm to lineout
 * a ConnectionFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-01-20 Created.
 */
public interface Liner extends Serializable, Cloneable {

  /**
   * Layouts the Path. This may alter the number and type of points
   * in the Path.
   *
   * @param figure The ConnectionFigure to be lined out.
   */
  void lineout(ConnectionFigure figure);

  /**
   * Creates Handle's for the Liner.
   * The ConnectionFigure can provide these handles to the user, in order
   * to let her control the lineout.
   *
   * @param path The path for which to create handles.
   */
  Collection<Handle> createHandles(BezierPath path);

  Liner clone();
}
