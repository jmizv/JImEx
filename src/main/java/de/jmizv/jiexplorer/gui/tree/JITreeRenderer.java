package de.jmizv.jiexplorer.gui.tree;


import java.awt.Component;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.jmizv.jiexplorer.util.JIUtility;



public class JITreeRenderer extends DefaultTreeCellRenderer {

   /**
	 *
	 */
	private static final long serialVersionUID = 1112879549647523382L;

@Override
public Component getTreeCellRendererComponent(
         final JTree tree, final Object value, final boolean isSelected, final boolean isExpanded,
         final boolean leaf, final int row, final boolean hasFocus) {
      final Component component = super.getTreeCellRendererComponent(tree, value,
            isSelected, isExpanded, leaf, row, hasFocus);

      if ((value != null) && (value instanceof JITreeNode)) {
         final JITreeNode treeNode = (JITreeNode) value;

         final File selectedDir = (File) treeNode.getUserObject();

         try {
            setIcon(JIUtility.getSystemIcon(selectedDir));
            setText(treeNode.toString());
         } catch (final Exception exp) {
            //exp.printStackTrace();
         }
      }

      return component;
   }
}
