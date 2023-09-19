package de.jmizv.jiexplorer.gui.cattree;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIObserver;


public class JICatTreePanel extends JPanel  implements JIObservable {
	/**
	 *
	 */
	private static final long serialVersionUID = -8263892893340324436L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JICatTreePanel.class);

	private final String[] strToggle = {"Inclusive","Exclusive"};
	private final String[] strToggleAnd = {"AND","OR"};

//	private final JButton btnSearch;
	private final JComboBox cmbToggle;
	private final JComboBox cmbToggleAnd;
	private final JICatTree catTree;

	private final Vector<JIObserver> obs;
	private boolean changed;

	public JICatTreePanel() {
		super();

		this.catTree = new JICatTree();
		this.cmbToggleAnd = new JComboBox(this.strToggleAnd);
		this.cmbToggle = new JComboBox(this.strToggle);

		this.obs = new Vector<JIObserver>();
		this.changed = false;

		setLayout(new BorderLayout());

		add(new JScrollPane(this.catTree), BorderLayout.CENTER);

		this.cmbToggleAnd.setSelectedIndex(JIPreferences.getInstance().getQueryAndOr());
		this.cmbToggleAnd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				cmbToggleAndActionPerformed(evt);
				butSearchActionPerformed();
			}
		});

		this.cmbToggle.setSelectedIndex(JIPreferences.getInstance().isCategoryIncludeSubs()?0:1);
		this.cmbToggle.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				cmbToggleActionPerformed(evt);
				butSearchActionPerformed();
			}
		});

		this.catTree.addTreeSelectionListener(new TreeSelectionListener () {
			public final void valueChanged(final TreeSelectionEvent e) {
				final Thread runner = new Thread() {
					@Override
					public void run() {
						Runnable runnable = new Runnable() {
							public void run() {
								final JICatTreeNode selectedTreeNode = (JICatTreeNode) JICatTreePanel.this.catTree.getLastSelectedPathComponent();
								JIExplorer.instance().getContext().setSelectedCatNodes(JICatTreePanel.this.catTree.getSelectedNodes(),selectedTreeNode);

								if ((selectedTreeNode != null) && (selectedTreeNode.isLeaf())) {
									butSearchActionPerformed();
								}
							}
						};
						SwingUtilities.invokeLater(runnable);
					}
				};
				runner.start();
			}
		});
		catTree.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(final TreeSelectionEvent e) {
				butSearchActionPerformed();
			}

		});

		final JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new GridBagLayout());

		final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = .3333;
		gridBagConstraints.weighty = 1;
		controlsPanel.add(this.cmbToggleAnd,gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = .3333;
		gridBagConstraints.weighty = 1;
		controlsPanel.add(this.cmbToggle,gridBagConstraints);

		add(controlsPanel,BorderLayout.SOUTH);
		setVisible(true);
	}

	public void reload() {
		this.catTree.refresh();
		this.catTree.repaint();
	}

	private void cmbToggleActionPerformed(final java.awt.event.ActionEvent evt) {
		JIPreferences.getInstance().setCategoryIncludeSubs(!JIPreferences.getInstance().isCategoryIncludeSubs());
		this.cmbToggle.setSelectedIndex(JIPreferences.getInstance().isCategoryIncludeSubs()?0:1);
	}

	private void cmbToggleAndActionPerformed(final java.awt.event.ActionEvent evt) {
		JIPreferences.getInstance().setQueryAndOr(JIPreferences.getInstance().getQueryAndOr()==1?0:1);
		this.cmbToggleAnd.setSelectedIndex(JIPreferences.getInstance().getQueryAndOr());
	}

	private void butSearchActionPerformed() {
		if (JICatTreePanel.this.catTree.getSelectedNode()!=null) {
			final Thread runner = new Thread() {
				@Override
				public void run() {
					Runnable runnable = new Runnable() {
						public void run() {
							log.debug("catTree_valueChanged Category = " + JICatTreePanel.this.catTree.getSelectedNode().getCategoryPath());
							setChanged();
							notifyObservers(JIObservable.CATAGORY_CHANGED);
						}
					};
					SwingUtilities.invokeLater(runnable);
				}
			};
			runner.start();
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
