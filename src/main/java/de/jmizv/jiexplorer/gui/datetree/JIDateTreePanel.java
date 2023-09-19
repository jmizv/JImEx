package de.jmizv.jiexplorer.gui.datetree;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIObserver;


public class JIDateTreePanel extends JPanel  implements JIObservable {
	/**
	 *
	 */
	private static final long serialVersionUID = -4084236240207896554L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIDateTreePanel.class);

	private final JIDateTree dateTree;

	private final Vector<JIObserver> obs;
	private boolean changed;

	protected JPopupMenu            popup;
	protected JMenuItem             jMenuItemSearch;
	protected JMenuItem             jMenuItemExpand;

	public JIDateTreePanel() {
		super();

		this.dateTree = new JIDateTree();

		this.obs = new Vector<JIObserver>();
		this.changed = false;

		this.dateTree.addTreeSelectionListener(new TreeSelectionListener () {
			public final void valueChanged(final TreeSelectionEvent e) {
				final Thread runner = new Thread() {
					@Override
					public void run() {
						Runnable runnable = new Runnable() {
							public void run() {
								final JIDateTreeNode selectedTreeNode = (JIDateTreeNode) JIDateTreePanel.this.dateTree.getLastSelectedPathComponent();
								JIExplorer.instance().getContext().setSelectedDateNodes(JIDateTreePanel.this.dateTree.getSelectedNodes(),selectedTreeNode);

								// Refresh the address field.
								if ((selectedTreeNode != null) && (selectedTreeNode.getType() >= JIDateTreeNode.YEAR)) {
									butSearchActionPerformed(null);
								}
							}
						};
						SwingUtilities.invokeLater(runnable);
					}
				};
				runner.start();
			}
		});


		setLayout(new BorderLayout());

		add(new JScrollPane(this.dateTree), BorderLayout.CENTER);

		initPopMenu();
		setVisible(true);
	}

	public void reload() {
		this.dateTree.refresh();
		this.dateTree.repaint();
	}

	private void butSearchActionPerformed(final java.awt.event.ActionEvent evt) {
		final Thread runner = new Thread() {
			@Override
			public void run() {
				Runnable runnable = new Runnable() {
					public void run() {
						log.debug("catTree_valueChanged Category = " + JIDateTreePanel.this.dateTree.getSelectedNode().getCategoryPath());
						setChanged();
						notifyObservers(JIObservable.DATE_CHANGED);
					}
				};
				SwingUtilities.invokeLater(runnable);
			}
		};
		runner.start();
	}

	public void initPopMenu() {
		this.popup = new JPopupMenu();

		this.jMenuItemSearch = new JMenuItem(new AbstractAction() {
			/**
			 *
			 */
			private static final long serialVersionUID = 4532427393951521645L;

			public void actionPerformed(final ActionEvent e) {
				butSearchActionPerformed(null);
			}
		});
		this.popup.add(this.jMenuItemSearch);

		this.jMenuItemExpand =  new JMenuItem(new AbstractAction() {

			/**
			 *
			 */
			private static final long serialVersionUID = 9192074878050321775L;

			public void actionPerformed(final ActionEvent e) {
				if (JIDateTreePanel.this.dateTree.clickedPath==null) {
					return;
				}

				if (JIDateTreePanel.this.dateTree.isExpanded(JIDateTreePanel.this.dateTree.clickedPath)) {
					JIDateTreePanel.this.dateTree.collapsePath(JIDateTreePanel.this.dateTree.clickedPath);
				} else {
					JIDateTreePanel.this.dateTree.expandPath(JIDateTreePanel.this.dateTree.clickedPath);
				}
			}
		});
		this.popup.add(this.jMenuItemExpand);


		this.dateTree.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseReleased(final MouseEvent e) {
				final int x = e.getX();
				final int y = e.getY();
				final TreePath path = JIDateTreePanel.this.dateTree.getPathForLocation(x, y);
				if (path == null) {
					return;
				}

				JIDateTreePanel.this.dateTree.clickedPath = path;

				if (e.isPopupTrigger() || (e.getButton() == MouseEvent.BUTTON3)) {
					JIDateTreePanel.this.jMenuItemExpand.setEnabled(true);
					if (JIDateTreePanel.this.dateTree.isExpanded(path)) {
						JIDateTreePanel.this.jMenuItemExpand.getAction().putValue(Action.NAME, "Collapse");
					} else {
						JIDateTreePanel.this.jMenuItemSearch.getAction().putValue(Action.NAME, "Search");
						JIDateTreePanel.this.jMenuItemExpand.getAction().putValue(Action.NAME, "Expand");
						if (((JIDateTreeNode)JIDateTreePanel.this.dateTree.clickedPath.getLastPathComponent()).isLeaf()) {
							JIDateTreePanel.this.jMenuItemExpand.setEnabled(false);
						}
					}

					JIDateTreePanel.this.popup.show(JIDateTreePanel.this.dateTree, x, y);
				}
			}
		});
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
			if (!this.changed) {
				return;
			}
			arrLocal = this.obs.toArray();
			clearChanged();
		}

		for (int i = arrLocal.length-1; i>=0; i--) {
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
}
