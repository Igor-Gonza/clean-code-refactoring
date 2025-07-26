/*
 * @(#)DefaultDrawing.java  2.0  2006-01-14
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

package org.jhotdraw.draw.drawings;

import org.jhotdraw.draw.FigureLayerComparator;
import org.jhotdraw.draw.listeners.FigureListener;
import org.jhotdraw.draw.events.FigureEvent;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.util.ReversedList;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * DefaultDrawing.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class DefaultDrawing extends AbstractDrawing implements FigureListener, UndoableEditListener {
  private ArrayList<org.jhotdraw.draw.figures.Figure> figures = new ArrayList<>();
  private boolean needsSorting = false;

  /**
   * Creates a new instance.
   */
  public DefaultDrawing() {
  }

  protected int indexOf(org.jhotdraw.draw.figures.Figure figure) {
    return figures.indexOf(figure);
  }

  public void basicAdd(int index, org.jhotdraw.draw.figures.Figure figure) {
    figures.add(index, figure);
    figure.addFigureListener(this);
    figure.addUndoableEditListener(this);
    invalidateSortOrder();
  }

  public void basicRemove(org.jhotdraw.draw.figures.Figure figure) {
    figures.remove(figure);
    figure.removeFigureListener(this);
    figure.removeUndoableEditListener(this);
    invalidateSortOrder();
  }


  public void draw(Graphics2D g) {
    synchronized (getLock()) {
      ensureSorted();
      ArrayList<org.jhotdraw.draw.figures.Figure> toDraw = new ArrayList<>(figures.size());
      Rectangle clipRect = g.getClipBounds();
      for (org.jhotdraw.draw.figures.Figure f : figures) {
        if (f.getDrawBounds().intersects(clipRect)) {
          toDraw.add(f);
        }
      }
      draw(g, toDraw);
    }
  }

  public void draw(Graphics2D g, Collection<org.jhotdraw.draw.figures.Figure> figures) {
    for (org.jhotdraw.draw.figures.Figure f : figures) {
      if (f.isVisible()) {
        f.draw(g);
      }
    }
  }

  public Collection<org.jhotdraw.draw.figures.Figure> sort(Collection<Figure> c) {
    HashSet<org.jhotdraw.draw.figures.Figure> unsorted = new HashSet<>(c);
    ArrayList<org.jhotdraw.draw.figures.Figure> sorted = new ArrayList<>(c.size());
    for (org.jhotdraw.draw.figures.Figure f : figures) {
      if (unsorted.contains(f)) {
        sorted.add(f);
        unsorted.remove(f);
      }
    }
    for (org.jhotdraw.draw.figures.Figure f : c) {
      if (unsorted.contains(f)) {
        sorted.add(f);
        unsorted.remove(f);
      }
    }
    return sorted;
  }

  public void figureAreaInvalidated(FigureEvent e) {
    fireAreaInvalidated(e.getInvalidatedArea());
  }

  public void figureChanged(org.jhotdraw.draw.events.FigureEvent e) {
    invalidateSortOrder();
    fireAreaInvalidated(e.getInvalidatedArea());
  }

  public void figureAdded(org.jhotdraw.draw.events.FigureEvent e) {
  }

  public void figureRemoved(org.jhotdraw.draw.events.FigureEvent e) {
  }

  public void figureRequestRemove(org.jhotdraw.draw.events.FigureEvent e) {
    remove(e.getFigure());
  }

  public org.jhotdraw.draw.figures.Figure findFigure(Point2D.Double p) {
    for (org.jhotdraw.draw.figures.Figure f : getFiguresFrontToBack()) {
      if (f.isVisible() && f.contains(p)) {
        return f;
      }
    }
    return null;
  }

  public org.jhotdraw.draw.figures.Figure findFigureExcept(Point2D.Double p, org.jhotdraw.draw.figures.Figure ignore) {
    for (org.jhotdraw.draw.figures.Figure f : getFiguresFrontToBack()) {
      if (f != ignore && f.isVisible() && f.contains(p)) {
        return f;
      }
    }
    return null;
  }

  public org.jhotdraw.draw.figures.Figure findFigureExcept(Point2D.Double p, Collection<org.jhotdraw.draw.figures.Figure> ignore) {
    for (org.jhotdraw.draw.figures.Figure f : getFiguresFrontToBack()) {
      if (!ignore.contains(f) && f.isVisible() && f.contains(p)) {
        return f;
      }
    }
    return null;
  }

  public Collection<org.jhotdraw.draw.figures.Figure> findFigures(Rectangle2D.Double bounds) {
    ArrayList<org.jhotdraw.draw.figures.Figure> intersection = new ArrayList<>();
    for (org.jhotdraw.draw.figures.Figure f : figures) {
      if (f.isVisible() && f.getBounds().intersects(bounds)) {
        intersection.add(f);
      }
    }
    return intersection;
  }

  public Collection<org.jhotdraw.draw.figures.Figure> findFiguresWithin(Rectangle2D.Double bounds) {
    ArrayList<org.jhotdraw.draw.figures.Figure> contained = new ArrayList<>();
    for (org.jhotdraw.draw.figures.Figure f : figures) {
      if (f.isVisible() && bounds.contains(f.getBounds())) {
        contained.add(f);
      }
    }
    return contained;
  }

  public Collection<org.jhotdraw.draw.figures.Figure> getFigures() {
    return Collections.unmodifiableCollection(figures);
  }

  public org.jhotdraw.draw.figures.Figure findFigureInside(Point2D.Double p) {
    org.jhotdraw.draw.figures.Figure f = findFigure(p);
    return (f == null) ? null : f.findFigureInside(p);
  }

  /**
   * Returns an iterator to iterate in
   * Z-order front to back over the figures.
   */
  public java.util.List<org.jhotdraw.draw.figures.Figure> getFiguresFrontToBack() {
    ensureSorted();
    return new ReversedList<>(figures);
  }

  public void bringToFront(org.jhotdraw.draw.figures.Figure figure) {
    if (figures.remove(figure)) {
      figures.add(figure);
      invalidateSortOrder();
      fireAreaInvalidated(figure.getDrawBounds());
    }
  }

  public void sendToBack(org.jhotdraw.draw.figures.Figure figure) {
    if (figures.remove(figure)) {
      figures.add(0, figure);
      invalidateSortOrder();
      fireAreaInvalidated(figure.getDrawBounds());
    }
  }

  /**
   * We propagate all edit events from our figures to
   * undoable edit listeners, which have registered with us.
   */
  public void undoableEditHappened(UndoableEditEvent e) {
    fireUndoableEditHappened(e.getEdit());
  }

  public void figureAttributeChanged(org.jhotdraw.draw.events.FigureEvent e) {
  }

  public boolean contains(org.jhotdraw.draw.figures.Figure f) {
    return figures.contains(f);
  }

  /**
   * Invalidates the sort order.
   */
  private void invalidateSortOrder() {
    needsSorting = true;
  }

  /**
   * Ensures that the figures are sorted in z-order sequence from back to
   * front.
   */
  private void ensureSorted() {
    if (needsSorting) {
      figures.sort(FigureLayerComparator.INSTANCE);
      needsSorting = false;
    }
  }
}
