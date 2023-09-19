package de.jmizv.jiexplorer.gui.datetree;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


public class JIDateTree extends JTree {
	/**
	 *
	 */
	private static final long serialVersionUID = -3352977634380001680L;

	//private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIDateTree.class);
	protected static Icon folderIcon = FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.home")));

	protected DefaultTreeModel      model;
	protected JTextField            display;
	protected String                editingCategory;
	protected TreePath              clickedPath;
	protected JIDateTreeNode        selectedTreeNode;

	public JIDateTree() {
		this(createDateTree());
	}

	public JIDateTree(final DefaultTreeModel model) {
		super(model);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		this.model = model;

		putClientProperty("JTree.lineStyle", "Angled");
		setEditable(false);

		setSelectionRow(0);
		setBackground(Color.white);
		setAlignmentX((float) 0.5);
		setShowsRootHandles(false);
		setDragEnabled(false);

		final DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();
		dtcr.setLeafIcon(dtcr.getClosedIcon());
		setCellRenderer(dtcr);


		addTreeExpansionListener(new TreeExpansionListener(){
			public final void treeExpanded(final TreeExpansionEvent e) {
				final Thread runner = new Thread() {
					@Override
					public void run() {
						Runnable runnable = new Runnable() {
							public void run() {
								DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
								TreePath newPath = new TreePath(treeModel.getPathToRoot(JIDateTree.this.selectedTreeNode));

								setSelectionPath(newPath);
							}
						};
						SwingUtilities.invokeLater(runnable);
					}
				};
				runner.start();

			}

			public final void treeCollapsed(final TreeExpansionEvent e) {
			}
		});

		addTreeWillExpandListener(new TreeWillExpandListener() {
			public final void treeWillExpand(final TreeExpansionEvent e) throws ExpandVetoException {

				final TreePath path = e.getPath();

				JIDateTree.this.selectedTreeNode = (JIDateTreeNode) path.getLastPathComponent();

				if (!JIDateTree.this.selectedTreeNode.isExplored()) {
					JIDateTree.this.selectedTreeNode.explore();
				}
			}

			public final void treeWillCollapse(final TreeExpansionEvent e) {
			}
		});
	}

	public static final DefaultTreeModel createDateTree() {
		JIDateTreeNode rootNode = null;

		rootNode = new JIDateTreeNode("Dates",-1);
		//JIThumbnailDB.getInstance().getImageDates(rootNode);
		return new DefaultTreeModel(rootNode);
	}

	@Override
	public boolean isPathEditable(final TreePath path) {
		return false;
	}

	public DefaultMutableTreeNode getTreeNode(final TreePath path) {
		return (JIDateTreeNode)(path.getLastPathComponent());
	}

	public JIDateTreeNode getSelectedNode() {
		final TreePath path = getSelectionPath();
		return (JIDateTreeNode)path.getLastPathComponent();
	}

	public void updateSelectionPath(final String strPath) {
		final TreePath path = getSelectionPath();
		final JIDateTreeNode node = (JIDateTreeNode)path.getLastPathComponent();
		node.setUserObject(strPath);
		getTreeModel().nodeStructureChanged(node);
	}

	public void refresh() {
		((JIDateTreeNode)this.model.getRoot()).removeAllChildren();
		((JIDateTreeNode)this.model.getRoot()).refresh();
		getTreeModel().reload(((JIDateTreeNode)this.model.getRoot()));
	}

	public final JIDateTreeNode findChildNode(final JIDateTreeNode parentTreeNode, final File selectedSubDir) {
		if (!parentTreeNode.isExplored()) {
			parentTreeNode.explore();
		}

		final int count = this.getModel().getChildCount(parentTreeNode);

		for (int i = 0; i < count; i++) {
			final Object oneChild = this.getModel().getChild(parentTreeNode, i);

			if (oneChild instanceof JIDateTreeNode) {
				final File file = (File) ((JIDateTreeNode) oneChild).getUserObject();

				if (file.equals(selectedSubDir)) {
					return (JIDateTreeNode) oneChild;
				}
			}
		}
		return null;
	}

	public Vector<JIDateTreeNode> getSelectedNodes() {
		final Vector<JIDateTreeNode> selectedNodes = new Vector<JIDateTreeNode>();
		final DefaultTreeSelectionModel dtsm = (DefaultTreeSelectionModel)this.getSelectionModel();
		final TreePath[] paths = dtsm.getSelectionPaths();

		if ((paths != null) && (paths.length > 0)) {
			for (final TreePath element : paths) {
				selectedNodes.add((JIDateTreeNode)getTreeNode(element));
			}
		}

		return selectedNodes;
	}

	public TreePath getClickedPath() {
		return this.clickedPath;
	}

	public void setClickedPath(final TreePath clickedPath) {
		this.clickedPath = clickedPath;
	}

	public DefaultTreeModel getTreeModel() {
		return this.model;
	}

	public void setTreeModel(final DefaultTreeModel model) {
		this.model = model;
	}
}
