/*
 * @(#)ApplyAttributesAction.java  1.0  25. November 2003
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

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.FigureSelectionEvent;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.Locale;

/**
 * ApplyAttributesAction.
 *
 * @author Werner Randelshofer
 * @version 1.0 25. November 2003  Created.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ApplyAttributesAction extends AbstractSelectedAction {
  private final ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());

  /**
   * Creates a new instance.
   */
  public ApplyAttributesAction(DrawingEditor editor) {
    super(editor);
    labels.configureAction(this, "attributesApply");
    setEnabled(true);
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    applyAttributes();
  }

  public void applyAttributes() {
    DrawingEditor editor = getEditor();
    for (Figure figure : getView().getSelectedFigures()) {
      editor.applyDefaultAttributesTo(figure);
    }
  }

  public void selectionChanged(FigureSelectionEvent evt) {
    setEnabled(getView().getSelectionCount() == 1);
  }
}
