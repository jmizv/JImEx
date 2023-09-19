package de.jmizv.jiexplorer.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


public class OrderedDiskObjectList extends Vector<DiskObject> {

	/**
	 *
	 */
	private static final long serialVersionUID = -6210090952004948225L;
	private static final int SORT_BY_PATH = 4;
	private static final int SORT_BY_DATE = 3;
	private static final int SORT_BY_SIZE = 2;
	private static final int SORT_BY_TYPE = 1;
	private static final int SORT_BY_NAME = 0;

	private static final Vector<Comparator<DiskObject>> comparators = new Vector<Comparator<DiskObject>>(5);

	static {
		comparators.add(SORT_BY_NAME, new SortName());
		comparators.add(SORT_BY_TYPE, new SortType());
		comparators.add(SORT_BY_SIZE, new SortSize());
		comparators.add(SORT_BY_DATE, new SortDate());
		comparators.add(SORT_BY_PATH, new SortPath());
	};


	private int currentIndex = 0;

	public OrderedDiskObjectList() {
		super();
	}

	public OrderedDiskObjectList(final int size) {
		super(size);
	}

	public OrderedDiskObjectList(final DiskObject[] list) {
		super(list.length);
		this.elementData = list;
		this.setSize(this.elementData.length);
	}

	public OrderedDiskObjectList(final DiskObject[] list, final int index) {
		super(list.length);
		this.elementData = list;
		this.setSize(this.elementData.length);
		this.currentIndex = index;
	}


	@Override
	public final boolean isEmpty() {
		return (size() > 0) ? false : true;
	}

	protected final int nextImageIndex() {
		return (this.currentIndex < size() - 1) ? ++this.currentIndex:(this.currentIndex = 0);
	}

	protected final int previousImageIndex() {
		return ((this.currentIndex > 0) ? --this.currentIndex:(this.currentIndex = size() - 1));
	}

	public final DiskObject currentImage() {
		if (isEmpty()) {
			return null;
		}
		return elementAt(this.currentIndex);
	}

	public final DiskObject previousImage() {
		if (isEmpty()) {
			return null;
		}
		return elementAt(previousImageIndex());
	}

	public final DiskObject nextImage() {
		if (isEmpty()) {
			return null;
		}
		return elementAt(nextImageIndex());
	}

	public final DiskObject nextImageFile(final int dir) {
		//  0 = forward, 1 = backward, 2 = random
		switch (dir) {
		case 2:
			return randImage();
		case 1:
			return previousImage();
		default:
			return nextImage();
		}
	}

	public final DiskObject randImage() {
		this.currentIndex = (int) Math.round(( size() - 1 ) * Math.random());
		return elementAt(this.currentIndex);
	}

	public final void sort(final int type) {
		Collections.sort(this,comparators.elementAt(type>4?0:type));
	}

	/**
	 * @return the currentIndex
	 */
	public synchronized final int getCurrentIndex() {
		return this.currentIndex;
	}

	/**
	 * @param currentIndex the currentIndex to set
	 */
	public synchronized final void setCurrentIndex(final int currentIndex) {
		this.currentIndex = currentIndex;
	}

}

class SortDate implements Comparator<DiskObject> {
	public int compare(final DiskObject a, final DiskObject b) {
		if (a == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?-1:1;
		}
		if (b == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?1:-1;
		}
		final int result = (!JIPreferences.getInstance().isThumbnailSortDesend())
			? (int)(a.getLastModified() - b.getLastModified()) : (int)(b.getLastModified() - a.getLastModified());

		return (result != 0)?result:((!JIPreferences.getInstance().isThumbnailSortDesend())
			? a.getFile().getAbsolutePath().compareTo(b.getFile().getAbsolutePath())
			: b.getFile().getAbsolutePath().compareTo(a.getFile().getAbsolutePath()));
	}
}

class SortSize implements Comparator<DiskObject> {
	public int compare(final DiskObject a, final DiskObject b) {
		if (a == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?-1:1;
		}
		if (b == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?1:-1;
		}
		final int result = (!JIPreferences.getInstance().isThumbnailSortDesend())
			? (int)(a.getLength() - b.getLength()) : (int)(b.getLength() - a.getLength());

		return (result != 0)?result:((!JIPreferences.getInstance().isThumbnailSortDesend())
			? a.getFile().getAbsolutePath().compareTo(b.getFile().getAbsolutePath())
			: b.getFile().getAbsolutePath().compareTo(a.getFile().getAbsolutePath()));
	}
}

class SortType implements Comparator<DiskObject> {
	public int compare(final DiskObject a, final DiskObject b) {
		if (a == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?-1:1;
		}
		if (b == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?1:-1;
		}
		return (!JIPreferences.getInstance().isThumbnailSortDesend())
			? (a.getSuffix()+a.getName()+a.getPath()).compareToIgnoreCase((b.getSuffix()+b.getName()+b.getPath()))
			: (b.getSuffix()+b.getName()+b.getPath()).compareToIgnoreCase((a.getSuffix()+a.getName()+a.getPath()));
	}
}

class SortName implements Comparator<DiskObject> {
	public int compare(final DiskObject a, final DiskObject b) {
		if (a == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?-1:1;
		}
		if (b == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?1:-1;
		}
		return (!JIPreferences.getInstance().isThumbnailSortDesend())
			? a.getName().compareToIgnoreCase(b.getName()) : b.getName().compareToIgnoreCase(a.getName());
	}
}

class SortPath implements Comparator<DiskObject> {
	public int compare(final DiskObject a, final DiskObject b) {
		if (a == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?-1:1;
		}
		if (b == null) {
			return (JIPreferences.getInstance().isThumbnailSortDesend())?1:-1;
		}
		return (!JIPreferences.getInstance().isThumbnailSortDesend())
			? a.getFile().getAbsolutePath().compareTo(b.getFile().getAbsolutePath())
			: b.getFile().getAbsolutePath().compareTo(a.getFile().getAbsolutePath());
	}
}