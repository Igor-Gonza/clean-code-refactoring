/*
 * @(#)FloatingTextField.java  2.0  2006-01-014
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

import org.jhotdraw.draw.figures.TextFigure;
import org.jhotdraw.draw.figures.TextHolder;
import org.jhotdraw.draw.views.DrawingView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A text field overlay that is used to edit a TextFigure.
 * A FloatingTextField requires a two-step initialization:
 * In a first step the overlay is created and in a
 * second step it can be positioned.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 * @see TextFigure
 */
@SuppressWarnings("unused")
public class FloatingTextField {

  private final JTextField editWidget;
  private DrawingView view;

  public FloatingTextField() {
    editWidget = new JTextField(20);
  }

  /**
   * Creates the overlay for the given Component.
   */
  public void createOverlay(DrawingView view) {
    createOverlay(view, null);
  }

  public void requestFocus() {
    editWidget.requestFocus();
  }

  /**
   * Creates the overlay for the given Container using a
   * specific font.
   */
  public void createOverlay(DrawingView view, TextHolder figure) {
    view.getContainer().add(editWidget, 0);
    Font f = figure.getFont();
    // FIXME - Should scale with fractional value!
    f = f.deriveFont(f.getStyle(), (float) (f.getSize() * view.getScaleFactor()));
    editWidget.setFont(f);
    editWidget.setForeground(figure.getTextColor());
    editWidget.setBackground(figure.getFillColor());
    this.view = view;
  }

  public Insets getInsets() {
    return editWidget.getInsets();
  }

  /**
   * Adds an action listener
   */
  public void addActionListener(ActionListener listener) {
    editWidget.addActionListener(listener);
  }

  /**
   * Remove an action listener
   */
  public void removeActionListener(ActionListener listener) {
    editWidget.removeActionListener(listener);
  }

  /**
   * Positions the overlay.
   */
  public void setBounds(Rectangle r, String text) {
    editWidget.setText(text);
    editWidget.setBounds(r.x, r.y, r.width, r.height);
    editWidget.setVisible(true);
    editWidget.selectAll();
    editWidget.requestFocus();
  }

  /**
   * Gets the text contents of the overlay.
   */
  public String getText() {
    return editWidget.getText();
  }

  /**
   * Gets the preferred size of the overlay.
   */
  public Dimension getPreferredSize(int cols) {
    editWidget.setColumns(cols);
    return editWidget.getPreferredSize();
  }

  /**
   * Removes the overlay.
   */
  public void endOverlay() {
    view.getContainer().requestFocus();
    if (editWidget != null) {
      editWidget.setVisible(false);
      view.getContainer().remove(editWidget);

      Rectangle bounds = editWidget.getBounds();
      view.getContainer().repaint(bounds.x, bounds.y, bounds.width, bounds.height);
    }
  }
}

