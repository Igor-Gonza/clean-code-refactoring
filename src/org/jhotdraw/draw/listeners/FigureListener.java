/*
 * @(#)FigureListener.java  1.0  11. November 2003
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

import java.util.EventListener;

/**
 * Listener interested in Figure changes.
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface FigureListener extends EventListener {

  /**
   * Sent when an area used by the figure needs to be repainted.
   */
  void figureAreaInvalidated(FigureEvent e);

  /**
   * Sent when an attribute of the figure has changed.
   */
  void figureAttributeChanged(FigureEvent e);

  /**
   * Sent when the geometry (e.g. the bounds) of the figure has changed.
   */
  void figureChanged(FigureEvent e);

  /**
   * Sent when a figure was added to a drawing.
   */
  void figureAdded(FigureEvent e);

  /**
   * Sent when a figure was removed from a drawing.
   */
  void figureRemoved(FigureEvent e);

  /**
   * Sent when the figure requests to be removed from a drawing.
   */
  void figureRequestRemove(FigureEvent e);
}
