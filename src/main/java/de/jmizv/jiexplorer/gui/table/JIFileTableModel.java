package de.jmizv.jiexplorer.gui.table;

import java.awt.Rectangle;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

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


public class JIFileTableModel  extends AbstractTableModel implements Serializable, JIFileModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1694565342712630802L;

	private static final Vector<String> colNames = new Vector<String>(6);

	static {
		colNames.add("Name");
		colNames.add("Size");
		colNames.add("Type");
		colNames.add("Date");
		colNames.add("Dimensions");
		colNames.add("");
	};

	protected OrderedDiskObjectList    dataVector;

    /** The <code>Vector</code> of column identifiers. */
    protected Vector    columnIdentifiers;

    private JIExplorerContext reloadContext;
	private boolean loading = false;
	private final JTable table;

	public JIFileTableModel(final JTable table) {
		super();
		this.table = table;
		setColumnIdentifiers(colNames);
	}

	public JIFileTableModel(final JTable table, final OrderedDiskObjectList dataVector) {
		super();
		this.table = table;
		this.dataVector = dataVector;
		setColumnIdentifiers(colNames);
	}

	public DiskObject[] diskObjects() {
		if ((this.dataVector != null) && (this.dataVector.size() > 0)) {
			final DiskObject[] results = new DiskObject[this.dataVector.size()];
			int cnt = 0;
			for (final DiskObject dObj: this.dataVector) {
				results[cnt++] = dObj;
			}
			return results;
		}
		return null;
	}

	public void sort(final int type) {
		this.dataVector.sort(type);
		synchronized (this) {fireTableRowsUpdated(0, this.dataVector.size()-1); this.loading = false;};
	}

	public OrderedDiskObjectList getDiskObjectList() {
		return this.dataVector;
	}

	@Override
	public Class<?> getColumnClass(final int c) {
		if (c == 0) {
			return DiskObject.class;
		}

        return String.class;
    }

	@Override
	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	public synchronized boolean loading() {
		return this.loading;
	}

	public void setData() {
		if (this.loading) {
			return;
		}

		JIExplorer.instance().getContext().setState(JIExplorerContext.DIRECTORY_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		if (this.reloadContext.getSelectedDirNodes() != null) {
			synchronized (this) {this.loading = true;};
			this.setDataVector(getData(this.reloadContext.getSelectedDirNodes()), colNames);
			JIExplorer.instance().getContext().setImageCnt(getRowCount());
			synchronized (this) {this.fireTableDataChanged(); this.loading = false;};
			if (this.dataVector.size()>0) {
				this.table.setRowSelectionInterval(0, 0);
			}
			this.table.scrollRectToVisible(new Rectangle(0,0,10,10));
			this.table.requestFocusInWindow();
		}
	}

	public void setKeyWordData() {
		if (this.loading) {
			return;
		}
		synchronized (this) {this.loading = true;};

		JIExplorer.instance().getContext().setState(JIExplorerContext.KEY_WORDS_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		if (JIPreferences.getInstance().getQueryAndOr() == 0) {
			this.setDataVector(JIThumbnailService.getInstance().getKeyWordImagesAND(this.reloadContext.getSelectedKeyWords(),null), colNames);
		} else {
			this.setDataVector(JIThumbnailService.getInstance().getKeyWordImagesOR(this.reloadContext.getSelectedKeyWords(),null), colNames);
		}
		JIExplorer.instance().getContext().setImageCnt(getRowCount());
		synchronized (this) {fireTableDataChanged(); this.loading = false;};
		if (this.dataVector.size()>0) {
			this.table.setRowSelectionInterval(0, 0);
		}
		this.table.scrollRectToVisible(new Rectangle(0,0,10,10));
		this.table.requestFocusInWindow();
	}

	public void setCategoryData() {
		if (this.loading) {
			return;
		}
		synchronized (this) {this.loading = true;};

		JIExplorer.instance().getContext().setState(JIExplorerContext.CATEGORY_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		if (JIPreferences.getInstance().getQueryAndOr() == 0) {
			this.setDataVector(JIThumbnailService.getInstance().getCategoryImagesAND(this.reloadContext.getSelectedCatNodes(),null), colNames);
		} else {
			this.setDataVector(JIThumbnailService.getInstance().getCategoryImages(this.reloadContext.getSelectedCatNodes(),null), colNames);
		}
		JIExplorer.instance().getContext().setImageCnt(getRowCount());
		synchronized (this) {fireTableDataChanged(); this.loading = false;};
		if (this.dataVector.size()>0) {
			this.table.setRowSelectionInterval(0, 0);
		}
		this.table.scrollRectToVisible(new Rectangle(0,0,10,10));
		this.table.requestFocusInWindow();
	}

	public void setDateData() {
		if (this.loading) {
			return;
		}
		synchronized (this) {this.loading = true;};

		JIExplorer.instance().getContext().setState(JIExplorerContext.DATE_STATE);
		this.reloadContext = JIExplorer.instance().getContext();

		this.setDataVector(JIThumbnailService.getInstance().getDateImages(this.reloadContext.getSelectedDateNodes(),null), colNames);
		JIExplorer.instance().getContext().setImageCnt(getRowCount());
		synchronized (this) {fireTableDataChanged(); this.loading = false;};
		if (this.dataVector.size()>0) {
			this.table.setRowSelectionInterval(0, 0);
		}
		this.table.scrollRectToVisible(new Rectangle(0,0,10,10));
		this.table.requestFocusInWindow();
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


	private OrderedDiskObjectList getData(final Vector<JITreeNode> nodes) {
		final File selectedDir = nodes.elementAt(0).getFile();
		final OrderedDiskObjectList diskObjects = new OrderedDiskObjectList();
		if (selectedDir == null) {
			return null;
		}


		final File[] files = selectedDir.listFiles();

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

		// The selected dir or driver might have no children.
		if (files == null) {
			return diskObjects;
		}
		StatusBarPanel.getInstance().getLblSrc().setText("Loading ... ");

		final int fileNum = files.length;

		for (int i = 0; i < fileNum; i++) {
			final File file = files[i];

			StatusBarPanel.getInstance().getLblSrc().setText("Loading ... "+(int)(100*(((double)i)/(double)fileNum)+1)+"%");

			if (!file.isDirectory()) {
				final String suffix = JIUtility.suffix(file.getName());
				if ((suffix != null) && JIUtility.isSupportedImage(suffix)) {
					final DiskObject dObj = new DiskObject(file);
					JIThumbnailService.getInstance().getDiskObject(dObj);
					diskObjects.add(dObj);
				}
			}
		}
		StatusBarPanel.getInstance().getLblSrc().setText("Directory: "+selectedDir.getName());

		return diskObjects;
	}

	public DiskObject[] copyInto(){
		final List<DiskObject> al = new ArrayList<DiskObject>();
		for (int i=0; i<this.getRowCount(); i++) {
			final DiskObject dObj = (DiskObject)this.getValueAt(0, i);
			if (dObj.getType() == DiskObject.TYPE_FILE) {
				al.add(dObj);
			}
		}
		final DiskObject[] dol = new DiskObject[al.size()];
		return al.toArray(dol);
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

    public Vector getDataVector() {
        return this.dataVector;
    }

    private static Vector nonNullVector(final Vector v) {
    	return (v != null) ? v : new Vector();
    }

    public void setDataVector(final OrderedDiskObjectList dataVector, final Vector columnIdentifiers) {
    	this.dataVector = (dataVector != null) ? dataVector : new OrderedDiskObjectList(1);
    	this.columnIdentifiers = nonNullVector(columnIdentifiers);
    	fireTableStructureChanged();
    }

    public void newDataAvailable(final TableModelEvent event) {
    	fireTableChanged(event);
    }


//  Manipulating rows

    /**
     *  Equivalent to <code>fireTableChanged</code>.
     *
     *  @param event the change event
     *
     */
    public void rowsRemoved(final TableModelEvent event) {
    	fireTableChanged(event);
    }

    public void setNumRows(final int rowCount) {
    	final int old = getRowCount();
    	if (old == rowCount) {
			return;
		}
    	this.dataVector.setSize(rowCount);
    	if (rowCount <= old) {
			fireTableRowsDeleted(rowCount, old-1);
		} else {
			fireTableRowsInserted(old, rowCount-1);
		}
    }

    public void setRowCount(final int rowCount) {
    	setNumRows(rowCount);
    }

    public void addRow(final DiskObject rowData) {
    	insertRow(getRowCount(), rowData);
    }

    public void insertRow(final int row, final DiskObject rowData) {
    	this.dataVector.insertElementAt(rowData, row);
    	fireTableRowsInserted(row, row);
    }

    public void removeRow(final int row) {
    	this.dataVector.removeElementAt(row);
    	fireTableRowsDeleted(row, row);
    }


//  Manipulating columns


    /**
     * Replaces the column identifiers in the model.  If the number of
     * <code>newIdentifier</code>s is greater than the current number
     * of columns, new columns are added to the end of each row in the model.
     * If the number of <code>newIdentifier</code>s is less than the current
     * number of columns, all the extra columns at the end of a row are
     * discarded. <p>
     *
     * @param   columnIdentifiers  vector of column identifiers.  If
     *				<code>null</code>, set the model
     *                          to zero columns
     * @see #setNumRows
     */
    public void setColumnIdentifiers(final Vector columnIdentifiers) {
    	setDataVector(this.dataVector, columnIdentifiers);
    }


//  Implementing the TableModel interface


    /**
     * Returns the number of rows in this data table.
     * @return the number of rows in the model
     */
    public int getRowCount() {
    	return this.dataVector.size();
    }

    /**
     * Returns the number of columns in this data table.
     * @return the number of columns in the model
     */
    public int getColumnCount() {
    	return this.columnIdentifiers.size();
    }

    @Override
	public String getColumnName(final int column) {
    	Object id = null;
    	// This test is to cover the case when
    	// getColumnCount has been subclassed by mistake ...
    	if (column < this.columnIdentifiers.size()) {
			id = this.columnIdentifiers.elementAt(column);
		}
    	return (id == null) ? super.getColumnName(column) : id.toString();
    }

    public Object getValueAt(final int row, final int column) {
    	if (this.dataVector.elementAt(row)==null) {
			return null;
		}

    	switch (column) {
	    	case 0: return this.dataVector.elementAt(row);
	    	case 1: return "  " + JIUtility.length2KB(this.dataVector.elementAt(row).getLength());
	    	case 2: return "  " + this.dataVector.elementAt(row).getSuffix();
	    	case 3: return "  " + this.dataVector.elementAt(row).getShortDate();
	    	case 4: return "  " + this.dataVector.elementAt(row).getDim();
	    	case 5: return "";
    	}

    	return null;
    }


    @Override
	public void setValueAt(final Object aValue, final int row, final int column) {}

}

