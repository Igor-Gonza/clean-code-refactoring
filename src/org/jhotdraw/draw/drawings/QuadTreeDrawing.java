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

package org.jhotdraw.draw.drawings;

import org.jhotdraw.draw.FigureLayerComparator;
import org.jhotdraw.draw.events.FigureEvent;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.listeners.FigureListener;
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
import java.util.List;

/**
 * QuadTreeDrawing uses a QuadTree2DDouble to improve responsiveness of drawings which
 * contain many figures.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class QuadTreeDrawing extends AbstractDrawing implements FigureListener, UndoableEditListener {
  private final ArrayList<Figure> figures = new ArrayList<>();
  private final QuadTree2DDouble<Figure> quadTree = new QuadTree2DDouble<>();
  private boolean needsSorting = false;

  /**
   * Creates a new instance.
   */
  public QuadTreeDrawing() {
    // TODO document why this constructor is empty
  }

  protected int indexOf(Figure figure) {
    return figures.indexOf(figure);
  }

  public void basicAdd(int index, Figure figure) {
    figures.add(index, figure);
    quadTree.add(figure, figure.getDrawBounds());
    figure.addFigureListener(this);
    figure.addUndoableEditListener(this);
    needsSorting = true;
  }

  public void basicRemove(Figure figure) {
    figures.remove(figure);
    quadTree.remove(figure);
    figure.removeFigureListener(this);
    figure.removeUndoableEditListener(this);
    needsSorting = true;
  }

  public void draw(Graphics2D g) {
    Collection<Figure> c = quadTree.findIntersects(g.getClipBounds().getBounds2D());
    Collection<Figure> toDraw = sort(c);
    draw(g, (ArrayList<Figure>) toDraw);
  }

  /**
   * Implementation note: Sorting can not be done for orphaned figures.
   */
  public Collection<Figure> sort(Collection<Figure> c) {
    ensureSorted();
    ArrayList<Figure> sorted = new ArrayList<>(c.size());
    for (Figure f : figures) {
      if (c.contains(f)) {
        sorted.add(f);
      }
    }
    return sorted;
  }

  public void draw(Graphics2D g, ArrayList<Figure> c) {
    for (Figure f : c) {
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
    // TODO document why this method is empty
  }

  public void figureRemoved(FigureEvent e) {
    // TODO document why this method is empty
  }

  public void figureRequestRemove(FigureEvent e) {
    remove(e.getFigure());
  }

  public Collection<Figure> getFigures(Rectangle2D.Double bounds) {
    return quadTree.findInside(bounds);
  }

  public Collection<Figure> getFigures() {
    return Collections.unmodifiableCollection(figures);
  }

  public Figure findFigureInside(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    for (Figure f : getFiguresFrontToBack()) {
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
  public List<Figure> getFiguresFrontToBack() {
    ensureSorted();
    return new ReversedList<>(figures);
  }

  public Figure findFigure(Point2D.Double p) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0:
        return null;
      case 1: {
        Figure f = c.iterator().next();
        return (f.contains(p)) ? f : null;
      }
      default: {
        for (Figure f : getFiguresFrontToBack()) {
          if (c.contains(f) && f.contains(p)) return f;
        }
        return null;
      }
    }
  }

  public Figure findFigureExcept(Point2D.Double p, Figure ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0: {
        return null;
      }
      case 1: {
        Figure f = c.iterator().next();
        return (f == ignore || !f.contains(p)) ? null : f;
      }
      default: {
        for (Figure f : getFiguresFrontToBack()) {
          if (f != ignore && f.contains(p)) return f;
        }
        return null;
      }
    }
  }

  public Figure findFigureExcept(Point2D.Double p, Collection<Figure> ignore) {
    Collection<Figure> c = quadTree.findContains(p);
    switch (c.size()) {
      case 0: {
        return null;
      }
      case 1: {
        Figure f = c.iterator().next();
        return (!ignore.contains(f) || !f.contains(p)) ? null : f;
      }
      default: {
        for (Figure f : getFiguresFrontToBack()) {
          if (!ignore.contains(f) && f.contains(p)) return f;
        }
        return null;
      }
    }
  }

  public Collection<Figure> findFigures(Rectangle2D.Double r) {
    Collection<Figure> c = quadTree.findIntersects(r);
    switch (c.size()) {
      case 0:
        // fall through
      case 1:
        return c;
      default:
        return sort(c);
    }
  }

  public Collection<Figure> findFiguresWithin(Rectangle2D.Double r) {
    Collection<Figure> c = findFigures(r);
    ArrayList<Figure> result = new ArrayList<>(c.size());
    for (Figure f : c) {
      if (r.contains(f.getBounds())) {
        result.add(f);
      }
    }
    return result;
  }

  public void bringToFront(Figure figure) {
    if (figures.remove(figure)) {
      figures.add(figure);
      needsSorting = true;
      fireAreaInvalidated(figure.getDrawBounds());
    }
  }

  public void sendToBack(Figure figure) {
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
    // TODO document why this method is empty
  }

  public boolean contains(Figure f) {
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
