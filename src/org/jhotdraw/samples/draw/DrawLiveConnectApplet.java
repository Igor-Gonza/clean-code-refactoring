/*
 * @(#)DrawLiveConnectApplet.java  2.0  2006-01-15
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

package org.jhotdraw.samples.draw;

import netscape.javascript.JSObject;
import org.jhotdraw.draw.drawings.Drawing;
import org.jhotdraw.draw.figures.TextFigure;
import org.jhotdraw.draw.action.SwingWorker;
import org.jhotdraw.xml.NanoXMLLiteDOMInput;
import org.jhotdraw.xml.NanoXMLLiteDOMOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

/**
 * DrawLiveConnectApplet. Supports loading and saving of images to JavaScript.
 *
 * @author wrandels
 * @version 2.0 Changed to support double precision coordinates.
 * <br>1.0 Created on 10. März 2004, 13:22.
 */
public class DrawLiveConnectApplet extends JApplet {
  private static final String VERSION = "0.44";
  private static final String NAME = "PlasmaDraw";

  /**
   * Initializes the applet DrawApplet
   */
  @Override
  public void init() {
    // Set look and feel
    // -----------------
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      // Do nothing.
      // If we can't set the desired look and feel, UIManager does
      // automatically the right thing for us.
    }

    // Display copyright info while we are loading the data
    // ----------------------------------------------------
    Container c = getContentPane();
    c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
    String[] lines = getAppletInfo().split("\n");
    for (String line : lines) {
      c.add(new JLabel(line));
    }

    // We load the data using a worker thread
    // --------------------------------------
    new SwingWorker() {
      public Object construct() {
        Object result;
        try {
          if (getParameter("data") != null && !getParameter("data").isEmpty()) {
            NanoXMLLiteDOMInput domInput = new NanoXMLLiteDOMInput(new DrawFigureFactory(), new StringReader(getParameter("data")));
            domInput.openElement("PlasmaDraw");
            result = domInput.readObject(0);
          } else if (getParameter("datafile") != null) {
            InputStream in = null;
            try {
              URL url = new URL(getDocumentBase(), getParameter("datafile"));
              in = url.openConnection().getInputStream();
              NanoXMLLiteDOMInput domInput = new NanoXMLLiteDOMInput(new DrawFigureFactory(), in);
              domInput.openElement("PlasmaDraw");
              result = domInput.readObject(0);
            } finally {
              if (in != null) in.close();
            }
          } else {
            result = null;
          }
        } catch (Exception t) {
          result = t;
        }
        return result;
      }

      @Override
      public void finished() {
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.removeAll();

        Object result = getValue();
        initComponents();
        if (result != null) {
          if (result instanceof Drawing) {
            setDrawing((Drawing) result);
          } else if (result instanceof Throwable) {
            getDrawing().add(new TextFigure(result.toString()));
            ((Throwable) result).printStackTrace();
          }
        }

        boolean isLiveConnect;
        try {
          Class.forName("netscape.javascript.JSObject");
          isLiveConnect = true;
        } catch (Exception t) {
          isLiveConnect = false;
        }
        loadButton.setEnabled(isLiveConnect && getParameter("dataread") != null);
        saveButton.setEnabled(isLiveConnect && getParameter("datawrite") != null);

        if (isLiveConnect) {
          String methodName = getParameter("dataread");
          if (methodName.indexOf('(') > 0) {
            methodName = methodName.substring(0, methodName.indexOf('(') - 1);
          }
          JSObject win = JSObject.getWindow(DrawLiveConnectApplet.this);
          Object data = win.call(methodName);
          if (data instanceof String) {
            setData((String) data);
          }
        }
        c.validate();
      }
    }.start();
  }

  private void setDrawing(Drawing d) {
    drawingPanel.setDrawing(d);
  }

  private Drawing getDrawing() {
    return drawingPanel.getDrawing();
  }

  public void setData(String text) {
    if (text != null && !text.isEmpty()) {
      try (StringReader in = new StringReader(text)) {
        NanoXMLLiteDOMInput domInput = new NanoXMLLiteDOMInput(new DrawFigureFactory(), in);
        domInput.openElement("PlasmaDraw");

        setDrawing((Drawing) domInput.readObject(0));
      } catch (Exception e) {
        getDrawing().clear();
        TextFigure tf = new TextFigure();
        tf.setText(e.getMessage());
        tf.setBounds(new Point2D.Double(10, 10), new Point2D.Double(100, 100));
        getDrawing().add(tf);
        e.printStackTrace();
      }
    }
  }

  public String getData() {
    CharArrayWriter out = new CharArrayWriter();
    try {
      NanoXMLLiteDOMOutput domo = new NanoXMLLiteDOMOutput(new DrawFigureFactory());
      domo.openElement("PlasmaDraw");
      domo.writeObject(getDrawing());
      domo.closeElement();
      domo.save(out);
    } catch (IOException e) {
      TextFigure tf = new TextFigure();
      tf.setText(e.getMessage());
      tf.setBounds(new Point2D.Double(10, 10), new Point2D.Double(100, 100));
      getDrawing().add(tf);
      e.printStackTrace();
    } finally {
      if (out != null) out.close();
    }
    return out.toString();
  }

  @Override
  public String[][] getParameterInfo() {
    return new String[][]{
            {"data", "String", "the data to be displayed by this applet."},
            {"datafile", "URL", "an URL to a file containing the data to be displayed by this applet."},
            {"dataread", "function()", "the name of a JavaScript function which can be used to read the data."},
            {"datawrite", "function()", "the name of a JavaScript function which can be used to write the data."}
    };
  }

  @Override
  public String getAppletInfo() {
    return NAME + "\nVersion " + VERSION
            + "\n\nCopyright 2004 © Werner Randelshofer"
            + "\nAlle Rechte vorbehalten."
            + "\n\nDiese Software basiert auf"
            + "\nJHotDraw © 1996, 1997 IFA Informatik und Erich Gamma";

  }

  /**
   * This method is called from within the init() method to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  private void initComponents() {//GEN-BEGIN:initComponents
    toolButtonGroup = new javax.swing.ButtonGroup();
    drawingPanel = new org.jhotdraw.samples.draw.DrawingPanel();
    jToolBar1 = new javax.swing.JToolBar();
    loadButton = new javax.swing.JButton();
    saveButton = new javax.swing.JButton();

    FormListener formListener = new FormListener();

    getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

    jToolBar1.setFloatable(false);
    loadButton.setText("Laden");
    loadButton.addActionListener(formListener);

    jToolBar1.add(loadButton);

    saveButton.setText("Speichern");
    saveButton.addActionListener(formListener);

    jToolBar1.add(saveButton);

    getContentPane().add(jToolBar1, java.awt.BorderLayout.SOUTH);

  }

  // Code for dispatching events from components to event handlers.

  private class FormListener implements java.awt.event.ActionListener {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
      if (evt.getSource() == loadButton) {
        DrawLiveConnectApplet.this.load(evt);
      } else if (evt.getSource() == saveButton) {
        DrawLiveConnectApplet.this.save(evt);
      }
    }
  }//GEN-END:initComponents

  private void save(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save
    try {
      String methodName = getParameter("datawrite");
      if (methodName.indexOf('(') > 0) {
        methodName = methodName.substring(0, methodName.indexOf('(') - 1);
      }
      JSObject win = JSObject.getWindow(this);
      Object result = win.call(methodName, getData());
    } catch (Exception t) {
      TextFigure tf = new TextFigure("Fehler: " + t);
      AffineTransform tx = new AffineTransform();
      tx.translate(10, 20);
      tf.transform(tx);
      getDrawing().add(tf);
    }
  }//GEN-LAST:event_save

  private void load(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_load
    try {
      String methodName = getParameter("dataread");
      if (methodName.indexOf('(') > 0) {
        methodName = methodName.substring(0, methodName.indexOf('(') - 1);
      }
      JSObject win = JSObject.getWindow(this);
      Object result = win.call(methodName);
      if (result instanceof String) {
        setData((String) result);
      }
    } catch (Exception t) {
      TextFigure tf = new TextFigure("Fehler: " + t);
      AffineTransform tx = new AffineTransform();
      tx.translate(10, 20);
      tf.transform(tx);
      getDrawing().add(tf);
    }

  }//GEN-LAST:event_load

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private org.jhotdraw.samples.draw.DrawingPanel drawingPanel;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JButton loadButton;
  private javax.swing.JButton saveButton;
  private javax.swing.ButtonGroup toolButtonGroup;
  // End of variables declaration//GEN-END:variables

}
