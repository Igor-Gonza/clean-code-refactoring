/*
 * @(#)AttributeToggler.java  4.0  2006-06-07
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

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

/**
 * AttributeToggler toggles an attribute of the selected figures between two
 * different values.
 * If the name of a compatible JTextComponent action is specified, the toggler
 * checks if the current permanent focus owner is a JTextComponent, and if it is,
 * it will apply the text action to the JTextComponent.
 *
 * @author Werner Randelshofer
 * @version 4.0 2006-06-07 Reworked.
 * <br>3.0 2006-02-27 Support for compatible text action added.
 * <br>2.0 2006-02-27 Toggle attributes regardless from action state.
 * <br>1.0 27. November 2003  Created.
 */
public class AttributeToggler implements ActionListener {
  private final DrawingEditor editor;
  private final AttributeKey<Boolean> key;
  private final Object value1;
  private final Object value2;
  private final Action compatibleTextAction;

  /**
   * Creates a new instance.
   */
  public AttributeToggler(DrawingEditor editor, AttributeKey<Boolean> key, Object value1, Object value2) {
    this(editor, key, value1, value2, null);
  }

  public AttributeToggler(DrawingEditor editor, AttributeKey<Boolean> key, Object value1, Object value2, Action compatibleTextAction) {
    this.editor = editor;
    this.key = key;
    this.value1 = value1;
    this.value2 = value2;
    this.compatibleTextAction = compatibleTextAction;
  }

  public DrawingView getView() {
    return editor.getView();
  }

  public DrawingEditor getEditor() {
    return editor;
  }

  public void actionPerformed(ActionEvent evt) {
    if (compatibleTextAction != null) {
      Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
      if (focusOwner instanceof JTextComponent) {
        compatibleTextAction.actionPerformed(evt);
        return;
      }
    }

    Iterator<Figure> i = getView().getSelectedFigures().iterator();
    Object newValue = null;
    if (i.hasNext()) {
      Figure f = i.next();
      Object attr = f.getAttribute(key);
      if (value1 == null && attr == null || (attr != null && attr.equals(value1))) {
        newValue = value2;
      } else {
        newValue = value1;
      }
      getEditor().setDefaultAttribute(key, newValue);
      f.setAttribute(key, newValue);
    }
    while (i.hasNext()) {
      Figure f = i.next();
      f.setAttribute(key, newValue);
    }
  }
}
