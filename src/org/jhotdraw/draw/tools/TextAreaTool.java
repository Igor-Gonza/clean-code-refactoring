/*
 * @(#)TextTool.java  2.0  2006-01-14
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

import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.FloatingTextArea;
import org.jhotdraw.draw.figures.CompositeFigure;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.figures.TextHolder;
import org.jhotdraw.geom.Insets2DDouble;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Tool to create new or edit existing text figures.
 * The editing behavior is implemented by overlaying the
 * Figure providing the text with a FloatingTextArea.<p>
 * A tool interaction is done once a Figure that is not
 * a TextHolder is clicked.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 * @see org.jhotdraw.draw.figures.TextHolder
 * @see FloatingTextArea
 */
public class TextAreaTool extends CreationTool implements ActionListener {
  private FloatingTextArea textArea;
  private org.jhotdraw.draw.figures.TextHolder typingTarget;

  /**
   * Creates a new instance.
   */
  public TextAreaTool(org.jhotdraw.draw.figures.TextHolder prototype) {
    super(prototype);
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
      textHolder = (org.jhotdraw.draw.figures.TextHolder) pressedFigure;
      if (!textHolder.isEditable()) textHolder = null;
    }

    if (textHolder != null) {
      createdFigure = null;
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
            /*
            textHolder = (TextHolder)getCreatedFigure();
            beginEdit(textHolder);*/
    }
  }
    /*
    public void mouseDragged(java.awt.event.MouseEvent e) {
    }
     */

  protected void beginEdit(TextHolder textHolder) {
    if (textArea == null) {
      textArea = new FloatingTextArea();

      //textArea.addActionListener(this);
    }

    if (textHolder != typingTarget && typingTarget != null) {
      endEdit();
    }
    textArea.createOverlay(getView(), textHolder);
    textArea.setBounds(getFieldBounds(textHolder), textHolder.getText());
    textArea.requestFocus();
    typingTarget = textHolder;
  }

  private Rectangle2D.Double getFieldBounds(org.jhotdraw.draw.figures.TextHolder figure) {
    Rectangle2D.Double r = figure.getBounds();
    Insets2DDouble insets = figure.getInsets();
    r = new Rectangle2D.Double(r.x + insets.left, r.y + insets.top, r.width - insets.left - insets.right, r.height - insets.top - insets.bottom);
    // FIXME - Find a way to determine the parameters for grow.
    //r.grow(1,2);
    //r.width += 16;
    r.x -= 1;
    r.y -= 2;
    r.width += 18;
    r.height += 4;
    return r;
  }

  public void mouseReleased(MouseEvent evt) {
    if (createdFigure != null) {
      org.jhotdraw.draw.figures.TextHolder textHolder = (org.jhotdraw.draw.figures.TextHolder) createdFigure;
      Rectangle2D.Double bounds = createdFigure.getBounds();
      if (bounds.width == 0 && bounds.height == 0) {
        getDrawing().remove(createdFigure);
      } else {
        if (bounds.width < 5 && bounds.height < 5) {
          createdFigure.willChange();
          createdFigure.basicSetBounds(new Point2D.Double(bounds.x, bounds.y), new Point2D.Double(bounds.x + 100, bounds.y + 100));
          createdFigure.changed();
        }
        getView().addToSelection(createdFigure);
      }
      if (createdFigure instanceof org.jhotdraw.draw.figures.CompositeFigure) {
        ((CompositeFigure) createdFigure).layout();
      }
      createdFigure = null;
      getDrawing().fireUndoableEditHappened(creationEdit);
      beginEdit(textHolder);
    }
  }

  protected void endEdit() {
    if (typingTarget != null) {
      if (!textArea.getText().isEmpty()) {
        typingTarget.setText(textArea.getText());
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
      typingTarget = null;

      textArea.endOverlay();
    }
    //	        view().checkDamage();
  }

  public void actionPerformed(ActionEvent event) {
    endEdit();
    fireToolDone();
  }
}
