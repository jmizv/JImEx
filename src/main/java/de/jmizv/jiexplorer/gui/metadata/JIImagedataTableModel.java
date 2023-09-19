package de.jmizv.jiexplorer.gui.metadata;

import java.io.Serial;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.util.DiskObject;


public class JIImagedataTableModel extends AbstractTableModel {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String[] headerNames = {"Name", "Value"};
    private final String[] columnNames = {"Name", "Path", "Size", "Dimension", "Type", "Last Modified", "Key Words", "Categories"};
    private final String[] colData;

    public JIImagedataTableModel(final DiskObject dObj) {
        this.colData = new String[this.columnNames.length];
        this.colData[0] = dObj.getName();
        this.colData[1] = dObj.getPath();
        this.colData[2] = dObj.getSize();
        this.colData[3] = dObj.getWidth() + "x" + dObj.getHeight();
        this.colData[4] = dObj.getMimeType();
        this.colData[5] = new Date(dObj.getFile().lastModified()).toString();
        this.colData[6] = JIThumbnailService.getInstance().getKeyWordsForImage(dObj);
        this.colData[7] = JIThumbnailService.getInstance().getCategoriesForImage(dObj);
    }

    public int getColumnCount() {
        return this.headerNames.length;
    }

    public int getRowCount() {
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(final int col) {
        return this.headerNames[col];
    }

    public Object getValueAt(final int row, final int col) {
        if (col == 0) {
            return this.columnNames[row];
        }
        if (col == 1) {
            return this.colData[row];
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(final int c) {
        return getValueAt(0, c).getClass();
    }

}
