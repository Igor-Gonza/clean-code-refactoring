/*
 * @(#)NodeFigure.java  1.0  July 4, 2006
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
 *
 * Original code copyright JHotDraw:
 * Project:		JHotDraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	(c) by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.samples.net.figures;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connectors.AbstractConnector;
import org.jhotdraw.draw.connectors.Connector;
import org.jhotdraw.draw.connectors.LocatorConnector;
import org.jhotdraw.draw.locators.RelativeLocator;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.Insets2DDouble;
import org.jhotdraw.util.ResourceBundleUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;

import static org.jhotdraw.draw.AttributeKeys.DECORATOR_INSETS;

/**
 * NodeFigure.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 4, 2006, Created.
 */
@SuppressWarnings("unused")
public class NodeFigure extends TextFigure {
  private LinkedList<org.jhotdraw.draw.connectors.AbstractConnector> connectors;
  private static org.jhotdraw.draw.connectors.LocatorConnector north;
  private static org.jhotdraw.draw.connectors.LocatorConnector south;
  private static org.jhotdraw.draw.connectors.LocatorConnector east;
  private static org.jhotdraw.draw.connectors.LocatorConnector west;

  public NodeFigure() {
    RectangleFigure rf = new RectangleFigure();
    setDecorator(rf);
    createConnectors();
    setDrawDecoratorFirst(true);
    DECORATOR_INSETS.set(this, new Insets2DDouble(6, 10, 6, 10));
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.net.Labels");
    setText(labels.getString("nodeDefaultName"));
  }

  private void createConnectors() {
    connectors = new LinkedList<>();
    connectors.add(new org.jhotdraw.draw.connectors.LocatorConnector(this, new org.jhotdraw.draw.locators.RelativeLocator(0.5, 0)));
    connectors.add(new LocatorConnector(this, new org.jhotdraw.draw.locators.RelativeLocator(0.5, 1)));
    connectors.add(new org.jhotdraw.draw.connectors.LocatorConnector(this, new org.jhotdraw.draw.locators.RelativeLocator(1, 0.5)));
    connectors.add(new org.jhotdraw.draw.connectors.LocatorConnector(this, new org.jhotdraw.draw.locators.RelativeLocator(0, 0.5)));
    for (AbstractConnector c : connectors) {
      c.setVisible(true);
    }

  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    java.util.List<Handle> handles = new LinkedList<>();
    if (detailLevel == 0) {
      handles.add(new MoveHandle(this, org.jhotdraw.draw.locators.RelativeLocator.northWest()));
      handles.add(new MoveHandle(this, org.jhotdraw.draw.locators.RelativeLocator.northEast()));
      handles.add(new MoveHandle(this, org.jhotdraw.draw.locators.RelativeLocator.southWest()));
      handles.add(new MoveHandle(this, org.jhotdraw.draw.locators.RelativeLocator.southEast()));
      handles.add(new ConnectionHandle(this, org.jhotdraw.draw.locators.RelativeLocator.north(), new LineConnectionFigure()));
      handles.add(new ConnectionHandle(this, org.jhotdraw.draw.locators.RelativeLocator.east(), new LineConnectionFigure()));
      handles.add(new ConnectionHandle(this, RelativeLocator.south(), new LineConnectionFigure()));
      handles.add(new ConnectionHandle(this, org.jhotdraw.draw.locators.RelativeLocator.west(), new LineConnectionFigure()));
    }
    return handles;
  }

  @Override
  public Rectangle2D.Double getFigureDrawBounds() {
    Rectangle2D.Double b = super.getFigureDrawBounds();
    // Grow for connectors
    Geom.grow(b, 10d, 10d);
    return b;
  }

  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure figure) {
    // return closest connector
    double min = Double.MAX_VALUE;
    org.jhotdraw.draw.connectors.Connector closest = null;
    for (org.jhotdraw.draw.connectors.Connector c : connectors) {
      Point2D.Double p2 = Geom.center(c.getBounds());
      double d = Geom.length2(p.x, p.y, p2.x, p2.y);
      if (d < min) {
        min = d;
        closest = c;
      }
    }
    return closest;
  }

  @Override
  public org.jhotdraw.draw.connectors.Connector findCompatibleConnector(org.jhotdraw.draw.connectors.Connector c, boolean isStart) {
    return connectors.getFirst();
  }

  public NodeFigure clone() {
    NodeFigure that = (NodeFigure) super.clone();
    that.createConnectors();
    return that;
  }

  @Override
  protected void drawConnectors(Graphics2D g) {
    for (org.jhotdraw.draw.connectors.Connector c : connectors) {
      c.draw(g);
    }
  }

  @Override
  public int getLayer() {
    return -1; // stay below ConnectionFigures
  }

  public void setAttribute(AttributeKey key, Object newValue) {
    super.setAttribute(key, newValue);
    if (getDecorator() != null) {
      getDecorator().setAttribute(key, newValue);
    }
  }
}

