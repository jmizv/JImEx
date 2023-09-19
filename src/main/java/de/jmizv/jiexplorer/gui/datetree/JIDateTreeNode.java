package de.jmizv.jiexplorer.gui.datetree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import de.jmizv.jiexplorer.db.JIThumbnailService;


public class JIDateTreeNode  extends DefaultMutableTreeNode {

	/**
	 *
	 */
	private static final long serialVersionUID = -3455797927986604715L;
	public static final int ROOT=-1;
	public static final int YEAR=0;
	public static final int MONTH=1;
	public static final int DAY=2;
	public static final int IMAGE=3;

	private boolean explored = false;

	private int type;
	private long date;

	// Assocoates a file object with this node.
	public JIDateTreeNode(final String keyword) {
		setUserObject(keyword);
	}

	public JIDateTreeNode(final String keyword, final int type) {
		setUserObject(keyword);
		this.type = type;
	}

	public JIDateTreeNode(final String keyword, final int type, final long date) {
		setUserObject(keyword);
		this.type = type;
		this.date = date;
	}

	@Override
	public JIDateTreeNode getParent() {
		return (JIDateTreeNode)this.parent;
	}

	public int getType() {
		return this.type;
	}

	public void setType(final int type) {
		this.type = type;
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		if (this.type > MONTH) {
			return true;
		}
		return false;
	}

	public String getCategoryPath() {
		//Required to handle updateds from TreeCellEditor
		final Object obj = getUserObject();
		return (String)obj;
	}

	public Vector getChildren() {
		return this.children;
	}

	public boolean isExplored() {
		return this.explored;
	}

	public void refresh() {
		this.explored = false;
		explore();
	}

	public void explore() {
		if (!this.explored) {
			this.removeAllChildren();
			if (this.type == ROOT) {
				final Vector<JIDateTreeNode> c = JIThumbnailService.getInstance().getImageYears();
				for (final JIDateTreeNode node: c) {
					if (node.userObject != null) {
						add(node);
					}
				}
			} else if (this.type == YEAR) {
				final Vector<JIDateTreeNode> c = JIThumbnailService.getInstance().getImageYearMonths((String)getUserObject());
				for (final JIDateTreeNode node: c) {
					add(node);
				}
			} else if (this.type == MONTH) {
				final Vector<JIDateTreeNode> c = JIThumbnailService.getInstance().getImageYearMonthDays((String)(getParent().getUserObject()),(String)getUserObject());
				for (final JIDateTreeNode node: c) {
					add(node);
				}
			}
		}
		this.explored = true;
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
			return str.substring(lastIndex+1);
		}
	}

	/**
	 * @return the date
	 */
	public final long getDate() {
		return this.date;
	}

	public String getDisplayFormat() {
		final String result = ((this.type == YEAR)?toString():getParent().getDisplayFormat()+"-"+toString());
		return result;
	}
}
