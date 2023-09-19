package de.jmizv.jiexplorer.gui.cattree;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


public class JICatTreeNode extends DefaultMutableTreeNode {

    //private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CatTreeNode.class);

    /**
     *
     */
    private static final long serialVersionUID = -4803026780159592417L;

    //private JICatTreeNode parent;
    //private Vector<JICatTreeNode> children;
    private boolean explored = false;

    private String categoryID;

    // Assocoates a file object with this node.
    public JICatTreeNode(final String keyword) {
        setUserObject(keyword);
    }

    public JICatTreeNode(final String keyword, final String categoryID) {
        setUserObject(keyword);
        this.categoryID = categoryID;
    }

    @Override
    public JICatTreeNode getParent() {
        return (JICatTreeNode) this.parent;
    }

    public String getCategoryID() {
        return this.categoryID;
    }

    public void setcategoryID(final String categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        explore();
        if (this.getChildCount() > 0) {
            return false;
        }
        return true;
    }

    public String getCategoryPath() {
        //Required to handle updateds from TreeCellEditor
        final Object obj = getUserObject();
        return (String) obj;
    }

    public boolean isExplored() {
        return this.explored;
    }

    public void refresh() {
        this.explored = false;
        this.removeAllChildren();
        explore();
    }

    public void explore() {
        if (!isExplored()) {
            final JICatTreeNode[] childnodes = JIThumbnailService.getInstance().exploreCategoryTreeNodeIDs((String) getUserObject(), getCategoryID());

            if (childnodes != null) {
                for (final JICatTreeNode element : childnodes) {
                    add(element);
                }
            }
            this.explored = true;
        }
    }

    public void deleteNode() {
        if (!isExplored()) {
            explore();
            this.explored = true;
        }

        if (this.children != null) {
            for (final Enumeration<TreeNode> e = this.children.elements(); e.hasMoreElements(); ) {
                TreeNode treeNode = e.nextElement();
                if (treeNode instanceof JICatTreeNode) {
                    ((JICatTreeNode) treeNode).deleteNode();
                } else {
                    throw new IllegalArgumentException("Expected JICatTreeNode but was " + treeNode.getClass().getSimpleName());
                }
            }
        }
        JIThumbnailService.getInstance().deleteCategoryTreeNode(this);
    }

    public Vector<String> getNodeIDs() {
        final Vector<String> subNodeDIs = new Vector<String>();

        if (!isExplored()) {
            explore();
            this.explored = true;
        }

        subNodeDIs.add(this.getCategoryID());
        if ((this.children != null) && !this.children.isEmpty() && JIPreferences.getInstance().isCategoryIncludeSubs()) {
            for (final Enumeration<TreeNode> e = this.children.elements(); e.hasMoreElements(); ) {
                TreeNode treeNode = e.nextElement();
                if (treeNode instanceof JICatTreeNode) {
                    subNodeDIs.addAll(((JICatTreeNode) treeNode).getNodeIDs());
                } else {
                    throw new IllegalArgumentException("Expected JICatTreeNode but was " + treeNode.getClass().getSimpleName());
                }
            }
        }
        return subNodeDIs;
    }

    public Vector<String> getSubNodeIDs() {
        final Vector<String> subNodeDIs = new Vector<String>();

        if (!isExplored()) {
            explore();
            this.explored = true;
        }

        if (this.children != null) {
            for (final Enumeration<TreeNode> e = this.children.elements(); e.hasMoreElements(); ) {
                TreeNode treeNode = e.nextElement();
                if (treeNode instanceof JICatTreeNode) {
                    subNodeDIs.addAll(((JICatTreeNode) treeNode).getSubNodeIDs());
                } else {
                    throw new IllegalArgumentException("Expected JICatTreeNode but was " + treeNode.getClass().getSimpleName());
                }
            }
            for (final Enumeration<TreeNode> e = this.children.elements(); e.hasMoreElements(); ) {
                TreeNode treeNode = e.nextElement();
                if (treeNode instanceof JICatTreeNode) {
                    subNodeDIs.add(((JICatTreeNode) treeNode).getCategoryID());
                } else {
                    throw new IllegalArgumentException("Expected JICatTreeNode but was " + treeNode.getClass().getSimpleName());
                }
            }
        }
        return subNodeDIs;
    }

    public Vector<String> getParentNodeIDs() {
        final Vector<String> parentNodeDIs = new Vector<String>();

        if (!isExplored()) {
            explore();
            this.explored = true;
        }

        if (this.parent != null) {
            parentNodeDIs.addAll(((JICatTreeNode) this.parent).getParentNodeIDs());
            parentNodeDIs.add(((JICatTreeNode) this.parent).getCategoryID());
        }
        return parentNodeDIs;
    }

    public boolean isSelected() {
        return false;
    }

    @Override
    public String toString() {
        final String str = (String) getUserObject();
        final int lastIndex = str.lastIndexOf(":");
        if (lastIndex < 0) {
            return str;
        } else {
            return str.substring(lastIndex + 1);
        }
    }

//	@Override
//	public Enumeration<JICatTreeNode> children() {
//		return children.elements();
//	}
}
