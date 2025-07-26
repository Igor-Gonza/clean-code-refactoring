/*
 * @(#)SVGPath.java  1.0  July 8, 2006
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

package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figures.AbstractAttributedCompositeFigure;
import org.jhotdraw.draw.figures.BezierFigure;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.draw.handlers.RotateHandle;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.samples.svg.SVGUtil;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;

import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * SVGPath is a composite Figure which contains one or more
 * BezierFigures as its children.
 * <p>
 * XXX - Roll in the read() method of SVGLine.
 *
 * @author Werner Randelshofer
 * @version 1.0 July 8, 2006, Created.
 */
public class SVGPath extends AbstractAttributedCompositeFigure implements SVGFigure {
  /**
   * This path is used for drawing.
   */
  private GeneralPath path;

  /**
   * Creates a new instance.
   */
  public SVGPath() {
    add(new org.jhotdraw.draw.figures.BezierFigure());
    SVGUtil.setDefaults(this);
  }

  public void drawFigure(Graphics2D g) {
    validatePath();
    if (AttributeKeys.FILL_COLOR.get(this) != null) {
      g.setColor(AttributeKeys.FILL_COLOR.get(this));
      drawFill(g);
    }
    if (STROKE_COLOR.get(this) != null) {
      g.setStroke(AttributeKeys.getStroke(this));
      g.setColor(STROKE_COLOR.get(this));

      drawStroke(g);
    }
    if (isConnectorsVisible()) {
      drawConnectors(g);
    }
    //g.drawString(this.toString()+" "+getChildCount(), (float) getBounds().x, (float) getBounds().y);
  }

  public void drawFill(Graphics2D g) {
    if (!getChildren().isEmpty() /*&&
                ((BezierFigure) getChildren().get(0)).isClosed()*/) {
      g.fill(path);
    }
  }

  public void drawStroke(Graphics2D g) {
    g.draw(path);
  }

  public void invalidate() {
    super.invalidate();
    invalidatePath();
  }

  protected void validate() {
    validatePath();
    super.validate();
  }

  protected void validatePath() {
    if (path == null) {
      path = new GeneralPath();
      path.setWindingRule(WINDING_RULE.get(this) == WindingRule.EVEN_ODD ?
              GeneralPath.WIND_EVEN_ODD :
              GeneralPath.WIND_NON_ZERO
      );
      for (org.jhotdraw.draw.figures.Figure child : getChildren()) {
        org.jhotdraw.draw.figures.BezierFigure b = (org.jhotdraw.draw.figures.BezierFigure) child;
        path.append(b.getBezierPath(), false);
      }
    }
  }

  protected void invalidatePath() {
    path = null;
  }

  @Override
  public void write(DOMOutput out) throws IOException {
    writePoints(out);
    writeAttributes(out);
  }

  protected void writePoints(DOMOutput out) {
    StringBuilder buf = new StringBuilder();
    for (Figure child : getChildren()) {
      org.jhotdraw.draw.figures.BezierFigure b = (org.jhotdraw.draw.figures.BezierFigure) child;
      buf.append(SVGUtil.toPathData(b.getBezierPath()));
    }
    out.addAttribute("d", buf.toString());
  }

  protected void writeAttributes(DOMOutput out) throws IOException {
    SVGUtil.writeAttributes(this, out);
  }

  @Override
  public void read(DOMInput in) throws IOException {
    readPoints(in);
    readAttributes(in);
    AffineTransform tx = SVGUtil.getTransform(in, "transform");
    basicTransform(tx);
  }

  protected void readPoints(DOMInput in) throws IOException {
    removeAllChildren();
    if (in.getTagName().equals("polyline")) {
      BezierPath b = new BezierPath();
      String points = in.getAttribute("points", "");

      StringTokenizer tt = new StringTokenizer(points, " ,");
      while (tt.hasMoreTokens()) {
        b.add(new BezierPath.Node(
                Double.parseDouble(tt.nextToken()),
                Double.parseDouble(tt.nextToken())
        ));
      }
      org.jhotdraw.draw.figures.BezierFigure child = new org.jhotdraw.draw.figures.BezierFigure();
      child.basicSetBezierPath(b);
      basicAdd(child);

    } else if (in.getTagName().equals("polygon")) {
      BezierPath b = new BezierPath();
      b.setClosed(true);
      String points = in.getAttribute("points", "");

      StringTokenizer tt = new StringTokenizer(points, " ,");
      while (tt.hasMoreTokens()) {
        b.add(new BezierPath.Node(
                Double.parseDouble(tt.nextToken()),
                Double.parseDouble(tt.nextToken())
        ));
      }
      org.jhotdraw.draw.figures.BezierFigure child = new org.jhotdraw.draw.figures.BezierFigure();
      child.basicSetBezierPath(b);
      basicAdd(child);

    } else {
      java.util.List<BezierPath> paths = SVGUtil.getPath(in, "d");
      for (BezierPath b : paths) {
        org.jhotdraw.draw.figures.BezierFigure child = new org.jhotdraw.draw.figures.BezierFigure();
        child.basicSetBezierPath(b);
        basicAdd(child);
      }
      if (paths.isEmpty()) {
        org.jhotdraw.draw.figures.BezierFigure child = new BezierFigure();
        basicAdd(child);
      }
    }
  }

  protected void readAttributes(DOMInput in) throws IOException {
    SVGUtil.readAttributes(this, in);
  }

  public void basicTransform(AffineTransform tx) {
    super.basicTransform(tx);
    invalidatePath();
  }

  public boolean isEmpty() {
    for (org.jhotdraw.draw.figures.Figure child : getChildren()) {
      org.jhotdraw.draw.figures.BezierFigure b = (org.jhotdraw.draw.figures.BezierFigure) child;
      if (b.getPointCount() > 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public LinkedList<org.jhotdraw.draw.handlers.Handle> createHandles(int detailLevel) {
    LinkedList<org.jhotdraw.draw.handlers.Handle> handles;
    if (detailLevel == 0) {
      handles = (LinkedList<Handle>) super.createHandles(detailLevel);
      handles.add(new RotateHandle(this));
    } else {
      handles = new LinkedList<>();
      for (org.jhotdraw.draw.figures.Figure child : getChildren()) {
        handles.addAll(child.createHandles(detailLevel));
      }
    }
    return handles;
  }

  @Override
  public Collection<Action> getActions(Point2D.Double p) {
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.svg.Labels");
    LinkedList<Action> actions = new LinkedList<>();
    actions.add(new AbstractAction(labels.getString("closePath")) {
      public void actionPerformed(ActionEvent evt) {
        for (org.jhotdraw.draw.figures.Figure child : getChildren()) {
          org.jhotdraw.draw.figures.BezierFigure b = (org.jhotdraw.draw.figures.BezierFigure) child;
          b.setClosed(true);
        }
      }
    });
    actions.add(new AbstractAction(labels.getString("openPath")) {
      public void actionPerformed(ActionEvent evt) {
        for (org.jhotdraw.draw.figures.Figure child : getChildren()) {
          org.jhotdraw.draw.figures.BezierFigure b = (org.jhotdraw.draw.figures.BezierFigure) child;
          b.setClosed(false);
        }
      }
    });
    actions.add(new AbstractAction(labels.getString("windingEvenOdd")) {
      public void actionPerformed(ActionEvent evt) {
        WINDING_RULE.set(SVGPath.this, WindingRule.EVEN_ODD);
      }
    });
    actions.add(new AbstractAction(labels.getString("windingNonZero")) {
      public void actionPerformed(ActionEvent evt) {
        WINDING_RULE.set(SVGPath.this, WindingRule.NON_ZERO);
      }
    });
    return actions;
  }
}
