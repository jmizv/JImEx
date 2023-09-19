package de.jmizv.jiexplorer.gui.metadata;

import java.io.Serial;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

public class JIMetadataTableModel extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String[] columnNames = {"Name", "Value"};
    private final Object[][] data;

    public JIMetadataTableModel(final Directory metadataDirectory) {
        //final Charset charset = Charset.forName("ISO-8859-1");
        this.data = new Object[metadataDirectory.getTagCount()][2];

        Iterator<Tag> tagIter = metadataDirectory.getTags().iterator();

        for (int dataCnt = 0; tagIter.hasNext(); dataCnt++) {
            final Tag tag = (Tag) tagIter.next();
            try {
                //data[dataCnt][1] = String.valueOf(charset.encode(tag.getDescription()).asCharBuffer().array());
                this.data[dataCnt][1] = tag.getDescription();
            } catch (final RuntimeException e) {
                this.data[dataCnt][1] = "";
            }
            //data[dataCnt][0] = String.valueOf(charset.encode(tag.getTagName()).asCharBuffer().array());
            this.data[dataCnt][0] = tag.getTagName();
        }
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public int getRowCount() {
        return this.data.length;
    }

    @Override
    public String getColumnName(final int col) {
        return this.columnNames[col];
    }

    public Object getValueAt(final int row, final int col) {
        return this.data[row][col];
    }

    @Override
    public Class<?> getColumnClass(final int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return col >= 2;
    }

    @Override
    public void setValueAt(final Object value, final int row, final int col) {
        this.data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}
