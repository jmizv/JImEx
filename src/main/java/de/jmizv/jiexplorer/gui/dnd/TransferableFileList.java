/*
 * Created on Apr 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.jmizv.jiexplorer.gui.dnd;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Vector;


public class TransferableFileList<T> extends Vector<File> implements
		Transferable, ClipboardOwner {
	//private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TransferableFileList.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 428771818684443607L;

	final static int FILE = 0;

	final static int STRING = 1;

	DataFlavor flavors[] = { DataFlavor.javaFileListFlavor,
			DataFlavor.stringFlavor };

	private int actionType = 0x1;

	public TransferableFileList() {
	}

	/* Returns the array of flavors in which it can provide the data. */
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return this.flavors;
	}

	/* Returns whether the requested flavor is supported by this object. */
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		boolean b = false;
		b |= flavor.equals(this.flavors[FILE]);
		b |= flavor.equals(this.flavors[STRING]);
		return (b);
	}

	/**
	 * If the data was requested in the "java.lang.String" flavor, return the
	 * String representing the selection.
	 */
	public synchronized Object getTransferData(final DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(this.flavors[FILE])) {
			return this;
		} else if (flavor.equals(this.flavors[STRING])) {
			return (elementAt(0)).getAbsolutePath();
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public void lostOwnership(final Clipboard clipboard,
			final Transferable contents) {
		//log.debug("LostOwnership"+ contents);
	}

	public int getAction() {
		return this.actionType;
	}

	public void setCopy() {
		this.actionType = 0x1;
	}

	public void setMove() {
		this.actionType = 0x2;
	}
}
