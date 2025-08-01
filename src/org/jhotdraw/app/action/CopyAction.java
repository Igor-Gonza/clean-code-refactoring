/*
 * @(#)CopyAction.java  1.0  October 9, 2005
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

package org.jhotdraw.app.action;

import org.jhotdraw.app.EditableComponent;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Copies the selected region and place its contents into the system clipboard.
 * Acts on the EditableComponent or JTextComponent which had the focus when
 * the ActionEvent was generated.
 *
 * @author Werner Randelshofer
 * @version 1.0 October 9, 2005, Created.
 */
public class CopyAction extends DefaultEditorKit.CopyAction {
  public final static String ID = "copy";

  /**
   * Creates a new instance.
   */
  public CopyAction() {
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.app.Labels");
    labels.configureAction(this, ID);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
    if (focusOwner instanceof EditableComponent) {
      ((EditableComponent) focusOwner).copy();
    } else {
      super.actionPerformed(evt);
    }
  }
}
