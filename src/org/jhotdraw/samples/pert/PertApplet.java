/*
 * @(#)PertApplet.java  1.0  2006-07-15
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

package org.jhotdraw.samples.pert;

import org.jhotdraw.draw.drawings.Drawing;
import org.jhotdraw.draw.figures.TextFigure;
import org.jhotdraw.draw.action.SwingWorker;
import org.jhotdraw.xml.NanoXMLLiteDOMInput;
import org.jhotdraw.xml.NanoXMLLiteDOMOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

/**
 * PertApplet.
 *
 * @author Werner Randelshofer
 * @version 1.0 2006-07-15 Created.
 */
public class PertApplet extends JApplet {
  private final static String VERSION = "0.5";
  private final static String NAME = "JHotDraw Pert";
  private PertPanel drawingPanel;

  /**
   * We override getParameter() to make it work even if we have no Applet
   * context.
   */
  public String getParameter(String name) {
    try {
      return super.getParameter(name);
    } catch (NullPointerException e) {
      return null;
    }
  }

  /**
   * Initializes the applet PertApplet
   */
  public void init() {
    // Set look and feel
    // -----------------
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Throwable e) {
      // Do nothing.
      // If we can't set the desired look and feel, UIManager does
      // automatically the right thing for us.
    }

    // Display copyright info while we are loading the data
    // ----------------------------------------------------
    Container c = getContentPane();
    c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
    String[] labels = getAppletInfo().split("\n");//Strings.split(getAppletInfo(), '\n');
    for (int i = 0; i < labels.length; i++) {
      c.add(new JLabel((labels[i].isEmpty()) ? " " : labels[i]));
    }

    // We load the data using a worker thread
    // --------------------------------------
    new SwingWorker() {
      public Object construct() {
        Object result;
        try {
          System.out.println("getParameter.datafile:" + getParameter("datafile"));
          if (getParameter("data") != null) {
            NanoXMLLiteDOMInput domInput = new NanoXMLLiteDOMInput(new PertFactory(), new StringReader(getParameter("data")));
            domInput.openElement("PertDiagram");
            result = domInput.readObject(0);
          } else if (getParameter("datafile") != null) {
            InputStream in = null;
            try {
              URL url = new URL(getDocumentBase(), getParameter("datafile"));
              in = url.openConnection().getInputStream();
              NanoXMLLiteDOMInput domInput = new NanoXMLLiteDOMInput(new PertFactory(), in);
              domInput.openElement("PertDiagram");
              result = domInput.readObject(0);
            } finally {
              if (in != null) in.close();
            }
          } else {
            result = null;
          }
        } catch (Throwable t) {
          result = t;
        }
        return result;
      }

      public void finished() {
        if (getValue() instanceof Throwable) {
          ((Throwable) getValue()).printStackTrace();
        }
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.removeAll();
        c.add(drawingPanel = new PertPanel());

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
        NanoXMLLiteDOMInput domInput = new NanoXMLLiteDOMInput(new PertFactory(), in);
        domInput.openElement("Pert");

        setDrawing((Drawing) domInput.readObject(0));
      } catch (Throwable e) {
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
      NanoXMLLiteDOMOutput domo = new NanoXMLLiteDOMOutput(new PertFactory());
      domo.openElement("Pert");
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

  public String[][] getParameterInfo() {
    return new String[][]{{"data", "String", "the data to be displayed by this applet."}, {"datafile", "URL", "an URL to a file containing the data to be displayed by this applet."},};
  }

  public String getAppletInfo() {
    return NAME + "\nVersion " + VERSION + "\n\nCopyright © 2004-2006, © Werner Randelshofer" + "\nAlle Rights Reserved." + "\n\nThis software is based on" + "\nJHotDraw © 1996-1997, IFA Informatik und Erich Gamma" + "\nNanoXML © 2000-2002 Marc De Scheemaecker" + "\n" + "\nJavaScript code can access the drawing data using the setData() and getData() methods."

            ;
  }

  /**
   * This method is called from within the init() method to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {
    toolButtonGroup = new javax.swing.ButtonGroup();

  }// </editor-fold>//GEN-END:initComponents

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame f = new JFrame("JHotDraw Pert Applet");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PertApplet a = new PertApplet();
        f.getContentPane().add(a);
        a.init();
        f.setSize(500, 400);
        f.setVisible(true);
        a.start();
      }
    });
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup toolButtonGroup;
  // End of variables declaration//GEN-END:variables

}
