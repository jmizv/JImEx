package de.jmizv.jiexplorer.gui.cattree;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.tree.JITreeCellEditorListener;


public class JICatTreeCellEditorListener implements CellEditorListener {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JITreeCellEditorListener.class);
	private final JICatTree adaptee;

	/** Creates a new instance of JITreeCellEditorListener */
	public JICatTreeCellEditorListener(final JICatTree adaptee) {
		this.adaptee = adaptee;
	}

	public void editingStopped(final ChangeEvent e) {
		if (this.adaptee.getEditingCategory() != null) {
			final String newName = this.adaptee.editor.getCellEditorValue().toString();
			String categoryPath = this.adaptee.getEditingCategory();
			if (!newName.trim().equals("")) {

				log.debug("newName = " + newName);
				final JICatTreeNode selectedTreeNode = (JICatTreeNode)this.adaptee.getSelectionPath().getLastPathComponent();
				if (!JIThumbnailService.getInstance().categoryExists(selectedTreeNode, newName)) {
					JIThumbnailService.getInstance().updateCategoryTreeNode(selectedTreeNode, newName);
					categoryPath = newName;
				}
			}
			this.adaptee.updateSelectionPath(categoryPath);
			((JICatTreeNode)this.adaptee.getSelectionPath().getLastPathComponent()).explore();

		}
		this.adaptee.setEditingDir(null);
	}

	public void editingCanceled(final ChangeEvent e) {
		this.adaptee.setEditingDir(null);
	}
}
