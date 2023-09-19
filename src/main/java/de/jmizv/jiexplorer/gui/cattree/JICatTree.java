package de.jmizv.jiexplorer.gui.cattree;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.jmizv.jiexplorer.db.JIThumbnailService;


public class JICatTree extends JTree {
	/**
	 *
	 */
	private static final long serialVersionUID = 3397576704013061591L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JICatTree.class);

	protected DefaultTreeModel      model;
	protected JTextField            display;
	protected DefaultTreeCellEditor editor;
	protected String                editingCategory;
	protected JPopupMenu            popup;
	protected TreePath              clickedPath;
	protected JMenuItem             jMenuItemSearch;
	protected JMenuItem             jMenuItemExpand;
//	protected JMenuItem             jMenuItemIncludeSubs;
	protected JICatTreeNode         selectedTreeNode;

	public JICatTree() {
		this(createCategoryTree());
	}

	public JICatTree(final DefaultTreeModel model) {
		super(model);

		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		this.model = model;

		setSelectionRow(0);
		setBackground(Color.white);
		setAlignmentX((float) 0.5);
		setShowsRootHandles(false);
		setDragEnabled(false);

		putClientProperty("JTree.lineStyle", "Angled");

		final JICatTreeCellRenderer renderer = new JICatTreeCellRenderer();
		setCellRenderer(renderer);
		final JICatTreeCellEditorListener cel = new JICatTreeCellEditorListener(this);

		this.editor = new DefaultTreeCellEditor(this, renderer);
		this.editor.addCellEditorListener(cel);
		setCellEditor(this.editor);
		setEditable(true);

		final DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();
		dtcr.setLeafIcon(dtcr.getClosedIcon());
		setCellRenderer(dtcr);
		initPopMenu();

		putClientProperty("JTree.lineStyle", "Angled");

		addTreeExpansionListener(new TreeExpansionListener(){
			public final void treeExpanded(final TreeExpansionEvent e) {
				final Thread runner = new Thread() {
					@Override
					public void run() {
						Runnable runnable = new Runnable() {
							public void run() {
								DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
								TreePath newPath = new TreePath(treeModel.getPathToRoot(JICatTree.this.selectedTreeNode));

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

				JICatTree.this.selectedTreeNode = (JICatTreeNode) path.getLastPathComponent();

				if (!JICatTree.this.selectedTreeNode.isExplored() && !JICatTree.this.selectedTreeNode.isRoot()) {
					JICatTree.this.selectedTreeNode.explore();
				}
			}

			public final void treeWillCollapse(final TreeExpansionEvent e) {
			}
		});
	}

	public static final DefaultTreeModel createCategoryTree() {
		JICatTreeNode rootNode = null;

		rootNode = new JICatTreeNode("Categories","root");

		rootNode.explore();
		return new DefaultTreeModel(rootNode);
	}

	public void initPopMenu() {
		this.popup = new JPopupMenu();

		this.jMenuItemSearch = new JMenuItem(new AbstractAction() {
			/**
			 *
			 */
			private static final long serialVersionUID = 5900099633961656011L;

			public void actionPerformed(final ActionEvent e) {
				if (JICatTree.this.clickedPath==null) {
					return;
				}
				if (isExpanded(JICatTree.this.clickedPath)) {
					collapsePath(JICatTree.this.clickedPath);
				} else {
					expandPath(JICatTree.this.clickedPath);
				}
			}
		});
		this.popup.add(this.jMenuItemSearch);

		this.jMenuItemExpand =  new JMenuItem(new AbstractAction() {

			/**
			 *
			 */
			private static final long serialVersionUID = -4009237316400174362L;

			public void actionPerformed(final ActionEvent e) {
				if (JICatTree.this.clickedPath==null) {
					return;
				}
				if (isExpanded(JICatTree.this.clickedPath)) {
					collapsePath(JICatTree.this.clickedPath);
				} else {
					expandPath(JICatTree.this.clickedPath);
				}
			}
		});
		this.popup.add(this.jMenuItemExpand);

		final JMenuItem refreshAction =  new JMenuItem(new AbstractAction("Refresh") {

			/**
			 *
			 */
			private static final long serialVersionUID = -4040840964012795175L;

			public void actionPerformed(ActionEvent e) {
				repaint();
				refresh();
			}
		});
		this.popup.add(refreshAction);

//		jMenuItemIncludeSubs =  new JMenuItem(new AbstractAction() {
//
//			public void actionPerformed(final ActionEvent e) {
//				jPref.setCIncludeSubs(!jPref.isCIncludeSubs());
//			}
//		});
//		popup.add(jMenuItemIncludeSubs);

		this.popup.addSeparator();

		final Action createAction = new AbstractAction("New Category") {

			/**
			 *
			 */
			private static final long serialVersionUID = 8554339409266846798L;

			public void actionPerformed(ActionEvent e) {
				boolean unqueName = true;
				int nameCount = 1;
				String newName = "New Category";
				String newNodeID;

				TreePath path = getSelectionPath();
				log.debug("actionPerformed NewCategory path = "+path);
				if (path == null) {
					return;
				}

				if (!isExpanded(path)) {
					expandPath(path);
				}

				repaint();

				JICatTreeNode node = (JICatTreeNode)getTreeNode(path);
				if (node == null) {
					return;
				}

				while (!unqueName) {
					for (Enumeration<TreeNode> enumer = node.children(); enumer.hasMoreElements();) {
						if (!JIThumbnailService.getInstance().categoryExists(JICatTree.this.selectedTreeNode, newName)) {
							unqueName = false;
							newName = "New Category" + nameCount++;
						}
					}
				}
				String strPath = (String) node.getUserObject();
				log.debug("node.getUserObject() = "+strPath);
				newNodeID = JIThumbnailService.getInstance().addCategoryTreeNode(node,newName);
				if (newNodeID != null) {
					JICatTreeNode newNode = new JICatTreeNode(newName,newNodeID);
					node.add(newNode);
					JICatTree.this.model.nodeStructureChanged(node);

					TreePath spath = new TreePath(newNode.getPath());
					log.debug("actionPerformed NewCategory path = "+spath);
					clearSelection();
					scrollPathToVisible(spath);
					expandPath(spath);

					JICatTree.this.repaint();
					startEditingAtPath(spath);
					//TODO: Need to set focus to the editing path
				}
			}
		};
		this.popup.add(createAction);

		final Action deleteAction = new AbstractAction("Delete") {

			/**
			 *
			 */
			private static final long serialVersionUID = 9146933090099186850L;

			public void actionPerformed(ActionEvent e) {
				repaint();
				TreePath path = getSelectionPath();
				if ((path == null) || (path.getPathCount() < 2)) {
					return;
				}
				JICatTreeNode node = (JICatTreeNode)getTreeNode(path);
				if (node == null) {
					return;
				}

				String key = node.getCategoryPath();
				if (JOptionPane.showConfirmDialog(null,
						"Do you want to delete \ncategory \""+key+"\" ?",
						"JIExplorer", JOptionPane.YES_NO_OPTION)
						!= JOptionPane.YES_OPTION) {
					return;
				}

				JIThumbnailService.getInstance().deleteCategoryTreeNode(node);

				TreeNode parent = node.getParent();
				node.removeFromParent();
				JICatTree.this.selectedTreeNode = (JICatTreeNode) parent;
				JICatTree.this.model.nodeStructureChanged(parent);
			}
		};
		this.popup.add(deleteAction);
		registerKeyboardAction(deleteAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
				JComponent.WHEN_FOCUSED);

		final Action renameAction = new AbstractAction("Rename") {

			/**
			 *
			 */
			private static final long serialVersionUID = 7762627949189387794L;

			public void actionPerformed(ActionEvent e) {
				repaint();
				TreePath path = getSelectionPath();
				if (path == null) {
					return;
				}

				log.debug("selection path = "+path);
				scrollPathToVisible(path);
				startEditingAtPath(path);
			}
		};
		this.popup.add(renameAction);

		addMouseListener(new PopupTrigger());
	}

	@Override
	public boolean isPathEditable(final TreePath path) {
		if ((path == null) || (path.getPathCount() < 2)) {
			return false;
		}
		final JICatTreeNode node = (JICatTreeNode) getTreeNode(path);
		if (node == null) {
			return false;
		}
		final String category = node.getCategoryPath();
		if (category != null) {
			this.editingCategory = category;
			return true;
		}
		return false;
	}

	public DefaultMutableTreeNode getTreeNode(final TreePath path) {
		return (JICatTreeNode)(path.getLastPathComponent());
	}

	public JICatTreeNode getSelectedNode() {
		final TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		}

		return (JICatTreeNode)path.getLastPathComponent();
	}

	public void updateSelectionPath(final String strPath) {
		final TreePath path = getSelectionPath();
		final JICatTreeNode node = (JICatTreeNode)path.getLastPathComponent();
		node.setUserObject(strPath);
		getTreeModel().nodeStructureChanged(node);
	}

	public void refresh() {
		final JICatTreeNode node;

		if (getSelectionPath() == null) {
			node = (JICatTreeNode)this.model.getRoot();
		} else {
			node = (JICatTreeNode)getSelectionPath().getLastPathComponent();
		}
		node.refresh();
		getTreeModel().reload(node);
	}

	public Vector<JICatTreeNode> getSelectedNodes() {
		final Vector<JICatTreeNode> selectedNodes = new Vector<JICatTreeNode>();
		final DefaultTreeSelectionModel dtsm = (DefaultTreeSelectionModel)this.getSelectionModel();
		final TreePath[] paths = dtsm.getSelectionPaths();

		if ((paths != null) && (paths.length > 0)) {
			for (final TreePath element : paths) {
				selectedNodes.add((JICatTreeNode)getTreeNode(element));
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

	public String getEditingCategory() {
		return this.editingCategory;
	}

	public void setEditingDir(final String editingCategory) {
		this.editingCategory = editingCategory;
	}

	public DefaultTreeModel getTreeModel() {
		return this.model;
	}

	public void setTreeModel(final DefaultTreeModel model) {
		this.model = model;
	}

	public JPopupMenu getPopup() {
		return this.popup;
	}

	public void setPopup(final JPopupMenu popup) {
		this.popup = popup;
	}

	class PopupTrigger extends MouseAdapter {

		@Override
		public void mouseReleased(final MouseEvent e) {
			final int x = e.getX();
			final int y = e.getY();
			final TreePath path = getPathForLocation(x, y);
			if (path == null) {
				return;
			}

			JICatTree.this.clickedPath = path;

			if (e.isPopupTrigger() || (e.getButton() == MouseEvent.BUTTON3)) {
				JICatTree.this.jMenuItemExpand.setEnabled(true);
				if (isExpanded(path)) {
					JICatTree.this.jMenuItemExpand.getAction().putValue(Action.NAME, "Collapse");
				} else {
					JICatTree.this.jMenuItemExpand.getAction().putValue(Action.NAME, "Expand");
					if (((JICatTreeNode)JICatTree.this.clickedPath.getLastPathComponent()).isLeaf()) {
						JICatTree.this.jMenuItemExpand.setEnabled(false);
					}
				}

//				if (jPref.isCIncludeSubs()) {
//					jMenuItemIncludeSubs.getAction().putValue(Action.NAME, "Exclude Sub Categories");
//				} else {
//					jMenuItemIncludeSubs.getAction().putValue(Action.NAME, "Include Sub Categories");
//				}

				JICatTree.this.popup.show(JICatTree.this, x, y);
			}
		}
	}
}
