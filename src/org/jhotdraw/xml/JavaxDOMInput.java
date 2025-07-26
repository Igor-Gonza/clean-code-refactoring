/*
 * @(#)JavaxDOMInput.java  2.1  2006-07-08
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * DOMInput.
 *
 * @author Werner Randelshofer
 * @version 2.1 2006-07-08 Support objects which don't have an ID.
 * <br>2.0 2006-06-10 Support for Enum and double array objects added.
 * <br>1.0 February 17, 2004, Created.
 */
public class JavaxDOMInput implements DOMInput {
  /**
   * This map is used to unmarshall references to objects to
   * the XML DOM. A key in this map is a String representing a marshalled
   * reference. A value in this map is an unmarshalled Object.
   */
  private HashMap<String, Object> idObjects = new HashMap<>();

  /**
   * The document used for input.
   */
  private Document document;
  /**
   * The current node used for input.
   */
  private Node current;

  /**
   * The factory used to create objects from XML tag names.
   */
  private DOMFactory factory;

  public JavaxDOMInput(DOMFactory factory, InputStream in) throws IOException {
    this.factory = factory;
    try {
      Transformer t = TransformerFactory.newInstance().newTransformer();
      DOMResult result = new DOMResult();
      t.transform(new StreamSource(in), result);
      document = (Document) result.getNode();
      current = document;
    } catch (TransformerException e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  public JavaxDOMInput(DOMFactory factory, Reader in) throws IOException {
    this.factory = factory;
    try {
      Transformer t = TransformerFactory.newInstance().newTransformer();
      DOMResult result = new DOMResult();
      t.transform(new StreamSource(in), result);
      document = (Document) result.getNode();
      current = document;
    } catch (TransformerException e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  /**
   * Returns the tag name of the current element.
   */
  public String getTagName() {
    return ((Element) current).getTagName();
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public String getAttribute(String name, String defaultValue) {
    String value = ((Element) current).getAttribute(name);
    return (value.isEmpty()) ? defaultValue : value;
  }

  /**
   * Gets the text of the current element of the DOM Document.
   */
  public String getText() {
    return getText(null);
  }

  /**
   * Gets the text of the current element of the DOM Document.
   */
  public String getText(String defaultValue) {
    if (current.getChildNodes().getLength() == 0) return defaultValue;

    StringBuilder buf = new StringBuilder();
    getText(current, buf);

    return buf.toString();
  }

  private static void getText(Node n, StringBuilder buf) {
    if (n.getNodeValue() != null) buf.append(n.getNodeValue());
    NodeList children = n.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      getText(children.item(i), buf);
    }
  }

  /**
   * Gets an attribute of the current element of the DOM Document and of
   * all parent DOM elements.
   */
  public java.util.List<String> getInheritedAttribute(String name) {
    LinkedList<String> values = new LinkedList<>();
    Node node = current;
    while (node != null) {
      String value = ((Element) node).getAttribute(name);
      values.addFirst(value);
      node = node.getParentNode();
    }
    return values;
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public int getAttribute(String name, int defaultValue) {
    String value = ((Element) current).getAttribute(name);
    return (value.isEmpty()) ? defaultValue : Long.decode(value).intValue();
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public double getAttribute(String name, double defaultValue) {
    String value = ((Element) current).getAttribute(name);
    return (value.isEmpty()) ? defaultValue : Double.parseDouble(value);
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public boolean getAttribute(String name, boolean defaultValue) {
    String value = ((Element) current).getAttribute(name);
    return (value.isEmpty()) ? defaultValue : Boolean.parseBoolean(value);
  }

  /**
   * Returns the number of child elements of the current element.
   */
  public int getElementCount() {
    int count = 0;
    NodeList list = current.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if ((node instanceof Element)) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns the number of child elements with the specified tag name
   * of the current element.
   */
  public int getElementCount(String tagName) {
    int count = 0;
    NodeList list = current.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if ((node instanceof Element) && ((Element) node).getTagName().equals(tagName)) {
        count++;
      }
    }
    return count;
  }

  /**
   * Opens the element with the specified index and makes it the current node.
   */
  public void openElement(int index) {
    int count = 0;
    NodeList list = current.getChildNodes();
    int len = list.getLength();
    for (int i = 0; i < len; i++) {
      Node node = list.item(i);
      if ((node instanceof Element)) {
        if (count++ == index) {
          current = node;
          return;
        }
      }
    }
  }

  /**
   * Opens the last element with the specified name and makes it the current node.
   */
  public void openElement(String tagName) {
    int count = 0;
    NodeList list = current.getChildNodes();
    int len = list.getLength();
    for (int i = 0; i < len; i++) {
      Node node = list.item(i);
      if ((node instanceof Element) && ((Element) node).getTagName().equals(tagName)) {
        current = node;
        return;
      }
    }
    throw new IllegalArgumentException("element not found:" + tagName);
  }

  /**
   * Opens the element with the specified name and index and makes it the
   * current node.
   */
  public void openElement(String tagName, int index) {
    int count = 0;
    NodeList list = current.getChildNodes();
    int len = list.getLength();
    for (int i = 0; i < len; i++) {
      Node node = list.item(i);
      if ((node instanceof Element) && ((Element) node).getTagName().equals(tagName)) {
        if (count++ == index) {
          current = node;
          return;
        }
      }
    }
    throw new IllegalArgumentException("no such child " + tagName + "[" + index + "]");
  }

  /**
   * Closes the current element of the DOM Document.
   * The parent of the current element becomes the current element.
   *
   * @throws IllegalArgumentException if the provided tagName does
   *                                  not match the tag name of the element.
   */
  public void closeElement() {
        /*
        if (! ((Element) current).getTagName().equals(tagName)) {
            throw new IllegalArgumentException("Attempt to close wrong element:"+tagName +"!="+((Element) current).getTagName());
        }*/
    current = current.getParentNode();
  }

  /**
   * Reads an object from the current element.
   */
  public Object readObject() throws IOException {
    return readObject(0);
  }

  /**
   * Reads an object from the current element.
   */
  public Object readObject(int index) throws IOException {
    openElement(index);
    Object o;

    String tagName = getTagName();
    if (tagName.equals("null")) {
      o = null;
    } else if (tagName.equals("string")) {
      o = getText();
    } else if (tagName.equals("int")) {
      o = Integer.decode(getText());
    } else if (tagName.equals("long")) {
      o = Long.decode(getText());
    } else if (tagName.equals("float")) {
      o = Float.parseFloat(getText());
    } else if (tagName.equals("double")) {
      o = Double.parseDouble(getText());
    } else if (tagName.equals("boolean")) {
      o = Boolean.valueOf(getText());
    } else if (tagName.equals("color")) {
      o = new Color(getAttribute("rgba", 0xff));
    } else if (tagName.equals("intArray")) {
      int[] a = new int[getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = (Integer) readObject(i);
      }
      o = a;
    } else if (tagName.equals("floatArray")) {
      float[] a = new float[getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = (Float) readObject(i);
      }
      o = a;
    } else if (tagName.equals("doubleArray")) {
      double[] a = new double[getElementCount()];
      for (int i = 0; i < a.length; i++) {
        a[i] = (Double) readObject(i);
      }
      o = a;
    } else if (tagName.equals("font")) {
      o = new Font(getAttribute("name", "Dialog"), getAttribute("style", 0), getAttribute("size", 0));
    } else if (tagName.equals("enum")) {
      o = factory.createEnum(getAttribute("type", null), getText());
    } else {
      String ref = getAttribute("ref", null);
      String id = getAttribute("id", ref);

      // Keep track of objects which have an ID
      if (id == null) {
        o = factory.create(getTagName());
      } else if (idObjects.containsKey(id)) {
        o = idObjects.get(id);
      } else {
        o = factory.create(getTagName());
        idObjects.put(id, o);
      }
      if (ref == null) {
        if (o instanceof DOMStorable) {
          ((DOMStorable) o).read(this);
        }
      }
    }

    closeElement();
    return o;
  }
}
