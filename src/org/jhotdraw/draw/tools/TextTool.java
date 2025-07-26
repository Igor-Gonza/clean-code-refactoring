/*
 * @(#)TextTool.java  1.0  19. November 2003
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

package org.jhotdraw.draw.tools;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.FloatingTextField;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.figures.TextHolder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Tool to create new or edit existing text figures.
 * The editing behavior is implemented by overlaying the
 * Figure providing the text with a FloatingTextField.<p>
 * A tool interaction is done once a Figure that is not
 * a TextHolder is clicked.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 * @see org.jhotdraw.draw.figures.TextHolder
 * @see FloatingTextField
 */
public class TextTool extends CreationTool implements ActionListener {
  private FloatingTextField textField;
  private TextHolder typingTarget;

  /**
   * Creates a new instance.
   */
  public TextTool(org.jhotdraw.draw.figures.TextHolder prototype) {
    super(prototype);
  }

  /**
   * Creates a new instance.
   */
  public TextTool(org.jhotdraw.draw.figures.Figure prototype, Map attributes) {
    super(prototype, attributes);
  }

  public void deactivate(DrawingEditor editor) {
    endEdit();
    super.deactivate(editor);
  }

  /**
   * If the pressed figure is a TextHolder it can be edited otherwise
   * a new text figure is created.
   */
  public void mousePressed(MouseEvent e) {
    org.jhotdraw.draw.figures.TextHolder textHolder = null;
    Figure pressedFigure = getDrawing().findFigureInside(getView().viewToDrawing(new Point(e.getX(), e.getY())));
    if (pressedFigure instanceof org.jhotdraw.draw.figures.TextHolder) {
      textHolder = ((org.jhotdraw.draw.figures.TextHolder) pressedFigure).getLabelFor();
      if (!textHolder.isEditable()) textHolder = null;
    }
    if (textHolder != null) {
      beginEdit(textHolder);
      return;
    }
    if (typingTarget != null) {
      endEdit();
      fireToolDone();
    } else {
      super.mousePressed(e);
      // update view so the created figure is drawn before the floating text
      // figure is overlaid. (Note, fDamage should be null in StandardDrawingView
      // when the overlay figure is drawn because a JTextField cannot be scrolled)
      //view().checkDamage();
      textHolder = (org.jhotdraw.draw.figures.TextHolder) getCreatedFigure();
      beginEdit(textHolder);
    }
  }

  public void mouseDragged(java.awt.event.MouseEvent e) {
  }

  protected void beginEdit(org.jhotdraw.draw.figures.TextHolder textHolder) {
    if (textField == null) {
      textField = new FloatingTextField();
      textField.addActionListener(this);
    }

    if (textHolder != typingTarget && typingTarget != null) {
      endEdit();
    }
    textField.createOverlay(getView(), textHolder);
    textField.setBounds(getFieldBounds(textHolder), textHolder.getText());
    textField.requestFocus();
    typingTarget = textHolder;
  }


  private Rectangle getFieldBounds(org.jhotdraw.draw.figures.TextHolder figure) {
        /*
        Rectangle box = getView().drawingToView(figure.getBounds());
        int nChars = figure.getTextColumns();
        Dimension d = textField.getPreferredSize(nChars);
        d.width = Math.max(box.width, d.width);
        return new Rectangle(box.x - 6, box.y - 4, d.width, d.height);
         */
    Rectangle box = getView().drawingToView(figure.getBounds());
    //Dimension d = textField.getPreferredSize(3);
    //d.width = Math.max(box.width, d.width);
    Insets insets = textField.getInsets();
    return new Rectangle(box.x - insets.left, box.y - insets.top, box.width + insets.left + insets.right, box.height + insets.top + insets.bottom);
  }

  public void mouseReleased(MouseEvent evt) {
        /*
        if (createdFigure != null) {
            Rectangle bounds = createdFigure.getBounds();
            if (bounds.width == 0 && bounds.height == 0) {
                getDrawing().remove(createdFigure);
            } else {
                getView().addToSelection(createdFigure);
            }
            createdFigure = null;
            getDrawing().fireUndoableEditHappened(creationEdit);
            fireToolDone();
        }*/
  }

  protected void endEdit() {
    if (typingTarget != null) {
      //typingTarget.willChange();
      if (!textField.getText().isEmpty()) {
        typingTarget.setText(textField.getText());
        if (createdFigure != null) {
          getDrawing().fireUndoableEditHappened(creationEdit);
          createdFigure = null;
        }
      } else {
        if (createdFigure != null) {
          getDrawing().remove(getAddedFigure());
        } else {
          typingTarget.setText("");
        }
      }
      // nothing to undo
      //	            setUndoActivity(null);
      //typingTarget.changed();
      typingTarget = null;

      textField.endOverlay();
    }
    //	        view().checkDamage();
  }

  public void actionPerformed(ActionEvent event) {
    endEdit();
    fireToolDone();
  }
}
