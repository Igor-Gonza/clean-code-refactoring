/*
 * @(#)VerticalGridLayout.java  1.0  27. November 2003
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

package org.jhotdraw.draw.action;

import java.awt.*;

/**
 * VerticalGridLayout.
 * <p>
 * A grid layout which lays out columns first.
 *
 * @author Werner Randelshofer
 * @version 1.0 27. November 2003  Created.
 */
@SuppressWarnings("unused")
public class VerticalGridLayout implements LayoutManager, java.io.Serializable {

  int hgap;
  int vgap;
  int rows;
  int cols;
  boolean isVertical;

  /**
   * Creates a grid layout with a default of one column per component,
   * in a single row.
   */
  public VerticalGridLayout() {
    this(1, 0, 0, 0);
  }

  /**
   * Creates a grid layout with the specified number of rows and
   * columns. All components in the layout are given equal size.
   * <p>
   * One, but not both, of <code>rows</code> and <code>cols</code> can
   * be zero, which means that any number of objects can be placed in a
   * row or in a column.
   *
   * @param rows the rows, with the value zero meaning
   *             any number of rows.
   * @param cols the columns, with the value zero meaning
   *             any number of columns.
   */
  public VerticalGridLayout(int rows, int cols) {
    this(rows, cols, 0, 0);
  }

  public VerticalGridLayout(int rows, int cols, boolean isVertical) {
    this(rows, cols, 0, 0, isVertical);
  }

  public VerticalGridLayout(int rows, int cols, int hgap, int vgap) {
    this(rows, cols, hgap, vgap, true);
  }

  /**
   * Creates a grid layout with the specified number of rows and
   * columns. All components in the layout are given equal size.
   * <p>
   * In addition, the horizontal and vertical gaps are set to the
   * specified values. Horizontal gaps are placed at the left and
   * right edges, and between each of the columns. Vertical gaps are
   * placed at the top and bottom edges, and between each of the rows.
   * <p>
   * One, but not both, of <code>rows</code> and <code>cols</code> can
   * be zero, which means that any number of objects can be placed in a
   * row or in a column.
   * <p>
   * All <code>VerticalGridLayout</code> constructors defer to this one.
   *
   * @param rows the rows, with the value zero meaning
   *             any number of rows
   * @param cols the columns, with the value zero meaning
   *             any number of columns
   * @param hgap the horizontal gap
   * @param vgap the vertical gap
   * @throws IllegalArgumentException if the value of both
   *                                  <code>rows</code> and <code>cols</code> is
   *                                  set to zero
   */
  public VerticalGridLayout(int rows, int cols, int hgap, int vgap, boolean isVertical) {
    if ((rows == 0) && (cols == 0)) {
      throw new IllegalArgumentException("rows and cols cannot both be zero");
    }
    this.rows = rows;
    this.cols = cols;
    this.hgap = hgap;
    this.vgap = vgap;
    this.isVertical = isVertical;
  }

  /**
   * Gets the number of rows in this layout.
   *
   * @return the number of rows in this layout
   * @since JDK1.1
   */
  public int getRows() {
    return rows;
  }

  /**
   * Sets the number of rows in this layout to the specified value.
   *
   * @param rows the number of rows in this layout
   * @throws IllegalArgumentException if the value of both
   *                                  <code>rows</code> and <code>cols</code> is set to zero
   * @since JDK1.1
   */
  public void setRows(int rows) {
    if ((rows == 0) && (this.cols == 0)) {
      throw new IllegalArgumentException("rows and cols cannot both be zero");
    }
    this.rows = rows;
  }

  public void setVertical(boolean b) {
    isVertical = b;
  }

  public boolean isVertical() {
    return isVertical;
  }

  /**
   * Gets the number of columns in this layout.
   *
   * @return the number of columns in this layout
   * @since JDK1.1
   */
  public int getColumns() {
    return cols;
  }

  /**
   * Sets the number of columns in this layout to the specified value.
   * Setting the number of columns has no effect on the layout
   * if the number of rows specified by a constructor or by
   * the <tt>setRows</tt> method is non-zero. In that case, the number
   * of columns displayed in the layout is determined by the total
   * number of components and the number of rows specified.
   *
   * @param cols the number of columns in this layout
   * @throws IllegalArgumentException if the value of both
   *                                  <code>rows</code> and <code>cols</code> is set to zero
   * @since JDK1.1
   */
  public void setColumns(int cols) {
    if ((cols == 0) && (this.rows == 0)) {
      throw new IllegalArgumentException("rows and cols cannot both be zero");
    }
    this.cols = cols;
  }

  /**
   * Gets the horizontal gap between components.
   *
   * @return the horizontal gap between components
   * @since JDK1.1
   */
  public int getHgap() {
    return hgap;
  }

  /**
   * Sets the horizontal gap between components to the specified value.
   *
   * @param hgap the horizontal gap between components
   * @since JDK1.1
   */
  public void setHap(int hgap) {
    this.hgap = hgap;
  }

  /**
   * Gets the vertical gap between components.
   *
   * @return the vertical gap between components
   * @since JDK1.1
   */
  public int getVgap() {
    return vgap;
  }

  /**
   * Sets the vertical gap between components to the specified value.
   *
   * @param vgap the vertical gap between components
   * @since JDK1.1
   */
  public void setVgap(int vgap) {
    this.vgap = vgap;
  }

  /**
   * Adds the specified component with the specified name to the layout.
   *
   * @param name the name of the component
   * @param comp the component to be added
   */
  public void addLayoutComponent(String name, Component comp) {
  }

  /**
   * Removes the specified component from the layout.
   *
   * @param comp the component to be removed
   */
  public void removeLayoutComponent(Component comp) {
  }

  /**
   * Determines the preferred size of the container argument using
   * this grid layout.
   * <p>
   * The preferred width of a grid layout is the largest preferred
   * width any component in the container times the number of
   * columns, plus the horizontal padding times the number of columns
   * plus one, plus the left and right insets of the target container.
   * <p>
   * The preferred height of a grid layout is the largest preferred
   * height of component in the container times the number of
   * rows, plus the vertical padding times the number of rows plus one,
   * plus the top and bottom insets of the target container.
   *
   * @param parent the container in which to do the layout
   * @return the preferred dimensions to lay out the
   * subcomponents of the specified container
   */
  public Dimension preferredLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {
      Insets insets = parent.getInsets();
      int nComponents = parent.getComponentCount();
      int nRows = rows;
      int nCols = cols;

      if (nRows > 0) {
        nCols = (nComponents + nRows - 1) / nRows;
      } else {
        nRows = (nComponents + nCols - 1) / nCols;
      }
      int w = 0;
      int h = 0;
      for (int i = 0; i < nComponents; i++) {
        Component comp = parent.getComponent(i);
        Dimension d = comp.getPreferredSize();
        if (w < d.width) {
          w = d.width;
        }
        if (h < d.height) {
          h = d.height;
        }
      }
      return new Dimension(insets.left + insets.right + nCols * w + (nCols - 1) * hgap, insets.top + insets.bottom + nRows * h + (nRows - 1) * vgap);
    }
  }

  /**
   * Determines the minimum size of the container argument using this
   * grid layout.
   * <p>
   * The minimum width of a grid layout is the largest minimum width
   * of any component in the container times the number of columns,
   * plus the horizontal padding times the number of columns plus one,
   * plus the left and right insets of the target container.
   * <p>
   * The minimum height of a grid layout is the largest minimum height
   * of any component in the container times the number of rows,
   * plus the vertical padding times the number of rows plus one, plus
   * the top and bottom insets of the target container.
   *
   * @param parent the container in which to do the layout
   * @return the minimum dimensions needed to lay out the
   * subcomponents of the specified container
   */
  public Dimension minimumLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {
      Insets insets = parent.getInsets();
      int nComponents = parent.getComponentCount();
      int nRows = rows;
      int nCols = cols;

      if (nRows > 0) {
        nCols = (nComponents + nRows - 1) / nRows;
      } else {
        nRows = (nComponents + nCols - 1) / nCols;
      }
      int w = 0;
      int h = 0;
      for (int i = 0; i < nComponents; i++) {
        Component comp = parent.getComponent(i);
        Dimension d = comp.getMinimumSize();
        if (w < d.width) {
          w = d.width;
        }
        if (h < d.height) {
          h = d.height;
        }
      }
      return new Dimension(insets.left + insets.right + nCols * w + (nCols - 1) * hgap, insets.top + insets.bottom + nRows * h + (nRows - 1) * vgap);
    }
  }

  /**
   * Lays out the specified container using this layout.
   * <p>
   * This method reshapes the components in the specified target
   * container in order to satisfy the constraints of the
   * <code>VerticalGridLayout</code> object.
   * <p>
   * The grid layout manager determines the size of individual
   * components by dividing the free space in the container into
   * equal-sized portions according to the number of rows and columns
   * in the layout. The container's free space equals the container's
   * size minus any insets and any specified horizontal or vertical
   * gap. All components in a grid layout are given the same size.
   *
   * @param parent the container in which to do the layout
   * @see java.awt.Container
   * @see java.awt.Container#doLayout
   */
  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {
      Insets insets = parent.getInsets();
      int nComponents = parent.getComponentCount();
      int nRows = rows;
      int nCols = cols;
      boolean ltr = parent.getComponentOrientation().isLeftToRight();

      if (nComponents == 0) {
        return;
      }
      if (nRows > 0) {
        nCols = (nComponents + nRows - 1) / nRows;
      } else {
        nRows = (nComponents + nCols - 1) / nCols;
      }
      int w = parent.getWidth() - (insets.left + insets.right);
      int h = parent.getHeight() - (insets.top + insets.bottom);
      w = (w - (nCols - 1) * hgap) / nCols;
      h = (h - (nRows - 1) * vgap) / nRows;

      int i;
      if (ltr) {
        for (int c = 0, x = insets.left; c < nCols; c++, x += w + hgap) {
          for (int r = 0, y = insets.top; r < nRows; r++, y += h + vgap) {
            if (isVertical) {
              i = r + c * nRows;
            } else {
              i = r * nCols + c;
            }
            if (i < nComponents) {
              parent.getComponent(i).setBounds(x, y, w, h);
            }
          }
        }
      } else {
        for (int c = 0, x = parent.getWidth() - insets.right - w; c < nCols; c++, x -= w + hgap) {
          for (int r = 0, y = insets.top; r < nRows; r++, y += h + vgap) {
            if (isVertical) {
              i = r + c * nRows;
            } else {
              i = r * nCols + c;
            }
            if (i < nComponents) {
              parent.getComponent(i).setBounds(x, y, w, h);
            }
          }
        }
      }
    }
  }

  /**
   * Returns the string representation of this grid layout's values.
   *
   * @return a string representation of this grid layout
   */
  public String toString() {
    return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + ",rows=" + rows + ",cols=" + cols + "]";
  }
}
