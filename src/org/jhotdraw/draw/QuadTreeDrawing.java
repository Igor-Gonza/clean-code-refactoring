/*
 * @(#)QuadTreeDrawing.java  2.0  2006-01-14
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

package org.jhotdraw.draw;

import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.geom.QuadTree2DDouble;
import org.jhotdraw.util.ReversedList;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * QuadTreeDrawing uses a QuadTree2DDouble to improve responsiveness of drawings which
 * contain many figures.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class QuadTreeDrawing extends AbstractDrawing implements FigureListener, UndoableEditListener {
  private ArrayList<org.jhotdraw.draw.figures.Figure> figures = new ArrayList<>();
  private QuadTree2DDouble<org.jhotdraw.draw.figures.Figure> quadTree = new QuadTree2DDouble<>();
  private boolean needsSorting = false;

  /**
   * Creates a new instance.
   */
  public QuadTreeDrawing() {
  }

  protected int indexOf(org.jhotdraw.draw.figures.Figure figure) {
    return figures.indexOf(figure);
  }

  public void basicAdd(int index, org.jhotdraw.draw.figures.Figure figure) {
    figures.add(index, figure);
    quadTree.add(figure, figure.getDrawBounds());
    figure.addFigureListener(this);
    figure.addUndoableEditListener(this);
    needsSorting = true;
  }

  public void basicRemove(org.jhotdraw.draw.figures.Figure figure) {
    figures.remove(figure);
    quadTree.remove(figure);
    figure.removeFigureListener(this);
    figure.removeUndoableEditListener(this);
    needsSorting = true;
  }

  public void draw(Graphics2D g) {
    Collection<org.jhotdraw.draw.figures.Figure> c = quadTree.findIntersects(g.getClipBounds().getBounds2D());
    Collection<org.jhotdraw.draw.figures.Figure> toDraw = sort(c);
    draw(g, toDraw);
  }

  /**
   * Implementation note: Sorting can not be done for orphaned figures.
   */
  public Collection<org.jhotdraw.draw.figures.Figure> sort(Collection<org.jhotdraw.draw.figures.Figure> c) {
    ensureSorted();
    ArrayList<org.jhotdraw.draw.figures.Figure> sorted = new ArrayList<>(c.size());
    for (org.jhotdraw.draw.figures.Figure f : figures) {
      if (c.contains(f)) {
        sorted.add(f);
      }
    }
    return sorted;
  }

  public void draw(Graphics2D g, Collection<org.jhotdraw.draw.figures.Figure> c) {
    for (org.jhotdraw.draw.figures.Figure f : c) {
      f.draw(g);
    }
  }


  public void figureAreaInvalidated(FigureEvent e) {
    fireAreaInvalidated(e.getInvalidatedArea());
  }

  public void figureChanged(FigureEvent e) {
    quadTree.remove(e.getFigure());
    quadTree.add(e.getFigure(), e.getFigure().getDrawBounds());
    needsSorting = true;
    fireAreaInvalidated(e.getInvalidatedArea());
  }

  public void figureAdded(FigureEvent e) {
  }

  public void figureRemoved(FigureEvent e) {
  }

  public void figureRequestRemove(FigureEvent e) {
    remove(e.getFigure());
  }

  public Collection<org.jhotdraw.draw.figures.Figure> getFigures(Rectangle2D.Double bounds) {
    return quadTree.findInside(bounds);
  }

  public Collection<org.jhotdraw.draw.figures.Figure> getFigures() {
    return Collections.unmodifiableCollection(figures);
  }

  public org.jhotdraw.draw.figures.Figure findFigureInside(Point2D.Double p) {
    Collection<org.jhotdraw.draw.figures.Figure> c = quadTree.findContains(p);
    for (org.jhotdraw.draw.figures.Figure f : getFiguresFrontToBack()) {
      if (c.contains(f) && f.contains(p)) {
        return f.findFigureInside(p);
      }
    }
    return null;

  }

  /**
   * Returns an iterator to iterate in
   * Z-order front to back over the figures.
   */
  public java.util.List<org.jhotdraw.draw.figures.Figure> getFiguresFrontToBack() {
    ensureSorted();
    return new ReversedList<>(figures);
  }

  public org.jhotdraw.draw.figures.Figure findFigure(Point2D.Double p) {
    Collection<org.jhotdraw.draw.figures.Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1: {
        org.jhotdraw.draw.figures.Figure f = c.iterator().next();
        return (f.contains(p)) ? f : null;
      }
      default: {
        for (org.jhotdraw.draw.figures.Figure f : getFiguresFrontToBack()) {
          if (c.contains(f) && f.contains(p)) return f;
        }
        return null;
      }
    }
  }

  public org.jhotdraw.draw.figures.Figure findFigureExcept(Point2D.Double p, org.jhotdraw.draw.figures.Figure ignore) {
    Collection<org.jhotdraw.draw.figures.Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0: {
        return null;
      }
      case 1: {
        org.jhotdraw.draw.figures.Figure f = c.iterator().next();
        return (f == ignore || !f.contains(p)) ? null : f;
      }
      default: {
        for (org.jhotdraw.draw.figures.Figure f : getFiguresFrontToBack()) {
          if (f != ignore && f.contains(p)) return f;
        }
        return null;
      }
    }
  }

  public org.jhotdraw.draw.figures.Figure findFigureExcept(Point2D.Double p, Collection ignore) {
    Collection<org.jhotdraw.draw.figures.Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0: {
        return null;
      }
      case 1: {
        org.jhotdraw.draw.figures.Figure f = c.iterator().next();
        return (!ignore.contains(f) || !f.contains(p)) ? null : f;
      }
      default: {
        for (org.jhotdraw.draw.figures.Figure f : getFiguresFrontToBack()) {
          if (!ignore.contains(f) && f.contains(p)) return f;
        }
        return null;
      }
    }
  }

  public Collection<Figure> findFigures(Rectangle2D.Double r) {
    Collection<org.jhotdraw.draw.figures.Figure> c = quadTree.findIntersects(r);
    switch (c.size()) {
      case 0:
        // fall through
      case 1:
        return c;
      default:
        return sort(c);
    }
  }

  public Collection<org.jhotdraw.draw.figures.Figure> findFiguresWithin(Rectangle2D.Double r) {
    Collection<org.jhotdraw.draw.figures.Figure> c = findFigures(r);
    ArrayList<org.jhotdraw.draw.figures.Figure> result = new ArrayList<>(c.size());
    for (org.jhotdraw.draw.figures.Figure f : c) {
      if (r.contains(f.getBounds())) {
        result.add(f);
      }
    }
    return result;
  }

  public void bringToFront(org.jhotdraw.draw.figures.Figure figure) {
    if (figures.remove(figure)) {
      figures.add(figure);
      needsSorting = true;
      fireAreaInvalidated(figure.getDrawBounds());
    }
  }

  public void sendToBack(org.jhotdraw.draw.figures.Figure figure) {
    if (figures.remove(figure)) {
      figures.add(0, figure);
      needsSorting = true;
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

  public void figureAttributeChanged(FigureEvent e) {
  }

  public boolean contains(org.jhotdraw.draw.figures.Figure f) {
    return figures.contains(f);
  }

  /**
   * Ensures that the figures are sorted in z-order sequence.
   */
  private void ensureSorted() {
    if (needsSorting) {
      figures.sort(FigureLayerComparator.INSTANCE);
      needsSorting = false;
    }
  }
}
