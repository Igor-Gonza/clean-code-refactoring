/*
 * @(#)GeometryEdit.java  1.0  January 22, 2006
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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * GeometryEdit.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 22, 2006, Created.
 */
public class GeometryEdit extends AbstractUndoableEdit {
  private final Figure owner;
  private final Object oldGeometry;
  private final Object newGeometry;

  public GeometryEdit(Figure owner, Object oldGeometry, Object newGeometry) {
    this.owner = owner;
    this.oldGeometry = oldGeometry;
    this.newGeometry = newGeometry;
  }

  @Override
  public String getPresentationName() {
    return "Geometry changed";
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    owner.willChange();
    owner.restoreTo(oldGeometry);
    owner.changed();
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    owner.willChange();
    owner.restoreTo(newGeometry);
    owner.changed();
  }
}
