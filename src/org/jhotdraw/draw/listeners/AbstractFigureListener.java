/*
 * @(#)AbstractFigureListener.java  1.0  2. February 2004
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

package org.jhotdraw.draw.listeners;

import org.jhotdraw.draw.events.FigureEvent;

/**
 * AbstractFigureListener.
 *
 * @author Werner Randelshofer
 * @version 1.0 2. February 2004  Created.
 */
// TODO Unimplemented class
public abstract class AbstractFigureListener implements FigureListener {

  public void figureAreaInvalidated(org.jhotdraw.draw.events.FigureEvent e) {
  }

  public void figureAttributeChanged(org.jhotdraw.draw.events.FigureEvent e) {
  }

  public void figureAdded(FigureEvent e) {
  }

  public void figureChanged(org.jhotdraw.draw.events.FigureEvent e) {
  }

  public void figureRemoved(org.jhotdraw.draw.events.FigureEvent e) {
  }

  public void figureRequestRemove(org.jhotdraw.draw.events.FigureEvent e) {
  }
}
