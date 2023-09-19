package de.jmizv.jiexplorer.gui.cattree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class JICatTreeCellRenderer extends DefaultTreeCellRenderer {

	/**
	 *
	 */
	private static final long serialVersionUID = -3850022427865221850L;

	@Override
	public Component getTreeCellRendererComponent(
			final JTree tree, final Object value, final boolean isSelected, final boolean isExpanded,
			final boolean leaf, final int row, final boolean hasFocus) {
		final Component component = super.getTreeCellRendererComponent(tree, value,
				isSelected, isExpanded, leaf, row, hasFocus);

		if ((value != null) && (value instanceof JICatTreeNode)) {
			final JICatTreeNode treeNode = (JICatTreeNode) value;

			setText(treeNode.toString());
		}

		return component;
	}
}
