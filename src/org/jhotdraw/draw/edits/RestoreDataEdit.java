/*
 * @(#)RestoreDataEdit.java  2.0  2006-01-14
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

package org.jhotdraw.draw.edits;

import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Locale;

/**
 * RestoreDataEdit.
 *
 * @author Werner Randelshofer
 * @version 2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class RestoreDataEdit extends AbstractUndoableEdit {
  private final Figure figure;
  private final Object oldRestoreData;
  private final Object newRestoreData;

  public RestoreDataEdit(Figure figure, Object oldRestoreData) {
    this.figure = figure;
    this.oldRestoreData = oldRestoreData;
    this.newRestoreData = figure.getRestoreData();
  }

  @Override
  public String getPresentationName() {
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels", Locale.getDefault());
    return labels.getString("transformFigure");
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    figure.willChange();
    figure.restoreTo(newRestoreData);
    figure.changed();
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    figure.willChange();
    figure.restoreTo(oldRestoreData);
    figure.changed();
  }
}
