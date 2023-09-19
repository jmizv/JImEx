package de.jmizv.jiexplorer.gui.metadata;

import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFTag;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;

public class JIIFDMetadataTableModel extends AbstractTableModel {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String[] columnNames = {"Name", "Value"};
    private final Object[] col1;
    private final String[] col2;

    public JIIFDMetadataTableModel(final IFD metadataDirectory) {
        final Object[] col = metadataDirectory.getTags().toArray();
        this.col1 = new Object[col.length];
        this.col2 = new String[col.length];

        for (int i = 0; i < col.length; i++) {
            final String str = ((TIFFTag) col[i]).toString();
            this.col1[i] = str.substring(0, str.indexOf("type:"));
            this.col2[i] = str.substring(str.indexOf("value:") + 6);
        }
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public int getRowCount() {
        return this.col1.length;
    }

    @Override
    public String getColumnName(final int col) {
        return this.columnNames[col];
    }

    public Object getValueAt(final int row, final int col) {
        if (col == 0) {
            return this.col1[row];
        }
        return this.col2[row];
    }

    @Override
    public Class<?> getColumnClass(final int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        return false;
    }
}
