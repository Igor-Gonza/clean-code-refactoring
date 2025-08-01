/*
 * @(#)ConnectionFigure.java  2.0  2006-01-14
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

import org.jhotdraw.draw.connectors.Connector;
import org.jhotdraw.draw.liners.Liner;

import java.awt.geom.Point2D;

/**
 * Figures to connect Connectors provided by Figures.
 * A ConnectionFigure knows its start and end Connector.
 * It uses the Connectors to locate its connection points.<p>
 * A ConnectionFigure can have multiple bezier segments. It provides
 * operations to split and join bezier segments.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public interface ConnectionFigure extends Figure {
  // DRAWING
  // SHAPE AND BOUNDS
  // ATTRIBUTES
  // EDITING
  // CONNECTING

  /**
   * Sets the start Connector of the connection.
   * Set this to null to disconnect the start connection.
   *
   * @param start the start figure of the connection
   */
  void setStartConnector(Connector start);

  /**
   * Gets the start Connector.
   * Returns null, if there is no start connection.
   */
  Connector getStartConnector();

  /**
   * Sets the end Connector of the connection.
   * Set this to null to disconnect the end connection.
   *
   * @param end the end figure of the connection
   */
  void setEndConnector(Connector end);

  /**
   * Gets the end Connector.
   * Returns null, if there is no end connection.
   */
  Connector getEndConnector();

  /**
   * Updates the connection.
   * FIXME - What das this do?
   */
  void updateConnection();

  /**
   * Checks if two figures can be connected using this ConnectionFigure.
   * Implement this method to constrain the allowed connections between figures.
   */
  boolean canConnect(Figure start, Figure end);

  /**
   * Checks if this ConnectionFigure can be attached to the provided
   * start figure.
   * This is used to provide an early feedback to the user, when he/she
   * creates a new connection.
   */
  boolean canConnect(Figure start);

  /**
   * Checks if the ConnectionFigure connects the same figures.
   * <p>
   * FIXME - What do we need this for?
   */
  boolean connectsSame(ConnectionFigure other);

  /**
   * Sets the start point.
   */
  void setStartPoint(Point2D.Double p);

  /**
   * Sets the end point.
   */
  void setEndPoint(Point2D.Double p);

  /**
   * Sets the specified point.
   */
  void setPoint(int index, Point2D.Double p);

  /**
   * Gets the point count.
   */
  int getPointCount();

  /**
   * Returns the specified point.
   */
  Point2D.Double getPoint(int index);

  /**
   * Gets the start point.
   */
  Point2D.Double getStartPoint();

  /**
   * Gets the end point.
   */
  Point2D.Double getEndPoint();

  /**
   * Gets the start figure of the connection.
   * This is a convenience method for doing getStartConnector().getOwner()
   * and handling null cases.
   */
  Figure getStartFigure();

  /**
   * Gets the end figure of the connection.
   * This is a convenience method for doing getEndConnector().getOwner()
   * and handling null cases.
   */
  Figure getEndFigure();
// COMPOSITE FIGURES

  /**
   * Get a Liner object which encapsulated a lineout
   * algorithm for this figure. Typically, a Liner
   * accesses the child components of this figure and arranges
   * their graphical presentation.
   *
   * @return lineout strategy used by this figure
   */
  Liner getLiner();

  /**
   * A lineout algorithm is used to define how the child components
   * should be laid out in relation to each other. The task for
   * line outing the child components for presentation is delegated
   * to a Liner which can be plugged in at runtime.
   */
  void lineout();

  /**
   * Set a Liner object which encapsulated a lineout
   * algorithm for this figure. Typically, a Liner
   * accesses the child components of this figure and arranges
   * their graphical presentation. It is a good idea to set
   * the Liner in the protected initialize() method
   * so it can be recreated if a GraphicalCompositeFigure is
   * read and restored from a StorableInput stream.
   *
   * @param newValue encapsulation of a lineout algorithm.
   */
  void setLiner(Liner newValue);
  // CLONING
  // EVENT HANDLING
}
