/*
 * @(#)BoxHandleKit.java  2.0  2006-01-14
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

import org.jhotdraw.draw.edits.GeometryEdit;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.draw.locators.RelativeLocator;
import org.jhotdraw.draw.handlers.LocatorHandle;
import org.jhotdraw.draw.locators.Locator;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * A set of utility methods to create Handles for the common
 * locations on a figure's display box.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class BoxHandleKit {

  /**
   * Creates a new instance.
   */
  public BoxHandleKit() {
    // TODO document why this constructor is empty
  }

  /**
   * Creates handles for each lead of a
   * figure and adds them to the provided collection.
   */
  public static void addLeadHandles(Figure f, Collection<Handle> handles) {
    handles.add(southEast(f));
    handles.add(southWest(f));
    handles.add(northEast(f));
    handles.add(northWest(f));
  }

  /**
   * Fills the given Vector with handles at each
   * the north, south, east, and west of the figure.
   */
  public static void addEdgeHandles(Figure f, Collection<Handle> handles) {
    handles.add(south(f));
    handles.add(north(f));
    handles.add(east(f));
    handles.add(west(f));
  }

  /**
   * Fills the given Vector with handles at each
   * the north, south, east, and west of the figure.
   */
  public static void addBoxHandles(Figure f, Collection<Handle> handles) {
    addLeadHandles(f, handles);
    addEdgeHandles(f, handles);
  }

  public static Handle south(Figure owner) {
    return new SouthHandle(owner);
  }

  public static Handle southEast(Figure owner) {
    return new SouthEastHandle(owner);
  }

  public static Handle southWest(Figure owner) {
    return new SouthWestHandle(owner);
  }

  public static Handle north(Figure owner) {
    return new NorthHandle(owner);
  }

  public static Handle northEast(Figure owner) {
    return new NorthEastHandle(owner);
  }

  public static Handle northWest(Figure owner) {
    return new NorthWestHandle(owner);
  }

  public static Handle east(Figure owner) {
    return new EastHandle(owner);
  }

  public static Handle west(Figure owner) {
    return new WestHandle(owner);
  }

  private static class ResizeHandle extends LocatorHandle {
    private int dx;
    private int dy;
    Object geometry;

    ResizeHandle(Figure owner, Locator loc) {
      super(owner, loc);
    }

    public void trackStart(Point anchor, int modifiersEx) {
      geometry = getOwner().getRestoreData();
      Point location = getLocation();
      dx = -anchor.x + location.x;
      dy = -anchor.y + location.y;
    }

    public void trackStep(Point anchor, Point lead, int modifiersEx) {
      Point2D.Double p = view.viewToDrawing(new Point(lead.x + dx, lead.y + dy));
      view.getConstrainer().constrainPoint(p);

      trackStepNormalized(p);
    }

    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
      fireUndoableEditHappened(new GeometryEdit(getOwner(), geometry, getOwner().getRestoreData()));

    }

    protected void trackStepNormalized(Point2D.Double p) {
      // TODO document why this method is empty
    }

    /**
     * FIXME - Replace operation parameters by a Rectangle2D.Double.
     */
    protected void setBounds(Point2D.Double anchor, Point2D.Double lead) {
      Figure f = getOwner();
      f.willChange();
      Rectangle2D.Double oldBounds = f.getBounds();
      Rectangle2D.Double newBounds = new Rectangle2D.Double(Math.min(anchor.x, lead.x), Math.min(anchor.y, lead.y), Math.abs(anchor.x - lead.x), Math.abs(anchor.y - lead.y));
      double sx = newBounds.width / oldBounds.width;
      double sy = newBounds.height / oldBounds.height;

      AffineTransform tx = new AffineTransform();
      tx.translate(-oldBounds.x, -oldBounds.y);
      if (!Double.isNaN(sx) && !Double.isNaN(sy) && (sx != 1d || sy != 1d) && sx >= 0.0001 && sy >= 0.0001) {
        f.basicTransform(tx);
        tx.setToIdentity();
        tx.scale(sx, sy);
        f.basicTransform(tx);
        tx.setToIdentity();
      }
      tx.translate(newBounds.x, newBounds.y);
      f.basicTransform(tx);
      f.changed();
    }
  }

  private static class NorthEastHandle extends ResizeHandle {
    NorthEastHandle(Figure owner) {
      super(owner, org.jhotdraw.draw.locators.RelativeLocator.northEast());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(r.x, Math.min(r.y + r.height - 1, p.y)), new Point2D.Double(Math.max(r.x, p.x), r.y + r.height));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
    }
  }

  private static class EastHandle extends ResizeHandle {
    EastHandle(Figure owner) {
      super(owner, RelativeLocator.east());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(Math.max(r.x + 1, p.x), r.y + r.height));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    }
  }

  private static class NorthHandle extends ResizeHandle {
    NorthHandle(Figure owner) {
      super(owner, RelativeLocator.north());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(r.x, Math.min(r.y + r.height - 1, p.y)), new Point2D.Double(r.x + r.width, r.y + r.height));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    }
  }

  private static class NorthWestHandle extends ResizeHandle {
    NorthWestHandle(Figure owner) {
      super(owner, RelativeLocator.northWest());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(Math.min(r.x + r.width - 1, p.x), Math.min(r.y + r.height - 1, p.y)), new Point2D.Double(r.x + r.width, r.y + r.height));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
    }
  }

  private static class SouthEastHandle extends ResizeHandle {
    SouthEastHandle(Figure owner) {
      super(owner, RelativeLocator.southEast());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(Math.max(r.x + 1, p.x), Math.max(r.y + 1, p.y)));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
    }
  }

  private static class SouthHandle extends ResizeHandle {
    SouthHandle(Figure owner) {
      super(owner, RelativeLocator.south());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, Math.max(r.y + 1, p.y)));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
    }
  }

  private static class SouthWestHandle extends ResizeHandle {
    SouthWestHandle(Figure owner) {
      super(owner, RelativeLocator.southWest());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(Math.min(r.x + r.width - 1, p.x), r.y), new Point2D.Double(r.x + r.width, Math.max(r.y + 1, p.y)));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
    }
  }

  private static class WestHandle extends ResizeHandle {
    WestHandle(Figure owner) {
      super(owner, RelativeLocator.west());
    }

    @Override
    protected void trackStepNormalized(Point2D.Double p) {
      Rectangle2D.Double r = getOwner().getBounds();
      setBounds(new Point2D.Double(Math.min(r.x + r.width - 1, p.x), r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
    }
  }
}