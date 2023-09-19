package de.jmizv.jiexplorer.gui.tree;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIThumbnailCache;
import de.jmizv.jiexplorer.filetask.DeleteFileTask;
import de.jmizv.jiexplorer.filetask.ProgressDialog;
import de.jmizv.jiexplorer.gui.AbstractDiskObjectList;
import de.jmizv.jiexplorer.gui.dnd.TransferActionListener;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIObserver;


public class JIDirTree extends JTree implements JIObservable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2844546991944685813L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIDirTree.class);

	protected DefaultTreeModel      model;
	protected DefaultTreeCellEditor editor;

	protected File                  editingDir;

	protected TreePath   clickedPath;

	protected JPopupMenu popup;
	protected JMenuItem  jMenuItemExpand;
	protected JMenuItem  jMenuItemRefresh;
	protected JMenuItem  jMenuItemCut;
	protected JMenuItem  jMenuItemCopy;
	protected JMenuItem  jMenuItemPaste;
	protected JMenuItem  jMenuItemDelete;
	protected JMenuItem  jMenuItemRename;
	protected JMenuItem  jMenuItemNewFolder;
	protected JMenuItem  startThumbnailScan;
	protected JMenuItem  stopThumbnailScan;

	protected AbstractDiskObjectList jilist;

	protected Vector<JIObserver> obs;
	protected boolean changed;

	public JIDirTree() {
		super();

		this.obs = new Vector<JIObserver>();
		this.changed = false;
	}

	public JIDirTree(final DefaultTreeModel model) {
		super(model);

		this.obs = new Vector<JIObserver>();
		this.changed = false;

		this.model = model;
		putClientProperty("JTree.lineStyle", "Angled");

		final JITreeRenderer renderer = new JITreeRenderer();
		setCellRenderer(renderer);
		final JITreeCellEditorListener cel = new JITreeCellEditorListener(this);

		this.editor = new DefaultTreeCellEditor(this, renderer);
		this.editor.addCellEditorListener(cel);
		setCellEditor(this.editor);
		setEditable(true);

		addTreeSelectionListener(new JITreeDiskSelectionAdapter());
		addTreeExpansionListener(new JITreeDiskExpansionAdapter());
		addTreeWillExpandListener(new JITreeDiskWillExpandAdapter());

		initPopMenu();

		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setBackground(Color.white);
		setAlignmentX((float) 0.5);
		setShowsRootHandles(false);
		//setTransferHandler(new NodeTransferHandler());
		this.setDragEnabled(false);
	}

	public void gotoLastDirectory() {
		File prefDir = null;
		if ((JIPreferences.getInstance().getDirectoryPath() != null)) {
			prefDir = new File(JIPreferences.getInstance().getDirectoryPath());
		} else {
			prefDir = new File(System.getProperty("user.home"));
		}
		log.debug("Last Dir = "+prefDir);
		if(prefDir.exists() && prefDir.canRead() && prefDir.isDirectory()) {
			final JITreeNode selectedTreeNode = findNodeForDir(new File(JIPreferences.getInstance().getDirectoryPath()));

			if (selectedTreeNode != null) {
				expandPaths(prefDir);
			}
		}
	}

	public void initPopMenu() {
		this.popup = new JPopupMenu();

		this.jMenuItemExpand = new JMenuItem();
		this.jMenuItemExpand.setText("Expand");
		this.jMenuItemExpand.setAction(new AbstractAction() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2975977946216576290L;

			public void actionPerformed(final ActionEvent e) {
				if (JIDirTree.this.clickedPath==null) {
					return;
				}
				if (isExpanded(JIDirTree.this.clickedPath)) {
					collapsePath(JIDirTree.this.clickedPath);
				} else {
					expandPath(JIDirTree.this.clickedPath);
				}
			}
		});
		this.popup.add(this.jMenuItemExpand);


		this.jMenuItemRefresh = new JMenuItem();
		this.jMenuItemRefresh.setText("Refresh");
		this.jMenuItemRefresh.setAction(new AbstractAction("Refresh", null) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1118192487617852891L;

			public void  actionPerformed(final ActionEvent e) {
				repaint();
				refresh();
			}
		});


		this.startThumbnailScan = new JMenuItem();
		this.startThumbnailScan.setText("Start Thumbnail Scan");
		this.startThumbnailScan.setAction(new AbstractAction("Start Thumbnail Scan", null) {
			/**
			 *
			 */
			private static final long serialVersionUID = 4665381025841973090L;

			public void  actionPerformed(final ActionEvent e) {
				JIDirTree.this.setChanged();
				JIDirTree.this.notifyObservers(JIObservable.PROGERSS_START);
				JIDirTree.this.clearChanged();
			}
		});

		this.stopThumbnailScan = new JMenuItem();
		this.stopThumbnailScan.setText("Stop Thumbnail Scan");
		this.stopThumbnailScan.setAction(new AbstractAction("Stop Thumbnail Scan", null) {
			/**
			 *
			 */
			private static final long serialVersionUID = 4663700532292943461L;

			public void  actionPerformed(final ActionEvent e) {
				JIDirTree.this.setChanged();
				JIDirTree.this.notifyObservers(JIObservable.PROGERSS_CANCELLED);
				JIDirTree.this.clearChanged();
			}
		});

		this.jMenuItemNewFolder = new JMenuItem();
		this.jMenuItemNewFolder.setText("New Dir");
		this.jMenuItemNewFolder.setAction(new AbstractAction("New Dir", null) {

			/**
			 *
			 */
			private static final long serialVersionUID = -4803446455626595256L;

			public void actionPerformed(final ActionEvent e) {
				repaint();
				TreePath path = getSelectionPath();
				if ((path == null) || (path.getPathCount() < 2)) {
					return;
				}
				final JITreeNode node = (JITreeNode)getTreeNode(path);
				if (node == null) {
					return;
				}

				final File dir = node.getFile();
				int index = 0;
				File newDir = new File(dir, "New Directory");
				while (newDir.exists()) {
					index++;
					newDir = new File(dir, "New Directory"+index);
				}
				newDir.mkdirs();

				final JITreeNode newNode = new JITreeNode(newDir);
				node.add(newNode);
				JIDirTree.this.model.nodeStructureChanged(node);

				path = new TreePath(newNode.getPath());
				scrollPathToVisible(path);
				startEditingAtPath(path);
			}
		});


		this.jMenuItemDelete = new JMenuItem();
		this.jMenuItemDelete.setText("Delete");
		this.jMenuItemDelete.setAction(new AbstractAction("Delete", null) {

			/**
			 *
			 */
			private static final long serialVersionUID = 7856603564309835423L;

			public void actionPerformed(final ActionEvent e) {
				repaint();
				final TreePath path = getSelectionPath();
				if ((path == null) || (path.getPathCount() < 2)) {
					return;
				}
				final JITreeNode node = (JITreeNode)getTreeNode(path);
				if (node == null) {
					return;
				}

				final File dir = node.getFile();
				if ((dir != null) && dir.isDirectory()) {
					if (JOptionPane.showConfirmDialog(null,
							"Do you want to delete \ndirectory \""+dir.getName()+"\" ?",
							"JIExplorer", JOptionPane.YES_NO_OPTION)
							!= JOptionPane.YES_OPTION) {
						return;
					}

					final TreeNode parent = node.getParent();

					node.removeFromParent();
					JIDirTree.this.model.nodeStructureChanged(parent);


					final DeleteFileTask deleteTask = new DeleteFileTask(dir);
					final ProgressDialog pd = new ProgressDialog(((Frame)getRootPane().getParent()),deleteTask.getOperationName(),deleteTask,null);
					pd.run();
				}
			}
		});

		registerKeyboardAction(this.jMenuItemDelete.getAction(),
				KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
				JComponent.WHEN_FOCUSED);

		this.jMenuItemRename = new JMenuItem();
		this.jMenuItemRename.setText("Rename");
		this.jMenuItemRename.setAction(new AbstractAction("Rename", null) {

			/**
			 *
			 */
			private static final long serialVersionUID = 3144301944244338497L;

			public void actionPerformed(final ActionEvent e) {
				repaint();
				final TreePath path = getSelectionPath();
				if (path == null) {
					return;
				}
				scrollPathToVisible(path);
				startEditingAtPath(path);
			}
		});


		final TransferActionListener actionListener = new TransferActionListener();

//		this.jMenuItemCopy = new JMenuItem();
//		this.jMenuItemCopy.setText("Copy");
//		this.jMenuItemCopy.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
//		this.jMenuItemCopy.addActionListener(actionListener);
//		this.jMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
//		this.jMenuItemCopy.setMnemonic(KeyEvent.VK_C);
//
//		this.jMenuItemCut = new JMenuItem();
//		this.jMenuItemCut.setText("Cut");
//		this.jMenuItemCut.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
//		this.jMenuItemCut.addActionListener(actionListener);
//		this.jMenuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
//		this.jMenuItemCut.setMnemonic(KeyEvent.VK_U);

		this.jMenuItemPaste = new JMenuItem();
		this.jMenuItemPaste.setText("Paste");
		this.jMenuItemPaste.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
		this.jMenuItemPaste.addActionListener(actionListener);
		this.jMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		this.jMenuItemPaste.setMnemonic(KeyEvent.VK_P);

		addMouseListener(new PopupTrigger());
	}

	@Override
	public boolean isPathEditable(final TreePath path) {
		if ((path == null) || (path.getPathCount() < 3)) {
			//log.debug("isPathEditable = false "+path);
			return false;
		}
		final JITreeNode node = (JITreeNode) getTreeNode(path);
		if (node == null) {
			//log.debug("isPathEditable = false node = null");
			return false;
		}
		final File dir = node.getFile();
		if ((dir != null) && dir.isDirectory()) {
			this.editingDir = dir;
			//log.debug("isPathEditable = true "+editingDir);
			return true;
		}
		//log.debug("isPathEditable = false");
		return false;
	}

	public DefaultMutableTreeNode getTreeNode(final TreePath path) {
		return (JITreeNode)(path.getLastPathComponent());
	}

	public JITreeNode getSelectedNode() {
		final TreePath path = getSelectionPath();
		if (path != null) {
			return (JITreeNode)path.getLastPathComponent();
		} else {
			return null;
		}
	}

	public File getCurrentDir() {
		final TreePath path = getSelectionPath();
		if (path != null) {
			return ((JITreeNode)path.getLastPathComponent()).getFile();
		} else {
			return new File(JIPreferences.getInstance().getDirectoryPath());
		}
	}


	public void setJIList(final AbstractDiskObjectList jilist) {
		this.jilist = jilist;
	}

	public AbstractDiskObjectList getJIList() {
		return this.jilist;
	}

	public void addFile(final File file) {
		this.jilist.addFile(file);
	}

	public void updateSelectionPath(final File file) {
		final TreePath path = getSelectionPath();
		final JITreeNode node = (JITreeNode)path.getLastPathComponent();
		node.setUserObject(file);
		getTreeModel().nodeStructureChanged(node);
	}

	public void refresh() {
		final TreePath path = getSelectionPath();
		final JITreeNode node = (JITreeNode)path.getLastPathComponent();
		node.refresh();
		getTreeModel().reload(node);

		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setBackground(Color.white);
		setAlignmentX((float) 0.5);
		setShowsRootHandles(false);
	}

	public final JITreeNode findChildNode(final JITreeNode parentTreeNode, final File selectedSubDir) {
		log.info("findChildNode "+selectedSubDir.getPath());
		if (!parentTreeNode.isExplored()) {
			parentTreeNode.explore();
		}

		final int count = this.getModel().getChildCount(parentTreeNode);

		for (int i = 0; i < count; i++) {
			final Object oneChild = this.getModel().getChild(parentTreeNode, i);

			if (oneChild instanceof JITreeNode) {
				final File file = (File) ((JITreeNode) oneChild).getUserObject();

				if (file.equals(selectedSubDir)) {
					return (JITreeNode) oneChild;
				}
			}
		}
		return null;
	}

	   public final JITreeNode findNodeForDir(final File dir) {
		      log.info("findNodeForDir "+dir.getPath());

		      JITreeNode parentNode = (JITreeNode) this.getModel().getRoot();

		      if (!parentNode.isExplored()) {
				parentNode.explore();
			}

		      final File parentFile = (File) (parentNode).getUserObject();
		      final String dirPath = dir.getAbsolutePath();

		      if (parentFile.equals(new File(JIExplorer.ROOT_FOLDER))) {
		         final int count = this.getModel().getChildCount(parentNode);

		         for (int i = 0; i < count; i++) {
		            final Object oneChild = this.getModel().getChild(parentNode, i);
		            final String onePath = ((JITreeNode) oneChild).toString();

		            if (dirPath.startsWith(onePath)) {
		               parentNode = (JITreeNode) oneChild;
		               break;
		            }
		         }
		      } else if (!dirPath.startsWith(parentFile.getAbsolutePath())) {
				return null;
			}

		      final Iterator<String> iter = parsePath(dir).iterator();

		      boolean pathNotFound = false;
		      if (iter.hasNext()) {
		         iter.next();

		         while (iter.hasNext() && !pathNotFound) {
		            if (!parentNode.isExplored()) {
						parentNode.explore();
					}

		            final String nextPath = iter.next();

		            pathNotFound = true;
		            final int count = this.getModel().getChildCount(parentNode);

		            for (int i = 0; i < count; i++) {
		               final Object oneChild = this.getModel().getChild(parentNode, i);
		               final String onePath = ((JITreeNode) oneChild).toString();

		               if (onePath.equals(nextPath)) {
		                  parentNode = (JITreeNode) oneChild;
		                  pathNotFound = false;
		                  break;
		               }
		            }
		         }

		         if (pathNotFound) {
		            log.info("findNodeForDir NULL");
		            return null;
		         } else {
		            log.info("findNodeForDir "+parentNode);
		            return parentNode;
		         }
		      }
		      return null;
		   }

	public final static List<String>  parsePath(final File selectedDir) {
		// First parse the given directory path into separate path names/fields.
		final List<String> paths = new ArrayList<String>();
		final String selectedAbsPath = selectedDir.getAbsolutePath();
		int beginIndex = 0;
		int endIndex = selectedAbsPath.indexOf(File.separator);

		// For the first path name, attach the path separator.
		// For Windows, it should be like 'C:\', for Unix, it should be like '/'.
		paths.add(selectedAbsPath.substring(beginIndex, endIndex + 1));
		beginIndex = endIndex + 1;
		endIndex = selectedAbsPath.indexOf(File.separator, beginIndex);
		while (endIndex != -1) {
			// For other path names, do not attach the path separator.
			paths.add(selectedAbsPath.substring(beginIndex, endIndex));
			beginIndex = endIndex + 1;
			endIndex = selectedAbsPath.indexOf(File.separator, beginIndex);
		}
		final String lastPath = selectedAbsPath.substring(beginIndex, selectedAbsPath.length());

		if ((lastPath != null) && (lastPath.length() != 0)) {
			paths.add(lastPath);
		}

		return paths;
	}

	public TreePath getClickedPath() {
		return this.clickedPath;
	}


	public void setClickedPath(final TreePath clickedPath) {
		this.clickedPath = clickedPath;
	}


	public File getEditingDir() {
		return this.editingDir;
	}


	public void setEditingDir(final File editingDir) {
		this.editingDir = editingDir;
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
			if (e.isPopupTrigger() || (e.getButton() == MouseEvent.BUTTON3)) {
				final int x = e.getX();
				final int y = e.getY();
				final TreePath path = getPathForLocation(x, y);
				if (path == null) {
					return;
				}

				JIDirTree.this.popup.removeAll();
				if (isExpanded(path)) {
					JIDirTree.this.jMenuItemExpand.setText("Collapse");
				} else {
					JIDirTree.this.jMenuItemExpand.setText("Expand");
				}
				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemExpand);
				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemRefresh);
				if (!JIExplorer.instance().getContext().isStatusBarProgressTaskRunning()) {
					JIDirTree.this.startThumbnailScan.setText("Start Thumbnail Scan");
					JIDirTree.this.popup.add(JIDirTree.this.startThumbnailScan);
				} else {
					JIDirTree.this.stopThumbnailScan.setText("Stop Thumbnail Scan");
					JIDirTree.this.popup.add(JIDirTree.this.stopThumbnailScan);
				}
				JIDirTree.this.popup.addSeparator();
				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemNewFolder);
				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemDelete);
				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemRename);
//				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemCut);
//				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemCopy);
				JIDirTree.this.popup.add(JIDirTree.this.jMenuItemPaste);

				setSelectionPath(path);
				scrollPathToVisible(path);
				JIDirTree.this.popup.show(JIDirTree.this, x, y);
				JIDirTree.this.clickedPath = path;
			}
		}
	}



	/**
	 * Adds an observer to the set of observers for this object, provided
	 * that it is not the same as some observer already in the set.
	 * The order in which notifications will be delivered to multiple
	 * observers is not specified. See the class comment.
	 *
	 * @param   o   an observer to be added.
	 * @throws NullPointerException   if the parameter o is null.
	 */
	public synchronized void addObserver(final JIObserver o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (!this.obs.contains(o)) {
			this.obs.addElement(o);
		}
	}

	/**
	 * Deletes an observer from the set of observers of this object.
	 * Passing <CODE>null</CODE> to this method will have no effect.
	 * @param   o   the observer to be deleted.
	 */
	public synchronized void deleteObserver(final JIObserver o) {
		this.obs.removeElement(o);
	}

	/**
	 * If this object has changed, as indicated by the
	 * <code>hasChanged</code> method, then notify all of its observers
	 * and then call the <code>clearChanged</code> method to
	 * indicate that this object has no longer changed.
	 * <p>
	 * Each observer has its <code>update</code> method called with two
	 * arguments: this observable object and <code>null</code>. In other
	 * words, this method is equivalent to:
	 * <blockquote><tt>
	 * notifyObservers(null)</tt></blockquote>
	 *
	 * @see     java.util.Observable#clearChanged()
	 * @see     java.util.Observable#hasChanged()
	 * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void notifyObservers() {
		notifyObservers(null);
	}

	/**
	 * If this object has changed, as indicated by the
	 * <code>hasChanged</code> method, then notify all of its observers
	 * and then call the <code>clearChanged</code> method to indicate
	 * that this object has no longer changed.
	 * <p>
	 * Each observer has its <code>update</code> method called with two
	 * arguments: this observable object and the <code>arg</code> argument.
	 *
	 * @param   arg   any object.
	 * @see     java.util.Observable#clearChanged()
	 * @see     java.util.Observable#hasChanged()
	 * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void notifyObservers(final Object arg) {
		/*
		 * a temporary array buffer, used as a snapshot of the state of
		 * current Observers.
		 */
		Object[] arrLocal;

		synchronized (this) {
			/* We don't want the Observer doing callbacks into
			 * arbitrary code while holding its own Monitor.
			 * The code where we extract each Observable from
			 * the Vector and store the state of the Observer
			 * needs synchronization, but notifying observers
			 * does not (should not).  The worst result of any
			 * potential race-condition here is that:
			 * 1) a newly-added Observer will miss a
			 *   notification in progress
			 * 2) a recently unregistered Observer will be
			 *   wrongly notified when it doesn't care
			 */
			if (!this.changed ) {
				return;
			}
			arrLocal = this.obs.toArray();
			clearChanged();
		}

		for (int i = arrLocal.length-1; i>=0; i--) {
			log.debug("JIDirTree::notifyObservers - Observer = "+arrLocal[i].getClass().getName()+" arg = "+arg);
			((JIObserver)arrLocal[i]).update(this, arg);
		}
	}

	/**
	 * Clears the observer list so that this object no longer has any observers.
	 */
	public synchronized void deleteObservers() {
		this.obs.removeAllElements();
	}

	/**
	 * Marks this <tt>Observable</tt> object as having been changed; the
	 * <tt>hasChanged</tt> method will now return <tt>true</tt>.
	 */
	protected synchronized void setChanged() {
		this.changed = true;
	}

	/**
	 * Indicates that this object has no longer changed, or that it has
	 * already notified all of its observers of its most recent change,
	 * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
	 * This method is called automatically by the
	 * <code>notifyObservers</code> methods.
	 *
	 * @see     java.util.Observable#notifyObservers()
	 * @see     java.util.Observable#notifyObservers(java.lang.Object)
	 */
	protected synchronized void clearChanged() {
		this.changed = false;
	}

	/**
	 * Tests if this object has changed.
	 *
	 * @return  <code>true</code> if and only if the <code>setChanged</code>
	 *          method has been called more recently than the
	 *          <code>clearChanged</code> method on this object;
	 *          <code>false</code> otherwise.
	 * @see     java.util.Observable#clearChanged()
	 * @see     java.util.Observable#setChanged()
	 */
	public synchronized boolean hasChanged() {
		return this.changed;
	}

	/**
	 * Returns the number of observers of this <tt>Observable</tt> object.
	 *
	 * @return  the number of observers of this object.
	 */
	public synchronized int countObservers() {
		return this.obs.size();
	}

	private final void jTreeDiskValueChanged(final TreeSelectionEvent e) {

		final JITreeNode selectedTreeNode = (JITreeNode) getTreeNode(e.getPath());

		// Refresh the address field.
		if (selectedTreeNode != null) {
			final Vector<JITreeNode> vec = new Vector<JITreeNode>(1);
			vec.add(selectedTreeNode);

			JIExplorer.instance().getContext().setSelectedDirNodes(vec,selectedTreeNode);

			// Stop loading of all Icons if in progress
			JIThumbnailCache.getInstance().setProcessAllIcons(false);

			final File selectedDir = ((JITreeNode)this.getSelectionPath().getLastPathComponent()).getFile();
			if (this.jilist != null) {
				this.jilist.loadDirectory(selectedDir);
			}

			JIPreferences.getInstance().setDirectoryPath(selectedDir.getPath());
			log.debug("JIPreferences.getInstance().getDirectoryPath = "+JIPreferences.getInstance().getDirectoryPath());
		}
	}


	/**
	 * Expands the tree to the given path.
	 */
	public final void expandPaths(final File selectedDir) {

		final JITreeNode node = findNodeForDir(selectedDir);
		if (node == null) {
			//log.debug("expandPaths NULL ");
			return;
		}

		final Vector<JITreeNode> vec = new Vector<JITreeNode>(1);
		vec.add(node);
		JIExplorer.instance().getContext().setSelectedDirNodes(vec,0);

		final TreePath newPath = new TreePath(node.getPath());

		if (!isExpanded(newPath)) {
			expandPath(newPath);
			log.debug("expandPaths expandPath " + newPath);
		}
		setSelectionPath(newPath);
		scrollPathToVisible(newPath);
	}

	final class JITreeDiskWillExpandAdapter implements javax.swing.event.TreeWillExpandListener {

		public final void treeWillExpand(final TreeExpansionEvent e) throws ExpandVetoException {
			final TreePath path = e.getPath();

			final JITreeNode selectedNode = (JITreeNode) path.getLastPathComponent();

			if (!selectedNode.isExplored()) {
				selectedNode.explore();
			}
		}

		public final void treeWillCollapse(final TreeExpansionEvent e) {
		}
	}

	final class JITreeDiskExpansionAdapter implements javax.swing.event.TreeExpansionListener {

		public final void treeExpanded(final TreeExpansionEvent e) {
			final Thread runner = new Thread() {
				@Override
				public void run() {
					Runnable runnable = new Runnable() {
						public void run() {
							TreePath path = JIDirTree.this.getSelectionPath();
							if (path != null) {
								JIDirTree.this.setSelectionPath(path);
							}
						}
					};
					SwingUtilities.invokeLater(runnable);
				}
			};
			runner.start();
		}

		public final void treeCollapsed(final TreeExpansionEvent e) {
		}
	}

	final class JITreeDiskSelectionAdapter implements javax.swing.event.TreeSelectionListener {

		public final void valueChanged(final TreeSelectionEvent e) {
			final Thread runner = new Thread() {
				@Override
				public void run() {
					Runnable runnable = new Runnable() {
						public void run() {
							jTreeDiskValueChanged(e);
						}
					};
					SwingUtilities.invokeLater(runnable);
				}
			};
			runner.start();
		}
	}

	public void setSelectedDir(final DiskObject dObj) {
			final Thread runner = new Thread() {
				@Override
				public void run() {
					Runnable runnable = new Runnable() {
						public void run() {
							expandPaths(dObj.getFile());
						}
					};
					SwingUtilities.invokeLater(runnable);
				}
			};
			runner.start();
			runner.setPriority(6);
			return;
	}
}

