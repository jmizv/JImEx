/*
 * JITreeCellEditorListener.java
 *
 * Created on May 2, 2005, 8:45 PM
 */

package de.jmizv.jiexplorer.gui.tree;

import java.io.File;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author rem
 */
public class JITreeCellEditorListener implements CellEditorListener {
	//private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JITreeCellEditorListener.class);

	private final JIDirTree adaptee;

	/** Creates a new instance of JITreeCellEditorListener */
	public JITreeCellEditorListener(final JIDirTree adaptee) {
		this.adaptee = adaptee;
	}

	public void editingStopped(final ChangeEvent e) {
		if ( this.adaptee.getEditingDir() != null ) {
			final String newName = this.adaptee.editor.getCellEditorValue().toString();
			final File dir = this.adaptee.getEditingDir();
			if (!newName.trim().equals("")) {
				final File newDir = new File(dir.getParentFile(), newName);
				if (!newDir.exists()) {
					dir.renameTo(newDir);

					this.adaptee.updateSelectionPath(newDir);;
				} else {
					// return the original Diskobject to the node
					// this needs to be done because the cellEditor serializes
					// the DiskObject.
					this.adaptee.updateSelectionPath(dir);
				}
			} else {
				// return the original Diskobject to the node
				// this needs to be done because the cellEditor serializes
				// the DiskObject.
				this.adaptee.updateSelectionPath(dir);
			}
		}
		this.adaptee.setEditingDir(null);
	}

	public void editingCanceled(final ChangeEvent e) {
		this.adaptee.setEditingDir(null);
	}
}
