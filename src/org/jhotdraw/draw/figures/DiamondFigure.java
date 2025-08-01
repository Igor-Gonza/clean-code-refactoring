/*
 * @(#)DiamondFigure.java  1.0  2006-03-27
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

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.connectors.ChopDiamondConnector;
import org.jhotdraw.draw.connectors.Connector;
import org.jhotdraw.geom.Geom;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A diamond with vertices at the midpoints of its enclosing rectangle.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-03-27 Created.
 */
@SuppressWarnings("unused")
public class DiamondFigure extends AttributedFigure {
  /**
   * If the attribute IS_QUADRATIC is set to true, all sides of the diamond have
   * the same length.
   */
  public static final AttributeKey<Boolean> IS_QUADRATIC = new AttributeKey<>("isQuadratic", false);

  /**
   * The bounds of the diamond figure.
   */
  private Rectangle2D.Double rectangle;

  /**
   * Creates a new instance.
   */
  public DiamondFigure() {
    this(0, 0, 0, 0);
  }

  public DiamondFigure(double x, double y, double width, double height) {
    rectangle = new Rectangle2D.Double(x, y, width, height);
        /*
        setFillColor(Color.white);
        setStrokeColor(Color.black);
         */
  }

  // DRAWING
  protected void drawFill(Graphics2D g) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    if (isQuadratic()) {
      double side = Math.max(r.width, r.height);
      r.x -= (side - r.width) / 2;
      r.y -= (side - r.height) / 2;
      r.width = r.height = side;
    }

    double grow = AttributeKeys.getPerpendicularFillGrowth(this);
    double growX;
    double growY;
    if (grow == 0d) {
      growX = growY = 0d;
    } else {
      double w = r.width / 2d;
      double h = r.height / 2d;
      double lineLength = Math.sqrt(w * w + h * h);
      double scale = grow / lineLength;
      double yb = scale * w;
      double xa = scale * h;

      growX = ((yb * yb) / xa + xa);
      growY = ((xa * xa) / yb + yb);

      Geom.grow(r, growX, growY);
    }

    GeneralPath diamond = new GeneralPath();
    diamond.moveTo((float) (r.x + r.width / 2), (float) r.y);
    diamond.lineTo((float) (r.x + r.width), (float) (r.y + r.height / 2));
    diamond.lineTo((float) (r.x + r.width / 2), (float) (r.y + r.height));
    diamond.lineTo((float) r.x, (float) (r.y + r.height / 2));
    diamond.closePath();
    g.fill(diamond);
  }

  protected void drawStroke(Graphics2D g) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    if (isQuadratic()) {
      double side = Math.max(r.width, r.height);
      r.x -= (side - r.width) / 2;
      r.y -= (side - r.height) / 2;
      r.width = r.height = side;
    }

    double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
    double growX;
    double growY;
    if (grow == 0d) {
      growX = growY = 0d;
    } else {
      double w = r.width / 2d;
      double h = r.height / 2d;
      double lineLength = Math.sqrt(w * w + h * h);
      double scale = grow / lineLength;
      double yb = scale * w;
      double xa = scale * h;

      growX = ((yb * yb) / xa + xa);
      growY = ((xa * xa) / yb + yb);

      Geom.grow(r, growX, growY);
    }

    GeneralPath diamond = new GeneralPath();
    diamond.moveTo((float) (r.x + r.width / 2), (float) r.y);
    diamond.lineTo((float) (r.x + r.width), (float) (r.y + r.height / 2));
    diamond.lineTo((float) (r.x + r.width / 2), (float) (r.y + r.height));
    diamond.lineTo((float) r.x, (float) (r.y + r.height / 2));
    diamond.closePath();
    g.draw(diamond);
  }

  // SHAPE AND BOUNDS
  public Rectangle2D.Double getBounds() {
    return (Rectangle2D.Double) rectangle.clone();
  }

  public Rectangle2D.Double getFigureDrawBounds() {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    if (isQuadratic()) {
      double side = Math.max(r.width, r.height);
      r.x -= (side - r.width) / 2;
      r.y -= (side - r.height) / 2;
      r.width = r.height = side;
    }
    double grow = AttributeKeys.getPerpendicularHitGrowth(this);
    double growX;
    double growY;
    if (grow == 0d) {
      growX = growY = 0d;
    } else {
      double w = r.width / 2d;
      double h = r.height / 2d;
      double lineLength = Math.sqrt(w * w + h * h);
      double scale = grow / lineLength;
      double yb = scale * w;
      double xa = scale * h;

      growX = ((yb * yb) / xa + xa);
      growY = ((xa * xa) / yb + yb);

      Geom.grow(r, growX, growY);
    }

    return r;
  }

  /**
   * Checks if a Point2D.Double is inside the figure.
   */
  public boolean contains(Point2D.Double p) {
    Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
    if (isQuadratic()) {
      double side = Math.max(r.width, r.height);
      r.x -= (side - r.width) / 2;
      r.y -= (side - r.height) / 2;
      r.width = r.height = side;
    }
    //   if (r.contains(p)) {

    double grow = AttributeKeys.getPerpendicularFillGrowth(this);
    double growX;
    double growY;
    if (grow == 0d) {
      growX = growY = 0d;
    } else {
      double w = r.width / 2d;
      double h = r.height / 2d;
      double lineLength = Math.sqrt(w * w + h * h);
      double scale = grow / lineLength;
      double yb = scale * w;
      double xa = scale * h;

      growX = ((yb * yb) / xa + xa);
      growY = ((xa * xa) / yb + yb);

      Geom.grow(r, growX, growY);
    }

    GeneralPath diamond = new GeneralPath();
    diamond.moveTo((float) (r.x + r.width / 2), (float) r.y);
    diamond.lineTo((float) (r.x + r.width), (float) (r.y + r.height / 2));
    diamond.lineTo((float) (r.x + r.width / 2), (float) (r.y + r.height));
    diamond.lineTo((float) r.x, (float) (r.y + r.height / 2));
    diamond.closePath();
    return diamond.contains(p);
  }

  public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
    rectangle.x = Math.min(anchor.x, lead.x);
    rectangle.y = Math.min(anchor.y, lead.y);
    rectangle.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
    rectangle.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
  }

  /**
   * Moves the Figure to a new location.
   *
   * @param tx the transformation matrix.
   */
  public void basicTransform(AffineTransform tx) {
    Point2D.Double anchor = getStartPoint();
    Point2D.Double lead = getEndPoint();
    basicSetBounds((Point2D.Double) tx.transform(anchor, anchor), (Point2D.Double) tx.transform(lead, lead));
  }

  public void restoreTo(Object geometry) {
    Rectangle2D.Double r = (Rectangle2D.Double) geometry;
    rectangle.x = r.x;
    rectangle.y = r.y;
    rectangle.width = r.width;
    rectangle.height = r.height;
  }

  public Object getRestoreData() {
    return rectangle.clone();
  }

  // ATTRIBUTES
  public boolean isQuadratic() {
    Boolean b = (Boolean) getAttribute(IS_QUADRATIC);
    return b != null && b;
  }

  public void setQuadratic(boolean newValue) {
    setAttribute(IS_QUADRATIC, newValue);
  }
// EDITING
// CONNECTING

  /**
   * Returns the Figures connector for the specified location.
   * By default, a ChopDiamondConnector is returned.
   *
   * @see ChopDiamondConnector
   */
  public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
    return new ChopDiamondConnector(this);
  }

  public Connector findCompatibleConnector(Connector c, boolean isStart) {
    return new ChopDiamondConnector(this);
  }

  // COMPOSITE FIGURES
// CLONING
  public DiamondFigure clone() {
    DiamondFigure that = (DiamondFigure) super.clone();
    that.rectangle = (Rectangle2D.Double) this.rectangle.clone();
    return that;
  }
// EVENT HANDLING
}
