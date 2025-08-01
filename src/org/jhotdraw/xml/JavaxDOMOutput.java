/*
 * @(#)DOMOutput.java  2.1  2006-08-26
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

package org.jhotdraw.xml;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import java.io.*;

/**
 * DOMOutput.
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-08-26 Method setDoctype added.
 * <br>2.0 2006-06-10 Support for prototype objects added. Support for
 * Enum and double array objects added.
 * <br>1.2 2006-03-20 Added support for default values.
 * <br>1.1 2006-01-18 Remove ".0" at the end of float and double numbers.
 * <br>1.0 February 17, 2004, Created.
 */
public class JavaxDOMOutput implements DOMOutput {
  /**
   * The doctype of the XML document.
   */
  private String doctype;

  /**
   * This map is used to marshall references to objects to
   * the XML DOM. A key in this map is a Java Object, a value in this map
   * is String representing a marshalled reference to that object.
   */
  private final HashMap<Object, String> objectIds;

  /**
   * This map is used to cache prototype objects.
   */
  private HashMap<String, Object> prototypes;

  /**
   * The document used for output.
   */
  private final Document document;
  /**
   * The current node used for output.
   */
  private Node current;
  /**
   * The factory used to create objects.
   */
  private final DOMFactory factory;

  /**
   * Creates a new instance.
   */
  public JavaxDOMOutput(DOMFactory factory) throws IOException {
    this.factory = factory;
    try {
      objectIds = new HashMap<>();
      document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      current = document;
    } catch (ParserConfigurationException e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  /**
   * Writes the contents of the DOMOutput into the specified output stream.
   */
  public void save(OutputStream out) throws IOException {
    try {
      if (doctype != null) {
        OutputStreamWriter w = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        w.write("<!DOCTYPE ");
        w.write(doctype);
        w.write(">\n");
        w.flush();
      }
      Transformer t = TransformerFactory.newInstance().newTransformer();
      t.transform(new DOMSource(document), new StreamResult(out));
    } catch (TransformerException e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  /**
   * Writes the contents of the DOMOutput into the specified output stream.
   */
  public void save(Writer out) throws IOException {
    try {
      if (doctype != null) {
        out.write("<!DOCTYPE ");
        out.write(doctype);
        out.write(">\n");
      }
      Transformer t = TransformerFactory.newInstance().newTransformer();
      t.transform(new DOMSource(document), new StreamResult(out));
    } catch (TransformerException e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  /**
   * Puts a new element into the DOM Document.
   * The new element is added as a child to the current element in the DOM
   * document. Then it becomes the current element.
   * The element must be closed using closeElement.
   */
  public void openElement(String tagName) {
    Element newElement = document.createElement(tagName);
    current.appendChild(newElement);
    current = newElement;
  }

  /**
   * Closes the current element of the DOM Document.
   * The parent of the current element becomes the current element.
   *
   * @throws IllegalArgumentException if the provided tagName does
   *                                  not match the tag name of the element.
   */
  public void closeElement() {
//    if (!((Element) current).getName().equals(tagName)) {
//      throw new IllegalArgumentException("Attempt to close wrong element:" + tagName + "!=" + ((Element) current).getName());
//    }
    current = current.getParentNode();
  }

  /**
   * Adds a comment to the current element of the DOM Document.
   */
  public void addComment(String comment) {
    current.appendChild(document.createComment(comment));
  }

  /**
   * Adds a text to current element of the DOM Document.
   * Note: Multiple consecutive texts will be merged.
   */
  public void addText(String text) {
    current.appendChild(document.createTextNode(text));
  }

  /**
   * Adds an attribute to current element of the DOM Document.
   */
  public void addAttribute(String name, String value) {
    if (value != null) {
      ((Element) current).setAttribute(name, value);
    }
  }

  /**
   * Adds an attribute to current element of the DOM Document.
   */
  public void addAttribute(String name, int value) {
    ((Element) current).setAttribute(name, Integer.toString(value));
  }

  /**
   * Adds an attribute to current element of the DOM Document.
   */
  public void addAttribute(String name, boolean value) {
    ((Element) current).setAttribute(name, Boolean.toString(value));
  }

  /**
   * Adds an attribute to current element of the DOM Document.
   */
  public void addAttribute(String name, float value) {
    // Remove the awkward .0 at the end of each number
    String str = Float.toString(value);
    if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
    ((Element) current).setAttribute(name, str);
  }

  /**
   * Adds an attribute to current element of the DOM Document.
   */
  public void addAttribute(String name, double value) {
    // Remove the awkward .0 at the end of each number
    String str = Double.toString(value);
    if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
    ((Element) current).setAttribute(name, str);
  }

  public void writeObject(Object o) throws IOException {
    if (o == null) {
      openElement("null");
      closeElement();
    } else if (o instanceof DOMStorable) {
      writeStorable((DOMStorable) o);
    } else if (o instanceof String) {
      openElement("string");
      addText((String) o);
      closeElement();
    } else if (o instanceof Integer) {
      openElement("int");
      addText(o.toString());
      closeElement();
    } else if (o instanceof Long) {
      openElement("long");
      addText(o.toString());
      closeElement();
    } else if (o instanceof Double) {
      openElement("double");
      addText(o.toString());
      closeElement();
    } else if (o instanceof Float) {
      openElement("float");
      addText(o.toString());
      closeElement();
    } else if (o instanceof Boolean) {
      openElement("boolean");
      addText(o.toString());
      closeElement();
    } else if (o instanceof Color) {
      Color c = (Color) o;
      openElement("color");
      addAttribute("rgba", "#" + Integer.toHexString(c.getRGB()));
      closeElement();
    } else if (o instanceof int[]) {
      openElement("intArray");
      int[] a = (int[]) o;
      for (int j : a) {
        writeObject(j);
      }
      closeElement();
    } else if (o instanceof float[]) {
      openElement("floatArray");
      float[] a = (float[]) o;
      for (float v : a) {
        writeObject(v);
      }
      closeElement();
    } else if (o instanceof double[]) {
      openElement("doubleArray");
      double[] a = (double[]) o;
      for (double v : a) {
        writeObject(v);
      }
      closeElement();
    } else if (o instanceof Font) {
      Font f = (Font) o;
      openElement("font");
      addAttribute("name", f.getName());
      addAttribute("style", f.getStyle());
      addAttribute("size", f.getSize());
      closeElement();
    } else if (o instanceof Enum) {
      openElement("enum");
      Enum e = (Enum) o;
      addAttribute("type", factory.getEnumName(e));
      addText(factory.getEnumValue(e));
      closeElement();
    } else {
      throw new IllegalArgumentException("unable to store: " + o + " " + o.getClass());
    }
  }

  private void writeStorable(DOMStorable o) throws IOException {
    String tagName = factory.getName(o);
    if (tagName == null) throw new IllegalArgumentException("no tag name for:" + o);
    openElement(tagName);
    if (objectIds.containsKey(o)) {
      addAttribute("ref", objectIds.get(o));
    } else {
      String id = Integer.toString(objectIds.size(), 16);
      objectIds.put(o, id);
      addAttribute("id", id);
      o.write(this);
    }
    closeElement();
  }

  public void addAttribute(String name, float value, float defaultValue) {
    if (value != defaultValue) {
      addAttribute(name, value);
    }
  }

  public void addAttribute(String name, int value, int defaultValue) {
    if (value != defaultValue) {
      addAttribute(name, value);
    }
  }

  public void addAttribute(String name, double value, double defaultValue) {
    if (value != defaultValue) {
      addAttribute(name, value);
    }
  }

  public void addAttribute(String name, boolean value, boolean defaultValue) {
    if (value != defaultValue) {
      addAttribute(name, value);
    }
  }

  public void addAttribute(String name, String value, String defaultValue) {
    if (!value.equals(defaultValue)) {
      addAttribute(name, value);
    }
  }

  public Object getPrototype() {
    if (prototypes == null) {
      prototypes = new HashMap<>();
    }
    if (!prototypes.containsKey(current.getNodeName())) {
      prototypes.put(current.getNodeName(), factory.create(current.getNodeName()));
    }
    return prototypes.get(current.getNodeName());
  }

  public void setDoctype(String doctype) {
    this.doctype = doctype;
  }
}
