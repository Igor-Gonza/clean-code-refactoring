/*
 * @(#)ToggleGridAction.java  1.1 2006-04-21
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

import org.jhotdraw.draw.Constrainer;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.GridConstrainer;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * ToggleGridAction.
 *
 * @author Werner Randelshofer
 * @version 1.1 2006-04-21 Constructor with DrawingEditor parameter added.
 * <br>1.0 January 16, 2006, Created.
 */
@SuppressWarnings("unused")
public class ToggleGridAction extends AbstractViewAction {
  public final static String ID = "alignGrid";
  private String label;
  private final Constrainer onConstrainer, offConstrainer;

  /**
   * Creates a new instance.
   */
  public ToggleGridAction(DrawingEditor editor) {
    this(editor, new GridConstrainer(10, 10), new GridConstrainer(1, 1));
  }

  public ToggleGridAction(DrawingEditor editor, Constrainer onConstrainer, Constrainer offConstrainer) {
    this((DrawingView) null, new GridConstrainer(10, 10), new GridConstrainer(1, 1));
    setEditor(editor);
  }

  /**
   * Creates a new instance.
   */
  public ToggleGridAction(DrawingView view, Constrainer onConstrainer, Constrainer offConstrainer) {
    super(view);
    this.onConstrainer = onConstrainer;
    this.offConstrainer = offConstrainer;
    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.draw.Labels");
    labels.configureAction(this, ID);
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {

    if (getView().getConstrainer() == onConstrainer) {
      getView().setConstrainer(offConstrainer);
    } else {
      getView().setConstrainer(onConstrainer);
    }
  }

}
