package de.jmizv.jiexplorer.gui.thumb;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Rectangle;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.gui.AbstractDiskObjectList;
import de.jmizv.jiexplorer.gui.JIFileModel;
import de.jmizv.jiexplorer.gui.JImagePanel;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIExplorerContext;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIObserver;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;


public class JIThumbnailListPane extends AbstractDiskObjectList {
	/**
	 *
	 */
	private static final long serialVersionUID = 3672478063192038911L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIThumbnailListPane.class);

	public static String MY_COMPUTER_FOLDER_PATH = System.getProperty("java.io.tmpdir") + File.separator + "My Computer";

	private final JIThumbnailList list;
	private final ExecutorService pool;
	private final JScrollPane scrollPane;
	private final JImagePanel imagepanel;

	public JIThumbnailListPane(final Frame frame) {
		this(frame, null);
	}

	public JIThumbnailListPane(final Frame frame,final OrderedDiskObjectList dObjList) {
		super();
		this.pool = Executors.newFixedThreadPool(2);
		this.scrollPane = new JScrollPane();
		if (dObjList != null) {
			this.list = new JIThumbnailList(frame, this.scrollPane, dObjList);
		} else {
			this.list = new JIThumbnailList(frame, this.scrollPane);
		}
		this.list.addListSelectionListener(new JIListSelectionAdapter());
		this.list.setFrame();
		this.scrollPane.setViewportView(this.list);
		this.setLayout(new BorderLayout());
		this.add(this.scrollPane,BorderLayout.CENTER);
		this.scrollPane.setAutoscrolls(true);
		this.imagepanel = null;
	}

	public JIThumbnailListPane(final Frame frame, final int scrollMode, final boolean viewer) {
		this(frame, scrollMode, viewer, null);
	}
	public JIThumbnailListPane(final Frame frame, final int scrollMode, final boolean viewer,final OrderedDiskObjectList dObjList) {
		super();
		this.pool = Executors.newFixedThreadPool(2);
		this.scrollPane = new JScrollPane();

		if (dObjList != null) {
			this.list = new JIThumbnailList(frame, this.scrollPane, viewer?1:scrollMode, dObjList);
		} else {
			this.list = new JIThumbnailList(frame, this.scrollPane, viewer?1:scrollMode);
		}
		this.list.addListSelectionListener(new JIListSelectionAdapter());
		this.list.setFrame();
		this.scrollPane.setViewportView(this.list);
		if (viewer) {
			this.imagepanel = new JImagePanel(true);
			final JSplitPane jSplitPane = new JSplitPane();
			jSplitPane.setBorder(null);
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.add(this.imagepanel, JSplitPane.TOP);
			jSplitPane.add(this.scrollPane, JSplitPane.BOTTOM);
			final int iconHeight = JIPreferences.getInstance().getIconDim().height + 80 + jSplitPane.getDividerSize();
			jSplitPane.setDividerLocation(JIPreferences.getInstance().getBrowserDim().height-iconHeight);
			jSplitPane.setResizeWeight(1);
			this.list.addObserver(this.imagepanel);
			this.setLayout(new BorderLayout());
			this.add(jSplitPane,BorderLayout.CENTER);
		} else {
			this.setLayout(new BorderLayout());
			this.add(this.scrollPane,BorderLayout.CENTER);
			this.imagepanel = null;
		}
		this.scrollPane.setAutoscrolls(true);
	}

	public void loadDirectory(final File dir) {

		if (!JIExplorer.loading) {
			try {getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			} catch (final RuntimeException e) {}
			this.pool.execute(new Runnable() {
				public void run() {

					((JIListModel) JIThumbnailListPane.this.list.getModel()).setData();
					JIThumbnailListPane.this.list.setChanged();
					JIThumbnailListPane.this.list.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
					JIThumbnailListPane.this.list.clearChanged();
					//list.ensureIndexIsVisible(0);
					try {getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} catch (final RuntimeException e) {}
				}
			});
		} else {
			this.list.setChanged();
			this.list.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
			this.list.clearChanged();
		}
	}


	public void addFile(final File file) {
		this.pool.execute(new Runnable() {
			public void run() {
				if (file != null) {
					final JIListModel model = (JIListModel)JIThumbnailListPane.this.list.getModel();
					final DiskObject diskObj = new DiskObject(file);
					if (!model.contains(diskObj)) {
						model.addElement(diskObj);
						model.notifyAsUpdated(model.indexOf(diskObj));
					}
				}
			}
		});
	}

	public void update(final JIObservable observable, final Object obj) {
		if (obj instanceof String ) {
			log.debug("listObject = "+obj);
			if(((String)obj).equals(JIObservable.CATAGORY_CHANGED)) {
				this.list.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
				try {  JIExplorer.instance().getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));}
				catch (final RuntimeException e) {}
				this.pool.execute(new Runnable() {
					public void run() {
						((JIListModel)JIThumbnailListPane.this.list.getModel()).setCategoryData();
						JIThumbnailListPane.this.list.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
						try { JIExplorer.instance().getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));}
						catch (final RuntimeException e) {}
					}
				});
				return;
			} else if(((String)obj).equals(JIObservable.KEYWORDS_CHANGED)) {
				this.list.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
				try { JIExplorer.instance().getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));}
				catch (final RuntimeException e) {}
				this.pool.execute(new Runnable() {
					public void run() {
						((JIListModel)JIThumbnailListPane.this.list.getModel()).setKeyWordData();
						JIThumbnailListPane.this.list.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
						try {  JIExplorer.instance().getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));}
						catch (final RuntimeException e) {}
					}
				});
				return;
			} else if(((String)obj).equals(JIObservable.DATE_CHANGED)) {
				this.list.notifyStatusBar(JIObservable.DIRECTORY_LOADING);
				try {  JIExplorer.instance().getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));}
				catch (final RuntimeException e) {}
				this.pool.execute(new Runnable() {
					public void run() {
						((JIListModel)JIThumbnailListPane.this.list.getModel()).setDateData();
						JIThumbnailListPane.this.list.notifyStatusBar(JIObservable.DIRECTORY_SIZE);
						try {  JIExplorer.instance().getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));}
						catch (final RuntimeException e) {}
					}
				});
				return;
			}
		}
	}

	public final void reload() {
		(this.list.getThumbnailListModel()).reload();
	}

	public final void resetPreferences() {
		this.list.reset();
	}

	public void addObserver(final JIObserver o) {
		this.list.addObserver(o);
	}

	public void deleteObserver(final JIObserver o) {
		this.list.deleteObserver(o);
	}

	public void notifyObservers() {
		this.list.notifyObservers(null);
	}

	public void notifyObservers(final Object arg) {
		this.list.notifyObservers(arg);
	}

	public void deleteObservers() {
		this.list.deleteObservers();
	}

	protected void setChanged() {
		this.list.setChanged();
	}

	protected void clearChanged() {
		this.list.clearChanged();
	}

	public boolean hasChanged() {
		return this.list.hasChanged();
	}

	public int countObservers() {
		return this.list.countObservers();
	}

	public JIFileModel getFileListModel() {
		return this.list.getThumbnailListModel();
	}
	public JIExplorerContext getReloadContext(){
		return this.list.getThumbnailListModel().getReloadContext();
	}
	public void setReloadContext(final JIExplorerContext reloadContext){
		this.list.getThumbnailListModel().setReloadContext(reloadContext);
	}

	final class JIListSelectionAdapter implements javax.swing.event.ListSelectionListener {

		JIListSelectionAdapter() { }

		public final void valueChanged(final ListSelectionEvent e) {
			final Thread runner = new Thread() {
				@Override
				public void run() {
					Runnable runnable = new Runnable() {
						public void run() {
							JIThumbnailListPane.this.list.listValueChanged(e);
						}
					};
					SwingUtilities.invokeLater(runnable);
				}
			};
			runner.start();
		}
	}

	public int[] getSelectedIndices() {
		return this.list.getSelectedIndices();
	}

	public void restoreSelection(final DiskObject lastSelectedDObj) {
		final OrderedDiskObjectList dObjectList = getFileListModel().getDiskObjectList();
		final DiskObject[] indexValues = JIExplorer.instance().getContext().getSelectedDiskObjects();
		final int[] indices = new int[indexValues.length];
		for (int i=0; i<indexValues.length; i++) {
			indices[i] = dObjectList.indexOf(indexValues[i]);
		}
		if ((indices != null) && (indices.length > 0)) {
			this.list.setSelectedIndices(indices);
		}
		if ((lastSelectedDObj != null) && (this.list.getThumbnailListModel().getSize() > 0)) {
			final int index = this.list.getThumbnailListModel().indexOf(lastSelectedDObj);
			final Rectangle rec = this.list.getCellBounds(index, index);

			if (this.list.getLayoutOrientation() == JList.HORIZONTAL_WRAP) {
				final int loc = ((JSplitPane)getParent()).getDividerLocation();
				final int size = ((JSplitPane)getParent()).getDividerSize();
				final int width = getParent().getWidth();
				final int mywidth = width-(loc+size);
				final int rowCnt = (int)(mywidth / JIPreferences.getInstance().getIconDim().getWidth());
				rec.y = JIPreferences.getInstance().getIconDim().height*((index/rowCnt));
			}
			this.list.scrollRectToVisible(rec);
		}
	}

	public JComponent getRendererComponent() {
		final Object obj = this.list.getCellRenderer();
		return (((obj != null) && (obj instanceof JComponent))?(JComponent)obj:null);
	}
}
