/*
 * @(#)NanoXMLDOMInput.java  2.1.1  2006-08-21
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

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.awt.*;

import net.n3.nanoxml.*;

/**
 * NanoXMLDOMInput.
 *
 * @author Werner Randelshofer
 * @version 2.1.1 2006-08-21 Fixed exceptions.
 * <br>2.0 2006-06-10 Support for Enum and double array objects added.
 * <br>1.0 February 17, 2004, Created.
 */
public class NanoXMLDOMInput implements DOMInput {
  /**
   * This map is used to unmarshall references to objects to
   * the XML DOM. A key in this map is a String representing a marshalled
   * reference. A value in this map is an unmarshalled Object.
   */
  private final HashMap<String, Object> idObjects = new HashMap<>();

  /**
   * The current node used for input.
   */
  private XMLElement current;

  /**
   * The factory used to create objects from XML tag names.
   */
  private DOMFactory factory;

  /**
   * The stack.
   */
  private final Stack<XMLElement> stack = new Stack<>();

  public NanoXMLDOMInput(DOMFactory factory, InputStream in) throws IOException {
    this(factory, new InputStreamReader(in, StandardCharsets.UTF_8));
  }

  public NanoXMLDOMInput(DOMFactory factory, Reader in) throws IOException {
    this.factory = factory;

    try {

      IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
      IXMLReader reader = new StdXMLReader(in);
      parser.setReader(reader);
      /*
       * The document used for input.
       */
      XMLElement document = new XMLElement();
      current = (XMLElement) parser.parse();
      document.addChild(current);
      current = document;
    } catch (Exception e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  /**
   * Returns the tag name of the current element.
   */
  public String getTagName() {
    return current.getName();
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public String getAttribute(String name, String defaultValue) {
    String value = current.getAttribute(name);
    return (value == null || value.isEmpty()) ? defaultValue : value;
  }

  /**
   * Gets an attribute of the current element of the DOM Document and of
   * all parent DOM elements.
   */
  public java.util.List<String> getInheritedAttribute(String name) {
    LinkedList<String> values = new LinkedList<>();
    for (XMLElement node : stack) {
      String value = node.getAttribute(name);
      values.add(value);
    }
    String value = current.getAttribute(name);
    values.add(value);
    return values;
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
    String value = current.getContent();
    return (value == null) ? defaultValue : value;
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public int getAttribute(String name, int defaultValue) {
    String value = current.getAttribute(name);
    return (value == null || value.isEmpty()) ? defaultValue : Long.decode(value).intValue();
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public double getAttribute(String name, double defaultValue) {
    String value = current.getAttribute(name);
    return (value == null || value.isEmpty()) ? defaultValue : Double.parseDouble(value);
  }

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  public boolean getAttribute(String name, boolean defaultValue) {
    String value = current.getAttribute(name);
    return (value == null || value.isEmpty()) ? defaultValue : Boolean.parseBoolean(value);
  }

  /**
   * Returns the number of child elements of the current element.
   */
  public int getElementCount() {
    return current.getChildrenCount();
  }

  /**
   * Returns the number of child elements with the specified tag name
   * of the current element.
   */
  public int getElementCount(String tagName) {
    int count = 0;
    ArrayList list = current.getChildren();
    for (Object o : list) {
      XMLElement node = (XMLElement) o;
      if (node.getName().equals(tagName)) {
        count++;
      }
    }
    return count;
  }

  /**
   * Opens the element with the specified index and makes it the current node.
   */
  public void openElement(int index) {
    stack.push(current);
    ArrayList list = current.getChildren();
    current = (XMLElement) list.get(index);
  }

  /**
   * Opens the last element with the specified name and makes it the current node.
   */
  public void openElement(String tagName) throws IOException {
    ArrayList list = current.getChildren();
    for (Object o : list) {
      XMLElement node = (XMLElement) o;
      if (node.getName().equals(tagName)) {
        stack.push(current);
        current = node;
        return;
      }
    }
    throw new IOException("no such element:" + tagName);
  }

  /**
   * Opens the element with the specified name and index and makes it the
   * current node.
   */
  public void openElement(String tagName, int index) throws IOException {
    int count = 0;
    ArrayList list = current.getChildren();
    for (Object o : list) {
      XMLElement node = (XMLElement) o;
      if (node.getName().equals(tagName) && count++ == index) {
        stack.push(current);
        current = node;
        return;
      }

    }
    throw new IOException("no such element:" + tagName + " at index:" + index);
  }

  /**
   * Closes the current element of the DOM Document.
   * The parent of the current element becomes the current element.
   *
   * @throws IllegalArgumentException if the provided tagName does
   *                                  not match the tag name of the element.
   */
  public void closeElement() {
    current = stack.pop();
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

      if (ref == null && o instanceof DOMStorable) {
        ((DOMStorable) o).read(this);
      }

    }

    closeElement();
    return o;
  }
}
