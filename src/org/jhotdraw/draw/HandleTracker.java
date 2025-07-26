/*
 * @(#)HandleTracker.java  1.0  2003-12-01
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

import org.jhotdraw.draw.handlers.Handle;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
/**
 * HandleTracker implements interactions with the handles of a Figure.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version 1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 */
public class HandleTracker extends AbstractTool {
    private final org.jhotdraw.draw.handlers.Handle masterHandle;
    private final HandleMultiCaster multiCaster;
    private Point anchor;
    
    /** Creates a new instance. */
    public HandleTracker(org.jhotdraw.draw.handlers.Handle handle) {
        masterHandle = handle;
        multiCaster = new HandleMultiCaster(handle);
    }
    public HandleTracker(org.jhotdraw.draw.handlers.Handle master, Collection<Handle> handles) {
        masterHandle = master;
        multiCaster = new HandleMultiCaster(handles);
    }

    public void activate(DrawingEditor editor) {
        super.activate(editor);
        
        getView().setCursor(masterHandle.getCursor());
    }
    
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        getView().setCursor(Cursor.getDefaultCursor());
    }
    
    public void keyPressed(KeyEvent evt) {
        multiCaster.keyPressed(evt);
    }
    
    public void keyReleased(KeyEvent evt) {
        multiCaster.keyReleased(evt);
    }
    
    public void keyTyped(KeyEvent evt) {
        multiCaster.keyTyped(evt);
    }
    
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
        multiCaster.trackDoubleClick(new Point(evt.getX(), evt.getY()),
                evt.getModifiersEx(), getView());
        }
    }
    
    public void mouseDragged(MouseEvent evt) {
        multiCaster.trackStep(anchor, new Point(evt.getX(), evt.getY()),
                evt.getModifiersEx(), getView());
    }
    
    public void mouseEntered(MouseEvent evt) {
    }
    
    public void mouseExited(MouseEvent evt) {
    }
    
    public void mouseMoved(MouseEvent evt) {
        updateCursor(editor.findView((Container) evt.getSource()),new Point(evt.getX(), evt.getY()));
    }
    
    public void mousePressed(MouseEvent evt) {
        //handle.mousePressed(evt);
        anchor = new Point(evt.getX(), evt.getY());
        multiCaster.trackStart(anchor, evt.getModifiersEx(), getView());
    }
    
    public void mouseReleased(MouseEvent evt) {
        multiCaster.trackEnd(anchor, new Point(evt.getX(), evt.getY()),
                evt.getModifiersEx(), getView());
        fireToolDone();
    }    
}
