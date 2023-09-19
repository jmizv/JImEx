package de.jmizv.jiexplorer.gui.table;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIThumbnailCache;
import de.jmizv.jiexplorer.gui.AbstractDiskObjectList;
import de.jmizv.jiexplorer.gui.JIFileModel;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIExplorerContext;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIObserver;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;


public class JITableListPane extends AbstractDiskObjectList {
	/**
	 *
	 */
	private static final long serialVersionUID = -950091080332259780L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JITableListPane.class);

	public static String MY_COMPUTER_FOLDER_PATH = System.getProperty("java.io.tmpdir") + File.separator + "My Computer";

	private final JIFileTable table;
	private final ExecutorService pool;
	private final JScrollPane scrollPane;


	public JITableListPane() {
		this(null);
	}

	public JITableListPane(final OrderedDiskObjectList dObjList) {
		super();
		this.pool = Executors.newFixedThreadPool(2);

		if (dObjList != null) {
			this.table = new JIFileTable(dObjList);
		} else {
			this.table = new JIFileTable();
		}

		this.scrollPane = new JScrollPane();
		this.scrollPane.setBackground(this.table.getBackground());
		this.scrollPane.setViewportView(this.table);
		this.setLayout(new BorderLayout());
		this.add(this.scrollPane,BorderLayout.CENTER);
		this.scrollPane.setAutoscrolls(true);

		this.table.setIntercellSpacing(new Dimension(2,1));

		JIThumbnailCache.getInstance().invalidate();
	}

	public void addElement(final DiskObject dObj) {
		((JIFileTableModel)this.table.getModel()).addRow(dObj);
	}

	public void addFile(final File file) {
		this.pool.execute(new Runnable() {
			public void run() {
				if (file != null) {
					final JIFileTableModel model = (JIFileTableModel)JITableListPane.this.table.getModel();
					final DiskObject diskObj = new DiskObject(file);
					model.addRow(diskObj);
				}
			}
		});
	}

	public void loadDirectory(final File dir) {
		getRootPane().setBackground(this.table.getBackground());
		this.table.setChanged();
		this.table.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
		this.table.clearChanged();
		this.table.scrollRectToVisible(new Rectangle(0,0,10,10));

		if (!JIExplorer.loading) {
			this.pool.execute(new Runnable() {
				public void run() {

					try {getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					} catch (final RuntimeException e) {}
					((JIFileTableModel) JITableListPane.this.table.getModel()).setData();

					JITableListPane.this.table.setChanged();
					JITableListPane.this.table.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
					JITableListPane.this.table.clearChanged();

					setColumnWidths();

					try {getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} catch (final RuntimeException e) {}
					JITableListPane.this.table.scrollRectToVisible(new Rectangle(0,0,10,10));
				}
			});
		}
	}

	public final void reload() {
		this.pool.execute(new Runnable() {
			public void run() {
				((JIFileTableModel)JITableListPane.this.table.getModel()).reload();
				JITableListPane.this.table.scrollRectToVisible(new Rectangle(0,0,10,10));
			}
		});
	}

	public void resetPreferences() {
		this.table.initActions();
	}

	public void addObserver(final JIObserver o) {
		this.table.addObserver(o);
	}

	public int countObservers() {
		return this.table.countObservers();
	}

	public void deleteObserver(final JIObserver o) {
		this.table.deleteObserver(o);
	}

	public void deleteObservers() {
		this.table.deleteObservers();
	}

	public JIFileModel getFileListModel() {
		return this.table.getFileListModel();
	}

	public boolean hasChanged() {
		return this.table.hasChanged();
	}

	public void notifyObservers() {
		this.table.notifyObservers(null);
	}

	public void notifyObservers(final Object arg) {
		this.table.notifyObservers(arg);
	}

	public void update(final JIObservable observable, final Object obj) {
		if (obj instanceof String ) {
			log.debug("listObject = "+obj);
			if(((String)obj).equals(JIObservable.CATAGORY_CHANGED)) {
				this.table.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
				this.pool.execute(new Runnable() {
					public void run() {
						try { getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));}
						catch (final RuntimeException e) {}
						invalidate();
						((JIFileTableModel)JITableListPane.this.table.getModel()).setCategoryData();
						JITableListPane.this.table.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
						setColumnWidths();

						try { getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));}
						catch (final RuntimeException e) {}

						JITableListPane.this.table.scrollRectToVisible(new Rectangle(0,0,10,10));
					}
				});
				return;
			} else if(((String)obj).equals(JIObservable.KEYWORDS_CHANGED)) {
				this.table.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
				this.pool.execute(new Runnable() {
					public void run() {
						try { getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));}
						catch (final RuntimeException e) {}
						((JIFileTableModel)JITableListPane.this.table.getModel()).setKeyWordData();
						JITableListPane.this.table.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
						setColumnWidths();

						try { getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));}
						catch (final RuntimeException e) {}
					}
				});
				return;
			} else if(((String)obj).equals(JIObservable.DATE_CHANGED)) {
				this.table.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
				this.pool.execute(new Runnable() {
					public void run() {
						try { getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));}
						catch (final RuntimeException e) {}
						((JIFileTableModel)JITableListPane.this.table.getModel()).setDateData();
						JITableListPane.this.table.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
						setColumnWidths();

						try { getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));}
						catch (final RuntimeException e) {}
					}
				});
				return;
			}
		}
	}

	public JIExplorerContext getReloadContext(){
		return ((JIFileModel)this.table.getModel()).getReloadContext();
	}

	public void setReloadContext(final JIExplorerContext reloadContext){
		((JIFileModel)this.table.getModel()).setReloadContext(reloadContext);
	}

	public int[] getSelectedIndices() {
		return this.table.getSelectedRows();
	}

	public void restoreSelection(final DiskObject lastSelectedDObj) {
		this.table.clearSelection();

		this.table.setSelectionModel(new DefaultListSelectionModel());
		this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    this.table.setColumnSelectionAllowed(false);
	    this.table.setRowSelectionAllowed(true);
	    this.table.revalidate();

		final OrderedDiskObjectList dObjectList = getFileListModel().getDiskObjectList();
		final DiskObject[] indexValues = JIExplorer.instance().getContext().getSelectedDiskObjects();
		final int[] indices = new int[indexValues.length];

		for (int i=0; i<indexValues.length; i++) {
			indices[i] = dObjectList.indexOf(indexValues[i]);
			this.table.addRowSelectionInterval(indices[i], indices[i]);
		}

		if (lastSelectedDObj != null) {
			final int index = ((JIFileTableModel)this.table.getModel()).getDiskObjectList().indexOf(lastSelectedDObj);
			final Rectangle rec = this.table.getCellRect(index, 0, true);
			this.table.scrollRectToVisible(rec);
		}
		setColumnWidths();
	}

	protected void setColumnWidths() {
		this.table.getColumnModel().getColumn(0).setPreferredWidth(250);
		this.table.getColumnModel().getColumn(0).setMinWidth(250);
		this.table.getColumnModel().getColumn(0).setMaxWidth(500);

		this.table.getColumnModel().getColumn(1).setPreferredWidth(70);
		this.table.getColumnModel().getColumn(1).setMinWidth(70);
		this.table.getColumnModel().getColumn(1).setMaxWidth(140);

		this.table.getColumnModel().getColumn(2).setPreferredWidth(50);
		this.table.getColumnModel().getColumn(2).setMinWidth(50);
		this.table.getColumnModel().getColumn(2).setMaxWidth(100);

		this.table.getColumnModel().getColumn(3).setPreferredWidth(120);
		this.table.getColumnModel().getColumn(3).setMinWidth(120);
		this.table.getColumnModel().getColumn(0).setMaxWidth(240);

		this.table.getColumnModel().getColumn(4).setPreferredWidth(95);
		this.table.getColumnModel().getColumn(4).setMinWidth(95);
		this.table.getColumnModel().getColumn(0).setMaxWidth(180);

		this.table.getColumnModel().getColumn(5).setPreferredWidth(1000);
		this.table.getColumnModel().getColumn(5).setMinWidth(0);
		this.table.getColumnModel().getColumn(5).setMaxWidth(2000);
	}

	public JComponent getRendererComponent() {
		return null;
	}
}
