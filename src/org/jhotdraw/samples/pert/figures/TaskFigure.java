/*
 * @(#)TaskFigure.java  1.0  18. Juni 2006
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

package org.jhotdraw.samples.pert.figures;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.events.FigureEvent;
import org.jhotdraw.draw.figures.Figure;
import org.jhotdraw.draw.figures.GraphicalCompositeFigure;
import org.jhotdraw.draw.figures.ListFigure;
import org.jhotdraw.draw.figures.RectangleFigure;
import org.jhotdraw.draw.figures.TextFigure;
import org.jhotdraw.draw.handlers.Handle;
import org.jhotdraw.draw.handlers.MoveHandle;
import org.jhotdraw.draw.layouters.VerticalLayouter;
import org.jhotdraw.draw.listeners.AbstractFigureListener;
import org.jhotdraw.draw.locators.RelativeLocator;
import org.jhotdraw.geom.Insets2DDouble;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;

import static org.jhotdraw.draw.AttributeKeys.*;

/**
 * TaskFigure.
 *
 * @author Werner Randelshofer.
 * @version 1.0 18. Juni 2006 Created.
 */
public class TaskFigure extends GraphicalCompositeFigure {
  private HashSet<DependencyFigure> dependencies;
  /**
   * Derived attribute. We cache it here for efficiency reasons.
   */
  private int startTime;

  /**
   * This adapter is used, to connect a TextFigure with the name of
   * the TaskFigure model.
   */
  private static class NameAdapter extends AbstractFigureListener {
    private TaskFigure target;

    public NameAdapter(TaskFigure target) {
      this.target = target;
    }

    public void figureAttributeChanged(org.jhotdraw.draw.events.FigureEvent e) {
      // We could fire a property change event here, in case
      // some other object would like to observe us.
      //target.firePropertyChange("name", e.getOldValue(), e.getNewValue());
    }
  }

  private static class DurationAdapter extends org.jhotdraw.draw.listeners.AbstractFigureListener {
    private TaskFigure target;

    public DurationAdapter(TaskFigure target) {
      this.target = target;
    }

    public void figureAttributeChanged(FigureEvent evt) {
      // We could fire a property change event here, in case
      // some other object would like to observe us.
      //target.firePropertyChange("duration", e.getOldValue(), e.getNewValue());
      for (TaskFigure succ : target.getSuccessors()) {
        succ.updateStartTime();
      }
    }
  }

  /**
   * Creates a new instance.
   */
  public TaskFigure() {
    super(new org.jhotdraw.draw.figures.RectangleFigure());

    setLayouter(new VerticalLayouter());

    RectangleFigure nameCompartmentPF = new org.jhotdraw.draw.figures.RectangleFigure();
    STROKE_COLOR.set(nameCompartmentPF, null);
    nameCompartmentPF.setAttributeEnabled(STROKE_COLOR, false);
    FILL_COLOR.set(nameCompartmentPF, null);
    nameCompartmentPF.setAttributeEnabled(FILL_COLOR, false);
    org.jhotdraw.draw.figures.ListFigure nameCompartment = new ListFigure(nameCompartmentPF);
    org.jhotdraw.draw.figures.ListFigure attributeCompartment = new org.jhotdraw.draw.figures.ListFigure();
    SeparatorLineFigure separator1 = new SeparatorLineFigure();

    applyAttributes(getPresentationFigure());

    add(nameCompartment);
    add(separator1);
    add(attributeCompartment);

    Insets2DDouble insets = new Insets2DDouble(4, 8, 4, 8);
    LAYOUT_INSETS.set(nameCompartment, insets);
    LAYOUT_INSETS.set(attributeCompartment, insets);

    org.jhotdraw.draw.figures.TextFigure nameFigure;
    nameCompartment.add(nameFigure = new org.jhotdraw.draw.figures.TextFigure());
    FONT_BOLD.set(nameFigure, true);
    nameFigure.setAttributeEnabled(FONT_BOLD, false);

    org.jhotdraw.draw.figures.TextFigure durationFigure;
    attributeCompartment.add(durationFigure = new org.jhotdraw.draw.figures.TextFigure());
    FONT_BOLD.set(durationFigure, true);
    durationFigure.setText("0");
    durationFigure.setAttributeEnabled(FONT_BOLD, false);

    TextFigure startTimeFigure;
    attributeCompartment.add(startTimeFigure = new org.jhotdraw.draw.figures.TextFigure());
    startTimeFigure.setEditable(false);
    startTimeFigure.setText("0");
    startTimeFigure.setAttributeEnabled(FONT_BOLD, false);

    applyAttributes(this);
    setAttributeEnabled(STROKE_DASHES, false);

    ResourceBundleUtil labels = ResourceBundleUtil.getLAFBundle("org.jhotdraw.samples.pert.Labels");

    setName(labels.getString("taskDefaultName"));
    setDuration(0);
    startTime = 0;

    dependencies = new HashSet<>();
    nameFigure.addFigureListener(new NameAdapter(this));
    durationFigure.addFigureListener(new DurationAdapter(this));
  }

  public Collection<org.jhotdraw.draw.handlers.Handle> createHandles(int detailLevel) {
    List<Handle> handles = new LinkedList<>();
    if (detailLevel == 0) {
      handles.add(new org.jhotdraw.draw.handlers.MoveHandle(this, org.jhotdraw.draw.locators.RelativeLocator.northWest()));
      handles.add(new org.jhotdraw.draw.handlers.MoveHandle(this, RelativeLocator.northEast()));
      handles.add(new MoveHandle(this, org.jhotdraw.draw.locators.RelativeLocator.southWest()));
      handles.add(new org.jhotdraw.draw.handlers.MoveHandle(this, org.jhotdraw.draw.locators.RelativeLocator.southEast()));
      handles.add(new org.jhotdraw.draw.handlers.ConnectionHandle(this, org.jhotdraw.draw.locators.RelativeLocator.east(), new DependencyFigure()));
    }
    return handles;
  }

  public void setName(String newValue) {
    getNameFigure().setText(newValue);
  }

  public String getName() {
    return getNameFigure().getText();
  }

  public void setDuration(int newValue) {
    int oldValue = getDuration();
    getDurationFigure().setText(Integer.toString(newValue));
    if (oldValue != newValue) {
      for (TaskFigure succ : getSuccessors()) {
        succ.updateStartTime();
      }
    }
  }

  public int getDuration() {
    try {
      return Integer.parseInt(getDurationFigure().getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public void updateStartTime() {
    int oldValue = getStartTime();
    int newValue = 0;
    for (TaskFigure pre : getPredecessors()) {
      newValue = Math.max(newValue, pre.getStartTime() + pre.getDuration());
    }
    getStartTimeFigure().setText(Integer.toString(newValue));
    if (newValue != oldValue) {
      for (TaskFigure succ : getSuccessors()) {
        // The if-statement here guards against
        // cyclic task dependencies.
        if (!this.isDependentOf(succ)) {
          succ.updateStartTime();
        }
      }
    }
    if (oldValue != newValue) {
      fireAreaInvalidated();
    }
  }

  public int getStartTime() {
    try {
      return Integer.parseInt(getStartTimeFigure().getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private org.jhotdraw.draw.figures.TextFigure getNameFigure() {
    return (org.jhotdraw.draw.figures.TextFigure) ((org.jhotdraw.draw.figures.ListFigure) getChild(0)).getChild(0);
  }

  private org.jhotdraw.draw.figures.TextFigure getDurationFigure() {
    return (org.jhotdraw.draw.figures.TextFigure) ((org.jhotdraw.draw.figures.ListFigure) getChild(2)).getChild(0);
  }

  private org.jhotdraw.draw.figures.TextFigure getStartTimeFigure() {
    return (org.jhotdraw.draw.figures.TextFigure) ((org.jhotdraw.draw.figures.ListFigure) getChild(2)).getChild(1);
  }

  private void applyAttributes(Figure f) {
    Map<AttributeKey, Object> attr = getPresentationFigure().getAttributes();
    for (Map.Entry<AttributeKey, Object> entry : attr.entrySet()) {
      f.setAttribute(entry.getKey(), entry.getValue());
    }
  }

  public TaskFigure clone() {
    TaskFigure that = (TaskFigure) super.clone();
    that.dependencies = new HashSet<>();
    that.getNameFigure().addFigureListener(new NameAdapter(that));
    that.getDurationFigure().addFigureListener(new DurationAdapter(that));
    that.updateStartTime();
    return that;
  }

  public void read(DOMInput in) throws IOException {
    double x = in.getAttribute("x", 0d);
    double y = in.getAttribute("y", 0d);
    double w = in.getAttribute("w", 0d);
    double h = in.getAttribute("h", 0d);
    setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
    readAttributes(in);
    in.openElement("model");
    in.openElement("name");
    setName((String) in.readObject());
    in.closeElement();
    in.openElement("duration");
    setDuration((Integer) in.readObject());
    in.closeElement();
    in.closeElement();
  }

  public void write(DOMOutput out) throws IOException {
    Rectangle2D.Double r = getBounds();
    out.addAttribute("x", r.x);
    out.addAttribute("y", r.y);
    writeAttributes(out);
    out.openElement("model");
    out.openElement("name");
    out.writeObject(getName());
    out.closeElement();
    out.openElement("duration");
    out.writeObject(getDuration());
    out.closeElement();
    out.closeElement();
  }

  public int getLayer() {
    return 0;
  }

  public Set<DependencyFigure> getDependencies() {
    return Collections.unmodifiableSet(dependencies);
  }

  public void addDependency(DependencyFigure f) {
    dependencies.add(f);
    updateStartTime();
  }

  public void removeDependency(DependencyFigure f) {
    dependencies.remove(f);
    updateStartTime();
  }

  /**
   * Returns dependent PertTasks which are directly connected via a
   * PertDependency to this TaskFigure.
   */
  public List<TaskFigure> getSuccessors() {
    LinkedList<TaskFigure> list = new LinkedList<>();
    for (DependencyFigure c : getDependencies()) {
      if (c.getStartFigure() == this) {
        list.add((TaskFigure) c.getEndFigure());
      }
    }
    return list;
  }

  /**
   * Returns predecessor PertTasks which are directly connected via a
   * PertDependency to this TaskFigure.
   */
  public List<TaskFigure> getPredecessors() {
    LinkedList<TaskFigure> list = new LinkedList<>();
    for (DependencyFigure c : getDependencies()) {
      if (c.getEndFigure() == this) {
        list.add((TaskFigure) c.getStartFigure());
      }
    }
    return list;
  }

  /**
   * Returns true, if the current task is a direct or
   * indirect dependent of the specified task.
   * If the dependency is cyclic, then this method returns true
   * if <code>this</code> is passed as a parameter and for every other
   * task in the cycle.
   */
  public boolean isDependentOf(TaskFigure t) {
    if (this == t) return true;
    for (TaskFigure pre : getPredecessors()) {
      if (pre.isDependentOf(t)) {
        return true;
      }
    }
    return false;
  }

  public String toString() {
    return "TaskFigure#" + hashCode() + " " + getName() + " " + getDuration() + " " + getStartTime();
  }
}

