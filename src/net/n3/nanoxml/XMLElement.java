/* XMLElement.java                                                 NanoXML/Java
 *
 * $Revision: 1.5 $
 * $Date: 2002/02/06 18:50:12 $
 * $Name: RELEASE_2_2_1 $
 *
 * This file is part of NanoXML 2 for Java.
 * Copyright (C) 2000-2002 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 */

package net.n3.nanoxml;

import java.io.Serializable;
import java.util.*;

/**
 * XMLElement is an XML element. The standard NanoXML builder generates a
 * tree of such iterator.
 *
 * @author Marc De Scheemaecker
 * @version $Name: RELEASE_2_2_1 $, $Revision: 1.5 $
 * @see net.n3.nanoxml.StdXMLBuilder
 */
@SuppressWarnings("unused")
public class XMLElement implements IXMLElement, Serializable {

  /**
   * Necessary for serialization.
   */
  private static final long serialVersionUID = -2383376380548624920L;

  /**
   * No line number defined.
   */
  public static final int NO_LINE = -1;

  /**
   * The parent element.
   */
  private IXMLElement parent;

  /**
   * The attributes of the element.
   */
  private final ArrayList<XMLAttribute> attributes;

  /**
   * The child iterator.
   */
  private final ArrayList<IXMLElement> children;

  /**
   * The name of the element.
   */
  private String name;

  /**
   * The full name of the element.
   */
  private String fullName;

  /**
   * The namespace URI.
   */
  private String namespace;

  /**
   * The content of the element.
   */
  private String content;

  /**
   * The system ID of the source data where this element is located.
   */
  private final String systemID;

  /**
   * The line in the source data where this element starts.
   */
  private final int lineNumber;

  /**
   * Creates an empty element to be used for #PCDATA content.
   */
  public XMLElement() {
    this(null, null, null, NO_LINE);
  }

  /**
   * Creates an empty element.
   *
   * @param fullName the name of the element.
   */
  public XMLElement(String fullName) {
    this(fullName, null, null, NO_LINE);
  }

  /**
   * Creates an empty element.
   *
   * @param fullName the name of the element.
   * @param systemID the system ID of the XML data where the element starts.
   * @param lineNumber   the line in the XML data where the element starts.
   */
  public XMLElement(String fullName, String systemID, int lineNumber) {
    this(fullName, null, systemID, lineNumber);
  }

  /**
   * Creates an empty element.
   *
   * @param fullName  the full name of the element
   * @param namespace the namespace URI.
   */
  public XMLElement(String fullName, String namespace) {
    this(fullName, namespace, null, NO_LINE);
  }

  /**
   * Creates an empty element.
   *
   * @param fullName  the full name of the element
   * @param namespace the namespace URI.
   * @param systemID  the system ID of the XML data where the element starts.
   * @param lineNumber    the line in the XML data where the element starts.
   */
  public XMLElement(String fullName, String namespace, String systemID, int lineNumber) {
    this.attributes = new ArrayList<>();
    this.children = new ArrayList<>(8);
    this.fullName = fullName;
    if (namespace == null) {
      this.name = fullName;
    } else {
      int index = fullName.indexOf(':');
      if (index >= 0) {
        this.name = fullName.substring(index + 1);
      } else {
        this.name = fullName;
      }
    }
    this.namespace = namespace;
    this.content = null;
    this.lineNumber = lineNumber;
    this.systemID = systemID;
    this.parent = null;
  }

  /**
   * Creates an element to be used for #PCDATA content.
   */
  public IXMLElement createPCDataElement() {
    return new XMLElement();
  }

  /**
   * Creates an empty element.
   *
   * @param fullName the name of the element.
   */
  public IXMLElement createElement(String fullName) {
    return new XMLElement(fullName);
  }

  /**
   * Creates an empty element.
   *
   * @param fullName the name of the element.
   * @param systemID the system ID of the XML data where the element starts.
   * @param lineNumber   the line in the XML data where the element starts.
   */
  public IXMLElement createElement(String fullName, String systemID, int lineNumber) {
    return new XMLElement(fullName, systemID, lineNumber);
  }

  /**
   * Creates an empty element.
   *
   * @param fullName  the full name of the element
   * @param namespace the namespace URI.
   */
  public IXMLElement createElement(String fullName, String namespace) {
    return new XMLElement(fullName, namespace);
  }

  /**
   * Creates an empty element.
   *
   * @param fullName  the full name of the element
   * @param namespace the namespace URI.
   * @param systemID  the system ID of the XML data where the element starts.
   * @param lineNumber    the line in the XML data where the element starts.
   */
  public IXMLElement createElement(String fullName, String namespace, String systemID, int lineNumber) {
    return new XMLElement(fullName, namespace, systemID, lineNumber);
  }

  /**
   * Returns the parent element. This method returns null for the root
   * element.
   */
  public IXMLElement getParent() {
    return this.parent;
  }

  /**
   * Returns the full name (i.e. the name including an eventual namespace
   * prefix) of the element.
   *
   * @return the name, or null if the element only contains #PCDATA.
   */
  public String getFullName() {
    return this.fullName;
  }

  /**
   * Returns the name of the element.
   *
   * @return the name, or null if the element only contains #PCDATA.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the namespace of the element.
   *
   * @return the namespace, or null if no namespace is associated with the
   * element.
   */
  public String getNamespace() {
    return this.namespace;
  }

  /**
   * Sets the full name. This method also sets the short name and clears the
   * namespace URI.
   *
   * @param name the non-null name.
   */
  public void setName(String name) {
    this.name = name;
    this.fullName = name;
    this.namespace = null;
  }

  /**
   * Sets the name.
   *
   * @param fullName  the non-null full name.
   * @param namespace the namespace URI, which may be null.
   */
  public void setName(String fullName, String namespace) {
    int index = fullName.indexOf(':');
    if ((namespace == null) || (index < 0)) {
      this.name = fullName;
    } else {
      this.name = fullName.substring(index + 1);
    }
    this.fullName = fullName;
    this.namespace = namespace;
  }

  /**
   * Adds a child element.
   *
   * @param child the non-null child to add.
   */
  public void addChild(IXMLElement child) {
    if (child == null) {
      throw new IllegalArgumentException("child must not be null");
    }
    if ((child.getName() == null) && (!this.children.isEmpty())) {
      IXMLElement lastChild = this.children.get(this.children.size() - 1);

      if (lastChild.getName() == null) {
        lastChild.setContent(lastChild.getContent() + child.getContent());
        return;
      }
    }
    ((XMLElement) child).parent = this;
    this.children.add(child);
  }

  /**
   * Inserts a child element.
   *
   * @param child the non-null child to add.
   * @param index where to put the child.
   */
  public void insertChild(IXMLElement child, int index) {
    if (child == null) {
      throw new IllegalArgumentException("child must not be null");
    }
    if ((child.getName() == null) && (!this.children.isEmpty())) {
      IXMLElement lastChild = this.children.get(this.children.size() - 1);
      if (lastChild.getName() == null) {
        lastChild.setContent(lastChild.getContent() + child.getContent());
        return;
      }
    }
    ((XMLElement) child).parent = this;
    this.children.add(index, child);
  }

  /**
   * Removes a child element.
   *
   * @param child the non-null child to remove.
   */
  public void removeChild(IXMLElement child) {
    if (child == null) {
      throw new IllegalArgumentException("child must not be null");
    }
    this.children.remove(child);
  }

  /**
   * Removes the child located at a certain index.
   *
   * @param index the index of the child, where the first child has index 0.
   */
  public void removeChildAtIndex(int index) {
    this.children.remove(index);
  }

  /**
   * Returns an enumeration of all child iterator.
   *
   * @return the non-null enumeration
   */
  public Iterator<IXMLElement> iterateChildren() {
    return this.children.iterator();
  }

  /**
   * Returns whether the element is a leaf element.
   *
   * @return true if the element has no children.
   */
  public boolean isLeaf() {
    return this.children.isEmpty();
  }

  /**
   * Returns whether the element has children.
   *
   * @return true if the element has children.
   */
  public boolean hasChildren() {
    return (!this.children.isEmpty());
  }

  /**
   * Returns the number of children.
   *
   * @return the count.
   */
  public int getChildrenCount() {
    return this.children.size();
  }

  /**
   * Returns a vector containing all the child iterator.
   *
   * @return the vector.
   */
  public ArrayList<IXMLElement> getChildren() {
    return this.children;
  }

  /**
   * Returns the child at a specific index.
   *
   * @param index the index of the child
   * @return the non-null child
   * @throws java.lang.ArrayIndexOutOfBoundsException if the index is out of bounds.
   */
  public IXMLElement getChildAtIndex(int index) throws ArrayIndexOutOfBoundsException {
    return this.children.get(index);
  }

  /**
   * Searches a child element.
   *
   * @param name the full name of the child to search for.
   * @return the child element, or null if no such child was found.
   */
  public IXMLElement getFirstChildNamed(String name) {
    for (IXMLElement child : this.children) {
      String childName = child.getFullName();
      if ((childName != null) && childName.equals(name)) {
        return child;
      }
    }
    return null;
  }

  /**
   * Searches a child element.
   *
   * @param name      the name of the child to search for.
   * @param namespace the namespace, which may be null.
   * @return the child element, or null if no such child was found.
   */
  public IXMLElement getFirstChildNamed(String name, String namespace) {
    for (IXMLElement child : this.children) {
      String str = child.getName();
      boolean found = (str != null) && (str.equals(name));
      str = child.getNamespace();
      if (str == null) {
        found &= (name == null);
      } else {
        found &= str.equals(namespace);
      }
      if (found) {
        return child;
      }
    }
    return null;
  }

  /**
   * Returns a vector of all child iterator named <I>name</I>.
   *
   * @param name the full name of the children to search for.
   * @return the non-null vector of child iterator.
   */
  public ArrayList<IXMLElement> getChildrenNamed(String name) {
    ArrayList<IXMLElement> result = new ArrayList<>(this.children.size());
    for (IXMLElement child : this.children) {
      String childName = child.getFullName();
      if ((childName != null) && childName.equals(name)) {
        result.add(child);
      }
    }
    return result;
  }

  /**
   * Returns a vector of all child iterator named <I>name</I>.
   *
   * @param name      the name of the children to search for.
   * @param namespace the namespace, which may be null.
   * @return the non-null vector of child iterator.
   */
  public ArrayList<IXMLElement> getChildrenNamed(String name, String namespace) {
    ArrayList<IXMLElement> result = new ArrayList<>(this.children.size());
    for (IXMLElement child : this.children) {
      String str = child.getName();
      boolean found = (str != null) && (str.equals(name));
      str = child.getNamespace();
      if (str == null) {
        found &= (name == null);
      } else {
        found &= str.equals(namespace);
      }

      if (found) {
        result.add(child);
      }
    }
    return result;
  }

  /**
   * Searches an attribute.
   *
   * @param fullName the non-null full name of the attribute.
   * @return the attribute, or null if the attribute does not exist.
   */
  private XMLAttribute findAttribute(String fullName) {
    for (XMLAttribute attr : this.attributes) {
      if (attr.getFullName().equals(fullName)) {
        return attr;
      }
    }
    return null;
  }

  /**
   * Searches an attribute.
   *
   * @param name      the non-null short name of the attribute.
   * @param namespace the name space, which may be null.
   * @return the attribute, or null if the attribute does not exist.
   */
  private XMLAttribute findAttribute(String name, String namespace) {
    for (XMLAttribute attr : this.attributes) {
      boolean found = attr.getName().equals(name);
      if (namespace == null) {
        found &= (attr.getNamespace() == null);
      } else {
        found &= namespace.equals(attr.getNamespace());
      }

      if (found) {
        return attr;
      }
    }
    return null;
  }

  /**
   * Returns the number of attributes.
   */
  public int getAttributeCount() {
    return this.attributes.size();
  }

  /**
   * @param name the non-null name of the attribute.
   * @return the value, or null if the attribute does not exist.
   * @deprecated As of NanoXML/Java 2.1, replaced by
   * {@link #getAttribute(java.lang.String, java.lang.String)}
   * Returns the value of an attribute.
   */
  public String getAttribute(String name) {
    return this.getAttribute(name, null);
  }

  /**
   * Returns the value of an attribute.
   *
   * @param name         the non-null full name of the attribute.
   * @param defaultValue the default value of the attribute.
   * @return the value, or defaultValue if the attribute does not exist.
   */
  public String getAttribute(String name, String defaultValue) {
    XMLAttribute attr = this.findAttribute(name);
    if (attr == null) {
      return defaultValue;
    } else {
      return attr.getValue();
    }
  }

  /**
   * Returns the value of an attribute.
   *
   * @param name         the non-null name of the attribute.
   * @param namespace    the namespace URI, which may be null.
   * @param defaultValue the default value of the attribute.
   * @return the value, or defaultValue if the attribute does not exist.
   */
  public String getAttribute(String name, String namespace, String defaultValue) {
    XMLAttribute attr = this.findAttribute(name, namespace);
    if (attr == null) {
      return defaultValue;
    } else {
      return attr.getValue();
    }
  }

  /**
   * Returns the value of an attribute.
   *
   * @param name         the non-null full name of the attribute.
   * @param defaultValue the default value of the attribute.
   * @return the value, or defaultValue if the attribute does not exist.
   */
  public int getAttribute(String name, int defaultValue) {
    String value = this.getAttribute(name, Integer.toString(defaultValue));
    return Integer.parseInt(value);
  }

  /**
   * Returns the value of an attribute.
   *
   * @param name         the non-null name of the attribute.
   * @param namespace    the namespace URI, which may be null.
   * @param defaultValue the default value of the attribute.
   * @return the value, or defaultValue if the attribute does not exist.
   */
  public int getAttribute(String name, String namespace, int defaultValue) {
    String value = this.getAttribute(name, namespace, Integer.toString(defaultValue));
    return Integer.parseInt(value);
  }

  /**
   * Returns the type of an attribute.
   *
   * @param name the non-null full name of the attribute.
   * @return the type, or null if the attribute does not exist.
   */
  public String getAttributeType(String name) {
    XMLAttribute attr = this.findAttribute(name);
    if (attr == null) {
      return null;
    } else {
      return attr.getType();
    }
  }

  /**
   * Returns the namespace of an attribute.
   *
   * @param name the non-null full name of the attribute.
   * @return the namespace, or null if there is none associated.
   */
  public String getAttributeNamespace(String name) {
    XMLAttribute attr = this.findAttribute(name);
    if (attr == null) {
      return null;
    } else {
      return attr.getNamespace();
    }
  }

  /**
   * Returns the type of an attribute.
   *
   * @param name      the non-null name of the attribute.
   * @param namespace the namespace URI, which may be null.
   * @return the type, or null if the attribute does not exist.
   */
  public String getAttributeType(String name, String namespace) {
    XMLAttribute attr = this.findAttribute(name, namespace);
    if (attr == null) {
      return null;
    } else {
      return attr.getType();
    }
  }

  /**
   * Sets an attribute.
   *
   * @param name  the non-null full name of the attribute.
   * @param value the non-null value of the attribute.
   */
  public void setAttribute(String name, String value) {
    XMLAttribute attr = this.findAttribute(name);
    if (attr == null) {
      attr = new XMLAttribute(name, name, null, value, "CDATA");
      this.attributes.add(attr);
    } else {
      attr.setValue(value);
    }
  }

  /**
   * Sets an attribute.
   *
   * @param fullName  the non-null full name of the attribute.
   * @param namespace the namespace URI of the attribute, which may be null.
   * @param value     the non-null value of the attribute.
   */
  public void setAttribute(String fullName, String namespace, String value) {
    int index = fullName.indexOf(':');
    String name = fullName.substring(index + 1);
    XMLAttribute attr = this.findAttribute(name, namespace);
    if (attr == null) {
      attr = new XMLAttribute(fullName, name, namespace, value, "CDATA");
      this.attributes.add(attr);
    } else {
      attr.setValue(value);
    }
  }

  /**
   * Removes an attribute.
   *
   * @param name the non-null name of the attribute.
   */
  public void removeAttribute(String name) {
    for (int i = 0; i < this.attributes.size(); i++) {
      XMLAttribute attr = this.attributes.get(i);
      if (attr.getFullName().equals(name)) {
        this.attributes.remove(i);
        return;
      }
    }
  }

  /**
   * Removes an attribute.
   *
   * @param name      the non-null name of the attribute.
   * @param namespace the namespace URI of the attribute, which may be null.
   */
  public void removeAttribute(String name, String namespace) {
    for (int i = 0; i < this.attributes.size(); i++) {
      XMLAttribute attr = this.attributes.get(i);
      boolean found = attr.getName().equals(name);
      if (namespace == null) {
        found &= (attr.getNamespace() == null);
      } else {
        found &= attr.getNamespace().equals(namespace);
      }

      if (found) {
        this.attributes.remove(i);
        return;
      }
    }
  }

  /**
   * Returns an enumeration of all attribute names.
   *
   * @return the non-null enumeration.
   */
  public Iterator<String> iterateAttributeNames() {
    ArrayList<String> result = new ArrayList<>();
    for (XMLAttribute attr : this.attributes) {
      result.add(attr.getFullName());
    }
    return result.iterator();
  }

  /**
   * Returns whether an attribute exists.
   *
   * @return true if the attribute exists.
   */
  public boolean hasAttribute(String name) {
    return this.findAttribute(name) != null;
  }

  /**
   * Returns whether an attribute exists.
   *
   * @return true if the attribute exists.
   */
  public boolean hasAttribute(String name, String namespace) {
    return this.findAttribute(name, namespace) != null;
  }

  /**
   * Returns all attributes as a Properties object.
   *
   * @return the non-null set.
   */
  public Properties getAttributes() {
    Properties result = new Properties();
    for (XMLAttribute attr : this.attributes) {
      result.put(attr.getFullName(), attr.getValue());
    }
    return result;
  }

  /**
   * Returns all attributes in a specific namespace as a Properties object.
   *
   * @param namespace the namespace URI of the attributes, which may be null.
   * @return the non-null set.
   */
  public Properties getAttributesInNamespace(String namespace) {
    Properties result = new Properties();
    for (XMLAttribute attr : this.attributes) {
      if (namespace == null) {
        if (attr.getNamespace() == null) {
          result.put(attr.getName(), attr.getValue());
        }
      } else {
        if (namespace.equals(attr.getNamespace())) {
          result.put(attr.getName(), attr.getValue());
        }
      }
    }
    return result;
  }

  /**
   * Returns the system ID of the data where the element started.
   *
   * @return the system ID, or null if unknown.
   * @see #getLineNr
   */
  public String getSystemID() {
    return this.systemID;
  }

  /**
   * Returns the line number in the data where the element started.
   *
   * @return the line number, or NO_LINE if unknown.
   * @see #NO_LINE
   * @see #getSystemID
   */
  public int getLineNr() {
    return this.lineNumber;
  }

  /**
   * Return the #PCDATA content of the element. If the element has a
   * combination of #PCDATA content and child iterator, the #PCDATA
   * sections can be retrieved as unnamed child objects. In this case,
   * this method returns null.
   *
   * @return the content.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Sets the #PCDATA content. It is an error to call this method with a
   * non-null value if there are child objects.
   *
   * @param content the (possibly null) content.
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Returns true if the element equals another element.
   *
   * @param rawElement the element to compare to
   */
  public boolean equals(Object rawElement) {
    try {
      return this.equalsXMLElement((IXMLElement) rawElement);
    } catch (ClassCastException e) {
      return false;
    }
  }

  /**
   * Returns true if the element equals another element.
   *
   * @param elt the element to compare to
   */
  public boolean equalsXMLElement(IXMLElement elt) {
    if (!this.name.equals(elt.getName())) {
      return false;
    }
    if (this.attributes.size() != elt.getAttributeCount()) {
      return false;
    }
    for (XMLAttribute attr : this.attributes) {
      if (!elt.hasAttribute(attr.getName(), attr.getNamespace())) {
        return false;
      }
      String value = elt.getAttribute(attr.getName(), attr.getNamespace(), null);
      if (!attr.getValue().equals(value)) {
        return false;
      }
      String type = elt.getAttributeType(attr.getName(), attr.getNamespace());
      if (!attr.getType().equals(type)) {
        return false;
      }
    }
    if (this.children.size() != elt.getChildrenCount()) {
      return false;
    }
    for (int i = 0; i < this.children.size(); i++) {
      IXMLElement child1 = this.getChildAtIndex(i);
      IXMLElement child2 = elt.getChildAtIndex(i);

      if (!child1.equalsXMLElement(child2)) {
        return false;
      }
    }
    return true;
  }

}
