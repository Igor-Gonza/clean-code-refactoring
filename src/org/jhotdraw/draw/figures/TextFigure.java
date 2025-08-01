/*
 * @(#)TextFigure.java  1.0.1  2006-02-27
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

package org.jhotdraw.draw.figures;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.handlers.FontSizeHandle;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.draw.handlers.MoveHandle;
import org.jhotdraw.draw.locators.RelativeLocator;
import org.jhotdraw.draw.tools.Tool;
import org.jhotdraw.draw.tools.TextTool;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.Insets2DDouble;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * A text figure.
 *
 * @author Werner Randelshofer
 * @version 2.0.1 2006-02-27 Draw UNDERLINE_LOW_ONE_PIXEL instead of UNDERLINE_ON.
 * <br>2.0 2006-01-14 Changed to support double precision coordinates.
 * <br>1.0 2003-12-01 Derived from JHotDraw 5.4b1.
 * @see TextTool
 */
public class TextFigure extends AttributedFigure implements TextHolder {
  protected Point2D.Double origin = new Point2D.Double();
  private boolean editable = true;

  // cache of the TextFigure's layout
  private transient TextLayout textLayout;

  public TextFigure() {
    this("Text");
  }

  public TextFigure(String text) {
    setText(text);
  }

  /**
   * Gets the text shown by the text figure.
   */
  public String getText() {
    return (String) getAttribute(TEXT);
  }

  /**
   * Sets the text shown by the text figure.
   */
  public void setText(String newText) {
    setAttribute(TEXT, newText);
  }

  public void basicTransform(AffineTransform tx) {
    tx.transform(origin, origin);
  }

  public void basicSetBounds(Point2D.Double anchor, Point2D.Double lead) {
    origin = new Point2D.Double(anchor.x, anchor.y);
  }

  public boolean contains(Point2D.Double p) {
    if (getBounds().contains(p)) {
      return true;
    }
    if (decorator != null) {
      updateDecoratorBounds();
      return decorator.contains(p);
    }
    return false;
  }

  protected void drawStroke(Graphics2D g) {
    // TODO document why this method is empty
  }

  protected void drawFill(Graphics2D g) {
    // TODO document why this method is empty
  }

  @Override
  protected void drawText(Graphics2D g) {
    if (getText() != null || isEditable()) {
      TextLayout layout = getTextLayout();
      layout.draw(g, (float) origin.x, (float) (origin.y + layout.getAscent()));
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    textLayout = null;
  }

  protected TextLayout getTextLayout() {
    if (textLayout == null) {
      String text = getText();
      if (text == null || text.isEmpty()) {
        text = " ";
      }

      FontRenderContext frc = getFontRenderContext();
      HashMap<TextAttribute, Object> textAttributes = new HashMap<>();
      textAttributes.put(TextAttribute.FONT, getFont());
      if (Boolean.TRUE.equals(FONT_UNDERLINED.get(this))) {
        textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
      }
      textLayout = new TextLayout(text, textAttributes, frc);
    }
    return textLayout;
  }

  public Rectangle2D.Double getBounds() {
    TextLayout layout = getTextLayout();
    return new Rectangle2D.Double(origin.x, origin.y, layout.getAdvance(), layout.getAscent() + layout.getDescent());
  }

  @Override
  public Dimension2DDouble getPreferredSize() {
    Rectangle2D.Double b = getBounds();
    return new Dimension2DDouble(b.width, b.height);
  }

  @Override
  public Rectangle2D.Double getFigureDrawBounds() {
    if (getText() == null) {
      return getBounds();
    } else {
      TextLayout layout = getTextLayout();
      Rectangle2D.Double r = new Rectangle2D.Double(origin.x, origin.y, layout.getAdvance(), layout.getAscent());
      Rectangle2D lBounds = layout.getBounds();
      if (!lBounds.isEmpty() && !Double.isNaN(lBounds.getX())) {
        r.add(new Rectangle2D.Double(lBounds.getX() + origin.x, (lBounds.getY() + origin.y + layout.getAscent()), lBounds.getWidth(), lBounds.getHeight()));
      }
      // grow by two pixels to take antialiasing into account
      Geom.grow(r, 2d, 2d);
      return r;
    }
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    LinkedList<Handle> handles = new LinkedList<>();
    if (detailLevel == 0) {
      handles.add(new MoveHandle(this, RelativeLocator.northWest()));
      handles.add(new MoveHandle(this, RelativeLocator.northEast()));
      handles.add(new MoveHandle(this, RelativeLocator.southEast()));
      handles.add(new FontSizeHandle(this));
    }
    return handles;
  }

  @Override
  protected void validate() {
    super.validate();
    textLayout = null;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean b) {
    this.editable = b;
  }

  public int getTextColumns() {
    return (getText() == null) ? 4 : Math.max(getText().length(), 4);
  }

  /**
   * Returns a specialized tool for the given coordinate.
   * <p>Returns null, if no specialized tool is available.
   */
  @Override
  public Tool getTool(Point2D.Double p) {
    return (isEditable() && contains(p)) ? new TextTool(this) : null;
  }

  @Override
  public void read(DOMInput in) throws IOException {
    setBounds(new Point2D.Double(in.getAttribute("x", 0d), in.getAttribute("y", 0d)), new Point2D.Double(0, 0));
    readAttributes(in);
  }

  @Override
  public void write(DOMOutput out) throws IOException {
    Rectangle2D.Double b = getBounds();
    out.addAttribute("x", b.x);
    out.addAttribute("y", b.y);
    writeAttributes(out);
  }

  /**
   * Gets the number of characters used to expand tabs.
   */
  public int getTabSize() {
    return 8;
  }

  public TextHolder getLabelFor() {
    return this;
  }

  public Insets2DDouble getInsets() {
    return new Insets2DDouble(0, 0, 0, 0);
  }

  public void restoreTo(Object geometry) {
    Point2D.Double p = (Point2D.Double) geometry;
    origin.x = p.x;
    origin.y = p.y;
  }

  public Object getRestoreData() {
    return origin.clone();
  }

  public TextFigure clone() {
    TextFigure that = (TextFigure) super.clone();
    that.origin = (Point2D.Double) this.origin.clone();
    that.textLayout = null;
    return that;
  }

  public Font getFont() {
    return AttributeKeys.getFont(this);
  }

  public Color getTextColor() {
    return TEXT_COLOR.get(this);
  }

  public Color getFillColor() {
    return FILL_COLOR.get(this);
  }

  public void setFontSize(float size) {
    FONT_SIZE.set(this, (double) size);
  }

  public float getFontSize() {
    return FONT_SIZE.get(this).floatValue();
  }
}
