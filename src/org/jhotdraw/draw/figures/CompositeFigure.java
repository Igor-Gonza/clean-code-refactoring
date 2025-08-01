/*
 * @(#)CompositeFigure.java  1.0  27. January 2006
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
import org.jhotdraw.draw.layouters.Layouter;
import org.jhotdraw.geom.Insets2DDouble;

import java.util.List;

/**
 * A CompositeFigure is composed of several child Figures.
 * A CompositeFigure can be laid out using a Layouter.
 *
 * @author Werner Randelshofer
 * @version 1.0 27. January 2006 Created.
 */
public interface CompositeFigure extends Figure {
  /**
   * The value of this attribute is a Insets2DDouble object.
   */
  AttributeKey<Insets2DDouble> LAYOUT_INSETS = new AttributeKey<>("layoutInsets", new Insets2DDouble(0, 0, 0, 0));

  /**
   * Adds a child to the figure.
   * This is a convenience method for add(getChildCount(), child);
   */
  void add(Figure child);

  /**
   * Adds a child to the figure at the specified index.
   */
  void add(int index, Figure child);

  /**
   * Adds a child to the figure without firing events.
   * This is a convenience method for basicAdd(getChildCount(), child);
   */
  void basicAdd(Figure child);

  /**
   * Adds a child to the figure at the specified index without
   * firing events.
   */
  void basicAdd(int index, Figure child);

  /**
   * Removes the specified child.
   * Returns true, if the Figure contained the removed child.
   */
  boolean remove(Figure child);

  /**
   * Removes the child at the specified index.
   * Returns the removed child figure.
   */
  Figure removeChild(int index);

  /**
   * Removes all children from the composite figure.
   */
  void removeAllChildren();

  /**
   * Removes the specified child without firing events.
   * Returns true, if the Figure contained the removed child.
   */
  boolean basicRemove(Figure child);

  /**
   * Removes the child at the specified index without firing events.
   * Returns the removed child figure.
   */
  Figure basicRemoveChild(int index);

  /**
   * Removes all children from the composite figure without firing events.
   */
  void basicRemoveAllChildren();

  /**
   * Returns an unchangeable list view on the children.
   */
  List<Figure> getChildren();

  /**
   * Returns the number of children.
   */
  int getChildCount();

  /**
   * Returns the child figure at the specified index.
   */
  Figure getChild(int index);

  /**
   * Get a Layouter object which encapsulated a layout
   * algorithm for this figure. Typically, a Layouter
   * accesses the child components of this figure and arranges
   * their graphical presentation.
   *
   * @return layout strategy used by this figure
   */
  Layouter getLayouter();

  /**
   * A layout algorithm is used to define how the child components
   * should be laid out in relation to each other. The task for
   * layouting the child components for presentation is delegated
   * to a Layouter which can be plugged in at runtime.
   */
  void layout();

  /**
   * Set a Layouter object which encapsulated a layout
   * algorithm for this figure. Typically, a Layouter
   * accesses the child components of this figure and arranges
   * their graphical presentation. It is a good idea to set
   * the Layouter in the protected initialize() method
   * so it can be recreated if a GraphicalCompositeFigure is
   * read and restored from a StorableInput stream.
   *
   * @param newValue encapsulation of a layout algorithm.
   */
  void setLayouter(Layouter newValue);
}
