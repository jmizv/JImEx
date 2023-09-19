/*
 * JIListModel.java
 *
 * Created on March 27, 2005, 1:48 AM
 */

package de.jmizv.jiexplorer.gui.thumb;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.JIFileModel;
import de.jmizv.jiexplorer.gui.StatusBarPanel;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.gui.tree.JITreeNode;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.FileSortbyDate;
import de.jmizv.jiexplorer.util.FileSortbyName;
import de.jmizv.jiexplorer.util.FileSortbyPath;
import de.jmizv.jiexplorer.util.FileSortbySize;
import de.jmizv.jiexplorer.util.FileSortbyType;
import de.jmizv.jiexplorer.util.JIExplorerContext;
import de.jmizv.jiexplorer.util.JIUtility;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;


public class JIListModel extends AbstractListModel implements JIFileModel {
	/**
	 *
	 */
	private static final long serialVersionUID = -6616279538578529897L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIListModel.class);

	private final OrderedDiskObjectList delegate;

	private JIExplorerContext reloadContext;
	private boolean loading = false;
	private final JList list;

	public JIListModel(final JList list) {
		this.list = list;
		this.delegate = new OrderedDiskObjectList();
	}

	public JIListModel(final JList list, final OrderedDiskObjectList delegate) {
		this.list = list;
		this.delegate = delegate;
	}

	public synchronized boolean loading() {
		return this.loading;
	}

	public void sort(final int type) {
		this.delegate.sort(type);
		synchronized (this) {fireContentsChanged(this, 0, getSize()-1); this.loading = false;};
	}

	public synchronized void notifyAsUpdated(final int index) {
		fireContentsChanged(this, index, index);
	}

	public DiskObject[] copyInto(){
		final List<DiskObject> al = new ArrayList<DiskObject>();
		for (int i=0; i<this.getSize(); i++) {
			final DiskObject dObj = elementAt(i);
			if (dObj.getType() == DiskObject.TYPE_FILE) {
				al.add(dObj);
			}
		}
		final DiskObject[] dol = new DiskObject[al.size()];
		return al.toArray(dol);
	}

	public OrderedDiskObjectList getDiskObjectList() {
		return this.delegate;
	}

	public void setData() {
		if (this.loading) {
			return;
		}

		JIExplorer.instance().getContext().setState(JIExplorerContext.DIRECTORY_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		if (this.reloadContext.getSelectedDirNodes() != null) {
			synchronized (this) {this.loading = true;};
			this.list.getSelectionModel().setValueIsAdjusting(true);
			this.list.requestFocusInWindow();
			getData(this.reloadContext.getSelectedDirNodes());
			JIExplorer.instance().getContext().setImageCnt(getSize());
			synchronized (this) {fireContentsChanged(this, 0, getSize()-1); this.loading = false;};
			this.list.getSelectionModel().setValueIsAdjusting(false);
		}
	}

	public void setKeyWordData() {
		if (this.loading) {
			return;
		}
		synchronized (this) {this.loading = true;};

		JIExplorer.instance().getContext().setState(JIExplorerContext.KEY_WORDS_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		this.list.setValueIsAdjusting(true);
		clear();
		if (JIPreferences.getInstance().getQueryAndOr() == 0) {
			JIThumbnailService.getInstance().getKeyWordImagesAND(this.reloadContext.getSelectedKeyWords(), this.delegate);
		} else {
			JIThumbnailService.getInstance().getKeyWordImagesOR(this.reloadContext.getSelectedKeyWords(), this.delegate);
		}
		this.list.setValueIsAdjusting(false);
		JIExplorer.instance().getContext().setImageCnt(getSize());
		synchronized (this) {fireContentsChanged(this, 0, getSize()-1); this.loading = false;};
		if (this.delegate.size()>0) {
			this.list.setSelectedIndex(0);
		}
		this.list.requestFocusInWindow();
	}

	public void setCategoryData() {
		if (this.loading) {
			return;
		}
		synchronized (this) {this.loading = true;};

		JIExplorer.instance().getContext().setState(JIExplorerContext.CATEGORY_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		this.list.setValueIsAdjusting(true);
		clear();
		if (JIPreferences.getInstance().getQueryAndOr() == 0) {
			JIThumbnailService.getInstance().getCategoryImagesAND(this.reloadContext.getSelectedCatNodes(), this.delegate);
		} else {
			JIThumbnailService.getInstance().getCategoryImages(this.reloadContext.getSelectedCatNodes(), this.delegate);
		}
		this.list.setValueIsAdjusting(false);
		JIExplorer.instance().getContext().setImageCnt(getSize());
		synchronized (this) {fireContentsChanged(this, 0, getSize()-1); this.loading = false;};
		if (this.delegate.size()>0) {
			this.list.setSelectedIndex(0);
		}
		this.list.requestFocusInWindow();
	}

	public void setDateData() {
		if (this.loading) {
			return;
		}
		synchronized (this) {this.loading = true;};

		JIExplorer.instance().getContext().setState(JIExplorerContext.DATE_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		this.list.setValueIsAdjusting(true);
		clear();

		JIThumbnailService.getInstance().getDateImages(this.reloadContext.getSelectedDateNodes(), this.delegate);
		this.list.setValueIsAdjusting(false);
		JIExplorer.instance().getContext().setImageCnt(getSize());
		synchronized (this) {fireContentsChanged(this, 0, getSize()-1); this.loading = false;};
		this.list.setSelectedIndex(0);
		this.list.requestFocusInWindow();
	}

	public void reload() {
		JIExplorer.instance().setContext(this.reloadContext);

		if (this.reloadContext.getState() == JIExplorerContext.DIRECTORY_STATE) {
			setData();
		}

		if (this.reloadContext.getState() == JIExplorerContext.KEY_WORDS_STATE) {
			setKeyWordData();
		}

		if (this.reloadContext.getState() == JIExplorerContext.CATEGORY_STATE) {
			setCategoryData();
		}

		if (this.reloadContext.getState() == JIExplorerContext.DATE_STATE) {
			setDateData();
		}
	}

	// Gets the table data from the left tree.
	public void getData(final Vector<JITreeNode> nodes) {
		final File selectedDir = nodes.elementAt(0).getFile();
		if (selectedDir == null) {
			return;
		}

		log.debug("JIListModel::getData - Start - "+ System.currentTimeMillis());

		final File[] files = selectedDir.listFiles();

		StatusBarPanel.getInstance().getProgressBar().setMaximum(files.length);
		StatusBarPanel.getInstance().getProgressBar().setValue(0);
				
		// The selected dir or driver might have no children.
		if (files == null) {
			return;
		}

		log.debug("JIListModel::getData - Sort Dir List - "+ System.currentTimeMillis());

		switch(JIPreferences.getInstance().getThumbnailSortOrder()) {
		case 4:
			Arrays.sort(files, new FileSortbyPath<File>());
			break;
		case 3:
			Arrays.sort(files, new FileSortbyDate<File>());
			break;
		case 2:
			Arrays.sort(files, new FileSortbySize<File>());
			break;
		case 1:
			Arrays.sort(files, new FileSortbyType<File>());
			break;
		case 0:
		default:
			Arrays.sort(files, new FileSortbyName<File>());
		}

		final int fileNum = files.length;

		clear();
		log.debug("JIListModel::getData - Add Files to List - "+ System.currentTimeMillis());
		StatusBarPanel.getInstance().getLblSrc().setText("Loading ... ");

		for (int i = 0; i < fileNum; i++) {
			final File file = files[i];

			StatusBarPanel.getInstance().getLblSrc().setText("Loading ... "+(int)(100*(((double)i)/(double)fileNum)+1)+"%");
			
			if (!file.isDirectory()) {
				final String suffix = JIUtility.suffix(file.getName());
				if ((suffix != null) && JIUtility.isSupportedImage(suffix)) {
					final DiskObject dObj = new DiskObject(file);
					JIThumbnailService.getInstance().getDiskObject(dObj);
					addElement(dObj);
					if (size() == 1) {
						this.list.setSelectedIndex(0);
					}
				}
			}
		}
		log.debug("JIListModel::getData - Done - "+ System.currentTimeMillis());
		StatusBarPanel.getInstance().getLblSrc().setText("Directory: "+selectedDir.getName());
		
		return;
	}

	/**
	 * @return the reloadContext
	 */
	public synchronized final JIExplorerContext getReloadContext() {
		return this.reloadContext;
	}

	/**
	 * @param reloadContext the reloadContext to set
	 */
	public synchronized final void setReloadContext(final JIExplorerContext reloadContext) {
		this.reloadContext = reloadContext;
	}

	/**
	 * @return the loading
	 */
	public synchronized final boolean isLoading() {
		return this.loading;
	}

	public int getSize() {
		if (this.delegate == null) {
			return 0;
		}
		return this.delegate.size();
	}

	public Object getElementAt(final int index) {
		if (this.delegate == null) {
			return null;
		}

		return ((index>=this.delegate.size())||(index<0))?null:this.delegate.elementAt(index);
	}

	public void copyInto(final Object anArray[]) {
		this.delegate.copyInto(anArray);
	}

	public void trimToSize() {
		this.delegate.trimToSize();
	}


	public void ensureCapacity(final int minCapacity) {
		this.delegate.ensureCapacity(minCapacity);
	}


	public void setSize(final int newSize) {
		final int oldSize = this.delegate.size();
		this.delegate.setSize(newSize);
		if (oldSize > newSize) {
			fireIntervalRemoved(this, newSize, oldSize-1);
		} else if (oldSize < newSize) {
			fireIntervalAdded(this, oldSize, newSize-1);
		}
	}

	public int capacity() {
		return this.delegate.capacity();
	}

	public int size() {
		return this.delegate.size();
	}

	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	public Enumeration<?> elements() {
		return this.delegate.elements();
	}

	public boolean contains(final DiskObject elem) {
		return this.delegate.contains(elem);
	}

	public int indexOf(final DiskObject elem) {
		return this.delegate.indexOf(elem);
	}

	public int indexOf(final DiskObject elem, final int index) {
		return this.delegate.indexOf(elem, index);
	}

	public int lastIndexOf(final DiskObject elem) {
		return this.delegate.lastIndexOf(elem);
	}

	public int lastIndexOf(final DiskObject elem, final int index) {
		return this.delegate.lastIndexOf(elem, index);
	}

	public DiskObject elementAt(final int index) {
		return this.delegate.elementAt(index);
	}

	public DiskObject firstElement() {
		return this.delegate.firstElement();
	}

	public DiskObject lastElement() {
		return this.delegate.lastElement();
	}

	public void setElementAt(final DiskObject obj, final int index) {
		this.delegate.setElementAt(obj, index);
		fireContentsChanged(this, index, index);
	}

	public void removeElementAt(final int index) {
		this.delegate.removeElementAt(index);
		fireIntervalRemoved(this, index, index);
	}

	public void insertElementAt(final DiskObject obj, final int index) {
		this.delegate.insertElementAt(obj, index);
		fireIntervalAdded(this, index, index);
	}

	public void addElement(final DiskObject obj) {
		final int index = this.delegate.size();
		this.delegate.addElement(obj);
		fireIntervalAdded(this, index, index);
	}

	public boolean removeElement(final DiskObject obj) {
		final int index = indexOf(obj);
		final boolean rv = this.delegate.removeElement(obj);
		if (index >= 0) {
			fireIntervalRemoved(this, index, index);
		}
		return rv;
	}

	public void removeAllElements() {
		final int index1 = this.delegate.size()-1;
		this.delegate.removeAllElements();
		if (index1 >= 0) {
			fireIntervalRemoved(this, 0, index1);
		}
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}


	public DiskObject[] toArray() {
		final DiskObject[] rv = new DiskObject[this.delegate.size()];
		this.delegate.copyInto(rv);
		return rv;
	}

	public DiskObject get(final int index) {
		return this.delegate.elementAt(index);
	}


	public DiskObject set(final int index, final DiskObject element) {
		final DiskObject rv = this.delegate.elementAt(index);
		this.delegate.setElementAt(element, index);
		fireContentsChanged(this, index, index);
		return rv;
	}

	public void add(final int index, final DiskObject element) {
		this.delegate.insertElementAt(element, index);
		fireIntervalAdded(this, index, index);
	}

	public DiskObject remove(final int index) {
		final DiskObject rv = this.delegate.elementAt(index);
		this.delegate.removeElementAt(index);
		fireIntervalRemoved(this, index, index);
		return rv;
	}

	/**
	 * Removes all of the elements from this list.  The list will
	 * be empty after this call returns (unless it throws an exception).
	 */
	 public void clear() {
		final int index1 = this.delegate.size()-1;
		this.delegate.removeAllElements();
		if (index1 >= 0) {
			fireIntervalRemoved(this, 0, index1);
		}
	}

	public void removeRange(final int fromIndex, final int toIndex) {
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex must be <= toIndex");
		}
		for(int i = toIndex; i >= fromIndex; i--) {
			this.delegate.removeElementAt(i);
		}
		fireIntervalRemoved(this, fromIndex, toIndex);
	}

        /*
        public void addAll(Collection c) {
        }

        public void addAll(int index, Collection c) {
        }
        */
}
