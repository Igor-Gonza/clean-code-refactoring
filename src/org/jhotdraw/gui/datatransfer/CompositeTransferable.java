/*
 * @(#)CompositeTransferable.java  1.0  2002-04-07
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

package org.jhotdraw.gui.datatransfer;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Werner Randelshofer
 */
public class CompositeTransferable implements Transferable, ClipboardOwner {
  private final HashMap transferables = new HashMap<>();
  private final LinkedList flavors = new LinkedList<>();

  /**
   * Creates a new instance of CompositeTransferable
   */
  public CompositeTransferable() {
  }

  public void add(Transferable t) {
    DataFlavor[] f = t.getTransferDataFlavors();
    for (DataFlavor dataFlavor : f) {
      if (!transferables.containsKey(dataFlavor)) flavors.add(dataFlavor);
      transferables.put(dataFlavor, t);
    }
  }

  /**
   * Returns an object which represents the data to be transferred.  The class
   * of the object returned is defined by the representation class of the flavor.
   *
   * @param flavor the requested flavor for the data
   * @throws IOException                if the data is no longer available
   *                                    in the requested flavor.
   * @throws UnsupportedFlavorException if the requested data flavor is
   *                                    not supported.
   * @see DataFlavor#getRepresentationClass
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    Transferable t = (Transferable) transferables.get(flavor);
    if (t == null) throw new UnsupportedFlavorException(flavor);
    return t.getTransferData(flavor);
  }

  /**
   * Returns an array of DataFlavor objects indicating the flavors the data
   * can be provided in.  The array should be ordered according to preference
   * for providing the data (from most richly descriptive to least descriptive).
   *
   * @return an array of data flavors in which this data can be transferred
   */
  public DataFlavor[] getTransferDataFlavors() {
    return (DataFlavor[]) flavors.toArray(new DataFlavor[transferables.size()]);
  }

  /**
   * Returns whether the specified data flavor is supported for
   * this object.
   *
   * @param flavor the requested flavor for the data
   * @return boolean indicating whether the data flavor is supported
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return transferables.containsKey(flavor);
  }

  public void lostOwnership(Clipboard clipboard, Transferable contents) {
  }
}
