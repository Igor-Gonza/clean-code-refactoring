/*
 * @(#)DOMInput.java  1.0  10. März 2004
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

import java.io.IOException;

/**
 * DOMInput.
 *
 * @author Werner Randelshofer
 * @version 1.0 10. März 2004  Created.
 */
public interface DOMInput {

  /**
   * Returns the tag name of the current element.
   */
  String getTagName();

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  String getAttribute(String name, String defaultValue);

  /**
   * Gets the text of the current element of the DOM Document.
   */
  String getText();

  /**
   * Gets the text of the current element of the DOM Document.
   */
  String getText(String defaultValue);

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  int getAttribute(String name, int defaultValue);

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  double getAttribute(String name, double defaultValue);

  /**
   * Gets an attribute of the current element of the DOM Document.
   */
  boolean getAttribute(String name, boolean defaultValue);

  /**
   * Gets an attribute of the current element of the DOM Document and of
   * all parent DOM elements.
   */
  java.util.List<String> getInheritedAttribute(String name);

  /**
   * Returns the number of child elements of the current element.
   */
  int getElementCount();

  /**
   * Returns the number of child elements with the specified tag name
   * of the current element.
   */
  int getElementCount(String tagName);

  /**
   * Opens the element with the specified index and makes it the current node.
   */
  void openElement(int index) throws IOException;

  /**
   * Opens the last element with the specified name and makes it the current node.
   */
  void openElement(String tagName) throws IOException;

  /**
   * Opens the element with the specified name and index and makes it the
   * current node.
   */
  void openElement(String tagName, int index) throws IOException;

  /**
   * Closes the current element of the DOM Document.
   * The parent of the current element becomes the current element.
   *
   * @throws IllegalArgumentException if the provided tagName does
   *                                  not match the tag name of the element.
   */
  void closeElement();

  /**
   * Reads an object from the current element.
   */
  Object readObject() throws IOException;

  /**
   * Reads an object from the current element.
   */
  Object readObject(int index) throws IOException;
}
