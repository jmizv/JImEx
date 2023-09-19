package de.jmizv.jiexplorer.gui.keyword;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIObserver;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public final class JIKeyWordPane extends JPanel implements JIObservable {

    @Serial
    private static final long serialVersionUID = 546675145572698214L;
    private final Vector<JIObserver> obs;
    private boolean changed;
    private JTextField addKeywordsTextField;

    private JList<String> availKeyWordsList;

    private JPopupMenu popup;
    private Action actionSearch;
    private Action actionDelete;

    private final String[] strToggle = {"AND", "OR"};

    private JComboBox<String> cmbToggle;

    public JIKeyWordPane() {
        super();

        this.changed = false;
        this.obs = new Vector<>();

        initGUI();
        initPopMenu();
    }

    public void initGUI() {
        JScrollPane availScrollPane = new JScrollPane();
        this.cmbToggle = new JComboBox<>(this.strToggle);

        setLayout(new BorderLayout());
        DefaultListModel<String> listModel = getKeyWordsList(new DefaultListModel<>());
        this.availKeyWordsList = new JList<>(listModel);
        this.availKeyWordsList.setLayoutOrientation(JList.VERTICAL_WRAP);

        this.availKeyWordsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                searchKeywords();
            }
        });
        this.availKeyWordsList.setBorder(new javax.swing.border.EtchedBorder());
        this.availKeyWordsList.setVisibleRowCount(-1);
        availScrollPane.setViewportView(this.availKeyWordsList);

        this.cmbToggle.setSelectedIndex(JIPreferences.getInstance().getQueryAndOr());
        this.cmbToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                cmbToggleActionPerformed();
                searchKeywords();
            }
        });

        final JPanel controlsPanel = new JPanel();
        final GridBagLayout controlsPanelLayout = new GridBagLayout();
        controlsPanelLayout.rowWeights = new double[]{0.1};
        controlsPanelLayout.rowHeights = new int[]{7};
        controlsPanelLayout.columnWeights = new double[]{0.1, 0.1, 1.0, 0.1};
        controlsPanelLayout.columnWidths = new int[]{7, 7, 7, 7};
        controlsPanel.setLayout(controlsPanelLayout);

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = .6666;
        gridBagConstraints.weighty = 1;

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = .3333;
        gridBagConstraints.weighty = 1;
        controlsPanel.add(cmbToggle, new GridBagConstraints(0, 0, 1, 1, 0.3333, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
        {
            JLabel keywordsJlabel = new JLabel();
            controlsPanel.add(keywordsJlabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
            keywordsJlabel.setText("Key Words:");
        }
        {
            addKeywordsTextField = new JTextField();
            controlsPanel.add(addKeywordsTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 0, 4), 0, 0));
        }
        {
            JButton addKeywordJbutton = new JButton();
            controlsPanel.add(addKeywordJbutton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0));
            addKeywordJbutton.setText("Add");
            addKeywordJbutton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(final MouseEvent evt) {
                    final String text = addKeywordsTextField.getText();
                    final String[] keyItems = text.split(",");
                    for (final String kw : keyItems) {
                        insertKeyWord(kw.trim());
                        JIThumbnailService.getInstance().insertNewKeyWord(kw.trim());
                    }
                    addKeywordsTextField.setText("");
                }
            });
        }

        add(controlsPanel, BorderLayout.SOUTH);
        setVisible(true);
        {
            JScrollPane jScrollPane_IL = new JScrollPane(availScrollPane);
            add(jScrollPane_IL, BorderLayout.CENTER);
            jScrollPane_IL.setMinimumSize(new java.awt.Dimension(300, 100));
            jScrollPane_IL.setPreferredSize(new java.awt.Dimension(262, 100));
            jScrollPane_IL.setAutoscrolls(true);
        }
    }

    public void initPopMenu() {
        this.popup = new JPopupMenu();

        this.actionSearch = new AbstractAction("Search") {

            @Serial
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                searchKeywords();
            }
        };
        this.popup.add(this.actionSearch);

        this.actionDelete = new AbstractAction("Delete") {

            @Serial
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                List<String> objs = JIKeyWordPane.this.availKeyWordsList.getSelectedValuesList();
                final int[] indices = JIKeyWordPane.this.availKeyWordsList.getSelectedIndices();
                for (int i = 0; i < objs.size(); i++) {
                    ((DefaultListModel<String>) JIKeyWordPane.this.availKeyWordsList.getModel()).removeElementAt(indices[i]);
                    JIThumbnailService.getInstance().deleteKeyWord(objs.get(i));
                }
            }
        };
        this.popup.add(this.actionDelete);

        this.availKeyWordsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                System.out.println("mousePressed x[" + e.getX() + "] y[" + e.getY() + "]");
                if (e.isPopupTrigger()) {
                    JIKeyWordPane.this.popup.removeAll();
                    JIKeyWordPane.this.popup.add(JIKeyWordPane.this.actionSearch);
                    JIKeyWordPane.this.popup.add(JIKeyWordPane.this.actionDelete);
                    JIKeyWordPane.this.popup.show(JIKeyWordPane.this, e.getX(), e.getY());
                }
            }
        });
    }

    private void cmbToggleActionPerformed() {
        JIPreferences.getInstance().setQueryAndOr(JIPreferences.getInstance().getQueryAndOr() == 1 ? 0 : 1);
        this.cmbToggle.setSelectedIndex(JIPreferences.getInstance().getQueryAndOr());
    }

    private DefaultListModel<String> getKeyWordsList(DefaultListModel<String> dlm) {
        return JIThumbnailService.getInstance().getKeyWords(dlm);
    }

    public void updateKeyWords() {
        DefaultListModel<String> listModel = getKeyWordsList(new DefaultListModel<>());
        this.availKeyWordsList.setModel(listModel);
    }

    public void insertKeyWord(final String keys) {
        DefaultListModel<String> listModel = (DefaultListModel<String>) this.availKeyWordsList.getModel();
        listModel.addElement(keys);
    }

    public int getLastSelectedIndex() {
        List<String> objs = this.availKeyWordsList.getSelectedValuesList();
        final Object obj = this.availKeyWordsList.getSelectedValue();
        int cnt = 0;
        for (Object o : objs) {
            if (o.equals(obj)) {
                return cnt;
            }
            cnt++;
        }
        return -1;
    }

    private void searchKeywords() {
        // @TODO change signatures of methods `setSelectedKeyWords` to match List<String>.
        JIExplorer.instance().getContext().setSelectedKeyWords(availKeyWordsList.getSelectedValuesList().toArray(new String[0]),
                getLastSelectedIndex());

        JIKeyWordPane.this.setChanged();
        JIKeyWordPane.this.notifyObservers(JIObservable.KEYWORDS_CHANGED);
    }


    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param o an observer to be added.
     * @throws NullPointerException if the parameter o is null.
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
     *
     * @param o the observer to be deleted.
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
     * @see java.util.Observable#hasChanged()
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
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
     * @param arg any object.
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
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

        for (int i = arrLocal.length - 1; i >= 0; i--) {
            ((JIObserver) arrLocal[i]).update(this, arg);
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
    private synchronized void setChanged() {
        this.changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has
     * already notified all of its observers of its most recent change,
     * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
     * This method is called automatically by the
     * <code>notifyObservers</code> methods.
     *
     * @see java.util.Observable#notifyObservers()
     * @see java.util.Observable#notifyObservers(java.lang.Object)
     */
    private synchronized void clearChanged() {
        this.changed = false;
    }

    /**
     * Tests if this object has changed.
     *
     * @return <code>true</code> if and only if the <code>setChanged</code>
     * method has been called more recently than the
     * <code>clearChanged</code> method on this object;
     * <code>false</code> otherwise.
     */
    public synchronized boolean hasChanged() {
        return this.changed;
    }

    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     *
     * @return the number of observers of this object.
     */
    public synchronized int countObservers() {
        return this.obs.size();
    }
}
