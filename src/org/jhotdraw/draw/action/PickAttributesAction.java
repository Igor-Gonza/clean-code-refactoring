/*
 * @(#)PickAttributesAction.java  1.0  25. November 2003
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

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.editors.DrawingEditor;
import org.jhotdraw.draw.events.FigureSelectionEvent;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * PickAttributesAction.
 *
 * @author Werner Randelshofer
 * @version 1.0 25. November 2003  Created.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class PickAttributesAction extends AbstractSelectedAction {
  private final ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());

  /**
   * Creates a new instance.
   */
  public PickAttributesAction(org.jhotdraw.draw.editors.DrawingEditor editor) {
    super(editor);
    labels.configureAction(this, "attributesPick");
    setEnabled(true);
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    pickAttributes();
  }

  public void pickAttributes() {
    DrawingEditor editor = getEditor();
    Collection<Figure> selection = getView().getSelectedFigures();
    if (!selection.isEmpty()) {
      org.jhotdraw.draw.figures.Figure figure = selection.iterator().next();
      for (Map.Entry<AttributeKey, Object> entry : figure.getAttributes().entrySet()) {
        if (entry.getKey() != AttributeKeys.TEXT) {
          editor.setDefaultAttribute(entry.getKey(), entry.getValue());
        }
      }
    }
  }

  public void selectionChanged(FigureSelectionEvent evt) {
    setEnabled(getView().getSelectionCount() == 1);
  }
}
